package com.example.traffic.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.traffic.HomeActivity;
import com.example.traffic.config.AppConfig;
import com.example.traffic.fragments.RecentUploadsFragment;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

public class RetrieveVilationsTask extends AsyncTask<String, JSONObject, JSONObject> {
	
	private RecentUploadsFragment recentUploads;
	
	// Constructor. Get activity as paramters (helps in calling
	// a method in it once we get the response).
	public RetrieveVilationsTask(RecentUploadsFragment recentUploads) {
		this.recentUploads = recentUploads;
	}
	
	// Must be implemented in AsyncTask sublcass.
	public JSONObject doInBackground(String ...params) {
		//return getViolationTypes();
		try {
			return getViolationLists();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}
	
	
	public void onPostExecute(JSONObject result) {
		recentUploads.updateItems(result);
	}
	
	public JSONObject getViolationLists() throws ClientProtocolException, IOException {
		JSONObject violations = new JSONObject();
		
		String token = AppConfig.get_auth_token( recentUploads.getActivity());
		
		String url = AppConfig.API_BASE_URL + "/violations";
		url += "?token=" + token;
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		
		HttpResponse response = httpClient.execute(request);
		BufferedReader rd = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent())
		);
		
		String line, responseText = "";
		while( (line = rd.readLine()) != null) {
			responseText += line;
		}
		
		Log.i("Violations", responseText);
		try {
			violations = new JSONObject(responseText);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return violations;
	}	
}
