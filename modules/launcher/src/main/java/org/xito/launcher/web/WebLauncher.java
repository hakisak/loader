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

package org.xito.launcher.web;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

import org.xito.boot.*;
import org.xito.boot.util.RuntimeHelper;
import org.xito.launcher.*;
//import org.jdesktop.jdic.filetypes.*;
//import org.jdesktop.jdic.desktop.*;

/**
 *
 * @author DRICHAN
 */
public class WebLauncher {

   
   /** Creates a new instance of WebLauncher */
   public WebLauncher() {
   }
   
   /**
    * Launches web browser with URL in a new thread
    */
   public void launch(final URL url) {
  
       
      //On App MacOS just call the open command
      if(NativeLibDesc.currentOS() == NativeLibDesc.MAC_OS) {
          try {
              RuntimeHelper.exec("open", url.toString(), null, null);
          }
          catch(Exception exp) {
              org.xito.dialog.DialogManager.showError(null, "Web Error", "Error Launching Browser", exp);
          }
          return;
      } 
      
      //Other OS's us Jdesktop code
      Thread t = new Thread() {
         public void run() {
            try {
               Desktop.getDesktop().browse(url.toURI());
            }
            catch(Exception exp) {
               exp.printStackTrace();
            }
         }
      };

      t.start();
   }

}
