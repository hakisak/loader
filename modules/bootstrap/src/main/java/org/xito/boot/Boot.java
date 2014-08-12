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

import java.lang.reflect.InvocationTargetException;
import java.security.*;
import java.text.MessageFormat;

import java.util.*;
import java.util.Timer;
import java.util.logging.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

//Used to load the System Look and Feel

import org.xito.dialog.*;
import org.xito.reflect.*;
import org.xito.boot.ui.Defaults;
import org.xito.boot.util.WeakVector;

/**
 * <p>
 * Boot is the main entry point to the BootStrap Environment. Boot will read properties from the command line and
 * from a boot.properties from the boot dir.
 * </p>
 * <p>
 * Unless a different main application is specified in boot.properties BootStrap
 * will call Shell.main which will startup specified boot and startup services
 * </p>
 * <p><b>Optional Command Line Arguments</b></p>
 * <pre>
 * -bootdir  Directory where bootstrap environment is loaded from<br>
 * -nogui    Do not show any gui messages etc. Run in headless mode<br>
 * </pre>
 * <p>
 * All command line options of the form -argname argvalue will be stored and can be read later by
 * calling Boot.getArgProperties()
 * </p>
 * <p><b>boot.properties</b></p>
 * <p>
 * The BootStrap will read a set of properties from a boot.properties file found in
 * the bootdir. The following properties can be specified:
 * </p>
 * <pre>
 * app.name   Short name of this application used to store user specific files in user's home
 * app.icon   URL to image file for Applications icon. Can be relative to bootdir
 * nativeLAF  Set to true to use System native look and feel false otherwise. Defaults to true.
 * app.main   Class name of main application defaults to Shell
 * </pre>
 * <p>All properties in boot.properties will also be added to System properties. Therefore you can use boot.properties to set System
 * wide VM settings etc.
 * </p>
 *
 * @author Deane Richan
 * @version $revision$
 */
public class Boot {

   private static final Logger logger = Logger.getLogger(Boot.class.getName());
   
   /** command line argument for nogui option */
   public static final String NOGUI_ARG = "nogui";
   
   /** command line argument for minimum service mode */
   public static final String MIN_MODE_ARG = "minmode";
   
   /** command line argument to launch an external app */
   public static final String LAUNCH_EXT_ARG = "launchExternal";
   
   /** property placed in System.properties for nogui setting */
   public static final String NOGUI_PROP = "boot.nogui";
   
   /** command line argument for passing additional system properties */
   public static final String PROPS_FILE_ARG = "propfile";
   
   /** command line argument for bootdir */
   public static final String BOOTDIR_ARG = "bootdir";
   
   /** property placed in System.properties for boot dir */
   public static final String BOOTDIR_PROP = "boot.dir";
   
   /** property used to turn off built-in security manager */
   public static final String NO_SECURITY_PROP = "boot.no.security";
   
   /** property used to turn off built-in security manager */
   public static final String NO_SECURITY_WARNING_PROP = "boot.no.security.warning";
   
   /** property used to turn off auto trusting of boot dir code. Defaults to true **/
   public static final String TRUST_BOOT_DIR = "trust.boot.dir";
   
   /** property used to turn off built-in caching, defaults to true*/
   public static final String BOOT_USE_CACHE = "boot.use.cache";
   
   /** property used to set the caching dir, defaults to {app.base.dir}/cache */
   public static final String BOOT_CACHE_DIR = "boot.cache.dir";
   
   /** property used to set whether we should check for UI. Defaults to true, if nogui mode this property has no effect */
   public static final String CHECK_UI = "boot.checkui";
   
   /** Short name of application bootstrap is booting */
   public static final String APP_NAME = "app.name";
   
   /** Descriptive Name of application  */
   public static final String APP_DISPLAY_NAME = "app.display.name";
   
   /** url to app icon relative to bootdir */
   public static final String APP_ICON = "app.icon";
   
   /** boot properties option for native L&F can be true false. Defaults to true */
   public static final String NATIVE_LAF_PROP="native.laf";
   
   /** property name of application dir for user settings */
   public static final String APP_BASEDIR = "app.base.dir";
   
   /** property name of url to use to Check Online */
   public static final String CHECK_ONLINE_URL_PROP = "check.online.url";
   
   /** Default URL used to test Online Connection */
   public static final String DEFAULT_CHECK_ONLINE_URL = "http://www.google.com";
   
   /** File name of offline properties */
   public static final String OFFLINE_PROPS_FILE = "offline.properties";
   
   /** property name for offline status */
   public static final String OFFLINE_PROP = "offline";
   
   /** arg name for offline status */
   public static final String OFFLINE_ARG = "offline";
   
   /** Constant for WINDOWS_OS */
   public static final String WINDOWS_OS = NativeLibDesc.WINDOWS_OS;
   
