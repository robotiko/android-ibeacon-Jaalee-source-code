 package com.jaalee.ibeacon.service;
 
 import android.os.Parcel;
 import android.os.Parcelable;
import android.os.Parcelable.Creator;
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
 public class StartRMData
   implements Parcelable
 {
   private RegionData regionData;
   private long scanPeriod;
   private long betweenScanPeriod;
   private String callbackPackageName;
   public static final Parcelable.Creator<StartRMData> CREATOR = new Parcelable.Creator<StartRMData>()
   {
     public StartRMData createFromParcel(Parcel in) {
       return new StartRMData(in);
     }
 
     public StartRMData[] newArray(int size) {
       return new StartRMData[size];
     }
   };
 
   public StartRMData(RegionData regionData, String callbackPackageName)
   {
     this.regionData = regionData;
     this.callbackPackageName = callbackPackageName;
   }
   public StartRMData(long scanPeriod, long betweenScanPeriod) {
     this.scanPeriod = scanPeriod;
     this.betweenScanPeriod = betweenScanPeriod;
   }
 
   public StartRMData(RegionData regionData, String callbackPackageName, long scanPeriod, long betweenScanPeriod) {
     this.scanPeriod = scanPeriod;
     this.betweenScanPeriod = betweenScanPeriod;
     this.regionData = regionData;
     this.callbackPackageName = callbackPackageName;
   }
 
   public long getScanPeriod() {
     return this.scanPeriod; } 
   public long getBetweenScanPeriod() { return this.betweenScanPeriod; } 
   public RegionData getRegionData() {
     return this.regionData;
   }
   public String getCallbackPackageName() {
     return this.callbackPackageName;
   }
   public int describeContents() {
     return 0;
   }
 
   public void writeToParcel(Parcel out, int flags) {
     out.writeParcelable(this.regionData, flags);
     out.writeString(this.callbackPackageName);
     out.writeLong(this.scanPeriod);
     out.writeLong(this.betweenScanPeriod);
   }
 
   private StartRMData(Parcel in)
   {
     this.regionData = ((RegionData)in.readParcelable(getClass().getClassLoader()));
     this.callbackPackageName = in.readString();
     this.scanPeriod = in.readLong();
     this.betweenScanPeriod = in.readLong();
   }
 }

