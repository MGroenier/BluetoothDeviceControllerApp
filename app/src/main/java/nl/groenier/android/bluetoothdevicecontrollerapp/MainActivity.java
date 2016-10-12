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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
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

    private ListView deviceListView;
    private DeviceListAdapter mDeviceListAdapter;
    private List listOfDevices;

    //private static final String DEVICE_ADDRESS = "20:16:07:22:68:86";
//    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter mBluetoothAdapter;
    //private BluetoothDevice selectedBluetoothDevice;
    private BluetoothDevice connectedBluetoothDevice;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    //private InputStream inputStream;

    private DataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        datasource = new DataSource(this);

        discoverDevices = (ImageButton) findViewById(R.id.button_discover_devices);
        scanPulseShape = (ImageView) findViewById(R.id.image_view_scan_shape);
        scanPulseAnimation = AnimationUtils.loadAnimation(this, R.anim.scan);

        deviceListView = (ListView) findViewById(R.id.list_view_devices);
        listOfDevices = new ArrayList<Device>();

        mDeviceListAdapter = new DeviceListAdapter(this,R.layout.devices_list_item,listOfDevices);
        deviceListView.setAdapter(mDeviceListAdapter);

        mDeviceListAdapter.notifyDataSetChanged();

        bluetoothSetup();

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Device selectedDevice = (Device) listOfDevices.get(position);

                Device deviceFromDatabase = datasource.getDevice(selectedDevice.getBluetoothDevice().getAddress());

                // Check whether device isn't already in the database, look for the MAC-address.
                if(deviceFromDatabase != null) {
                    Intent intentStartDeviceControl;
                    switch (deviceFromDatabase.getDeviceType()) {
                        case "rotating light":
                            intentStartDeviceControl = new Intent(MainActivity.this, DeviceControlFlashingLightActivity.class);
                            break;
                        case "wallplug":
                            intentStartDeviceControl = new Intent(MainActivity.this, DeviceControlWallplugActivity.class);
                            break;
                        default:
                            intentStartDeviceControl = new Intent(MainActivity.this, DeviceControlFlashingLightActivity.class);
                            break;
                    }

                    intentStartDeviceControl.putExtra("selectedDeviceMac", selectedDevice.getBluetoothDevice().getAddress());
                    intentStartDeviceControl.putExtra("selectedDeviceDisplayName", selectedDevice.getDisplayName());
                    intentStartDeviceControl.putExtra("connectedBluetoothDevice", selectedDevice.getBluetoothDevice());
                    startActivity(intentStartDeviceControl);
                } else {
                    Intent intentStartDeviceRegister = new Intent(MainActivity.this, DeviceRegisterActivity.class);
                    intentStartDeviceRegister.putExtra("selectedDeviceMac", selectedDevice.getBluetoothDevice().getAddress());
                    intentStartDeviceRegister.putExtra("selectedDeviceName", selectedDevice.getBluetoothDevice().getName());
                    startActivity(intentStartDeviceRegister);
                }

                connectedBluetoothDevice = selectedDevice.getBluetoothDevice();
                //bluetoothSetupSocket(connectedBluetoothDevice);
                //bluetoothConnect();
            }
        });

        discoverDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listOfDevices.clear();
                mDeviceListAdapter.notifyDataSetChanged();
                scanPulseShape.startAnimation(scanPulseAnimation);
                bluetoothSetup();
            }
        });

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

        if (mBluetoothAdapter != null) {
            bluetoothDiscoverDevices();
        }

    }

    public void bluetoothDiscoverDevices() {
        mBluetoothAdapter.startDiscovery();
        // Register the BroadcastReceiver for discovering a new device
        IntentFilter filterDeviceFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiverDeviceFound, filterDeviceFound);
        // Register the BroadcastReceiver for when the discovering has finished
        IntentFilter filterDiscoveryFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiverDiscoveryFinished, filterDiscoveryFinished);
    }

    // Create a BroadcastReceiver for ACTION_DISCOVERY_FINISHED
    private final BroadcastReceiver mReceiverDiscoveryFinished = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery ends
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
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
                // Add the name and address to an array adapter to show in a ListView
                listOfDevices.add(new Device(R.drawable.ic_bluetooth_black_45dp,device));
                mDeviceListAdapter.notifyDataSetChanged();
            }
        }
    };

    public void bluetoothSetupSocket(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        connectedBluetoothDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        socket = tmp;
    }

    public void bluetoothConnect() {
        // The discovery can be cancelled now, we found our device
        mBluetoothAdapter.cancelDiscovery();

        try {
            socket.connect();
        }
        catch (IOException connectException) {
            // Not able to connect, close the socket and get out
            Toast.makeText(this, "Failed to connect to the socket!", Toast.LENGTH_SHORT).show();
            try {
                socket.close();
            } catch (IOException closeException) { }
            return;
        }

        Toast.makeText(this, "Successfully connected to the socket!", Toast.LENGTH_SHORT).show();
        bluetoothSendData();
        //manageConnectedSocket(socket);

    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) { }
    }

    public void bluetoothSendData() {
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to get OutputStream.", Toast.LENGTH_SHORT).show();
        }
    }

    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
            Log.d("Write Bytes", "write: " + bytes.toString());
        } catch (IOException e) {
            Toast.makeText(this, "Failed to write!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiverDeviceFound);
        unregisterReceiver(mReceiverDiscoveryFinished);
    }
}
