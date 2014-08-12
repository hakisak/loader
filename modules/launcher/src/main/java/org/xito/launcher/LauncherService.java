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

package org.xito.launcher;

import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.lang.reflect.*;
import org.w3c.dom.*;

import org.xito.boot.*;
import org.xito.dazzle.worker.WindowBlockingBusyWorker;
import org.xito.reflect.*;
import org.xito.launcher.applet.*;
import org.xito.launcher.web.*;
import org.xito.launcher.youtube.*;
import org.xito.launcher.sys.*;
import org.xito.launcher.jnlp.*;
import org.xito.launcher.jnlp.service.*;

/**
 *
 * @author  Deane
 */
public class LauncherService {
   
   private static Logger logger = Logger.getLogger(LauncherService.class.getName());
   
   private static ArrayList<LauncherActionFactory> actionFactories = new ArrayList<LauncherActionFactory>();
   private static boolean initialized_flag;

   /**
    * Initialize Launcher Env
    */
   private synchronized static void initialize() {
      
      //setup the JNLP ServiceManager
      javax.jnlp.ServiceManager.setServiceManagerStub(new ServiceManagerStubImpl());
      
      //Add Actions with this Launcher Service
      registerActionFactory(new JavaRemoteAppActionFactory());
      registerActionFactory(new JavaAppActionFactory());
      registerActionFactory(new JNLPActionFactory());
      registerActionFactory(new AppletActionFactory());
      registerActionFactory(new YouTubeActionFactory());
      registerActionFactory(new WebActionFactory());
      registerActionFactory(new LocalAppActionFactory());
      
      //Add Launchers to BootStrap for External Launches
      registerLauncher(new AppletLauncher(), AppletDesc.class, "applet"); 
      registerLauncher(new JNLPLauncher(), JNLPAppDesc.class, "jnlp"); 
      registerLauncher(new JavaLauncher(), JavaAppDesc.class, "java");

      initialized_flag = true;
   }

    /**
     * Return true if this service has been initialized
     * @return
     */
   public synchronized boolean isInitialized() {
       return initialized_flag;
   }
   
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      
      initialize();
      
      //See if we are suppose to launch an app
      String jnlpArg = Boot.getArgProperties().getProperty("jnlp");
      if(jnlpArg == null) {
         jnlpArg = System.getProperty("launcher.jnlp.startup");
      }
      
      String appletArg = Boot.getArgProperties().getProperty("applet");
      String xappArg = Boot.getArgProperties().getProperty("xapp");
      String webArg = Boot.getArgProperties().getProperty("web");
      String localArg = Boot.getArgProperties().getProperty("local");
      
      //Check for JNLP App Launch
      if(jnlpArg != null) launchJNLP(jnlpArg);
      //Check for Xapp Launch
      else if(appletArg != null) launchApp(xappArg);
      //Check for Applet Launch
      else if(appletArg != null) launchApplet(appletArg, true);
      //Check for web launch
      else if(webArg != null) launchWeb(webArg);
          
