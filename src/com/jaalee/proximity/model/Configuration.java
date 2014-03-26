package com.jaalee.proximity.model;

import org.json.JSONException;
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
public class Configuration
{
  private String token;
  private String apiUrl;

  public String getApiUrl()
  {
	  return this.apiUrl;
  }
  public String getToken() {
	  return this.token;
  }
  public static Configuration fromJson(JSONObject json) throws JSONException {
	  Configuration configuration = new Configuration();
	  configuration.token = json.getString("api_token");
	  configuration.apiUrl = json.getString("kit_url");
	  return configuration;
  }
}
