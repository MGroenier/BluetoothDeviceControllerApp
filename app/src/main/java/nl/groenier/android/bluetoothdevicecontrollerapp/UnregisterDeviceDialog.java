//package nl.groenier.android.bluetoothdevicecontrollerapp;
//
//import android.app.Dialog;
//import android.app.DialogFragment;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//
///**
// * Created by Martijn on 04/11/2016.
// */
//
//public class UnregisterDeviceDialog extends android.support.v4.app.DialogFragment {
//
//    private Context mContext;
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the Builder class for convenient dialog construction
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(R.string.are_you_sure)
//                .setMessage(R.string.dialog_unregister_device)
//                .setPositiveButton(R.string.unregister, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        getActivity().finish();
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
//        // Create the AlertDialog object and return it
//        return builder.create();
//
//    }
//}