   /** Constant for MAC_OS */
   public static final String MAC_OS = NativeLibDesc.MAC_OS;
   
   /** Constant for LINUX_OS */
   public static final String LINUX_OS = NativeLibDesc.LINUX_OS;
   
   private static boolean initialized_flag = false;
   private static boolean quicklaunch_flag = false;
   private static URL jnlpCodeBase = null;
   private static boolean nogui = false;
   private static boolean minMode = false;
   private static boolean offline = false;
   private static boolean launch_external = false;
   private static Properties bootProps = new Properties();
   private static Properties argProps = new Properties();
   private static File bootDir;
   private static ThreadGroup bootGroup;
   private static String appName;
   private static String appDisplayName;
   private static boolean lookAndFeelInstalled = false;
   private static CacheManager cacheManager;
   private static ImageIcon appIcon;
   private static ServiceManager serviceManager;
   private static Vector offlineListeners = new Vector();
      
   /**
    * Setup the Security Manager
    */
   private static void setupSecurity() {
      
      //Check to see if they want Security Turned Off
      String noSecurityProp = bootProps.getProperty(NO_SECURITY_PROP);
      if(noSecurityProp != null && noSecurityProp.equals("true") && !isQuickLaunch()) {
         logger.log(Level.WARNING, "BUILT IN SECURITY MANAGER is DISABLED !!!");
         return;
      }
      
      bootGroup = Thread.currentThread().getThreadGroup();
      BootSecurityManager sm = new BootSecurityManager();
      Policy bootPolicy = new BootPolicy(sm);
      Policy.setPolicy(bootPolicy);
      
      System.setSecurityManager(sm);
   }
   
