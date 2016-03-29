package com.example.traffic.tasks;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.traffic.*;
import com.example.traffic.config.AppConfig;

public class ReverseGeocodeTask extends AsyncTask<String, String, String> {
	
	private AddViolation sourceActivity;
	private Location location;
	
	public ReverseGeocodeTask(AddViolation activity, Location location) {
		this.sourceActivity = activity;
		this.location = location;
	}
	@Override
	protected String doInBackground(String ...params) {
		try {
			return reverseGeocode();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	@Override
	protected void onPostExecute(String locality) {
		sourceActivity.updateLocation(locality);
	}
	
	protected String reverseGeocode() throws IOException {
		// Instantiate the geocoder class.
		String locality = "";
		Geocoder geocoder = new Geocoder(this.sourceActivity, Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(
				location.getLatitude(), 
				location.getLongitude(), 
				1
			);
		} catch (IOException e) {
			Log.i("LOCATION", "Failed to fetch addresses.");
			e.printStackTrace();
		}
		if(addresses != null && addresses.size() > 0) {
			Address address = addresses.get(0);
			locality = address.getLocality();
		}
		Log.i("LOCATION", locality);
		return locality;
	}
}
