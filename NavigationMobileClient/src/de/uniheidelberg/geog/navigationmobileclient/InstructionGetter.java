package de.uniheidelberg.geog.navigationmobileclient;

import java.io.IOException;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class InstructionGetter extends UrlDataGetter {
	private Context mContext;
	
	public InstructionGetter(Context context) {
		super();
		mContext = context;
	}
	
	public Instruction parse(JSONObject json) {
		// parse the instruction from JSON to 
		/*
		 * 	{
		 * 		"InstructionResponse": {
		 * 			"InstructionContent":{
		 * 				"Element":"|forward 0.56637573|||||continue",
		 * 				"Location": {
		 * 					"Accuracy":0,
		 * 					"Pos":"8.676126 49.419476"
		 * 				}
		 * 			}
		 * 			"Location": {
		 * 				"Accuracy":1.2400000095367432,
		 * 				"Pos":"8.64519 49.42297"
		 * 			}
		 * 		}
		 * 	}
		 */
		
		Instruction inst = new Instruction();
		try {
			JSONObject root = json.getJSONObject("InstructionResponse");
			JSONObject content = root.getJSONObject("InstructionContent");
			String element = content.getString("Element");
			long wpId = content.getLong("WaypointId");
			JSONObject loc = content.getJSONObject("Location");
			Waypoint wp = new Waypoint(loc.getString("Pos"));
			
			JSONObject userLoc = root.getJSONObject("Location");
			Waypoint userWp = new Waypoint(userLoc.getString("Pos"));
			
			inst.setInstruction(element);
			inst.setWaypointId(wpId);
			inst.setInstructionLocation(wp);
			inst.setUserLocation(userWp);			
			
		} catch (JSONException jsone) {
			Log.e("InstructionGetter", "Error reading JSON response: " + json.toString());
		}
		
		return inst;
	}
	
	public Instruction getInstruction(long routeId, long lastWpId, Location userLoc) {
		Instruction inst = null;
		try {
			JSONObject root = new JSONObject();
			JSONObject req = new JSONObject();
			req.put("RouteId", routeId);
			req.put("LastWaypointId", lastWpId);
			JSONObject loc = new JSONObject();
			loc.put("Pos", userLoc.getLatitude() + " " + userLoc.getLongitude());
			loc.put("Accuracy", userLoc.getAccuracy());
			req.put("Location", loc);
			root.put("InstructionRequest", req);
			
			String url = mContext.getString( R.string.ors_url) + "/GetInstruction";
			
			String resp = getPostData(url, root.toString());
			
			inst = parse(new JSONObject(resp));
		} catch (JSONException jsone) {
			Log.e("InstructionFragment", "Error construction JSON request: " + jsone);
		} catch (IOException ioe) {
			Log.e("InstructionFragment", "Error reading data from navigation service: " + ioe);
		} catch (HttpException httpe) {
			Log.e("InstructionFragment", "Error getting route from server: " + httpe.getMessage());
			// TODO: Display message to user
		}
		
		return inst;
	}
}