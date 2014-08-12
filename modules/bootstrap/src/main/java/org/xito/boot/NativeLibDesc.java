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

package org.xito.boot;

import java.net.*;
import java.io.*;

/**
 *
 * @author Deane Richan
 */
public class NativeLibDesc {
   
   public static final String WINDOWS_OS = "windows";
   public static final String MAC_OS = "mac";
   public static final String LINUX_OS = "linux";
   
   private String os;
   private URL jarURL;
   
   /** Creates a new instance of NativeLibDesc */
   public NativeLibDesc(String os, URL jarURL) {
      
      this.os = os;
      this.jarURL = jarURL;
   }
   
   /**
    * Get the Path to the Lib Jar
    */
   public URL getPath() {
      return jarURL;
   }
   
   /**
    * Set the Path to the Lib Jar
    */
   public void setPath(URL jarURL) {
      this.jarURL = jarURL;
   }
   
   /**
    * Get the OS
    */
   public String getOS() {
      return os;
   }
      
   /**
    * Get the Current OS of this running app
    */
   public static String currentOS() {
      String os = System.getProperty("os.name");
      
      if(os.startsWith("Windows")) {
         return WINDOWS_OS;
      }
      
      if(os.equals("Mac OS X")) {
         return MAC_OS;
      }
      
      if(os.startsWith("Linux")) {
         return LINUX_OS;
      }
      
      return os;
   }
   
   /**
    * Show the URL of this NativeLib
    */
   public String toString() {
      
      return jarURL.toString();
    }
}
