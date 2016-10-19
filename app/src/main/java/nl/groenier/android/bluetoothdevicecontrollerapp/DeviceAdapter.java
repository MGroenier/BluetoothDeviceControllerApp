package nl.groenier.android.bluetoothdevicecontrollerapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Martijn on 18/10/2016.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    final Context context;
    private final List<Device> deviceArrayList;

    public DeviceAdapter(List<Device> list, Context context) {
        deviceArrayList = list;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return deviceArrayList.size();
    }

    private Device getItem(int position) {
        return deviceArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return deviceArrayList.get(position).getId();
    }

    public void updateList(List<Device> newlist) {
        // Set new updated list
        deviceArrayList.clear();
        deviceArrayList.addAll(newlist);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.devices_list_item_cardview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Populate the row
        holder.populateRow(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView deviceDisplayName;
        private final TextView deviceMacAddress;
        private final ImageView deviceIcon;

        //initialize the variables
        public ViewHolder(View view) {
            super(view);
            deviceDisplayName = (TextView) view.findViewById(R.id.text_view_devices_list_item_name);
            deviceMacAddress = (TextView) view.findViewById(R.id.text_view_devices_list_item_mac);
            deviceIcon = (ImageView) view.findViewById(R.id.image_view_devices_list_item);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            Device selectedDevice = getItem(getAdapterPosition());

            if(selectedDevice.getDeviceType() != "unknownDeviceType") {
                Intent intentStartDeviceControl;
                switch (selectedDevice.getDeviceType()) {
                    case "rotating light":
                        intentStartDeviceControl = new Intent(context, DeviceControlFlashingLightActivity.class);
                        break;
                    case "wallplug":
                        intentStartDeviceControl = new Intent(context, DeviceControlWallplugActivity.class);
                        break;
                    default:
                        intentStartDeviceControl = new Intent(context, DeviceControlFlashingLightActivity.class);
                        break;
                }
                intentStartDeviceControl.putExtra("selectedDeviceMac", selectedDevice.getBluetoothDevice().getAddress());
                intentStartDeviceControl.putExtra("selectedDeviceDisplayName", selectedDevice.getDisplayName());
                intentStartDeviceControl.putExtra("connectedBluetoothDevice", selectedDevice.getBluetoothDevice());
                context.startActivity(intentStartDeviceControl);

            } else {
                Intent intentStartDeviceRegister = new Intent(context, DeviceRegisterActivity.class);
                intentStartDeviceRegister.putExtra("selectedDeviceMac", selectedDevice.getBluetoothDevice().getAddress());
                intentStartDeviceRegister.putExtra("selectedDeviceName", selectedDevice.getBluetoothDevice().getName());
                context.startActivity(intentStartDeviceRegister);
            }

        }

        public void populateRow(Device device) {
            deviceDisplayName.setText(device.getDisplayName());
            deviceMacAddress.setText(device.getBluetoothDevice().getAddress());
            deviceIcon.setImageResource(device.getIcon());
        }
    }
}