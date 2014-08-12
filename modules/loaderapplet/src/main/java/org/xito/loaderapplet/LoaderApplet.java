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

package org.xito.loaderapplet;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.*;
import java.applet.*;
import java.util.*;

/**
 * Applet used to detect Java version and launch a JNLP application.
 */
public class LoaderApplet extends Applet {
   
   String requiredVersion;
   String updateJavaImage;
   URL updateJavaURL;
   String startAppImage;
   URL startAppURL;
   Image currentImage;
   Font smallFont = new Font("SansSerif", Font.BOLD, 12);
   Font largeFont = new Font("SansSerif", Font.BOLD, 20);
   
   Rectangle updateClickArea;
   Rectangle startClickArea;
   int mode = 0;
   String appName;
   
   private static int UPDATE_JAVA_MODE = 1;
   private static int START_APP_MODE = 2;
   
   private static String DEFAULT_UPDATE_JAVA_IMAGE = "/update_java.jpg";
   private static String DEFAULT_JAVA_URL = "http://java.com/en/download/index.jsp";
   private static String DEFAULT_START_APP_IMAGE = "/start_xito.jpg";
   private static String DEAFULT_START_APP_URL = "/quick_launch.jnlp";
   private static String DEFAULT_CLICK_AREA = "255, 45, 200, 40";
   private static String DEFAULT_REQUIRED_VERSION = "1.4";
   
   public LoaderApplet() {
      
   }
   
   //Test isGoodVersion Logic
   public static void main(String[] args) {

       if(args.length == 0) {
           System.out.println("Usage: [current_version] [required_version]");
           return;
       }

       System.out.print("Is good Version:");
       System.out.println(new LoaderApplet().isGoodVersion(args[0], args[1]));
   }

   public void init() {
      
      //Init Parameters
      try {
         //Update Java IMAGE
         String update_java_image_param = getParameter("update_java_img", DEFAULT_UPDATE_JAVA_IMAGE);
         updateJavaImage = update_java_image_param;
         
         //Update Click Area
         String update_click_area_param = getParameter("update_click_area", DEFAULT_CLICK_AREA);
         StringTokenizer updateAreatokenizer = new StringTokenizer(update_click_area_param, ",");         
         
         //Update Java URL
         String java_url_param = getParameter("java_url", DEFAULT_JAVA_URL);
         updateJavaURL = new URL(java_url_param);
         
         //Start App IMAGE
         String start_app_image_param = getParameter("start_app_img", DEFAULT_START_APP_IMAGE);
         startAppImage = start_app_image_param;

         //Start Click Area
         String start_click_area_param = getParameter("start_click_area", DEFAULT_CLICK_AREA);
         StringTokenizer startAreatokenizer = new StringTokenizer(start_click_area_param, ",");
         
         //Start APP URL
         String start_app_url_param = getParameter("start_app_url", DEAFULT_START_APP_URL);
         startAppURL = new URL(getDocumentBase(), start_app_url_param);
         
         //Java Version
         requiredVersion = getParameter("required_version", DEFAULT_REQUIRED_VERSION);
         
         //App name
         appName = getParameter("app_name", "Xito");
         
         //Update Click Area
         try {
            String x_v=null;
            String y_v=null;
            String w_v=null;
            String h_v=null;
            int i=0;
            while(updateAreatokenizer.hasMoreTokens()) {
               String token = updateAreatokenizer.nextToken();
               switch(i) {
                  case 0: x_v = token.trim(); i++; break; 
                  case 1: y_v = token.trim(); i++; break;
                  case 2: w_v = token.trim(); i++; break;
                  case 3: h_v = token.trim(); i++; break;
               }
            }
            updateClickArea = new Rectangle(Integer.parseInt(x_v), Integer.parseInt(y_v), Integer.parseInt(w_v), Integer.parseInt(h_v));
         }
         catch(Exception badValue) {
            badValue.printStackTrace();
            System.out.println("invalid click area:"+update_click_area_param);
            updateClickArea = new Rectangle(0,0, super.getWidth(), super.getHeight());
         }
         
         //Start Click Area
         try {
            String x_v=null;
            String y_v=null;
            String w_v=null;
            String h_v=null;
            int i=0;
            while(startAreatokenizer.hasMoreTokens()) {
               String token = startAreatokenizer.nextToken();
               switch(i) {
                  case 0: x_v = token.trim(); i++; break; 
                  case 1: y_v = token.trim(); i++; break;
                  case 2: w_v = token.trim(); i++; break;
                  case 3: h_v = token.trim(); i++; break;
               }
            }
            startClickArea = new Rectangle(Integer.parseInt(x_v), Integer.parseInt(y_v), Integer.parseInt(w_v), Integer.parseInt(h_v));
         }
         catch(Exception badValue) {
            badValue.printStackTrace();
            System.out.println("invalid click area:"+start_click_area_param);
            startClickArea = new Rectangle(0,0, super.getWidth(), super.getHeight());
         }
         
         
      }
      catch(Exception exp) {
         exp.printStackTrace();
      }
      
      setLayout(null);
      
      //check Version
      String javaVersion = System.getProperty("java.version");
      if(isGoodVersion(javaVersion, requiredVersion)) {
         currentImage = getImage(getDocumentBase(), startAppImage);
         mode = START_APP_MODE;
      }
      else {
         currentImage = getImage(getDocumentBase(), updateJavaImage);
         mode = UPDATE_JAVA_MODE;
      }
      
      MyMouseAdapter ml = new MyMouseAdapter();
      this.addMouseListener(ml);
      this.addMouseMotionListener(ml);
      repaint();
   }
   
