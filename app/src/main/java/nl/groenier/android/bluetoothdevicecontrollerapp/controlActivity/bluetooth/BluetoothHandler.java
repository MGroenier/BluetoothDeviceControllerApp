package nl.groenier.android.bluetoothdevicecontrollerapp.controlActivity.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static nl.groenier.android.bluetoothdevicecontrollerapp.MainActivity.REQUEST_ENABLE_BT;

/**
 * Created by Martijn on 03/11/2016.
 */

public class BluetoothHandler {

    //public static final int REQUEST_ENABLE_BT = 0001;

    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private Activity mActivity;

    public BluetoothHandler(Context context) {
        this.mContext = context;
        if (mContext instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    public void bluetoothSetup() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext, "This device does not support Bluetooth.", Toast.LENGTH_SHORT).show();
        }
        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
        }
    }

}
