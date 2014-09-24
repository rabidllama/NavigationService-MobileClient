package de.uniheidelberg.geog.navigationmobileclient;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import de.uniheidelberg.geog.navigationmobileclient.wheelmap.WheelmapNode;
import de.uniheidelberg.geog.navigationmobileclient.wheelmap.WheelmapNodeGetter;

public class WheelmapMapFragment extends Fragment {
	public static final String EXTRA_CATEGORY_ID = "de.uniheidelberg.geog.navigationmobileclient.WheelmapMapFragment.wheelmap_category_id";
	public static final String EXTRA_TYPE_ID = "de.uniheidelberg.geog.navigationmobileclient.WheelmapMapFragment.wheelmap_type_id";
	
	private int mCatId;
	private int mTypeId;
	private ArrayList<WheelmapNode> mItems;
	
	//private LinearLayout mNodeList;
	private ListView mListView;
	private WheelmapListAdapter mListAdapter;
	private LinearLayout mLoading;
	
	private Location mLoc;
	
	private BroadcastReceiver mOnLocationChange = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// Update the location stored based on the values from the Location Receiver
			Location recLoc = (Location) intent.getExtras().get(LocationService.EXTRA_LOCATION);
			
			if(recLoc != null && mLoc == null) {
				mLoc = recLoc;
				new GetNodesTask().execute();
			}
			// Trigger an event to refresh the list of place
			// Check if distance change is significant
			if(mLoc != null && recLoc != null) {
				boolean sigDistance = recLoc.distanceTo(mLoc) > LocationService.SIGNIFICANT_DISTANCE;
				if(sigDistance && LocationService.isBetterLocation(recLoc, mLoc)) {
					mLoc = recLoc;
					Log.i("Location", "Causing refresh");
					new GetNodesTask().execute();
				}
			} 
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(LocationService.LOCATION_ACTION);
		getActivity().registerReceiver(mOnLocationChange, filter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(mOnLocationChange);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().startService(new Intent(getActivity(), LocationService.class));
		
		// Limit to a particular category or node type
		mCatId = (int)getActivity().getIntent()
				.getIntExtra(EXTRA_CATEGORY_ID, -1);
		mTypeId = (int)getActivity().getIntent()
				.getIntExtra(EXTRA_TYPE_ID, -1);
				
		mItems = new ArrayList<WheelmapNode>();
		new GetNodesTask().execute();		
		
		setRetainInstance(true);		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_wheelmap_map, parent, false);
		
		WheelmapNode[] nodeArr = new WheelmapNode[mItems.size()];
		for(int i=0; i<mItems.size(); i++) {
			nodeArr[i] = mItems.get(i);
		}
		mListAdapter = new WheelmapListAdapter(getActivity(), R.layout.wheelmap_node_list_item, nodeArr);

		mListView = (ListView) v.findViewById(R.id.wheelmap_map_node_list2);
		mListView.setAdapter(mListAdapter);
		
		mListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
				WheelmapNode node = (WheelmapNode) adapter.getItemAtPosition(position);
				
