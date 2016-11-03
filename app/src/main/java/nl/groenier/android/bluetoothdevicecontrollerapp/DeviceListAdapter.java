package nl.groenier.android.bluetoothdevicecontrollerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import nl.groenier.android.bluetoothdevicecontrollerapp.model.Device;

/**
 * Created by Martijn on 08/10/2016.
 */

public class DeviceListAdapter extends ArrayAdapter<Device> {

    private LayoutInflater inflater;

    public DeviceListAdapter(Context context, int resource, List<Device> objects) {
        super(context, resource, objects);

        inflater = LayoutInflater.from(getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder;

        //Check if the row is new
        if (row == null) {

            //Inflate the layout if it didn't exist yet
            row = inflater.inflate(R.layout.devices_list_item_cardview, parent, false);
            //Create a new view holder instance
            holder = new ViewHolder(row);
            //set the holder as a tag so we can get it back later
            row.setTag(holder);

        } else {

            //The row isn't new so we can reuse the view holder
            holder = (ViewHolder) row.getTag();
        }

        //Populate the row
        holder.populateRow(getItem(position));
        return row;

    }

}
