package de.uniheidelberg.geog.navigationmobileclient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;

public class PreferencesJSONSerializer {
	private Context mContext;
	private String mFilename;
	
	public PreferencesJSONSerializer(Context c, String f) {
		mContext = c;
		mFilename = f;
	}
	
	public void savePreference(String name, Object value) throws IOException {
		// First we need to read the existing preferences so that we can either append or overwrite
		Writer writer = null;
		try {
			ArrayList<Preference> prefs = loadPreferences();
			// Check if the preference is already present, and if so overwrite it's value
			
			int index = -1;
			int currIndex = -1;
			for(Preference pref : prefs) {
				currIndex++;
				if(pref.getName().equalsIgnoreCase("name")) {
					// already present so remove
					index = currIndex;
				}
			}
			if(index > -1) {
				prefs.remove(index);
			}
			
			// now add the Preference
			prefs.add(new Preference(name, value));
			
			// Write to file
			JSONArray jsonArray = new JSONArray();
			for(Preference pref : prefs) {
				jsonArray.put(pref.toJSON());
			}
			
			// Write to disk
			OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(jsonArray.toString());
		} catch (JSONException je) {
			
		} catch (IOException ioe) {
			
		} finally {
			if(writer != null)
				writer.close();
		}
	}
	
	public ArrayList<Preference> loadPreferences() throws IOException, JSONException {
		ArrayList<Preference> prefs = new ArrayList<Preference>();
		BufferedReader reader = null;
		try {
			InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			
			// Parse the data
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString())
					.nextValue();
			// Build the array of preferences
			for(int i = 0; i < array.length(); i++) {
				prefs.add(new Preference(array.getJSONObject(i)));
			}
		} catch (FileNotFoundException fnfe) {
			// ignore
		} finally {
			if(reader != null) {
				reader.close();
			}
		}
		
		return prefs;
	}
}
