package nl.groenier.android.bluetoothdevicecontrollerapp.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Martijn on 29/09/2016.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "bluetooth_application.db";
    private static final int DATABASE_VERSION = 9;
    // Courses
    public static final String TABLE_DEVICE = "device";
    public static final String COLUMN_DEVICE_ID = "device_id";
    public static final String COLUMN_DEVICE_DISPLAY_NAME = "device_display_name";
    public static final String COLUMN_DEVICE_MAC = "device_mac";
    public static final String COLUMN_DEVICE_TYPE = "device_type";
    public static final String COLUMN_DEVICE_ICON = "device_icon";

    // Creating the table
    private static final String DATABASE_CREATE_SERIES =
            "CREATE TABLE " + TABLE_DEVICE +
                    "(" +
                    COLUMN_DEVICE_ID + " integer primary key autoincrement, " +
                    COLUMN_DEVICE_DISPLAY_NAME + " text not null," +
                    COLUMN_DEVICE_MAC + " text not null," +
                    COLUMN_DEVICE_TYPE + " text not null," +
                    COLUMN_DEVICE_ICON + " int" +
                    ");";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute the sql to create the table assignments
        db.execSQL(DATABASE_CREATE_SERIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // When the database gets upgraded you should handle the update to make sure there is no data loss.
        // This is the default code you put in the upgrade method, to delete the table and call the oncreate again.
        //if(oldVersion == 1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
            onCreate(db);
        //}
    }

}