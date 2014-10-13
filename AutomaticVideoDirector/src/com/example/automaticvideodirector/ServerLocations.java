package com.example.automaticvideodirector;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class ServerLocations {
	static final String video_upload = "/video/";
	static final String video_selected = "/selected";
	static final String event_list = "/events";
	static final String event_descr = "/event/1";
	static final String video_metadata_upload = "/event/1";
	
	static String getServerAndPort(Context context) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return "http://"
        		+ sharedPref.getString(SettingsActivity.SERVER_ADDRESS, "") + ":"
        		+ sharedPref.getString(SettingsActivity.SERVER_PORT, "");
	}

	
	static String getVideoUploadUrl(Context context, int id) {
		System.out.println(getServerAndPort(context) + video_upload + Integer.toString(id));
        return getServerAndPort(context) + video_upload + Integer.toString(id);
	}
	
	static String getVideoMetadataUploadUrl(Context context) {
	    return getServerAndPort(context) + video_metadata_upload;
	}
	
	static String getSelectedListUrl(Context context) {
        return getServerAndPort(context) + video_selected;
	}
	
}
