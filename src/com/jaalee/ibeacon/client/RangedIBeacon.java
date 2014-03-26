 package com.jaalee.ibeacon.client;
 
 import android.util.Log;

import com.jaalee.ibeacon.IBeacon;
import com.jaalee.ibeacon.IBeaconManager;

 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Date;
import java.util.Iterator;
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
 public class RangedIBeacon extends IBeacon
 {
   private static String TAG = "RangedIBeacon";
   public static long DEFAULT_SAMPLE_EXPIRATION_MILLISECONDS = 5000L;
   private long sampleExpirationMilliseconds = DEFAULT_SAMPLE_EXPIRATION_MILLISECONDS;
 
   private ArrayList<Measurement> measurements = new ArrayList();
 
   public RangedIBeacon(IBeacon ibeacon)
   {
     super(ibeacon);
     addMeasurement(Integer.valueOf(this.rssi));
   }
 
   public void setSampleExpirationMilliseconds(long milliseconds) {
     this.sampleExpirationMilliseconds = milliseconds;
   }
   public void addMeasurement(Integer rssi) {
     Measurement measurement = new Measurement();
     measurement.rssi = rssi;
     measurement.timestamp = new Date().getTime();
     this.measurements.add(measurement);
   }
 
   public boolean allMeasurementsExpired()
   {
     refreshMeasurements();
     return this.measurements.size() == 0;
   }
 
   private synchronized void refreshMeasurements()
   {
     Date now = new Date();
     ArrayList newMeasurements = new ArrayList();
     Iterator iterator = this.measurements.iterator();
     while (iterator.hasNext()) {
       Measurement measurement = (Measurement)iterator.next();
       if (now.getTime() - measurement.timestamp < this.sampleExpirationMilliseconds) {
         newMeasurements.add(measurement);
       }
     }
     this.measurements = newMeasurements;
     Collections.sort(this.measurements);
   }
 
   private double calculateRunningAverage() {
     refreshMeasurements();
     int size = this.measurements.size();
     int startIndex = 0;
     int endIndex = size - 1;
     if (size > 2) {
       startIndex = size / 10 + 1;
       endIndex = size - size / 10 - 2;
     }
 
     int sum = 0;
     for (int i = startIndex; i <= endIndex; i++) {
       sum += ((Measurement)this.measurements.get(i)).rssi.intValue();
     }
     double runningAverage = sum / (endIndex - startIndex + 1);
 
     if (IBeaconManager.LOG_DEBUG) Log.d(TAG, "Running average rssi based on " + size + " measurements: " + runningAverage);
     return runningAverage;
   }
 
   protected void addRangeMeasurement(Integer rssi)
   {
     this.rssi = rssi.intValue();
     addMeasurement(rssi);
     if (IBeaconManager.LOG_DEBUG) Log.d(TAG, "calculating new range measurement with new rssi measurement:" + rssi);
     this.runningAverageRssi = Double.valueOf(calculateRunningAverage());
     this.accuracy = null;
     this.proximity = null;
   }
 
   private class Measurement
     implements Comparable<Measurement>
   {
     Integer rssi;
     long timestamp;
 
     private Measurement()
     {
     }
 
     public int compareTo(Measurement arg0)
     {
       return this.rssi.compareTo(arg0.rssi);
     }
   }
 }

