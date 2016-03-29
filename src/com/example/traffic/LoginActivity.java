package com.example.traffic;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.traffic.helpers.Toaster;
import com.example.traffic.tasks.*;

public class LoginActivity extends Activity {
	
	private EditText usernameField;
	private EditText passwordField;
	private SQLiteOpenHelper dbHelper;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Intent intent = this.getIntent();
        String flash;
        if( (flash = intent.getStringExtra("flash")) != null) {
        	Toaster.showToast(this, flash);
        }
        
        // Get the username and password fields.
        // Makes it easy to get their values later.
    	usernameField = (EditText) findViewById(R.id.usernameField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        
        // Check if the user is already logged in.
        Context context = getApplicationContext();;
    	SharedPreferences pref = context.getSharedPreferences(
    			getString(R.string.preference_file_key),
    			Context.MODE_PRIVATE
    	);
    	String logState = pref.getString("logged_in", "false");
    	if(logState == "true") {
    		intent = new Intent(this, HomeActivity.class);
    		startActivity(intent);
    	}
        
        // Init the database hleper in here for later reference;
        dbHelper = new DatabaseHelper(this);
    }

    
    @Override	
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    /* 
     * Gets the username and password, converts it to json data,
     * then send it to the (POST /auth/login) API of the web service.
     * Note these things are done in an Async task. This is to avoid
     * mixing in network activity with normal user interface functions.
     */
    public void loginUser() {
    	String username = usernameField.getText().toString();
    	String password = passwordField.getText().toString();
  
    	// Validation part.
    	String empty = "";
    	// Check if username is empty.
    	if(username.isEmpty()) {
    		Toast toast = Toast.makeText(
        			this,
        			"Please enter a username.",
        			Toast.LENGTH_LONG);
    		toast.show();
    		return;
    	}
    	// Check if username contains othe characters.
    	if(! Pattern.matches("[a-zA-Z]+", username)) {
    		Toast toast = Toast.makeText(
        			this,
        			"Username can only contain alphabets",
        			Toast.LENGTH_LONG);
    		toast.show();
    		return;
    	}
    	
    	// Check if password is empty.
    	if(password.isEmpty()) {
    		Toast toast = Toast.makeText(
        			this,
        			"Please enter a password.",
        			Toast.LENGTH_LONG);
    		toast.show();
    		return;
    	}

    	
    	ArrayList<NameValuePair> userData = new ArrayList<NameValuePair>();
    	userData.add(new BasicNameValuePair("username", username));
    	userData.add(new BasicNameValuePair("password", password));
    	
    	// Now send the data to the login user task to
    	// check if they are correct.
    	new AuthenticateUserTask(this, userData).execute();
    	
    }
    
