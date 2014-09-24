package de.uniheidelberg.geog.navigationmobileclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SearchPlaceTextFragment extends Fragment {	
	public static final String ACTION_LOCATION_UPDATE = "de.uniheidelberg.geog.navigationmobileclient.SearchPlaceTextFragment.UpdateLocation";
	public static final String EXTRA_TARGET_LOCATION = "de.uniheidelberg.geog.navigationmobileclient.SearchPlaceTextFragment.TargetLocation";
	
	private EditText mSearchTextBox;
	private Button mSearchButton;
	
	WaypointLocation mLocation;
	
	SearchPlaceActivityInterface mCallback;
	
	/**
	 * Receiver for an updated location from the map interface
	 */
	private BroadcastReceiver mOnLocationUpdated = new BroadcastReceiver() {
	
		@Override
		public void onReceive(Context context, Intent intent) {
			// Update the map with the new location
			if(intent.hasExtra(EXTRA_TARGET_LOCATION)) {
				mLocation = (WaypointLocation) intent.getSerializableExtra(EXTRA_TARGET_LOCATION);
				updateSearchText();
			}			
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		// listen for an updated location
		IntentFilter filter = new IntentFilter(SearchPlaceMapFragment.ACTION_LOCATION_UPDATE);
		getActivity().registerReceiver(mOnLocationUpdated, filter);
	}
	
	@Override
	public void onPause() {
		// stop listening for location update
		getActivity().unregisterReceiver(mOnLocationUpdated);
		super.onPause();
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_search_place_text, parent, false);
		
		mSearchTextBox = (EditText) v.findViewById(R.id.search_place_text_text);
		mSearchButton = (Button) v.findViewById(R.id.search_place_text_button);
		mSearchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// perform search
				new GetAddressTask().execute();
			}
		});
		
		// Check if parent activity has a location. If so, use it
		if(mCallback.getLocation() != null) {
			mLocation = mCallback.getLocation();
			updateSearchText();
		}
				
		return v;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		//Check that the activity implements the correct interface
		try {
			mCallback = (SearchPlaceActivityInterface) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement the SearchPlaceActivityInterface");
		}
	}
	
	private void updateSearchText() {
		if(mLocation != null && mSearchTextBox != null) {
			mSearchTextBox.setText(mLocation.getAddress());
		}
	}
	
	private class GetAddressTask extends AsyncTask<Void, Void, WaypointLocation> {

		@Override
		protected WaypointLocation doInBackground(Void... params) {
			AddressGetter getter = new AddressGetter();
			WaypointLocation wp = getter.searchByAddress(mSearchTextBox.getText().toString());
			return wp;
			/*
			
			//String search = mSearchTextBox.getText().toString();
			
			// Move to a new thread
			//String nomAddr = "http://nominatim.openstreetmap.org/search/?q=";
			Builder uriAddr = new Uri.Builder();
			uriAddr.scheme("http").authority("nominatim.openstreetmap.org")
				.appendPath("search")
				.appendQueryParameter("q", mSearchTextBox.getText().toString())
				.appendQueryParameter("format", "json");
			
			UrlDataGetter getter = new UrlDataGetter();
			WaypointLocation wp = null;
			try {				
				String result = getter.getUrl(uriAddr.build().toString());
				// parse the json response
				JSONArray jsonArr = new JSONArray(result);
				JSONObject json = jsonArr.getJSONObject(0);
				String lat = json.getString("lat");
				String lon = json.getString("lon");
				String addr = json.getString("display_name");
				// parse to double
				wp = new WaypointLocation(Double.parseDouble(lat), Double.parseDouble(lon));
				wp.setAddress(addr);
				
			} catch (IOException ioe) {
				Log.e("error", ioe.getMessage());
			} catch (JSONException jsone) {
				Log.e("AddressSearch", "Error parsing JSON: " + jsone.getMessage());
			}
			
			return wp;*/
		}
		
		@Override
		protected void onPostExecute(WaypointLocation addr) {
			// Send the location to the map fragment
			Intent i = new Intent(ACTION_LOCATION_UPDATE);
			i.putExtra(SearchPlaceMapFragment.EXTRA_TARGET_LOCATION, addr);
			getActivity().sendBroadcast(i);
			mCallback.updateLocation(addr);
		}
		
	}
}