   private boolean isGoodVersion(String version, String requiredVersion) {
      
      int[] versionValues = parseVersion(version);
      int[] requiredVersionValues = parseVersion(requiredVersion);
      
      System.out.println("Version: "+getVersionString(versionValues));
      System.out.println("Required Version: "+getVersionString(requiredVersionValues));
      
      for(int i=0;i<requiredVersionValues.length;i++) {
         int requiredV = requiredVersionValues[i];
         //the version doesn't have enought digits to compare
         if(i >= versionValues.length) {
            return false;
         }
         
         //required version digit is greater then current version
         if(requiredV>versionValues[i]) {
            return false;
         }
         //required version is less then or equal to current version
         else if(versionValues[i] > requiredV) {
            return true;
         }
         
         //we only loop if the digits are equal and we have to test the next digit
      }
      
      //this means that were exactly equal
      return true;
   }
   
   private String getVersionString(int[] values) {
      
      StringBuffer buf = new StringBuffer();
      for(int i=0;i<values.length;i++) {
         buf.append(""+values[i]);
         if(i<values.length-1) {
            buf.append(":");
         }
      }
      
      return buf.toString();
   }
   
   private int[] parseVersion(String version) {
      
      StringTokenizer tokenizer = new StringTokenizer(version, "._-");
      if(tokenizer.countTokens() == 0) {
         return new int[]{0};
      }
           
      int values[] = new int[tokenizer.countTokens()];
      for(int i=0;i<values.length;i++) {
         try {
            String token = tokenizer.nextToken();
            values[i] = Integer.parseInt(token);
         }
         catch(NumberFormatException exp) {
            return values;
         }
      }
      
      return values;
   }
   
   public String getParameter(String name, String defaultValue) {
      String param_value = super.getParameter(name);
      if(param_value != null) return param_value;
      
      return defaultValue;
   }
   
   public void paint(Graphics g) {

      g.setFont(largeFont);
      g.drawString(appName,20,30);
      g.setFont(smallFont);
      if(currentImage == null) {
         g.drawString("Launcher Initializing..."+mode,20,40);
      }
      else {
         if(mode == UPDATE_JAVA_MODE) {
            g.drawString("Java update required. ("+updateJavaImage+")", 20,40);
         }
         else if(mode == START_APP_MODE) {
            g.drawString("Java version ok. ("+startAppImage+")", 20,40);
         }
         
         g.drawImage(currentImage, 0,0, this);
         //g.setColor(Color.black);
         //g.drawString(System.getProperty("java.version"), 20, 30);
         
      }
   }
   
   public void start() {
      
   }
   
   public void stop() {
      
   }
   
   public class MyMouseAdapter extends MouseAdapter implements MouseMotionListener {
      
      public void mouseClicked(MouseEvent e) {
         
            if(mode == UPDATE_JAVA_MODE && updateClickArea.contains(e.getX(), e.getY())) {
               LoaderApplet.this.getAppletContext().showDocument(updateJavaURL, "new");
            }
            else if(mode == START_APP_MODE && startClickArea.contains(e.getX(), e.getY())) {
               LoaderApplet.this.getAppletContext().showDocument(startAppURL);
            }
      }
      
      public void mouseMoved(MouseEvent e) {
         
         if(mode == UPDATE_JAVA_MODE && updateClickArea.contains(e.getX(), e.getY())) {
            LoaderApplet.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            LoaderApplet.this.getAppletContext().showStatus("Update Java: "+updateJavaURL.toString());
         }
         else if(mode == START_APP_MODE && startClickArea.contains(e.getX(), e.getY())) {
            LoaderApplet.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            LoaderApplet.this.getAppletContext().showStatus("Launch: "+startAppURL.toString());
         }
         else {
            LoaderApplet.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            LoaderApplet.this.getAppletContext().showStatus("");
         }
      }
      
      public void mouseDragged(MouseEvent e) {}
   }
}