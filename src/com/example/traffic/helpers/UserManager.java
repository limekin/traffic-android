package com.example.traffic.helpers;

import com.example.traffic.R;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {
	
	private Context context;
	
	// Constructor.
	public UserManager(Context context) {
		this.context = context;
	}
	
	public int current_user_id() {
		SharedPreferences pref = context.getSharedPreferences(
			context.getString(R.string.preference_file_key), 
			Context.MODE_PRIVATE
		);
		
		String user_id = pref.getString("user_id", "none");
		if(user_id == "none") return -1;
		
		return Integer.parseInt(user_id);
	}
}
