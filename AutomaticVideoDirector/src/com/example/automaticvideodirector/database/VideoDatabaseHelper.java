package com.example.automaticvideodirector.database;

import android.content.Context;
import android.database.sqlite.*;
import android.util.Log;

public class VideoDatabaseHelper extends SQLiteOpenHelper {
	
    private static final String DATABASE_NAME = "metadata.db";
    private static final int DATABASE_VERSION = 1;
    
	public static final String TABLE_METADATA = "metadata";
    
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FILENAME = "filename";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_RESOLUTION = "resolution";
    public static final String COLUMN_FRAMERATE = "framerate";
    public static final String COLUMN_SERVERID = "serverId";
    public static final String COLUMN_STATUS = "status";
    

    
    
    
    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
        + TABLE_METADATA + "(" + COLUMN_ID + " integer primary key autoincrement, "
        + COLUMN_FILENAME + " text,"+ COLUMN_TIMESTAMP + " text,"+ COLUMN_DURATION + " integer,"
        + COLUMN_RESOLUTION + " text,"+ COLUMN_FRAMERATE+ " integer,"+ COLUMN_SERVERID+" integer,"+ COLUMN_STATUS+" text)";
    
    
    VideoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    

    @Override
    public void onCreate(SQLiteDatabase db) {
    	Log.d("DATABASE-CHECK","execute");
        db.execSQL(DATABASE_CREATE);
        Log.d("DATABASE-CHECK","execute");
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}