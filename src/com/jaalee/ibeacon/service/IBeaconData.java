 package com.jaalee.ibeacon.service;
 
 import android.os.Parcel;
 import android.os.Parcelable;
 import android.os.Parcelable.Creator;

import com.jaalee.ibeacon.IBeacon;

 import java.util.ArrayList;
 import java.util.Collection;
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
 public class IBeaconData extends IBeacon
   implements Parcelable
 {
   public static final Parcelable.Creator<IBeaconData> CREATOR = new Parcelable.Creator<IBeaconData>()
   {
     public IBeaconData createFromParcel(Parcel in) {
       return new IBeaconData(in);
     }
 
     public IBeaconData[] newArray(int size) {
       return new IBeaconData[size];
     }
   };
 
   public IBeaconData(IBeacon iBeacon)
   {
     super(iBeacon);
   }
   public static Collection<IBeaconData> fromIBeacons(Collection<IBeacon> iBeacons) {
     ArrayList iBeaconDatas = new ArrayList();
     Iterator iBeaconIterator = iBeacons.iterator();
     while (iBeaconIterator.hasNext()) {
       iBeaconDatas.add(new IBeaconData((IBeacon)iBeaconIterator.next()));
     }
     return iBeaconDatas;
   }
   public static Collection<IBeacon> fromIBeaconDatas(Collection<IBeaconData> iBeaconDatas) {
     ArrayList iBeacons = new ArrayList();
     if (iBeaconDatas != null) {
       Iterator iBeaconIterator = iBeaconDatas.iterator();
       while (iBeaconIterator.hasNext()) {
         iBeacons.add(iBeaconIterator.next());
       }
     }
     return iBeacons;
   }
 
   public int describeContents() {
     return 0;
   }
 
   public void writeToParcel(Parcel out, int flags) {
     out.writeInt(this.major);
     out.writeInt(this.minor);
     out.writeString(this.proximityUuid);
     out.writeInt(getProximity());
     out.writeDouble(getAccuracy());
     out.writeInt(this.rssi);
     out.writeInt(this.txPower);
   }
 
   private IBeaconData(Parcel in)
   {
     this.major = in.readInt();
     this.minor = in.readInt();
     this.proximityUuid = in.readString();
     this.proximity = Integer.valueOf(in.readInt());
     this.accuracy = Double.valueOf(in.readDouble());
     this.rssi = in.readInt();
     this.txPower = in.readInt();
   }
 }

