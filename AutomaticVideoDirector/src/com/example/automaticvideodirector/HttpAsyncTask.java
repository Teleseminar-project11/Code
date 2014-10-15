package com.example.automaticvideodirector;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.automaticvideodirector.database.MetaData;

import android.os.AsyncTask;
import android.util.Log;


/**
*
* @author thilo
*/

public class HttpAsyncTask extends AsyncTask<String, Void, String> {
	
	
	public static final int HTTP_POST   = 1;
	public static final int HTTP_GET    = 2;
	public static final int HTTP_UPLOAD = 3;
	
	private static final String DEBUG_TAG = "HTTP-POST-AutomaticVideoDirector";
	
	private int request;
	private String url;
	private MetaData data;
	private Callback callback;
	
	public interface Callback {
		public void run (String result);
	}
	
	
	public HttpAsyncTask(int request, String url, MetaData data, Callback callback){
		this.request=request;
		this.data = data;
		this.url = url;
		this.callback = callback;
	}
	
	@Override
	protected String doInBackground(String... urls) {
		Log.d(DEBUG_TAG, "doInBackground first line");
		
		switch (request) {
			case HTTP_GET: {
				try {
					return httpGet(url);
				} catch (IOException e) {
					Log.d(DEBUG_TAG, e.getMessage());
					return null;
				}
			} 
			case HTTP_POST: {
				try{
					return httpPost(url, data);
				}
				catch (IOException e){
					return null;
				}
			} 
			case HTTP_UPLOAD: {
				Log.d(DEBUG_TAG, "attempting HTTP_UPLOAD");
				try {
					return httpFileUpload(url, data);
				} catch (IOException e) {
					Log.d(DEBUG_TAG, e.getMessage());
					return null;
				}
			}
		}
		return null;
	}
	
	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(String result) {
		if (result == null) {
			Log.d(DEBUG_TAG, "Error");
		} else {
			Log.d(DEBUG_TAG, "Result: "+ result);
		}
		if (callback != null) {
			callback.run(result);
		}
	}
	
	/*
	 * HTTP requests
	 */
	public String httpPost(String myurl, MetaData data) throws IOException {

		String response = "";

		URL url = new URL(myurl);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try {
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setChunkedStreamingMode(0);
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.connect();

			//Creation - JSONObject
			JSONObject jsonParam = new JSONObject();
			try {
				jsonParam.put("name", data.getVideoFile());
				jsonParam.put("finish_time", data.getTimeStamp());
				jsonParam.put("duration", data.getDuration());
				jsonParam.put("width", data.getWidth());
				jsonParam.put("height", data.getHeight());
				jsonParam.put("shaking", data.getShaking());
				Log.d(DEBUG_TAG, jsonParam.toString());
			} catch (JSONException e) {
				Log.d(DEBUG_TAG, "JSON wrong");
				e.printStackTrace();
			}

			//POST REQUEST
			OutputStream output = new BufferedOutputStream(urlConnection.getOutputStream());
			output.write(jsonParam.toString().getBytes());
			output.flush();

			//POST RESPONSE
			StringBuilder sb = new StringBuilder();
			int httpResult = urlConnection.getResponseCode();
			Log.d(DEBUG_TAG, "httpResult: " + httpResult);
			if (httpResult == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
				while ((response = br.readLine()) != null) {
					sb.append(response + "\n");
				}
				br.close();
				Log.d(DEBUG_TAG, sb.toString());
				response = sb.toString();
			} else {
				Log.d(DEBUG_TAG, urlConnection.getResponseMessage());
			}
		} finally {
			urlConnection.disconnect();
		}
		return response;
	}
	
	
	public String httpGet(String myurl) throws IOException {
		URL url = new URL(myurl);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod("GET");
		urlConnection.setDoInput(true);
		urlConnection.connect();
		int response = urlConnection.getResponseCode();
		Log.d(DEBUG_TAG, "The response is: " + response);

		InputStream in = null;
		List<String> responseBody = new ArrayList<String>();
		try {
			in = new BufferedInputStream(urlConnection.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				responseBody.add(line);
			}
			reader.close();
			return responseBody.toString();

			//			String contentAsString = readIt(in, 500);
			//			return contentAsString;
		} finally {
			if (in != null) {
				in.close();
			}
			urlConnection.disconnect();
		}
	}
	
	public String httpFileUpload(String myurl, MetaData data) throws IOException {

		URL url = new URL(myurl);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try {
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
			urlConnection.setRequestMethod("PUT");
			urlConnection.setChunkedStreamingMode(0);
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Cache-Control", "no-cache");
			urlConnection.setRequestProperty("Content-Type", "video/mpeg");
			urlConnection.connect();
			
			Log.d(DEBUG_TAG, "Url Connection setup complete");

            DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());
            
            FileInputStream fstrm = new FileInputStream(CameraActivity.getVideoDir() 
            		+ "/" + data.getVideoFile());
            // create a buffer of maximum size
            int bytesAvailable = fstrm.available();
                
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[ ] buffer = new byte[bufferSize];

            // read file and write it into stream...
            int bytesRead = fstrm.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fstrm.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fstrm.read(buffer, 0,bufferSize);
            }

            // close streams
            fstrm.close();
                
            dos.flush();

            // retrieve the response from server
            Log.d(DEBUG_TAG, Integer.toString(urlConnection.getResponseCode()));
            Log.d(DEBUG_TAG, urlConnection.getResponseMessage());
            Log.d(DEBUG_TAG, "Reading response");
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			List<String> responseBody = new ArrayList<String>();
			while ((line = reader.readLine()) != null) {
				responseBody.add(line);
			}
			reader.close();
            dos.close();
            
            Log.d(DEBUG_TAG, responseBody.toString());
            return responseBody.toString();
		} finally {
			urlConnection.disconnect();
		}
	}

//	public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
//		Reader reader = null;
//		reader = new InputStreamReader(stream, "UTF-8");        
//		char[] buffer = new char[len];
//		reader.read(buffer);
//		return new String(buffer);
//	}
}
