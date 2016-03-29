package com.example.traffic.tasks;

import com.example.traffic.*;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.traffic.config.AppConfig;
import com.example.traffic.helpers.Base64Converter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

public class AddViolationTask extends AsyncTask<String, String, String>{

	// Declare the variables we need to access later.
	private ArrayList<NameValuePair> violationData;
	private Context sourceActivity;
	
	// Constructore function get the activity and data to send.
	public AddViolationTask(Context context, ArrayList<NameValuePair> violationData) {
		this.sourceActivity = context;
		this.violationData = violationData;
	}
	@Override
	protected String doInBackground(String... params) {
		try {
			return sendViolationData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Other string";
	}
	
	private Bitmap getThumbnail(Bitmap imageBitmap) {
		return ThumbnailUtils.extractThumbnail(imageBitmap, 100, 100);
	}
	
	protected void onPostExecute(String result) {
		( (AddViolation) this.sourceActivity).addViolationCallback();
	}
	private String sendViolationData() throws IOException {
		// Get users authentication token.
		String token = AppConfig.get_auth_token( this.sourceActivity );
		
		// First create the json data to send to the server.
		JSONObject root = new JSONObject();
		JSONObject data = new JSONObject();
		JSONObject attributes = new JSONObject();
		JSONObject violation = new JSONObject();
		
		
		// Now build the object.
		try {
			int i;
			// Add first four values.
			for(i = 0; i < 4; ++i) {
				violation.put(
					violationData.get(i).getName(),
					violationData.get(i).getValue()
				);
			}
			/*
			 * Now get the bitmaps of the original image
			 * and the thumbnail of it.
			 */
			Uri imageUri = Uri.parse(violationData.get(i).getValue());
			Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(
				this.sourceActivity.getContentResolver(),
				imageUri
			);
			Bitmap thumbNail = getThumbnail(imageBitmap);
			
			/*
			 * Now add it to the json data after encoding.
			 */
			violation.put(
				"image",
				Base64Converter.encode(thumbNail)
				//Base64Converter.encode(imageBitmap)
			);
			data.put("violation", violation);
			data.put("token" , token);
			root.put("data", data);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost request = new HttpPost( 
			AppConfig.API_BASE_URL + "/violations"
		);
		
		// Add the json data to the request data.
		request.setEntity( new StringEntity(root.toString()) );
		// Set proper headers.
		request.setHeader("Content-type", "application/json");
		// Now send the request.
		HttpResponse response = httpClient.execute(request);
		
		// Now get the response.
		BufferedReader rd = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent())
		);
		
		String line, responseText = "";
		while( (line = rd.readLine()) != null) {
			responseText += line;
		}
		Log.i("RESPONSE", responseText);
		
			
		return "Success";	
	}
	
}
