package de.uniheidelberg.geog.navigationmobileclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SearchPlaceButtonsFragment extends Fragment {
	
	SearchPlaceActivityInterface mCallback;
	
	Button mSubmit;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle onSavedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_search_place_buttons, parent, false);
		mSubmit = (Button) v.findViewById(R.id.search_place_buttons_select);
		mSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Submit the selected location
				// TODO: Need to add a check to make sure that a location has been selected
				WaypointLocation wp = mCallback.getLocation();
				if(wp != null) {
					// Submit the location back to the settings page
					Intent i = new Intent(getActivity(), RouteSettingsActivity.class);
					i.putExtra(RouteSettingsFragment.EXTRA_TARGET_LOCATION, wp);
					getActivity().startActivity(i);
				}
			}
		});
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
}
