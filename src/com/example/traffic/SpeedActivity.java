package com.example.traffic;

import java.util.Date;

import com.example.traffic.R;
import com.example.traffic.helpers.Toaster;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class SpeedActivity extends Activity {

	private LocationManager locationManager;
	private LocationListener locationListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speed);
		setTitle("Speed Track");
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		this.locationManager = (LocationManager) this.getSystemService(
			Context.LOCATION_SERVICE
		);
		
		this.locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				// Just for knowing.
				Log.i("LOCATION", "New location found : " + location.toString());
				Date currentDate = new Date();
				long seconds = currentDate.getTime() / 1000;
				
				double prevLat = Double.valueOf(SpeedActivity.this.getPrevLat());
				double prevLong = Double.valueOf(SpeedActivity.this.getPrevLong());
				long prevTime = Long.valueOf(SpeedActivity.this.getPrevTime());
				Log.i ("LOCATION", "Previous Latitude: " + String.valueOf(prevLat));
				Log.i("LOCATION", "Previous Longitude: " + String.valueOf(prevLong));
				
				// Only do the calculation if there is a prev lat and long stored.
				if(prevLat != 0.0 && prevLong != 0.0) {
					Log.i("HERE", "lOCATION DUDE");
					if(prevLat != location.getLatitude() || prevLong != location.getLongitude()) {
						Log.i("LOCATION", "New update.");
						double distance = this.getDistance(prevLong, location.getLongitude(), 
								prevLat, location.getLatitude());
						double speed = distance/((double) seconds - (double) prevTime);
						Toaster.showToast(SpeedActivity.this, String.valueOf(speed));
						speed = Math.ceil(speed);
						Log.i("SPEED", String.valueOf(speed));
						SpeedActivity.this.updateDistance(
							speed
						);
					}
				}
				
				
				SpeedActivity.this.addTime(seconds);
				SpeedActivity.this.addLat(location.getLatitude());
				SpeedActivity.this.addLong(location.getLongitude());
			}

			@Override
			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}
			
			public double getDistance(double long_one, double long_two, 
					double lat_one, double lat_two) {
				double R = 6371;
				double dLat = deg2rad(lat_two - lat_one);
				double dLong = deg2rad(long_two - long_one);
				double a = 
						Math.sin(dLat/2) * Math.sin(dLat/2) +
					    Math.cos(deg2rad(lat_one)) * Math.cos(deg2rad(lat_two)) * 
					    Math.sin(dLong/2) * Math.sin(dLong/2);
				double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
				double d = R * c * 100;
				return d;
			}
			
			public double deg2rad(double deg) {
				return deg * (Math.PI/180);
			}
		};
		
		this.startLocationUpdates();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_speed, menu);
		return true;
	}
	
	public String getPrevLat() {
		SharedPreferences pref = this.getSharedPreferences(
			getString(R.string.preference_file_key),
			Context.MODE_PRIVATE
		);
		String prevLat = pref.getString("prev_lat", "0");
		return prevLat;
	}
	
	public String getPrevLong() {
		SharedPreferences pref = this.getSharedPreferences(
			getString(R.string.preference_file_key),
			Context.MODE_PRIVATE
		);
		String prevLong = pref.getString("prev_long", "0");
		return prevLong;
	}
	
	public String getPrevTime() {
		SharedPreferences pref = this.getSharedPreferences(
			getString(R.string.preference_file_key),
			Context.MODE_PRIVATE
		);
		String prevTime = pref.getString("prev_time", "0");
		return prevTime;
	}
	
	
	
	public void addLat(double lat) {
		SharedPreferences pref = this.getSharedPreferences(
			getString(R.string.preference_file_key),
			Context.MODE_PRIVATE
		);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("prev_lat", String.valueOf(lat));
		editor.commit();
	}
	
	public void addLong(double longg) {
		SharedPreferences pref = this.getSharedPreferences(
			getString(R.string.preference_file_key),
			Context.MODE_PRIVATE
		);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("prev_long", String.valueOf(longg));
		editor.commit();
	}
	
	public void addTime(long time) {
		SharedPreferences pref = this.getSharedPreferences(
			getString(R.string.preference_file_key),
			Context.MODE_PRIVATE
		);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("prev_time", String.valueOf(time));
		editor.commit();
	}

	public void updateDistance(double distance) {
		TextView speed = (TextView) this.findViewById(R.id.textView2);
		speed.setText( String.valueOf(distance) + " m/s");
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void startLocationUpdates() {
		boolean gpsEnabled = locationManager.isProviderEnabled(
			LocationManager.GPS_PROVIDER
		);
		boolean networkEnabled = locationManager.isProviderEnabled(
			LocationManager.NETWORK_PROVIDER
		);
		if( gpsEnabled ) {
			Log.i("LOCATION", "GPS enabled.");
			locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				10000,
				0, 
				this.locationListener
			);
		} else if(networkEnabled) {
			Log.i("LOCATION", "Networkd enabled.");
			locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 
				10000, 
				0,
				this.locationListener
			);
		}
	}
	
	@Override
	public void onDestroy() {
		this.locationManager.removeUpdates(this.locationListener);
		super.onDestroy();
	}

}
