package com.jaalee.ibeacon;
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
public abstract interface IBeaconData
{
  public abstract Double getLatitude();

  public abstract void setLatitude(Double paramDouble);

  public abstract void setLongitude(Double paramDouble);

  public abstract Double getLongitude();

  public abstract String get(String paramString);

  public abstract void set(String paramString1, String paramString2);

  public abstract void sync(IBeaconDataNotifier paramIBeaconDataNotifier);

  public abstract boolean isDirty();
}

