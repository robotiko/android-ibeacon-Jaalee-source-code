 package com.jaalee.proximity.ibeacon.data.proximitykit;
 
 import android.content.Context;
 import android.content.SharedPreferences;
 import android.content.SharedPreferences.Editor;
 import android.preference.PreferenceManager;
 import android.util.Log;

import com.jaalee.ibeacon.IBeacon;
import com.jaalee.ibeacon.IBeaconData;
import com.jaalee.ibeacon.IBeaconDataNotifier;
import com.jaalee.ibeacon.client.DataProviderException;
import com.jaalee.proximity.ProximityKitManager;
import com.jaalee.proximity.ProximityKitSyncException;
import com.jaalee.proximity.ProximityKitSyncNotifier;
import com.jaalee.proximity.api.KitApi;
import com.jaalee.proximity.api.KitAsynchApi;
import com.jaalee.proximity.api.KitAsynchApiCallback;
import com.jaalee.proximity.ibeacon.IBeaconManager;
import com.jaalee.proximity.ibeacon.data.Persister;
import com.jaalee.proximity.licensing.Configuration;
import com.jaalee.proximity.licensing.LicenseManager;
import com.jaalee.proximity.model.Kit;
import com.jaalee.proximity.model.KitIBeacon;

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
 public class ProximityKitPersister
   implements Persister
 {
   private static final String TAG = "ProximityKitPersister";
   private LicenseManager licenseManager;
   private Context context;
   private ProximityKitManager pkManager;
 
   public ProximityKitPersister(ProximityKitManager manager, LicenseManager licenseManager, Context context)
   {
     this.licenseManager = licenseManager;
     this.context = context;
     this.pkManager = manager;
   }
 
   public void saveToCloud(IBeaconData data, IBeaconDataNotifier notifier)
   {
   }
 
   public void loadKitFromCloud(final ProximityKitSyncNotifier notifier) 
   {
     this.licenseManager.validateLicense();
//     new KitAsynchApi(new KitApi(this.licenseManager.getConfiguration().getKitUrl(), this.licenseManager.getConfiguration().getLicenseKey(), this.licenseManager.getDeviceId(), "android-1.1.0"), new KitAsynchApiCallback() 
//     {
//    	 public void requestComplete(KitApi api) 
//    	 {
//    		 if ((api.getResponseCode() == 200) && (api.getException() == null) && (api.getResponseKit() != null)) 
//    		 {
//    			 Log.d("ProximityKitPersister", "kit fetch succeeded");
//    			 ProximityKitPersister.this.pkManager.setKit(api.getResponseKit());
//    			 ProximityKitPersister.this.saveToCache(api.getResponseString());
//    			 notifier.didSync();
//    		 }
//    		 else 
//    		 {
//    			 Log.d("ProximityKitPersister", "kit: " + api.getResponseKit() + "fetch failed: " + api.getResponseCode(), api.getException());
//    			 if (api.getException() != null) {
//    				 notifier.didFailSync(api.getException());
//    			 }
//    			 else if (api.getResponseKit() == null) {
//    				 notifier.didFailSync(new ProximityKitSyncException("Failed to receive a non-null kit from ProximityKit"));
//    			 }
//    			 else
//    				 notifier.didFailSync(new ProximityKitSyncException("Failed to sync with HTTP response code " + api.getResponseCode()));
//    		 }
//    	 }
//     }).execute(new Void[0]);
   }
 
   public void loadFromCloud(final IBeacon iBeacon, final IBeaconDataNotifier notifier)
   {
     this.licenseManager.validateLicense();
     IBeaconData iBeaconData = ProximityKitPersister.this.loadFromCache(iBeacon);
	 notifier.iBeaconDataUpdate(iBeacon, iBeaconData, null);     
//     new KitAsynchApi(new KitApi(this.licenseManager.getConfiguration().getKitUrl(), this.licenseManager.getConfiguration().getLicenseKey(), this.licenseManager.getDeviceId(), "android-1.1.0"), new KitAsynchApiCallback() 
//     {
//    	 public void requestComplete(KitApi api) 
//    	 {
//    		 if ((api.getResponseCode() == 200) && (api.getException() == null)) 
//    		 {
//    			 if (api.getResponseKit() != null) 
//    			 {
//    				 ProximityKitPersister.this.pkManager.setKit(api.getResponseKit());
//    				 ProximityKitPersister.this.saveToCache(api.getResponseString());
//    			 }
//    			 IBeaconData iBeaconData = ProximityKitPersister.this.loadFromCache(iBeacon);
//    			 notifier.iBeaconDataUpdate(iBeacon, iBeaconData, null);
//    		 }
//    		 else 
//    		 {
//    			 notifier.iBeaconDataUpdate(iBeacon, null, new DataProviderException("Failed to load iBeacon data", api.getException()));
//    		 }
//    	 }
//     }).execute(new Void[0]);
   }
 
   public IBeaconData loadFromCache(IBeacon iBeacon)
   {
	   if (this.pkManager.getKit() == null) 
	   {
		   SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		   String jsonString = preferences.getString("proximityKitData", null);
		   if (jsonString != null) {
			   try
			   {
				   JSONObject json = new JSONObject(jsonString);
				   this.pkManager.setKit(Kit.fromJson(json));
				   if (IBeaconManager.LOG_DEBUG) Log.d("ProximityKitPersister", "Loaded proximityKitData from shared preferences"); 
			   }
			   catch (JSONException e)
			   {
				   Log.w("ProximityKitPersister", "Failed to parse json stored in proximityKitData in shared preferences: " + jsonString, e);
			   }
		   }
	   }
 
     if (this.pkManager.getKit() != null)
     {
       for (KitIBeacon kitIBeacon : this.pkManager.getKit().getIBeacons())
       {
         if ((kitIBeacon.getProximityUuid() != null) && (kitIBeacon.getProximityUuid().equalsIgnoreCase(iBeacon.getProximityUuid())) && (kitIBeacon.getMajor() == iBeacon.getMajor()) && (kitIBeacon.getMinor() == iBeacon.getMinor()))
         {
           return new PkIBeaconData(kitIBeacon, this, iBeacon);
         }
       }
       Log.d("ProximityKitPersister", "The requested iBeacon is not in the proximity kit data");
     }
     return null;
   }
 
   protected void saveToCache(String kitString) {
     SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
     SharedPreferences.Editor edit = preferences.edit();
     edit.putString("proximityKitData", kitString);
     edit.commit();
     Log.d("ProximityKitPersister", "Saved proximity kit data to shared preferences");
   }
 }

