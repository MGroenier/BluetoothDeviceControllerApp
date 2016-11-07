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

import java.util.List;

import nl.groenier.android.bluetoothdevicecontrollerapp.controlActivity.DeviceControlFlashingLightActivity;
import nl.groenier.android.bluetoothdevicecontrollerapp.controlActivity.DeviceControlWallplugActivity;
import nl.groenier.android.bluetoothdevicecontrollerapp.model.Device;

/**
 * Created by Martijn on 18/10/2016.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    final Context context;
    private final List<Device> deviceArrayList;
//    private List<Device> deviceArrayList;

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

//    public void updateList(List<Device> newlist) {
//        // Set new updated list
//        deviceArrayList.clear();
//        deviceArrayList.addAll(newlist);
//    }

    //ViewHolder contains one item of the list
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.devices_list_item_cardview, parent, false);
        return new ViewHolder(itemView);
    }

    //fill in the fields of the item
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
            String deviceType = selectedDevice.getDeviceType().getName();

            //Check whether deviceType is known, if yes then go to the correct Control Activity
            if(!deviceType.equals("Unknown")) {
                Intent intentStartDeviceControl;
                switch (deviceType) {
                    case "Siren":
                        intentStartDeviceControl = new Intent(context, DeviceControlFlashingLightActivity.class);
                        break;
                    case "Wall plug":
                        intentStartDeviceControl = new Intent(context, DeviceControlWallplugActivity.class);
                        break;
                    default:
                        intentStartDeviceControl = new Intent(context, DeviceControlFlashingLightActivity.class);
                        break;
                }
                intentStartDeviceControl.putExtra("selectedDeviceId", selectedDevice.getId());
                intentStartDeviceControl.putExtra("connectedBluetoothDevice", selectedDevice.getBluetoothDevice());
                context.startActivity(intentStartDeviceControl);

            } else { // if not, then go to the device registration Activity
                Log.d("fatal", "else: " + deviceType);
                Intent intentStartDeviceRegister = new Intent(context, DeviceRegisterActivity.class);
                intentStartDeviceRegister.putExtra("selectedDeviceMac", selectedDevice.getBluetoothDevice().getAddress());
                intentStartDeviceRegister.putExtra("selectedDeviceName", selectedDevice.getBluetoothDevice().getName());
                context.startActivity(intentStartDeviceRegister);
            }

        }

        public void populateRow(Device device) {
            deviceDisplayName.setText(device.getDisplayName());
            deviceMacAddress.setText(device.getBluetoothDevice().getAddress());
            deviceIcon.setImageResource(device.getDeviceType().getIcon());
        }
    }
}