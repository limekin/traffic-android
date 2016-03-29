package com.example.traffic;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class IndexActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		
		// Check if the user is already logged in,
		// in case he is logged in show his home page.
		SharedPreferences pref = this.getSharedPreferences(
			getString(R.string.preference_file_key),
			Context.MODE_PRIVATE
		);
		String user_id = pref.getString("user_id", "none");
		Log.i("INDEX", "Got user_id: " + user_id);
		
		// A valid user id is present in the file.
		if(user_id != "none") {
			Intent homeIntent = new Intent(this, HomeActivity.class);
			startActivity(homeIntent); 
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_index, menu);
		return true;
	}
	
	public void showLogin(View view) {
		// Create a new intent to start the login activity.
		Intent loginIntent = new Intent(this, LoginActivity.class);
		startActivity(loginIntent);
	}
	
	public void showRegister(View view) {
		// Create a new intent to start the regoster activity.
		Intent registerIntent = new Intent(this, RegisterActivity.class);
		startActivity(registerIntent);
	}


}
