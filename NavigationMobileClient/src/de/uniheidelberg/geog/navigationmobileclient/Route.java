package de.uniheidelberg.geog.navigationmobileclient;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class Route implements Serializable {
	private static final long serialVersionUID = 5986502991340399362L;
	
	private long mId;
	private String mDuration;
	private String mUom;
	private double mLength;
	
	private ArrayList<Waypoint> mWaypoints;
	
	public Route() {
		mWaypoints = new ArrayList<Waypoint>();
	}
	
	public void addWaypoint(Waypoint wp) {
		mWaypoints.add(wp);
	}
	public ArrayList<Waypoint> getWaypoints() {
		return mWaypoints;
	}

	public long getId() {
		return mId;
	}

	public void setId(long l) {
		mId = l;
	}

	public String getDuration() {
		return mDuration;
	}

	public void setDuration(String duration) {
		mDuration = duration;
	}

	public String getUom() {
		return mUom;
	}

	public void setUom(String uom) {
		mUom = uom;
	}

	public double getLength() {
		return mLength;
	}

	public void setLength(double length) {
		mLength = length;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Length: " + mLength + mUom + "\n");
		sb.append("Duration: " + mDuration + "\n");
		for(Waypoint wp : mWaypoints)
			sb.append(wp + "\n");
		
		return sb.toString();
	}
	
	public void save(Context context) {
		try {
			FileOutputStream fos = context.openFileOutput("saved_route.bin", Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(this);
			oos.flush();
			oos.close();
			fos.close();
			
		} catch (IOException ioe) {
			Log.e("Route", "Error writing route to file: " + ioe.getLocalizedMessage());
		} 
		
		Log.e("Route", "Saved to device");
	}
	
	public static Route loadSavedRoute(Context context) {
		Route saved = null;
		try {
			FileInputStream fin = context.openFileInput("saved_route.bin");
			ObjectInputStream ois = new ObjectInputStream(fin);
			saved = (Route) ois.readObject();
		} catch (IOException ioe) {
			Log.e("Route", "Error reading route from file: " + ioe.getLocalizedMessage());
		} catch (ClassNotFoundException cnfe) {
			Log.e("Route", "Error reading route from file: " + cnfe.getLocalizedMessage());
		}
		
		return saved;
	}
}
