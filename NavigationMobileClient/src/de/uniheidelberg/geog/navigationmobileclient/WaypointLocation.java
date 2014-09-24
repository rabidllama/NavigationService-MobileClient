package de.uniheidelberg.geog.navigationmobileclient;

import java.io.Serializable;

public class WaypointLocation extends Waypoint implements Serializable {
	
	private static final long serialVersionUID = -3111314463643473503L;
	
	private String mAddress;
	
	public WaypointLocation(double lat, double lng) {
		super(lat, lng);
	}
	
	public String getAddress() {
		return mAddress;
	}
	
	public String getShortAddress() {
		String ret = mAddress; 
		if(ret.length() > 45)
			ret = ret.substring(0, 45) + "...";
		
		return ret;
	}
	
	public void setAddress(String address) {
		mAddress = address;
	}
	
	
}