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
 public class ConfigurationAsynchApi extends AsyncTask<Void, Void, Void>
 {
   private static final String TAG = "ConfigurationAsynchApi";
   private Exception exception = null;
   private String urlString = null;
   private String response = null;
   ConfigurationApi configurationApi;
   ConfigurationAsynchApiCallback callback;
 
   public ConfigurationAsynchApi(ConfigurationApi configurationApi, ConfigurationAsynchApiCallback callback)
   {
     this.configurationApi = configurationApi;
     this.callback = callback;
   }
 
   protected Void doInBackground(Void[] params)
   {
     this.configurationApi.request();
     this.callback.requestComplete(this.configurationApi);
     return null;
   }
 
   protected void onPostExecute(IBeaconData data)
   {
   }
 }

