package com.example.automaticvideodirector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.automaticvideodirector.database.MetaData;

public class DisplayMessageActivity extends Activity {
	private TextView mResponseTextView;
	
	private void show_toast (String s){
    	Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
        toast.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);
		
	    mResponseTextView = (TextView)findViewById(R.id.response_body);
		
		Intent intent = getIntent();
		String requestURL = intent.getStringExtra(MainActivity.REQUEST_URL);
    	
    	ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    if (networkInfo == null || !networkInfo.isConnected()) {
	    	show_toast("No network");
	    	return;
	    }
	    
		new HttpAsyncTask(HttpAsyncTask.HTTP_GET, requestURL, new MetaData(),
			new HttpAsyncTask.Callback() {
				@Override
				public void run(String result) {
					try {
	    				mResponseTextView.setText(result);
	    			} catch (Exception e) {
	    				mResponseTextView.setText("HTTP_GET failed");
	    			}
				}
			}
		).execute();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
