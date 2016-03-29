package com.example.traffic.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.traffic.*;
import com.example.traffic.config.AppConfig;

public class RegisterUserTask extends AsyncTask<String, Void, String>{
	
	// Used to store the activity from which this task has been called.
	private Context context;
	private ArrayList<NameValuePair> list;
	
	public RegisterUserTask(Context context, ArrayList<NameValuePair> list) {
		this.context = context;
		this.list = list;
	}
	
	protected String doInBackground(String... params) {
		// Send username and password the register function.
		try {
			return registerUser();
		} // Catch IO Exception.
		catch(IOException e) {
			return "Couldn't not register user.";
		}
	}
	
	protected void onPostExecute(String result) {
		((RegisterActivity) this.context).registerSuccess(result);
	}
	
	private String registerUser () throws IOException {
		
		try {
			JSONObject root = new JSONObject();
			JSONObject data = new JSONObject();
			JSONObject attributes = new JSONObject();
			
			// Now add all the fields of the user details,
			// to the attributes json object.
			for(int i = 0; i < list.size(); ++i) {
				attributes.put(list.get(i).getName(), list.get(i).getValue());
			}
			// Now build the proper json structure.
			data.put("attributes", attributes);
			data.put("type", "users");
			root.put("data", data);
			
			Log.i("HTTP", "Registering the user ...");
			HttpClient client = new DefaultHttpClient();
			HttpPost postReq = new HttpPost(AppConfig.API_BASE_URL + "/users");
			
			// Add the json data as the request paramter.
			postReq.setEntity( new StringEntity(root.toString()));
			
			/*
			 * Now set the headers.
			 * Accept: application/json means that we want the reponse to be in the json format.
			 * Content-type: application/json means that the request we are sending to the 
			 * server is in json format.
			 */
			postReq.setHeader("Accept", "application/json");
			postReq.setHeader("Content-type", "application/json");
			
			// Send the request and get back the response.
			HttpResponse response = client.execute(postReq);
			
			// Now get the response.
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent())
			);
			
			String responseText = "", line;
			while( (line = rd.readLine()) != null) {
				responseText += line;
			}
			
				
			// Output the response header.
			Log.i("HTTP", "Status from /api/users [POST]: " +
					response.getStatusLine().toString());
			
			// Output the respnse text.
			Log.i("HTTP", "Response from /api/users [POST]: " +
					responseText);
			
		// Catch the http exception if any.
		} catch(Exception e) {
			Log.e("HTTP", "Error in http connection :" + e.toString());
			e.printStackTrace();
		}
		
		return "Done";
	}
}
