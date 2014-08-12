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

package org.xito.prefs.file;

import java.io.*;
import java.security.*;

//import org.xito.prefs.util.*;
import java.util.prefs.*;
import org.xito.boot.*;

/**
 *
 * @author Deane Richan
 * @version $Revision: 1.4 $
 * @since $Date: 2007/09/02 00:50:15 $
 */
public class FilePreferencesFactory implements PreferencesFactory {
   File prefDir;
   FilePreferences userRootNode;
   FilePreferences systemRootNode;
   
   /**
    * This may be called by JRE Boot Sequence
    */
   public FilePreferencesFactory() {
      this(new File(Boot.getUserAppDir(), "prefs"));
   }
   
   /** Creates new FilePreferencesFactory */
   public FilePreferencesFactory(File pPrefDir) {
      prefDir = pPrefDir;
      
      //Create the Dir
      if(prefDir.exists() == false) {
         prefDir.mkdir();
      }
      
      //Create Each Root Node
      File _userDir = new File(prefDir, "user");
      File _sysDir = new File(prefDir, "system");
      _userDir.mkdir();
      _sysDir.mkdir();
      
      userRootNode = new FilePreferences(_userDir);
      systemRootNode = new FilePreferences(_sysDir);
   }
   
   /**
    * Returns the user root preference node corresponding to the calling
    * user.  In a server, the returned value will typically depend on
    * some implicit client-context.
    */
   public Preferences userRoot() {
      return userRootNode;
   }
   
   /**
    * Returns the system root preference node.  (Multiple calls on this
    * method will return the same object reference.)
    */
   public Preferences systemRoot() {
      return systemRootNode;
   }
   
}
