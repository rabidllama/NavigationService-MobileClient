package de.uniheidelberg.geog.navigationmobileclient;

import java.io.Serializable;

public class Waypoint implements Serializable {
	private static final long serialVersionUID = 7435925006305883533L;

	protected double mLong;
	protected double mLat;

	public Waypoint(double lat, double lng) {
		mLong = lng;
		mLat = lat;
	}
	
	public Waypoint(String str) {
		String comps[] = str.split(" ");
		mLat = Double.parseDouble(comps[0]);
		mLong = Double.parseDouble(comps[1]);
	}
	
	public double getLong() {
		return mLong;
	}
	public void setLong(double l) {
		mLong = l;
	}
	public double getLat() {
		return mLat;
	}
	public void setLat(double lat) {
		mLat = lat;
	}
	
	public String toString() {
		return mLat + " " + mLong;
	}
}
