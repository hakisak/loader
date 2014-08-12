// Copyright 2007 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.launcher;

import java.util.*;

/**
 * Resources used by Launcher Service
 *
 * @author Deane Richan
 */
public class Resources {

  private final static String pkg = "org.xito.launcher";
  public final static ResourceBundle bundle = ResourceBundle.getBundle(pkg + ".resource");
  public final static ResourceBundle jnlpBundle = ResourceBundle.getBundle(pkg + ".jnlp_resource");
  public final static ResourceBundle javaBundle = ResourceBundle.getBundle(pkg + ".java_resource");
  public final static ResourceBundle appletBundle = ResourceBundle.getBundle(pkg + ".applet_resource");
  public final static ResourceBundle sysBundle = ResourceBundle.getBundle(pkg + ".sys_resource");
  public final static ResourceBundle webBundle = ResourceBundle.getBundle(pkg + ".web_resource");
  public final static ResourceBundle youtubeBundle = ResourceBundle.getBundle(pkg + ".youtube_resource");
  
  /**
   * Get a resource value for a specific OS
   */
  public static String getStringForOS(ResourceBundle bundle, String name, String osName) {
      
      if(bundle == null || name == null) 
          return null;
      
      String defaultValue = null;
      String osValue = null;
      
      try {
          defaultValue = bundle.getString(name);
          if(osName != null) {
            osValue = bundle.getString(name+"."+osName);
            return osValue;
          }
      }
      catch(MissingResourceException missing) {
          if(defaultValue != null) {
              return defaultValue;
          }
      }
      
      return "** " + name + " **";
  }
  
  /**
   * Returns an int value for given resource and OS NAME
   */
  public static int getIntForOS(ResourceBundle bundle, String name, String osName, int defaultValue) {
      
      String value = getStringForOS(bundle, name, osName);
      if(value == null) return defaultValue;
      if(value.startsWith("**")) {
          return defaultValue;
      }
      
      try {
          return Integer.valueOf(value).intValue();
      }
      catch(NumberFormatException numFormat) {
          return defaultValue;
      }
  }
}
