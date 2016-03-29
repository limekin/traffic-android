package com.example.traffic.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Statistic {
	
	private JSONObject data;
	public Statistic(JSONObject stat) {
		this.data = stat;
	}
	
	public String location() {
		try {
			return this.data.getString("location");
		} catch (JSONException e) {
			e.printStackTrace();
			return "none";
		}		
	}
	
	public String violation_count() {
		try {
			return this.data.getString("violation_count");
		} catch (JSONException e) {
			e.printStackTrace();
			return "none";
		}
	}
	
	public String max_violation() {
		try {
			return this.data.getString("max_type");
		} catch (JSONException e) {
			e.printStackTrace();
			return "none";
		}
	}
	
}
