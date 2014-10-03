package com.example.automaticvideodirector.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MetaDataSource {
	

	private SQLiteDatabase database;
	private VideoDatabaseHelper dbHelper;
	private String[] allColumns = {	VideoDatabaseHelper.COLUMN_ID, 
									VideoDatabaseHelper.COLUMN_FILENAME, 
									VideoDatabaseHelper.COLUMN_TIMESTAMP, 
									VideoDatabaseHelper.COLUMN_DURATION, 
									VideoDatabaseHelper.COLUMN_RESOLUTION, 
									VideoDatabaseHelper.COLUMN_FRAMERATE,
									VideoDatabaseHelper.COLUMN_STATUS       };
	
	public MetaDataSource(Context context) {
	    dbHelper = new VideoDatabaseHelper(context);
	}
	
	public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
	    dbHelper.close();
	}
	
	//AFTER A VIDEOFILE IS CREATED NEW METADATA WILL BE INSERTED TO THE DATABASE AND HAS TO BE SEND VIA HTTP_POST TO THE SERVER
	public void insertMetaData(String filename, String timestamp, String duration, String resolution, String framerate, String status) {
	    ContentValues values = new ContentValues();
	    
	    values.put(VideoDatabaseHelper.COLUMN_FILENAME, filename);
	    values.put(VideoDatabaseHelper.COLUMN_TIMESTAMP, timestamp);
	    values.put(VideoDatabaseHelper.COLUMN_DURATION, duration);
	    values.put(VideoDatabaseHelper.COLUMN_RESOLUTION, resolution);
	    values.put(VideoDatabaseHelper.COLUMN_FRAMERATE, framerate);
	    values.put(VideoDatabaseHelper.COLUMN_STATUS, status);

	    database.insert(VideoDatabaseHelper.TABLE_METADATA, null, values);

	}
	
	
	//Gets the metadata of a specific requested videofile.--->Check status if a videofile is available on this device and hasn't been uploaded yes(Column_STATUS)
	public MetaData selectMetaData(String filename){
		String selectQuery = "SELECT  * FROM " + VideoDatabaseHelper.TABLE_METADATA +"WHERE"+VideoDatabaseHelper.COLUMN_FILENAME+"="+filename;
		Cursor cursor = database.rawQuery(selectQuery, null);
		MetaData newMetaData=new MetaData();
		if (cursor!=null) {
	        	newMetaData.setId(cursor.getLong(0));
	        	newMetaData.setVideoFile(cursor.getString(1));
	        	newMetaData.setTimeStamp(cursor.getInt(2));
	        	newMetaData.setDuration(cursor.getString(3));
	        	newMetaData.setResolution(cursor.getString(4));
	        	newMetaData.setFrameRate(cursor.getString(5));
	        	newMetaData.setStatus(cursor.getString(6));
	        }
		return newMetaData;
	}
}
