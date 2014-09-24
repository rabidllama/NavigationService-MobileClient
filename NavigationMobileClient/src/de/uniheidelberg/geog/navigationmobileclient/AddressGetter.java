package de.uniheidelberg.geog.navigationmobileclient;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

public class AddressGetter extends UrlDataGetter {
	public WaypointLocation parse(JSONObject json) {
		WaypointLocation wp = null;
		try {
			String lat = json.getString("lat");
			String lon = json.getString("lon");
			String addr = json.getString("display_name");
			// parse to double
			wp = new WaypointLocation(Double.parseDouble(lat), Double.parseDouble(lon));
			wp.setAddress(addr);
		} catch (JSONException jsone) {
			Log.e("AdressGetter", "Error parsing JSON: " + jsone.getLocalizedMessage());
		}
		return wp;
	}
	
	public WaypointLocation searchByAddress(String addr) {
		WaypointLocation wp = null;
		
		Builder uriAddr = new Uri.Builder();
		uriAddr.scheme("http").authority("nominatim.openstreetmap.org")
			.appendPath("search")
			.appendQueryParameter("q", addr)
			.appendQueryParameter("addressdetails", "0")
			.appendQueryParameter("format", "json");
		
		try {
			String result = getUrl(uriAddr.build().toString());
			JSONArray data = new JSONArray(result);
			wp = parse(data.getJSONObject(0));
			
		} catch (IOException ioe) {
			Log.e("AddressGetter", "Error getting data: " + ioe.getLocalizedMessage());
		} catch (JSONException jsone) {
			Log.e("AdressGetter", "Error parsing JSON: " + jsone.getLocalizedMessage());
		}
		
		
		return wp;
	}
	
	public WaypointLocation searchByLocation(double lat, double lng) {
		WaypointLocation wp = null;
		
		Builder uriAddr = new Uri.Builder();
		uriAddr.scheme("http").authority("nominatim.openstreetmap.org")
			.appendPath("reverse")
			.appendQueryParameter("lat", Double.toString(lat))
			.appendQueryParameter("lon", Double.toString(lng))
			.appendQueryParameter("addressdetails", "0")
			.appendQueryParameter("format", "json");
		
		try {
			String result = getUrl(uriAddr.build().toString());
			JSONObject data = new JSONObject(result);
			wp = parse(data);
		} catch (IOException ioe) {
			Log.e("AddressGetter", "Error getting data: " + ioe.getLocalizedMessage());
		} catch (JSONException jsone) {
			Log.e("AdressGetter", "Error parsing JSON: " + jsone.getLocalizedMessage());
		}
		
		return wp;
	}
	
}
