package de.uniheidelberg.geog.navigationmobileclient.wheelmap;

import java.io.Serializable;

public class WheelmapNode implements Comparable<WheelmapNode>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 845872138104162152L;
	
	public static enum WheelchairAccessible { YES, NO, LIMITED, UNKNOWN};
	
	private int mId;
	private double mLat;
	private double mLong;
	private String mName;
	private WheelchairAccessible mWheelchair; 
	private String mWheelchairDesc;
	private String mStreet;
	private String mHouseNumber;
	private String mCity;
	private String mPostcode;
	private String mWebsite;
	private String mPhone;
	
	public int getId() {
		return mId;
	}
	public void setId(int id) {
		mId = id;
	}
	public double getLat() {
		return mLat;
	}
	public void setLat(double lat) {
		mLat = lat;
	}
	public double getLong() {
		return mLong;
	}
	public void setLong(double l) {
		mLong = l;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		mName = name;
	}
	public WheelchairAccessible getWheelchair() {
		return mWheelchair;
	}
	public void setWheelchair(WheelchairAccessible wheelchair) {
		mWheelchair = wheelchair;
	}
	public String getWheelchairDesc() {
		return mWheelchairDesc;
	}
	public void setWheelchairDesc(String wheelchairDesc) {
		mWheelchairDesc = wheelchairDesc;
	}
	public String getStreet() {
		return mStreet;
	}
	public void setStreet(String street) {
		mStreet = street;
	}
	public String getHouseNumber() {
		return mHouseNumber;
	}
	public void setHouseNumber(String houseNumber) {
		mHouseNumber = houseNumber;
	}
	public String getCity() {
		return mCity;
	}
	public void setCity(String city) {
		mCity = city;
	}
	public String getPostcode() {
		return mPostcode;
	}
	public void setPostcode(String postcode) {
		mPostcode = postcode;
	}
	public String getWebsite() {
		return mWebsite;
	}
	public void setWebsite(String website) {
		mWebsite = website;
	}
	public String getPhone() {
		return mPhone;
	}
	public void setPhone(String phone) {
		mPhone = phone;
	}
	@Override
	public int compareTo(WheelmapNode alternate) {
		return mName.compareToIgnoreCase(alternate.getName());
	}
}
