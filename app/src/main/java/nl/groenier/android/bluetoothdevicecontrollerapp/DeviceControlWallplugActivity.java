package nl.groenier.android.bluetoothdevicecontrollerapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import nl.groenier.android.bluetoothdevicecontrollerapp.R;
import nl.groenier.android.bluetoothdevicecontrollerapp.SQLite.DataSource;

/**
 * Created by Martijn on 09/10/2016.
 */

public class DeviceControlWallplugActivity extends AppCompatActivity {

    private Button buttonTurnOn;
    private Button buttonTurnOff;
    private Button buttonUnregisterDevice;

    private String deviceToControlMac;
    private String deviceToControlName;

    private DataSource datasource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control_wallplug);

        datasource = new DataSource(this);

        buttonTurnOn = (Button) findViewById(R.id.button_device_control_wallplug_turn_on);
        buttonTurnOff = (Button) findViewById(R.id.button_device_control_wallplug_turn_off);
        buttonUnregisterDevice = (Button) findViewById(R.id.button_device_control_wallplug_unregister);

        deviceToControlMac = getIntent().getStringExtra("selectedDeviceMac");
        deviceToControlName = getIntent().getStringExtra("selectedDeviceName");

        getSupportActionBar().setTitle("Wallplug: " + deviceToControlName);

        buttonTurnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DeviceControlWallplugActivity.this, "STUB, Turn on!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DeviceControlWallplugActivity.this, "STUB, Turn off!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonUnregisterDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datasource.deleteDevice(deviceToControlMac);
                Toast.makeText(DeviceControlWallplugActivity.this, "Device deleted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}
