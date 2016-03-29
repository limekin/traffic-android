package com.example.traffic;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.example.traffic.tasks.RegisterUserTask;

//import android.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	/*
	 * Declare the edit texts in here, so we can initialize it in the
	 * on create method, and use them in all the methods of this class.
	 */
	private EditText nameText, usernameText, emailText,
		passwordText, confirmPasswordText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		/*
		 * Retrieve the edit boxes here.
		 * Makes it easier to get values from them later.
		 */
		nameText = (EditText) findViewById(R.id.editText1);
		usernameText = (EditText) findViewById(R.id.editText2);
		emailText = (EditText) findViewById(R.id.editText3);
		passwordText = (EditText) findViewById(R.id.editText4);
		confirmPasswordText = (EditText) findViewById(R.id.editText5);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_register, menu);
		return true;
	}
	
	// Signs the user up.
	public void signUpUser(View view) {
		
		// We use another method to store the user details on the server.
		registerUser();
	}
	
	// Handles the registration of the user.
	public void registerUser() {
		if(this.validate() == false) return;
		/*
		 *  ArrayList is a class from java.utils package.
		 *  It's basically used to hold a list of items of a 
		 *  particular type, in our case NameValuePair.
		 *  NameValuePair is a class from org.apache.
		 *  A NameValuePair is simply used to store a key-value pair.
		 *  So in here we are using an array of NameValuePairs. 
		 */
		ArrayList<NameValuePair> userDetails = new ArrayList<NameValuePair>();
		userDetails.add( 
				new BasicNameValuePair("name",
				nameText.getText().toString())
		);
		userDetails.add(
				new BasicNameValuePair("username",
				usernameText.getText().toString())
		);
		userDetails.add(
			new BasicNameValuePair("email",
			emailText.getText().toString())
		);
		userDetails.add(
			new BasicNameValuePair("password",
			passwordText.getText().toString())
		);
		new RegisterUserTask(this, userDetails).execute();
	}
	
	public Boolean validate() {
		String name = nameText.getText().toString();
		String username = usernameText.getText().toString();
		String email = emailText.getText().toString();
		String password = passwordText.getText().toString();
		String confirm = confirmPasswordText.getText().toString();
		
		// Name validation.
		if( name.isEmpty()) {
			Toast toast = Toast.makeText(
				this, 
				"Name cannot be empty.", 
				Toast.LENGTH_LONG);
				toast.show();
			return false;
		}
		if(! Pattern.matches("[a-zA-z\\s]+", name)) {
			Toast toast = Toast.makeText(
					this, 
					"Name can only contain alphabets and spaces.", 
					Toast.LENGTH_LONG);
					toast.show();
			return false;
		}
		
		// Username validation.
		if(username.isEmpty()) {
			Toast toast = Toast.makeText(
				this,
				"Username cannot be empty.",
				Toast.LENGTH_LONG
					
			);
			toast.show();
			return false;
		}
		if(! Pattern.matches("[a-zA-Z]+", username)) {
			Toast toast = Toast.makeText(
				this,
				"Username can only contain alphabets without spaces.",
				Toast.LENGTH_LONG
						
			);
			toast.show();
			return false;
		}
		
		// Email validation.
		if(email.isEmpty()) {
			Toast toast = Toast.makeText(
				this,
				"Email cannot be empty.",
				Toast.LENGTH_LONG
			);
			toast.show();
			return false;
		}
		if(! Pattern.matches("[\\w]+@(\\w)+\\.com", email))  {
			Toast toast = Toast.makeText(
				this,
				"Please enter a valid email.",
				Toast.LENGTH_LONG
			);
			toast.show();
			return false;
		}
		
		// Password validation.
		if( password.isEmpty()) {
			Toast toast = Toast.makeText(
				this,
				"Password cannot be left empty.",
				Toast.LENGTH_LONG
			);
			toast.show();
			return false;
		}
		if(! password.contentEquals(confirm)) {
			Toast toast = Toast.makeText(
				this,
				"Password doesn't match confirm password.",
				Toast.LENGTH_LONG
			);
			toast.show();
			return false;
		}
		return true;	
	}
	
	/*
	 * This method will be called by the RegisterUserTask after getting a
	 * successful response from the server (user registered).
	 */
	public void registerSuccess(String result) {
		// After registering we redirect him to the login page.
		Intent loginIntent = new Intent(this, LoginActivity.class);
		loginIntent.putExtra("flash", "Registered successfully !");
		startActivity(loginIntent);
	}
}
