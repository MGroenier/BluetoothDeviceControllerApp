package nl.groenier.android.bluetoothdevicecontrollerapp;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

/**
 * Created by Martijn on 08/10/2016.
 */

public class Device implements Serializable {

    private long id;
    private int image;
    private String deviceType;
    private BluetoothDevice mBluetoothDevice;

    public Device(long id, int image, String deviceType, BluetoothDevice bluetoothDevice) {
        this.id = id;
        this.image = image;
        this.deviceType = deviceType;
        mBluetoothDevice = bluetoothDevice;
    }

    public Device(int image, BluetoothDevice bluetoothDevice) {
        this.image = image;
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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
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
