package com.jaalee.proximity.model;

import java.util.ArrayList;
import java.util.List;
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
public class Kit
{
  private List<KitIBeacon> iBeacons;

  public List<KitIBeacon> getIBeacons()
  {
	  return this.iBeacons;
  }
  public static Kit fromJson(JSONObject json) throws JSONException {
	  Kit kit = new Kit();
	  JSONObject jsonKit = json.getJSONObject("kit");
	  JSONArray jsonIBeacons = jsonKit.getJSONArray("ibeacons");
	  kit.iBeacons = new ArrayList();
	  for (int i = 0; i < jsonIBeacons.length(); i++) {
		  KitIBeacon iBeacon = KitIBeacon.fromJson(jsonIBeacons.getJSONObject(i));
		  kit.iBeacons.add(iBeacon);
	  }
	  return kit;
  }
}
