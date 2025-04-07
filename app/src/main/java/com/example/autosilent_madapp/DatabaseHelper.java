//package com.example.autosilent_madapp;
//
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//public class DatabaseHelper extends SQLiteOpenHelper {
//
//
//    private static final String DATABASE_NAME = "autosilent";
//    private static final int DATABASE_VERSION = 1;
//    public static final String TABLE_NAME = "locations";
//    public static final String COLUMN_ID = "id";
//    public static final String COLUMN_ADDRESS = "address";
//    public static final String COLUMN_LATITUDE = "latitude";
//    public static final String COLUMN_LONGITUDE = "longitude";
//
//
//
//    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
//                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                COLUMN_ADDRESS + " TEXT, " +
//                COLUMN_LATITUDE + " REAL, " +
//                COLUMN_LONGITUDE + " REAL)";
//        db.execSQL(createTableQuery);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);
//    }
//
//    // Insert location into the database
//    public long insertLocation(String address, double latitude, double longitude) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_ADDRESS, address);
//        values.put(COLUMN_LATITUDE, latitude);
//        values.put(COLUMN_LONGITUDE, longitude);
//        return db.insert(TABLE_NAME, null, values);
//    }
//
//
//    // Check if a location already exists in the database
//    public boolean isLocationExists(double latitude, double longitude) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
//                COLUMN_LATITUDE + " = ? AND " +
//                COLUMN_LONGITUDE + " = ?";
//        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(latitude), String.valueOf(longitude)});
//        boolean exists = cursor.getCount() > 0;
//        cursor.close();
//        return exists;
//    }
//
//    // Fetch all locations from the database
//    public Cursor getAllLocations() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
//    }
//
////    extra added
//    public int deleteLocation(int id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
//    }
//
//}


// version to database updated to include time database





package com.example.autosilent_madapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "autosilent";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "locations";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";



    // Timer Table
    public static final String TABLE_TIMER = "timer_data";
    public static final String COLUMN_TIMER_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL)";

        // Create timer table
        String createTimerTable = "CREATE TABLE " + TABLE_TIMER + " (" +
                COLUMN_TIMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_START_TIME + " TEXT, " +
                COLUMN_END_TIME + " TEXT)";
        db.execSQL(createTimerTable);
        db.execSQL(createTableQuery);
        Log.d("DatabaseHelper", "onCreate called");

    }

//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMER);
//        onCreate(db);
//    }


//    this is important it upgrades the data to upgraded version....
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Create timer_data table if it doesn't exist
            String createTimerTable = "CREATE TABLE IF NOT EXISTS " + TABLE_TIMER + " (" +
                    COLUMN_TIMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_START_TIME + " TEXT, " +
                    COLUMN_END_TIME + " TEXT)";
            db.execSQL(createTimerTable);
        }
    }

    // Insert location into the database
    public long insertLocation(String address, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        return db.insert(TABLE_NAME, null, values);
    }


    // Check if a location already exists in the database
    public boolean isLocationExists(double latitude, double longitude) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_LATITUDE + " = ? AND " +
                COLUMN_LONGITUDE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(latitude), String.valueOf(longitude)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Fetch all locations from the database
    public Cursor getAllLocations() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    //    extra added
    public int deleteLocation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Method to delete timer data by its ID
    public int deleteTimerData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TIMER, COLUMN_TIMER_ID + "=?", new String[]{String.valueOf(id)});
    }


    // Insert timer data into the database
    public long insertTimerData(String date, String startTime, String endTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_START_TIME, startTime);
        values.put(COLUMN_END_TIME, endTime);
        return db.insert(TABLE_TIMER, null, values);
    }

    // Fetch all timer data from the database
    public Cursor getAllTimerData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TIMER, null);
    }

}
