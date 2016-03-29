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
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.traffic.*;
import com.example.traffic.config.AppConfig;

public class AuthenticateUserTask extends AsyncTask<String, JSONObject, JSONObject> {
	
	private LoginActivity sourceActivity;
	private ArrayList<NameValuePair> userData;
	public AuthenticateUserTask(LoginActivity activity, ArrayList<NameValuePair> userData) {
		this.sourceActivity = activity;
		this.userData = userData;
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
		sourceActivity.loginCallback(result);
	}
	
	protected JSONObject loginUser() throws IOException {
		String jsonResponse = "";
		JSONObject responseJson = new JSONObject();
		Log.i("HTTP", "Preparing to authenticate the user ...");
		
		try {
			
			/* We have to send JSON formatted data to the server.
			* An example of how the data should look like in the format :
			* {
			* 	"data": {
			* 		"auth": {
			* 			"username": "usersname",
			* 			"password": "userspaddword"
			* 		}
			* 	}
			* }
			*/
			JSONObject root = new JSONObject();
			JSONObject auth = new JSONObject();
			JSONObject data = new JSONObject();
			
			// Now add username and password to the auth.
			auth.put("username", userData.get(0).getValue());
			auth.put("password", userData.get(1).getValue());
		
			// Now add the auth object under data object, just like in the
			// above format.
			data.put("auth", auth);
			
			// Now put data under root.
			root.put("data", data);
			
			// Now convert the json object to string, coz we are
			// sending it as the request data.
			String jsonData = root.toString();
			
			// Initialize the client for sending the request.
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(AppConfig.API_BASE_URL + "/auth/login");
			
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
