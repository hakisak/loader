// Copyright (C) 2005 Xito.org. http://www.xito.org
//
// This file is part of Xito(tm).
//
// This Software is licensed under the terms of the
// COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0
//
// To view the complete Terms of this license visit:
// http://www.opensource.org/licenses/cddl1.txt
//
// COVERED SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN AS IS BASIS, WITHOUT
// WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, WITHOUT
// LIMITATION, WARRANTIES THAT THE COVERED SOFTWARE IS FREE OF DEFECTS,
// MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR NON-INFRINGING. THE ENTIRE
// RISK AS TO THE QUALITY AND PERFORMANCE OF THE COVERED SOFTWARE IS WITH YOU.
// SHOULD ANY COVERED SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, YOU (NOT THE
// INITIAL DEVELOPER OR ANY OTHER CONTRIBUTOR) ASSUME THE COST OF ANY
// NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
// CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY COVERED
// SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.

package org.xito.launcher.applet;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.logging.*;
import javax.swing.*;

import org.xito.dialog.*;
import org.xito.boot.Boot;
import org.xito.boot.AppDesc;
import org.xito.boot.AppLauncher;
import org.xito.boot.AppClassLoader;
import org.xito.boot.LaunchException;
import org.xito.reflect.*;
import org.xito.launcher.*;
import org.xito.launcher.web.*;

/**
 *
 * @author Deane Richan
 */
public class AppletLauncher extends AppLauncher {
   
   private static Logger logger = Logger.getLogger(AppletLauncher.class.getName());
      
   public AppletLauncher() {}
      
   /**
    * Launch an Applet internally.
    * @param showError
    */
   public void launchInternal(AppletDesc appletDesc, boolean showError) throws LaunchException {
      launchInternal(appletDesc);
   }
   
   /**
    * Launch an Applet from a URL. This will parse for applets and launch the first
    * applet found in the same VM
    * @param appletURL
    */
   public Thread launch(URL appletURL) throws LaunchException {
      return launch(appletURL, null);
   }
   
   /**
    * Launch an Applet from a URL and update the appletDesc with info from the URL. If
    * the appletDesc is not null and specifies to use a SeperateVM launchExternal will be called.
    * If the applet is launched External then the returned Thread will be null
    * @param appletURL
    * @param appletDesc to update with info obtained from appletURL
    */
   public Thread launch(URL appletURL, AppletDesc appletDesc) throws LaunchException {
      
      //If no URL
      if(appletURL == null)
         throw new LaunchException(Resources.appletBundle.getString("launch.error.no.address"));
      
      AppletParseThread t = new AppletParseThread(appletURL, appletDesc);
      t.start();
      
      return t;
   }
   
   /**
    * Launches an Applet. This method will not parse for Applets from the DocumentURL. The AppDesc
    * must be either pre-parsed or be a custom AppletDesc. This method does not spawn a new Thread and will throw
    * a LaunchException if there are any errors launching the applet.
    * This method is primarily used by BootStrap to perform a launchExternal
    *
    * @param appDesc this should be an instance of AppletDesc or a ClassCastException will occur
    */
   protected void launch(AppDesc appDesc) throws LaunchException {
      
      AppletDesc appletDesc = (AppletDesc)appDesc;
      
      //Check for Launch in Browser and then just launch the Browser
      if(appletDesc.useWebBrowser()) {
         WebLauncher webLauncher = new WebLauncher();
         webLauncher.launch(appletDesc.getDocumentURL());
         return;
      }
      
      AppletInstance appletInstance;
      Reflection r = Reflection.getToolKit();
      
      //Setup new App Context
      if(appletDesc.useNewAppContext()) {
         try {
            Class cls = Class.forName("sun.awt.SunToolkit");
            r.callStatic(cls, "createNewAppContext");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } catch(Throwable exp) {
            //We can ignore this
            logger.log(Level.WARNING, "Error setting new App Context", exp);
         }
      }
      
      //Create the applet and initialize the Applet Environment
      try {
         AppClassLoader loader = appletDesc.getNewAppletClassLoader(this.getClass().getClassLoader());
         //Print out URLS for debugging
         if(logger.isLoggable(Level.INFO)) {
            StringBuffer urlText = new StringBuffer();
            urlText.append("ClassPath for Applet:"+appletDesc.getName()+"\n");
            urlText.append(loader.getClassPathString());
            logger.info(urlText.toString());
         }
         
         
         if(appletDesc.getAppletClass() == null)
            throw new LaunchException(Resources.appletBundle.getString("launch.error.no.applet.class"));
         
         Class appletCls = loader.loadClass(appletDesc.getAppletClass());
         Object applet = r.newInstance(appletCls);
         appletInstance = new AppletInstance(loader, (java.applet.Applet)applet);
         AppletEnvironment appletEnv = new AppletEnvironment(appletDesc, appletInstance);
                  
         appletEnv.startApplet();
      } catch(Throwable exp) {
         logger.log(Level.SEVERE, exp.getMessage(), exp);
         throw new LaunchException(exp);
      }
   }
   
