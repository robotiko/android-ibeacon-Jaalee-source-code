 package com.jaalee.ibeacon.service;
 
 import android.util.Log;
 import java.util.Date;
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
 public class MonitorState
 {
   private static final String TAG = "MonitorState";
   public static long INSIDE_EXPIRATION_MILLIS = 10000L;
   private boolean inside = false;
   private long lastSeenTime = 0L;
   private Callback callback;
 
   public MonitorState(Callback c)
   {
     this.callback = c;
   }
 
   public Callback getCallback() {
     return this.callback;
   }
 
   public boolean markInside()
   {
     this.lastSeenTime = new Date().getTime();
     if (!this.inside) {
       this.inside = true;
       return true;
     }
     return false;
   }
   public boolean isNewlyOutside() {
     if ((this.inside) && 
       (this.lastSeenTime > 0L) && (new Date().getTime() - this.lastSeenTime > INSIDE_EXPIRATION_MILLIS)) {
       this.inside = false;
       Log.d("MonitorState", "We are newly outside the region because the lastSeenTime of " + this.lastSeenTime + " was " + (new Date().getTime() - this.lastSeenTime) + " seconds ago, and that is over the expiration duration of  " + INSIDE_EXPIRATION_MILLIS);
       this.lastSeenTime = 0L;
       return true;
     }
 
     return false;
   }
   public boolean isInside() {
     if ((this.inside) && 
       (!isNewlyOutside())) {
       return true;
     }
 
     return false;
   }
 }

