package de.uniheidelberg.geog.navigationmobileclient;



import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainPageFragment extends Fragment {
	
	//private BroadcastReceiver mLocationReceiver = new LocationReceiver();
	//private Location mLastLocation;
	
	private Route mRoute;
	
	private Button mContinue;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Start the GPS system
		Intent intent = new Intent(getActivity().getApplicationContext(), LocationService.class);
		getActivity().startService(intent);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main_page, parent, false);
		
		Button wheelmapButton = (Button) v.findViewById(R.id.main_poi_button);
		wheelmapButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), WheelmapCategoryActivity.class);//RouteSettingsActivity.class);
				startActivity(i);
			}
		});
		
		Button newRouteButton = (Button) v.findViewById(R.id.main_route_button);
		newRouteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), RouteSettingsActivity.class);
				startActivity(i);
			}
		});
		
		mContinue = (Button) v.findViewById(R.id.main_continue_button);
		mContinue.setEnabled(false);
		//mContinue.setBackground(getResources().getDrawable(R.drawable.return_button_disabled));
		// Try to load a route from memory
		new LoadRouteTask().execute();
		
		return v;
	}
	
	private void updateDisplay() {
		if(mRoute != null) {
			mContinue.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getActivity(), NavigationInstructionActivity.class);
					i.putExtra(NavigationInstructionFragment.EXTRA_ROUTE_ID, mRoute.getId());
					startActivity(i);
				}
			});
			mContinue.setEnabled(true);
		}
	}
	
	private class LoadRouteTask extends AsyncTask<Void, Void, Route> {

		@Override
		protected Route doInBackground(Void... params) {
			Route rt = Route.loadSavedRoute(getActivity());
			return rt;
		}
		
		@Override
		protected void onPostExecute(Route route) {
			mRoute = route;
			updateDisplay();
		}
	}
}
