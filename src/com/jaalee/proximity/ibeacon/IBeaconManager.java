 package com.jaalee.proximity.ibeacon;
 
 import android.content.Context;
 import android.util.Log;

import com.jaalee.ibeacon.IBeacon;
import com.jaalee.ibeacon.IBeaconDataFactorySetter;
import com.jaalee.ibeacon.IBeaconDataNotifier;
import com.jaalee.ibeacon.MonitorNotifier;
import com.jaalee.ibeacon.RangeNotifier;
import com.jaalee.ibeacon.Region;
import com.jaalee.proximity.licensing.LicenseManager;

import java.util.Collection;
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
 public class IBeaconManager extends com.jaalee.ibeacon.IBeaconManager
 {
   private static final String TAG = "IBeaconManager_proximity";
   private LicenseManager licenseManager = null;
   protected IBeaconDataNotifier dataNotifier = null;
   Context context = null;
 
   public static IBeaconManager getInstanceForApplication(Context context) {
     if (client == null) {
       if (LOG_DEBUG) Log.d("IBeaconManager_proximity", "IBeaconManager instance creation");
       client = new IBeaconManager(context);
     }
     return (IBeaconManager)client;
   }
 
   private IBeaconManager(Context context) {
     super(context);
     this.context = context;
   }
 
   public LicenseManager getLicenseManager() {
     if (this.licenseManager == null) {
       this.licenseManager = constructLicenseManager();
     }
     return this.licenseManager;
   }
   private LicenseManager constructLicenseManager() {
     this.licenseManager = new LicenseManager(this.context);
 
     new IBeaconDataFactorySetter(this.licenseManager, this.context);
     return this.licenseManager;
   }
 
   public void licenseChanged(Context context)
   {
     this.licenseManager = null;
     getLicenseManager();
   }
 
   public void setDataNotifier(IBeaconDataNotifier notifier)
   {
     Log.d("IBeaconManager_proximity", "IBeaconManager.dataNotifier set to " + notifier);
 
     this.dataNotifier = notifier;
     if (this.dataNotifier == null) {
       Log.d("IBeaconManager_proximity", "Disabling data requests");
       super.setDataRequestNotifier(null);
     }
     else {
       super.setDataRequestNotifier(new RangeNotifier()
       {
         public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region)
         {
           for (IBeacon iBeacon : iBeacons) {
             Log.d("IBeaconManager_proximity", "Requesting data for iBeacon with dataNotifier:" + IBeaconManager.this.dataNotifier);
             iBeacon.requestData(IBeaconManager.this.dataNotifier);
           }
         }
       });
     }
   }
 
   public IBeaconDataNotifier getDataNotifier()
   {
     return this.dataNotifier;
   }
 
   public MonitorNotifier getMonitorNotifier() {
     return this.monitorNotifier;
   }
 }

