// Copyright 2007 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.prefs;

import java.io.*;
import java.security.Permission;

import java.util.prefs.*;

/**
 *
 * @author Deane Richan
 * @version $Revision: 1.1 $
 * @since $Date: 2007/09/02 01:04:58 $
 */
public class PrefManagerService {
   
   private static File prefDir;
   private static PreferencesFactory factory;
   private static Permission prefsPerm = new RuntimePermission("preferences");
   
   public static void main(String[] args) {
      
      //Install the File Preferences Factory
      //We can't seem to install this Service as a PreferenceFactory because the
      //PreferencesFactory must be loaded from the SystemClassLoader bummer
      System.setProperty("java.util.prefs.PreferencesFactory", "org.xito.prefs.file.FilePreferencesFactory");
      Thread.currentThread().setContextClassLoader(PrefManagerService.class.getClassLoader());
      
      //Initialize env by Getting System and User Roots
      Preferences sysroot = Preferences.systemRoot();
      Preferences userroot = Preferences.userRoot();
  
      //Setup the ShutDown Thread
      Runtime.getRuntime().addShutdownHook(new ShutDownThread());
   }
   
   /**
    * This Thread is used to shutdown the Perference Service Basically it stores the
    * Current Desktop and performs other shutdown operations for the Desktop
    */
   private static class ShutDownThread extends Thread {
      
      /**
       * Run Method for ShutDown Thread
       */
      public void run() {
         
         //Flush any Preferences
         try {
            Preferences.userRoot().flush();
            Preferences.systemRoot().flush();
         }
         catch(Exception exp) {
            exp.printStackTrace();
         }
      }
   }
   
}
