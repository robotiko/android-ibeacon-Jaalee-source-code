 package com.jaalee.ibeacon;
 
 import android.util.Log;

import com.jaalee.ibeacon.client.IBeaconDataFactory;
import com.jaalee.ibeacon.client.NullIBeaconDataFactory;
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
 public class IBeacon
 {
   public static final int PROXIMITY_IMMEDIATE = 1;
   public static final int PROXIMITY_NEAR = 2;
   public static final int PROXIMITY_FAR = 3;
   public static final int PROXIMITY_UNKNOWN = 0;
   private static final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
   private static final String TAG = "IBeacon";
   protected String proximityUuid;
   protected int major;
   protected int minor;
   protected Integer proximity;
   protected Double accuracy;
   protected int rssi;
   protected int txPower;
   protected Double runningAverageRssi = null;
 
   protected static IBeaconDataFactory iBeaconDataFactory = new NullIBeaconDataFactory();
 
   public double getAccuracy()
   {
     if (this.accuracy == null) {
       this.accuracy = Double.valueOf(calculateAccuracy(this.txPower, this.runningAverageRssi != null ? this.runningAverageRssi.doubleValue() : this.rssi));
     }
     return this.accuracy.doubleValue();
   }
 
   public int getMajor()
   {
     return this.major;
   }
 
   public int getMinor()
   {
     return this.minor;
   }
 
   public int getProximity()
   {
     if (this.proximity == null) {
       this.proximity = Integer.valueOf(calculateProximity(getAccuracy()));
     }
     return this.proximity.intValue();
   }
 
   public int getRssi()
   {
     return this.rssi;
   }
 
   public int getTxPower()
   {
     return this.txPower;
   }
 
   public String getProximityUuid()
   {
     return this.proximityUuid;
   }
 
   public int hashCode()
   {
     return this.minor;
   }
 
   public boolean equals(Object that)
   {
     if (!(that instanceof IBeacon)) {
       return false;
     }
     IBeacon thatIBeacon = (IBeacon)that;
     return (thatIBeacon.getMajor() == getMajor()) && (thatIBeacon.getMinor() == getMinor()) && (thatIBeacon.getProximityUuid().equals(getProximityUuid()));
   }
 
   public static IBeacon fromScanData(byte[] scanData, int rssi)
   {
     int startByte = 2;
     boolean patternFound = false;
     while (startByte <= 5) {
       if (((scanData[(startByte + 2)] & 0xFF) == 2) && ((scanData[(startByte + 3)] & 0xFF) == 21))
       {
         patternFound = true;
         break;
       }
       if (((scanData[startByte] & 0xFF) == 45) && ((scanData[(startByte + 1)] & 0xFF) == 36) && ((scanData[(startByte + 2)] & 0xFF) == 191) && ((scanData[(startByte + 3)] & 0xFF) == 22))
       {
         if (IBeaconManager.LOG_DEBUG) Log.d("IBeacon", "This is a proprietary Estimote beacon advertisement that does not meet the iBeacon standard.  Identifiers cannot be read.");
         IBeacon iBeacon = new IBeacon();
         iBeacon.major = 0;
         iBeacon.minor = 0;
         iBeacon.proximityUuid = "00000000-0000-0000-0000-000000000000";
         iBeacon.txPower = -55;
         return iBeacon;
       }
       if (((scanData[startByte] & 0xFF) == 173) && ((scanData[(startByte + 1)] & 0xFF) == 119) && ((scanData[(startByte + 2)] & 0xFF) == 0) && ((scanData[(startByte + 3)] & 0xFF) == 198))
       {
         if (IBeaconManager.LOG_DEBUG) Log.d("IBeacon", "This is a proprietary Gimbal beacon advertisement that does not meet the iBeacon standard.  Identifiers cannot be read.");
         IBeacon iBeacon = new IBeacon();
         iBeacon.major = 0;
         iBeacon.minor = 0;
         iBeacon.proximityUuid = "00000000-0000-0000-0000-000000000000";
         iBeacon.txPower = -55;
         return iBeacon;
       }
       startByte++;
     }
 
     if (!patternFound)
     {
       if (IBeaconManager.LOG_DEBUG) Log.d("IBeacon", "This is not an iBeacon advertisment (no 4c000215 seen in bytes 2-5).  The bytes I see are: " + bytesToHex(scanData));
       return null;
     }
 
     IBeacon iBeacon = new IBeacon();
 
     iBeacon.major = ((scanData[(startByte + 20)] & 0xFF) * 256 + (scanData[(startByte + 21)] & 0xFF));
     iBeacon.minor = ((scanData[(startByte + 22)] & 0xFF) * 256 + (scanData[(startByte + 23)] & 0xFF));
     iBeacon.txPower = scanData[(startByte + 24)];
     iBeacon.rssi = rssi;
 
     byte[] proximityUuidBytes = new byte[16];
     System.arraycopy(scanData, startByte + 4, proximityUuidBytes, 0, 16);
     String hexString = bytesToHex(proximityUuidBytes);
     StringBuilder sb = new StringBuilder();
     sb.append(hexString.substring(0, 8));
     sb.append("-");
     sb.append(hexString.substring(8, 12));
     sb.append("-");
     sb.append(hexString.substring(12, 16));
     sb.append("-");
     sb.append(hexString.substring(16, 20));
     sb.append("-");
     sb.append(hexString.substring(20, 32));
     iBeacon.proximityUuid = sb.toString();
 
     return iBeacon;
   }
 
   public void requestData(IBeaconDataNotifier notifier) {
     iBeaconDataFactory.requestIBeaconData(this, notifier);
   }
 
   protected IBeacon(IBeacon otherIBeacon) {
     this.major = otherIBeacon.major;
     this.minor = otherIBeacon.minor;
     this.accuracy = otherIBeacon.accuracy;
     this.proximity = otherIBeacon.proximity;
     this.rssi = otherIBeacon.rssi;
     this.proximityUuid = otherIBeacon.proximityUuid;
     this.txPower = otherIBeacon.txPower;
   }
 
   protected IBeacon()
   {
   }
 
   protected IBeacon(String proximityUuid, int major, int minor, int txPower, int rssi) {
     this.proximityUuid = proximityUuid;
     this.major = major;
     this.minor = minor;
     this.rssi = rssi;
     this.txPower = txPower;
   }
 
   public IBeacon(String proximityUuid, int major, int minor) {
     this.proximityUuid = proximityUuid;
     this.major = major;
     this.minor = minor;
     this.rssi = this.rssi;
     this.txPower = -59;
     this.rssi = 0;
   }
 
   protected static double calculateAccuracy(int txPower, double rssi) {
     if (rssi == 0.0D) {
       return -1.0D;
     }
 
     if (IBeaconManager.LOG_DEBUG) Log.d("IBeacon", "calculating accuracy based on rssi of " + rssi);
 
     double ratio = rssi * 1.0D / txPower;
     if (ratio < 1.0D) {
       return Math.pow(ratio, 10.0D);
     }
 
     double accuracy = 0.89976D * Math.pow(ratio, 7.7095D) + 0.111D;
     if (IBeaconManager.LOG_DEBUG) Log.d("IBeacon", " avg rssi: " + rssi + " accuracy: " + accuracy);
     return accuracy;
   }
 
   protected static int calculateProximity(double accuracy)
   {
     if (accuracy < 0.0D) {
       return 0;
     }
 
     if (accuracy < 0.5D) {
       return 1;
     }
 
     if (accuracy <= 4.0D) {
       return 2;
     }
 
     return 3;
   }
 
   private static String bytesToHex(byte[] bytes)
   {
     char[] hexChars = new char[bytes.length * 2];
 
     for (int j = 0; j < bytes.length; j++) {
       int v = bytes[j] & 0xFF;
       hexChars[(j * 2)] = hexArray[(v >>> 4)];
       hexChars[(j * 2 + 1)] = hexArray[(v & 0xF)];
     }
     return new String(hexChars);
   }
 }

