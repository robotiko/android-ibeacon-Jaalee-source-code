 package com.jaalee.proximity.ibeacon.data.proximitykit;
 
 import com.jaalee.ibeacon.IBeacon;
import com.jaalee.ibeacon.IBeaconData;
import com.jaalee.ibeacon.IBeaconDataNotifier;
import com.jaalee.proximity.model.KitIBeacon;

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
 public class PkIBeaconData
   implements IBeaconData
 {
   private KitIBeacon pkIBeacon;
   private ProximityKitPersister persister;
   private IBeacon iBeacon;
 
   public PkIBeaconData(KitIBeacon kitIBeacon, ProximityKitPersister persister, IBeacon iBeacon)
   {
     this.pkIBeacon = kitIBeacon;
     this.persister = persister;
     this.iBeacon = iBeacon;
   }
 
   public Double getLatitude()
   {
     return this.pkIBeacon.getLatitude();
   }
 
   public void setLatitude(Double latitude)
   {
     throw new UnsupportedOperationException("iBeacon data is currently read-only");
   }
 
   public void setLongitude(Double longitude)
   {
     throw new UnsupportedOperationException("iBeacon data is currently read-only");
   }
 
   public Double getLongitude()
   {
     return this.pkIBeacon.getLongitude();
   }
 
   public String get(String key)
   {
     return (String)this.pkIBeacon.getAttributes().get(key);
   }
 
   public void set(String key, String value)
   {
     throw new UnsupportedOperationException("iBeacon data is currently read-only");
   }
 
   public void sync(IBeaconDataNotifier notifier)
   {
     this.persister.loadFromCloud(this.iBeacon, notifier);
   }
 
   public boolean isDirty()
   {
     return false;
   }
 }

