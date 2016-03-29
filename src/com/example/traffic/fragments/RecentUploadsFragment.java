package com.example.traffic.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.traffic.R;
import com.example.traffic.adapters.UploadedImagesAdapter;
import com.example.traffic.models.Violation;
import com.example.traffic.tasks.RetrieveVilationsTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecentUploadsFragment extends Fragment {
	
	private ArrayList<Violation> violations;
	private ListView violationsList;
	private UploadedImagesAdapter listAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		violations = new ArrayList<Violation>();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_recent_uploads, null);
		Context context = getActivity().getBaseContext();
		
		// Now the section for the uploaded images.
		ListView lv = (ListView) view.findViewById(R.id.listView1);
		violationsList  = lv;
		if(lv == null)
			Log.i("LISTVIEW", "Annoying man");
		
		listAdapter = new UploadedImagesAdapter(
			context, violations
		);
		
		lv.setAdapter(listAdapter);

		// Start fetching the list of violations.
		new RetrieveVilationsTask(this).execute();
		return view;
	}
	
	
	public void updateItems(JSONObject jsonResponse) {
		JSONArray jsonViolations = new JSONArray();
		try {
			jsonViolations = jsonResponse.getJSONArray("data");
		} catch (JSONException e) {			// 
			e.printStackTrace();
		}
		
		// Hide the progress bar since we got the results.
		View view = this.getView();
		ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
		if(progressBar != null) {
			ViewGroup vg = (ViewGroup)progressBar.getParent();
			vg.removeView(progressBar);
		}
		
		if(jsonViolations.length() == 0) {
			TextView emptyMessage = new TextView(this.getActivity());
			emptyMessage.setText("You haven't reported any violations yet.");
			RelativeLayout container = (RelativeLayout) view.findViewById(R.id.violations_container);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT
			);
			params.setMargins(10, 10, 10, 10);
			emptyMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
			emptyMessage.setLayoutParams(params);
			container.addView(emptyMessage);
			
			
		}
		/*ListView lv = (ListView) view.findViewById(R.id.listView1);
		lv.setLayoutParams( 
			new LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.WRAP_CONTENT)
		);
		*/
		
		// Clear the violations.
		this.violations.clear();
		
		// Now add up all the violations again.
		for(int i=0 ; i < jsonViolations.length(); ++i ) {
			try {
				this.violations.add(
					new Violation(jsonViolations.getJSONObject(i)
				));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		this.listAdapter.notifyDataSetChanged();
	}
	
	public void deleteViolation(View view) {
		Log.i("HEY DUDE", "So cool" ); 
	}
}
