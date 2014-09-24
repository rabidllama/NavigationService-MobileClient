package de.uniheidelberg.geog.navigationmobileclient;

import java.util.HashMap;
import java.util.Map;

public class Persona {
	private String mName;
	private String mDescription;
	private Map<String, Object> mPreferences;
	
	public Persona() {
		mPreferences = new HashMap<String, Object>();
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public void setDescription(String desc) {
		mDescription = desc;
	}
	
	public String getname() {
		return mName;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	public Object getPreference(String name) {
		return mPreferences.get(name);
	}
	
	public void addPreference(String name, Object value) {
		mPreferences.put(name, value);
	}
}
