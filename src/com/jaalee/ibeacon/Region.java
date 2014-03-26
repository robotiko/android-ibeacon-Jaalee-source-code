 package com.jaalee.ibeacon;
 
 import android.util.Log;
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
 public class Region
 {
   private static final String TAG = "Region";
   protected Integer major;
   protected Integer minor;
   protected String proximityUuid;
   protected String uniqueId;
 
   public Region(String uniqueId, String proximityUuid, Integer major, Integer minor)
   {
     this.major = major;
     this.minor = minor;
     this.proximityUuid = normalizeProximityUuid(proximityUuid);
     this.uniqueId = uniqueId;
     if (uniqueId == null)
       throw new NullPointerException("uniqueId may not be null");
   }
 
   public Integer getMajor()
   {
     return this.major;
   }
 
   public Integer getMinor()
   {
     return this.minor;
   }
 
   public String getProximityUuid()
   {
     return this.proximityUuid;
   }
 
   public String getUniqueId()
   {
     return this.uniqueId;
   }
 
   public boolean matchesIBeacon(IBeacon iBeacon)
   {
     if ((this.proximityUuid != null) && (!iBeacon.getProximityUuid().equals(this.proximityUuid))) {
       if (IBeaconManager.LOG_DEBUG) Log.d("Region", "unmatching proxmityUuids: " + iBeacon.getProximityUuid() + " != " + this.proximityUuid);
       return false;
     }
     if ((this.major != null) && (iBeacon.getMajor() != this.major.intValue())) {
       if (IBeaconManager.LOG_DEBUG) Log.d("Region", "unmatching major: " + iBeacon.getMajor() + " != " + this.major);
       return false;
     }
     if ((this.minor != null) && (iBeacon.getMinor() != this.minor.intValue())) {
       if (IBeaconManager.LOG_DEBUG) Log.d("Region", "unmatching minor: " + iBeacon.getMajor() + " != " + this.minor);
       return false;
     }
     return true;
   }
 
   protected Region(Region otherRegion) {
     this.major = otherRegion.major;
     this.minor = otherRegion.minor;
     this.proximityUuid = otherRegion.proximityUuid;
     this.uniqueId = otherRegion.uniqueId;
   }
 
   protected Region()
   {
   }
 
   public int hashCode() {
     return this.uniqueId.hashCode();
   }
 
   public boolean equals(Object other) {
     if ((other instanceof Region)) {
       return ((Region)other).uniqueId.equals(this.uniqueId);
     }
     return false;
   }
 
   public String toString() {
     return "proximityUuid: " + this.proximityUuid + " major: " + this.major + " minor:" + this.minor;
   }
 
   public static String normalizeProximityUuid(String proximityUuid)
   {
     if (proximityUuid == null) {
       return null;
     }
     String dashlessUuid = proximityUuid.toLowerCase().replaceAll("[\\-\\s]", "");
     if (dashlessUuid.length() != 32)
     {
       throw new RuntimeException("UUID: " + proximityUuid + " is too short.  Must contain exactly 32 hex digits, and there are this value has " + dashlessUuid.length() + " digits.");
     }
     if (!dashlessUuid.matches("^[a-fA-F0-9]*$"))
     {
       throw new RuntimeException("UUID: " + proximityUuid + " contains invalid characters.  Must be dashes, a-f and 0-9 characters only.");
     }
     StringBuilder sb = new StringBuilder();
     sb.append(dashlessUuid.substring(0, 8));
     sb.append('-');
     sb.append(dashlessUuid.substring(8, 12));
     sb.append('-');
     sb.append(dashlessUuid.substring(12, 16));
     sb.append('-');
     sb.append(dashlessUuid.substring(16, 20));
     sb.append('-');
     sb.append(dashlessUuid.substring(20, 32));
     return sb.toString();
   }
 
   public Object clone()
   {
     return new Region(this);
   }
 }

