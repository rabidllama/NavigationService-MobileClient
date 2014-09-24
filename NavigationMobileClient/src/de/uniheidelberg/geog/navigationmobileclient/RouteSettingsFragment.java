package de.uniheidelberg.geog.navigationmobileclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RouteSettingsFragment extends Fragment {
	public static final String EXTRA_TARGET_LOCATION = "de.uniheidelberg.geog.navigationmobileclient.RouteSettingsFragment.target_location";
	
	public static final String LATEST_LOCATION_ACTION = "de.uniheidelberg.geog.navigationmobileclient.RouteSettingsFragment.LatestLocationAction";
	
	private WaypointLocation mTargetLocation;
	private WaypointLocation mStartLocation;
		
	private Location mLoc;
	
	private TextView mStartText;
	private TextView mTargetText;
	
	private Button mSubmit;
	private Button mFindPlace;
	
	// Receiver for the last location - requested when the activity first loads to reduce waiting time for getting a location fix
	private BroadcastReceiver mOnLastLocationReceived = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// When we receive a location, store it and update the display
			if(intent.hasExtra(LocationService.EXTRA_LOCATION)) {
				mLoc = (Location) intent.getSerializableExtra(LocationService.EXTRA_LOCATION);
				updateDisplay();
			}
		}
	};
	
	private BroadcastReceiver mOnLocationChange = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// Update the location stored based on the values from the Location Receiver
			Location recLoc = (Location) intent.getExtras().get(LocationService.EXTRA_LOCATION);
			Log.i("Settings", "Location received");
			if(recLoc != null && mLoc == null) {
				mLoc = recLoc;
				mStartLocation = new WaypointLocation(mLoc.getLatitude(), mLoc.getLongitude());
				new GetAddressTask().execute();
			}
			
			// Check if distance change is significant
			if(mLoc != null && recLoc != null) {
				boolean sigDistance = recLoc.distanceTo(mLoc) > LocationService.SIGNIFICANT_DISTANCE;
				if(sigDistance && LocationService.isBetterLocation(recLoc, mLoc)) {
					mLoc = recLoc;
					mStartLocation.setLat(mLoc.getLatitude());
					mStartLocation.setLong(mLoc.getLongitude());

					new GetAddressTask().execute();
				}				
			} 
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(LocationService.LOCATION_ACTION);
		getActivity().registerReceiver(mOnLocationChange, filter);	
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(mOnLocationChange);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(getActivity().getIntent().hasExtra(EXTRA_TARGET_LOCATION)) {
			mTargetLocation = (WaypointLocation)getActivity().getIntent().getSerializableExtra(EXTRA_TARGET_LOCATION);
		}
		
		setRetainInstance(true);
		
		// Listen for the latest location on loading
		IntentFilter filter = new IntentFilter(LATEST_LOCATION_ACTION);
		getActivity().registerReceiver(mOnLastLocationReceived, filter);
		
		// Send a request for the latest location
		Intent i = new Intent(LocationService.LATEST_LOCATION_ACTION);
		// Add the action to be performed when the location is broadcast back by the location service
		i.putExtra(LocationService.EXTRA_LATEST_LOCATION_REQUESTER, LATEST_LOCATION_ACTION);
		getActivity().sendBroadcast(i);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_route_settings, parent, false);
		
		mTargetText = (TextView) v.findViewById(R.id.routesettings_end_location);
		mStartText = (TextView) v.findViewById(R.id.routesettings_start_location);
		
		if(mTargetLocation != null) {
			// Populate information
			mTargetText.setText(mTargetLocation.getAddress());
		}
		if(mStartLocation != null) {
			mStartText.setText(mStartLocation.getAddress());
		}
		
		mSubmit = (Button) v.findViewById(R.id.routesettings_begin_button);
		mSubmit.setEnabled(false);
		mSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), RouteSelectionActivity.class);
				i.putExtra(RouteSelectionFragment.EXTRA_TARGET_LOCATION, mTargetLocation);
				i.putExtra(RouteSelectionFragment.EXTRA_START_LOCATION, mStartLocation);
				startActivity(i);	
			}
		});
		
		mFindPlace = (Button) v.findViewById(R.id.routesettings_end_button);
		mFindPlace.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Load the activity for finding a place
				Intent i = new Intent(getActivity(), SearchPlaceActivity.class);
				if(mTargetLocation != null) 
					i.putExtra(SearchPlaceActivity.EXTRA_TARGET_LOCATION, mTargetLocation);
				startActivity(i);
			}
		});
		
		return v;
	}
	
	public void updateDisplay() {
		if(mStartLocation != null)
			mStartText.setText(mStartLocation.getShortAddress());
		if(mTargetLocation != null)
			mTargetText.setText(mTargetLocation.getShortAddress());
		
		if(mStartLocation != null && mTargetLocation != null)
			mSubmit.setEnabled(true);
	}
	
	private class GetAddressTask extends AsyncTask<Void, Void, WaypointLocation> {

		@Override
		protected WaypointLocation doInBackground(Void... params) {
			AddressGetter getter = new AddressGetter();
			WaypointLocation wp = getter.searchByLocation(mLoc.getLatitude(), mLoc.getLongitude());
			return wp;
		}
		
		@Override
		protected void onPostExecute(WaypointLocation start) {
			mStartLocation = start;
			updateDisplay();
		}
		
	}
}
