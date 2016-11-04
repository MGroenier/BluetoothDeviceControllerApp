package nl.groenier.android.bluetoothdevicecontrollerapp.controlActivity;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import nl.groenier.android.bluetoothdevicecontrollerapp.DeviceAdapter;
import nl.groenier.android.bluetoothdevicecontrollerapp.DeviceSettingsActivity;
import nl.groenier.android.bluetoothdevicecontrollerapp.MainActivity;
import nl.groenier.android.bluetoothdevicecontrollerapp.R;
import nl.groenier.android.bluetoothdevicecontrollerapp.SQLite.DataSource;
import nl.groenier.android.bluetoothdevicecontrollerapp.bluetooth.BluetoothHandler;
import nl.groenier.android.bluetoothdevicecontrollerapp.model.Device;
import nl.groenier.android.bluetoothdevicecontrollerapp.model.DeviceType;

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

    private BluetoothHandler mBluetoothHandler;

    private DataSource datasource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control_wallplug);

        mParentLayout = findViewById(android.R.id.content);

        datasource = new DataSource(this);
        mBluetoothHandler = new BluetoothHandler(mParentLayout);

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

        mBluetoothHandler.bluetoothSetupSocket(connectedBluetoothDevice);

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
                mBluetoothHandler.write("0".getBytes());
                mImageViewTopImage.setImageResource(R.drawable.wallplug_on);
            }
        });

        buttonTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothHandler.write("1".getBytes());
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

        IntentFilter filterDeviceFound = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mReceiverDeviceDisconnected, filterDeviceFound);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mBluetoothHandler.closeSocket();
    }

    // Create a BroadcastReceiver for ACTION_ACL_DISCONNECTED
    private final BroadcastReceiver mReceiverDeviceDisconnected = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When device disconnects
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                mBluetoothHandler.closeSocket();
                finish();
            }
        }
    };

}
