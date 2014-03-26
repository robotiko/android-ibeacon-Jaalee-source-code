 package com.jaalee.proximity.geofence;
 
 import android.content.Context;
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
 public class GeofenceManager
 {
   private static GeofenceManager instance;
   private Context context;
 
   public static GeofenceManager getInstanceForApplication(Context context)
   {
     if (instance == null) {
       instance = new GeofenceManager(context);
     }
     return instance;
   }
 
   private GeofenceManager(Context context) {
     this.context = context;
   }
 
   public void bind(GeofenceConsumer consumer)
   {
   }
 
   public void unbind(GeofenceConsumer consumer)
   {
   }
 }

