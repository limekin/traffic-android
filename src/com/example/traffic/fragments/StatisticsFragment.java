package com.example.traffic.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.traffic.R;
import com.example.traffic.models.*;
import com.example.traffic.tasks.GetStatisticsTask;
import com.example.traffic.adapters.StatisticAdapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class StatisticsFragment extends Fragment {

	private StatisticAdapter adapter;
	private ArrayList<Statistic> stats;
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		this.stats = new ArrayList<Statistic>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstance) {
		View view = inflater.inflate(R.layout.fragment_statistics, null);
		Context context = getActivity().getBaseContext();
		
		// Now get the list view and set its adapter.
		ListView statList = (ListView) view.findViewById(R.id.listView1);
		this.adapter = new StatisticAdapter(context, this.stats );
		
		// Set the adapter.
		statList.setAdapter(this.adapter);
		
		// Start fetching the statistic reports.
		new GetStatisticsTask(this).execute();
		
		return view;
	}
	
	/*
	 *  Updates the statistic items with the reponse
	 *  content.
	 */
	public void updateItems(JSONObject response) {
		JSONArray statsArray = new JSONArray();
		try {
			statsArray = response.getJSONArray("data");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// Clear the current stats.
		this.stats.clear();
		for(int i=0; i<statsArray.length(); ++i) {
			try {
				this.stats.add(
					new Statistic( statsArray.getJSONObject(i))
				);
			} catch (JSONException e) {e.printStackTrace();}
		}
		
		this.adapter.notifyDataSetChanged();
	}
}
