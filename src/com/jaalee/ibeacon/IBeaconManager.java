 package com.jaalee.ibeacon;
 
 import android.bluetooth.BluetoothAdapter;
 import android.bluetooth.BluetoothManager;
 import android.content.ComponentName;
 import android.content.Context;
 import android.content.Intent;
 import android.content.ServiceConnection;
 import android.content.pm.PackageManager;
 import android.os.IBinder;
 import android.os.Message;
 import android.os.Messenger;
 import android.os.RemoteException;
 import android.util.Log;

import com.jaalee.ibeacon.service.IBeaconService;
import com.jaalee.ibeacon.service.RegionData;
import com.jaalee.ibeacon.service.StartRMData;

 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
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
 public class IBeaconManager
 {
   private static final String TAG = "IBeaconManager";
   private Context context;
   protected static IBeaconManager client = null;
   private Map<IBeaconConsumer, ConsumerInfo> consumers = new HashMap();
   private Messenger serviceMessenger = null;
   protected RangeNotifier rangeNotifier = null;
   protected RangeNotifier dataRequestNotifier = null;
   protected MonitorNotifier monitorNotifier = null;
   private ArrayList<Region> monitoredRegions = new ArrayList();
   private ArrayList<Region> rangedRegions = new ArrayList();
 
   public static boolean LOG_DEBUG = false;
   public static final long DEFAULT_FOREGROUND_SCAN_PERIOD = 1100L;
   public static final long DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD = 0L;
   public static final long DEFAULT_BACKGROUND_SCAN_PERIOD = 10000L;
   public static final long DEFAULT_BACKGROUND_BETWEEN_SCAN_PERIOD = 300000L;
   private long foregroundScanPeriod = 1100L;
   private long foregroundBetweenScanPeriod = 0L;
   private long backgroundScanPeriod = 10000L;
   private long backgroundBetweenScanPeriod = 300000L;
 
   private ServiceConnection iBeaconServiceConnection = new ServiceConnection()
   {
     public void onServiceConnected(ComponentName className, IBinder service) {
       if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconManager", "we have a connection to the service now");
       IBeaconManager.this.serviceMessenger = new Messenger(service);
       synchronized (IBeaconManager.this.consumers) {
         Iterator consumerIterator = IBeaconManager.this.consumers.keySet().iterator();
         while (consumerIterator.hasNext()) {
           IBeaconConsumer consumer = (IBeaconConsumer)consumerIterator.next();
           Boolean alreadyConnected = Boolean.valueOf(((IBeaconManager.ConsumerInfo)IBeaconManager.this.consumers.get(consumer)).isConnected);
           if (!alreadyConnected.booleanValue()) {
             consumer.onIBeaconServiceConnect();
             IBeaconManager.ConsumerInfo consumerInfo = (IBeaconManager.ConsumerInfo)IBeaconManager.this.consumers.get(consumer);
             consumerInfo.isConnected = true;
             IBeaconManager.this.consumers.put(consumer, consumerInfo);
           }
         }
       }
     }
 
     public void onServiceDisconnected(ComponentName className)
     {
       Log.e("IBeaconManager", "onServiceDisconnected");
     }
   };
 
   public void setForegroundScanPeriod(long p)
   {
     this.foregroundScanPeriod = p;
   }
 
   public void setForegroundBetweenScanPeriod(long p)
   {
     this.foregroundBetweenScanPeriod = p;
   }
 
   public void setBackgroundScanPeriod(long p)
   {
     this.backgroundScanPeriod = p;
   }
 
   public void setBackgroundBetweenScanPeriod(long p)
   {
     this.backgroundBetweenScanPeriod = p;
   }
 
   public static IBeaconManager getInstanceForApplication(Context context)
   {
     if (client == null) {
       if (LOG_DEBUG) Log.d("IBeaconManager", "IBeaconManager instance creation");
       client = new IBeaconManager(context);
     }
     return client;
   }
 
   protected IBeaconManager(Context context) {
     this.context = context;
   }
 
   public boolean checkAvailability()
   {
     if (!this.context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
       throw new BleNotAvailableException("Bluetooth LE not supported by this device");
     }
 
     if (((BluetoothManager)this.context.getSystemService("bluetooth")).getAdapter().isEnabled()) {
       return true;
     }
 
     return false;
   }
 
   public void bind(IBeaconConsumer consumer)
   {
     synchronized (this.consumers) {
       if (this.consumers.keySet().contains(consumer)) {
         if (LOG_DEBUG) Log.d("IBeaconManager", "This consumer is already bound"); 
       }
       else
       {
         if (LOG_DEBUG) Log.d("IBeaconManager", "This consumer is not bound.  binding: " + consumer);
         this.consumers.put(consumer, new ConsumerInfo());
         Intent intent = new Intent(consumer.getApplicationContext(), IBeaconService.class);
         consumer.bindService(intent, this.iBeaconServiceConnection, 1);
         if (LOG_DEBUG) Log.d("IBeaconManager", "consumer count is now:" + this.consumers.size());
         if (this.serviceMessenger != null)
           setBackgroundMode(consumer, false);
       }
     }
   }
 
   public void unBind(IBeaconConsumer consumer)
   {
     synchronized (this.consumers) {
       if (this.consumers.keySet().contains(consumer)) {
         Log.d("IBeaconManager", "Unbinding");
         consumer.unbindService(this.iBeaconServiceConnection);
         this.consumers.remove(consumer);
       }
       else {
         if (LOG_DEBUG) Log.d("IBeaconManager", "This consumer is not bound to: " + consumer);
         if (LOG_DEBUG) Log.d("IBeaconManager", "Bound consumers: ");
         for (int i = 0; i < this.consumers.size(); i++)
           Log.i("IBeaconManager", " " + this.consumers.get(Integer.valueOf(i)));
       }
     }
   }
 
   public boolean isBound(IBeaconConsumer consumer)
   {
     synchronized (this.consumers) {
       return (this.consumers.keySet().contains(consumer)) && (this.serviceMessenger != null);
     }
   }
 
   public boolean setBackgroundMode(IBeaconConsumer consumer, boolean backgroundMode)
   {
     synchronized (this.consumers) {
       Log.i("IBeaconManager", "setBackgroundMode for consumer" + consumer);
       if (this.consumers.keySet().contains(consumer)) {
         try {
           ConsumerInfo consumerInfo = (ConsumerInfo)this.consumers.get(consumer);
           consumerInfo.isInBackground = backgroundMode;
           this.consumers.put(consumer, consumerInfo);
           updateScanPeriods();
           return true;
         }
         catch (RemoteException e) {
           Log.e("IBeaconManager", "Failed to set background mode", e);
           return false;
         }
       }
 
       if (LOG_DEBUG) Log.d("IBeaconManager", "This consumer is not bound to: " + consumer);
       return false;
     }
   }
 
   public void setRangeNotifier(RangeNotifier notifier)
   {
     this.rangeNotifier = notifier;
   }
 
   public void setMonitorNotifier(MonitorNotifier notifier)
   {
     this.monitorNotifier = notifier;
   }
 
   public void startRangingBeaconsInRegion(Region region)
     throws RemoteException
   {
     if (this.serviceMessenger == null) {
       throw new RemoteException("The IBeaconManager is not bound to the service.  Call iBeaconManager.bind(IBeaconConsumer consumer) and wait for a callback to onIBeaconServiceConnect()");
     }
     Message msg = Message.obtain(null, 2, 0, 0);
     StartRMData obj = new StartRMData(new RegionData(region), callbackPackageName(), getScanPeriod(), getBetweenScanPeriod());
     msg.obj = obj;
     this.serviceMessenger.send(msg);
     synchronized (this.rangedRegions) {
       this.rangedRegions.add((Region)region.clone());
     }
   }
 
   public void stopRangingBeaconsInRegion(Region region)
     throws RemoteException
   {
     if (this.serviceMessenger == null) {
       throw new RemoteException("The IBeaconManager is not bound to the service.  Call iBeaconManager.bind(IBeaconConsumer consumer) and wait for a callback to onIBeaconServiceConnect()");
     }
     Message msg = Message.obtain(null, 3, 0, 0);
     StartRMData obj = new StartRMData(new RegionData(region), callbackPackageName(), getScanPeriod(), getBetweenScanPeriod());
     msg.obj = obj;
     this.serviceMessenger.send(msg);
     synchronized (this.rangedRegions) {
       Region regionToRemove = null;
       for (Region rangedRegion : this.rangedRegions) {
         if (region.getUniqueId().equals(rangedRegion.getProximityUuid())) {
           regionToRemove = rangedRegion;
         }
       }
       this.rangedRegions.remove(regionToRemove);
     }
   }
 
   public void startMonitoringBeaconsInRegion(Region region)
     throws RemoteException
   {
     if (this.serviceMessenger == null) {
       throw new RemoteException("The IBeaconManager is not bound to the service.  Call iBeaconManager.bind(IBeaconConsumer consumer) and wait for a callback to onIBeaconServiceConnect()");
     }
     Message msg = Message.obtain(null, 4, 0, 0);
     StartRMData obj = new StartRMData(new RegionData(region), callbackPackageName(), getScanPeriod(), getBetweenScanPeriod());
     msg.obj = obj;
     this.serviceMessenger.send(msg);
     synchronized (this.monitoredRegions) {
       this.monitoredRegions.add((Region)region.clone());
     }
   }
 
   public void stopMonitoringBeaconsInRegion(Region region)
     throws RemoteException
   {
     if (this.serviceMessenger == null) {
       throw new RemoteException("The IBeaconManager is not bound to the service.  Call iBeaconManager.bind(IBeaconConsumer consumer) and wait for a callback to onIBeaconServiceConnect()");
     }
     Message msg = Message.obtain(null, 5, 0, 0);
     StartRMData obj = new StartRMData(new RegionData(region), callbackPackageName(), getScanPeriod(), getBetweenScanPeriod());
     msg.obj = obj;
     this.serviceMessenger.send(msg);
     synchronized (this.monitoredRegions) {
       Region regionToRemove = null;
       for (Region monitoredRegion : this.monitoredRegions) {
         if (region.getUniqueId().equals(monitoredRegion.getProximityUuid())) {
           regionToRemove = monitoredRegion;
         }
       }
       this.monitoredRegions.remove(regionToRemove);
     }
   }
 
   public void updateScanPeriods()
     throws RemoteException
   {
     if (this.serviceMessenger == null) {
       throw new RemoteException("The IBeaconManager is not bound to the service.  Call iBeaconManager.bind(IBeaconConsumer consumer) and wait for a callback to onIBeaconServiceConnect()");
     }
     Message msg = Message.obtain(null, 6, 0, 0);
     StartRMData obj = new StartRMData(getScanPeriod(), getBetweenScanPeriod());
     msg.obj = obj;
     this.serviceMessenger.send(msg);
   }
 
   
   public void setScanPeriods()
     throws RemoteException
   {
     updateScanPeriods();
   }
 
   private String callbackPackageName() {
     String packageName = this.context.getPackageName();
     if (LOG_DEBUG) Log.d("IBeaconManager", "callback packageName: " + packageName);
     return packageName;
   }
 
   public MonitorNotifier getMonitoringNotifier()
   {
     return this.monitorNotifier;
   }
 
   public RangeNotifier getRangingNotifier()
   {
     return this.rangeNotifier;
   }
 
   public Collection<Region> getMonitoredRegions()
   {
     ArrayList clonedMontoredRegions = new ArrayList();
     synchronized (this.monitoredRegions) {
       for (Region montioredRegion : this.monitoredRegions) {
         clonedMontoredRegions.add((Region)montioredRegion.clone());
       }
     }
     return clonedMontoredRegions;
   }
 
   public Collection<Region> getRangedRegions()
   {
     ArrayList clonedRangedRegions = new ArrayList();
     synchronized (this.rangedRegions) {
       for (Region rangedRegion : this.rangedRegions) {
         clonedRangedRegions.add((Region)rangedRegion.clone());
       }
     }
     return clonedRangedRegions;
   }
 
   protected void setDataRequestNotifier(RangeNotifier notifier) {
     this.dataRequestNotifier = notifier; } 
   protected RangeNotifier getDataRequestNotifier() { return this.dataRequestNotifier; }
 
 
   private boolean isInBackground()
   {
     boolean background = true;
     synchronized (this.consumers) {
       for (IBeaconConsumer consumer : this.consumers.keySet()) {
         if (!((ConsumerInfo)this.consumers.get(consumer)).isInBackground) {
           background = false;
         }
       }
     }
     return background;
   }
 
   private long getScanPeriod() {
     if (isInBackground()) {
       return this.backgroundScanPeriod;
     }
 
     return this.foregroundScanPeriod;
   }
 
   private long getBetweenScanPeriod() {
     if (isInBackground()) {
       return this.backgroundBetweenScanPeriod;
     }
 
     return this.foregroundBetweenScanPeriod;
   }
 
   private class ConsumerInfo
   {
     public boolean isConnected = false;
     public boolean isInBackground = false;
 
     private ConsumerInfo()
     {
     }
   }
 }

