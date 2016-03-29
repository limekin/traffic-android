package com.example.traffic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.example.traffic.tasks.AddViolationTask;
import com.example.traffic.tasks.ReverseGeocodeTask;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class AddViolation extends Activity {

	// Declaring all the form fields, so that
	// we can access it easy later.
	private EditText plateNo;
	private EditText description;
	private EditText location;
	private Spinner violationType;
	private String imageUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_violation);
		
		Log.i("DEBUG", this.getIntent().getStringExtra("capturedImage") );
		
		// Set the path of the image we took.
		imageUri = this.getIntent().getStringExtra("capturedImage");
		addImage();
		
		// Now get the spinner.
		Spinner violationTypes = (Spinner) findViewById(R.id.spinner1);
		
		// Fill in the violation types we defined in the values file.
		ArrayAdapter<CharSequence> adapter  = ArrayAdapter.createFromResource(
				this,
				R.array.violation_types,
				android.R.layout.simple_spinner_item
		);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		violationTypes.setAdapter(adapter);
		
		// Now init all the form fields.
		initFields();
		
		// Get the location.
		Location currentLocation = getLocation();
		if(currentLocation != null) 
			new ReverseGeocodeTask(this, currentLocation).execute();
	}
	
	public Location getLocation() {
		LocationListener listener = new LocationListener() {
			public void onLocationChanged(Location location) {
				Log.i("LOCATION", location.toString());			
			}

			@Override
			public void onProviderDisabled(String arg0) {				
			}

			@Override
			public void onProviderEnabled(String arg0) {
			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}
		} ;
		
		// Gets the location manager.
		LocationManager locationManager = (LocationManager) this.getSystemService(
			Context.LOCATION_SERVICE
		); 
		/*
		locationManager.requestLocationUpdates(
			LocationManager.NETWORK_PROVIDER, 0, 0, listener
		);
		*/
		
		// First check it the netowkr provider is enabled on the device.
		String networkName = LocationManager.NETWORK_PROVIDER;
		if( !locationManager.isProviderEnabled(networkName))
			return null;
		return locationManager.getLastKnownLocation(
			LocationManager.NETWORK_PROVIDER
		);
	}
	
	// Retrieves and inits all the form fields used in the activity.
	public void initFields() {
		plateNo = (EditText) findViewById(R.id.editText1);
		location = (EditText) findViewById(R.id.editText2);
		violationType = (Spinner) findViewById(R.id.spinner1);
		description = (EditText) findViewById(R.id.editText3);
	}
	
	// Updates the location.
	public void updateLocation(String locality) {
		this.location.setText(locality);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add("Sample");
		menu.add("Other");
		menu.add("Testing");
		getMenuInflater().inflate(R.menu.activity_add_violation, menu);
		return true;
	}
	
	// Adds the image that has been taken from the camera,
	// to the preview image view.
	public void addImage() {
		// Find the view we want to insert the image.
		ImageView previewImage = (ImageView) findViewById(R.id.imageView1);
		
		// Now get the image path sent along with the intent.
		Uri imageUri = Uri.parse(getIntent().getStringExtra("capturedImage"));
		try {
			// First get the image we captured before.
			Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(
				getApplicationContext().getContentResolver(),
				imageUri
			);
			
			/* Then make a thumbnail out of it for preview puropses.
			* Loading up the whole image takes a lot of space in the
			* RAM/Memory.
			*/
			Bitmap previewBitmap = ThumbnailUtils.extractThumbnail(
				imageBitmap, 100, 100
			);
			previewImage.setImageBitmap(previewBitmap);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public Boolean validate() {
		String desc = description.getText().toString();
		String loc = location.getText().toString();
		String plate = plateNo.getText().toString();
		
		// Description validation.
		if( desc.isEmpty()) {
			Toast toast = Toast.makeText(
				this,
				"Please enter a description",
				Toast.LENGTH_LONG
			);
			toast.show();
			return false;
		}
		
		if( loc.isEmpty()) {
			Toast toast = Toast.makeText(
				this,
				"Please enter a location.",
				Toast.LENGTH_LONG
			);
			toast.show();
			return false;
		}
		
		return true;
	}


	// Saves the report, i.e. posts the data to the web service.
	public void saveReport(View view) {
		if(this.validate() == false) return;
		// Create a array list and add all the vlues into the 
		// list and send it to the add violation task.
		ArrayList<NameValuePair> violationData = new ArrayList<NameValuePair>();
		violationData.add(
			new BasicNameValuePair("description", description.getText().toString())
		);
		violationData.add(
			new BasicNameValuePair("location", location.getText().toString())
		);
		violationData.add(
			new BasicNameValuePair("vehicle_plate_no", plateNo.getText().toString())
		);
		violationData.add(
			new BasicNameValuePair("violation_type", (String) violationType.getSelectedItem())
		);
		violationData.add(
			new BasicNameValuePair("imageUri", this.imageUri)
		);
		
		// Now pass the data to the add violation task.
		new AddViolationTask(this, violationData).execute();
	}
	
	public void addViolationCallback() {
		Intent homeIntent = new Intent(this, HomeActivity.class);
		homeIntent.putExtra("flash", "Violation added.");
		startActivity(homeIntent);
	}
}
