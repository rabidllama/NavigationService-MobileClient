package de.uniheidelberg.geog.navigationmobileclient.wheelmap;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import de.uniheidelberg.geog.navigationmobileclient.UrlDataGetter;

public class WheelmapNodeTypeGetter extends UrlDataGetter {
	public void parseItems(ArrayList<WheelmapNodeType> items, JSONObject data) throws IOException {
		// parse the response from wheelmap api
		/*
		 * 	{ 
		 * 		"conditions": {
      				"format":"json",
      				"page":1,
      				"per_page":6,
      				"locale":"de"
    			},
  				"meta":
    			{
      				"page":1,
      				"num_pages":17,
      				"item_count":6,
      				"item_count_total":102
    			},
  				"node_types":[
    				{
      					"id":1,
      					"identifier":"veterinary",
      					"icon":"/images/icons/hospital.png",
      					"localized_name":"Tierarzt",
      					"category":{
        					"id": 6,
        					"identifier": "misc"
      					}
    				},
    				{...}
    			]
    		}
		 */
		
		try {
			//JSONObject meta = data.getJSONObject("meta");
			JSONArray nodes = data.getJSONArray("node_types");
			for(int i=0; i<nodes.length(); i++) {
				// loop through the categories and add them to the items array list
				JSONObject node = nodes.getJSONObject(i);
				WheelmapNodeType wmNodeType = new WheelmapNodeType();
				
				if(node.has("id"))
					wmNodeType.setId(node.getInt("id"));
				if(node.has("identifier"))
					wmNodeType.setIdentifier(node.getString("identifier"));
				if(node.has("icon"))
					wmNodeType.setIcon(node.getString("icon"));
				if(node.has("localized_name"))
					wmNodeType.setLocalisedName(node.getString("localized_name"));
				
				items.add(wmNodeType);
			}
		} catch (JSONException jsone) {
			Log.e("Wheelmap", "Error parsing response", jsone);
		}
	}
	
	public void parseItems(ArrayList<WheelmapNodeType> items, String json) throws IOException {
		try {
			JSONObject jsonObj = new JSONObject(json);
			parseItems(items, jsonObj);
		} catch (JSONException jsone) {
			Log.e("Wheelmap", "Error parsing response", jsone);
		}
	}
}
