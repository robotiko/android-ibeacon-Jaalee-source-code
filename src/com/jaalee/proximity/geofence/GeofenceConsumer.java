package com.jaalee.proximity.geofence;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
public abstract interface GeofenceConsumer
{
  public abstract void onGeofenceServiceConnect();

  public abstract Context getApplicationContext();

  public abstract void unbindService(ServiceConnection paramServiceConnection);

  public abstract boolean bindService(Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt);
}

