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

/**
 * Created by Martijn on 20/10/2016.
 */

public class DeviceSettingsActivity extends AppCompatActivity {

    private ImageButton buttonSaveDevice;
    private EditText editTextDisplayName;

    private DataSource datasource;

    private Long deviceToEditId;
    private String deviceToRegisterMac;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_edit);
        getSupportActionBar().setTitle("Edit this device");
        datasource = new DataSource(this);

        buttonSaveDevice = (ImageButton) findViewById(R.id.button_save_edit_device);
        editTextDisplayName = (EditText) findViewById(R.id.edit_text_display_name_edit_device);

        deviceToEditId = getIntent().getLongExtra("selectedDeviceId", 0);

        editTextDisplayName.setText(datasource.getDevice(deviceToEditId).getDisplayName());

        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Wall plug");
        spinnerArray.add("Siren");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sItems = (Spinner) findViewById(R.id.spinner_edit_device);
        sItems.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(datasource.getDevice(deviceToEditId).getDeviceType().getName());
        sItems.setSelection(spinnerPosition);

        buttonSaveDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String spinnersSelectedDeviceType = sItems.getSelectedItem().toString();
                DeviceType selectedDeviceType = datasource.getDeviceType(spinnersSelectedDeviceType);
                datasource.updateDevice(deviceToEditId, editTextDisplayName.getText().toString(), selectedDeviceType.getId());
                finish();
            }
        });

    }

}