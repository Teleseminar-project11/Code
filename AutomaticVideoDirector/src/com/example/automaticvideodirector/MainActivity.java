package com.example.automaticvideodirector;

import java.io.File;

import com.example.automaticvideodirector.database.MetaData;

import android.net.*;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserActivity;

/**
*
* @author thilo
*/

public class MainActivity extends Activity {
	
	private static final String DEBUG_TAG = "Main-Activity";
	public static final String REQUEST_URL = "com.example.automaticvideodirector.REQUEST_URL";
	public static final String FILE_UPLOAD = "com.example.automaticvideodirector.FILEUPLOAD";
	
	private static boolean isConnected = false;
	private TextView textView;
	private Button buttonIsConnected;
	private Button buttonConnect;
	private Button buttonRecord;	
	
	final int ACTIVITY_CHOOSE_FILE = 1;
	final int A_FILE_DIALOG_ACTIVITY = 2;
	
	private TextView filePathTextView;
	//For Testing
//	private MetaData data = new MetaData("id-1","file1.mov","x-34");

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
		
		filePathTextView = (TextView)findViewById(R.id.select_file);
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
//		    	new HttpAsyncTask("POST", data).execute();
//				new HttpAsyncTask("GET", data).execute();
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
	
	private void show_toast (String s) {
    	Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
        toast.show();
	}
	
    public void sendFile(View view) {    	
    	EditText editText = (EditText) findViewById(R.id.select_file);
        String uploadFile = editText.getText().toString();
        Log.d(DEBUG_TAG, uploadFile);
        if (!(new File(uploadFile).exists())) {
        	show_toast("File does not exist!");
        	return;
        }
        
        String requestURL = getString(R.string.video_upload_url);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    if (networkInfo != null && networkInfo.isConnected()) {
	    	MetaData video = new MetaData();
	    	video.setVideoFile(uploadFile);
			new HttpAsyncTask(HttpAsyncTask.HTTP_UPLOAD, requestURL, video,
				new HttpAsyncTask.Callback() {
					@Override
					public void run(String result) {
						try {
							show_toast(result);
						} catch (Exception e) {
							show_toast("Upload failed");
						}
					}
				}
			).execute();
	    	
        } else {
	    	show_toast("No network");
	    	return;
	    }
    	
    	show_toast("Transefing: " + filePathTextView.getText());
    }

    public void pickFile(View view) {
    	// One way to do it. Launches application selection dialog.
//    	Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
//    	chooseFile.setType("file/*");
//    	Intent c = Intent.createChooser(chooseFile, "Choose file");
//    	startActivityForResult(c, ACTIVITY_CHOOSE_FILE);
    	
    	// Another way to do it: aFileDialog library.
		Intent intent = new Intent(this, FileChooserActivity.class);
		intent.putExtra(FileChooserActivity.INPUT_START_FOLDER, Environment.getExternalStorageDirectory() + "/Download/");
		intent.putExtra(FileChooserActivity.INPUT_CAN_CREATE_FILES, false);
		this.startActivityForResult(intent, A_FILE_DIALOG_ACTIVITY);
    }
    
    public void sendHttpGet(View view) {
		Intent intent = new Intent(this, DisplayMessageActivity.class);
        
//        String requestURL = "http://www.google.ru/killer-robots.txt";
        String requestURL = getString(R.string.video_list_url);
    	intent.putExtra(REQUEST_URL, requestURL);
    	
    	show_toast("Getting: " + requestURL);
    	startActivity(intent);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
	    	case ACTIVITY_CHOOSE_FILE: {
	    		if (resultCode == RESULT_OK) {
	    			Uri uri = data.getData();
	    			String filePath = uri.getPath();
	    			filePathTextView.setText(filePath);
	    		}
	    	}
	    	case A_FILE_DIALOG_ACTIVITY: {
	    		if (resultCode == RESULT_OK) {
//	    			boolean fileCreated = false;
	    			String filePath = "";
	
	    			Bundle bundle = data.getExtras();
	    			if(bundle != null) {
	    				if(bundle.containsKey(FileChooserActivity.OUTPUT_NEW_FILE_NAME)) {
//	    					fileCreated = true;
	    					File folder = (File) bundle.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
	    					String name = bundle.getString(FileChooserActivity.OUTPUT_NEW_FILE_NAME);
	    					filePath = folder.getAbsolutePath() + "/" + name;
	    				} else {
//	    					fileCreated = false;
	    					File file = (File) bundle.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
	    					filePath = file.getAbsolutePath();
	    				}
	    			}
	    			filePathTextView.setText(filePath);
	    		} else {
	    			filePathTextView.setText("");
	    		}
	    	}
		}
    }
}
