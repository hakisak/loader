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
import java.net.*;
import java.text.*;
import java.util.logging.*;

import javax.swing.*;

import org.xito.dialog.*;
import org.xito.boot.Boot;
import org.xito.boot.AppDesc;
import org.xito.boot.AppInstance;
import org.xito.boot.AppLauncher;
import org.xito.boot.AppClassLoader;
import org.xito.boot.CacheManager;
import org.xito.boot.CachePolicy;
import org.xito.boot.LaunchException;
import org.xito.boot.AppLauncher.AppThread;
import org.xito.reflect.*;
import org.xito.launcher.*;
import org.xito.launcher.sys.*;
import org.xito.launcher.jnlp.xml.*;

/**
 *
 * @author Deane Richan
 */
public class JNLPLauncher extends AppLauncher {
   
   private static Logger logger = Logger.getLogger(JNLPLauncher.class.getName());
      
   public JNLPLauncher() {
      
   }
   
   /**
    * Launch a JNLP App in the background
    */
   public Thread launchBackground(final JNLPAppDesc jnlpDesc) {
      
      //if use seperate VM then launch external
      if(jnlpDesc.useSeperateVM()) {
         Thread t = new Thread() {
            public void run() {
               try {
                  launchExternal(jnlpDesc);
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
      
      JNLPAppThread t = new JNLPAppThread(jnlpDesc);
      t.start();
      
      return t;
   }
   
   /**
    * Launch a JNLP App internally.
    * @param showError
    */
   public void launchInternal(JNLPAppDesc jnlpDesc, boolean showError) throws LaunchException {
      launchInternal(jnlpDesc);
   }
   
   /**
    * Launch an JNLP App Using WebStart
    */
   public void launchWithWebStart(URL url) throws LaunchException {
      File exec = getWebStartExec();
      
      //Check exec
      if(exec == null) {
         String msg = Resources.jnlpBundle.getString("webstart.location.unknown");
         throw new LaunchException(msg);
      }
      
      if(exec.exists()== false) {
         String msg = Resources.jnlpBundle.getString("webstart.not.found");
         msg = MessageFormat.format(msg, exec.toString());
         throw new LaunchException(msg);
      }
      
      //Now perform a local sys app launching using WebStart Executable and URL
      LocalAppDesc appDesc = new LocalAppDesc();
      appDesc.setName("webstart");
      appDesc.setExecutableCmd(exec.toString());
      appDesc.setArgs(url.toString());
      
      try {
         LocalAppLauncher launcher = new LocalAppLauncher();
         launcher.launch(appDesc);
      }
      catch(IOException ioExp) {
         String msg = Resources.jnlpBundle.getString("webstart.launch.error");
         msg = MessageFormat.format(msg, ioExp.getMessage());
         throw new LaunchException(msg, ioExp);
      }
   }
   
   /**
    * Get the full path to webstart Exectutable
    */
   private File getWebStartExec() {
      String vmVersion = System.getProperty("java.vm.version");
      String javaHome = System.getProperty("java.home");
      String osName = System.getProperty("os.name");
      String execName = "javaws";
      
      //Apple MacOSX      
      if(osName.startsWith("Mac OS")) {
          return new File("/usr/bin/javaws");
      }
      
      //Windows
      if(osName.startsWith("Windows")) execName = execName+".exe";
      
      File exec = null;
      if(vmVersion.startsWith("1.4.")) {
         exec = new File(javaHome, "javaws" + File.separatorChar + execName);
      }
      //Must be 1.5 or greater
      else {
         exec = new File(javaHome, "bin" + File.separatorChar + execName);
      }
      
      return exec;
   }
   
   
   /**
    * Launch an JNLP App from a URL. This will parse the jnlp file.
    * @param jnlpURL
    */
   public void launchInternal(URL jnlpURL) throws LaunchException {
      
      JNLPAppDesc jnlpDesc = new JNLPAppDesc();
      String url = jnlpURL.toString();
      jnlpDesc.setName(url.substring(url.lastIndexOf('/')));
      jnlpDesc.setJNLPAddress(jnlpURL);
      
      launchInternal(jnlpDesc);
   }
   
   /**
    * Launches JNLP Application. If useWebStart is set to true in the JNLPAppDesc then this app will be launched with
    * WebStart. useSeperateVM is ignored for this Method
    * This method is primarily used by BootStrap to perform a launchExternal
    *
    * @param appDesc this should be an instance of JNLPAppDesc or a ClassCastException will occur
    */
   protected void launch(AppDesc appDesc) throws LaunchException {
      
      JNLPAppDesc jnlpDesc = (JNLPAppDesc)appDesc;
      
      //Check for Launch in Browser and then just launch the Browser
      if(jnlpDesc.useWebStart()) {
         launchWithWebStart(jnlpDesc.getJNLPAddress());
         return;
      }
      
      //Get the jnlp address and converted to a cached resource
      URL address = jnlpDesc.getJNLPAddress();
      if(address == null) {
         throw new LaunchException("No JNLP Address specified");
      }
      
      CacheManager cm = Boot.getCacheManager();
            
      try {
         logger.info("Getting jnlp file:"+address.toString());
         CachePolicy cachePolicy = CachePolicy.ALWAYS;
         URL cachedJNLPURL = cm.getResource(address, cm.getDefaultListener(), cachePolicy);
         
         JNLPParser parser = new JNLPParser();
         JNLPNode jnlpNode = parser.parse(cachedJNLPURL);
         jnlpDesc.setJNLPNode(jnlpNode);
         
         super.launch(jnlpDesc);
      }
      catch(InvalidJNLPException badJNLP) {
         throw new LaunchException(badJNLP);
      }
      catch(IOException ioExp) {
         throw new LaunchException(ioExp);
      }
   }
   
   /**
    * If the jnlpDesc specifies to use a seperateVM then launchExternal 
    * will be called for this jnlpDesc.
    * @param jnlpDesc
    */
   public void launchInternal(JNLPAppDesc jnlpDesc) throws LaunchException {
      
      //Check to see if we should use WebStart
      if(jnlpDesc.useWebStart()) {
         launchWithWebStart(jnlpDesc.getJNLPAddress());
      }
      
      //Check to see if we should launch External
      if(jnlpDesc.useSeperateVM()) {
         try {
            launchExternal(jnlpDesc);
         }
         catch(IOException ioExp) {
            throw new LaunchException(ioExp);
         }
      }
      else {
         //Launch internal in new thread but join that thread
         try {
            JNLPAppThread t = new JNLPAppThread(jnlpDesc);
            t.start();
            t.join();
         }
         catch(Throwable exp) {
            throw new RuntimeException(exp);
         }
      }
   }
   
   /******************************************
    * JNLPAppThread
    ******************************************/
   public final class JNLPAppThread extends Thread {
      private JNLPAppDesc jnlpDesc;
      private AppInstance appInstance;
      
      public JNLPAppThread(JNLPAppDesc jnlpDesc) {
         super(new ThreadGroup(jnlpDesc.getName()), jnlpDesc.getName());
         this.jnlpDesc = jnlpDesc;
         this.setDaemon(true);
      }
      
      /**
       * Launch the Application. Just like any Thread start should be called not run
       */
      public void run() {
         
         ProgressDialogDescriptor desc = new ProgressDialogDescriptor();
         desc.setTitle(Resources.jnlpBundle.getString("launch.title"));
         
         String subtitle = Resources.jnlpBundle.getString("launch.subtitle");
         subtitle = MessageFormat.format(subtitle, jnlpDesc.getName());
         desc.setSubtitle(subtitle);
         
         String msg = Resources.jnlpBundle.getString("launch.message");
         msg = MessageFormat.format(msg, jnlpDesc.getName());
         desc.setMessage(msg);
         
         desc.setButtonTypes(new ButtonType[]{new ButtonType("Hide", 99)});
         desc.setWidth(350);
         ProgressDialog dialog = new ProgressDialog(null, desc, false);
         dialog.setVisible(true);
         
         try {
            launch(jnlpDesc);
         }
         catch(LaunchException exp) {
            dialog.setVisible(false);
            msg = MessageFormat.format(Resources.jnlpBundle.getString("launch.error.message"), exp.getMessage()); 
            Boot.showError(Resources.jnlpBundle.getString("launch.error.title"), msg, exp);
         }
         finally {
            dialog.setVisible(false);
         }
         
      }
   }
}
