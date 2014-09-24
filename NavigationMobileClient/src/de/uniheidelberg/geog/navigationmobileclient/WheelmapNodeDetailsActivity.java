package de.uniheidelberg.geog.navigationmobileclient;

import android.support.v4.app.Fragment;

public class WheelmapNodeDetailsActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new WheelmapNodeDetailsFragment();
	}

}
