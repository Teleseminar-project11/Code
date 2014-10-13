package com.example.automaticvideodirector;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {
	public static final String SERVER_ADDRESS = "server_address";
	public static final String SERVER_PORT = "server_port";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}