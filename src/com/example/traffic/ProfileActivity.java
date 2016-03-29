package com.example.traffic;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.traffic.tasks.GetUserDetailsTask;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.HandlerThread;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ProfileActivity extends Activity {

	private TextView name, username, email;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
						
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Get the references to all the edit texts.
		name = (TextView) findViewById(R.id.textView3);
		username = (TextView) findViewById(R.id.textView5);
		email = (TextView) findViewById(R.id.textView7);
		new GetUserDetailsTask(this).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setUserData(JSONObject userData) {
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		ViewGroup parent = (ViewGroup) progressBar.getParent();
		parent.removeView(progressBar);
		
		JSONObject user;
		try {
			user = userData.getJSONObject("data");
			name.setText(user.getString("name"));
			name.setVisibility(View.VISIBLE);
			
			username.setText(user.getString("username"));
			username.setVisibility(View.VISIBLE);
			
			email.setText(user.getString("email"));
			email.setVisibility(View.VISIBLE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		 
	}

}
