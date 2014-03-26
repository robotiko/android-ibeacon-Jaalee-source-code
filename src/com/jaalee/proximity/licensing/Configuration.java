package com.jaalee.proximity.licensing;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jaalee.proximity.ibeacon.IBeaconManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
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
public class Configuration
{
  private static final String TAG = "Configuration";
  private static String API_TOKEN_KEY = "PKAPIToken";
  private static String KIT_URL_KEY = "PKKitURL";
  protected String deviceId;
  protected String licenseKey;
  protected String kitUrl;
  private Context context;

  public Configuration(Context context)
  {
	  this.context = context;
  }

  public Configuration(Context context, Configuration oldConfiguration, String apiUrl, String apiKey) {
	  if (oldConfiguration != null) {
		  this.deviceId = oldConfiguration.deviceId;
	  }
	  this.kitUrl = apiUrl;
	  this.licenseKey = apiKey;
  }

  private SharedPreferences getPreferences() {
	  return PreferenceManager.getDefaultSharedPreferences(this.context);
  }

  public String getKitUrl() {
	  if (this.kitUrl == null) {
		  if (IBeaconManager.LOG_DEBUG) Log.d("Configuration", "kitURL is null.  Looking for properties file");
		  loadFromProperties();
	  }
		
	  return this.kitUrl;
  }

  public String getLicenseKey() {
	  if (this.licenseKey == null) {
		  loadFromProperties();
    }
	  return this.licenseKey;
  }

  private void loadFromProperties()
  {
	  InputStream inputStream = new PropertiesFile().getInputStream();
	  if (inputStream == null)
		  Log.e("Configuration", "Cannot find " + PropertiesFile.PROPERTIES_FILE_NAME + " on classpath.");
    else
      try
      {
    	  Properties props = new Properties();
    	  props.load(inputStream);
    	  this.licenseKey = ((String)props.get(API_TOKEN_KEY));
    	  this.kitUrl = ((String)props.get(KIT_URL_KEY));
    	  if ((this.licenseKey == null) || (this.kitUrl == null)) {
    		  throw new LicensingException("Invalid " + PropertiesFile.PROPERTIES_FILE_NAME + " file on the classpath.  Please download a new configuration file from Radius Networks.");
    	  }
    	  if (IBeaconManager.LOG_DEBUG) Log.d("Configuration", "loaded properties file.  license key is " + this.licenseKey); 
      }
	  catch (IOException e) { Log.e("Configuration", "Cannot read " + PropertiesFile.PROPERTIES_FILE_NAME, e);
	  if (getLicenseKey() == null)
		  throw new LicensingException("No license found.  Please verify you have a " + PropertiesFile.PROPERTIES_FILE_NAME + " file on the classpath.");
	  }
  }

  public String getDeviceId()
  {
	  if (this.deviceId == null) {
      try {
    	  this.deviceId = getPreferences().getString("radius_proximity_device_id", null);
    	  if (this.deviceId == null) {
    		  this.deviceId = UUID.randomUUID().toString();
    		  SharedPreferences.Editor edit = getPreferences().edit();
    		  edit.putString("radius_proximity_device_id", this.deviceId);
    		  edit.commit();
    	  }
      }
      catch (Exception e) {
    	  Log.e("Configuration", "Can't get or generate device id", e);
      }
    }
	  return this.deviceId;
  }
}
