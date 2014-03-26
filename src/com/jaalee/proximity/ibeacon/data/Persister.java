package com.jaalee.proximity.ibeacon.data;

import com.jaalee.ibeacon.IBeacon;
import com.jaalee.ibeacon.IBeaconData;
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
public abstract interface Persister
{
  public abstract void saveToCloud(IBeaconData paramIBeaconData, IBeaconDataNotifier paramIBeaconDataNotifier);

  public abstract void loadFromCloud(IBeacon paramIBeacon, IBeaconDataNotifier paramIBeaconDataNotifier);

  public abstract IBeaconData loadFromCache(IBeacon paramIBeacon);
}

