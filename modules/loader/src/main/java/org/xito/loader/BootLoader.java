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

package org.xito.loader;

import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import javax.swing.*;

/**
 * The BootLoader is used primarily to be embedded into a Native launcher like Launch4J so that it
 * can easily launch the BootStrap. This class simple looks for the boot.jar in the current directory
 * and then launches the BootStrap. 
 *
 * @author Deane Richan
 */
public class BootLoader {
   
   public static void main(String args[]) {

      try {
         //find boot.jar
         File bootJar = new File("boot.jar");
         //File wlafJar = new File("winlaf-0.5.1.jar");
         if(bootJar.exists() == false) {
            throw new IOException("The BootStrap (boot.jar) cannot be located");
         }
      
         //load boot.jar
         //URL[] classPath = new URL[]{bootJar.toURL(), wlafJar.toURL()};
         URL[] classPath = new URL[]{bootJar.toURI().toURL()};
         URLClassLoader bootClassLoader = new URLClassLoader(classPath);
         Class bootClass = bootClassLoader.loadClass("org.xito.boot.Boot");
         
         //Call main method passing our arguments through to BootStrap
         Method mainMethod = bootClass.getMethod("main", new Class[]{String[].class});
         mainMethod.invoke(null, new Object[]{args});
      }
      catch(Exception exp) {
         exp.printStackTrace();
         String msg = exp.getMessage();
         try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }catch(Exception e){}
         JOptionPane.showMessageDialog(null, msg, "Error Loading BootStrap", JOptionPane.ERROR_MESSAGE, null);
         System.exit(0);
      }
   }
   
}
