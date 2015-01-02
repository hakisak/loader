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

import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import java.text.MessageFormat;
import java.lang.reflect.*;
import java.util.logging.*;

import org.xito.reflect.*;
import org.xito.boot.util.RuntimeHelper;

/**
 * AppLauncher is used to launch an application based on an AppDesc. 
 *
 * @author  Deane Richan
 */
public class AppLauncher {
   
   private static Logger logger = Logger.getLogger(AppLauncher.class.getName());
   
   private static Hashtable registeredLaunchers = new Hashtable();
   private static Hashtable registeredDescriptors = new Hashtable();
      
   //Register built-in launcher and descriptor
   static {
      registerLauncher(new AppLauncher(), AppDesc.class, "app");
   }
   
   /** Creates a new instance of AppLauncher */
   public AppLauncher() {
   }
   
   /**
    * Register a new Launcher to handle External Launch requests 
    */
   public static boolean registerLauncher(AppLauncher launcher, Class appDescClass, String externalType) {
      
      if(registeredLaunchers.containsKey(externalType.toLowerCase())) {
         return false;
      }
      
      registeredLaunchers.put(externalType.toLowerCase(), launcher);
      registeredDescriptors.put(externalType.toLowerCase(), appDescClass);
      
      return true;
   }
   
   /**
    * Get a Launcher to handle External Launch requests
    */
   public static AppLauncher getLauncher(String externalType) {

      return (AppLauncher)registeredLaunchers.get(externalType);
   }
   
   /**
    * Get an AppDesc Class to handle External Launch requests
    */
   public static AppDesc getAppDesc(String externalType, byte[] data) throws Throwable {
      
      Class descClass = (Class)registeredDescriptors.get(externalType);
      
      if(descClass == null) {
         return null;
      }
            
      Reflection rk = Reflection.getToolKit();
      return (AppDesc)rk.callStatic(descClass, "readFileData", data);
   }
   
   /**
    * Launch an Application in the background
    */
   public Thread launchBackground(final AppDesc appDesc) {
      
      //if use seperate VM then launch external
      if(appDesc.useSeperateVM()) {
         Thread t = new Thread() {
            public void run() {
               try {
                  launchExternal(appDesc);
               }
               catch(IOException ioExp) {
                  logger.log(Level.SEVERE, ioExp.getMessage(), ioExp);
               }
            }
         };
         
         t.start();
         return t;
      }
      
      //use same VM so just spawn thread
      AppThread t = new AppThread(appDesc);
      t.start();
      return t;
   }
   
   /**
    * Launch an Application in the background
    */
   public Thread launchBackground(URL appURL) {
      
      return launchBackground(new AppDesc(appURL));
      /*
      try {
         
         AppDesc appDesc = new AppDesc(appURL);
         AppThread t = new AppThread(appDesc);
         t.start();
         return t;
      }
      catch(Throwable exp) {
         throw new RuntimeException(exp);
      }
      */
   }
      
   /**
    * Launch an Application in an External VM. This will boot a new Bootstrap using the current bootdir with 
    * Min Mode. It will then launch the App described by the Descriptor. This Descriptor object must have a
    * registered AppLauncher 
    *
    * @throws IOException if there is a problem creating the Process
    */
   public Process launchExternal(AppDesc appDesc) throws IOException {
      
      byte[] data = appDesc.getFileData();
      String type = appDesc.getExternalType();
      File appDescFile = File.createTempFile(appDesc.getName(), "." +type.toLowerCase());
      
      FileOutputStream out = new FileOutputStream(appDescFile);
      out.write(data);
      out.close();
      
      Runtime rt = Runtime.getRuntime();
      String javaCmd = getJavaCommand();  
      String classPath = System.getProperty("java.class.path");
      
      String cmds[] = new String[]{javaCmd, "-cp", classPath, "org.xito.boot.Boot",
         "-"+Boot.MIN_MODE_ARG, "-"+Boot.BOOTDIR_ARG, Boot.getBootDir().toString(),
         "-"+Boot.LAUNCH_EXT_ARG, appDescFile.toString()};      
      
      StringBuffer cmdLine = new StringBuffer();
      for(int i=0;i<cmds.length;i++) {
         cmdLine.append(cmds[i]+" ");
      }
         
      logger.info("Launching External:"+cmdLine);
      String env[] = null;
      OutputStream outStream = null;
      OutputStream errStream = null;
      InputStream inStream = null;
      
      return RuntimeHelper.exec(cmds, env, Boot.getBootDir(),  outStream, errStream, inStream);
   }
   
   /**
    * Get the command to execute a new Java Instance using this VM's Version
    */
   public static String getJavaCommand() {
      
      String javaHome = System.getProperty("java.home");
      String javaCmd = javaHome + File.separatorChar + "bin" + File.separatorChar + "java";
      
      return javaCmd;
   }
   
