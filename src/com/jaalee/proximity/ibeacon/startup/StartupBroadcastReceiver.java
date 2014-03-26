 package com.jaalee.proximity.ibeacon.startup;
 
 import android.content.BroadcastReceiver;
 import android.content.Context;
 import android.content.Intent;
import android.os.Build;
 import android.os.Build.VERSION;
 import android.util.Log;

import com.jaalee.ibeacon.IBeaconIntentProcessor;
import com.jaalee.ibeacon.service.IBeaconService;
import com.jaalee.proximity.ibeacon.IBeaconManager;
import com.jaalee.proximity.licensing.LicenseManager;
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
 public class StartupBroadcastReceiver extends BroadcastReceiver
 {
   private static final String TAG = "StartupBroadcastReceiver";
   private Context context;
 
   public void onReceive(Context context, Intent intent)
   {
     if (IBeaconManager.LOG_DEBUG) Log.d("StartupBroadcastReceiver", "onReceive called in startup broadcast receiver");
     if (Build.VERSION.SDK_INT < 18) {
       Log.w("StartupBroadcastReceiver", "Not starting up iBeacon service because we do not have SDK version 18 (Android 4.3).  We have: " + Build.VERSION.SDK_INT);
       return;
     }
     IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(context.getApplicationContext());
     iBeaconManager.getLicenseManager().validateLicense();
 
     Intent startServiceIntent = new Intent(context, IBeaconService.class);
     context.startService(startServiceIntent);
     startServiceIntent = new Intent(context, IBeaconIntentProcessor.class);
     context.startService(startServiceIntent);
   }
 }

