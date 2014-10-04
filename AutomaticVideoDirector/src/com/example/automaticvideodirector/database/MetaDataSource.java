package com.example.automaticvideodirector.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MetaDataSource {
	

	private SQLiteDatabase database;
	private VideoDatabaseHelper dbHelper;
	private String[] allColumns = {	VideoDatabaseHelper.COLUMN_ID, 
									VideoDatabaseHelper.COLUMN_FILENAME, 
									VideoDatabaseHelper.COLUMN_TIMESTAMP, 
									VideoDatabaseHelper.COLUMN_DURATION, 
									VideoDatabaseHelper.COLUMN_RESOLUTION, 
									VideoDatabaseHelper.COLUMN_FRAMERATE,      };
	
	public MetaDataSource(Context context) {
		Log.d("DATABASE-CHECK","before dbHelper = new....");
	    dbHelper = new VideoDatabaseHelper(context);
	}
	
	public void open() throws SQLException {
		Log.d("DATABASE-CHECK","before dbHelper.getWriteable....");
	    database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
	    dbHelper.close();
	}
	
	//AFTER A VIDEOFILE IS CREATED NEW METADATA WILL BE INSERTED TO THE DATABASE AND HAS TO BE SEND VIA HTTP_POST TO THE SERVER
	public void insertMetaData(MetaData data) {
	    ContentValues values = new ContentValues();
	    
	    values.put(VideoDatabaseHelper.COLUMN_FILENAME, data.getVideoFile());
	    values.put(VideoDatabaseHelper.COLUMN_TIMESTAMP, data.getTimeStamp());
	    values.put(VideoDatabaseHelper.COLUMN_DURATION, data.getDuration());
	    values.put(VideoDatabaseHelper.COLUMN_RESOLUTION, data.getResolution());
	    values.put(VideoDatabaseHelper.COLUMN_FRAMERATE, data.getFrameRate());
	    Log.d("DATABASE-CHECK","before status");
	 

	    long insertId= database.insert(VideoDatabaseHelper.TABLE_METADATA, null, values);
	    Cursor cursor = database.query(VideoDatabaseHelper.TABLE_METADATA,
	            allColumns, VideoDatabaseHelper.COLUMN_ID + " = " + insertId, null,
	            null, null, null);
	        cursor.moveToFirst();
	        Log.d("INSERT CHECK", "ID:"+cursor.getLong(0));
	        Log.d("INSERT CHECK", "Filename:"+cursor.getString(1));
	        Log.d("INSERT CHECK", "Timestamp:"+cursor.getInt(2));
	        Log.d("INSERT CHECK", "Duration:"+cursor.getInt(3));
	        Log.d("INSERT CHECK", "resolution:"+cursor.getString(4));
	        Log.d("INSERT CHECK", "framerate:"+cursor.getInt(5));
	        cursor.close();
	    Log.d("DATABASE-CHECK","after status");

	}
	
	
	//Gets the metadata of a specific requested videofile.--->Check status if a videofile is available on this device and hasn't been uploaded yes(Column_STATUS)
	public MetaData selectMetaData(String filename){
		String selectQuery = "SELECT  * FROM " + VideoDatabaseHelper.TABLE_METADATA +"WHERE"+VideoDatabaseHelper.COLUMN_FILENAME+"="+filename;
		Cursor cursor = database.rawQuery(selectQuery, null);
		MetaData newMetaData=new MetaData();
		if (cursor!=null) {
	        	newMetaData.setId(cursor.getLong(0));
	        	newMetaData.setVideoFile(cursor.getString(1));
	        	newMetaData.setTimeStamp(cursor.getString(2));
	        	newMetaData.setDuration(cursor.getString(3));
	        	newMetaData.setResolution(cursor.getString(4));
	        	newMetaData.setFrameRate(cursor.getString(5));
	        }
		return newMetaData;
	}
	

}
