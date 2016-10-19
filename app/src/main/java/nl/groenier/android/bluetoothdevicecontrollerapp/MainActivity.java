package nl.groenier.android.bluetoothdevicecontrollerapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import nl.groenier.android.bluetoothdevicecontrollerapp.SQLite.DataSource;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 0001;

    private ImageButton discoverDevices;
    private ImageView scanPulseShape;
    private Animation scanPulseAnimation;

    private List listOfDevices;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothDevice connectedBluetoothDevice;
    private BluetoothSocket socket;
    private OutputStream outputStream;

    private DataSource datasource;

    private RecyclerView deviceRecyclerView;
    private RecyclerView.Adapter deviceAdapter;
    private RecyclerView.LayoutManager deviceLayoutManager;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        datasource = new DataSource(this);

        discoverDevices = (ImageButton) findViewById(R.id.button_discover_devices);
        scanPulseShape = (ImageView) findViewById(R.id.image_view_scan_shape);
        scanPulseAnimation = AnimationUtils.loadAnimation(this, R.anim.scan);

        listOfDevices = new ArrayList<Device>();

        // Retrieve a reference to the RecyclerView
        deviceRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_devices);

        // Use LinearLayoutManager
        deviceLayoutManager = new LinearLayoutManager(this);
        deviceRecyclerView.setLayoutManager(deviceLayoutManager);

        // Specyfing an adapter
        deviceAdapter = new DeviceAdapter(listOfDevices, this);
        deviceRecyclerView.setAdapter(deviceAdapter);

        bluetoothSetup();

        discoverDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listOfDevices.clear();
                bluetoothSetup();
                bluetoothDiscoverDevices();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        listOfDevices.clear();
        bluetoothSetup();
        bluetoothDiscoverDevices();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth enabled successfully!", Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Failed to enable Bluetooth.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void bluetoothSetup() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "This device does not support Bluetooth.", Toast.LENGTH_SHORT).show();
        }
        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
        }
    }

    public void bluetoothDiscoverDevices() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.startDiscovery();
            // Register the BroadcastReceiver for discovering a new device
            IntentFilter filterDeviceFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiverDeviceFound, filterDeviceFound);
            // Register the BroadcastReceiver for when the discovering has finished
            IntentFilter filterDiscoveryStarted = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            registerReceiver(mReceiverDiscoveryStarted, filterDiscoveryStarted);
            // Register the BroadcastReceiver for when the discovering has finished
            IntentFilter filterDiscoveryFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mReceiverDiscoveryFinished, filterDiscoveryFinished);
        }
    }

    // Create a BroadcastReceiver for ACTION_DISCOVERY_STARTED
    private final BroadcastReceiver mReceiverDiscoveryStarted = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery starts
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                discoverDevices.setEnabled(false);
                // Stop the animation of the discoverDevices Button
                scanPulseShape.startAnimation(scanPulseAnimation);
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_DISCOVERY_FINISHED
    private final BroadcastReceiver mReceiverDiscoveryFinished = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery ends
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                discoverDevices.setEnabled(true);
                //Stop the animation of the discoverDevices Button
                scanPulseShape.clearAnimation();
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiverDeviceFound = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                if(uuidExtra == null) {
                    Log.d("uuid", "uuidExtra is null");
                } else {
                    Log.d("uuid", "uuidExtra is not null");
                }

                Device deviceRetrievedFromDatabase = datasource.getDevice(device.getAddress());
                int iconForDevice;
                String deviceType;

                // If not null, then the device is present in the database
                if(deviceRetrievedFromDatabase != null) {
                    iconForDevice = deviceRetrievedFromDatabase.getIcon();
                    deviceType = deviceRetrievedFromDatabase.getDeviceType();
                    listOfDevices.add(new Device(iconForDevice,deviceRetrievedFromDatabase.getDisplayName(), deviceType, device));
                } else {
                    iconForDevice = R.drawable.ic_unknown_device_white_45dp;
                    deviceType = "unknownDeviceType";
                    listOfDevices.add(new Device(iconForDevice,device.getName(), deviceType, device));
                }

                deviceAdapter = new DeviceAdapter(listOfDevices, context);
                deviceRecyclerView.setAdapter(deviceAdapter);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiverDeviceFound);
        unregisterReceiver(mReceiverDiscoveryFinished);
    }
}
