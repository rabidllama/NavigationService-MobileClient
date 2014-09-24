package de.uniheidelberg.geog.navigationmobileclient;

import android.support.v4.app.Fragment;

public class NavigationInstructionActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new NavigationInstructionFragment();
	}

}
