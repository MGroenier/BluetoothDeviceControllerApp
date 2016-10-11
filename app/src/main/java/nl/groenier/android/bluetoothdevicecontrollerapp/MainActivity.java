package nl.groenier.android.bluetoothdevicecontrollerapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import nl.groenier.android.beaconcontrollerapp.R;
import nl.groenier.android.bluetoothdevicecontrollerapp.SQLite.DataSource;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 0001;

//    private TextView connectedDeviceTitle;
//    private TextView connectedDeviceMacAddress;
//    private TextView connectedDeviceSocket;
    private Button discoverDevices;

    private ListView deviceListView;
    private DeviceListAdapter mDeviceListAdapter;
    private List listOfDevices;

    private static final String DEVICE_ADDRESS = "20:16:07:22:68:86";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice selectedBluetoothDevice;
    private BluetoothDevice connectedBluetoothDevice;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private DataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        datasource = new DataSource(this);

//        connectedDeviceTitle = (TextView) findViewById(R.id.text_view_selected_device_title);
//        connectedDeviceMacAddress = (TextView) findViewById(R.id.text_view_selected_device_mac_address);
//        connectedDeviceSocket = (TextView) findViewById(R.id.text_view_selected_device_socket);
        discoverDevices = (Button)  findViewById(R.id.button_discover_devices);

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
                    Toast.makeText(MainActivity.this, selectedDevice.getBluetoothDevice().getAddress() + " present in database", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MainActivity.this, selectedDevice.getDisplayName(), Toast.LENGTH_SHORT).show();
                    startActivity(intentStartDeviceControl);
                } else {
                    Toast.makeText(MainActivity.this, selectedDevice.getBluetoothDevice().getAddress() + " NOT present in database", Toast.LENGTH_SHORT).show();
                    Intent intentStartDeviceRegister = new Intent(MainActivity.this, DeviceRegisterActivity.class);
                    intentStartDeviceRegister.putExtra("selectedDeviceMac", selectedDevice.getBluetoothDevice().getAddress());
                    intentStartDeviceRegister.putExtra("selectedDeviceName", selectedDevice.getBluetoothDevice().getName());
                    startActivity(intentStartDeviceRegister);
                }

                connectedBluetoothDevice = selectedDevice.getBluetoothDevice();
//                connectedDeviceTitle.setText(connectedBluetoothDevice.getName());
//                connectedDeviceMacAddress.setText(connectedBluetoothDevice.getAddress());
                bluetoothSetupSocket(connectedBluetoothDevice);
                bluetoothConnect();
            }
        });

        discoverDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listOfDevices.clear();
                mDeviceListAdapter.notifyDataSetChanged();
                bluetoothSetup();
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        Toast.makeText(this, "Scanning for available bluetooth devices.", Toast.LENGTH_SHORT).show();
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
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

//        connectedDeviceSocket.setText(tmp.toString());
        Toast.makeText(this, "Socket: " + tmp.toString(), Toast.LENGTH_SHORT).show();
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
        unregisterReceiver(mReceiver);
    }
}
