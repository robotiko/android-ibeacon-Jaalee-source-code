package com.jaalee.proximity.licensing;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jaalee.proximity.api.KitApi;
import com.jaalee.proximity.api.KitAsynchApi;
import com.jaalee.proximity.api.KitAsynchApiCallback;
import com.jaalee.proximity.ibeacon.IBeaconManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
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
public class LicenseManager
{
  private static final String TAG = "LicenseManager";
  private String deviceId;
  private Date lastLicenseCheck;
  private boolean goodStanding = true;
  private String badStandingReason = null;
  private long GOOD_STANDING_SERVER_CHECK_FEQUENCY_MILLIS = 86400000L;
  private long BAD_STANDING_SERVER_CHECK_FEQUENCY_MILLIS = 3600000L;
  private Configuration configuration;
  private int serverCheckCount = 0;
  private boolean skipServerCheck = false;
  private boolean serverCheckScheduled = false;
  Context context;

  public LicenseManager(Context context)
  {
	  reconfigure(context);
  }

  public void reconfigure(Context context)
  {
	  this.configuration = new Configuration(context);
	  this.context = context;
  }

  public void reconfigure(String apiUrl, String apiToken)
  {
	  this.configuration = new Configuration(this.context, this.configuration, apiUrl, apiToken);
	  this.context = this.context;
  }

  public LicenseManager(Context context, Configuration configuration, String badStandingReason, Date lastLicenseCheck)
  {
	  this.configuration = configuration;
     this.context = context;
     this.badStandingReason = badStandingReason;
     if (badStandingReason != null) {
       this.goodStanding = false;
    }
     this.lastLicenseCheck = lastLicenseCheck;
     this.skipServerCheck = true;
  }

  public Configuration getConfiguration() {
     return this.configuration;
  }

  public String getAccountId() {
     if (IBeaconManager.LOG_DEBUG) Log.d("LicenseManager", "getAccountId with configuration" + this.configuration);
     return this.configuration.getLicenseKey();
  }

  public int getServerCheckCount() {
     return this.serverCheckCount;
  }

  public void validateLicense() {
//     if (IBeaconManager.LOG_DEBUG) Log.d("LicenseManager", "validateLicense() called.  license key is " + this.configuration.getLicenseKey() + " in configuration: " + this.configuration);

//     Date now = new Date();
//     long licenseCheckFrequency;
    
//     if (this.goodStanding)
//    	 licenseCheckFrequency = this.GOOD_STANDING_SERVER_CHECK_FEQUENCY_MILLIS;
//     else {
//       licenseCheckFrequency = this.BAD_STANDING_SERVER_CHECK_FEQUENCY_MILLIS;
//    }

//     if ((this.lastLicenseCheck == null) || (now.getTime() - this.lastLicenseCheck.getTime() > licenseCheckFrequency)) {
//       scheduleServerCheck();
//       if (IBeaconManager.LOG_DEBUG) Log.d("LicenseManager", "server check scheduled");
//
//    }
//     else if (!this.goodStanding) {
//       throw new LicensingException("Licensing problem: " + this.badStandingReason + ".  Please go to proximitykit.com to update your license.");
//    }
  }

//  private void scheduleServerCheck()
//  {
//     if (this.serverCheckScheduled) {
//       if (IBeaconManager.LOG_DEBUG) Log.d("LicenseManager", "Skipping server check because one is already scheduled");
//       return;
//    }
//     this.serverCheckScheduled = true;
//     this.serverCheckCount += 1;
//
//     if (this.skipServerCheck) {
//       this.skipServerCheck = false;
//       Log.d("LicenseManager", "Skipping server check due to flag");
//       return;
//    }
//     new KitAsynchApi(new KitApi(getLicenseCheckUrl(), this.configuration.getLicenseKey(), getDeviceId(), "android-1.1.0"), new KitAsynchApiCallback() {
//      public void requestComplete(KitApi api) {
//         if (api.getResponseCode() == 401) {
//           Log.d("LicenseManager", "Licensing check says access is denied.");
//           LicenseManager.this.goodStanding = false;
//           LicenseManager.this.badStandingReason = "Invalid license";
//           LicenseManager.this.lastLicenseCheck = new Date();
//        }
//         else if (api.getResponseCode() == 200) {
//           if (IBeaconManager.LOG_DEBUG) Log.d("LicenseManager", "Licensing server says this key is authorized.");
//           LicenseManager.this.goodStanding = true;
//           LicenseManager.this.badStandingReason = null;
//           LicenseManager.this.lastLicenseCheck = new Date();
//        }
//         else if (IBeaconManager.LOG_DEBUG) { Log.d("LicenseManager", "Unexpected response from license server.  Code: " + api.getResponseCode() + " Exception: " + api.getException()); }
//
//         LicenseManager.this.serverCheckScheduled = false;
//      }
//    }).execute(new Void[0]);
//  }

//  private String getLicenseCheckUrl()
//  {
//    try
//    {
//       URL url = new URL(this.configuration.getKitUrl());
//       return "http://licensing.radiusnetworks.com" + url.getPath();
//    }
//    catch (MalformedURLException e) {
//       Log.w("LicenseManager", "Can't parse kit url: " + this.configuration.getKitUrl());
//     }return null;
//  }

//  private String getLicenseKey()
//  {
//     return this.configuration.getLicenseKey();
//  }

//  public String getDeviceId() {
//     if (this.deviceId == null) {
//      try {
//         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
//         this.deviceId = preferences.getString("radius_proximity_device_id", null);
//         if (this.deviceId == null) {
//           this.deviceId = UUID.randomUUID().toString();
//           SharedPreferences.Editor edit = preferences.edit();
//           edit.putString("radius_proximity_device_id", this.deviceId);
//           edit.commit();
//        }
//      }
//      catch (Exception e) {
//    	  Log.e("LicenseManager", "Can't get or generate device id", e);
//      }
//    }
//	return this.deviceId;
//  }
}
