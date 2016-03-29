package com.example.traffic.tasks;

import java.net.URL;
import java.util.ArrayList;

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
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.traffic.*;
import com.example.traffic.config.AppConfig;

public class GetTipsTask extends AsyncTask<String, JSONObject, JSONObject> {
	
	private Context sourceActivity;
	
	public GetTipsTask(Context activity) {
		this.sourceActivity = activity;
	}
	@Override
	protected JSONObject doInBackground(String ...params) {
		JSONObject errorJson = new JSONObject();
		try {
			return getTips();
		}
		catch(IOException e) {
			e.printStackTrace();
			return errorJson;
		}	
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		((SafetyActivity)sourceActivity).getTipsCallback(result);
	}
	
	protected JSONObject getTips() throws IOException {
		String jsonResponse = "";
		JSONObject responseJson = new JSONObject();
	
		try {
			String url = AppConfig.API_BASE_URL + "/tips";
			
			// Initialize the client for sending the request.
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
		
			// Set proper http headers.
			request.setHeader("Accept", "application/json");
			
			// Send the request to the server.
			HttpResponse response = client.execute(request);
			Log.i("HTTP", "Got response : " + response.getStatusLine().toString());
	
			// Get the stream of data in response.
			BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent())
			);
		
			// Convert the data in buffer to string.
			String line = "";
			while( (line = rd.readLine()) != null) {
				jsonResponse = jsonResponse +  line;
			}
			Log.i("HTTP", "Got response: " + jsonResponse);
			
			// Now convert the response to json, and check if there are any errors.
			// There will be an 'error' key present in the response json, if
			// there are any errors.
			responseJson = new JSONObject(jsonResponse); // Convert json string to JSONObject.
			if(responseJson.has("error")) {
				Log.i("HTTP", "Authentication returned error.");
				Log.i("HTTP", responseJson.getString("error"));
			}
		
		}catch(Exception e) {
			Log.i("Errorasdsa: ", e.getMessage());
			e.printStackTrace();
		}
		return responseJson;
	}
}
