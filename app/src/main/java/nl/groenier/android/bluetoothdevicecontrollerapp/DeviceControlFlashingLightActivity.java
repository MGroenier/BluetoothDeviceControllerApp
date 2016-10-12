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

public class DeviceControlFlashingLightActivity extends AppCompatActivity {

    private TextView textViewMac;
    private Button buttonTurnOn;
    private Button buttonTurnOff;
    private Button buttonUnregisterDevice;

    private String deviceToControlMac;
    private String deviceToControlDisplayName;

    private DataSource datasource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control_flashing_light);

        datasource = new DataSource(this);

        textViewMac = (TextView) findViewById(R.id.text_view_device_control_mac);
        buttonTurnOn = (Button) findViewById(R.id.button_device_control_flashing_light_turn_on);
        buttonTurnOff = (Button) findViewById(R.id.button_device_control_flashing_light_turn_off);
        buttonUnregisterDevice = (Button) findViewById(R.id.button_device_control_flashing_light_unregister);

        deviceToControlMac = getIntent().getStringExtra("selectedDeviceMac");
        deviceToControlDisplayName = getIntent().getStringExtra("selectedDeviceDisplayName");

        getSupportActionBar().setTitle("Siren: " + deviceToControlDisplayName);

        textViewMac.setText(deviceToControlMac);

        buttonTurnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DeviceControlFlashingLightActivity.this, "STUB, Turn on!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DeviceControlFlashingLightActivity.this, "STUB, Turn off!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonUnregisterDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datasource.deleteDevice(deviceToControlMac);
                Toast.makeText(DeviceControlFlashingLightActivity.this, "Device deleted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

}
