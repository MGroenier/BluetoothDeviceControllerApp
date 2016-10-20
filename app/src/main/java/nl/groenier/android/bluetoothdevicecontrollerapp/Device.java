package nl.groenier.android.bluetoothdevicecontrollerapp;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

/**
 * Created by Martijn on 08/10/2016.
 */

public class Device implements Serializable {

    private long id;
    private String displayName;
    private DeviceType deviceType;
    private String macAddress;
    private BluetoothDevice mBluetoothDevice;

    public Device(long id, String displayName, DeviceType deviceType, String macAddress, BluetoothDevice bluetoothDevice) {
        this.id = id;
        this.displayName = displayName;
        this.deviceType = deviceType;
        this.macAddress = macAddress;
        mBluetoothDevice = bluetoothDevice;
    }

    public Device(String displayName, DeviceType deviceType, BluetoothDevice bluetoothDevice) {
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

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        mBluetoothDevice = bluetoothDevice;
    }
}
