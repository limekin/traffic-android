package com.example.traffic.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Tip {

	private JSONObject data;
	
	public Tip(JSONObject tipData) {
		this.data = tipData;
	}
	
	public String content() {
		try {
			return this.data.getString("content");
		} catch (JSONException e) {
			e.printStackTrace();
			return "none";
		}
	}
}
