package com.example.automaticvideodirector;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.automaticvideodirector.database.MetaData;
import com.example.automaticvideodirector.database.MetaDataSource;
/**
 * @author thilo
 * 
 * This activity is responsible to record a video (of max. 20sec???). 
 * After creating the mediafile, a connection to the database has to be established to insert all the necessary meta-information.-->MetaDatSource.insertMetaData();
 * Additionally a HTTPURLCONNECTION has to be created to POST the metadata via JSON to the server--> new HttpAsnycTask("POST",MetaData metaData);
 * Last but not least information to user, if successful or not.
 * 
 */
public class CameraActivity extends Activity implements Observer {
	
	private static final String DEBUG_TAG = "CameraActivity";
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	private boolean isRecording = false;
	
	private Camera cameraInstance;
	private CameraPreview cameraPreview;
	private MediaRecorder mediaRecorder;
	
	private MetaDataSource datasource;
	private MetaData metaData;
	private File handlerFile;
	
	private Button captureButton;
	
	private ShakeDetection shakeDetector;
	private SensorManager sensorManager;
	private int counter;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		captureButton = (Button) findViewById(R.id.button_capture);
   
	}
	
	protected void onResume() {
		super.onResume();
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		 
		Log.d(DEBUG_TAG,"Camara Activity resumed");
		//CAMERA PREVIEW
		cameraInstance = getCameraInstance();
		cameraPreview = new CameraPreview(this, cameraInstance);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);
        
        //ESTABLISH CONNECTION TO DATABASE 
        datasource = new MetaDataSource(this);
        datasource.open();
        
        //SENSOR MANAGER
        counter=0;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        shakeDetector = new ShakeDetection(sensorManager);
        shakeDetector.addObserver(CameraActivity.this);
	}
	
	
	@Override
    protected void onPause() {
		Log.d(DEBUG_TAG,"Camera Activity paused");
        datasource.close();
        releaseMediaRecorder();
        releaseCamera();
        counter=0;
        shakeDetector.deleteObservers();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onPause();
	}
	
//	@Override
//    protected void onStop() {
//		Log.d(DEBUG_TAG,"Camera Activity stopped");
//        datasource.close();
//        releaseMediaRecorder();
//        releaseCamera();
//        counter=0;
//        shakeDetector.deleteObservers();
//        super.onStop();
//    }
//
//	@Override    
//	protected void onDestroy() {
//		Log.d(DEBUG_TAG,"Camera Activity destroyed");
//		datasource.close();
//		releaseMediaRecorder();
//		releaseCamera();
//		counter=0;
//		shakeDetector.deleteObservers();
//		super.onDestroy();
//	}

	/**IS CALLED WHEN THERE ARE CHANGES IN THE ACCELEROMETER OBSERVABLE */
	@Override
	public void update(Observable observable, Object data) {
		counter++;
		if(counter%10==0){
			show_toast("Reduce Shaking");
		}
	}

	/**REALEASE MEDIA RECORDER*/
    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