   /**
    * Launch an Application internally.
    */
   public void launchInternal(AppDesc appDesc) throws LaunchException {
      launchInternal(appDesc, false);
   }
   
   /**
    * Launch an Application internally.
    */
   public void launchInternal(URL appURL) throws LaunchException {
      try {
         AppDesc appDesc = new AppDesc(appURL);
         launchInternal(appDesc, false);
      }
      catch(Throwable exp) {
         throw new LaunchException(exp);
      }
   }
   
   /**
    * Launch an Application internally. This is used by Boot to process Launch External Requests. After
    * a new BootStrap/VM is started BootStrap will call this method to start the application
    */
   public void launchInternal(AppDesc appDesc, boolean showError) throws LaunchException {
      
      //Start in Application in new thread but then join that thread
      AppThread t = new AppThread(appDesc, showError);
      t.setDaemon(true);
      t.start();
      
      try {
         t.join();
      }
      catch(InterruptedException exp) {
         logger.log(Level.INFO, exp.getMessage(), exp);
      }
      
      //Throw the Launch Exception if it had one
      if(t.launchException != null)
         throw t.launchException;
   }
   
   /**
    * Launch an Application internally. This will launch in the current Thread. And
    * the current threads ContextClassLoader will be set to the new apps class loader.
    * 
    * This method should provide the actual code to launch an application. Creating a classloader,
    * downloading resources, starting the main class etc.
    */
   protected void launch(AppDesc appDesc) throws LaunchException {
      
      //create new appcontext if required
      if(appDesc.useNewAppContext()) {
         try {
            Class cls = Class.forName("sun.awt.SunToolkit");
            //Method m = cls.getMethod("createNewAppContext", new Class[]{});
            //m.invoke(null, new Object[]{});
            Reflection r = Reflection.getToolKit();
            r.callStatic(cls, "createNewAppContext");
         }
         catch(Throwable exp) {
            //We can ignore this
            logger.log(Level.WARNING, "Error setting new App Context: "+exp.getMessage(), exp);
         }
      }

      try {
         //first process the AppDescURL document if it has one
         appDesc.processAppDescURL();
                  
         AppClassLoader loader = appDesc.getNewAppClassLoader(null);  
         //Print out URLS for debugging
         if(logger.isLoggable(Level.INFO)) {
            StringBuffer urlText = new StringBuffer();
            urlText.append("ClassPath for App:"+appDesc.getName()+"\n");
            urlText.append(loader.getClassPathString());
            logger.info(urlText.toString());
         }

         //Set the Context Class Loader for this Thread
         Thread.currentThread().setContextClassLoader(loader);
         //Set ClassLoader for UI Defaults. This is in case the App wants to change its Look and Feel
         if(appDesc.useNewAppContext()) {
            javax.swing.UIManager.getDefaults().put("ClassLoader", loader);
         }
         AppInstance appInstance = new AppInstance(loader);
         
         String mainClassName = appDesc.getMainClass();
         //Try and get the mainclass from the jar manifest
         if(mainClassName == null || mainClassName.equals("")) {
            mainClassName = loader.getMainClassFromJars();
         }
         //Don't have a Main class throw an Exception
         if(mainClassName == null) {
            throw new LaunchException("no main class specified");
         }
         
         //execute the main method of the application
         Class mainCls = loader.loadClass(mainClassName);
         Method mainM = mainCls.getMethod("main", new Class[]{String[].class});
         logger.info("Calling " + mainClassName);
         String args[] = appDesc.getMainArgs();
         if(args == null) args = new String[0];
         mainM.invoke(null, new Object[]{args});
      }
      catch(Exception exp) {
         exp.printStackTrace();
         throw new LaunchException(exp);
      }
   }
   
   /**************************************
    * Thread Used to Launch an Application in the Background
    **************************************/
   public final class AppThread extends Thread {
      private AppDesc appDesc;
      private boolean showError = true;
      protected LaunchException launchException;
      
      public AppThread(AppDesc appDesc) {
         this(appDesc, true);
      }
      
      public AppThread(AppDesc appDesc, boolean showError) {
         super(new ThreadGroup(appDesc.getName()), appDesc.getName());
         this.appDesc = appDesc;
         this.showError = showError;
      }
      
      /**
       * Launch the Application. Just like any Thread start should be called not run
       */
      public void run() {
         
         try {
            launch(appDesc);
         }
         catch(LaunchException exp) {
            launchException = exp;
            if(!showError) return;

            String title = Resources.bundle.getString("app.start.error.title");
            String subtitle = MessageFormat.format(Resources.bundle.getString("app.start.error.subtitle"), appDesc.toString());
            String message = MessageFormat.format(Resources.bundle.getString("app.start.error.msg"), appDesc.toString(), exp.getClass().getName(), exp.getMessage());

            Boot.showError(title, subtitle, message, exp);
         }
      }
   }
}
