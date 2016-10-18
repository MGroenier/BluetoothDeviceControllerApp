package nl.groenier.android.bluetoothdevicecontrollerapp;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

/**
 * Created by Martijn on 08/10/2016.
 */

public class Device implements Serializable {

    private long id;
    private String displayName;
    private int icon;
    private String deviceType;
    private BluetoothDevice mBluetoothDevice;

    public Device(long id, String displayName, int icon, String deviceType, BluetoothDevice bluetoothDevice) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.deviceType = deviceType;
        mBluetoothDevice = bluetoothDevice;
    }

    public Device(int icon, String displayName, String deviceType, BluetoothDevice bluetoothDevice) {
        this.icon = icon;
        this.displayName = displayName;
        this.deviceType = deviceType;
        mBluetoothDevice = bluetoothDevice;
    }

    public Device() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        mBluetoothDevice = bluetoothDevice;
    }
}
