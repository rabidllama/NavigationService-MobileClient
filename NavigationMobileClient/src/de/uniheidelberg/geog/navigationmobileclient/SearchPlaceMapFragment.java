package de.uniheidelberg.geog.navigationmobileclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SearchPlaceMapFragment extends SupportMapFragment {
	public static final String EXTRA_TARGET_LOCATION = "de.uniheidelberg.geog.navigationmobileclient.SearchAddressMapFragment.TargetLocation";
	public static final String ACTION_LOCATION_UPDATE = "de.uniheidelberg.geog.navigationmobileclient.SearchAddressMapFragment.UpdateLocation";
	
	private WaypointLocation mLocation;
	private Marker mMarker;
	
	SearchPlaceActivityInterface mCallback;
	
	private BroadcastReceiver mOnLocationUpdated = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// Update the map with the new location
			if(intent.hasExtra(EXTRA_TARGET_LOCATION)) {
				mLocation = (WaypointLocation) intent.getSerializableExtra(EXTRA_TARGET_LOCATION);
				updateLocation();
			}			
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		// listen for an updated location
		IntentFilter filter = new IntentFilter(SearchPlaceTextFragment.ACTION_LOCATION_UPDATE);
		getActivity().registerReceiver(mOnLocationUpdated, filter);
		// Check if parent activity has a location. If so, use it
		if(mCallback.getLocation() != null) {
			mLocation = mCallback.getLocation();
			updateLocation();
		}
	}
	
	@Override
	public void onPause() {
		// stop listening
		getActivity().unregisterReceiver(mOnLocationUpdated);
		super.onPause();
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
	
	public void updateLocation() {
		// Update the map
		if(mLocation != null) {
			GoogleMap map = this.getMap();
			// add marker to the map
			LatLng markerLoc = new LatLng(mLocation.getLat(), mLocation.getLong());
			if(mMarker == null) {
				mMarker = map.addMarker(new MarkerOptions()
						.position(markerLoc)
						.draggable(true)
						);
				map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
					
					@Override
					public void onMarkerDragStart(Marker arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onMarkerDragEnd(Marker marker) {
						// Update location
						mLocation.setLat(marker.getPosition().latitude);
						mLocation.setLong(marker.getPosition().longitude);
						
						new GetAddressTask().execute();
					}
					
					@Override
					public void onMarkerDrag(Marker arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			} else {
				mMarker.setPosition(markerLoc);
				mMarker.setDraggable(true);
			}
			// zoom to location
			map.moveCamera(CameraUpdateFactory.newLatLng(markerLoc));
			map.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
			
		}
	}
	
	private class GetAddressTask extends AsyncTask<Void, Void, WaypointLocation> {

		@Override
		protected WaypointLocation doInBackground(Void... params) {
			WaypointLocation wp;
			AddressGetter getter = new AddressGetter();
			wp = getter.searchByLocation(mLocation.getLat(), mLocation.getLong());
			
			return wp;
		}
		
		@Override
		protected void onPostExecute(WaypointLocation loc) {
			// Send the info to the search box for updating the display
			if(loc != null) {
				mLocation = loc;
				Intent i = new Intent(ACTION_LOCATION_UPDATE);
				i.putExtra(SearchPlaceTextFragment.EXTRA_TARGET_LOCATION, mLocation);
				getActivity().sendBroadcast(i);
				
				mCallback.updateLocation(loc);
			}
		}
	}
}
