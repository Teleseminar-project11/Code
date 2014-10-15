package com.example.automaticvideodirector;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.automaticvideodirector.database.MetaData;
import com.example.automaticvideodirector.database.MetaDataSource;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class HttpGetService extends Service{
	
	
	
	private static final String DEBUG_TAG = "Get Service";
	
	private Thread t;
	private Runnable r;
	private static boolean isInterrupted = false;
	
	private MetaDataSource datasource;
	
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.d(DEBUG_TAG,"onStartCommand()");
	    return Service.START_NOT_STICKY;
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(DEBUG_TAG,"onBind()");
		return null;
	}
	
	
	@Override
	public void onCreate(){
		Log.d(DEBUG_TAG,"onCreate()");
		
		r = new Runnable(){

			@Override
			public void run() {
				Log.d(DEBUG_TAG,"run Thread");
				while(!isInterrupted){
				try {
					Thread.sleep(10000);
//					tryUpload();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Log.d(DEBUG_TAG,"THREAD TEST");
				}
				
			}
		};
		datasource = new MetaDataSource(this);
		Thread t = new Thread(r);
		t.start();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		stopThread();
		Log.d(DEBUG_TAG, t.toString());
	}
	
	
	public void stopThread(){
		isInterrupted = true;
	}
	
	public void startThread(){
		isInterrupted = false;
	}
	
	
	
	
	public void tryUpload() {    	
    	String selectedURL = ServerLocations.getSelectedListUrl(this);
    	
    	ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    if (networkInfo == null || !networkInfo.isConnected()) {
	    	show_toast("No network");
	    	return;
	    }
	    
		new HttpAsyncTask(HttpAsyncTask.HTTP_GET, selectedURL, null,
			new HttpAsyncTask.Callback() {
				@Override
				public void run(String result) {
					if (result != null) {
					    String no_escape = result
								.replace("[", "")
								.replace("]", "");
					    System.out.println(no_escape);
						try {
							JSONObject json = new JSONObject(no_escape);
							int serverID = json.getInt("id");
							System.out.println("Requested to upload " + serverID);
							// TODO check videos and get video id from metadata
							uploadVideo(serverID);
						} catch (JSONException e) {
							System.out.println("Invalid json response");
							show_toast("HTTP_GET failed:" + result);
						}					
	    			} else {
	    				show_toast("HTTP_GET failed:" + result);
	    			}
				}
			}
		).execute();
    }
    
    public void uploadVideo (int id) {    	
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    if (networkInfo != null && networkInfo.isConnected()) {
	    	String requestURL = ServerLocations.getVideoUploadUrl(this, id);
	    	datasource.open();
	    	MetaData video = datasource.selectMetaData(id);
	    	datasource.close();
	    	show_toast("Transefing: " + video.getVideoFile());
	    	
			new HttpAsyncTask(HttpAsyncTask.HTTP_UPLOAD, requestURL, video,
				new HttpAsyncTask.Callback() {
					@Override
					public void run(String result) {
						if (result != null) {
							show_toast(result);
						} else {
							show_toast("Upload failed");
						}
					}
				}
			).execute();
	    	
        } else {
	    	show_toast("No network");
	    	return;
	    }
    }
    
    
	private void show_toast (String s) {
    	Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
        toast.show();
	}

}