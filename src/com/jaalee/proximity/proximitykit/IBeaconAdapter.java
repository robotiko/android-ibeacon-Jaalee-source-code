package com.jaalee.proximity.proximitykit;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;

import com.jaalee.ibeacon.IBeaconConsumer;
import com.jaalee.ibeacon.Region;
import com.jaalee.proximity.ProximityKitManager;
import com.jaalee.proximity.ProximityKitNotifier;
import com.jaalee.proximity.model.KitIBeacon;

import java.util.ArrayList;
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
public class IBeaconAdapter
  implements IBeaconConsumer
{
//  private static final String TAG = "IBeaconAdapter";
  Context context;
  ProximityKitManager pkManager;
  ArrayList<Region> pkRegions;

  public IBeaconAdapter(ProximityKitManager manager, Context context, ProximityKitNotifier notifier)
  {
	   this.context = context;
	   this.pkManager = manager;
	   this.pkManager.getIBeaconManager().setDataNotifier(notifier);
	   this.pkManager.getIBeaconManager().setMonitorNotifier(notifier);

	   Log.d("IBeaconAdapter", "Setting datanotifier of: " + notifier + " on iBeaconManager: " + this.pkManager.getIBeaconManager());

	   Log.d("IBeaconAdapter", "constructor of " + this + " pkManager is " + this.pkManager);
	   this.pkRegions = new ArrayList<Region>();
	   Log.d("IBeaconAdapter", "constructor2 of " + this + " pkManager is " + this.pkManager);
	   this.pkManager.getIBeaconManager().bind(this);
  }

  public void updateRegions()
  {
	  Log.d("IBeaconAdapter", "updateRegions of " + this + " pkManager is " + this.pkManager);

	  for (Region region : this.pkRegions) {
		  try {
			  this.pkManager.getIBeaconManager().stopRangingBeaconsInRegion(region);
		  }
		  catch (RemoteException e)
		  {
			  Log.e("IBeaconAdapter", "Failed to stop ranging region due to remote exception", e);
		  }
		  try {
			  this.pkManager.getIBeaconManager().stopMonitoringBeaconsInRegion(region);
		  }
		  catch (RemoteException e)
		  {
			  Log.e("IBeaconAdapter", "Failed to stop ranging region due to remote exception", e);
		  }
	  }

	  Log.d("IBeaconAdapter", "updateRegions 2 of " + this + " pkManager is " + this.pkManager);

	  if (this.pkManager.getKit() != null)
		  for (KitIBeacon kitIBeacon : this.pkManager.getKit().getIBeacons()) {
			  Region region = new Region(kitIBeacon.toString(), kitIBeacon.getProximityUuid(), Integer.valueOf(kitIBeacon.getMajor()), Integer.valueOf(kitIBeacon.getMinor()));
			  try {
				  this.pkManager.getIBeaconManager().startMonitoringBeaconsInRegion(region);
			  }
			  catch (RemoteException e)
			  {
				  Log.e("IBeaconAdapter", "Failed to start monitoring regions due to remote exception", e);
			  }
			  try {
				  this.pkManager.getIBeaconManager().startRangingBeaconsInRegion(region);
			  }
			  catch (RemoteException e)
			  {
				  Log.e("IBeaconAdapter", "Failed to start ranging regions due to remote exception", e);
			  }
		  }
  }

  public void onIBeaconServiceConnect()
  {
	  Log.d("IBeaconAdapter", "onIBeaconServiceConnect");
	  updateRegions();
	  this.pkManager.getIBeaconManager().setDataNotifier(this.pkManager.getNotifier());
	  this.pkManager.getIBeaconManager().setMonitorNotifier(this.pkManager.getNotifier());
	  Log.d("IBeaconAdapter", "Setting datanotifier of: " + this.pkManager.getNotifier() + " on iBeaconManager: " + this.pkManager.getIBeaconManager());
  }

  public Context getApplicationContext()
  {
	  return this.context.getApplicationContext();
  }

  public void unbindService(ServiceConnection connection)
  {
	  this.context.unbindService(connection);
  }

  public boolean bindService(Intent intent, ServiceConnection connection, int mode)
  {
	  return this.context.bindService(intent, connection, mode);
  }
}
