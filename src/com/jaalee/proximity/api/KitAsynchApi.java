 package com.jaalee.proximity.api;
 
 import android.os.AsyncTask;

import com.jaalee.ibeacon.IBeaconData;
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
 public class KitAsynchApi extends AsyncTask<Void, Void, Void>
 {
   private static final String TAG = "AsynchReader";
   private Exception exception = null;
   private String urlString = null;
   private String response = null;
   KitApi kitApi;
   KitAsynchApiCallback callback;
 
   public KitAsynchApi(KitApi kitApi, KitAsynchApiCallback callback)
   {
     this.kitApi = kitApi;
     this.callback = callback;
   }
 
   protected Void doInBackground(Void[] params)
   {
     this.kitApi.request();
     this.callback.requestComplete(this.kitApi);
     return null;
   }
 
   protected void onPostExecute(IBeaconData data)
   {
   }
 }

