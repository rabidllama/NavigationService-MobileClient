package de.uniheidelberg.geog.navigationmobileclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class PersonaFragment extends Fragment {
	private Map<String, Persona> personaList;
	private TextView personaDescTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		personaList = new LinkedHashMap<String, Persona>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_persona, parent, false);
		
		personaDescTextView = (TextView) v.findViewById(R.id.fragment_persona_details);
		Button selectButton = (Button) v.findViewById(R.id.fragment_persona_select_button);
		selectButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Save the preferences from the persona for use in the system
				
			}
		});
		
		Spinner personas = (Spinner) v.findViewById(R.id.fragment_persona_list);
		
		personas.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int pos, long id) {
				// Set the text of the view to be the description
				String selected = ((TextView)v).getText().toString();
				
				StringBuilder content = new StringBuilder();
				Persona pers = personaList.get(selected);
				if(pers != null) {
					content.append(pers.getDescription());
					// now add each of the preferences				
					content.append("\n\nMax. Slope:")
						.append("\n\t" + pers.getPreference("slope"));
					content.append("\nMax. Curb Height:")
						.append("\n\t" + pers.getPreference("curb"));
					content.append("\nAvoid Surfaces:");
					ArrayList<String> surf = (ArrayList<String>) pers.getPreference("surface");
					for(String s : surf) {
						content.append("\n\t" + s);
					}
				}
				personaDescTextView.setText(content.toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		personaList.put(getResources().getString(R.string.persona_select_list_default), null);
		
		try {
			XmlResourceParser xrp = this.getResources().getXml(R.xml.personas);			
			int eventType = xrp.next();			//<persona>
			while(eventType != XmlPullParser.END_DOCUMENT) {
				// Looping through the xml document until we reach the end	
				// Need to loop through the document to extract each persona
				if(eventType == XmlPullParser.START_TAG
						&& xrp.getName().equalsIgnoreCase("persona")) {
					System.out.println("adding persona");
					Persona pers = new Persona();
					pers.setName(xrp.getAttributeValue(null, "name"));
					
					// At this point we are inside the persona record and so need to get the 
					// elements from within it (description and preferences)
					/*
					 * <persona name="xxx">
					 * 		<description>xxx</description>
					 * 		<preferences>
					 * 			<preference name="xxx">
					 * 				<value>xxx</value>
					 * 			</preference>
					 * 		</preferences>
					 * </persona>
					 */
					// We take control of reading here. The xml parser cannot extract whole objects and so
					// we need to go through each element in turn
					
					/* --- Begin Persona reading --- */
					// Main problem here is that when the system reads a text content for an element, the 
					// name is marked as null which causes NullPointerExceptions when doing the comparison
					eventType = xrp.next();			//<description>
					while(eventType != XmlPullParser.END_TAG
						&& eventType != XmlPullParser.END_DOCUMENT) {
						String elementName = xrp.getName();
						
						if(elementName != null && elementName.equalsIgnoreCase("description")) {
							System.out.println("Adding description");
							// Save the description
							eventType = xrp.next();		//text
							if(eventType == XmlPullParser.TEXT) {
								pers.setDescription(xrp.getText());
							}
							// Now move on to the next tag which should be the end of description
							eventType = xrp.next();		//</description>
							if(eventType != XmlPullParser.END_TAG 
									&& !xrp.getName().equalsIgnoreCase("description")) {
								// something wrong so throw exception
								throw new XmlPullParserException("Unexpected element in description");
							} else {
								// move to next element and loop again
								eventType = xrp.next();		//<preferences>
								continue;					// next loop iteration to get preferences
							}
						}
						
						if(elementName != null && elementName.equalsIgnoreCase("preferences")) {
							// In the list of preferences
							// Need to loop through each individual preference record and store it
							System.out.println("Adding preference");
							eventType = xrp.next();		// <preference>
							
							while(eventType == XmlPullParser.START_TAG
									&& xrp.getName().equalsIgnoreCase("preference")) {
								
								String prefName = xrp.getAttributeValue(null, "name");
								// store the preference (n.b. the surface pref can have multiple values)
								if(prefName.equalsIgnoreCase("slope")) {
									System.out.println("Adding slope");
									eventType = xrp.next();			// <value>
									if(xrp.getName() != null 
											&& eventType == XmlPullParser.START_TAG
											&& xrp.getName().equalsIgnoreCase("value")) {
										// Move to text node
										eventType = xrp.next();		// text
										if(eventType == XmlPullParser.TEXT) {
											pers.addPreference(prefName, Float.parseFloat(xrp.getText()));
										}
										
										// advance (value end tag)
										eventType = xrp.next();		// </value>
										if(eventType == XmlPullParser.END_TAG 
												&& !xrp.getName().equalsIgnoreCase("value")) {
											throw new XmlPullParserException("Unexpected item in slope preference value: " + xrp.getName());
										}
										
										//advance to next tag 
										eventType = xrp.next();		// </preference>
										if(eventType == XmlPullParser.END_TAG 
												&& !xrp.getName().equalsIgnoreCase("preference")) {
											throw new XmlPullParserException("Unexpected item in slope preference: " + xrp.getName());
										}
										
										eventType = xrp.next();		// <preference>  or </preferences>
										continue;
									}
								}
								
								if(prefName.equalsIgnoreCase("curb")) {
									System.out.println("Adding curb");
									eventType = xrp.next();		// <value>
									if(xrp.getName() != null 
											&& eventType == XmlPullParser.START_TAG
											&& xrp.getName().equalsIgnoreCase("value")) {
										// Move to text node
										eventType = xrp.next();		// text
										if(eventType == XmlPullParser.TEXT) {
											pers.addPreference(prefName, Float.parseFloat(xrp.getText()));
										}
										
										// advance (value end tag)
										eventType = xrp.next();		// </value>
										if(eventType == XmlPullParser.END_TAG 
												&& !xrp.getName().equalsIgnoreCase("value")) {
											throw new XmlPullParserException("Unexpected item in curb preference value");
										}
										//advance to next tag 
										eventType = xrp.next();		// </preference>
										if(eventType == XmlPullParser.END_TAG 
												&& !xrp.getName().equalsIgnoreCase("preference")) {
											throw new XmlPullParserException("Unexpected item in curb preference");
										}
										
										eventType = xrp.next();		// <preference> or </preferences>
										continue;
									}
								}
								
								if(prefName.equalsIgnoreCase("surface")) {
									System.out.println("Adding surface");
									// Loop through the various values
									ArrayList<String> rest = new ArrayList<String>();
									eventType = xrp.next();		// <value>
									while(eventType == XmlPullParser.START_TAG
											&& xrp.getName().equalsIgnoreCase("value")) {
										
										eventType = xrp.next();		// text
										if(eventType == XmlPullParser.TEXT) {
											rest.add(xrp.getText());
											System.out.println(xrp.getText());
										}
										// next element (should be closing value)
										eventType = xrp.next();		// </value>
										
										if(eventType != XmlPullParser.END_TAG
												&& !xrp.getName().equalsIgnoreCase("value")) {
											throw new XmlPullParserException("Unexpected tag in surface preferences");
										}
										
										eventType = xrp.next();		// <value> or </preference>
									}
									
									// Now tag should be </preference>
									pers.addPreference(prefName, rest);
									
									if(eventType == XmlPullParser.END_TAG 
											&& !xrp.getName().equalsIgnoreCase("preference")) {
										throw new XmlPullParserException("Unexpected item in surface preference");
									}
									
									eventType = xrp.next();		// <preference> or </preferences>
									continue;
									
								} // End of surface
							} // end of preferences loop
						} // end of preferences element
						
						eventType = xrp.next();
					} // end of the persona element
					
					personaList.put(pers.getname(), pers);
					System.out.println(pers.getname() + " added");
				}
				eventType = xrp.next();
			}
		} catch (IOException ioe) {
			
		} catch (XmlPullParserException xppe) {
			System.out.println(xppe.getMessage());
		}	
		
		// Set the values to display in the drop down box
		List<String> personaNames = new ArrayList<String>(personaList.keySet());
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, personaNames);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		personas.setAdapter(dataAdapter);
		
		return v;
	}
}
