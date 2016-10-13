package nl.groenier.android.bluetoothdevicecontrollerapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Martijn on 08/10/2016.
 */

public class ViewHolder {

    private ImageView image;
    private TextView title;
    private TextView macAddress;

    public ViewHolder (View view){
        image = (ImageView) view.findViewById(R.id.image_view_devices_list_item);
        title = (TextView) view.findViewById(R.id.text_view_devices_list_item_name);
        macAddress = (TextView) view.findViewById(R.id.text_view_devices_list_item_mac);
    }

    public void populateRow(Device item) {
        //Set the icon for this row
        image.setImageResource(item.getIcon());
        //Set the title for this row
        if(item.getBluetoothDevice().getName() != "") {
            title.setText(item.getBluetoothDevice().getName());
        } else {
            title.setText("Unknown");
        }
        macAddress.setText(item.getBluetoothDevice().getAddress());
    }


}