   /**
    * Launch an Applet in the Background. If this is not a Custom Applet then the Document Address
    * will be parsed for HTML applets and then the First Applet will be launched.
    * If the appletDesc specifies to use a seperateVM then launchExternal will be called for this appletDesc.
    * In which case the Thread returned will be null
    * @return Thread of launched Applet or null if launched in a new VM
    */
   public Thread launchInternal(AppletDesc appletDesc) throws LaunchException {
      
      //First check to see if this is a custom
      if(appletDesc.useCustomConfig() == false) {
         return launch(appletDesc.getDocumentURL(), appletDesc);
      }
      
      //Now check to see if we should launch External
      if(appletDesc.useSeperateVM()) {
         try {
            launchExternal(appletDesc);
         } catch(IOException ioExp) {
            throw new LaunchException(ioExp);
         }
         return null;
      }
      
      AppletThread t = new AppletThread(appletDesc);
      t.start();
      return t;
   }
   
   /******************************************
    * AppletThread
    ******************************************/
   public final class AppletThread extends Thread {
      
      private AppletDesc appletDesc;
      private AppletInstance appletInstance;
      
      public AppletThread(AppletDesc appletDesc) {
         super(new ThreadGroup(appletDesc.getName()), appletDesc.getName());
         this.appletDesc = appletDesc;
         this.setDaemon(true);
      }
      
      /**
       * Launch the Application. Just like any Thread start should be called not run
       */
      public void run() {
         try {
            launch(appletDesc);
         } catch(LaunchException exp) {
            String msg = MessageFormat.format(Resources.appletBundle.getString("launch.error.message"), exp.getMessage());
            Boot.showError(Resources.appletBundle.getString("launch.error.title"), msg, exp);
         }
         
      }
   }
   
   /******************************************
    * AppletParseThread
    ******************************************/
   public final class AppletParseThread extends Thread {
      
      private AppletDesc appletDesc;
      private URL appletURL;
      
      public AppletParseThread(URL appletURL, AppletDesc appletDesc) {
         super(new ThreadGroup(appletDesc.getName()), appletDesc.getName());
         this.appletURL = appletURL;
         this.appletDesc = appletDesc;
         this.setDaemon(true);
      }
      
      /**
       * Parse URL for applets then launch the Applet 
       */
      public void run() {
         
         ProgressDialogDescriptor desc = new ProgressDialogDescriptor();
         String title = Resources.appletBundle.getString("launch.title");
         title = MessageFormat.format(title, appletDesc.toString());
         desc.setTitle(title);
         
         String subtitle = Resources.appletBundle.getString("launch.subtitle");
         desc.setSubtitle(subtitle);
         
         String msg = Resources.appletBundle.getString("launch.message");
         msg = MessageFormat.format(msg, appletDesc.getName());
         desc.setMessage(msg);
         
         desc.setButtonTypes(new ButtonType[]{new ButtonType("Hide", 99)});
         desc.setWidth(350);
         ProgressDialog dialog = new ProgressDialog(null, desc, false);
         dialog.setVisible(true);
         
         //Parse for Applets
         AppletHTMLParser parser = new AppletHTMLParser();
         
         try {
            
            //If we are suppose to use the Web Browser then just 
            //Skip the Parsing
            if(appletDesc != null && appletDesc.useWebBrowser()) {
               new WebLauncher().launch(appletURL);
               return;
            }
            
            AppletDesc applets[] = parser.parseApplets(appletURL);
            if(applets.length >0) {
               //Update info in AppletDesc
               if(appletDesc != null) {
                  appletDesc.updateInfo(applets[0]);
                  applets[0].setName(appletDesc.getName());
                  applets[0].setTitle(appletDesc.getTitle());
                  applets[0].setUniqueID(appletDesc.getUniqueID());
               }
               
               if(appletDesc != null && appletDesc.useSeperateVM()) {
                  launchExternal(appletDesc);
               }
               else {
                  launch(applets[0]);
               }
            }
            else {
               throw new LaunchException(Resources.appletBundle.getString("launch.error.no.applets"));
            }
         } 
         catch(Throwable exp) {
            dialog.setVisible(false);
            logger.log(Level.SEVERE, exp.getMessage(), exp);
            msg = MessageFormat.format(Resources.appletBundle.getString("launch.error.message"), exp.getMessage());
            Boot.showError(Resources.appletBundle.getString("launch.error.title"), msg, exp);
         }
         finally {
            dialog.setVisible(false);
         }
         
      }
   }
}
