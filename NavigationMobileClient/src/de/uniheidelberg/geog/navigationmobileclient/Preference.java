package de.uniheidelberg.geog.navigationmobileclient;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Preference {
	private String mName;
	private ArrayList<Object> mData;
	
	public Preference() {
		
	}
	
	public Preference(String name, Object value) {
		mName = name;
		mData = new ArrayList<Object>();
	}
	
	public Preference(JSONObject json) throws JSONException {
		mData = new ArrayList<Object>();
		mName = json.getString("name");
		// The values could be stored as a single value or as an array
		JSONArray data = json.getJSONArray("values");
		// go through each value in the array
		for(int i = 0; i < data.length(); i++) {
			mData.add(data.get(i));
		}
	}
	
	public Object[] getValue() {
		return mData.toArray();
	}
	
	public String getName() {
		return mName;
	}
	
	public void setValue(ArrayList<Object> newValue) {
		mData = newValue;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("name", mName);
		JSONArray array = new JSONArray();
		for(Object obj : mData) {
			array.put(obj);
		}
		json.put("value", array);
		
		return json;
	}
}
