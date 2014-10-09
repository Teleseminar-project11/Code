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
	private int duration;
	private String resolution;
	private int shaking;
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
	public int getDuration(){
		return this.duration;
	}
	public String getResolution(){
		return this.resolution;
	}
	public int getShaking(){
		return this.shaking;
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
	public void setDuration(int duration){
		this.duration=duration;
	}
	public void setResolution(String resolution){
		this.resolution=resolution;
	}
	public void setShaking(int shaking){
		this.shaking=shaking;
	}
	public void setStatus(String status){
		this.status=status;
	}
	public void setServerId(long serverId){
		this.serverId = serverId;
	}
	

}
