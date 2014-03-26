 package com.jaalee.ibeacon.service;
 
 import com.jaalee.ibeacon.IBeacon;

 import java.util.HashSet;
import java.util.Set;
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
 public class RangeState
 {
   private Callback callback;
   private Set<IBeacon> iBeacons = new HashSet();
 
   public RangeState(Callback c) {
     this.callback = c;
   }
 
   public Callback getCallback() {
     return this.callback;
   }
   public void clearIBeacons() {
     this.iBeacons.clear();
   }
   public Set<IBeacon> getIBeacons() {
     return this.iBeacons;
   }
   public void addIBeacon(IBeacon iBeacon) {
     this.iBeacons.add(iBeacon);
   }
 }

