package nl.groenier.android.bluetoothdevicecontrollerapp.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import nl.groenier.android.bluetoothdevicecontrollerapp.R;

/**
 * Created by Martijn on 29/09/2016.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "bluetooth_application.db";
    private static final int DATABASE_VERSION = 19;

    // device table
    public static final String TABLE_DEVICE = "device";
    public static final String COLUMN_DEVICE_ID = "device_id";
    public static final String COLUMN_DEVICE_DISPLAY_NAME = "device_display_name";
    public static final String COLUMN_DEVICE_MAC = "device_mac";
    //public static final String COLUMN_DEVICE_TYPE = "device_type";

    // deviceType table
    public static final String TABLE_DEVICE_TYPE = "deviceType";
    public static final String COLUMN_DEVICE_TYPE_ID = "deviceType_id";
    public static final String COLUMN_DEVICE_TYPE_NAME = "deviceType_name";
    public static final String COLUMN_DEVICE_TYPE_ICON = "deviceType_icon";

    // Creating the device table
    private static final String DATABASE_CREATE_DEVICE =
            "CREATE TABLE " + TABLE_DEVICE +
                    "(" +
                    COLUMN_DEVICE_ID + " integer primary key autoincrement, " +
                    COLUMN_DEVICE_DISPLAY_NAME + " text not null, " +
                    COLUMN_DEVICE_MAC + " text not null, " +
                    COLUMN_DEVICE_TYPE_ID + " integer, " +
                    "FOREIGN KEY(" + COLUMN_DEVICE_TYPE_ID + ") REFERENCES " + TABLE_DEVICE_TYPE + "(" + COLUMN_DEVICE_TYPE_ID + ") ON DELETE CASCADE" +
                    ");";

    // Creating the deviceType table
    private static final String DATABASE_CREATE_DEVICE_TYPE =
            "CREATE TABLE " + TABLE_DEVICE_TYPE +
                    "(" +
                    COLUMN_DEVICE_TYPE_ID + " integer primary key autoincrement, " +
                    COLUMN_DEVICE_TYPE_NAME + " text not null," +
                    COLUMN_DEVICE_TYPE_ICON + " integer" +
                    ");";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute the sql to create the table assignments
        db.execSQL(DATABASE_CREATE_DEVICE);
        db.execSQL(DATABASE_CREATE_DEVICE_TYPE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // When the database gets upgraded you should handle the update to make sure there is no data loss.
        // This is the default code you put in the upgrade method, to delete the table and call the oncreate again.
        //if(oldVersion == 1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE_TYPE);
            onCreate(db);
        //}
    }

}