package com.example.automaticvideodirector;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.automaticvideodirector.database.MetaData;
import com.example.automaticvideodirector.database.MetaDataSource;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
/**
 * @author thilo
 * 
 * This activity is responsible to record a video (of max. 20sec???). 
 * After creating the mediafile, a connection to the database has to be established to insert all the necessary meta-information.-->MetaDatSource.insertMetaData();
 * Additionally a HTTPURLCONNECTION has to be created to POST the metadata via JSON to the server--> new HttpAsnycTask("POST",MetaData metaData);
 * Last but not least information to user if successful or not.
 * 
 */
public class CameraActivity extends Activity {
	
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	private static final int request =1;
	
	private boolean isRecording = false;
	
	private Camera cameraInstance;
	private CameraPreview cameraPreview;
	private MediaRecorder mediaRecorder;
	
	private MetaDataSource datasource;
	private MetaData metaData;
	private MediaMetadataRetriever retriever;
	private File handlerFile;
	private HttpAsyncTask httpTask;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
				
		cameraInstance = getCameraInstance();
		
		cameraPreview = new CameraPreview(this, cameraInstance);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);
        Log.d("DATABASE-CHECK","before datasource = new MetaDataSource(this)");
        datasource = new MetaDataSource(this);
        Log.d("DATABASE-CHECK","before datasource = new MetaDataSource(this)");
        datasource.open();
        
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(recordListener);
                
        Log.d("CameraActivity","onCreate-complete");
        
	}
	
	
	@Override
    protected void onPause() {
        
        datasource.close();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();  
        super.onPause();				// release the camera immediately on pause event
	}
	
	@Override
    protected void onStop() {
        
        datasource.close();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();
        super.onPause();				// release the camera immediately on pause event
    }

	@Override    
	protected void onDestroy() {        
	      datasource.close();
	      releaseMediaRecorder();
		  super.onDestroy();
	  }
	
	
	
	
	
    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            cameraInstance.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (cameraInstance != null){
        	cameraInstance.release();        // release the camera for other applications
        	cameraInstance = null;
        }
    }
	
	
	
	private boolean prepareVideoRecorder(Camera camera){
		Log.d("CameraActivity","in preparevideorecorder");
	    cameraInstance = camera;
	    mediaRecorder = new MediaRecorder();

	    // Step 1: Unlock and set camera to MediaRecorder
	    cameraInstance.unlock();
	    mediaRecorder.setCamera(cameraInstance);
	    Log.d("CameraActivity","in preparevideorecorder2");
	    // Step 2: Set sources
	    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	    mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
	    Log.d("CameraActivity","in preparevideorecorder3");
	    // Step 4: Set output file
	    handlerFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
	    mediaRecorder.setOutputFile(handlerFile.toString());
	    Log.d("CameraActivity","in preparevideorecorder4");
	    
	    // Step 5: Set the preview output
	    mediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());
	    

	    // Step 6: Prepare configured MediaRecorder
	    try {
	    	mediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.d("CameraActivity", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d("CameraActivity", "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    Log.d("CameraActivity","MediaRecorder-prepared");
	    return true;
	}
	
	
	
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        
	    	
	    	
	    	// Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	
	
	
	
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, we also should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this!!!!!!!!!!!!!!.
		Log.d("CameraActivity","in getOutputMediaFile1");
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
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
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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
	
	
	
	public MetaData getMetaDataFromFile(MediaMetadataRetriever retriever){
		
		MetaData data = new MetaData();
	
		
		
		data.setVideoFile(handlerFile.getName());
		data.setTimeStamp(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
		data.setDuration(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
		data.setFrameRate(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
		data.setResolution(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)+"*"+retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

		return data;
		
	}
	
	

	/*
	 * Button Listener 
	 */
	OnClickListener recordListener = new OnClickListener() {
		
		 @Override
	        public void onClick(View v) {
	            if (isRecording) {
	                // stop recording and release camera
	                mediaRecorder.stop();  // stop the recording
	                releaseMediaRecorder(); // release the MediaRecorder object
	                cameraInstance.lock();         // take camera access back from MediaRecorder

	                // inform the user that recording has stopped
	                //setCaptureButtonText("Capture");
	                isRecording = false;
	                Log.d("CameraActivity","Media recorder was already recording");
	                
	                
	                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
	                Log.d("MetaDataRetriever-CHECK",Uri.fromFile(handlerFile).toString());
	                retriever.setDataSource(CameraActivity.this, Uri.fromFile(handlerFile));
	                
	                metaData=getMetaDataFromFile(retriever);
	                datasource.insertMetaData(metaData);
	                
	                	new HttpAsyncTask(request,getString(R.string.video_upload_url), metaData, 
	                			new HttpAsyncTask.Callback() {
	    					@Override
	    					public void run(String result) {
	    						try {
	    							show_toast(result);
	    						} catch (Exception e) {
	    							show_toast("Upload of MetaData has failed");
	    						}
	    					}
	    				}
	                	).execute();
				
	                
	                
	            } else {
	            	
	                // initialize video camera
	                if (prepareVideoRecorder(cameraInstance)) {
	                    // Camera is available and unlocked, MediaRecorder is prepared,
	                    // now you can start recording
	                	Log.d("CameraActivity","Media recorder will be started in the next line");
	                    mediaRecorder.start();
	                    

	                    // inform the user that recording has started
	                    //setCaptureButtonText("Stop");
	                    isRecording = true;
	                    Log.d("CameraActivity","Media recorder started");
	                } else {
	                    // prepare didn't work, release the camera
	                    releaseMediaRecorder();
	                    Log.d("CameraActivity","prepare didn't work, release the camera");
	                    // inform user
	                }
	            }
	    }
	};
	
	private void show_toast (String s) {
    	Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
        toast.show();
	}
	
	
}
