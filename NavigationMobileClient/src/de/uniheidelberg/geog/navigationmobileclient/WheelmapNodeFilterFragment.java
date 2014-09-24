package de.uniheidelberg.geog.navigationmobileclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import de.uniheidelberg.geog.navigationmobileclient.wheelmap.WheelmapNodeType;
import de.uniheidelberg.geog.navigationmobileclient.wheelmap.WheelmapNodeTypeGetter;

public class WheelmapNodeFilterFragment extends Fragment {
	public static final String EXTRA_CAT_ID = "de.uniheidelberg.geog.navigationmobileclient.WheelmapNodeFilterFragment.wheelmap_category_id";
	ArrayList<WheelmapNodeType> mItems;
	LinearLayout mList;
	private int mCatId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mCatId = (int)getActivity().getIntent()
				.getIntExtra(EXTRA_CAT_ID, -1);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_wheelmap_nodes_filter, parent, false);
		
		mList = (LinearLayout) v.findViewById(R.id.wheelmap_filter_nodes_list);
		
		new GetNodeTypesTask().execute();
		
		return v;
	}
	
	protected void showButtons() {
		// add a button for each type
		if(getActivity() == null || mList == null) return;
		
		for(final WheelmapNodeType type : mItems) {
			Button btn = new Button(getActivity());
			btn.setText(type.getLocalisedName());
			
			btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getActivity(), WheelmapMapActivity.class);
					i.putExtra(WheelmapMapFragment.EXTRA_TYPE_ID, type.getId());
					startActivity(i);	
				}
			});
			
			mList.addView(btn);
		}
	}
	
	private class GetNodeTypesTask extends AsyncTask<Void, Void, ArrayList<WheelmapNodeType>> {
		@Override
		protected ArrayList<WheelmapNodeType> doInBackground(Void... params) {
			ArrayList<WheelmapNodeType> nodes = new ArrayList<WheelmapNodeType>();
			try {
				//http://wheelmap.org/api/categories?api_key=EPR2UP3TmegX7zHkhyZW&locale=en&page=2&per_page=5
				WheelmapNodeTypeGetter getter = new WheelmapNodeTypeGetter();
				String result = getter.getUrl("http://wheelmap.org/api/categories/"+mCatId+"/node_types?api_key=EPR2UP3TmegX7zHkhyZW&locale=en");
				getter.parseItems(nodes, result);
			} catch (IOException ioe) {
				Log.e("url", "Failed to fetch URL: ", ioe);
			} 
			
			// Sort the arraylist
			Collections.sort(nodes);
			
			return nodes;
		}
		
		@Override
		protected void onPostExecute(ArrayList<WheelmapNodeType> items) {
			mItems = items;
			//setupButtons();
			Log.i("Nodes", "" + mItems.size());
			showButtons();
		}
		
	}
}
