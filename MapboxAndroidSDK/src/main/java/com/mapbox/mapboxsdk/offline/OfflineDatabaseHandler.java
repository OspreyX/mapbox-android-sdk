package com.mapbox.mapboxsdk.offline;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OfflineDatabaseHandler  extends SQLiteOpenHelper
{
    private static OfflineDatabaseHandler offlineDatabaseHandler;

    private static final String TAG = "OfflineDatabaseHandler";

    // All Static variables
    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "MapboxOfflineDatabase";

    // Table name(s)
    public static final String TABLE_METADATA = "metadata";
    public static final String TABLE_DATA = "data";
    public static final String TABLE_RESOURCES = "resources";

    // Table Fields
    public static final String FIELD_METADATA_NAME = "name";
    public static final String FIELD_METADATA_VALUE = "value";

    public static final String FIELD_DATA_ID = "id";
    public static final String FIELD_DATA_VALUE = "value";

    public static final String FIELD_RESOURCES_ID = "id";
    public static final String FIELD_RESOURCES_URL = "url";
    public static final String FIELD_RESOURCES_STATUS = "value";

    /**
     * Constructor
     * @param context Context
     */
    private OfflineDatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static OfflineDatabaseHandler getInstance(Context context) {
        if (offlineDatabaseHandler == null) {
            offlineDatabaseHandler = new OfflineDatabaseHandler(context);
        }
        return offlineDatabaseHandler;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.w(TAG, "onCreate() called... Setting up application's database.");
        // Create The table(s)
        String metadata = "CREATE TABLE " + TABLE_METADATA + " (" + FIELD_METADATA_NAME  +" TEXT UNIQUE, " + FIELD_METADATA_VALUE +  " TEXT);";
        String data = "CREATE TABLE " + TABLE_DATA + " (" + FIELD_DATA_ID  +" INTEGER PRIMARY KEY, " + FIELD_DATA_VALUE +  " BLOB);";
        String resources = "CREATE TABLE " + TABLE_RESOURCES + " (" + FIELD_RESOURCES_URL  +" TEXT UNIQUE, " + FIELD_RESOURCES_STATUS +  " TEXT, " + FIELD_RESOURCES_ID + " INTEGER REFERENCES data);";

        db.execSQL("PRAGMA foreign_keys=ON;");
        db.beginTransaction();

        try {
            db.execSQL(metadata);
            db.execSQL(data);
            db.execSQL(resources);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Error creating database: " + e.toString());
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(TAG,"Upgrading database from version " + oldVersion + " to "+ newVersion + ", which will destroy all old data");
        db.execSQL("drop table if exists " + TABLE_METADATA);
        db.execSQL("drop table if exists " + TABLE_DATA);
        db.execSQL("drop table if exists " + TABLE_RESOURCES);
        onCreate(db);
    }
}