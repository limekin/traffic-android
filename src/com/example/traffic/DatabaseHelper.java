package com.example.traffic;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static String DATABASE_NAME = "learndb.db";
	public static int DATABASE_VERSION = 1;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create users table.
		String createTables = "CREATE TABLE users(" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"name TEXT," +
				"username TEXT," + 
				"email TEXT," + 
				"password TEXT);";
		// Create captures table.
		createTables += "CREATE TABLE violations(" + 
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"user_id INTEGER," +
				"description TEXT," + 
				"location TEXT," +
				"image_uri TEXT," + 
				"date_taken TEXT);"; 
		
		db.execSQL(createTables);
		
		// Add a record for the admin.
		ContentValues values = new ContentValues();
		values.put("name", "Kevin Jayanthan");
		values.put("username", "lime");
		values.put("email", "lime@lime.com");
		values.put("password", "pwd");
		db.insert("users", null, values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String dropQuery = "DROP TABLE IF EXISTS " + "users;";
		db.execSQL(dropQuery);
	}
	
}
