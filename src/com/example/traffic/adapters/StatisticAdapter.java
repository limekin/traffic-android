package com.example.traffic.adapters;

import java.util.ArrayList;

import com.example.traffic.R;
import com.example.traffic.models.Statistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StatisticAdapter extends BaseAdapter {

	private ArrayList<Statistic> stats; 
	private LayoutInflater inflater;
	
	// Constructor here.
	public StatisticAdapter(Context context, ArrayList<Statistic> stats) {
		this.stats = stats;
		inflater = (LayoutInflater) context.getSystemService(
			Context.LAYOUT_INFLATER_SERVICE
		);
	}
	@Override
	public int getCount() {
		return stats.size();
	}

	@Override
	public Object getItem(int index) {
		return stats.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View view, ViewGroup arg2) {
		// Inflater the view.
		View itemView = inflater.inflate(R.layout.stat_item, null);
		
		// Now get the text views that should show the stat info.
		TextView location = (TextView) itemView.findViewById(R.id.textView1);
		TextView violationCount = (TextView) itemView.findViewById(R.id.textView2);
		TextView violationType = (TextView) itemView.findViewById(R.id.textView3);
		
		location.setText(
			this.stats.get(index).location()
		);
		
		violationCount.setText(
			"Total violations: " + this.stats.get(index).violation_count()
		);
		
		violationType.setText(
			"Most reported violation type: " + 
			this.stats.get(index).max_violation()
		);
		
		return itemView;
	}

}
