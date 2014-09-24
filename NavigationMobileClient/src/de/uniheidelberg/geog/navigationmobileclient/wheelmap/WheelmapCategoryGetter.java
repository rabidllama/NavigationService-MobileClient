package de.uniheidelberg.geog.navigationmobileclient.wheelmap;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.uniheidelberg.geog.navigationmobileclient.UrlDataGetter;
import android.util.Log;

public class WheelmapCategoryGetter extends UrlDataGetter {
	public void parseItems(ArrayList<WheelmapCategory> items, JSONObject data) throws IOException {
		// Get the categories from the API request
		// structure is:
		/*
		 * 	{"conditions":{
		 * 		"page":1,
		 * 		"per_page":200,
		 * 		"format":"json",
		 * 		"locale":"en",
		 * 		"meta":{
		 * 			"page":1,
		 * 			"num_pages":1,
		 * 			"item_count_total":12,
		 * 			"item_count":12},
		 * 		"categories":[
		 * 			{
		 * 				"id":1,
		 * 				"identifier":"public_transfer",
		 * 				"localized_name":"Public transfer"
		 * 			},
		 * 			{...}
		 * 		]
		 * 	}	
		 */
		try {
			//JSONObject meta = data.getJSONObject("meta");
			JSONArray cats = data.getJSONArray("categories");
			for(int i=0; i<cats.length(); i++) {
				// loop through the categories and add them to the items array list
				JSONObject cat = cats.getJSONObject(i);
				WheelmapCategory wmCat = new WheelmapCategory();
				wmCat.setId(cat.getInt("id"));
				wmCat.setIdentifier(cat.getString("identifier"));
				wmCat.setLocalisedName(cat.getString("localized_name"));
				items.add(wmCat);
			}
		} catch (JSONException jsone) {
			Log.e("Wheelmap", "Error parsing response", jsone);
		}
	}
	
	public void parseItems(ArrayList<WheelmapCategory> items, String json) throws IOException {
		try {
			JSONObject jsonObj = new JSONObject(json);
			parseItems(items, jsonObj);
		} catch (JSONException jsone) {
			Log.e("Wheelmap", "Error parsing response", jsone);
		}
	}
}
