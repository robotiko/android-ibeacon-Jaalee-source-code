 package com.jaalee.proximity.licensing;
 
 import java.io.IOException;
 import java.io.InputStream;
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
 public class PropertiesFile
 {
   public static String PROPERTIES_FILE_NAME = "ProximityKit.properties";
 
   public boolean exists() {
     InputStream inputStream = getInputStream();
     if (inputStream != null) {
       try {
         inputStream.close();
       } catch (IOException e) {
       }
       return true;
     }
     return false;
   }
 
   public InputStream getInputStream() {
     InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
     return inputStream;
   }
 }

