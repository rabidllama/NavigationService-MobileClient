package de.uniheidelberg.geog.navigationmobileclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import de.uniheidelberg.geog.navigationmobileclient.wheelmap.WheelmapCategory;
import de.uniheidelberg.geog.navigationmobileclient.wheelmap.WheelmapCategoryGetter;

public class WheelmapCategoryFragment extends Fragment {
	ArrayList<WheelmapCategory> mItems;
	LinearLayout mList;
	LinearLayout mLoading;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_wheelmap_categories, parent, false);
		
		// Check for internet connection
		ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
		//NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
			Log.e("Connection", "No active internet connection");
			// TODO: Show an alert if the user is not connected to the internet.
		}
		
		mList = (LinearLayout)v.findViewById(R.id.wheelmap_categories_list);
		mLoading = (LinearLayout)v.findViewById(R.id.wheelmap_categories_loading);
		//setupButtons();
		
		new GetCategoriesTask().execute();
		return v;
	}
	
	void setupButtons() {
		if(getActivity() == null || mList == null) return;
		// Add a button for each category in Wheelmap
		for(final WheelmapCategory cat : mItems) {
			Button btn = new Button(getActivity());
			btn.setText(cat.getLocalisedName());
			
			btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// Load the map view with the corresponding category
					Intent i = new Intent(getActivity(), WheelmapMapActivity.class);
					i.putExtra(WheelmapMapFragment.EXTRA_CATEGORY_ID, cat.getId());
					startActivity(i);
				}
			});
			
			mList.addView(btn);
		}
		mLoading.setVisibility(View.INVISIBLE);
		mLoading.setLayoutParams(new LinearLayout.LayoutParams(0,0));
	}
	
	private class GetCategoriesTask extends AsyncTask<Void, Void, ArrayList<WheelmapCategory>> {
		@Override
		protected ArrayList<WheelmapCategory> doInBackground(Void... params) {
			ArrayList<WheelmapCategory> cats = new ArrayList<WheelmapCategory>();
			try {
				//http://wheelmap.org/api/categories?api_key=EPR2UP3TmegX7zHkhyZW&locale=en&page=2&per_page=5
				WheelmapCategoryGetter getter = new WheelmapCategoryGetter();
				String result = getter.getUrl("http://wheelmap.org/api/categories?api_key=EPR2UP3TmegX7zHkhyZW&locale=en");
				Log.d("Category", result);
				getter.parseItems(cats, result);
			} catch (IOException ioe) {
				Log.e("url", "Failed to fetch URL: ", ioe);
			} 
			
			// Sort the arraylist
			Collections.sort(cats);
			
			return cats;
		}
		
		@Override
		protected void onPostExecute(ArrayList<WheelmapCategory> items) {
			mItems = items;
			setupButtons();
		}
	}
}
