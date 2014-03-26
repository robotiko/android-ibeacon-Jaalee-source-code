 package com.jaalee.ibeacon.service;
 
 import android.app.Service;
 import android.bluetooth.BluetoothAdapter;
 import android.bluetooth.BluetoothAdapter.LeScanCallback;
 import android.bluetooth.BluetoothDevice;
 import android.bluetooth.BluetoothManager;
 import android.content.Context;
 import android.content.Intent;
 import android.os.AsyncTask;
 import android.os.Binder;
 import android.os.Handler;
 import android.os.IBinder;
 import android.os.Message;
 import android.os.Messenger;
 import android.util.Log;

import com.jaalee.ibeacon.IBeacon;
import com.jaalee.ibeacon.IBeaconManager;
import com.jaalee.ibeacon.Region;

 import java.lang.ref.WeakReference;
 import java.lang.reflect.Field;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
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
 public class IBeaconService extends Service
 {
   public static final String TAG = "IBeaconService";
   private Map<Region, RangeState> rangedRegionState;
   private Map<Region, MonitorState> monitoredRegionState;
   private BluetoothAdapter bluetoothAdapter;
   private boolean scanning;
   private boolean scanningPaused;
   private Date lastIBeaconDetectionTime;
   private HashSet<IBeacon> trackedBeacons;
   private Handler handler;
   private int bindCount;
   private long scanPeriod;
   private long betweenScanPeriod;
   private List<IBeacon> simulatedScanData;
   public static final int MSG_START_RANGING = 2;
   public static final int MSG_STOP_RANGING = 3;
   public static final int MSG_START_MONITORING = 4;
   public static final int MSG_STOP_MONITORING = 5;
   public static final int MSG_SET_SCAN_PERIODS = 6;
   final Messenger mMessenger;
   private int ongoing_notification_id;
   private long lastScanStartTime;
   private long lastScanEndTime;
   private long nextScanStartTime;
   private long scanStopTime;
   private BluetoothAdapter.LeScanCallback leScanCallback;
 
   public IBeaconService()
   {
     this.rangedRegionState = new HashMap();
     this.monitoredRegionState = new HashMap();
 
     this.lastIBeaconDetectionTime = new Date();
 
     this.handler = new Handler();
     this.bindCount = 0;
 
     this.scanPeriod = 1100L;
     this.betweenScanPeriod = 0L;
 
     this.simulatedScanData = null;
 
     this.mMessenger = new Messenger(new IncomingHandler(this));
 
     this.ongoing_notification_id = 1;
 
     this.lastScanStartTime = 0L;
     this.lastScanEndTime = 0L;
     this.nextScanStartTime = 0L;
     this.scanStopTime = 0L;
 
     this.leScanCallback = new BluetoothAdapter.LeScanCallback()
     {
       public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
       {
         if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconService", "got record");
         new IBeaconService.ScanProcessor().execute(new IBeaconService.ScanData[] { new IBeaconService.ScanData(device, rssi, scanRecord) });
       }
     };
   }
 
   public IBinder onBind(Intent intent)
   {
     Log.i("IBeaconService", "binding");
     this.bindCount += 1;
     return this.mMessenger.getBinder();
   }
 
   public boolean onUnbind(Intent intent)
   {
     Log.i("IBeaconService", "unbinding");
     this.bindCount -= 1;
     return false;
   }
 
   public void onCreate()
   {
     Log.i("IBeaconService", "iBeaconService version 0.7.5 is starting up");
     getBluetoothAdapter();
     try
     {
       Class klass = Class.forName("com.jaalee.ibeacon.SimulatedScanData");
       Field f = klass.getField("iBeacons");
       this.simulatedScanData = ((List)f.get(null));
     } catch (ClassNotFoundException e) {
       if (IBeaconManager.LOG_DEBUG)
         Log.d("IBeaconService", "No com.jaalee.ibeacon.SimulatedScanData class exists.");
     } catch (Exception e) {
       Log.e("IBeaconService", "Cannot get simulated Scan data.  Make sure your com.jaalee.ibeacon.SimulatedScanData class defines a field with the signature 'public static List<IBeacon> iBeacons'", e);
     }
   }
 
   public void onDestroy()
   {
     Log.i("IBeaconService", "onDestory called.  stopping scanning");
     this.handler.removeCallbacksAndMessages(null);
     scanLeDevice(Boolean.valueOf(false));
     if (this.bluetoothAdapter != null) {
       this.bluetoothAdapter.stopLeScan(this.leScanCallback);
       this.lastScanEndTime = new Date().getTime();
     }
   }
 
   private boolean isInBackground()
   {
     if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconService", "bound client count:" + this.bindCount);
     return this.bindCount == 0;
   }
 
   public void startRangingBeaconsInRegion(Region region, Callback callback)
   {
     synchronized (this.rangedRegionState) {
       if (this.rangedRegionState.containsKey(region)) {
         Log.i("IBeaconService", "Already ranging that region -- will replace existing region.");
         this.rangedRegionState.remove(region);
       }
       this.rangedRegionState.put(region, new RangeState(callback));
     }
     if (!this.scanning)
       scanLeDevice(Boolean.valueOf(true));
   }
 
   public void stopRangingBeaconsInRegion(Region region)
   {
     synchronized (this.rangedRegionState) {
       this.rangedRegionState.remove(region);
     }
     if ((this.scanning) && (this.rangedRegionState.size() == 0) && (this.monitoredRegionState.size() == 0))
       scanLeDevice(Boolean.valueOf(false));
   }
 
   public void startMonitoringBeaconsInRegion(Region region, Callback callback)
   {
     if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconService", "startMonitoring called");
     synchronized (this.monitoredRegionState) {
       if (this.monitoredRegionState.containsKey(region)) {
         Log.i("IBeaconService", "Already monitoring that region -- will replace existing region monitor.");
         this.monitoredRegionState.remove(region);
       }
       this.monitoredRegionState.put(region, new MonitorState(callback));
     }
     if (IBeaconManager.LOG_DEBUG)
       Log.d("IBeaconService", "Currently monitoring " + this.monitoredRegionState.size() + " regions.");
     if (!this.scanning)
       scanLeDevice(Boolean.valueOf(true));
   }
 
   public void stopMonitoringBeaconsInRegion(Region region)
   {
     if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconService", "stopMonitoring called");
     synchronized (this.monitoredRegionState) {
       this.monitoredRegionState.remove(region);
     }
     if (IBeaconManager.LOG_DEBUG)
       Log.d("IBeaconService", "Currently monitoring " + this.monitoredRegionState.size() + " regions.");
     if ((this.scanning) && (this.rangedRegionState.size() == 0) && (this.monitoredRegionState.size() == 0))
       scanLeDevice(Boolean.valueOf(false));
   }
 
   public void setScanPeriods(long scanPeriod, long betweenScanPeriod)
   {
     this.scanPeriod = scanPeriod;
     this.betweenScanPeriod = betweenScanPeriod;
     long now = new Date().getTime();
     if (this.nextScanStartTime > now)
     {
       long proposedNextScanStartTime = this.lastScanEndTime + betweenScanPeriod;
       if (proposedNextScanStartTime < this.nextScanStartTime) {
         this.nextScanStartTime = proposedNextScanStartTime;
         Log.i("IBeaconService", "Adjusted nextScanStartTime to be " + new Date(this.nextScanStartTime));
       }
     }
     if (this.scanStopTime > now)
     {
       long proposedScanStopTime = this.lastScanStartTime + scanPeriod;
       if (proposedScanStopTime < this.scanStopTime) {
         this.scanStopTime = proposedScanStopTime;
         Log.i("IBeaconService", "Adjusted scanStopTime to be " + new Date(this.scanStopTime));
       }
     }
   }
 
   private void scanLeDevice(Boolean enable)
   {
     if (getBluetoothAdapter() == null) {
       Log.e("IBeaconService", "No bluetooth adapter.  iBeaconService cannot scan.");
       if (this.simulatedScanData == null) {
         Log.w("IBeaconService", "exiting");
         return;
       }
       Log.w("IBeaconService", "proceeding with simulated scan data");
     }
 
     if (enable.booleanValue()) {
       long millisecondsUntilStart = this.nextScanStartTime - new Date().getTime();
       if (millisecondsUntilStart > 0L) {
         if (IBeaconManager.LOG_DEBUG) {
           Log.d("IBeaconService", "Waiting to start next bluetooth scan for another " + millisecondsUntilStart + " milliseconds");
         }
 
         this.handler.postDelayed(new Runnable()
         {
           public void run() {
             IBeaconService.this.scanLeDevice(Boolean.valueOf(true));
           }
         }
         , millisecondsUntilStart > 1000L ? 1000L : millisecondsUntilStart);
 
         return;
       }
 
       this.trackedBeacons = new HashSet();
       if ((!this.scanning) || (this.scanningPaused == true)) {
         this.scanning = true;
         this.scanningPaused = false;
         try {
           if (getBluetoothAdapter() != null)
             if (getBluetoothAdapter().isEnabled()) {
               getBluetoothAdapter().startLeScan(this.leScanCallback);
               this.lastScanStartTime = new Date().getTime();
             } else {
               Log.w("IBeaconService", "Bluetooth is disabled.  Cannot scan for iBeacons.");
             }
         }
         catch (Exception e) {
           Log.e("TAG", "Exception starting bluetooth scan.  Perhaps bluetooth is disabled or unavailable?");
         }
       }
       else if (IBeaconManager.LOG_DEBUG) { Log.d("IBeaconService", "We are already scanning"); }
 
       this.scanStopTime = (new Date().getTime() + this.scanPeriod);
       scheduleScanStop();
 
       if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconService", "Scan started"); 
     }
     else { if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconService", "disabling scan");
       this.scanning = false;
       if (getBluetoothAdapter() != null) {
         getBluetoothAdapter().stopLeScan(this.leScanCallback);
         this.lastScanEndTime = new Date().getTime();
       }
     }
   }
 
   private void scheduleScanStop()
   {
     long millisecondsUntilStop = this.scanStopTime - new Date().getTime();
     if (millisecondsUntilStop > 0L) {
       if (IBeaconManager.LOG_DEBUG)
         Log.d("IBeaconService", "Waiting to stop scan for another " + millisecondsUntilStop + " milliseconds");
       this.handler.postDelayed(new Runnable()
       {
         public void run() {
           IBeaconService.this.scheduleScanStop();
         }
       }
       , millisecondsUntilStop > 1000L ? 1000L : millisecondsUntilStop);
     }
     else
     {
       finishScanCycle();
     }
   }
 
   private void finishScanCycle()
   {
     if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconService", "Done with scan cycle");
     processExpiredMonitors();
     if (this.scanning == true)
       if (!anyRangingOrMonitoringRegionsActive()) {
         if (IBeaconManager.LOG_DEBUG)
           Log.d("IBeaconService", "Not starting scan because no monitoring or ranging regions are defined.");
       } else {
         processRangeData();
         if (IBeaconManager.LOG_DEBUG)
           Log.d("IBeaconService", "Restarting scan.  Unique beacons seen last cycle: " + this.trackedBeacons.size());
         if (getBluetoothAdapter() != null) {
           if (getBluetoothAdapter().isEnabled()) {
             getBluetoothAdapter().stopLeScan(this.leScanCallback);
             this.lastScanEndTime = new Date().getTime();
           } else {
             Log.w("IBeaconService", "Bluetooth is disabled.  Cannot scan for iBeacons.");
           }
         }
 
         this.scanningPaused = true;
 
         if (this.simulatedScanData != null)
         {
           if (0 != (getApplicationInfo().flags &= 2)) {
             for (IBeacon iBeacon : this.simulatedScanData)
               processIBeaconFromScan(iBeacon);
           }
           else {
             Log.w("IBeaconService", "Simulated scan data provided, but ignored because we are not running in debug mode.  Please remove simulated scan data for production.");
           }
         }
         this.nextScanStartTime = (new Date().getTime() + this.betweenScanPeriod);
         scanLeDevice(Boolean.valueOf(true));
       }
   }
 
   private void processRangeData()
   {
     Iterator regionIterator = this.rangedRegionState.keySet().iterator();
     while (regionIterator.hasNext()) {
       Region region = (Region)regionIterator.next();
       RangeState rangeState = (RangeState)this.rangedRegionState.get(region);
       if (IBeaconManager.LOG_DEBUG)
         Log.d("IBeaconService", "Calling ranging callback with " + rangeState.getIBeacons().size() + " iBeacons");
       rangeState.getCallback().call(this, "rangingData", new RangingData(rangeState.getIBeacons(), region));
       rangeState.clearIBeacons();
     }
   }
 
   private void processExpiredMonitors()
   {
     Iterator monitoredRegionIterator = this.monitoredRegionState.keySet().iterator();
     while (monitoredRegionIterator.hasNext()) {
       Region region = (Region)monitoredRegionIterator.next();
       MonitorState state = (MonitorState)this.monitoredRegionState.get(region);
       if (state.isNewlyOutside()) {
         if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconService", "found a monitor that expired: " + region);
         state.getCallback().call(this, "monitoringData", new MonitoringData(state.isInside(), region));
       }
     }
   }
 
   private void processIBeaconFromScan(IBeacon iBeacon) {
     this.lastIBeaconDetectionTime = new Date();
     this.trackedBeacons.add(iBeacon);
     if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconService", "iBeacon detected :" + iBeacon.getProximityUuid() + " " + iBeacon.getMajor() + " " + iBeacon.getMinor() + " accuracy: " + iBeacon.getAccuracy() + " proximity: " + iBeacon.getProximity());
 
     List matchedRegions = null;
     synchronized (this.monitoredRegionState) {
       matchedRegions = matchingRegions(iBeacon, this.monitoredRegionState.keySet());
     }
 
     Iterator matchedRegionIterator = matchedRegions.iterator();
     while (matchedRegionIterator.hasNext()) {
       Region region = (Region)matchedRegionIterator.next();
       MonitorState state = (MonitorState)this.monitoredRegionState.get(region);
       if (state.markInside()) {
         state.getCallback().call(this, "monitoringData", new MonitoringData(state.isInside(), region));
       }
 
     }
 
     if (IBeaconManager.LOG_DEBUG)
       Log.d("IBeaconService", "looking for ranging region matches for this ibeacon");
     synchronized (this.rangedRegionState) {
       matchedRegions = matchingRegions(iBeacon, this.rangedRegionState.keySet());
     }
     matchedRegionIterator = matchedRegions.iterator();
     while (matchedRegionIterator.hasNext()) {
       Region region = (Region)matchedRegionIterator.next();
       if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconService", "matches ranging region: " + region);
       RangeState rangeState = (RangeState)this.rangedRegionState.get(region);
       rangeState.addIBeacon(iBeacon);
     }
   }
 
   private List<Region> matchingRegions(IBeacon iBeacon, Collection<Region> regions)
   {
     List matched = new ArrayList();
     Iterator regionIterator = regions.iterator();
     while (regionIterator.hasNext()) {
       Region region = (Region)regionIterator.next();
       if (region.matchesIBeacon(iBeacon)) {
         matched.add(region);
       }
       else if (IBeaconManager.LOG_DEBUG) Log.d("IBeaconService", "This region does not match: " + region);
 
     }
 
     return matched;
   }
 
   private boolean anyRangingOrMonitoringRegionsActive()
   {
     return this.rangedRegionState.size() + this.monitoredRegionState.size() > 0;
   }
 
   private BluetoothAdapter getBluetoothAdapter() {
     if (this.bluetoothAdapter == null)
     {
       BluetoothManager bluetoothManager = (BluetoothManager)getApplicationContext().getSystemService("bluetooth");
 
       this.bluetoothAdapter = bluetoothManager.getAdapter();
     }
     return this.bluetoothAdapter;
   }
 
   private class ScanProcessor extends AsyncTask<IBeaconService.ScanData, Void, Void>
   {
     private ScanProcessor()
     {
     }
 
     protected Void doInBackground(IBeaconService.ScanData[] params)
     {
       IBeaconService.ScanData scanData = params[0];
 
       IBeacon iBeacon = IBeacon.fromScanData(scanData.scanRecord, scanData.rssi);
 
       if (iBeacon != null) {
         IBeaconService.this.processIBeaconFromScan(iBeacon);
       }
       return null;
     }
 
     protected void onPostExecute(Void result)
     {
     }
 
     protected void onPreExecute()
     {
     }
 
     protected void onProgressUpdate(Void[] values)
     {
     }
   }
 
   private class ScanData
   {
     public BluetoothDevice device;
     public int rssi;
     public byte[] scanRecord;
 
     public ScanData(BluetoothDevice device, int rssi, byte[] scanRecord)
     {
       this.device = device;
       this.rssi = rssi;
       this.scanRecord = scanRecord;
     }
   }
 
   static class IncomingHandler extends Handler
   {
     private final WeakReference<IBeaconService> mService;
 
     IncomingHandler(IBeaconService service)
     {
       this.mService = new WeakReference(service);
     }
 
     public void handleMessage(Message msg)
     {
       IBeaconService service = (IBeaconService)this.mService.get();
       StartRMData startRMData = (StartRMData)msg.obj;
 
       if (service != null)
         switch (msg.what) {
         case 2:
           Log.i("IBeaconService", "start ranging received");
           service.startRangingBeaconsInRegion(startRMData.getRegionData(), new com.jaalee.ibeacon.service.Callback(startRMData.getCallbackPackageName()));
           service.setScanPeriods(startRMData.getScanPeriod(), startRMData.getBetweenScanPeriod());
           break;
         case 3:
           Log.i("IBeaconService", "stop ranging received");
           service.stopRangingBeaconsInRegion(startRMData.getRegionData());
           service.setScanPeriods(startRMData.getScanPeriod(), startRMData.getBetweenScanPeriod());
           break;
         case 4:
           Log.i("IBeaconService", "start monitoring received");
           service.startMonitoringBeaconsInRegion(startRMData.getRegionData(), new com.jaalee.ibeacon.service.Callback(startRMData.getCallbackPackageName()));
           service.setScanPeriods(startRMData.getScanPeriod(), startRMData.getBetweenScanPeriod());
           break;
         case 5:
           Log.i("IBeaconService", "stop monitoring received");
           service.stopMonitoringBeaconsInRegion(startRMData.getRegionData());
           service.setScanPeriods(startRMData.getScanPeriod(), startRMData.getBetweenScanPeriod());
           break;
         case 6:
           Log.i("IBeaconService", "set scan intervals received");
           service.setScanPeriods(startRMData.getScanPeriod(), startRMData.getBetweenScanPeriod());
           break;
         default:
           super.handleMessage(msg);
         }
     }
   }
 
   public class IBeaconBinder extends Binder
   {
     public IBeaconBinder()
     {
     }
 
     public IBeaconService getService()
     {
       Log.i("IBeaconService", "getService of IBeaconBinder called");
 
       return IBeaconService.this;
     }
   }
 }

