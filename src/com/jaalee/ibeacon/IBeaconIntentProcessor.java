 package com.jaalee.ibeacon;
 
 import android.app.IntentService;
 import android.content.Intent;
 import android.os.Bundle;
 import android.util.Log;

import com.jaalee.ibeacon.service.IBeaconData;
import com.jaalee.ibeacon.service.MonitoringData;
import com.jaalee.ibeacon.service.RangingData;

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
 public class IBeaconIntentProcessor extends IntentService
 {
   private static final String TAG = "IBeaconIntentProcessor";
   private boolean initialized = false;
 
   public IBeaconIntentProcessor() {
     super("IBeaconIntentProcessor");
   }
 
   protected void onHandleIntent(Intent intent)
   {
     if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconIntentProcessor", "got an intent to process");
 
     MonitoringData monitoringData = null;
     RangingData rangingData = null;
 
     if ((intent != null) && (intent.getExtras() != null)) {
       monitoringData = (MonitoringData)intent.getExtras().get("monitoringData");
       rangingData = (RangingData)intent.getExtras().get("rangingData");
     }
 
     if (rangingData != null) {
       if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconIntentProcessor", "got ranging data");
       if (rangingData.getIBeacons() == null) {
         Log.w("IBeaconIntentProcessor", "Ranging data has a null iBeacons collection");
       }
       RangeNotifier notifier = IBeaconManager.getInstanceForApplication(this).getRangingNotifier();
       Collection iBeacons = IBeaconData.fromIBeaconDatas(rangingData.getIBeacons());
       if (notifier != null) {
         notifier.didRangeBeaconsInRegion(iBeacons, rangingData.getRegion());
       }
       else if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconIntentProcessor", "but ranging notifier is null, so we're dropping it.");
 
       RangeNotifier dataNotifier = IBeaconManager.getInstanceForApplication(this).getDataRequestNotifier();
       if (dataNotifier != null) {
         dataNotifier.didRangeBeaconsInRegion(iBeacons, rangingData.getRegion());
       }
     }
 
     if (monitoringData != null) {
       if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconIntentProcessor", "got monitoring data");
       MonitorNotifier notifier = IBeaconManager.getInstanceForApplication(this).getMonitoringNotifier();
       if (notifier != null) {
         if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconIntentProcessor", "Calling monitoring notifier:" + notifier);
         notifier.didDetermineStateForRegion(monitoringData.isInside() ? 1 : 0, monitoringData.getRegion());
         if (monitoringData.isInside()) {
           notifier.didEnterRegion(monitoringData.getRegion());
         }
         else
           notifier.didExitRegion(monitoringData.getRegion());
       }
     }
   }
 }

