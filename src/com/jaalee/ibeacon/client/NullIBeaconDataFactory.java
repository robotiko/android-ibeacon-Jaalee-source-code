 package com.jaalee.ibeacon.client;
 
 import android.os.Handler;

import com.jaalee.ibeacon.IBeacon;
import com.jaalee.ibeacon.IBeaconDataNotifier;
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
 public class NullIBeaconDataFactory
   implements IBeaconDataFactory
 {
   public void requestIBeaconData(IBeacon iBeacon, final IBeaconDataNotifier notifier)
   {
     Handler handler = new Handler();
     handler.post(new Runnable()
     {
       public void run() {
         notifier.iBeaconDataUpdate(null, null, new DataProviderException("Please upgrade to the Pro version of the Android iBeacon Library."));
       }
     });
   }
 }

