package com.example.automaticvideodirector.database;


/**
*
* @author thilo
*/

public class MetaData {
	
	
	//This class is for testing purposes, can be substituted by a file (Video + Metadata) class later??
	private long _id;
	private String filename;
	private String timestamp;
	private String duration;
	private String resolution;
	private String framerate;
	private String status;
	private long serverId;
	
	
	//GETTER
	public long getId(){
		return this._id;
	}
	public String getVideoFile(){
		return this.filename;
	}
	public String getTimeStamp(){
		return this.timestamp;
	}
	public String getDuration(){
		return this.duration;
	}
	public String getResolution(){
		return this.resolution;
	}
	public String getFrameRate(){
		return this.framerate;
	}
	public String getStatus(){
		return this.status;
	}
	public long getServerId(){
		return this.serverId;
	}
	
	//SETTER
	public void setId(long id){
		this._id=id;
	}
	public void setVideoFile(String filename){
		this.filename=filename;
	}
	public void setTimeStamp(String timestamp){
		this.timestamp=timestamp;
	}
	public void setDuration(String duration){
		this.duration=duration;
	}
	public void setResolution(String resolution){
		this.resolution=resolution;
	}
	public void setFrameRate(String framerate){
		this.framerate=framerate;
	}
	public void setStatus(String status){
		this.status=status;
	}
	public void setServerId(long serverId){
		this.serverId = serverId;
	}
	

}
