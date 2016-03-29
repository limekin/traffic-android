package com.example.traffic.config;

import com.example.traffic.R;

import android.content.Context;
import android.content.SharedPreferences;

public class AppConfig {
	// This is the base url of the web service that is running.
	public static String API_BASE_URL = "http://192.168.0.12:8081/traffic_service/index.php/api";
	//public static String API_BASE_URL = "http://10.0.2.2:8000/index.php/api";
	
	// Gets the user_id of the user.
	public static String get_auth_token(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
			context.getString(R.string.preference_file_key),
			Context.MODE_PRIVATE
		);
		String token = pref.getString("token", "none");
		return token;
	}
	
	
}
