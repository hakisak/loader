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

package org.xito.jdic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdesktop.jdic.init.JdicManager;
import org.xito.boot.Boot;
import org.xito.boot.NativeLibDesc;

/**
 * Initialize any Features of the JDIC Service
 * @author Deane Richan
 */
public class JDICService {
   
   private final static Logger logger = Logger.getLogger(JDICService.class.getName());
   
   /**
    * main method
    */
   public static void main(String args[]) {
      if(Boot.getCurrentOS() == Boot.WINDOWS_OS) {
         initWindows();
      }
   }
   
   /**
    * Provide a fix for the IeEmbed.exe problem.
    *
    */
   private static void initWindows() {
      
      String IeEMBED = "IeEmbed.exe";
      
      //Windows JDIC wants the IeEmbed.exe in the same directory as the jdic.jar
      //so by the time this code as executed the native libs will have been extracted
      //and we can copy that IeEmbed.exe to the right directory
      try {
         String jdicDir = (new URL(JdicManager.class.getProtectionDomain()
               .getCodeSource().getLocation(), ".")).openConnection().getPermission().getName();
         
         jdicDir = (new File(jdicDir)).getCanonicalPath();
         
         String sourceDir = new File(jdicDir, "windows" + File.separator + "jdic_windows.jar.native")
                              .getCanonicalPath();
         
         //Copy the IeEmbed.exe file
         File srcFile = new File(sourceDir, IeEMBED);
         if(srcFile.exists() == false) {
            logger.log(Level.SEVERE, "IeEmbed.exe was not found at: " + srcFile.getCanonicalPath());
            return;
         }
         
         logger.fine("Copying: " + IeEMBED + " to: " + jdicDir);
         copyFile(srcFile, new File(jdicDir, IeEMBED));
      }
      catch(Exception exp) {
         logger.log(Level.SEVERE, exp.getMessage(), exp);
      }
   }
   
   /**
    * Copy a file
    * @param srcFile
    * @param toFile
    * @throws IOException
    */
   private static void copyFile(File srcFile, File toFile) throws IOException {
            
      FileOutputStream out = new FileOutputStream(toFile);
      FileInputStream in = new FileInputStream(srcFile);
      
      byte[] buf = new byte[1024];
      int cb = in.read(buf);
      while(cb > 0) {
         out.write(buf, 0, cb);
         cb = in.read(buf);
      }
   }
   
}
