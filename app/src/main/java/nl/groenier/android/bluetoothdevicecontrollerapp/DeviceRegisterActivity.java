package nl.groenier.android.bluetoothdevicecontrollerapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.groenier.android.bluetoothdevicecontrollerapp.SQLite.DataSource;
import nl.groenier.android.bluetoothdevicecontrollerapp.model.DeviceType;

/**
 * Created by Martijn on 09/10/2016.
 */

public class DeviceRegisterActivity extends AppCompatActivity {

    private ImageButton buttonRegisterDevice;
    private TextView textViewName;
    private TextView textViewMac;
    private EditText editTextDisplayName;

    private DataSource datasource;

    private String deviceToRegisterName;
    private String deviceToRegisterMac;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_register);
        getSupportActionBar().setTitle("Register this device");
        datasource = new DataSource(this);

        buttonRegisterDevice = (ImageButton) findViewById(R.id.button_register_device);
        editTextDisplayName = (EditText) findViewById(R.id.edit_text_register_display_name);

        deviceToRegisterName = getIntent().getStringExtra("selectedDeviceName");
        deviceToRegisterMac = getIntent().getStringExtra("selectedDeviceMac");

        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Wall plug");
        spinnerArray.add("Siren");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sItems = (Spinner) findViewById(R.id.spinner);
        sItems.setAdapter(adapter);

        buttonRegisterDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String spinnersSelectedDeviceType = sItems.getSelectedItem().toString();
                DeviceType selectedDeviceType = datasource.getDeviceType(spinnersSelectedDeviceType);
                datasource.createDevice(editTextDisplayName.getText().toString(), deviceToRegisterMac, selectedDeviceType.getId());
                Toast.makeText(DeviceRegisterActivity.this, deviceToRegisterMac + " added to database", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private int getIconResource(String selectedDeviceType) {
        int iconResource = 0;

        switch (selectedDeviceType) {
            case "rotating light":
                iconResource = R.drawable.siren_icon_white;
                break;
            case "wallplug":
                iconResource = R.drawable.wallplug_white;
                break;
        }

        return iconResource;
    }

}