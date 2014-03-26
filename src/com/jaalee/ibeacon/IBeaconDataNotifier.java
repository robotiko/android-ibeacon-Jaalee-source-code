package com.jaalee.ibeacon;

import com.jaalee.ibeacon.client.DataProviderException;
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
public abstract interface IBeaconDataNotifier
{
  public abstract void iBeaconDataUpdate(IBeacon paramIBeacon, IBeaconData paramIBeaconData, DataProviderException paramDataProviderException);
}

