package com.jaalee.ibeacon.client;

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
public abstract interface IBeaconDataFactory
{
  public abstract void requestIBeaconData(IBeacon paramIBeacon, IBeaconDataNotifier paramIBeaconDataNotifier);
}

