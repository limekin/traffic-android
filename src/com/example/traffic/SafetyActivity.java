package com.example.traffic;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

import com.example.traffic.adapters.SafetyAdapter;
import com.example.traffic.models.Tip;
import com.example.traffic.tasks.GetTipsTask;

public class SafetyActivity extends Activity {

	private ListView tipsList;
	private ArrayList<Tip> tips;
	private SafetyAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_safety);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		tipsList = (ListView) findViewById(R.id.listView1);
		tips = new ArrayList<Tip>();
		this.adapter = new SafetyAdapter(this, this.tips);
		this.tipsList.setAdapter(this.adapter);
		
		new GetTipsTask(this).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_safety, menu);
		return true;
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

	public void getTipsCallback(JSONObject result) {
		if(result.has("error")) return;
		
		try {
			JSONArray contens;
			contens = result.getJSONArray("data");
			if(contens.length() == 0) return;
			this.tips.clear();
			
			for(int i=0; i<contens.length(); ++i) {
				this.tips.add(new Tip(contens.getJSONObject(i)));
			}
			this.adapter.notifyDataSetChanged();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