      //Noting to do so just return
      return;
   }
   
   /**
    * Find a registered Launcher for the given type
    * and launch it.
    * @param commandStr
    */
   public static LauncherAction launch(String commandStr) {
      
      //for now always launch the browser
      launchWeb(commandStr);
      
      return null;
   }
   
   /**
    * Launch a URL in a Web Browser
    */
   public static void launchWeb(String webAddress) {
      if(webAddress == null) return;
      
      WebLauncher launcher = new WebLauncher();
      try {
         launcher.launch(new URL(webAddress));
      }
      catch(MalformedURLException badURL) {
         //perhaps they are missing the protocol so default to http://
         try {
            launcher.launch(new URL("http://"+webAddress));
         }
         catch(MalformedURLException badURL2) {
            //Ok now show an Error
            Boot.showError("Web Address Error", "Cannot open Browser invalid address:"+webAddress, null);
         }
      }
   }
   
   /**
    *Start an Applet or Applets
    */
   private static void launchApplet(String appletArg, boolean exitOnStop) {

      URL appletURL = null;
      
      //Get the Applet URL
      try {
         if(appletArg.startsWith("http") || appletArg.startsWith("file")) {
            appletURL = new URL(appletArg);
         }
         else {
            appletURL = new URL(org.xito.boot.Boot.getBootDir().toURL(), appletArg);
         }
         
         AppletLauncher launcher = new AppletLauncher();
         Thread t = launcher.launch(appletURL);
         t.join();
      }
      catch(MalformedURLException badURL) {
         logger.log(Level.SEVERE,"Invalid Applet URL:"+appletArg, badURL);
         Boot.showError("Error Launching Applet", "Invalid Applet URL:"+appletArg, null);
      }
      catch(Exception exp) {
         exp.printStackTrace();
      }
   }
   
   /**
    * Register a new Launcher with the BootStrap. This is used for External Execution
    */
   public static void registerLauncher(AppLauncher launcher, Class descClass, String type) {
      AppLauncher.registerLauncher(launcher, descClass, type);
   }
   
   /**
    * Register a new Action
    */
   public static void registerActionFactory(LauncherActionFactory factory) {
      actionFactories.add(factory);
   }
   
   /**
    * Create an Action for a specified DOM Element. NOTE: This method could block and should 
    * be called in a busy worker if used from the event dispatch thread
    * @param e XML element
    * @return a create Action
    */
   public static LauncherAction createActionForElement(Element e)  {
      
      if(e == null)
         return null;
      
      LauncherActionFactory useFactory = null;
      for(LauncherActionFactory factory : actionFactories) {
         if(factory.getElementName().equals(e.getTagName())) {
            useFactory = factory;
            break;
         }
      }

      //didn't find a factory
      if(useFactory == null) return null;
      
      return useFactory.createActionFromDataElement(e);
   }
   
   public static LauncherActionFactory[] getActionFactories() {
      return (LauncherActionFactory[])actionFactories.toArray(new LauncherActionFactory[actionFactories.size()]);
   }
   
   /**
    * Start a Application
    */
   private static void launchApp(String appArg) {
            
      URL appURL= null;
      
      try {
         if(appArg.startsWith("http") || appArg.startsWith("file")) {
            appURL = new URL(appArg);
         }
         else {
            appURL = new URL(org.xito.boot.Boot.getBootDir().toURL(), appArg);
         }
         
         AppLauncher appLauncher = new AppLauncher();
         appLauncher.launchInternal(appURL);
      }
      catch(MalformedURLException badURL) {
         logger.log(Level.SEVERE, badURL.getMessage(), badURL);
         Boot.showError("App Launch Error", "Invalid App Address:"+appArg, badURL);
      }
      catch(LaunchException exp) {
         logger.log(Level.SEVERE, exp.getMessage(), exp);
         Boot.showError("App Launch Error", "An error occured launching App:"+appArg, exp);
      }
   }
   
   /**
    * Start a JNLP Application
    */
   private static void launchJNLP(String jnlpArg) {
            
      URL jnlpURL = null;
      
      try {
         if(jnlpArg.startsWith("http") || jnlpArg.startsWith("file")) {
            jnlpURL = new URL(jnlpArg);
         }
         else {
            jnlpURL = new URL(org.xito.boot.Boot.getBootDir().toURL(), jnlpArg);
         }
         
         JNLPLauncher jnlpLauncher = new JNLPLauncher();
         jnlpLauncher.launchInternal(jnlpURL);
      }
      catch(MalformedURLException badURL) {
         logger.log(Level.SEVERE, badURL.getMessage(), badURL);
         Boot.showError("JNLP Launch Error", "Invalid JNLP Address:"+jnlpArg, badURL);
      }
      catch(LaunchException exp) {
         logger.log(Level.SEVERE, exp.getMessage(), exp);
         Boot.showError("JNLP Launch Error", "An error occured launching JNLP App:"+jnlpArg, exp);
      }
   }
   
   /**
    * Create an action from an object. If an action factory return a non-null
    * value then it will be returned to caller
    * @param parentFrame dialogs should become child of
    * @return new LauncherAction or null if user canceled
    */
   public static LauncherAction createActionFromObject(java.awt.Frame parentFrame, Object obj) {
      
      //If no URL just return null
      if(obj == null) return null;
      
      LauncherAction action = null;
      Reflection rkit = Reflection.getToolKit();
      for(LauncherActionFactory factory : actionFactories) {
            
         action = factory.createAction(parentFrame, obj);
         if(action != null)   
            return action;
      }
      
      return null;   
   }
   
   /**
    * Prompt the user to create an Action
    * @param parentFrame dialogs should become child of
    * @param listener called when action is created
    * @return new LauncherAction or null if user canceled
    */
   public static void createAction(final java.awt.Frame parentFrame, final LauncherActionCreatedListener listener) {
      
      CreateActionDialog dialog = new CreateActionDialog(parentFrame);
      final LauncherActionFactory factory = dialog.getActionFactory();
      
      //User must have canceled
      if(factory == null) {
         return;
      }
      
      //create the action using a busy worker
      //then edit the action that is created
      new WindowBlockingBusyWorker<LauncherAction>(parentFrame) {
         
         public LauncherAction work() {
            return factory.createAction();
         }
         
         public void finished(LauncherAction action) {
            action.edit(parentFrame);
            listener.launcherActionCreated(action);
         }
         
      }.invokeLater();
   }
   
}
