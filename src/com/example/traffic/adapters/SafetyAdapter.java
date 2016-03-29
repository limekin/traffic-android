package com.example.traffic.adapters;

import java.util.ArrayList;

import com.example.traffic.models.Tip;

import com.example.traffic.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SafetyAdapter extends BaseAdapter {

	private ArrayList<Tip> tips;
	private LayoutInflater inflater;
	private Context context;
 
	
	public SafetyAdapter(Context context, ArrayList<Tip> tips) {
		this.context = context;
		this.tips = tips;
		this.inflater = (LayoutInflater) context.getSystemService(
			Context.LAYOUT_INFLATER_SERVICE
		);
	}
	@Override
	public int getCount() {
		return tips.size();
	}

	@Override
	public Object getItem(int index) {
		return tips.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View arg1, ViewGroup arg2) {
		View itemView = inflater.inflate(R.layout.single_tip, null);
		TextView contentView = (TextView) itemView.findViewById(R.id.textView1);
		contentView.setText(this.tips.get(index).content());
		
		return itemView;
	}

}
