 package com.jaalee.proximity.ibeacon.data;
 
 import android.content.Context;
 import android.util.Log;

import com.jaalee.ibeacon.IBeacon;
import com.jaalee.ibeacon.IBeaconData;
import com.jaalee.ibeacon.IBeaconDataNotifier;
import com.jaalee.ibeacon.client.IBeaconDataFactory;
import com.jaalee.proximity.ProximityKitManager;
import com.jaalee.proximity.ibeacon.data.proximitykit.ProximityKitPersister;
import com.jaalee.proximity.licensing.LicenseManager;
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
 public class CloudIBeaconDataFactory
   implements IBeaconDataFactory
 {
   private static final String TAG = "IBeaconDataFactory";
   private LicenseManager licenseManager = null;
   private Persister persister;
   private int configuredBackend;
   public static int OPEN_KEYVAL_BACKEND = 0;
   public static int PROXIMITY_KIT_BACKEND = 1;
   private Context context;
 
   public CloudIBeaconDataFactory(LicenseManager licenseManager, Context context, int configuredBackend)
   {
     this.licenseManager = licenseManager;
     this.context = context;
     if (configuredBackend == OPEN_KEYVAL_BACKEND) {
       throw new RuntimeException("OpenKeyval is no longer supported");
     }
     if (configuredBackend == PROXIMITY_KIT_BACKEND) {
       this.persister = new ProximityKitPersister(ProximityKitManager.getInstanceForApplication(context), licenseManager, context);
     }
     else
       throw new RuntimeException("Unrecognized backend parameter");
   }
 
   public void requestIBeaconData(IBeacon iBeacon, IBeaconDataNotifier notifier)
   {
     this.licenseManager.validateLicense();
     IBeaconData data = this.persister.loadFromCache(iBeacon);
     if (data != null) {
       if (notifier == null) {
         Log.w("IBeaconDataFactory", "requestIBeaconData called with a null notifier");
       }
       else {
         notifier.iBeaconDataUpdate(iBeacon, data, null);
       }
       return;
     }
 
     this.persister.loadFromCloud(iBeacon, notifier);
   }
 }