    /*
     * This function will be called with the JSON response object
     * from the server after sending the login deltails to the login eend point.
     */
    public void loginCallback(JSONObject response) {
    	/*
    	 * Checks if the response we got after sending the request has any errors,
    	 * in them (i.e. login wasn't successful).
    	 * Example error response :
    	 * {
    	 * 		"error": "The username or password wasn't found in the server.
    	 * }
    	 */
    	if( response.has("error") == true) {
    		Toast toast;
			try {
				toast = Toast.makeText(getApplicationContext(),
						response.getString("error"), 
						Toast.LENGTH_SHORT 
				);
				toast.show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   	
    	} else {
    		
    		/*
    		 * Login was successful, then store the user_id and user_token sent
    		 * back with the response. So we have store them locally, so that we
    		 * can check it and skip login page next time the user uses the app.
    		 * The token from the response should be sent with subsequent requests. 
    		 * Example correct login response :
    		 * {
    		 * 		"data" {
    		 * 			"token" : "3h4k2g432442k34k2hdsfadsf",
    		 * 			"user_id": 3
    		 * 		}
    		 * }
    		 */
    		Context context = getApplicationContext();;
        	SharedPreferences pref = context.getSharedPreferences(
        			getString(R.string.preference_file_key),
        			Context.MODE_PRIVATE
        	);
        	SharedPreferences.Editor editor = pref.edit();
        	try {
        		editor.putString("user_id", 
        			response.getJSONObject("data").getString("user_id")
        		);
        		editor.putString("token", 
        			response.getJSONObject("data").getString("token")
        		);
        	} catch(JSONException e) { e.printStackTrace(); }
        	
        	editor.commit();
        	
        	// Open HomeActivity.
        	Intent homeIntent = new Intent(this, HomeActivity.class);
        	startActivity(homeIntent);
    	}
    		    	
    }
    // Try authenticating the user when he clicks the login button.
    public void authenticate(View view) {
    	
    	/* We use the web service login here.
    	*  Commented code stores the data into the local
    	*  sqlite server.
    	*/
    	loginUser();
    	/*
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	
    	String username = usernameField.getText().toString();
    	String password = passwordField.getText().toString();
    	String whereClause = "username=? AND password=?";
    	String[] whereValues = new String[] {
    			username, 
    			password
    	};
    	Cursor c = db.query(
    			"users", // Table name to query.
    			null, // Passing null returns all columns in the database.
    			whereClause, // The query condition.
    			whereValues, // The values that fills in the query condition.
    			null, // Group by, null mens no group by.
    			null, // Having, null mens no having clause.
    			null // Sort order, null meams o sort order.
    			);
    	// Get the number of rowd from the result set.
    	int rowCount = c.getCount();
    	if(rowCount == 0 ) {
    		// There is no record with the given username and password.
    		Toast toast = Toast.makeText(getApplicationContext(),
        			"Login failed, please check your credentials.", 
        			Toast.LENGTH_SHORT 
        	);
        	toast.show();
    	}
    	else {
    		// The record was found show success message.
    		Toast toast = Toast.makeText(getApplicationContext(),
        			"Login success !", 
        			Toast.LENGTH_SHORT 
        	);
        	toast.show();
        	
        	// Now get the user's id from the result, to
        	// store it in the session (pref file in our case).
        	c.moveToFirst();
        	int id_column_index = c.getColumnIndex("id");
        	int user_id = c.getInt(id_column_index);
        	Log.i("USER", "Storing user id : " + user_id);
        	// Now store the login state.
        	Context context = getApplicationContext();;
        	SharedPreferences pref = context.getSharedPreferences(
        			getString(R.string.preference_file_key),
        			Context.MODE_PRIVATE
        	);
        	SharedPreferences.Editor editor = pref.edit();
        	editor.putString("logged_in", "true");
        	editor.putString("user_id", String.valueOf(user_id));
        	editor.commit();
        	Intent homeIntent = new Intent(this, HomeActivity.class);
        	startActivity(homeIntent);
    	}
    	*/
    	
    	
    }
    
    
    // This method will be called by AuthenticateUserTask, 
    // when the user is logs in successfully.
    // The response will contain a token that we should store,
    // in order to authenticate sub sequent requests.
    public void loginSuccess(String response) {
    	try {
    		// Parse the json data and create a json object.
    		JSONObject jsonRes = new JSONObject(response);
    		JSONObject data = jsonRes.getJSONObject("data");
    		
    		// Now add the token into the shared preferences file.
    		Context context = getApplicationContext();
    		SharedPreferences sharedPref = context.getSharedPreferences(
    			"com.example.traffic.pref_file", 
    			Context.MODE_PRIVATE
    		);
    		
    		// Now get the editor to the file and add the token 
    		// recieved as a response.
    		SharedPreferences.Editor editor = sharedPref.edit();
    		editor.putString("token" , data.getString("token"));
    		editor.commit();
        	Toast toast = Toast.makeText(getApplicationContext(),
        			"Logged in.", 
        			Toast.LENGTH_SHORT 
        	);
        	toast.show();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
  	
    }
    
    public void showRegisterActivity(View view) {
    	Intent intent = new Intent(this, RegisterActivity.class);
    	startActivity(intent);
    }
}
