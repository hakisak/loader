// Copyright 2007 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.about;

import java.awt.*;
import java.lang.reflect.*;
import javax.swing.*;
import java.util.*;

import org.xito.boot.*;
import org.xito.reflect.*;

/**
 * 
 * @author Deane Richan
 */
public class AboutService {

   private static JPanel defaultInfoPanel;

   public static void main(String args[]) {

      // Install OSX about menu
      String os = System.getProperty("os.name");
      if (os.startsWith("Mac OS")) {
         installAppleAboutMenu();
      }
   }

   /**
    * Install the About Window in the proper Apple Menu
    */
   private static void installAppleAboutMenu() {

      try {
         // Get Application
         Reflection rkit = Reflection.getToolKit();
         Class AppClass = rkit.findClass("com.apple.eawt.Application");
         Object appleApp = rkit.callStatic(AppClass, "getApplication");

         // Install Application Listener
         Class AppListenerClass = rkit
               .findClass("com.apple.eawt.ApplicationListener");
         Object appleAppHandler = Proxy.newProxyInstance(AboutService.class
               .getClassLoader(), new Class[] { AppListenerClass },
               new AppInvocationHandler());
         rkit.call(appleApp, "addApplicationListener", appleAppHandler,
               AppListenerClass);
         rkit.call(appleApp, "setEnabledAboutMenu", true);
      }
      catch (Exception exp) {
         exp.printStackTrace();
      }
   }

   /**
    * Get the Information to display in the About Box. Versions for the VM and
    * all loaded Services.
    * 
    * @return
    */
   public static String getAllServiceInfo() {

      java.util.List services = Boot.getServiceManager().getLoadedServices();
      StringBuffer serviceInfo = new StringBuffer();

      serviceInfo
            .append("<html><body style=\"font-family: sans-serif; font-size: small\">");
      serviceInfo.append("<b>Java Version: </b>"
            + System.getProperty("java.version") + "<br>");
      serviceInfo.append("<b>Java Home: </b>" + System.getProperty("java.home")
            + "<br>");
      serviceInfo.append("<b>Boot Dir: </b>" + Boot.getBootDir().toString()
            + "<br>");

      serviceInfo.append("<br><b>Xito BootStrap</b><br>");
      serviceInfo.append("version: "
            + Boot.class.getPackage().getImplementationVersion() + "<br><br>");

      if (services != null) {
         Iterator it = services.iterator();
         while (it.hasNext()) {
            ServiceDesc service = (ServiceDesc) it.next();
            String name = service.getName();
            String displayName = service.getDisplayName();
            String version = "version: " + service.getVersion();
            serviceInfo.append("<b>" + name + "</b><br>");
            serviceInfo.append(displayName + "<br>");
            serviceInfo.append(version + "<br><br>");
         }
      }

      serviceInfo.append("</body></html>");

      return serviceInfo.toString();
   }

   /**
    * Set the Default Info Panel used for the AboutWindow Displayed
    * 
    * @param infoPanel
    */
   public static void setDefaultInfoPanel(JPanel infoPanel) {
      defaultInfoPanel = infoPanel;
   }

   /**
    * Show the About Window as a child of owner
    * 
    * @param owner
    *           or null
    */
   public static void showAboutWindow(Frame owner) {
      new AboutWindow(owner, defaultInfoPanel).setVisible(true);
   }

   /**
    * Class Used to Handle Apple Application Events
    */
   public static class AppInvocationHandler implements InvocationHandler {

      public Object invoke(Object proxy, Method method, Object[] args) {
         if (method.getName().equals("handleAbout")) {
            handleAbout(args[0]);
         }

         return null;
      }

      public void handleAbout(Object event) {

         // Show the About Window
         showAboutWindow(null);

         try {
            // Set Handled Flag
            Reflection rkit = Reflection.getToolKit();
            rkit.call(event, "setHandled", true);
         }
         catch (Exception exp) {
            exp.printStackTrace();
         }
      }
   }

}
