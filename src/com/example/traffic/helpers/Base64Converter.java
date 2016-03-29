package com.example.traffic.helpers;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;


/*
 * This class is used to encode and decode and image bitmap
 * to string and from string to bitmap.
 */
public class Base64Converter {
	
	/*
	 * Converts an image bitmap to base64 encoded string.
	 */
	public static String encode(Bitmap imageBitmap) {
		ByteArrayOutputStream byteStream =  new ByteArrayOutputStream();
		
		// Compress the image to png format and get the bytes
		// to the given stream.
		imageBitmap.compress(
			Bitmap.CompressFormat.PNG, 
			70, 
			byteStream
		);
		
		// Copy it to a byte array.
		byte[] content = byteStream.toByteArray();
		String encodedImage = Base64.encodeToString(content, Base64.DEFAULT);
		
		return encodedImage;
	}
	
	/*
	 * Converts back a base64 encoded image bitmap to bitmap.
	 */
	public static Bitmap decode(String encodedString) {
		byte[] content = Base64.decode(encodedString, 0);
		
		return BitmapFactory.decodeByteArray(content, 0, content.length);
	}
}
