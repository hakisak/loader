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

package org.xito.appmanager;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.lang.reflect.*;
import javax.swing.*;

import org.xito.boot.*;
import org.xito.reflect.*;

/**
 * Main class of the Xito Application Manager
 *
 * @author Deane Richan
 */
public class Main {
  
  private static MainFrame mainFrame; 
  private static PopupMenu trayMenu;
  
  public static void initService(ServiceDesc service) {
  
  }
  
  public static void main(String args[]) {
          
     //Set Look and Feel
     if(Boot.getCurrentOS() == Boot.WINDOWS_OS) {
        try {
           javax.swing.UIManager.setLookAndFeel("net.java.plaf.windows.WindowsLookAndFeel");
        }
        catch (Exception e) {
           
        }
     }
     
     //hideDesktop();
     
     //build tray icon
     if(Boot.getCurrentOS() == Boot.WINDOWS_OS) {
        buildTrayIcon();
        mainFrame = new MainFrame(false); 
     }
     else {
        mainFrame = new MainFrame(true); 
     }
     
     mainFrame.show();
     mainFrame.loadApplications(true);
     
     //Install Quit Listener for Apple
     if(Boot.getCurrentOS() == Boot.MAC_OS) {
         installAppleQuitHandler();
     }
  }
  
  /**
   * Hide the Desktop
   */
  private static void hideDesktop() {
     try {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        Robot robot = new Robot();
        final BufferedImage screenImage = robot.createScreenCapture(new Rectangle(0,0,size.width, size.height));
        final Color overlay = new Color(0.5f,0.5f,0.5f,0.9f);
        Window w = new Window(new Frame()) {
           public void paint(Graphics g) {
              g.drawImage(screenImage, 0, 0, null);    
              g.setColor(overlay);
              g.fillRect(0,0, getWidth(), getHeight());
           }
        };
        w.setSize(size);
        w.setVisible(true);
     }
     catch(Exception exp) {
        exp.printStackTrace();
     }
  }
  
  /**
   * Build a Tray Icon for Windows
   */
  private static void buildTrayIcon() {

     try {
         TrayIcon trayIcon = new TrayIcon(new ImageIcon(Main.class.getResource("/org/xito/launcher/images/xito_16_grey.png")).getImage(), Boot.getAppDisplayName());
         trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               mainFrame.setVisible(true);
               if(mainFrame.getState() == Frame.ICONIFIED) {
                  mainFrame.setState(Frame.NORMAL);
               }
               mainFrame.toFront();
            }
         });

         trayMenu = new TrayPopupMenu();
         trayIcon.setPopupMenu(trayMenu);

         SystemTray systray = SystemTray.getSystemTray();
         systray.add(trayIcon);
     }
     catch(AWTException exp) {
         exp.printStackTrace();
     }
  }
  
  /**
   * Install Quit Handler for Apple
   */
  private static void installAppleQuitHandler() {
      try {
           //Get Applicatin 
           Reflection rkit = Reflection.getToolKit();
           Class AppClass = rkit.findClass("com.apple.eawt.Application");
           Object appleApp = rkit.callStatic(AppClass,"getApplication");      
           
           //Install Application Listener
           Class AppListenerClass = rkit.findClass("com.apple.eawt.ApplicationListener");
           Object appleAppHandler = Proxy.newProxyInstance(Main.class.getClassLoader(), new Class[]{AppListenerClass}, new AppInvocationHandler());
           rkit.call(appleApp, "addApplicationListener", appleAppHandler, AppListenerClass);
       }
       catch(Exception exp) {
          exp.printStackTrace(); 
       }
  }
  
  /**
   * Get the Popup Menu used with the System Tray
   */
  public static PopupMenu getTrayMenu() {
     return trayMenu;
  }
  
  private static MainFrame getMainFrame() {
     return mainFrame;
  }
  
  /**
    * Class Used to Handle Apple Application Events
    */
   public static class AppInvocationHandler implements InvocationHandler {
       
       public Object invoke(Object proxy, Method method, Object[] args) {
           if(method.getName().equals("handleQuit")) {
               handleQuit(args[0]);
           }
           
           return null;
       }
       
       public void handleQuit(Object event) {
           
           mainFrame.promptForExit();
           
           try {
               //Set Handled Flag to false note: true causes Apple to shutdown VM
               Reflection rkit = Reflection.getToolKit();
               rkit.call(event, "setHandled", false);
           }
           catch(Exception exp) {
               exp.printStackTrace();
           }
       }
   }
  
  /**
   * Menu for Tray Popup Menu
   */
  private static class TrayPopupMenu extends PopupMenu implements ActionListener {
     
     private MenuItem exitMI;
     private MenuItem aboutMI;
     private MenuItem showAppManagerMI;
     private MenuItem showDesktopMI;
     
     public TrayPopupMenu() {
        super();
        init();
     }
     
     private void init() {
        
        showAppManagerMI = new MenuItem("Show AppManager");
        showAppManagerMI.addActionListener(this);
        add(showAppManagerMI);
        
        //showDesktopMI = new JMenuItem("Show Desktop");
        //showDesktopMI.addActionListener(this);
        //add(showDesktopMI);
        
        aboutMI = new MenuItem("About");
        aboutMI.addActionListener(this);
        add(aboutMI);
        //add(new Separator());
        
        exitMI = new MenuItem("Exit");
        exitMI.addActionListener(this);
        add(exitMI);
     }
     
     public void actionPerformed(ActionEvent evt) {
        
        //Exit
        if(evt.getSource() == exitMI) {
           mainFrame.promptForExit();
        }
        
        //Show About
        if(evt.getSource() == aboutMI) {
           org.xito.about.AboutService.showAboutWindow(mainFrame);
        }
        
        //Show App Manager
        if(evt.getSource() == showAppManagerMI) {
           mainFrame.setVisible(true);
           mainFrame.toFront();
        }
     }
  }
}
