package com.example.traffic.helpers;

import android.content.Context;
import android.widget.Toast;

public class Toaster {
	public static void showToast(Context context, String text) {
		Toast toast = Toast.makeText(
			context, 
			text,
			Toast.LENGTH_LONG
		);
		toast.show();
	}
}
