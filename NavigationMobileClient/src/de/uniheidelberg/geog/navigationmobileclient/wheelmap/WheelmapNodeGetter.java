package de.uniheidelberg.geog.navigationmobileclient.wheelmap;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.uniheidelberg.geog.navigationmobileclient.UrlDataGetter;
import android.util.Log;

public class WheelmapNodeGetter extends UrlDataGetter {
	public void parseItems(ArrayList<WheelmapNode> items, JSONObject data) throws IOException {
		// Parse the response of the api request for nodes of a particular category
		/*
		 * { 
		 * 		"conditions": {
					"format":"json",
					"page":1,
					"per_page":2,
					"bbox":"13.3417453687,52.505599556059,13.434635631302,52.523390178425"
				},
				"meta": {
					"page":1,
					"num_pages":533,
					"item_count":2,
					"item_count_total":1066
    			},
    			"nodes":[
    				{
      					"id":19,
      					"lat":52.5231376,
      					"lon":13.3595838,
      					"node_type":{
        					"id":24,
        					"identifier":"museum"
	      				},      
	      				"category":{
	        				"id":2,
	        				"identifier":"tourism"
	      				}
	      				"wheelchair":"yes",
	      				"wheelchair_description":null,
	      				"street":"Unter den Linden",
	      				"housenumber":"1",
	      				"city":"Berlin",
	      				"postcode":"12345",
	      				"website":"http://some-other-url.com",
	      				"phone":"+49 30 456456523"
    				},
    				{...}
    			]
    		}
		 */
		try {
			//JSONObject meta = data.getJSONObject("meta");
			JSONArray nodes = data.getJSONArray("nodes");
			for(int i=0; i<nodes.length(); i++) {
				// loop through the categories and add them to the items array list
				JSONObject node = nodes.getJSONObject(i);
				WheelmapNode wmNode = new WheelmapNode();
				
				if(node.has("id")) 
					wmNode.setId(node.getInt("id"));
				if(node.has("lat")) 
					wmNode.setLat(node.getDouble("lat"));
				if(node.has("lon")) 
					wmNode.setLong(node.getDouble("lon"));
				if(node.has("wheelchair")) {
					String wheelchair = node.getString("wheelchair");
					
					if(wheelchair.equals("yes")) wmNode.setWheelchair(WheelmapNode.WheelchairAccessible.YES);
					else if (wheelchair.equals("no")) wmNode.setWheelchair(WheelmapNode.WheelchairAccessible.NO);
					else if (wheelchair.equals("limited")) wmNode.setWheelchair(WheelmapNode.WheelchairAccessible.LIMITED);
					else if (wheelchair.equals("unknown")) wmNode.setWheelchair(WheelmapNode.WheelchairAccessible.UNKNOWN);
				}
				if(node.has("name")) 
					wmNode.setName(node.getString("name"));
				if(node.has("wheelchair_description")) 
					wmNode.setWheelchairDesc(node.getString("wheelchair_description"));
				if(node.has("street")) 
					wmNode.setStreet(node.getString("street"));
				if(node.has("housenumber")) 
					wmNode.setHouseNumber(node.getString("housenumber"));
				if(node.has("city")) 
					wmNode.setCity(node.getString("city"));
				if(node.has("postcode")) 
					wmNode.setPostcode(node.getString("postcode"));
				if(node.has("website")) 
					wmNode.setWebsite(node.getString("website"));
				if(node.has("phone")) 
					wmNode.setPhone(node.getString("phone"));				
				
				items.add(wmNode);
			}
		} catch (JSONException jsone) {
			Log.e("Wheelmap", "Error parsing response", jsone);
		}
	}
	
	public void parseItems(ArrayList<WheelmapNode> items, String json) throws IOException {
		try {
			JSONObject jsonObj = new JSONObject(json);
			parseItems(items, jsonObj);
		} catch (JSONException jsone) {
			Log.e("Wheelmap", "Error parsing response", jsone);
		}
	}
}
