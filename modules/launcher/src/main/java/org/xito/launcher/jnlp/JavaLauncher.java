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

package org.xito.launcher.jnlp;

import java.io.*;
import java.text.*;
import java.util.logging.*;

import org.xito.dialog.*;
import org.xito.boot.Boot;
import org.xito.boot.AppDesc;
import org.xito.boot.AppInstance;
import org.xito.boot.AppLauncher;
import org.xito.boot.LaunchException;
import org.xito.launcher.*;

/**
 *
 * @author Deane Richan
 */
public class JavaLauncher extends AppLauncher {
   
   private static Logger logger = Logger.getLogger(JavaLauncher.class.getName());
      
   public JavaLauncher() {
      
   }
      
   /**
    * Launch an Java App internally.
    * @param showError
    */
   public void launchInternal(JavaAppDesc javaDesc, boolean showError) throws LaunchException {
      launchInternal(javaDesc);
   }
   
   /**
    * Launches Java Application. useSeperateVM is ignored for this Method
    * This method is primarily used by BootStrap to perform a launchExternal
    *
    * @param appDesc this should be an instance of JavaAppDesc or a ClassCastException will occur
    */
   protected void launch(AppDesc appDesc) throws LaunchException {
      
      JavaAppDesc javaDesc = (JavaAppDesc)appDesc;
      super.launch(javaDesc);
   }
   
   /**
    * Launch a Java Application in the Background
    * @param javaDesc
    * @return
    */
   public Thread launchBackground(final JavaAppDesc javaDesc) {

      //if use seperate VM then launch external
      if(javaDesc.useSeperateVM()) {
         Thread t = new Thread() {
            public void run() {
               try {
                  launchExternal(javaDesc);
               }
               catch(IOException ioExp) {
                  logger.log(Level.SEVERE, ioExp.getMessage(), ioExp);
                  String msg = MessageFormat.format(Resources.jnlpBundle.getString("launch.error.message"), ioExp.getMessage()); 
                  Boot.showError(Resources.jnlpBundle.getString("launch.error.title"), msg, ioExp);
               }
            }
         };
         
         t.start();
         return t;
      }
      
      JavaAppThread t = new JavaAppThread(javaDesc);
      t.start();
      return t;
   }
   
   /**
    * If the javaDesc specifies to use a seperateVM then launchExternal 
    * will be called for this javaDesc.
    * 
    * @param javaDesc
    */
   public void launchInternal(JavaAppDesc javaDesc) throws LaunchException {
      
      //Check to see if we should launch External
      if(javaDesc.useSeperateVM()) {
         try {
            launchExternal(javaDesc);
         }
         catch(IOException ioExp) {
            throw new LaunchException(ioExp);
         }
      }
      else {
         try {
            JavaAppThread t = new JavaAppThread(javaDesc);
            t.start();
            t.join();
         }
         catch(Throwable exp) {
            throw new RuntimeException(exp);
         }
      }
   }
   
   /******************************************
    * JavaAppThread
    ******************************************/
   public final class JavaAppThread extends Thread {
      private JavaAppDesc javaDesc;
      private AppInstance appInstance;
      
      public JavaAppThread(JavaAppDesc javaDesc) {
         super(new ThreadGroup(javaDesc.getName()), javaDesc.getName());
         this.javaDesc = javaDesc;
         this.setDaemon(true);
      }

       /**
        * Setup the Progress Dialog announcing the launch
        * @return
        */
      private ProgressDialog setupProgressDialog() {

          //don't use a progress Dialog if headless
          if(Boot.isHeadless()) {
              return null;
          }

          ProgressDialogDescriptor desc = new ProgressDialogDescriptor();
          desc.setTitle(Resources.javaBundle.getString("launch.title"));

          String subtitle = Resources.javaBundle.getString("launch.subtitle");
          subtitle = MessageFormat.format(subtitle, javaDesc.getName());
          desc.setSubtitle(subtitle);

          String msg = Resources.javaBundle.getString("launch.message");
          msg = MessageFormat.format(msg, javaDesc.getName());
          desc.setMessage(msg);

          desc.setButtonTypes(new ButtonType[]{new ButtonType("Hide", 99)});
          desc.setWidth(350);
          ProgressDialog dialog = new ProgressDialog(null, desc, false);
          dialog.setVisible(true);

          return dialog;
      }

      /**
       * Launch the Application. Just like any Thread start should be called not run
       */
      public void run() {
         
         ProgressDialog dialog = setupProgressDialog();
         
         try {
            launch(javaDesc);
         }
         catch(LaunchException exp) {
            if(dialog != null) {
                dialog.setVisible(false);
            }

            String msg = MessageFormat.format(Resources.javaBundle.getString("launch.error.message"), exp.getMessage());
            Boot.showError(Resources.javaBundle.getString("launch.error.title"), msg, exp);
         }
         finally {
            if(dialog != null) {
                dialog.setVisible(false);
            }
         }
         
      }
   }
}
