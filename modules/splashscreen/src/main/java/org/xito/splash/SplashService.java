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

package org.xito.splash;

//Java Imports
import java.util.*;
import java.util.logging.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.security.*;
import javax.swing.*;
import org.xito.boot.*;

/**
 * The Splash Service creates a Splash Screen and logs any messages that are logged to the 
 * org.xito.boot log category.
 *
 * @author Deabe Richan
 * @version
 *</p><p>
 */
public class SplashService  {
   
   private static final Logger logger = Logger.getLogger(SplashService.class.getName());
   
   /** Default width of Splash Window **/
   public static int WIDTH = 500;
   /** Default height of Splash Window **/
   public static int HEIGHT = 300;
   
   public static String WIDTH_PROP = "splash.width";
   public static String HEIGHT_PROP = "splash.height";
   public static String URL_PROP = "splash.url";
   
   private static SplashWindow splashPanel;
         
   private static int width, height;
   private static URL imageURL;
   
   /**
    * Init Service Method
    */
   public static void initService(ServiceDesc service) {
      
      //Image URL
      String urlStr = service.getProperties().getProperty(URL_PROP);
      if(urlStr != null) {
         try {
            imageURL = new URL(service.getContextURL(), urlStr);
         }
         catch(MalformedURLException badURL) {
            imageURL = null;
         }
      }
      else {
         urlStr = System.getProperty(URL_PROP);
         if(urlStr != null) {
            try {
               imageURL = new URL(Boot.getBootDir().toURL(), urlStr);
            }
            catch(MalformedURLException badURL) {
               imageURL = null;
            }
         }
      }
      
      //Get Image using Cache
      if(imageURL != null) {
         try {
            Boot.getCacheManager().downloadResource(imageURL,null);
            imageURL = Boot.getCacheManager().convertToCachedURL(imageURL);
         }
         catch(IOException ioExp) {
            logger.log(Level.WARNING, ioExp.toString(), ioExp);
         }
      }
      
      
      //Width
      String w = service.getProperties().getProperty(WIDTH_PROP);
      if(w == null) w = System.getProperty(WIDTH_PROP);
      if(w == null) {
         width = WIDTH;
      }
      else {
         try{
            width = Integer.parseInt(w.trim());
         }
         catch(Exception e) {
            e.printStackTrace();
            width = WIDTH;
         }
      }
      
      //Height
      String h = service.getProperties().getProperty(HEIGHT_PROP);
      if(h == null) h = System.getProperty(HEIGHT_PROP);
      if(h == null) {
         height = HEIGHT;
      }
      else {
         try{
            height = Integer.parseInt(h.trim());
         }
         catch(Exception e) {
            e.printStackTrace();
            height = HEIGHT;
         }
      }
   }
   
   /**
    * Starts the Splash and shows the Splash Window
    */
   public static void main(final String args[]) {
      
      showSplashScreen();
   }
       
   /**
    * Show the Splash Window for this Application
    *<p>
    * The splash image specified in the app.splash.url System property.
    *
    * If no argument was passed then the splash.png file located in this Applications CodeBase
    * will be used. If the splash image is not found an Error will be logged but
    * the application will continue to boot.
    *</p>
    */
   private static void showSplashScreen() {
      
      URL url = null;
            
      try {
         //Create the Window
         splashPanel = new SplashWindow(imageURL, width, height);
         Boot.getServiceManager().addStartupListener(splashPanel);
         splashPanel.showWindow();
      }
      catch(Exception exp) {
         exp.printStackTrace();
         //splashPanel = new SplashWindow();
         //splashPanel.showWindow();
      }
   }
   
   /**
    * Get the Shell's Splash Panel
    * @return JPanel where the Splash Image is Painted
    */
   public static JPanel getSplashPanel() {
      return splashPanel;
   }
}
