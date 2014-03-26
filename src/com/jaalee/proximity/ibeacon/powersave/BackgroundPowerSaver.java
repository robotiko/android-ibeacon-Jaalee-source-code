 package com.jaalee.proximity.ibeacon.powersave;
 
 import android.app.Activity;
 import android.app.Application;
 import android.app.Application.ActivityLifecycleCallbacks;
 import android.os.Bundle;
 import android.util.Log;

import com.jaalee.ibeacon.IBeaconConsumer;
import com.jaalee.proximity.ProximityKitManager;
import com.jaalee.proximity.ProximityKitNotifier;
import com.jaalee.proximity.ibeacon.IBeaconManager;
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
 public class BackgroundPowerSaver
   implements Application.ActivityLifecycleCallbacks
 {
   private static final String TAG = "BackgroundPowerSaver";
   private IBeaconManager iBeaconManager;
   private int activeActivityCount = 0;
   private IBeaconConsumer applicationConsumer;
 
   public BackgroundPowerSaver(Application application)
   {
     if ((application instanceof IBeaconConsumer)) {
       Log.d("BackgroundPowerSaver", "Background power saver started.  Application " + application + " is an IBeaconConsumer");
       this.applicationConsumer = ((IBeaconConsumer)application);
     }
     else if ((application instanceof ProximityKitNotifier)) {
       Log.d("BackgroundPowerSaver", "Background power saver started.  Application " + application + " is a ProximityKitNotifier.");
       this.applicationConsumer = ProximityKitManager.getInstanceForApplication(application).getIBeaconAdapter();
     }
     else {
       Log.d("BackgroundPowerSaver", "Background power saver started.  Application " + application + " is not an IBeaconConsumer");
     }
     application.registerActivityLifecycleCallbacks(this);
     this.iBeaconManager = IBeaconManager.getInstanceForApplication(application);
   }
 
   public void onActivityCreated(Activity activity, Bundle bundle)
   {
   }
 
   public void onActivityStarted(Activity activity)
   {
   }
 
   public void onActivityResumed(Activity activity)
   {
     this.activeActivityCount += 1;
     Log.d("BackgroundPowerSaver", "activity resumed: " + activity + "  active activities: " + this.activeActivityCount);
     try {
       IBeaconConsumer consumerActivity = (IBeaconConsumer)activity;
       if (this.iBeaconManager.isBound(consumerActivity)) this.iBeaconManager.setBackgroundMode(consumerActivity, false); 
     }
     catch (ClassCastException e) {  }
 
     if (this.iBeaconManager.isBound(this.applicationConsumer)) {
       Log.d("BackgroundPowerSaver", "application is bound -- going into foreground");
       this.iBeaconManager.setBackgroundMode(this.applicationConsumer, false);
     }
   }
 
   public void onActivityPaused(Activity activity)
   {
     this.activeActivityCount -= 1;
     Log.d("BackgroundPowerSaver", "activity paused: " + activity + "  active activities: " + this.activeActivityCount);
     try {
       IBeaconConsumer consumerActivity = (IBeaconConsumer)activity;
       if (this.iBeaconManager.isBound(consumerActivity)) this.iBeaconManager.setBackgroundMode(consumerActivity, true); 
     }
     catch (ClassCastException e) {  }
 
     if ((this.iBeaconManager.isBound(this.applicationConsumer)) && (this.activeActivityCount < 1)) {
       Log.d("BackgroundPowerSaver", "application is bound -- going into background");
       this.iBeaconManager.setBackgroundMode(this.applicationConsumer, true);
     }
   }
 
   public void onActivityStopped(Activity activity)
   {
   }
 
   public void onActivitySaveInstanceState(Activity activity, Bundle bundle)
   {
   }
 
   public void onActivityDestroyed(Activity activity)
   {
   }
 }

