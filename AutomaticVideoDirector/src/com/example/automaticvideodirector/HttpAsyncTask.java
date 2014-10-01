package com.example.automaticvideodirector;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;


/**
*
* @author thilo
*/

public class HttpAsyncTask extends AsyncTask<String, Void, String> {
	
	private static final String DEBUG_TAG = "HTTP-POST-AutomaticVideoDirector";
	private String request;
	private MetaData data;
	
	
	public HttpAsyncTask(String request, MetaData data){
		this.request=request;
		this.data = data;
	}
	
	@Override
	protected String doInBackground(String... urls) {
		Log.d(DEBUG_TAG, "doInBackground first line");
		
		if(request.equals("GET")){
			//...
		}
		else if(request.equals("POST")){
			try{
				Log.d(DEBUG_TAG, "before postData is called");
				httpPost("http://localhost:8080", data);
				Log.d(DEBUG_TAG, "postData executed");
			}
			catch (IOException e){
				return "unable to send data";
			}
		}
		return null;
	}
	
	/*
	 * HTTP requests
	 */
	public String httpPost(String myurl, MetaData data) throws IOException{
		
        String response = "";
        
    	URL url = new URL(myurl);
    	Log.d(DEBUG_TAG, "before URL connection instance");
    	HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    	Log.d(DEBUG_TAG, "after URL connection instance");
    	   try {
    	    urlConnection.setDoOutput(true);
    	    urlConnection.setRequestMethod("POST");  
    	    urlConnection.setChunkedStreamingMode(0);
    	    urlConnection.setRequestProperty("Content-Type","application/json"); 
    	    
    	    //Create JSONObject
    	    JSONObject jsonParam = new JSONObject();
    	    
    	    try {
				jsonParam.put("id", data.getId());
				jsonParam.put("filename", data.getVideoFile());
        	    jsonParam.put("data", data.getSomeVideoData());
        	    Log.d(DEBUG_TAG, "JSON right");
			} catch (JSONException e) {
				Log.d(DEBUG_TAG, "JSON wrong");
				e.printStackTrace();
			}
    	    
    	    //POST request
    	    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
    	    Log.d(DEBUG_TAG, jsonParam.toString());
    	    out.write(jsonParam.toString().getBytes());
    	    out.flush();
    	    
    	    //POST response
    	    StringBuilder sb = new StringBuilder();  
    	    int httpResult = urlConnection.getResponseCode();

    	    if(httpResult == HttpURLConnection.HTTP_OK){
    	    	BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8")); 
    	        
    	        while ((response = br.readLine()) != null) {  
    	        	sb.append(response + "\n");  
    	        }  
    	        br.close();  
    	        Log.d(DEBUG_TAG, sb.toString());
    	        response = sb.toString();
    	    } 
    	    else{
    	    	Log.d(DEBUG_TAG, urlConnection.getResponseMessage());  
    	    }
    	   } finally{
    		   urlConnection.disconnect();
    	   }
    	   return response;
        }
}
