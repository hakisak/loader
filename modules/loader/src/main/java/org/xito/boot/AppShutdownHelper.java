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

/**
 * <p> 
 * This class can be used to shutdown an application instance from a the context of code running
 * in that AppInstance. The AppInstance is determined by the Class Context of the Stack. 
 * Once as AppInstance is found the destroy() method of AppInstance will be called.
 * </p>
 * <p>
 * If there is a problem locating the AppInstance then the Default Constructor will throw an Exception
 * Running Applications can use the following code inplace of System.exit calls:
 * </p>
 * <code>
 *      try {
 *         Class.forName("org.xito.boot.AppShutdownHelper").newInstance();
 *      }
 *      catch(Exception exp) {
 *        System.exit(0);
 *      }    
 * </code>
 * @author Deane Richan
 */
public final class AppShutdownHelper {
   
   /** 
    * Shutdown the application that makes an Instance of this Class
    */
   public AppShutdownHelper() throws Exception {
      
      if(Boot.isLaunchingExternal()) {
         Boot.endSession(true);
         return;
      }
      
      SecurityManager sm = System.getSecurityManager();
      if(sm instanceof BootSecurityManager) {
         AppInstance app = ((BootSecurityManager)sm).getApplication();
         if(app == null) {         
            throw new Exception("No AppInstance found for current stack context");
         }
         app.destroy();
      }
      else {
         throw new Exception("BootSecurityManager not installed can't determine current AppInstance");
      }
      
   }
   
}
