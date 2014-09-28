package com.example.automaticvideodirector;


/**
*
* @author thilo
*/

public class Metadata {
	
	
	//This class is for testing purposes, can be substituted by a file (Video + Metadata) class later??
	
	private String phoneId;
	private String videoFile;
	private String someVideoData;
	
	public Metadata(String phoneId, String videoFile, String someVideoData){
		this.phoneId = phoneId;
		this.videoFile = videoFile;
		this.someVideoData = someVideoData;
	}
	
	
	public String getId(){
		return this.phoneId;
	}
	public String getVideoFile(){
		return this.videoFile;
	}
	public String getSomeVideoData(){
		return this.someVideoData;
	}
	
	
	
	
	
}
