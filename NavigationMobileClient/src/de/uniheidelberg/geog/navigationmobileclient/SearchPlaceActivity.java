package de.uniheidelberg.geog.navigationmobileclient;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.View;

public class SearchPlaceActivity extends FragmentActivity implements SearchPlaceActivityInterface {
	public static final String EXTRA_TARGET_LOCATION = "de.uniheidelberg.geog.navigationmobileclient.SearchPlaceActivity.TargetLocation";
	SearchPlaceTextFragment mSearchPlaceTextFragment;
	SearchPlaceMapFragment mSearchPlaceMapFragment;
	SearchPlaceButtonsFragment mSearchPlaceButtonsFragment;
	
	private WaypointLocation mLocation;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		// First check if a location has been passed as an extra
		if(getIntent().hasExtra(EXTRA_TARGET_LOCATION)) {
			mLocation = (WaypointLocation) getIntent().getSerializableExtra(EXTRA_TARGET_LOCATION);
		}
		
		setContentView(R.layout.activity_search_place);
		
		mSearchPlaceTextFragment = new SearchPlaceTextFragment();
		mSearchPlaceMapFragment = new SearchPlaceMapFragment();
		mSearchPlaceButtonsFragment = new SearchPlaceButtonsFragment();
		
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.search_place_text_holder, mSearchPlaceTextFragment);
		fragmentTransaction.add(R.id.search_place_map_holder, mSearchPlaceMapFragment);
		fragmentTransaction.add(R.id.search_place_buttons_holder, mSearchPlaceButtonsFragment);
		
		fragmentTransaction.commit();
	}
	
	@Override 
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return null;
		
	}

	@Override
	public void updateLocation(WaypointLocation location) {
		mLocation = location;
	}

	@Override
	public WaypointLocation getLocation() {
		return mLocation;
	}
}

