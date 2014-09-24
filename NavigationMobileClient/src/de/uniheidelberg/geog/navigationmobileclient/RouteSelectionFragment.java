package de.uniheidelberg.geog.navigationmobileclient;

import java.io.IOException;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RouteSelectionFragment extends Fragment {
	public static final String EXTRA_START_LOCATION = "de.uniheidelberg.geog.navigationmobileclient.RouteSelectionFragment.StartLocation";
	public static final String EXTRA_TARGET_LOCATION = "de.uniheidelberg.geog.navigationmobileclient.RouteSelectionFragment.TargetLocation";
	
	public static final String BROADCAST_ROUTE = "de.uniheidelberg.geog.navigationmobileclient.RouteSelectionFragment.BroadcastRoute";
	
	private WaypointLocation mStartLoc;
	private WaypointLocation mTargetLoc;
	
	private Route mRoute;
	
	private TextView mText;
	private Button mSelect;
	
	private Handler mErrorhandler;
	
	OnRouteObtainedListener mCallback;
	
	public interface OnRouteObtainedListener {
		public void onRouteObtained(Route route);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// get the data from the intent
		Intent i = getActivity().getIntent();
		if(i.hasExtra(EXTRA_START_LOCATION))
			mStartLoc = (WaypointLocation) i.getExtras().get(EXTRA_START_LOCATION);
		else
			mStartLoc = null;
		if(i.hasExtra(EXTRA_TARGET_LOCATION))
			mTargetLoc = (WaypointLocation) i.getExtras().get(EXTRA_TARGET_LOCATION);
		else
			mTargetLoc = null;
		
		setRetainInstance(true);
		
		mErrorhandler = new Handler();
		new GetRouteTask().execute();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_route_selection, parent, false);
		
		mText = (TextView) v.findViewById(R.id.route_selection_text);
		mSelect = (Button) v.findViewById(R.id.route_selection_select);	
		
		return v;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Ensure that the activity has implemented the callback interface
		try {
			mCallback = (OnRouteObtainedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnRouteObtainedListener");
		}
	}
	
	private void updateDisplay() {
		if(mRoute != null && mText != null) {
			//mText.setText(mRoute.toString());
			
			
			mSelect.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// Save the route to memory
					new SaveRouteTask().execute();
					// Move to the instruction screen
					Intent i = new Intent(getActivity(), NavigationInstructionActivity.class);
					i.putExtra(NavigationInstructionFragment.EXTRA_ROUTE_ID, mRoute.getId());
					startActivity(i);
				}
			});
		}
	}
	
	private class GetRouteTask extends AsyncTask<Void, Void, Route> {
		@Override
		protected Route doInBackground(Void... params) {
			Route rt = new Route();
			if(mTargetLoc != null && mStartLoc != null) {
				try {
					//http://wheelmap.org/api/categories?api_key=EPR2UP3TmegX7zHkhyZW&locale=en&page=2&per_page=5
					OrsRouteGetter getter = new OrsRouteGetter();
					String url = getString(R.string.ors_url);			
					
					String apiUrl = url + "/CreateRoute";
					//HttpClient httpClient = new DefaultHttpClient();
					//HttpPost httpPost = new HttpPost(apiUrl);				
					
					
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
					
					//httpPost.setHeader("content-type", "application/json; charset=utf8");
					//httpPost.setHeader("accept", "application/json");
					
					//httpPost.setHeader("Content-Length", json.toString());
					
					Log.d("url", url);
					Log.d("json in", json.toString());
					
					//StringEntity se = new StringEntity(json.toString());
					//httpPost.setEntity(se);
					//HttpResponse response = httpClient.execute(httpPost);
					
					//InputStream iStream = response.getEntity().getContent();					
					
					String result = getter.getPostData(apiUrl, json.toString());
					/*if(iStream != null) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
						String line = "";
						while((line = reader.readLine()) != null) {
							result += line;
						}
						iStream.close();
					}*/
					
					rt = getter.parseItem(new JSONObject(result));
					
				} catch (IOException ioe) {
					Log.e("url", "Failed to fetch URL: ", ioe);
				} catch (JSONException jsone) {
					Log.e("json", "Error with json: ", jsone);
				} catch (HttpException httpe) {
					Log.e("RouteSelectionFragment", "Error contacting the server: " + httpe.getMessage());
					final String message = "Error contacting the server" + httpe.getMessage();
					mErrorhandler.post(new Runnable() {
						
						@Override
						public void run() {
							// Show dialog
							new AlertDialog.Builder(getActivity())
									.setTitle("Server Error")
									.setMessage(message)
									.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// end the activity
											getActivity().finish();
										}
									})
									.setIcon(android.R.drawable.ic_dialog_alert)
									.show();
						}
					});
					
					return null;
				}
			}
			return rt;
		}
		
		@Override
		protected void onPostExecute(Route route) {
			mRoute = route;
			updateDisplay();
			// Set the route in the activity
			mCallback.onRouteObtained(route);
		}
	}
	
	private class SaveRouteTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mRoute.save(getActivity());
			return null;
		}
		
	}
}