				Intent i = new Intent(getActivity(), WheelmapNodeDetailsActivity.class);
				i.putExtra(WheelmapNodeDetailsFragment.EXTRA_NODE, node);
				startActivity(i);
			}
		});
		
		Button filter = (Button)v.findViewById(R.id.wheelmap_nodes_filter_button);
		filter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// open the filter window
				Intent i = new Intent(getActivity(), WheelmapNodeFilterActivity.class);
				i.putExtra(WheelmapNodeFilterFragment.EXTRA_CAT_ID, mCatId);
				startActivity(i);				
			}
		});
		
		mLoading = (LinearLayout) v.findViewById(R.id.wheelmap_map_loading);
		
		return v;
	}
	
	protected void showNodes() {
		if(mLoc == null || getActivity() == null || mListView == null) return;

		// Update the list view
		WheelmapNode[] nodeArr = new WheelmapNode[mItems.size()];
		for(int i=0; i<mItems.size(); i++) {
			nodeArr[i] = mItems.get(i);
		}
		// Need to sort based on distance from user
		boolean sorted = false;
		while(!sorted) {
			int currPos = 1;
			sorted = true;
			while(currPos < mItems.size()) {
				WheelmapNode prev = nodeArr[currPos-1];
				WheelmapNode curr = nodeArr[currPos];
				Location prevLoc = new Location("dummyprovider");
				prevLoc.setLatitude(prev.getLat());
				prevLoc.setLongitude(prev.getLong());
				Location currLoc = new Location("dummyprovider");
				currLoc.setLatitude(curr.getLat());
				currLoc.setLongitude(curr.getLong());
				if(currLoc.distanceTo(mLoc) < prevLoc.distanceTo(mLoc))
				{
					nodeArr[currPos-1] = curr;
					nodeArr[currPos] = prev;
					sorted= false;
				}
				currPos++;
			}
		}
			
		mListAdapter = new WheelmapListAdapter(getActivity(), R.layout.wheelmap_node_list_item, nodeArr);
		mListView.setAdapter(mListAdapter);		
		
		// Hide the loading bar
		mLoading.setVisibility(View.INVISIBLE);
		mLoading.setLayoutParams(new LinearLayout.LayoutParams(0,0));
	}
	
	private class GetNodesTask extends AsyncTask<Void, Void, ArrayList<WheelmapNode>> {
		@Override
		protected ArrayList<WheelmapNode> doInBackground(Void... params) {
			ArrayList<WheelmapNode> nodes = new ArrayList<WheelmapNode>();
			try {
				//http://wheelmap.org/api/categories?api_key=EPR2UP3TmegX7zHkhyZW&locale=en&page=2&per_page=5
				WheelmapNodeGetter getter = new WheelmapNodeGetter();
				String url = "http://wheelmap.org/api/";
				if(mCatId != -1)
					url += "categories/" + mCatId;
				else if(mTypeId != -1)
					url += "node_types/" + mTypeId;
				// Get the location of the user
				String bounding = "13.341,52.505,13.434,52.523";
								
				if(mLoc != null) {
					bounding = (mLoc.getLongitude()-0.010) + "," 
								+ (mLoc.getLatitude()-0.010) + "," 
								+ (mLoc.getLongitude()+0.010) + "," 
								+ (mLoc.getLatitude()+0.010);
				}
				
				String apiUrl = url + "/nodes?api_key=EPR2UP3TmegX7zHkhyZW&locale=en&bbox=" + bounding;
				
				String result = getter.getUrl(apiUrl);
				getter.parseItems(nodes, result);
			} catch (IOException ioe) {
				Log.e("url", "Failed to fetch URL: ", ioe);
			} 
			
			// Sort the arraylist
			Collections.sort(nodes);
			
			return nodes;
		}
		
		@Override
		protected void onPostExecute(ArrayList<WheelmapNode> items) {
			mItems = items;
			showNodes();
		}
	}
	
	private class WheelmapListAdapter extends ArrayAdapter<WheelmapNode> {
		Context context;
		int layouteResourceId;
		WheelmapNode data[] = null;
		
		public WheelmapListAdapter(Context context, int layoutResourceId, WheelmapNode[] data) {
			super(context, layoutResourceId, data);
			this.layouteResourceId = layoutResourceId;
			this.context = context;
			this.data = data;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			WheelmapNodeHolder holder = null;
			
			if(row == null) {
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				row = inflater.inflate(layouteResourceId, parent, false);
				
				holder = new WheelmapNodeHolder();
				holder.imgIcon = (ImageView)row.findViewById(R.id.wheelmap_node_list_item_icon);
				holder.txtName = (TextView)row.findViewById(R.id.wheelmap_node_list_item_name);
				holder.txtDistance = (TextView)row.findViewById(R.id.wheelmap_node_list_item_distance);
				
				row.setTag(holder);
			} else {
				holder = (WheelmapNodeHolder)row.getTag();
			}
			
			WheelmapNode node = data[position];
			holder.txtName.setText(node.getName());
			
			// Calculate distance
			if(mLoc != null) {
				Location loc = new Location("dummyprovider");
				loc.setLatitude(node.getLat());
				loc.setLongitude(node.getLong());
				
				DecimalFormat df = new DecimalFormat("#");
				holder.txtDistance.setText(df.format(mLoc.distanceTo(loc)) + "m");
			}
			
			// set the icon
			switch(node.getWheelchair()) {
			case YES:
				holder.imgIcon.setImageResource(R.drawable.yes);
				break;
			case LIMITED:
				holder.imgIcon.setImageResource(R.drawable.partial);
				break;
			case NO:
				holder.imgIcon.setImageResource(R.drawable.no);
				break;
			default:
				holder.imgIcon.setImageResource(R.drawable.unknown);
			}
			
			return row;
		}
		
		
		
		private class WheelmapNodeHolder {
			ImageView imgIcon;
			TextView txtName;
			TextView txtDistance;
		}
		
	}
}
