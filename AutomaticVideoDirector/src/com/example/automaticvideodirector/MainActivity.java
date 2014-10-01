package com.example.automaticvideodirector;


import android.net.*;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
*
* @author thilo
*/

public class MainActivity extends Activity {
	
	private static final String DEBUG_TAG = "Main-Activity";
	
	
	private static boolean isConnected = false;
	private TextView textView;
	private Button buttonIsConnected;
	private Button buttonConnect;
	private Button buttonRecord;	
	//For Testing
	private MetaData data = new MetaData("id-1","file1.mov","x-34");
	
	
	//Listener
	
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		buttonIsConnected = (Button) findViewById(R.id.button_isConnected);
		buttonIsConnected.setOnClickListener(isConnectedListener);
		
		buttonConnect = (Button) findViewById(R.id.button_connect);
		buttonConnect.setOnClickListener(connectListener);
		
		buttonRecord = (Button) findViewById(R.id.button_record);
		buttonRecord.setOnClickListener(recordAndShareListener);
		
		textView = (TextView) findViewById(R.id.textView_welcome);
		
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	
	
	
	
	/*
	 * Click Listener
	 */
	OnClickListener isConnectedListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			isConnected();
		}
	};
	OnClickListener connectListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
				isConnected();
				if(isConnected = true){
//			    	new HttpAsyncTask("POST", data).execute();
					new HttpAsyncTask("GET", data).execute();
			    	Log.d(DEBUG_TAG, "Http Task instance created");
			    } else {
			    	textView.setText("No network connection available.");
			    }
		}
	};
	
	OnClickListener recordAndShareListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			isConnected();
			if(isConnected == true){
				
				Intent intent = new Intent(MainActivity.this, CameraActivity.class);
				Log.d(DEBUG_TAG, "Change to Video CameraActivity.class");
				startActivity(intent);
		    	
		    } else {
		    	textView.setText("No network connection available.");
		    }
		}
	};
	
	
	
	/*
	 * Utils
	 */
	
	public boolean isConnected(){
		ConnectivityManager connMgr = (ConnectivityManager) 
			    getSystemService(Context.CONNECTIVITY_SERVICE);
			    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			    if (networkInfo != null && networkInfo.isConnected()) {
			    	isConnected = true;
			    	Toast.makeText(MainActivity.this, "Your device has connection to the Inetrnet",
			    			Toast.LENGTH_LONG).show();
			    	Log.d(DEBUG_TAG, "Connection possible");
			    } else {
			    	isConnected = false;
			    	Toast.makeText(MainActivity.this, "Your device has NO connection to the Inetrnet",
			    			Toast.LENGTH_LONG).show();
			    	Log.d(DEBUG_TAG, "Connection not possible");
			    }
				return isConnected;
	}
	
	


}
