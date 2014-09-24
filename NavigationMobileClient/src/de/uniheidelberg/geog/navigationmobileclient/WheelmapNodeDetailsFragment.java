package de.uniheidelberg.geog.navigationmobileclient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import de.uniheidelberg.geog.navigationmobileclient.wheelmap.WheelmapNode;

public class WheelmapNodeDetailsFragment extends Fragment {
	public static final String EXTRA_NODE = "de.uniheidelberg.geog.navigationmobileclient.WheelmapNodeDetailsFragment.node";
	
	private WheelmapNode mNode;
	private ImageView mIcon;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mNode = (WheelmapNode)getActivity().getIntent()
				.getSerializableExtra(EXTRA_NODE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_wheelmap_details, parent, false);
		
		// use the node to populate the display
		if(mNode != null) {
			updateText((TextView) v.findViewById(R.id.wheelmap_node_name), mNode.getName());
			updateText((TextView) v.findViewById(R.id.wheelmap_node_street), mNode.getStreet());
			updateText((TextView) v.findViewById(R.id.wheelmap_node_housenumber), mNode.getHouseNumber());
			updateText((TextView) v.findViewById(R.id.wheelmap_node_city), mNode.getCity());
			updateText((TextView) v.findViewById(R.id.wheelmap_node_postcode), mNode.getPostcode());
			
			mIcon = (ImageView) v.findViewById(R.id.wheelmap_node_accessible);
			
			// Website should be a hyperlink
			TextView txtWeb = (TextView) v.findViewById(R.id.wheelmap_node_website);
			if(mNode.getWebsite() == null || mNode.getWebsite().equals("null") || mNode.getWebsite().equals(""))
				txtWeb.setVisibility(View.GONE);
			else {
				// set the content to be a hyperlink
				String text = "<a href=\"" + mNode.getWebsite() + "\">Website</a>";
				txtWeb.setClickable(true);
				txtWeb.setMovementMethod(LinkMovementMethod.getInstance());
				txtWeb.setText(Html.fromHtml(text));
			}
			
			// Allow the calling of a phone number
			TextView txtPhone = (TextView) v.findViewById(R.id.wheelmap_node_phone);
			if(mNode.getPhone() == null || mNode.getPhone().equals("null") || mNode.getPhone().equals(""))
				txtPhone.setVisibility(View.GONE);
			else {
				txtPhone.setText(mNode.getPhone());
				txtPhone.setClickable(true);
				txtPhone.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						DialogFragment dialog = new CallNumberDialog();
						dialog.show(getFragmentManager(), "CallDialogFragment");
					}
				});
			}
			
			updateText((TextView) v.findViewById(R.id.wheelmap_node_description), mNode.getWheelchairDesc());
			
			// set the image showing whether it is wheelchair accessible
			switch(mNode.getWheelchair()) {
			case YES:
				mIcon.setImageResource(R.drawable.yes);
				mIcon.setContentDescription(getActivity().getText(R.string.wheelmap_details_icon_alt_accessible));
				break;
			case NO:
				mIcon.setImageResource(R.drawable.no);
				mIcon.setContentDescription(getActivity().getText(R.string.wheelmap_details_icon_alt_not));
				break;
			case LIMITED:
				mIcon.setImageResource(R.drawable.partial);
				mIcon.setContentDescription(getActivity().getText(R.string.wheelmap_details_icon_alt_limited));
				break;
			case UNKNOWN:
				mIcon.setImageResource(R.drawable.unknown);
				mIcon.setContentDescription(getActivity().getText(R.string.wheelmap_details_icon_alt_unknown));
				break;
			}
			
			// Add button event
			((Button) v.findViewById(R.id.wheelmap_node_ok)).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getActivity(), RouteSettingsActivity.class);
					
					WaypointLocation loc = new WaypointLocation(mNode.getLat(), mNode.getLong());
					
					String add = "";
					add = addToAddress(add, mNode.getName());
					add = addToAddress(add, mNode.getHouseNumber());
					add = addToAddress(add, mNode.getStreet());
					add = addToAddress(add, mNode.getCity());
					add = addToAddress(add, mNode.getPostcode());
					loc.setAddress(add);
					Log.i("Details address", add);
					i.putExtra(RouteSettingsFragment.EXTRA_TARGET_LOCATION, loc);
					
					startActivity(i);
				}
			});
		}
				
		return v;
	}
	
	private String addToAddress(String add, String comp) {
		String ret = add;
		if(add == null || add.equals("null") || add.equals("")) {
			ret = comp;
		} else {
			ret += ", " + comp;
		}
		
		return  ret;
	}
	
	
	private void updateText(TextView tv, String value) {

		Log.i("Details", value);
		if(value == null || value.equals("null") || value.equals(""))
			tv.setVisibility(View.GONE);
		else
			tv.setText(value);
	}
	
	private class CallNumberDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			
			builder.setMessage(getString(R.string.wheelmap_node_call_text, mNode.getName()))
					.setPositiveButton(R.string.wheelmap_node_call_ok_button, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String phone = mNode.getPhone().replaceAll("-", "").replaceAll(" ", "");
							Intent phoneIntent = new Intent(Intent.ACTION_CALL);
							phoneIntent.setData(Uri.parse("tel:" + phone));
							phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(phoneIntent);
						}
					})
					.setNegativeButton(R.string.wheelmap_node_call_cancel_button, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//User cancelled - do nothing
						}
					});
			return builder.create();
		}
	}
}
