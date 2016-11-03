package nl.groenier.android.bluetoothdevicecontrollerapp.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import nl.groenier.android.bluetoothdevicecontrollerapp.model.Device;
import nl.groenier.android.bluetoothdevicecontrollerapp.model.DeviceType;

/**
 * Created by Martijn on 29/09/2016.
 */

public class DataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] assignmentAllColumns = { MySQLiteHelper.COLUMN_DEVICE_ID, MySQLiteHelper.COLUMN_DEVICE_DISPLAY_NAME, MySQLiteHelper.COLUMN_DEVICE_MAC, MySQLiteHelper.COLUMN_DEVICE_TYPE_ID, MySQLiteHelper.COLUMN_DEVICE_TYPE_NAME, MySQLiteHelper.COLUMN_DEVICE_TYPE_ICON};

    public DataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
        database = dbHelper.getWritableDatabase();
        dbHelper.close();
    }

    // Opens the database to use it
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Closes the database when you no longer need it
    public void close() {
        dbHelper.close();
    }

    // ---------------------------------- TO BE MODIFIED!!! ----------------------------------------
    //COMMNTED ICON VALUE PUT AND DELETED THE ICON PARAMETER BECUASE IT WILL BE ENOUGH TO STATE THE DEVICE'S TYPE
    public long createDevice(String displayName, String mac, Long deviceTypeId) {
        // If the database is not open yet, open it
        if (!database.isOpen())
            open();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_DEVICE_DISPLAY_NAME, displayName);
        values.put(MySQLiteHelper.COLUMN_DEVICE_MAC, mac);
        values.put(MySQLiteHelper.COLUMN_DEVICE_TYPE_ID, deviceTypeId);
        long insertId = database.insert(MySQLiteHelper.TABLE_DEVICE, null, values);

        // If the database is open, close it
        if (database.isOpen())
            close();

        return insertId;
    }

    public long createDeviceType(String name, int icon) {

        if (!database.isOpen())
            open();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_DEVICE_TYPE_NAME, name);
        values.put(MySQLiteHelper.COLUMN_DEVICE_TYPE_ICON, icon);
        long insertId = database.insert(MySQLiteHelper.TABLE_DEVICE_TYPE, null, values);

        if (database.isOpen())
            close();

        return insertId;
    }

    public void deleteDevice(long id) {
        if (!database.isOpen())
            open();
        database.delete(MySQLiteHelper.TABLE_DEVICE, MySQLiteHelper.COLUMN_DEVICE_ID + " =?", new String[] { Long.toString(id)});
        if (database.isOpen())
            close();
    }

    public void deleteDevice(String mac) {
        if (!database.isOpen())
            open();
        database.delete(MySQLiteHelper.TABLE_DEVICE, MySQLiteHelper.COLUMN_DEVICE_MAC + " =?", new String[] {mac});
        if (database.isOpen())
            close();
    }

    public void updateDevice(Long deviceId, String displayName, Long deviceTypeId) {

        if (!database.isOpen())

            open();

        ContentValues args = new ContentValues();

        args.put(MySQLiteHelper.COLUMN_DEVICE_DISPLAY_NAME, displayName);

        args.put(MySQLiteHelper.COLUMN_DEVICE_TYPE_ID, deviceTypeId);

        database.update(MySQLiteHelper.TABLE_DEVICE, args, MySQLiteHelper.COLUMN_DEVICE_ID + "=?", new String[] { Long.toString(deviceId) });

        if (database.isOpen())

            close();

    }

    public Cursor getAllDevicesCursor() {

        if (!database.isOpen())

            open();

        Cursor cursor = database.rawQuery(

                "SELECT " +

                        MySQLiteHelper.COLUMN_DEVICE_ID + " AS _id," +

                        MySQLiteHelper.COLUMN_DEVICE_TYPE_ID +

                        " FROM " + MySQLiteHelper.TABLE_DEVICE, null);

        if (cursor != null) {

            cursor.moveToFirst();

        }

        if (database.isOpen())

            close();

        return cursor;

    }

    private Device cursorToDevice(Cursor cursor) {

        try {

            Device device = new Device();
            DeviceType deviceType = new DeviceType();

            device.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DEVICE_ID)));

            device.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DEVICE_DISPLAY_NAME)));

            deviceType.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DEVICE_TYPE_ID)));

            deviceType.setName(cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DEVICE_TYPE_NAME)));

            deviceType.setIcon(cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DEVICE_TYPE_ICON)));

            device.setDeviceType(deviceType);

            return device;

        } catch(CursorIndexOutOfBoundsException exception) {

            exception.printStackTrace();

            return null;

        }

    }

    public Device getDevice(long columnId) {

        if (!database.isOpen())
            open();

        Cursor cursor = database.rawQuery(

                "SELECT device.*, deviceType.* " +
                        " FROM " + MySQLiteHelper.TABLE_DEVICE +
                        " INNER JOIN " + MySQLiteHelper.TABLE_DEVICE_TYPE +
                        " ON device." + MySQLiteHelper.COLUMN_DEVICE_TYPE_ID + " = deviceType." + MySQLiteHelper.COLUMN_DEVICE_TYPE_ID +
                        " WHERE device." + MySQLiteHelper.COLUMN_DEVICE_ID + " = '" + columnId + "'", null);

        cursor.moveToFirst();
        Device device = cursorToDevice(cursor);
        cursor.close();

        if (database.isOpen())
            close();

        return device;

    }

    public Device getDevice(String macAddress) {

        if (!database.isOpen())
            open();

        Cursor cursor = database.rawQuery(

                "SELECT device.*, deviceType.* " +
                        " FROM " + MySQLiteHelper.TABLE_DEVICE +
                        " INNER JOIN " + MySQLiteHelper.TABLE_DEVICE_TYPE +
                        " ON device." + MySQLiteHelper.COLUMN_DEVICE_TYPE_ID + " = deviceType." + MySQLiteHelper.COLUMN_DEVICE_TYPE_ID +
                        " WHERE device." + MySQLiteHelper.COLUMN_DEVICE_MAC + " = '" + macAddress + "'", null);

        cursor.moveToFirst();
        Device device = cursorToDevice(cursor);
        cursor.close();

        if (database.isOpen())
            close();

        return device;
    }

    private DeviceType cursorToDeviceType(Cursor cursor) {

        try {

            DeviceType deviceType = new DeviceType();

            deviceType.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DEVICE_TYPE_ID)));

            deviceType.setName(cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DEVICE_TYPE_NAME)));

            deviceType.setIcon(cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DEVICE_TYPE_ICON)));

            return deviceType;

        } catch(CursorIndexOutOfBoundsException exception) {

            exception.printStackTrace();

            return null;

        }

    }

    public DeviceType getDeviceType(String name) {

        if (!database.isOpen())
            open();

        Cursor cursor = database.rawQuery(

                "SELECT * " +
                        "FROM " + MySQLiteHelper.TABLE_DEVICE_TYPE +
                        " WHERE " + MySQLiteHelper.COLUMN_DEVICE_TYPE_NAME + " = '" + name + "'", null);

        cursor.moveToFirst();
        DeviceType deviceType = cursorToDeviceType(cursor);
        cursor.close();

        if (database.isOpen())
            close();

        return deviceType;
    }

}