//            cameraInstance.lock();
        }
    }

    private void releaseCamera(){
        if (cameraInstance != null){
        	cameraInstance.release();
        	cameraInstance = null;
        }
    }

    /** GET AN INSTANCE OF THE CAMERA OBJECT */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	    	Log.d(DEBUG_TAG,"Camera.open() failed");
	    	// Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}

	/** PREPARE MEDIA RECORDER FOR RECORDING */
	private boolean prepareVideoRecorder(){
		Log.d(DEBUG_TAG,"prepareVideoRecorder()");
//	    cameraInstance = camera;
	    mediaRecorder = new MediaRecorder();

	    // Step 1: Unlock and set camera to MediaRecorder
	    cameraInstance.unlock();
	    mediaRecorder.setCamera(cameraInstance);
	    // Step 2: Set sources
	    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	    mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
	    // Step 4: Set output file
	    handlerFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
	    mediaRecorder.setOutputFile(handlerFile.toString());
	    // Step 5: Set the preview output
	    mediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());
	    // Step 6: Prepare configured MediaRecorder
	    try {
	    	mediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.d(DEBUG_TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d(DEBUG_TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    Log.i(DEBUG_TAG,"Media recorder successfully prepared");
	    return true;
	}
	
	

	/** Create a file Uri for saving an image or video */
//	private static Uri getOutputMediaFileUri(int type){
//	      return Uri.fromFile(getOutputMediaFile(type));
//	}
	
	static public String getVideoDir() {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) 
				+ "/Automatic Video Director";
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, we also should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this!!!!!!!!!!!!!!.
		Log.d("CameraActivity","in getOutputMediaFile1");
//	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//	              Environment.DIRECTORY_PICTURES), "Automatic Video Director");
		File mediaStorageDir = new File(getVideoDir());
	    // This location works best if we want the created images to be shared
	    // between applications and persist after your app has been uninstalled.
	    Log.d("CameraActivity",mediaStorageDir.getPath());
	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	    	Log.d("MyCameraApp", "Directory does not exists");
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }
	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }
	    return mediaFile;
	}

	/**
	 * Listener 
	 */
	public void recordListener(View v) {
        if (isRecording) {
            // inform the user that recording has stopped
            Log.d(DEBUG_TAG,"Media recorder was already recording");
            
            // stop recording and release camera
            mediaRecorder.stop();   // stop the recording
            Log.i(DEBUG_TAG,"Media recorder stopped");
            releaseMediaRecorder(); // release the MediaRecorder object
            Log.i(DEBUG_TAG,"Media recorder released");
            cameraInstance.lock();  // take camera access back from MediaRecorder
            
            Log.d(DEBUG_TAG,"Media recorder stopped, camera locked");
            
            
            //TODO Hangs if you do this 
//            captureButton.setText(getString(R.string.button_capture));
            captureButton.setBackgroundColor(getResources().getColor(R.color.green));
            
            isRecording = false;
            
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Log.d(DEBUG_TAG,Uri.fromFile(handlerFile).toString());
            retriever.setDataSource(CameraActivity.this, Uri.fromFile(handlerFile));
            
            metaData=getMetaDataFromFile(retriever);
            Date lastModDate = new Date(handlerFile.lastModified());
            Timestamp timestamp = new Timestamp(lastModDate.getTime());
            metaData.setTimeStamp(timestamp.toString());
            long insertId = datasource.insertMetaData(metaData);

            Log.d(DEBUG_TAG,"New row in table: ID=" +insertId +" Counter: "+counter);
            counter=0;
        	new HttpAsyncTask(HttpAsyncTask.HTTP_POST,
        			ServerLocations.getVideoMetadataUploadUrl(CameraActivity.this), 
        			metaData, 
	        		new HttpAsyncTask.Callback() {
						@Override
						public void run(String result, int code) {
							if (result != null && code == HttpURLConnection.HTTP_OK) {
								try {   
									//UPDATE DATABASE WITH SERVERID...
									System.out.println(result);
									String no_escape = result
											.replace("\\\"", "\"")
											.replace("\"{", "{")
											.replace("}\"", "}");
									JSONObject json = new JSONObject(no_escape);
									int serverID = json.getInt("id");
									String name = json.getString("name");
									System.out.println(name + " --> " + serverID);
									datasource.updateServerId(serverID, name);
									show_toast("Metadata was succefully sent to the server. "
											+name +" has new server-ID: "+ serverID);	
								} catch (Exception e) {
									System.out.println(e.getMessage());
									show_toast("Upload of MetaData has failed");
								}
							} else {
								show_toast("Server returned an error: " + code);
							}
						}
			}).execute();
            //Scans the external directory to add the media file immediately after creating
        	MediaScannerConnection.scanFile(CameraActivity.this,
        	          new String[] { handlerFile.toString() }, null,
        	          new MediaScannerConnection.OnScanCompletedListener() {
        	      public void onScanCompleted(String path, Uri uri) {
        	          Log.i("ExternalStorage", "Scanned " + path + ":");
        	          Log.i("ExternalStorage", "-> uri=" + uri);
        	      }
        	});
            
            
        } else {
        	
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
            	Log.d("CameraActivity","Media recorder will be started in the next line");
                mediaRecorder.start();
                Log.d("CameraActivity","Media recorder started");
                counter=0;
                // inform the user that recording has started
                
                //TODO Hangs if you do this 
//                captureButton.setText(getString(R.string.button_stop));
                captureButton.setBackgroundColor(getResources().getColor(R.color.red));
                
                isRecording = true;
                Log.i("CameraActivity","Media recorder started");
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }
	}

	public MetaData getMetaDataFromFile(MediaMetadataRetriever retriever){
		Log.i(DEBUG_TAG, "Retrieve meta data from mediafile");
		MetaData data = new MetaData();
		data.setVideoFile(handlerFile.getName());
//		data.setTimeStamp(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
		data.setTimeStamp(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
		data.setDuration(Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
		data.setShaking(counter);
		data.setWidth(Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)));
		data.setHeight(Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)));
		return data;
		
	}
	
	private void show_toast (String s) {
    	Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
        toast.show();
	}

	
}
