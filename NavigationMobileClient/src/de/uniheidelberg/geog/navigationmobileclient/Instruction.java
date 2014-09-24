package de.uniheidelberg.geog.navigationmobileclient;

import android.content.Context;

public class Instruction {
	public static enum Component_Type {ADJECTIVE, DIRECTION, NAME, NOUN, ORDINAL, PREPOSITION, VERB};
	
	private long mWaypointId;
	private long mRouteId;
	private Waypoint mInstructionLocation;
	private String mInstruction;
	private Waypoint mUserLocation;
	
	public long getWaypointId() {
		return mWaypointId;
	}
	public void setWaypointId(long id) {
		mWaypointId = id;
	}	
	public long getRouteId() {
		return mRouteId;
	}
	public void setRouteId(long routeId) {
		mRouteId = routeId;
	}
	public Waypoint getInstructionLocation() {
		return mInstructionLocation;
	}
	public void setInstructionLocation(Waypoint instructionLocation) {
		mInstructionLocation = instructionLocation;
	}
	public String getInstruction() {
		return mInstruction;
	}
	public void setInstruction(String instruction) {
		mInstruction = instruction;
	}
	public Waypoint getUserLocation() {
		return mUserLocation;
	}
	public void setUserLocation(Waypoint userLocation) {
		mUserLocation = userLocation;
	}	
	
	public String decodeInstruction(Context c) {
		// First split on |
		String[] els = mInstruction.split("\\|");
		String name = els[2];
		
		// Check if a name has been provided
		if(name == null || name.equals("") || name == "") {
			// No name
		} else {
			els[2] = "x";
		}
		
		String resName = els[0] + "_" + els[1] + "_" + els[2] + "_" + els[3] + "_" + els[4] + "_" + els[5] + "_" + els[6];
		
		int resId = c.getResources().getIdentifier(resName, "string", c.getPackageName());
		if(resId != 0) 
			return String.format(c.getString(resId), name);
		else
			return mInstruction;
	}
	
	public String getInstructionComponent(Component_Type type) {
		String[] els = mInstruction.split("\\|");
		if(els.length == 7) {
			switch(type) {
			case ADJECTIVE:
				return(els[0]);
			case DIRECTION:
				return(els[1]);
			case NAME:
				return(els[2]);
			case NOUN:
				return(els[3]);
			case ORDINAL:
				return(els[4]);
			case PREPOSITION:
				return(els[5]);
			case VERB:
				return(els[6]);
			}
		}
		return "";
	}
}
