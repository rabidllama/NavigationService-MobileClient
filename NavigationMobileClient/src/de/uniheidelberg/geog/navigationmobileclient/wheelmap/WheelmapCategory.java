package de.uniheidelberg.geog.navigationmobileclient.wheelmap;

public class WheelmapCategory implements Comparable<WheelmapCategory> {
	private int mId;
	private String mIdentifier;
	private String mLocalisedName;
	
	public String toString() {
		return mLocalisedName;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public String getIdentifier() {
		return mIdentifier;
	}

	public void setIdentifier(String identifier) {
		mIdentifier = identifier;
	}

	public String getLocalisedName() {
		return mLocalisedName;
	}

	public void setLocalisedName(String localisedName) {
		mLocalisedName = localisedName;
	}

	@Override
	public int compareTo(WheelmapCategory another) {
		return mLocalisedName.compareToIgnoreCase(another.getLocalisedName());
	}
	
}
