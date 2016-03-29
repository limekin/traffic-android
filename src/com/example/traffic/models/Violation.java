package com.example.traffic.models;

import org.json.JSONException;
import org.json.JSONObject;


// Represent a single violation.
public class Violation {
	
	private JSONObject data;
	public Violation(JSONObject violationData) {
		this.data = violationData;
	}
	
	public String violation_type() {
		try {
			return this.data.getString("violation_type");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return "none";
		}
	}
	
	public String location() {
		try {
			return this.data.getString("location");
		} catch (JSONException e) {
			e.printStackTrace();
			return "none";
		}
	}
	
	public String description() {
		try {
			return this.data.getString("description");
		} catch (JSONException e) {
			e.printStackTrace();
			return "none";
		}
	}
	
	public String image() {
		try {
			return this.data.getString("image_string");
		} catch (JSONException e) {
			e.printStackTrace();
			return "none";
		}
	}
	
	public String date() {
		try {
			return this.data.getString("reported_date");
		} catch (JSONException e) {
			e.printStackTrace();
			return "none";
		}
	}
	
	public String status() {
		try {
			return this.data.getString("status");
		} catch (JSONException e) {
			e.printStackTrace();
			return "none";
		}	
	}
	
	public String id() {
		try {
			return this.data.getString("id");
		} catch (JSONException e) {
			e.printStackTrace();
			return "none";
		}
	}
	
}
