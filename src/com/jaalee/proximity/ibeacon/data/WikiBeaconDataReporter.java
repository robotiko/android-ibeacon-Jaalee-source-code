 package com.jaalee.proximity.ibeacon.data;
 
 import android.content.Context;
 import android.location.Location;
 import android.location.LocationListener;
 import android.location.LocationManager;
 import android.os.Bundle;
 import android.os.Environment;
 import android.util.Log;

import com.jaalee.ibeacon.IBeacon;
import com.jaalee.ibeacon.IBeaconManager;

 import java.io.File;
 import java.io.FileOutputStream;
 import java.util.Date;
import java.util.HashMap;
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
 public class WikiBeaconDataReporter
   implements LocationListener
 {
   private static final String TAG = "WikiBeaconDataReporter";
   private Location lastLocation;
   private int gpsLocationStatus;
   private int networkLocationStatus;
   private Context context;
   private HashMap<IBeacon, Location> cache;
 
   public WikiBeaconDataReporter(Context context)
   {
     this.context = context;
 
     LocationManager mlocManager = (LocationManager)context.getSystemService("location");
     try {
       mlocManager.requestLocationUpdates("gps", 5000L, 50.0F, this);
     } catch (IllegalArgumentException e) {
     }
     try {
       mlocManager.requestLocationUpdates("network", 5000L, 50.0F, this);
     }
     catch (IllegalArgumentException e) {
     }
     this.cache = new HashMap();
   }
 
   public boolean updateWikiIBeaconData(IBeacon iBeacon) {
     if (this.lastLocation != null) {
       long fixAge = new Date().getTime() - this.lastLocation.getTime();
 
       if ((this.gpsLocationStatus == 2) || (this.networkLocationStatus == 2) || (fixAge < 60000L))
       {
         if (this.cache.get(iBeacon) != null) {
           long cachedAge = new Date().getTime() - ((Location)this.cache.get(iBeacon)).getTime();
 
           if (cachedAge < 60000L) {
             if (IBeaconManager.LOG_DEBUG) Log.d("WikiBeaconDataReporter", "Not processing wikiBeacon because we did so for this same device within the last 10 minutes");
             return false;
           }
         }
         if (IBeaconManager.LOG_DEBUG) Log.d("WikiBeaconDataReporter", "Upoading wikibeacon hit");
         saveWikiData(iBeacon, this.lastLocation);
         this.cache.put(iBeacon, this.lastLocation);
         return true;
       }
 
       if (IBeaconManager.LOG_DEBUG) Log.d("WikiBeaconDataReporter", "Not updating wikiBeacon because gps location status is " + this.gpsLocationStatus + ", network location status is " + this.networkLocationStatus + " and the age of the fix is " + fixAge + " milliseconds.");
 
     }
     else if (IBeaconManager.LOG_DEBUG) { Log.d("WikiBeaconDataReporter", "Not updating wikiBeacon because location is not available"); }
 
     return false;
   }
 
   private void saveWikiData(IBeacon iBeacon, Location location)
   {
     FileOutputStream outputStream = null;
 
     String string = iBeacon.getProximityUuid() + "," + iBeacon.getMajor() + "," + iBeacon.getMinor() + "," + location.getLatitude() + "," + location.getLongitude() + "," + location.getAccuracy() + "," + location.getTime() + "\n";
     try
     {
       File path = Environment.getExternalStoragePublicDirectory("wikiBeacon");
 
       File file = new File(path, "log.csv");
       boolean result = path.mkdirs();
       if (IBeaconManager.LOG_DEBUG) Log.d("WikiBeaconDataReporter", "mkdirs on " + path + " returned " + result);
       outputStream = new FileOutputStream(file, true);
       outputStream.write(string.getBytes());
       outputStream.close();
     } catch (Exception e) {
       Log.e("WikiBeaconDataReporter", "can't write wikibeacon data to file ", e);
     }
     finally {
       try {
         if (outputStream != null) outputStream.close(); 
       } catch (Exception e) {  }
 
     }
     if (IBeaconManager.LOG_DEBUG) Log.d("WikiBeaconDataReporter", "Wrote data to " + this.context.getFilesDir()); 
   }
 
   public void onLocationChanged(Location loc)
   {
     this.lastLocation = loc;
   }
 
   public void onProviderDisabled(String arg0)
   {
   }
 
   public void onProviderEnabled(String arg0)
   {
   }
 
   public void onStatusChanged(String provider, int status, Bundle arg2)
   {
     if (provider == "gps") {
       this.gpsLocationStatus = status;
     }
 
     if (provider == "network")
       this.networkLocationStatus = status;
   }
 }

