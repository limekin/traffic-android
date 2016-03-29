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
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.traffic.*;
import com.example.traffic.adapters.UploadedImagesAdapter;
import com.example.traffic.config.AppConfig;

public class DeleteViolationTask extends AsyncTask<String, JSONObject, JSONObject> {
	
	private UploadedImagesAdapter sourceActivity;
	private String violation_id;
	private Context context;
	private int index;
	public DeleteViolationTask(UploadedImagesAdapter adapter, String violation_id,
			int index, Context context) {
		this.sourceActivity = adapter;
		this.violation_id = violation_id;
		this.index = index;
		this.context = context;
	}
	@Override
	protected JSONObject doInBackground(String ...params) {
		JSONObject errorJson = new JSONObject();
		try {
			return loginUser();
		}
		catch(IOException e) {
			e.printStackTrace();
			return errorJson;
		}	
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		sourceActivity.deleteCallback(result, this.index);
	}
	
	protected JSONObject loginUser() throws IOException {
		String jsonResponse = "";
		String token = AppConfig.get_auth_token(this.context);
	
		JSONObject responseJson = new JSONObject();
		Log.i("HTTP", "Preparing to delete the violation ...");
		
		try {
			JSONObject root = new JSONObject();
			JSONObject data = new JSONObject();
		
			data.put("token", token);
			data.put("id", this.violation_id);
			
			root.put("data", data);
			
			// Now convert the json object to string, coz we are
			// sending it as the request data.
			String jsonData = root.toString();
			
			// Initialize the client for sending the request.
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(AppConfig.API_BASE_URL + "/violations/delete");
			
			// Set the json formatted data as request's entity (paramter).
			request.setEntity(new StringEntity(jsonData));
			// Set proper http headers.
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/json");
			
			
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
		
		} catch(HttpHostConnectException exception){
			try {
				responseJson.put("error", "Could not connect to the server, make sure that the server IP is correct.");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		catch(Exception e) {
			Log.i("Errorasdsa: ", e.getMessage());
			e.printStackTrace();
		}
		return responseJson;
	}
}
