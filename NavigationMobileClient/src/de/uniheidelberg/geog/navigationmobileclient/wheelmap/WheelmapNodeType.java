package de.uniheidelberg.geog.navigationmobileclient.wheelmap;

public class WheelmapNodeType implements Comparable<WheelmapNodeType>{
	private int mId;
	private String mIdentifier;
	private String mIcon;
	private String mLocalisedName;
	
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
	public String getIcon() {
		return mIcon;
	}
	public void setIcon(String icon) {
		mIcon = icon;
	}
	public String getLocalisedName() {
		return mLocalisedName;
	}
	public void setLocalisedName(String localisedName) {
		mLocalisedName = localisedName;
	}
	@Override
	public int compareTo(WheelmapNodeType alternate) {
		return mLocalisedName.compareToIgnoreCase(alternate.getLocalisedName());
	}	
}
