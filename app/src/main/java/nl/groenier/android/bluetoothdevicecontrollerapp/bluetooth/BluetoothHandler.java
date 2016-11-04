package nl.groenier.android.bluetoothdevicecontrollerapp.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static nl.groenier.android.bluetoothdevicecontrollerapp.MainActivity.REQUEST_ENABLE_BT;

/**
 * Created by Martijn on 03/11/2016.
 */

public class BluetoothHandler {

    private static final String TAG = "BluetoothHandler";
    private ConnectThread connectThread;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice connectedBluetoothDevice;
    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket socket;
    private OutputStream outputStream;

    private Context mContext;
    private Activity mActivity;
    private View view;

    public BluetoothHandler(View view) {
        this.view = view;
    }

    public BluetoothHandler(Context context) {
        this.mContext = context;
        if (mContext instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void startBluetoothDiscovery() {
        mBluetoothAdapter.startDiscovery();
    }

    public void cancelBluetoothDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
    }

    public void bluetoothSetup() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext, "This device does not support Bluetooth.", Toast.LENGTH_SHORT).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
        }
    }

    public void bluetoothSetupSocket(BluetoothDevice device) {
        connectThread = new ConnectThread(device);
        connectThread.run();
    }

    public void closeSocket() {
        connectThread.cancel();
    }

    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            //Toast.makeText(this, "Failed to write!", Toast.LENGTH_SHORT).show();
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            //mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                Snackbar.make(view,"Failed to connect!", Snackbar.LENGTH_SHORT).show();
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            try {
            outputStream = mmSocket.getOutputStream();
            } catch (IOException e) {
                Snackbar.make(view,"Failed to connect!", Snackbar.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            Snackbar.make(view,"Connected Successfully!", Snackbar.LENGTH_SHORT).show();

            // Do work to manage the connection (in a separate thread)
            //manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}
