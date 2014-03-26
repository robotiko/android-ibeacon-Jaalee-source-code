 package com.jaalee.ibeacon.service;
 
 import android.os.Parcel;
 import android.os.Parcelable;
 import android.os.Parcelable.Creator;

import com.jaalee.ibeacon.Region;
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
 public class MonitoringData
   implements Parcelable
 {
   private static final String TAG = "MonitoringData";
   private boolean inside;
   private RegionData regionData;
   public static final Parcelable.Creator<MonitoringData> CREATOR = new Parcelable.Creator<MonitoringData>()
   {
     public MonitoringData createFromParcel(Parcel in) {
       return new MonitoringData(in);
     }
 
     public MonitoringData[] newArray(int size) {
       return new MonitoringData[size];
     }
   };
 
   public MonitoringData(boolean inside, Region region)
   {
     this.inside = inside;
     this.regionData = new RegionData(region);
   }
   public boolean isInside() {
     return this.inside;
   }
   public Region getRegion() {
     return this.regionData;
   }
 
   public int describeContents()
   {
     return 0;
   }
   public void writeToParcel(Parcel out, int flags) {
     out.writeByte((byte)(this.inside ? 1 : 0));
     out.writeParcelable(this.regionData, flags);
   }
 
   private MonitoringData(Parcel in)
   {
     this.inside = (in.readByte() == 1);
     this.regionData = ((RegionData)in.readParcelable(getClass().getClassLoader()));
   }
 }

