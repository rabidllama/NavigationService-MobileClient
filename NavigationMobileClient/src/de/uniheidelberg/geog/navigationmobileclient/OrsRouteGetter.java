package de.uniheidelberg.geog.navigationmobileclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrsRouteGetter extends UrlDataGetter {
	public Route parseItem(JSONObject json) {
		// parse the JSON response for a route from the navigation service
		/*
		 * 	{
		 * 		"DetermineRouteResponse": {
		 * 			"RouteGeometry": {
		 * 				"srsName":"EPSG:4326",
		 * 				"waypoints": [
		 * 					"8.676688 49.41872",
		 * 					"8.676690 49.41878",
		 * 					...
		 * 				]
		 * 			},
		 * 			"RouteSummary": {
		 * 				"BoundingBox": {
		 * 					"srsName":"EPSG:4326",
		 * 					"corners": [
		 * 						"8.676121 49.41242",
		 * 						"8.692218 49.419662"
		 * 					]
		 *				},
		 *				"TotalDistance": {
		 *					"uom":"KM",
		 *					"length":2.2
		 *				},
		 *				"TotalTime":"PT2M26S"
		 *			}
		 *		}
		 *	}
		 */
		
		Route rt = new Route();
		try {
			JSONObject root = json.getJSONObject("DetermineRouteResponse");
			
			rt.setId(root.getLong("RouteId"));
			
			// Route Summary
			JSONObject summary = root.getJSONObject("RouteSummary");
			rt.setDuration(summary.getString("TotalTime"));
			JSONObject distance = summary.getJSONObject("TotalDistance");
			rt.setLength(distance.getDouble("length"));
			rt.setUom(distance.getString("uom"));
			
			// Now store the waypoints
			JSONObject geom = root.getJSONObject("RouteGeometry");
			JSONArray waypoints = geom.getJSONArray("waypoints");
			for(int i=0; i<waypoints.length(); i++) {
				rt.addWaypoint(new Waypoint(waypoints.getString(i)));
			}
			
		} catch (JSONException jsone) {
			
		}
		
		return rt;
	}
}
