package com.crowdfire.wardobe.database;

/**
 * Created by Anuja on 12/4/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Anuja on 12/4/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "CrowdFire";

    // Table Names
    private static final String DB_TABLE = "wardobe";
    private static final String DB_TABLE_FAV = "wardobe_fav";

    // column names
    private static final String KEY_TOP_NAME = "image_type";
    private static final String KEY_TOP_IMAGE = "image_data";
    private static final String FAV_TOP_IMAGE = "fav_top_data";
    private static final String FAV_BOTTOM_IMAGE = "fav_bottom_data";
    // Table create statement
    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "("+
            KEY_TOP_NAME + " TEXT," +
            KEY_TOP_IMAGE + " BLOB);";

    private static final String CREATE_TABLE_IMAGE_FAV = "CREATE TABLE " + DB_TABLE_FAV + "("+
            FAV_TOP_IMAGE + " BLOB," +
            FAV_BOTTOM_IMAGE + " BLOB);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating table
        db.execSQL(CREATE_TABLE_IMAGE);
        db.execSQL(CREATE_TABLE_IMAGE_FAV);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_FAV);

        // create new table
        onCreate(db);
    }
}