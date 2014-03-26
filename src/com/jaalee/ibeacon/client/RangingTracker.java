 package com.jaalee.ibeacon.client;
 
 import android.util.Log;

import com.jaalee.ibeacon.IBeacon;
import com.jaalee.ibeacon.IBeaconManager;

 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
import java.util.Map;
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
 public class RangingTracker
 {
   private static String TAG = "RangingTracker";
   private Map<IBeacon, RangedIBeacon> rangedIBeacons = new HashMap();
 
   public void addIBeacon(IBeacon iBeacon) { if (this.rangedIBeacons.containsKey(iBeacon)) {
       RangedIBeacon rangedIBeacon = (RangedIBeacon)this.rangedIBeacons.get(iBeacon);
       if (IBeaconManager.LOG_DEBUG) Log.d(TAG, "adding " + iBeacon.getProximityUuid() + " to existing range for: " + rangedIBeacon.getProximityUuid());
       rangedIBeacon.addRangeMeasurement(Integer.valueOf(iBeacon.getRssi()));
     }
     else {
       if (IBeaconManager.LOG_DEBUG) Log.d(TAG, "adding " + iBeacon.getProximityUuid() + " to new rangedIBeacon");
       this.rangedIBeacons.put(iBeacon, new RangedIBeacon(iBeacon));
     } }
 
   public synchronized Collection<IBeacon> getIBeacons() {
     ArrayList iBeacons = new ArrayList();
     Iterator iterator = this.rangedIBeacons.values().iterator();
     while (iterator.hasNext()) {
       RangedIBeacon rangedIBeacon = (RangedIBeacon)iterator.next();
       if (!rangedIBeacon.allMeasurementsExpired()) {
         iBeacons.add(rangedIBeacon);
       }
     }
     return iBeacons;
   }
 }

