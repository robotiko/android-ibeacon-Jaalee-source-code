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
 public class RegionData extends Region
   implements Parcelable
 {
   public static final Parcelable.Creator<RegionData> CREATOR = new Parcelable.Creator<RegionData>()
   {
     public RegionData createFromParcel(Parcel in) {
       return new RegionData(in);
     }
 
     public RegionData[] newArray(int size) {
       return new RegionData[size];
     }
   };
 
   public RegionData(String uniqueId, String proximityUuid, Integer major, Integer minor)
   {
     super(uniqueId, proximityUuid, major, minor);
   }
   public RegionData(Region region) {
     super(region);
   }
 
   public int describeContents() {
     return 0;
   }
 
   public void writeToParcel(Parcel out, int flags) {
     out.writeInt(this.major == null ? -1 : this.major.intValue());
     out.writeInt(this.minor == null ? -1 : this.minor.intValue());
     out.writeString(this.proximityUuid);
     out.writeString(this.uniqueId);
   }
 
   private RegionData(Parcel in)
   {
     this.major = Integer.valueOf(in.readInt());
     if (this.major.intValue() == -1) {
       this.major = null;
     }
     this.minor = Integer.valueOf(in.readInt());
     if (this.minor.intValue() == -1) {
       this.minor = null;
     }
     this.proximityUuid = in.readString();
     this.uniqueId = in.readString();
   }
 }

