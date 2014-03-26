package com.jaalee.proximity.model;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * This project is for developers to make a reference, 
 * but not for commercial purposes. If you have any questions when you use the codes, 
 * or you need the source codes which can be used for commercial purposes, please contact us directly.
 * 
 * @author Alvin.Bert
 * 
 * Technology Support: Alvin.Bert.hu@gmail.com
 * 
 * International Sales: Service@jaalee.com
 * 
 * Jaalee, Inc.
 * 
 * http://www.jaalee.com/
 */
public class KitIBeacon
{
  private String proximityUuid;
  private int major;
  private int minor;
  private Map<String, String> attributes;
  private Double latitude;
  private Double longitude;

  public int getMajor()
  {
	  return this.major;
  }
  public int getMinor() {
	  return this.minor;
  }
  public Map<String, String> getAttributes() {
	  return this.attributes;
  }
  public String getProximityUuid() {
	  return this.proximityUuid;
  }
  public Double getLatitude() {
	  return this.latitude;
  }
  public Double getLongitude() {
	  return this.longitude;
  }

  public static KitIBeacon fromJson(JSONObject json) throws JSONException {
	  KitIBeacon iBeacon = new KitIBeacon();
	  iBeacon.proximityUuid = json.getString("uuid");
	  iBeacon.major = json.getInt("major");
	  iBeacon.minor = json.getInt("minor");
	  if ((json.has("latitude")) && 
			  (!json.isNull("latitude"))) {
		  iBeacon.latitude = Double.valueOf(json.getDouble("latitude"));
	  }

	  if ((json.has("longitude")) && 
			  (!json.isNull("longitude"))) {
		  iBeacon.longitude = Double.valueOf(json.getDouble("longitude"));
	  }

	  iBeacon.attributes = new HashMap();
	  if (json.has("attributes")) {
		  JSONObject jsonProperties = json.getJSONObject("attributes");
		  JSONArray propertyNames = jsonProperties.names();
		  for (int i = 0; i < propertyNames.length(); i++) {
			  String key = propertyNames.getString(i);
			  String value = jsonProperties.getString(key);
			  iBeacon.attributes.put(key, value);
		  }
	  }
	  return iBeacon;
  }
}
