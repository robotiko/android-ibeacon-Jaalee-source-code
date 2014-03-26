 package com.jaalee.ibeacon.service;
 
 import android.content.ComponentName;
 import android.content.Context;
 import android.content.Intent;
 import android.os.Messenger;
 import android.os.Parcelable;
 import android.util.Log;
 
 public class Callback
 {
   private String TAG = "Callback";
   private Messenger messenger;
   private Intent intent;
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
   public Callback(String intentPackageName)
   {
     if (intentPackageName != null) {
       this.intent = new Intent();
       this.intent.setComponent(new ComponentName(intentPackageName, "com.jaalee.ibeacon.IBeaconIntentProcessor"));
     }
   }
 
   public Intent getIntent() { return this.intent; }
 
   public void setIntent(Intent intent) {
     this.intent = intent;
   }
 
   public boolean call(Context context, String dataName, Parcelable data)
   {
     if (this.intent != null) {
       Log.d(this.TAG, "attempting callback via intent: " + this.intent.getComponent());
       this.intent.putExtra(dataName, data);
       context.startService(this.intent);
       return true;
     }
     return false;
   }
 }

