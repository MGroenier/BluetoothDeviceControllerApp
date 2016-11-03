package nl.groenier.android.bluetoothdevicecontrollerapp.controlActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import nl.groenier.android.bluetoothdevicecontrollerapp.DeviceSettingsActivity;
import nl.groenier.android.bluetoothdevicecontrollerapp.R;
import nl.groenier.android.bluetoothdevicecontrollerapp.SQLite.DataSource;
import nl.groenier.android.bluetoothdevicecontrollerapp.model.Device;

/**
 * Created by Martijn on 09/10/2016.
 */

public class DeviceControlWallplugActivity extends AppCompatActivity {

    private ImageView mImageViewTopImage;
    private ImageButton imageButtonSettings;
    private Button buttonTurnOn;
    private Button buttonTurnOff;
    private ImageButton buttonUnregisterDevice;
    private View mParentLayout;

    private BluetoothDevice connectedBluetoothDevice;
    private Long deviceToControlId;
    private Device deviceToControl;
    private String deviceToControlMac;
    private String deviceToControlDisplayName;
    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket socket;
    private OutputStream outputStream;

    private DataSource datasource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control_wallplug);

        datasource = new DataSource(this);

        mParentLayout = findViewById(android.R.id.content);

        mImageViewTopImage = (ImageView) findViewById(R.id.image_view_wall_plug_icon);
        imageButtonSettings = (ImageButton) findViewById(R.id.image_button_wall_plug_settings);
        buttonTurnOn = (Button) findViewById(R.id.button_device_control_wallplug_turn_on);
        buttonTurnOff = (Button) findViewById(R.id.button_device_control_wallplug_turn_off);
        buttonUnregisterDevice = (ImageButton) findViewById(R.id.button_device_control_wallplug_unregister);

        deviceToControlId = getIntent().getLongExtra("selectedDeviceId",0);
        deviceToControl = datasource.getDevice(deviceToControlId);

        connectedBluetoothDevice = getIntent().getExtras().getParcelable("connectedBluetoothDevice");
        deviceToControlMac = deviceToControl.getMacAddress();
        deviceToControlDisplayName = deviceToControl.getDisplayName();

        bluetoothSetupSocket(connectedBluetoothDevice);
        bluetoothConnect();

        getSupportActionBar().setTitle("Wall plug: " + deviceToControlDisplayName);


        imageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentStartDeviceSettings = new Intent(DeviceControlWallplugActivity.this, DeviceSettingsActivity.class);
                intentStartDeviceSettings.putExtra("selectedDeviceId", deviceToControlId);
                DeviceControlWallplugActivity.this.startActivity(intentStartDeviceSettings);
            }
        });

        buttonTurnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                write("0".getBytes());
                mImageViewTopImage.setImageResource(R.drawable.wallplug_on);
            }
        });

        buttonTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                write("1".getBytes());
                mImageViewTopImage.setImageResource(R.drawable.wallplug);
            }
        });

        buttonUnregisterDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datasource.deleteDevice(deviceToControlId);
                Toast.makeText(DeviceControlWallplugActivity.this, "Device deleted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bluetoothSetupSocket(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;

        connectedBluetoothDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = tmp;
    }

    public void bluetoothConnect() {
        // The discovery can be cancelled now, we found our device
        //mBluetoothAdapter.cancelDiscovery();

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

        //Toast.makeText(this, "Successfully connected to the socket!", Toast.LENGTH_SHORT).show();
        Snackbar.make(mParentLayout,"Connected successfully!", Snackbar.LENGTH_SHORT).show();
        bluetoothSendData();
        //manageConnectedSocket(socket);

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
        } catch (IOException e) {
            Toast.makeText(this, "Failed to write!", Toast.LENGTH_SHORT).show();
        }
    }

}
