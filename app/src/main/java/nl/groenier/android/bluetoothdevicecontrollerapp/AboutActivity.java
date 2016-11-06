package nl.groenier.android.bluetoothdevicecontrollerapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Martijn on 06/11/2016.
 */

public class AboutActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        AboutFragment generalInformationFragment = new AboutFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_about, generalInformationFragment).commit();

    }
}
