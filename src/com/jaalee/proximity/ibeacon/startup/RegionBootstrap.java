 package com.jaalee.proximity.ibeacon.startup;
 
 import android.content.Context;
 import android.content.Intent;
 import android.content.ServiceConnection;
 import android.os.RemoteException;
 import android.util.Log;

import com.jaalee.ibeacon.IBeaconConsumer;
import com.jaalee.ibeacon.Region;
import com.jaalee.proximity.ibeacon.IBeaconManager;
import com.jaalee.proximity.licensing.LicenseManager;

 import java.util.ArrayList;
import java.util.List;
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
 public class RegionBootstrap
 {
   protected static final String TAG = "AppStarter";
   private IBeaconManager iBeaconManager;
   private BootstrapNotifier application;
   private List<Region> regions;
   private boolean disabled = false;
   private IBeaconConsumer iBeaconConsumer;
 
   public RegionBootstrap(BootstrapNotifier application, Region region)
   {
     if (application.getApplicationContext() == null) {
       throw new NullPointerException("The BootstrapNotifier instance is returning null from its getApplicationContext() method.  Have you implemented this method?");
     }
     this.iBeaconManager = IBeaconManager.getInstanceForApplication(application.getApplicationContext());
     this.iBeaconManager.getLicenseManager().validateLicense();
 
     this.application = application;
     this.regions = new ArrayList();
     this.regions.add(region);
     this.iBeaconConsumer = new InternalIBeaconConsumer();
     this.iBeaconManager.bind(this.iBeaconConsumer);
     if (IBeaconManager.LOG_DEBUG) Log.d("AppStarter", "Waiting for iBeaconService connection");
   }
 
   public RegionBootstrap(BootstrapNotifier application, List<Region> regions)
   {
     if (application.getApplicationContext() == null) {
       throw new NullPointerException("The BootstrapNotifier instance is returning null from its getApplicationContext() method.  Have you implemented this method?");
     }
     this.iBeaconManager = IBeaconManager.getInstanceForApplication(application.getApplicationContext());
     this.iBeaconManager.getLicenseManager().validateLicense();
 
     this.application = application;
     this.regions = regions;
 
     this.iBeaconConsumer = new InternalIBeaconConsumer();
     this.iBeaconManager.bind(this.iBeaconConsumer);
     if (IBeaconManager.LOG_DEBUG) Log.d("AppStarter", "Waiting for iBeaconService connection");
   }
 
   public void disable()
   {
     if (this.disabled) {
       return;
     }
     this.disabled = true;
     try {
       for (Region region : this.regions)
         this.iBeaconManager.stopMonitoringBeaconsInRegion(region);
     }
     catch (RemoteException e) {
       Log.e("AppStarter", "Can't stop bootstrap regions due to " + e);
     }
     this.iBeaconManager.unBind(this.iBeaconConsumer);
   }
 
   private class InternalIBeaconConsumer implements IBeaconConsumer
   {
     private InternalIBeaconConsumer()
     {
     }
 
     public void onIBeaconServiceConnect() {
       if (IBeaconManager.LOG_DEBUG) Log.d("AppStarter", "Activating background region monitoring");
       RegionBootstrap.this.iBeaconManager.setMonitorNotifier(RegionBootstrap.this.application);
       try {
         for (Region region : RegionBootstrap.this.regions) {
           if (IBeaconManager.LOG_DEBUG) Log.d("AppStarter", "Background region monitoring activated for region " + region);
           RegionBootstrap.this.iBeaconManager.startMonitoringBeaconsInRegion(region);
           RegionBootstrap.this.iBeaconManager.setBackgroundMode(this, true);
         }
       } catch (RemoteException e) {
         Log.e("AppStarter", "Can't set up bootstrap regions due to " + e);
       }
     }
 
     public boolean bindService(Intent intent, ServiceConnection conn, int arg2)
     {
       return RegionBootstrap.this.application.getApplicationContext().bindService(intent, conn, arg2);
     }
 
     public Context getApplicationContext()
     {
       return RegionBootstrap.this.application.getApplicationContext();
     }
 
     public void unbindService(ServiceConnection conn)
     {
       RegionBootstrap.this.application.getApplicationContext().unbindService(conn);
     }
   }
 }

