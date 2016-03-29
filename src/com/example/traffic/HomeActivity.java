package com.example.traffic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.traffic.R;

import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.traffic.adapters.*;
import com.example.traffic.helpers.Toaster;
import com.example.traffic.helpers.UserManager;

public class HomeActivity extends FragmentActivity {

	private ViewPager viewPager;
	private TabPagerAdapter pagerAdapter;
	private ActionBar actionBar;
	private File tempPicture;
	private DatabaseHelper dbHelper;
	
	// Used for capturing data form the camera.
	final int REQUEST_IMAGE_CAPTURE = 1;
	final int RESULT_OKAY = -1;
	private Uri capturedImageUri;
	
	// 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		this.setTitle("Home");
		
		Intent sourceIntent = this.getIntent();
		String flash;
		if(sourceIntent != null && (flash = sourceIntent.getStringExtra("flash")) != null) {
			Toaster.showToast(this, flash);
		}
		
		// Delete the database.
		this.deleteDatabase("learndb.db");
		// Init the db helper.
		dbHelper = new DatabaseHelper(this);
		
		// Set the temporary picture file to null.
		tempPicture = null;
		
		// Init the pager adapter. 
		// And add it as viewpager's adapter.
		// Get the reference to the activities action bar.
		// Set action bar mode to use tabs.
		pagerAdapter = new TabPagerAdapter( getSupportFragmentManager() );
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter( pagerAdapter );
		actionBar = getActionBar();
		actionBar.setNavigationMode(
			ActionBar.NAVIGATION_MODE_TABS
		);

		
		// Now create event listeners for the tabs to be created.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			
			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				viewPager.setCurrentItem(tab.getPosition());
			}
			
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
			}
		};
		
		// Now for the event listeners for view pager.
		viewPager.setOnPageChangeListener( new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		// Now create the tabs for the action bar.
		String[] tabNames = { "Report", "My reports", "Statistics" };
		for(int i=0; i<tabNames.length; ++i ) {
			actionBar.addTab(
				actionBar.newTab()
					.setText( tabNames[i] )
					.setTabListener(tabListener)
			);
		}
			
		/*
		// Get the list of images present in the directory.
		File imageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File[] images = imageDir.listFiles();
		for(int i=0; i<images.length; ++i) {
			Log.i("IMAGEQUERY", "Checking image : " + images[i].toString());
			Bitmap toAdd = BitmapFactory.decodeFile(images[i].getAbsolutePath());
			uploadedImages.add(toAdd);
		}
		lv.setAdapter(new UploadedImagesAdapter(
			this, uploadedImages
		));
		*/
		// That's pretty much it !! :D
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.item1:
				signout();
				break;
			case R.id.item2:
				showProfile();
				break;
			default:
				super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	public void getPicture(View view) {
		// Set the Uri for the new image.
		capturedImageUri = getTempFilename();
		if(capturedImageUri == null) {
			Log.i("IMAGEURI", "Image uri is null dude.");
		}
		
		// Create an intent to take the photo.
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		// Add the uri data to save the image.
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
		
		// See if there is an application that can take a picture.
		if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}
	
	//Gets the file name for new image.
	private Uri getFilename() {
		Calendar calendar = Calendar.getInstance();
		File newFile = new File( 
				this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
				"upload_" + calendar.getTimeInMillis()
		);
		try {
			newFile.createNewFile();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		Uri newFileUri = Uri.fromFile(newFile);
		return newFileUri;
	}
	
	// Gets the temporary file name.
	private Uri getTempFilename() {
		File tempFile = new File(
			this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
			"temp"
		);
		if(tempFile.exists()) {
			tempFile.delete();
		}
		try {
			tempFile.createNewFile();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		// Set the temporary file as this one !
		this.tempPicture = tempFile;
		Uri toReturn = Uri.fromFile(tempFile);
		
		return toReturn;
	}
	
	// Handles the data returned by the camera.
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OKAY) {
			
			/*Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");*/
			
			ImageView imageView = (ImageView) findViewById(R.id.imageView1);
			try {
				/*
				 * Start the add violation activity to store other details of
				 * the vilation.
				 */
				if(capturedImageUri == null) 
					Log.i("IMAGE", "The image is null dude.");
				Intent addVActivity = new Intent(this, AddViolation.class);
				addVActivity.putExtra("capturedImage", capturedImageUri.toString());
				startActivity(addVActivity);
				
				/*Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(
					getApplicationContext().getContentResolver(),
					capturedImageUri
				);
				*/
				/*
				Log.i("BITMAP", imageBitmap.toString());
				imageView.setImageBitmap(imageBitmap);
				*/
				/*
				Cursor cursor = MediaStore.Images.Thumbnails.query(
						getContentResolver(), 
						capturedImageUri, 
						null);
				if(cursor == null || cursor.getCount() == 0) {
					Log.i("THUMB", "No thumbs found !");
				} 
				Bitmap thumbMap = ThumbnailUtils.extractThumbnail(
					imageBitmap, 100, 100
				);
				imageView.setImageBitmap(thumbMap);
				
				// Get the list view and then update it's list.
				/*
				ListView lv = (ListView) findViewById(R.id.listView1);
				UploadedImagesAdapter adapter = (UploadedImagesAdapter) lv.getAdapter();
				adapter.add(thumbMap);
				*/
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Gets the bitmap of an image.
	public Bitmap getThumbnailBitmap(Uri uri){
	    String[] proj = { MediaStore.Images.Media.DATA };

	    // This method was deprecated in API level 11
	    // Cursor cursor = managedQuery(contentUri, proj, null, null, null);
	    Cursor cursor = MediaStore.Images.Media.query(
	    	getContentResolver(), uri, null
	    );	    
	    if(cursor == null) 
	    	Log.i("CURSOR", "Cursor is null !!");
	    int idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
	    cursor.moveToFirst();	    
	    long imageId = cursor.getLong(idIndex);

	    Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
	            getContentResolver(), imageId,
	            MediaStore.Images.Thumbnails.MINI_KIND,
	            null );
	    
	    return bitmap;
	}

	// Handles the saving of the captured photo.
	/*
	public void saveImage(View view) {
		
		// Get the database.
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String[] bases = this.databaseList();
		for(int i=0; i< bases.length; ++i)
			Log.i("DATABASE", bases[i]);
		
		// Get the description and location details of the image.
		// First get the container that has the info.
		EditText descriptionText = (EditText) container.findViewById(R.id.editText1);
		EditText locationText = (EditText) container.findViewById(R.id.editText2);
		String description = descriptionText.getText().toString();
		String location = locationText.getText().toString();
		
		// Get the user_id.
		UserManager currentUser = new UserManager(this);
		int user_id = currentUser.current_user_id();
		
		// Now save the information into the database.
		ContentValues values = new ContentValues();
		values.put("user_id", user_id);
		values.put("description", description);
		values.put("location", location);
		values.put("image_uri", capturedImageUri.toString());
		
		long insert_id = db.insert("violations", null, values);
		String toastText;
		if(insert_id == -1) 
			toastText = "Sorry, the information could not be saved.";
		else
			toastText = "Violation information saved successfully.";
		Toast toast = Toast.makeText(
				getApplicationContext(), 
				toastText, 
				Toast.LENGTH_SHORT
		);
		
	}
	*/
	
	/* ========================================
	 * Menu item select handlers in here.
	 * ========================================
	 */
	// Signs out the user.
	public void signout() {
		// Get the shared preferences file and
		// remove the user_id and logged in status.
		SharedPreferences pref = this.getSharedPreferences(
			getString(R.string.preference_file_key),
			Context.MODE_PRIVATE
		);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
		
		// Now redirect the user to the index page.
		Intent indexIntent = new Intent(this, IndexActivity.class);
		startActivity(indexIntent);
	}
	
	// Shows the profile of the user.
	public void showProfile() {
		Intent profileIntent = new Intent(this, ProfileActivity.class);
		startActivity(profileIntent);
	}
	
	// Shows the speed tracker.
	public void showSpeedTracker(View view) {
		Intent speedActivity = new Intent(this, SpeedActivity.class);
		startActivity(speedActivity);
	}
	
	// Shows the traffic safety tips.
	public void showTips(View view) {
		Intent tipsIntent = new Intent(this, SafetyActivity.class);
		startActivity(tipsIntent);
	}
	
}
