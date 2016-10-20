package nl.groenier.android.bluetoothdevicecontrollerapp;

/**
 * Created by Martijn on 19/10/2016.
 */

public class DeviceType {

    private long id;
    private String name;
    private int icon;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
