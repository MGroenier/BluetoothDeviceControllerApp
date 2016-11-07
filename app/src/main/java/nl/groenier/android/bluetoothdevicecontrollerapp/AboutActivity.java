package nl.groenier.android.bluetoothdevicecontrollerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Martijn on 06/11/2016.
 */

public class AboutActivity extends AppCompatActivity{

    private Button buttonContact;
    private Button buttonLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        buttonContact = (Button) findViewById(R.id.button_about_contact);
        buttonLocation = (Button) findViewById(R.id.button_about_location);

        AboutFragment generalInformationFragment = new AboutFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_about, generalInformationFragment).commit();

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startMapsActivity = new Intent(AboutActivity.this, MapsActivity.class);
                startActivity(startMapsActivity);
            }
        });

    }
}
