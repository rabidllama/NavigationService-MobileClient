package de.uniheidelberg.geog.navigationmobileclient;

import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NavigationInstructionFragment extends Fragment implements OnInitListener {
	public static final String EXTRA_ROUTE_ID = "de.uniheidelberg.geog.navigationmobileclient.navigationInstructionFragment.RouteId";
	
	private Instruction mInst;
	
	private TextView mText;
	private TextView mDistance;
	private LinearLayout mLoading;
	private ImageView mImage;
	
	private Location mLoc;
	
	private long mPrevWpId = -1;
	private long mCurrWpId = -1;
	private long mRouteId = -1;
	
	private int MY_DATA_CHECK_CODE = 0;
	private TextToSpeech mTts;
	
	private boolean hasSpoken = false;
	
	private BroadcastReceiver mOnLocationChange = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// Update the location stored based on the values from the Location Receiver
			Location recLoc = (Location) intent.getExtras().get(LocationService.EXTRA_LOCATION);
			
			if(recLoc != null) {
				mLoc = recLoc;
			}
			
			new GetInstructionTask().execute();	
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
		
		Intent i = getActivity().getIntent();
		mRouteId = i.getLongExtra(EXTRA_ROUTE_ID, -1);

		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_navigation_instruction, parent, false);
		mText = (TextView) v.findViewById(R.id.navigation_instruction_text);
		mLoading = (LinearLayout) v.findViewById(R.id.navigation_instruction_location_loading);
		mDistance = (TextView) v.findViewById(R.id.navigation_instruction_distance);
		mImage = (ImageView) v.findViewById(R.id.navigation_instruction_image);
		return v;
	}
	
	private void updateDisplay() {		
		// Look to see if there is a corresponding entry in xml resources
		
		String decoded = mInst.decodeInstruction(getActivity());
		
		mText.setText(decoded);
		
		// calculate the distance
		Waypoint wp = mInst.getInstructionLocation();
		Location wpLoc = new Location("DummyProvider");
		wpLoc.setLatitude(wp.getLat());
		wpLoc.setLongitude(wp.getLong());
		float dist = mLoc.distanceTo(wpLoc);
		mDistance.setText("Distance: " + String.format("%.0f", dist) + "m");
		
		// Add an image for the direction
		String direction = mInst.getInstructionComponent(Instruction.Component_Type.DIRECTION);
		String verb = mInst.getInstructionComponent(Instruction.Component_Type.VERB);
		
		if(direction.equalsIgnoreCase("left")) {
			if(verb.equalsIgnoreCase("turn")) 
				mImage.setImageDrawable(getResources().getDrawable(R.drawable.turn_left));
			if(verb.equalsIgnoreCase("veer"))
				mImage.setImageDrawable(getResources().getDrawable(R.drawable.veer_left));
			if(verb.equalsIgnoreCase("sharp"))
				mImage.setImageDrawable(getResources().getDrawable(R.drawable.sharp_turn_left));
		} else if(direction.equalsIgnoreCase("right")) {
			if(verb.equalsIgnoreCase("turn")) 
				mImage.setImageDrawable(getResources().getDrawable(R.drawable.turn_right));
			if(verb.equalsIgnoreCase("veer"))
				mImage.setImageDrawable(getResources().getDrawable(R.drawable.veer_right));
			if(verb.equalsIgnoreCase("sharp"))
				mImage.setImageDrawable(getResources().getDrawable(R.drawable.sharp_turn_right));
		}
		
		mImage.setContentDescription(decoded);
		
		// Hide the loading bar
		mLoading.setVisibility(View.INVISIBLE);
		mLoading.setLayoutParams(new LinearLayout.LayoutParams(0,0));		
		
		//private TextToSpeech tts = new TextToSpeech(getActivity().getApplicationContext(), this);
		//tts.setLanguage(Locale.UK);
		if(!mTts.isSpeaking() && !hasSpoken) {
			mTts.speak(decoded, TextToSpeech.QUEUE_FLUSH, null);
			hasSpoken = true;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MY_DATA_CHECK_CODE) {
			if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				mTts = new TextToSpeech(getActivity(), this);
			} else {
				Intent installTTSIntent = new Intent();
				installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installTTSIntent);
			}
		}
	}
	
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
	        mTts.setLanguage(Locale.UK);
	    } else if (status == TextToSpeech.ERROR) {
	        Toast.makeText(getActivity(), "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
	    }
	}
	
	
	
	private class GetInstructionTask extends AsyncTask<Void, Void, Instruction> {
		@Override
		protected Instruction doInBackground(Void... params) {			
			// Get an instruction from the navigation service		
			
			InstructionGetter getter = new InstructionGetter(getActivity());
			Instruction inst = getter.getInstruction(mRouteId, mPrevWpId, mLoc);
			
			return inst;
		}
		
		@Override
		protected void onPostExecute(Instruction instruction) {
			// update items
			mInst = instruction;
			
			if(instruction.getWaypointId() == mCurrWpId) {
				// Still on the same waypoint so do not update anything
			} else {
				// Id is not the same so we have advanced
				hasSpoken = false;
				// Check if the previous and current are the same, if they are then it has just been changed
				if(mPrevWpId == mCurrWpId) {
					// Just update the current one
					mCurrWpId = instruction.getWaypointId();
				} else {				
					mPrevWpId = mCurrWpId;
					mCurrWpId = instruction.getWaypointId();
				}
			}
			
			updateDisplay();
		}
	}

}
