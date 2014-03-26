 package com.jaalee.proximity;
 
 import android.content.Context;
 import android.util.Log;

import com.jaalee.proximity.api.ConfigurationApi;
import com.jaalee.proximity.api.ConfigurationAsynchApi;
import com.jaalee.proximity.api.ConfigurationAsynchApiCallback;
import com.jaalee.proximity.geofence.GeofenceManager;
import com.jaalee.proximity.ibeacon.IBeaconManager;
import com.jaalee.proximity.ibeacon.data.proximitykit.ProximityKitPersister;
import com.jaalee.proximity.licensing.LicenseManager;
import com.jaalee.proximity.model.Kit;
import com.jaalee.proximity.proximitykit.IBeaconAdapter;
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
 public class ProximityKitManager
 {
   private static final String TAG = "ProximityKitManager";
   private static ProximityKitManager instance = null;
   private ProximityKitNotifier notifier;
   private IBeaconAdapter iBeaconAdapter;
   protected Context context;
   private static ProximityKitManager proximityKitManager;
   private Kit kit;
 
   public static ProximityKitManager getInstanceForApplication(Context context)
   {
     if (instance == null) {
       instance = new ProximityKitManager(context);
     }
     return instance;
   }
   protected ProximityKitManager() {
   }
 
   protected ProximityKitManager(Context context) {
     this.context = context;
     IBeaconManager.getInstanceForApplication(context);
     GeofenceManager.getInstanceForApplication(context);
     this.iBeaconAdapter = new IBeaconAdapter(this, context, this.notifier);
   }
 
   public IBeaconManager getIBeaconManager()
   {
     return IBeaconManager.getInstanceForApplication(this.context);
   }
 
   public GeofenceManager getGeofenceManager()
   {
     return GeofenceManager.getInstanceForApplication(this.context);
   }
 
   public void start()
   {
     sync();
   }
 
   public void restart()
   {
     getIBeaconManager().getLicenseManager().reconfigure(this.context);
     getIBeaconManager().licenseChanged(this.context);
     start();
   }
 
   public void restart(final String code) 
   {
     String deviceId = getIBeaconManager().getLicenseManager().getConfiguration().getDeviceId();
     String url = "http://pkcode.radiusnetworks.com/api/configurations/" + code;
     Log.d("ProximityKitManager", "Trying to get configuration from " + url);
     new ConfigurationAsynchApi(new ConfigurationApi(url, deviceId, "android-1.1.0"), new ConfigurationAsynchApiCallback() {
       public void requestComplete(ConfigurationApi api) {
         if ((api.getException() != null) || (api.getResponseCode() != 200)) {
           Log.d("ProximityKitManager", "Failed to get configuration from proximity kit with code of " + code, api.getException());
           if (ProximityKitManager.this.notifier != null) {
             ProximityKitManager.this.notifier.didFailSync(api.getException());
           }
         }
         else
         {
           Log.d("ProximityKitManager", "Changing the configuration to fetch from this url:" + api.getResponseConfiguration().getApiUrl());
           ProximityKitManager.this.getIBeaconManager().getLicenseManager().reconfigure(api.getResponseConfiguration().getApiUrl(), api.getResponseConfiguration().getToken());
           if (ProximityKitManager.this.notifier != null)
             ProximityKitManager.this.start();
         }
       }
     }).execute(new Void[0]);
   }
 
   public void sync()
   {
       if (ProximityKitManager.this.notifier != null) {
           ProximityKitManager.this.notifier.didSync();
         }
         ProximityKitManager.this.iBeaconAdapter.updateRegions();
         
//     ProximityKitPersister persister = new ProximityKitPersister(this, getIBeaconManager().getLicenseManager(), this.context);
//     persister.loadKitFromCloud(new ProximityKitSyncNotifier()
//     {
//       public void didSync() {
//         if (ProximityKitManager.this.notifier != null) {
//           ProximityKitManager.this.notifier.didSync();
//         }
//         ProximityKitManager.this.iBeaconAdapter.updateRegions();
//       }
// 
//       public void didFailSync(Exception e)
//       {
//         if (ProximityKitManager.this.notifier != null)
//           ProximityKitManager.this.notifier.didFailSync(e);
//       }
//     });
   }
 
   public void setNotifier(ProximityKitNotifier notifier)
   {
     this.notifier = notifier;
     if (getIBeaconManager() != null) {
       getIBeaconManager().setDataNotifier(notifier);
       getIBeaconManager().setMonitorNotifier(notifier);
     }
   }
 
   public ProximityKitNotifier getNotifier()
   {
     return this.notifier;
   }
 
   public Kit getKit()
   {
     return this.kit;
   }
 
   public void setKit(Kit kit)
   {
     this.kit = kit;
   }
 
   public IBeaconAdapter getIBeaconAdapter() {
     return this.iBeaconAdapter;
   }
 }

