package nl.groenier.android.bluetoothdevicecontrollerapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.groenier.android.beaconcontrollerapp.R;
import nl.groenier.android.bluetoothdevicecontrollerapp.SQLite.DataSource;

/**
 * Created by Martijn on 09/10/2016.
 */

public class DeviceRegisterActivity extends AppCompatActivity {

    private Button buttonRegisterDevice;
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
        datasource = new DataSource(this);

        buttonRegisterDevice = (Button) findViewById(R.id.button_register_device);
        textViewMac = (TextView) findViewById(R.id.text_view_device_register_mac);
        textViewName = (TextView) findViewById(R.id.text_view_device_register_name);
        editTextDisplayName = (EditText) findViewById(R.id.edit_text_register_display_name);

        deviceToRegisterName = getIntent().getStringExtra("selectedDeviceName");
        deviceToRegisterMac = getIntent().getStringExtra("selectedDeviceMac");

        textViewName.setText(deviceToRegisterName);
        textViewMac.setText(deviceToRegisterMac);

        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("rotating light");
        spinnerArray.add("wallplug");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sItems = (Spinner) findViewById(R.id.spinner);
        sItems.setAdapter(adapter);

        buttonRegisterDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(DeviceRegisterActivity.this, "STUB, add device to database now", Toast.LENGTH_SHORT).show();
                datasource.createDevice(editTextDisplayName.getText().toString(), deviceToRegisterMac,sItems.getSelectedItem().toString());
                Toast.makeText(DeviceRegisterActivity.this, deviceToRegisterMac + " added to database", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}
