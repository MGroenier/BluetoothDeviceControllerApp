package nl.groenier.android.bluetoothdevicecontrollerapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import nl.groenier.android.bluetoothdevicecontrollerapp.model.Device;

/**
 * Created by Martijn on 08/10/2016.
 */

public class ViewHolder {

    private ImageView image;
    private TextView displayName;
    private TextView macAddress;

    public ViewHolder (View view){
        image = (ImageView) view.findViewById(R.id.image_view_devices_list_item);
        displayName = (TextView) view.findViewById(R.id.text_view_devices_list_item_name);
        macAddress = (TextView) view.findViewById(R.id.text_view_devices_list_item_mac);
    }

    public void populateRow(Device item) {
        //Set the icon for this row
        image.setImageResource(item.getDeviceType().getIcon());
        //Set the title for this row
        if(item.getDisplayName() != "") {
            displayName.setText(item.getDisplayName());
        } else {
            displayName.setText("Has not been named");
        }
        macAddress.setText(item.getBluetoothDevice().getAddress());
    }


}