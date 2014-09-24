package de.uniheidelberg.geog.navigationmobileclient;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service implements LocationListener {
	//private static final String TAG = "LocationService";
	public static final String LOCATION_ACTION = "de.uniheidelberg.geog.navigationmobileclient.LocationService.BroadcastAction";
	
	public static final String LATEST_LOCATION_ACTION = "de.uniheidelberg.geog.navigationmobileclient.LocationService.LatestLocationAction";
	
	public static final String EXTRA_LATITUDE = "de.uniheidelberg.geog.navigationmobileclient.LocationService.Latitude";
	public static final String EXTRA_LONGITUDE = "de.uniheidelberg.geog.navigationmobileclient.LocationService.Longitude";
	public static final String EXTRA_PROVIDER = "de.uniheidelberg.geog.navigationmobileclient.LocationService.Provider";
	public static final String EXTRA_LOCATION = "de.uniheidelberg.geog.navigationmobileclient.LocationService.Location";
	
	public static final String EXTRA_LATEST_LOCATION_REQUESTER = "de.uniheidelberg.geog.navigationmobileclient.LocationService.LatestLocationAction.Requester";
	
	public static final float SIGNIFICANT_DISTANCE = 200;
	
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	
	public LocationManager locMgr;
	public Location prevBestLocation = null;
	
	Intent intent;
	
	@Override
	public void onCreate() {
		super.onCreate();
		intent = new Intent(LOCATION_ACTION);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i("Location", "starting service");
		locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if(locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Log.e("Location", "Network provider enabled");
			locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, this);
		}
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);	
		
		// Register for listening for the requests for an instantanious last known position
		IntentFilter filter = new IntentFilter(LATEST_LOCATION_ACTION);
		registerReceiver(mOnLocationChange, filter);
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		locMgr.removeUpdates(this);
		unregisterReceiver(mOnLocationChange);
	}
	
	public static boolean isBetterLocation(Location location, Location currentBestLocation) {
		if(currentBestLocation == null)
			return true;
		
		// Check time
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;
		
		if(isSignificantlyNewer)
			return true;
		if(isSignificantlyOlder)
			return false;
		
		// Check accuracy
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;
		
		// Check if from different providers
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());
		
		if(isMoreAccurate)
			return true;
		else if (isNewer && !isLessAccurate)
			return true;
		else if(isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
			return true;
		
		return false;
	}
	
	private static boolean isSameProvider(String provider1, String provider2) {
		if(provider1 == null) 
			return provider2 == null;
		return provider1.equals(provider2);
	}

	@Override
	public void onLocationChanged(Location location) {		
		// Broadcast that a new location is available
		Intent i = new Intent(LOCATION_ACTION);
		if(isBetterLocation(location, prevBestLocation)) {
			prevBestLocation = location;	
		} 
		i.putExtra(EXTRA_LOCATION, prevBestLocation);
		sendBroadcast(i);		// send the location to anyone that is listening for the LOCATION_ACTION
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}	
	
	// Methods for receiving a request for the latest location and broadcasting the value
	private BroadcastReceiver mOnLocationChange = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// When a request is received for the specified action (LATEST_LOCATION_ACTION)
			// The listening for this is set in the onResume method
			
			// Send the data as an intent with the action set to be the one sent as an extra
			Log.e("LocationService", "Received request for latest location");
			if(intent.hasExtra(EXTRA_LATEST_LOCATION_REQUESTER)) {
				String requester = intent.getStringExtra(EXTRA_LATEST_LOCATION_REQUESTER);
				Intent i = new Intent(requester);
				i.putExtra(EXTRA_LOCATION, prevBestLocation);
				sendBroadcast(i);
				Log.e("LocationService", "latest location sent");
			}
		}
	};		
}