   /**
    * Check to make sure there is a Security Manager Installed
    */
   private static void checkSecurityManager() {

      //if we have a security manager just return
      if(System.getSecurityManager() != null) {
         return;
      }

      //log the warning
      logger.log(Level.WARNING, "********************************/n NO SECURITY MANAGER INSTALLED !!!/n********************************");
      
      //If we are headless then just return
      if(Boot.isHeadless()) {
         return;
      }
      
      //If the security warning has been turned off then just return
      String noSecurityWarning = bootProps.getProperty(NO_SECURITY_WARNING_PROP);
      if(Boolean.parseBoolean(noSecurityWarning)) {
         return;
      }
      
      //Tell the user that there is no security manager
      try {
         SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
               try {
                  //Thread.currentThread().setContextClassLoader(Boot.class.getClassLoader());

                  String title = Resources.bundle.getString("boot.security.warning.title");
                  title = MessageFormat.format(title, Boot.getAppDisplayName());
                  String subtitle = Resources.bundle.getString("boot.no.security.subtitle");
                  String msg = Resources.bundle.getString("boot.no.security.warning");

                  DialogDescriptor desc = new DialogDescriptor();
                  desc.setWindowTitle(title);
                  desc.setTitle(title);
                  desc.setSubtitle(subtitle);
                  desc.setMessage(msg);
                  desc.setMessageType(DialogManager.WARNING_MSG);
                  desc.setType(DialogManager.YES_NO);
                  desc.setWidth(300);
                  desc.setHeight(300);

                  int result = DialogManager.showDialog(desc);
                  if(result == DialogManager.NO) {
                     Boot.endSession(true);
                  }
               }
               catch(Throwable t) {
                  logger.log(Level.SEVERE, t.getMessage(), t);
               }
            }
         });
      }
      catch (InterruptedException e) {
         logger.log(Level.SEVERE, e.getMessage(), e);
      }
      catch (InvocationTargetException e) {
         logger.log(Level.SEVERE, e.getMessage(), e);
      }

   }
   
   /**
    * Return the BootStrap Thread Group used to 
    */
   private static ThreadGroup getThreadGroup() {
      return bootGroup;
   }
   
   /** 
    * Return true if BootStrap is in Debug Mode
    */
   public static boolean isDebug(){
      return false;
   }
   
   /**
    * return true if BootStrap is in Headless mode
    */
   public static boolean isHeadless() {
      return nogui;
   }
   
   /**
    * return true if BootStrap is in min-mode
    */
   public static boolean isMinMode() {
      return minMode;
   }
   
   /** 
    * Get the Application Name
    */
   public static String getAppName() {
      return appName;
   }
   
   /** 
    * Get the Application Display Name
    */
   public static String getAppDisplayName() {
      return appDisplayName;
   }
   
   /**
    * Get the Boot Dir
    */
   public static File getBootDir() {
      return bootDir;
   }
   
   /**
    * Main Entry point for Boot Environment
    */
   public static void main(String args[]) {
      if(!isInitialized()) {
         setArgProperties(args);
         initialize();
      }
   }
   
   /** 
    * Return true if the BootStrap has been Initialized
    */
   public static boolean isInitialized() {
      return initialized_flag;
   }
   
   /** 
    * Return true if the BootStrap has been launched from WebStart
    */
   public static boolean isQuickLaunch() {
      return quicklaunch_flag;
   }
   
   /**
    * Initialize the BootStrap. 
    * This will setup the Logging, Cache Manager, Security Manager, and boot services etc.
    *
    * If the Environment is already initialized then an error will be displayed.
    */
   public static void initialize() {
     
      if(isInitialized()) {
         showError("Initialize Error", "Cann't execute Boot.main. BootStrap already loaded.", null);
         return;
      }
      
      try {          
         initialized_flag = true;
         
         //Print out Version
         System.out.println("Xito BootStrap Version:"+Boot.class.getPackage().getImplementationVersion());
         System.out.println("==============================================");
         
         //Check for Quick Launch Mode
         checkQuickLaunch();
         
         //Process Boot Properties
         processBootProperties();

         //Setup Logging File
         setupLogging();
               
         //Setup Security
         setupSecurity();
         
         //Setup Cache Manager
         setupCache();
      
         //Setup App Icon
         setupAppIcon();
         
         //Setup Proxy Settings
         ProxyConfig.initialize();
         
         //Check Online Status
         checkOnline();
         
         //If we are in QuickLaunch Mode
         //Then we just check for up to date installed BootStrap, 
         //Launch it and exit
         if(isQuickLaunch()) {       
            QuickLaunch.init(jnlpCodeBase); 
            return;
         }
         
         //Start Services
         serviceManager = new ServiceManager();
         serviceManager.startAllServices();

         //Check for Installed Security Manager
         checkSecurityManager();

         //Check to see if we should launch any External Apps
         processLaunchExternal();

         //Check to see if a UI is available. This does nothing if in Headless mode
         checkUIAvailable();
      }
      catch(Throwable t) {
         String msg = t.getMessage();
         if(msg == null || msg.equals("")) {
            msg = "Unknown Error occured.";
         }
         Boot.shutdownError(msg, t);
      }
   }
   
   /**
    * When running in QuickLaunch mode we need to check that the local
    * installed copy of BootStrap is up to date. If not update it and
    * then launch that BootStrap in a new Process
    */
   private static void updateLocalBootStrap() {
      showError("BootStrap", "Update Check!", null);
   }
   
   /**
    * Checks to see if BootStrap is being started from Java WebStart and then
    * will install itself on the local machine and then launch itself
    */
   private static void checkQuickLaunch() {
      
      Reflection reflectKit = Reflection.getToolKit();
      Object basicService;
      try {
         Class jnlpSrvMrgClass = Class.forName("javax.jnlp.ServiceManager");
         basicService = reflectKit.callStatic(jnlpSrvMrgClass, "lookup", "javax.jnlp.BasicService");
         jnlpCodeBase = (URL)reflectKit.call(basicService, "getCodeBase");
         offline = reflectKit.callBoolean(basicService, "isOffline");  
         
         //set the quick launch properties
         quicklaunch_flag = true;
         bootProps = new Properties();
         bootProps.setProperty(APP_NAME, "xito_quicklaunch");
         //set in the JNLP file
         String displayName = System.getProperty("quicklaunch.app.display.name");
         if(displayName == null) displayName = "Xito QuickLaunch";
         bootProps.setProperty(APP_DISPLAY_NAME, displayName);
      }
      catch(Exception exp) {
         //showError("BootStrap", "QuickLaunch Check", exp);
         //If we got an exception here we are not running in WebStart
         //exp.printStackTrace();
      }
   }
   
   /**
    * Get the Stored Offline Status. This is the status the application was last 
    * in when it was last executed
    */
   private static boolean getStoredOfflineStatus() {
      
      File file = new File(getUserAppDir(), OFFLINE_PROPS_FILE);
      Properties props = new Properties();
      props.setProperty(OFFLINE_PROP, (isOffline()?"true":"false"));
      
      try {
         props.load(new FileInputStream(file));
         String status = props.getProperty(OFFLINE_PROP);
         if(status != null && status.equals("true")) {
            return true;
         }
         else {
            return false;
         }
      }
      catch(IOException ioExp) {
         logger.warning("Error Storing Offline Status:"+ioExp.getMessage());
      }
      
      //Online is the default
      return false;
   }
   
   /**
    * Store the Current Offline Status into a Properties file
    */
   private static void storeOfflineStatus() {
      
      File file = new File(getUserAppDir(), OFFLINE_PROPS_FILE);
      Properties props = new Properties();
      props.setProperty(OFFLINE_PROP, (isOffline()?"true":"false"));
      logger.info("Storing Offline Properties");
      try {
         FileOutputStream out = new FileOutputStream(file);
         props.store(out, null);
         out.close();
      }
      catch(IOException ioExp) {
         logger.warning("Error Storing Offline Status:"+ioExp.getMessage());
      }
   }
   
   /**
    * Checks to see if the Machine is Online. Shows a org.xito while this is happening
    * so the user can just choose to go into Offline Mode
    */
   private static void checkOnline() {
      
      //If app wants Offline Mode then just skip Check
      //If Quick Launch already set offline then we also can skip Check
      if(isOffline()) {
         return;
      }
      
      //Get the URL we use to check online status with
      String checkURL = System.getProperty(CHECK_ONLINE_URL_PROP);
      if(checkURL == null) {
         checkURL = DEFAULT_CHECK_ONLINE_URL;
      }
                        
      final OnlineTask task = new OnlineTask(checkURL);
      
      //If in Headless mode just do the check 
      if(Boot.isHeadless()){
         try {
            Thread checkThread = new Thread(task);
            checkThread.start();
            checkThread.join();
         }
         catch(Exception exp) {
            logger.log(Level.WARNING, exp.getMessage(), exp);
         }
         
         return;
      }
      else {
         SwingUtilities.invokeLater(new Runnable(){
            public void run() {
               //Must not be in Headless mode so show a progress org.xito during the Online Check
               ProgressDialogDescriptor desc = new ProgressDialogDescriptor();
               desc.setTitle(Resources.bundle.getString("check.online.xito.title"));
               desc.setSubtitle(Resources.bundle.getString("check.online.xito.subtitle"));
               desc.setMessage(Resources.bundle.getString("check.online.xito.description"));
               boolean previousOffline = getStoredOfflineStatus();
               String btnText = null;
               if(previousOffline) {
                  btnText = Resources.bundle.getString("check.online.xito.previous.offline");
               }
               else {
                  btnText = Resources.bundle.getString("check.online.xito.previous.online");
               }

               int OFFLINE_MODE = 999;
               desc.setButtonTypes(new ButtonType[]{new ButtonType(btnText, OFFLINE_MODE)});
               desc.setShowButtonSeparator(true);
               desc.setWidth(375);
               desc.setHeight(225);
               desc.setRunnableTask(task);

               ProgressDialog dialog = new ProgressDialog(null, desc, true);
               dialog.setVisible(true);
               if(dialog.getResult() == OFFLINE_MODE) {
                  dialog.cancelRunnableTask();
                  setOffline(true);
               }
            }
         });

      }
         
   }
   
   /**
    * Return true if the Application is in Offline Mode. False if Online
    */
   public static boolean isOffline() {
      return offline;
   }
   
   /**
    * Set to true if the application should be Offline. Set to false to be Online
    */
   public static void setOffline(boolean offlineMode) {
      offline = offlineMode;
      
      //emit events
      Iterator it = offlineListeners.iterator();
      while(it.hasNext()) {
         OfflineListener listener = (OfflineListener)it.next();
         if(listener != null && offline) {
            listener.offline();
         }
         else if(listener != null && !offline) {
            listener.online();
         }
      }
   }
   
   /**
    * Check to see if a UI is displayed
    */
   private static void checkUIAvailable() {
      
      String checkUIStr = System.getProperty(CHECK_UI);
      //if not in nogui mode check for a visible window
      if(Boot.isHeadless()==false && (checkUIStr == null || checkUIStr.equals("true"))) {
         
         //We need to check for a UI. Create a Timer to Check for the UI in 30 seconds
         java.util.Timer timer = new java.util.Timer(true);
         timer.schedule(new TimerTask() {
            public void run() {
               try {
                  BootSecurityManager sm = (BootSecurityManager)System.getSecurityManager();
                  //no security manager so we don't need to test
                  if(sm == null) return;
                  
                  if(sm.getExitClass() == null) sm.setExitClass(Boot.class);
                  if(!sm.checkWindowVisible()) {
                     String msg = Resources.bundle.getString("boot.no.user.interface");
                     msg = MessageFormat.format(msg, Boot.getAppDisplayName());
                     Boot.shutdownError(msg, null);
                  }
                  else {
                     logger.info("Visible User Interface Detected!");
                  }
               }
               catch(ClassCastException badCast) {
                  //Not using Our security manager can't check for UI
                  logger.info("Not using BootSecurityManager. Can't check for No UI");
               }
            }
         }, 30000); //30 seconds
         
      } 
      else {
         logger.info("Visible User Interface Check Disabled!");
      }
   }
   
   /**
    * Return true if the BootStrap is in the mode of Launching an External App
    */
   public static boolean isLaunchingExternal() {
      return launch_external;
   }
   
   /**
    * Process any launchExternal Application to launch a Java Application
    * if this launch fails then end the session
    */
   private static void processLaunchExternal() {
      
      String externalFile = Boot.argProps.getProperty(LAUNCH_EXT_ARG);
      if(externalFile == null) {
         logger.info("No External File to Launch");
         return;
      }
      
      logger.info("Launching external file:"+externalFile);
      launch_external = true;
      try {
         File extFile = new File(externalFile);
         String type = extFile.getName();
         if(type.lastIndexOf('.')>-1) {
            type = type.substring(type.lastIndexOf('.')+1);
            type = type.trim();
         }

         if(type == null) {
             throw new RuntimeException("Unknown launch type for file: " + externalFile);
         }

         logger.info("Getting launcher for type:"+type);
         
         //read in file data
         FileInputStream in = new FileInputStream(extFile);
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         byte[] buf = new byte[1024];
         int cbytes = in.read(buf);
         while(cbytes != -1) {
            out.write(buf, 0, cbytes);
            cbytes = in.read(buf);
         }
         in.close();

         byte[] data = out.toByteArray();
         out.close();

         AppDesc appDesc = AppLauncher.getAppDesc(type, data);
         AppLauncher appLauncher = AppLauncher.getLauncher(type.toLowerCase());
         
         if(appLauncher != null && appDesc != null) {
            appLauncher.launchInternal(appDesc, false);
         }
         else {
            throw new Exception("Error Launching External Application: The Launcher type is unknown");
         }
      }
      catch(Throwable exp) {
         logger.log(Level.SEVERE, exp.getMessage(), exp);
         String expMsg = exp.getMessage();
         if(expMsg == null) expMsg = "unknown";
         
         String msg = MessageFormat.format(Resources.bundle.getString("launch.external.error"), expMsg);
         //Exit the VM because this launchExternal had a problem
         Boot.shutdownError(msg, exp);
      }
   }
   
   /**
    * End This Session
    * @param force if true does not ask the user if the session should end
    */
   public static void endSession(boolean force) {
      storeOfflineStatus();
      
      if(serviceManager != null) {
         serviceManager.endSession();
      }
      
      System.exit(0);
   }
   
   /**
    * Get the Directory where Services are cached
    */
   private static void setupCache() {

      //First Boot Stream Manager
      URLStreamManager.main(new String[0]);
      
      //Check for use Cache Property
      String useCacheStr = System.getProperty(BOOT_USE_CACHE);
      boolean cacheDisabled = false;
      if(useCacheStr != null && useCacheStr.equals("false")) {
         cacheDisabled = true;
         logger.info("CACHE MANAGER IS DISABLED !!!");
      }
      
      //Check for Cache Dir Property
      String cacheDirStr = System.getProperty(BOOT_CACHE_DIR);
      File cacheDir = null;
      if(cacheDirStr != null) {
         cacheDir = new File(cacheDirStr);
         if(!cacheDir.exists()) {
            if(!cacheDir.mkdir()) {
               cacheDir = null;
               logger.warning("COULD NOT USE SPECIFIED CACHE DIR: "+cacheDir);
            }
         }
      }
      
      if(cacheDir == null) {
         cacheDir = new File(getUserAppDir(), "cache");
      }
      
      logger.info("Using Cache Dir: "+cacheDir.toString());
      cacheManager = new CacheManager(cacheDir, cacheDisabled);
      /*
      try {
         URLStreamManager.getDefaultManager().addProtocolHandler("cache", cacheManager);
      }
      catch(ProtocolSetException exp) {
         shutdownError(exp.getMessage(), exp);
      }
       */
   }
   
   /**
    * Get the currently installed ServiceManager
    */
   public static ServiceManager getServiceManager() {
      return serviceManager;
   }
   
   /**
    * Get the currently installed CacheManager
    */
   public static CacheManager getCacheManager() {
      return cacheManager;
   }
      
   /**
    * Check for logging File in Boot Directory
    */
   private static void setupLogging() {
      File logConfigFile = new File("./logging.properties");
      if(!logConfigFile.exists()) {
         logConfigFile = new File(bootDir, "/logging.properties");
      }
      
      if(!logConfigFile.exists()) return;
      
      try {
         FileInputStream in = new FileInputStream(logConfigFile);
         LogManager.getLogManager().readConfiguration(in);
      }
      catch(IOException ioExp) {
         logger.log(Level.SEVERE, "Error reading "+logConfigFile.getAbsolutePath(), ioExp);
      }
   }
   
   /**
    * Process Boot Properties using Arguments
    */
   private static void processBootProperties() {
      
      appName = null;
      
      //boot dir
      String bootDir_str = argProps.getProperty(BOOTDIR_ARG);
      if(bootDir_str == null) {
         //Check a System property for this option also
         bootDir_str = System.getProperty(BOOTDIR_PROP);
         if(bootDir_str == null) {
            bootDir_str = "";
         }
      }
      
      logger.info("bootdir arg:"+ bootDir_str);
      //Load Boot Properties
      try{
         bootDir = new File(bootDir_str);
         bootDir_str = bootDir.getCanonicalPath();
         bootDir = new File(bootDir_str);
         if(!bootDir.exists() || !bootDir.isDirectory()) {
            shutdownError(Resources.bundle.getString("no.boot.dir.error"), null);
         }

         logger.info("bootdir:"+ bootDir_str);

         //If not in quick mode then load boot properties from 
         //the boot dir. If in QuickLaunch mode then the boot properties
         //will have already been set for quick launch mode
         if(isQuickLaunch() == false) {
            bootProps.load(new FileInputStream(new File(bootDir_str, "boot.properties")));
         }

         //Log all Boot Properties
         logProperties(bootProps, "Boot Properties", Level.INFO);

         //Log all System Properties
         updateSystemProps(bootProps);
         logProperties(System.getProperties(), "System Properties", Level.INFO);
      }
      catch(FileNotFoundException exp) {
         shutdownError(Resources.bundle.getString("boot.properties.not.found.error"), exp);
      }
      catch(IOException ioExp) {
         shutdownError(Resources.bundle.getString("boot.properties.read.error"), ioExp);
      }
            
      //app name
      appName = bootProps.getProperty(APP_NAME);
      if(appName == null) {
         shutdownError(Resources.bundle.getString("no.app.name.error"), null);
      }
      
      //app display name
      appDisplayName = bootProps.getProperty(APP_DISPLAY_NAME);
      if(appDisplayName == null) appDisplayName = appName;
            
      //headless argument
      if(argProps.containsKey(NOGUI_ARG) && argProps.getProperty(NOGUI_ARG).equals("true")) {
         nogui = true;
         System.setProperty(NOGUI_PROP, "true");
      }
      
      //headless boot property
      if(bootProps.containsKey(NOGUI_PROP) && bootProps.getProperty(NOGUI_PROP).equals("true")) {
         nogui = true;
         System.setProperty(NOGUI_PROP, "true");
      } 
                  
      //basedir
      String appBaseDir = System.getProperty(APP_BASEDIR);
      File baseDir = null;
      if(appBaseDir == null) {
         baseDir = new File(System.getProperty("user.home"), '.'+appName);
      }
      else {
         baseDir = new File(appBaseDir);
      }
      
      if(baseDir.exists()==false) {
         if(baseDir.mkdir()==false) {
            String msg = MessageFormat.format(Resources.bundle.getString("no.user.settings.dir.error"), baseDir.toString());
            shutdownError(msg, null);
         }
      }else if(baseDir.isFile()){
         String msg = MessageFormat.format(Resources.bundle.getString("no.user.settings.dir.error"), baseDir.toString());
         shutdownError(msg, null);
      }
      System.setProperty(APP_BASEDIR, baseDir.toString());  
      logger.info("App Base Dir: "+baseDir.toString());
            
      //Native Look and Feel
      if(!bootProps.containsKey(NATIVE_LAF_PROP) || bootProps.getProperty(NATIVE_LAF_PROP).equals("true")) {
         setupNativeLookAndFeel();
      }
      //else use Java default Look and Feel
      
      //Check for restricted window property
      String windowWarning = Resources.bundle.getString("restricted.window.banner");
      if(System.getProperty("awt.appletWarning")== null) {
         System.setProperty("awt.appletWarning", windowWarning);
      }
      
      //Check for Min-Mode
      String minModeStr = getArgProperties().getProperty(MIN_MODE_ARG);
      if(minModeStr != null && minModeStr.equals("true"))
         minMode = true;
      
      //Check for Offline Mode
      String offlineStr = getArgProperties().getProperty(OFFLINE_ARG);
      if(offlineStr != null && offlineStr.equals("true")) {
         setOffline(true);
      }
      
      //load any specified system props file
      String propsFile = getArgProperties().getProperty(PROPS_FILE_ARG);
      if(propsFile != null) {
         try {
            File propFile = new File(propsFile);
            Properties props = new Properties();
            props.load(new FileInputStream(propFile));
            //add all properties
            Enumeration ep = props.keys();
            while(ep.hasMoreElements()) {
               String key = (String)ep.nextElement();
               System.getProperties().put(key, props.getProperty(key));
            }
         }
         catch(IOException ioExp) {
            logger.log(Level.SEVERE, "Error loading system properties from file:"+propsFile, ioExp);
         }
      }
   }
   
   /**
    * Return the value of a boot property
    * @param name
    * @param defaultValue
    * @return
    */
   public static String getBootProperty(String name, String defaultValue) {
      return bootProps.getProperty(name, defaultValue);
   }
   
   /**
    * Log Properties to a logger
    */
   private static void logProperties(Properties props, String title, Level level) {
      
      if(logger.isLoggable(level)) {
            StringBuffer logMsg = new StringBuffer();
            logMsg.append(title + ":\n===================\n");
            Iterator propNames = props.keySet().iterator();
            while(propNames.hasNext()) {
               String propName = (String)propNames.next();
               logMsg.append(propName+"="+props.getProperty(propName)+"\n");
            }
            logger.log(level, logMsg.toString());
         }
   }
   
   /** 
    * Setup the Swing LAF to use the Native Platform LAF.
    * This is the default unless boot.properties contains nativeLAF=false
    */
   private static void setupNativeLookAndFeel() {
      
      //Find WLAF Class if you can
      Class wlafClass = null;
      if(System.getProperty("os.name").startsWith("Windows")) {
         try {
            wlafClass = Class.forName("net.java.plaf.windows.WindowsLookAndFeel");
         }
         catch(ClassNotFoundException exp) {
            //Just skipp it and ise the Default Look and Feel
         }
      }
            
      try {
         if(wlafClass != null) {
            UIManager.setLookAndFeel("net.java.plaf.windows.WindowsLookAndFeel");
         }
         else {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         }
         lookAndFeelInstalled = true;
      }
      catch(Exception exp) {
         //This shouldn't happen but we will log an error
         logger.log(Level.WARNING, "Could not load System Look and Feel Class.", exp);
      }
   }
   
   /**
    * Update the System properties with the properties provided
    */
   private static void updateSystemProps(Properties props) {
      Enumeration keys = props.keys();
      while(keys.hasMoreElements()) {
         String keyName = (String)keys.nextElement();
         System.setProperty(keyName,  props.getProperty(keyName));
      }
   }
      
   /**
    * There was an error during boot. display error and exit
    */
   private static void shutdownError(String error, Throwable exp) {
      
      showError(Resources.bundle.getString("boot.error.title"), error, exp);
      
      System.err.println(error);
      endSession(true);
   }
   
   /**
    * Set Arg Properties from Arguments in string array
    * Each argument name and value will be placed in the returned properties object
    * @param args
    */
   private static void setArgProperties(String args[]) {
      argProps = processArgs(args);
   }
   
   /**
    * Return the Arguments passed to Boot on Startup
    */
   public static Properties getArgProperties() {
      return argProps;
   }
   
   /**
    * Returns the userdir for this application.
    */
   public static File getUserAppDir() {
      String dir = System.getProperty("user.home");
      
      return new File(dir, '.'+getAppName());
   }
   
   public static boolean isMacOS() {
      return (getCurrentOS().equals(NativeLibDesc.MAC_OS));
   }
   
   public static boolean isWindowsOS() {
      return (getCurrentOS().equals(NativeLibDesc.WINDOWS_OS));
   }
   
   /**
    * Return Current OS. Returns WINDOWS, MAC, LINUX constants or
    * os.name from System property if not WINDOWS, MAC, or LINUX
    */
  public static String getCurrentOS() {
      return NativeLibDesc.currentOS();
  }
   
   /**
    * Get App Icon
    */
   public static ImageIcon getAppIcon() {
      return appIcon;
   }
   
   /**
    * Setup the AppIcon
    */
   private static void setupAppIcon() {
      
      String appIconStr = System.getProperty("app.icon");
      logger.log(Level.INFO, "app.icon:"+appIconStr);
      if(appIconStr == null) {
         appIcon = null;
         return;
      }
      
      try {
         URL iconURL = null;
         iconURL = new URL(Boot.getBootDir().toURL(), appIconStr);
         logger.log(Level.INFO, "Icon URL:"+iconURL.toString());
         
         //Use cache if icon is http or file url
         if(appIconStr.startsWith("http") || appIconStr.startsWith("file")) {
            Boot.getCacheManager().downloadResource(iconURL, null);
            appIcon = new ImageIcon(Boot.getCacheManager().getCachedFileForURL(iconURL).toURL());
         }
         else {
            appIcon = new ImageIcon(iconURL);
         }
      }
      catch(Exception exp) {
         showError(Resources.bundle.getString("boot.error.title"), Resources.bundle.getString("icon.url.error"), exp);
         appIcon = null;
      }
            
       //Now Initialize Icon for Dialogs
      if(Boot.isHeadless()==false && appIcon!= null) {
         javax.swing.JDialog d = new javax.swing.JDialog();
         ((java.awt.Frame)d.getOwner()).setIconImage(appIcon.getImage());
      }
   }
   
   /**
    * Get VM Version
    */
   public static String getVMVersion() {
      return System.getProperty("java.vm.version");
   }
      
   /**
    * Process Startup Arguments
    * Each argument name and value will be placed in the returned properties object
    * @param args
    */
   public static Properties processArgs(String args[]) {
      
      Properties startupArgs = new Properties();
      if(args == null) return startupArgs;
      
      String _name = null;
      String _value = null;
      
      for(int i=0;i<args.length;i++) {
         //Get Name or Value
         if(args[i].startsWith("-")) {
            if(_name != null) {
               _value = "true";
               i--;
            }
            else {
               //Strip off '-'
               _name = args[i].substring(1);
               if(i==args.length-1) _value = "true";
            }
         }
         else if(_name != null) {
            _value = args[i];
         }
         else {
            //Must be a Boolean Arg
            _value = "true";
         }
         
         //Place them in the Map
         if(_name != null && _value != null) {
            startupArgs.put(_name, _value);
            _name = null;
            _value = null;
         }
      }
      
      return startupArgs;
   }
   
   /**
    * Add an OfflineListener
    */
   public static void addOfflineListener(OfflineListener listener) {
      if(!offlineListeners.contains(listener)) {
         offlineListeners.add(0, listener);
      }
   }
   
   /**
    * Remove an OfflineListener
    */
   public static void removeOfflineListener(OfflineListener listener) {
      offlineListeners.remove(listener);
   }
      
   /**
    * Show an Error in the BootStrap
    */
   public static void showError(final DialogDescriptor desc) {
      
      java.awt.EventQueue q = java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue();
      logger.log(Level.SEVERE, desc.getTitle()+":"+desc.getMessage(), desc.getException());
      
      //If NOT in the Dispatch Thread then we can spawn a new thread to show this message
      if(!isHeadless() && q.isDispatchThread() == false) {
         invokeAndWait(new Runnable() {
            public void run() {
               DialogManager.showDialog(desc);
            }
         });
      }
      //If we are in the Dispatch thread we shouln't spawn a new Thread
      else if(!isHeadless()) {
         DialogManager.showDialog(desc);
      }
   }
   
   /**
    * Show an Error in the BootStrap
    */
   public static void showError(final String title, final String msg, final Throwable exp) {
      
      final String specificTitle = (Boot.getAppDisplayName()!=null)?Boot.getAppDisplayName():"Xito BootStrap"+": "+title;
      logger.log(Level.SEVERE, title+":"+msg, exp);
      
      java.awt.EventQueue q = java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue();
            
      //If not in the Dispatch Thread we will spawn a new thread to show the error
      if(!isHeadless() && !q.isDispatchThread()) {
         invokeAndWait(new Runnable() {
            public void run() {
               try {
                  Thread.currentThread().setContextClassLoader(Boot.class.getClassLoader());
                  DialogManager.showError(null, specificTitle, msg, exp);
               }
               catch(Throwable t) {
                  logger.log(Level.SEVERE, t.getMessage(), t);
               }
            }
         });
      }
      //This could show the error in the calling apps look and feel but its better then
      //blocking the UI Dispatch Thread
      else if(!isHeadless() && q.isDispatchThread()) {
         DialogManager.showError(null, specificTitle, msg, exp);
      }
   }
   
   /**
    * Invoke a Thread in the Context of this BootStraps ThreadGroup and wait for
    * it to complete
    */
   public static void invokeAndWait(final Runnable runnable) {
             
      AccessController.doPrivileged(new PrivilegedAction(){
         public Object run() {
         
            Thread t = new Thread(getThreadGroup(), runnable);
            t.setContextClassLoader(Boot.class.getClassLoader());
            t.setDaemon(true);
            t.start();
            try {
               t.join();
            }
            catch(Exception exp) {
               logger.log(Level.SEVERE, exp.getMessage(), exp);
            }
            
            return null;
         }
      });
   }
   
   /**
    * Invoke a Thread in the Context of this BootStraps ThreadGroup return
    */
   public static void invokeLater(Runnable runnable) {
      
      Thread t = new Thread(getThreadGroup(), runnable);
      t.setContextClassLoader(Boot.class.getClassLoader());
      t.setDaemon(true);
      t.start();
   }
   
   /********************************************
    * Runnable Task to Check for Online Status
    *********************************************/
   private static class OnlineTask implements Runnable {
      
      private String checkURL;
      
      public OnlineTask(String url) {
         checkURL = url;
      }
      
      public void run() {
         try {
            URL url = new URL(checkURL);
            url.openConnection().getContent();
            //Must be online so set to Online Mode
            Boot.setOffline(false);
         }
         catch(MalformedURLException badURL) {
            String title = Resources.bundle.getString("check.online.xito.title");
            String msg = Resources.bundle.getString("check.online.bad.url.error");
            logger.log(Level.WARNING, msg);
            Boot.showError(title, msg, null);
         }
         catch(IOException ioExp) {
            ioExp.printStackTrace();
            Boot.setOffline(true);
         }
      }
   }
}
