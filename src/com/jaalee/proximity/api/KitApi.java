 package com.jaalee.proximity.api;
 
 import android.util.Log;

import com.jaalee.ibeacon.IBeaconManager;
import com.jaalee.proximity.model.Kit;

 import java.io.BufferedReader;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.net.HttpURLConnection;
 import java.net.URL;
import org.json.JSONObject;
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
 public class KitApi
 {
   private static final String TAG = "KitApi";
   private int responseCode = -1;
   protected String response;
   protected Kit responseKit;
   protected JSONObject responseJson;
   private String urlString;
   private String apiToken;
   private String deviceId;
   private String libraryVersion;
   Exception exception;
 
   public KitApi(String urlString, String apiToken, String deviceId, String libraryVersion)
   {
     this.urlString = urlString;
     this.apiToken = apiToken;
     this.deviceId = deviceId;
     this.libraryVersion = libraryVersion;
   }
 
   public JSONObject getResponseJson() {
     return this.responseJson;
   }
 
   public Kit getResponseKit() {
     return this.responseKit;
   }
 
   public int getResponseCode() {
     return this.responseCode;
   }
 
   public String getResponseString() {
     return this.response;
   }
 
   public Exception getException() {
     return this.exception;
   }
 
   public void request()
   {
     this.responseKit = null;
     this.responseJson = null;
     this.response = null;
     this.exception = null;
     String currentUrlString = this.urlString;
     int requestCount = 0;
     StringBuilder responseBuilder = new StringBuilder();
     URL url = null;
     HttpURLConnection conn = null;
     do {
       if (requestCount != 0) {
         if (IBeaconManager.LOG_DEBUG) Log.d("KitApi", new StringBuilder().append("Following redirect from ").append(this.urlString).append(" to ").append(conn.getHeaderField("Location")).toString());
         currentUrlString = conn.getHeaderField("Location");
       }
       requestCount++;
       this.responseCode = -1;
       try {
         url = new URL(currentUrlString);
       } catch (Exception e) {
         Log.e("KitApi", new StringBuilder().append("Can't construct URL from: ").append(this.urlString).toString());
         this.exception = e;
       }
 
       if (url == null) {
         if (IBeaconManager.LOG_DEBUG) Log.d("KitApi", "URL is null.  Cannot make request"); 
       }
       else {
         try { conn = (HttpURLConnection)url.openConnection();
           conn.addRequestProperty("Authorization", new StringBuilder().append("Token token=").append(this.apiToken).toString());
           conn.addRequestProperty("X-PK-AppID", this.deviceId);
           conn.addRequestProperty("X-PK-FrameworkVersion", this.libraryVersion);
           conn.addRequestProperty("Accept", "application/json");
           this.responseCode = conn.getResponseCode();
           if (IBeaconManager.LOG_DEBUG) Log.d("KitApi", new StringBuilder().append("response code is ").append(conn.getResponseCode()).toString()); 
         }
         catch (SecurityException e1)
         {
           Log.w("KitApi", "Can't reach proximitykit serve.  Have you added android.permission.INTERNET to your manifest?r", e1);
           this.exception = e1;
         }
         catch (FileNotFoundException e2) {
           Log.w("KitApi", "No proximity kit data exists at \"+urlString", e2);
           this.exception = e2;
         }
         catch (IOException e3) {
           Log.w("KitApi", "Can't reach proximitykit server", e3);
           this.exception = e3;
         }
       }
 
     }
 
     while (((requestCount < 10) && (this.responseCode == 302)) || (this.responseCode == 301) || (this.responseCode == 303));
 
     if (this.exception == null)
       try {
         BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         String inputLine;
         while ((inputLine = in.readLine()) != null)
           responseBuilder.append(inputLine);
         in.close();
         this.response = responseBuilder.toString();
         if (IBeaconManager.LOG_DEBUG) Log.d("KitApi", new StringBuilder().append("response body is ").append(this.response).toString());
         JSONObject jsonObj = new JSONObject(this.response);
         this.responseKit = Kit.fromJson(jsonObj);
       } catch (Exception e) {
         this.exception = e;
         Log.w("KitApi", new StringBuilder().append("error reading iBeacon data:").append(e).toString());
       }
   }
 }

