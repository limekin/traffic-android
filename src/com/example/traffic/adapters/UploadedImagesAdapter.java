package com.example.traffic.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.example.traffic.HomeActivity;
import com.example.traffic.R;
import com.example.traffic.helpers.Base64Converter;
import com.example.traffic.models.Violation;
import com.example.traffic.tasks.DeleteViolationTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UploadedImagesAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Violation> violations;
	private static LayoutInflater inflater;
	 
	public UploadedImagesAdapter(Context context, ArrayList<Violation> violations) {
		super();
		this.context = context;
		this.violations = violations;
		inflater = (LayoutInflater) context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE
		);
	}

	public int getCount() {
		return violations.size();
	}

	
	public Violation getItem(int position) {
		return violations.get(position);
	}

	
	public long getItemId(int position) {
		return position;
	}

	
	public View getView(int position, View arg1, ViewGroup arg2) {
		View itemView = inflater.inflate(R.layout.image_item, null);
		// Used to refer to the violation.
		final int index = position;
		// Get all the views we use to show the details of the violation.
		Violation currentViolation = this.violations.get(position);
		TextView violationType = (TextView) itemView.findViewById(R.id.textView1);
		TextView locationText = (TextView) itemView.findViewById(R.id.textView2);
		TextView description = (TextView) itemView.findViewById(R.id.textView3);
		TextView dateText = (TextView) itemView.findViewById(R.id.textView4);
		ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView1);
		TextView status = (TextView) itemView.findViewById(R.id.violation_status);
		Bitmap imageBitmap = Base64Converter.decode(
			currentViolation.image()
		);
		ImageButton imgButton = (ImageButton) itemView.findViewById(R.id.imageButton1);
		imgButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UploadedImagesAdapter.this.deleteViolation(index);
			}
			
		});
		
		// Now set all the details in the view we retreived above.
		violationType.setText( currentViolation.violation_type() );
		locationText.setText( currentViolation.location());
		description.setText( currentViolation.description());
		dateText.setText( currentViolation.date());
		imageView.setImageBitmap(imageBitmap);
		status.setText( "Track status : " + currentViolation.status());
		status.setTextColor( Color.parseColor("#306FDB"));
		
		return itemView;
	}
	
	public void deleteViolation(int index) {
		// A log message to debug.
		/*
		Log.i("Violation", "Removing violation at index : " + String.valueOf(index));
		this.violations.remove(index);
		this.notifyDataSetChanged();
		*/
		String violation_id = this.violations.get(index).id();
		new DeleteViolationTask(this, violation_id, index, this.context).execute();
	}
	
	public void deleteCallback(JSONObject response, int index) {
		if( response.has("error")) {
			Log.i("DELETE", "Could not delte the violation.");
		} else {
			this.violations.remove(index);
			this.notifyDataSetChanged();
			Toast toast = Toast.makeText(
				this.context, 
				"Violation deleted.", 
				Toast.LENGTH_LONG
			);
			toast.show();
		}
			
	}
}
