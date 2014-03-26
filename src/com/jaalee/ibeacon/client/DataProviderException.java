 package com.jaalee.ibeacon.client;
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
 public class DataProviderException extends Exception
 {
   private static final long serialVersionUID = -2574842662565384114L;
 
   public DataProviderException()
   {
   }
 
   public DataProviderException(String msg)
   {
     super(msg);
   }
   public DataProviderException(String msg, Throwable t) {
     super(msg, t);
   }
 }

