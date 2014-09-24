package de.uniheidelberg.geog.navigationmobileclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import de.uniheidelberg.geog.navigationmobileclient.RouteSelectionFragment.OnRouteObtainedListener;

public class RouteSelectionActivity extends FragmentActivity implements OnRouteObtainedListener {
	public static final String EXTRA_START_LOCATION = "de.uniheidelberg.geog.navigationmobileclient.RouteSelectionFragment.StartLocation";
	public static final String EXTRA_TARGET_LOCATION = "de.uniheidelberg.geog.navigationmobileclient.RouteSelectionFragment.TargetLocation";
	
	public static final String EXTRA_ROUTE = "de.uniheidelberg.geog.navigationmobileclient.RouteSelectionActivity.Route";
	
	private WaypointLocation mStartLoc;
	private WaypointLocation mTargetLoc;
	
	private Route mRoute;
	private GoogleMap mMap;
	
	//RouteMapFragment mRouteMapFragment;
	RouteSelectionFragment mRouteSelectionFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_preview);
		
		mRouteSelectionFragment = new RouteSelectionFragment();
		
		SupportMapFragment mapFrag = new SupportMapFragment() {
			@Override
			public void onActivityCreated(Bundle savedInstanceState) {
				super.onActivityCreated(savedInstanceState);
				mMap = this.getMap();
			}
			
		}; 
		
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		
		transaction.add(R.id.route_preview_buttons, mRouteSelectionFragment);
		transaction.add(R.id.route_preview_map, mapFrag);
		
		transaction.commit();
		
		Intent i = getIntent();
		
		if(i.hasExtra(EXTRA_START_LOCATION))
			mStartLoc = (WaypointLocation) i.getExtras().get(EXTRA_START_LOCATION);
		else
			mStartLoc = null;
		if(i.hasExtra(EXTRA_TARGET_LOCATION))
			mTargetLoc = (WaypointLocation) i.getExtras().get(EXTRA_TARGET_LOCATION);
		else
			mTargetLoc = null;
				
		mMap = mapFrag.getMap();
		//new GetRouteTask().execute();
		
	}

	@Override
	public void onRouteObtained(Route route) {
		// Update the map fragment
		mRoute = route;
		updateDisplay();
	}
	
	private void updateDisplay() {
		// Show the route on the map
		if(mRoute != null && mMap != null) {
			double maxX = Double.MIN_VALUE;
			double maxY = Double.MIN_NORMAL;
			double minX = Double.MAX_VALUE;
			double minY = Double.MAX_VALUE;
			
			PolylineOptions polyLine = new PolylineOptions().width(15).color(0x55FF0000);
			for(Waypoint wp : mRoute.getWaypoints()) {
				LatLng pt = new LatLng(wp.getLat(), wp.getLong());
				if(pt.latitude < minY)
					minY = pt.latitude;
				if(pt.latitude > maxY)
					maxY = pt.latitude;
				if(pt.longitude < minX)
					minX = pt.longitude;
				if(pt.longitude > maxX)
					maxX = pt.longitude;
				
				polyLine.add(pt);
			}
			
			mMap.addPolyline(polyLine);			
			
			// now zoom to this area
			LatLngBounds boundary = new LatLngBounds(new LatLng(minY, minX), new LatLng(maxY, maxX));
			
			mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundary, 50));
		}
	}

	/*private class GetRouteTask extends AsyncTask<Void, Void, Route> {
		@Override
		protected Route doInBackground(Void... params) {
			Route rt = new Route();
			if(mTargetLoc != null && mStartLoc != null) {
				try {
					//http://wheelmap.org/api/categories?api_key=EPR2UP3TmegX7zHkhyZW&locale=en&page=2&per_page=5
					OrsRouteGetter getter = new OrsRouteGetter();
					String url = getString(R.string.ors_url);			
					
					String apiUrl = url + "/CreateRoute";
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(apiUrl);				
					
					
					JSONObject json = new JSONObject();
					JSONObject routeRequest = new JSONObject();
					routeRequest.put("distanceUnit", "KM");
					JSONObject routePlan = new JSONObject();
					routePlan.put("RoutePreference", "Pedestrian");
					JSONObject waypointList = new JSONObject();
					JSONObject startPos = new JSONObject();
					startPos.put("srsName", "EPSG:4326");
					startPos.put("pos", mStartLoc.getLat() + " " + mStartLoc.getLong());
					JSONObject endPos = new JSONObject();
					endPos.put("srsName", "EPSG:4326");
					endPos.put("pos", mTargetLoc.getLat() + " " + mTargetLoc.getLong());
					waypointList.put("StartPosition", startPos);
					waypointList.put("EndPosition", endPos);
					routePlan.put("WayPointList", waypointList);
					routeRequest.put("RoutePlan", routePlan);
					json.put("DetermineRouteRequest", routeRequest);
					
					httpPost.setHeader("content-type", "application/json; charset=utf8");
					httpPost.setHeader("accept", "application/json");
					
					//httpPost.setHeader("Content-Length", json.toString());
					
					Log.d("url", url);
					Log.d("json in", json.toString());
					
					StringEntity se = new StringEntity(json.toString());
					httpPost.setEntity(se);
					HttpResponse response = httpClient.execute(httpPost);
					
					InputStream iStream = response.getEntity().getContent();
					
					String result = "";
					if(iStream != null) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
						String line = "";
						while((line = reader.readLine()) != null) {
							result += line;
						}
						iStream.close();
					}
					
					rt = getter.parseItem(new JSONObject(result));
					
				} catch (IOException ioe) {
					Log.e("url", "Failed to fetch URL: ", ioe);
				} catch (JSONException jsone) {
					Log.e("json", "Error with json: ", jsone);
				}
			}
			return rt;
		}
		
		@Override
		protected void onPostExecute(Route route) {
			Log.i("Route", "Obtained Route");
			mRoute = route;
			updateDisplay();
		}
	}*/
}