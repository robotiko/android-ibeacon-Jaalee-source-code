 package com.jaalee.ibeacon.service;
 
 import android.os.Parcel;
 import android.os.Parcelable;
 import android.os.Parcelable.Creator;
 import android.util.Log;

import com.jaalee.ibeacon.IBeacon;
import com.jaalee.ibeacon.IBeaconManager;
import com.jaalee.ibeacon.Region;

 import java.util.ArrayList;
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
 public class RangingData
   implements Parcelable
 {
   private static final String TAG = "RangingData";
   private Collection<IBeaconData> iBeaconDatas;
   private RegionData regionData;
   public static final Parcelable.Creator<RangingData> CREATOR = new Parcelable.Creator<RangingData>()
   {
     public RangingData createFromParcel(Parcel in) {
       return new RangingData(in);
     }
 
     public RangingData[] newArray(int size) {
       return new RangingData[size];
     }
   };
 
   public RangingData(Collection<IBeacon> iBeacons, Region region)
   {
     this.iBeaconDatas = IBeaconData.fromIBeacons(iBeacons);
     this.regionData = new RegionData(region);
   }
 
   public RangingData(Collection<IBeaconData> iBeacons, RegionData region) {
     this.iBeaconDatas = iBeacons;
     this.regionData = region;
   }
   public Collection<IBeaconData> getIBeacons() {
     return this.iBeaconDatas;
   }
   public RegionData getRegion() {
     return this.regionData;
   }
 
   public int describeContents()
   {
     return 0;
   }
   public void writeToParcel(Parcel out, int flags) {
     if (IBeaconManager.LOG_DEBUG) Log.d("RangingData", "writing RangingData");
     out.writeParcelableArray((Parcelable[])this.iBeaconDatas.toArray(new Parcelable[0]), flags);
     out.writeParcelable(this.regionData, flags);
     if (IBeaconManager.LOG_DEBUG) Log.d("RangingData", "done writing RangingData");
   }
 
   private RangingData(Parcel in)
   {
     if (IBeaconManager.LOG_DEBUG) Log.d("RangingData", "parsing RangingData");
     Parcelable[] parcelables = in.readParcelableArray(getClass().getClassLoader());
     this.iBeaconDatas = new ArrayList(parcelables.length);
     for (int i = 0; i < parcelables.length; i++) {
       this.iBeaconDatas.add((IBeaconData)parcelables[i]);
     }
     this.regionData = ((RegionData)in.readParcelable(getClass().getClassLoader()));
   }
 }

