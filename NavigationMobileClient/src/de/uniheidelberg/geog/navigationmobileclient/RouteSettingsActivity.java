package de.uniheidelberg.geog.navigationmobileclient;

import android.support.v4.app.Fragment;

public class RouteSettingsActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new RouteSettingsFragment();
	}

}
