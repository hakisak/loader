// Copyright (C) 2002 Xito.org. http://www.xito.org
//
// This file is part of Xito(tm).
//
// This is free software; you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License (LGPL)
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// It is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this application.
//
// Information about the GNU LGPL License can be obtained at
// http://www.gnu.org/licenses/

package org.xito.desktop;

import java.awt.*;
import java.util.*;
import java.util.prefs.*;
import java.util.logging.*;
import java.net.*;
import javax.swing.*;

import org.w3c.dom.*;

import org.xito.dialog.*;
import org.xito.dazzle.worker.BusyWorker;
import org.xito.dcf.*;
import org.xito.blx.*;
import org.xito.xmldocs.*;


/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.7 $
 * @since $Date: 2006/04/08 04:47:33 $
 */
public class DesktopService {
   
   public static final String DESKTOP_PATH = "desktop";
   public static final int PORT = 14001;
   
   
   private static DesktopService singleton;
   private static DesktopTheme desktopTheme;
   
   private boolean saveAllChildren = false;
      
   private Frame dialogOwner = new Frame();
   private Preferences preference;
   private XMLDocumentService docService;
   private BLXCompFactory componentFactory;
   private MasterTile masterTile;
   private ArrayList managedComponents = new ArrayList();
   private Desktop currentDesktop;
   private ArrayList newItemFactories = new ArrayList();
   private DesktopServer desktopServer;
   private URL codeBase;
   
   private Logger srvLogger = Logger.getLogger("org.xito.shell");
   
   public static ResourceBundle resources;
   static {
      //Get the Resources
      try {
         resources = ResourceBundle.getBundle("org.xito.desktop.resources");
      }
      catch(MissingResourceException exp) {
         exp.printStackTrace();
         String title = "Desktop: Error Loading Resources";
         String msg = "Could not load Desktop Resources";
         DialogManager.showError(null, title,  msg, exp);
      }
   }
   
   
   /** Creates new Desktop */
   private DesktopService() {
      
      //Setup Desktop Theme
      setupDesktopTheme();
      
      //Get DocService
      docService = DefaultXMLDocumentService.getDefaultService();
      
      //Get Component Factory Service
      componentFactory = BLXCompFactory.getInstance();
          
      //Setup Desktop Server for TCP-IP Communications
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            desktopServer = new DesktopServer(PORT);
            desktopServer.start();
         }
      });
      
   }
   
   /**
    * Get the default Resources for the Desktop Package
    */
   public static ResourceBundle getResources() {
      return getDefaultService().resources;
   }
   
   /**
    * Get the Default Desktop Service
    */
   public static DesktopService getDefaultService() {
      if(singleton == null) {
         singleton = new DesktopService();
      }
      
      return singleton;
   }
   
   /**
    * Start the Desktop Service
    */
   public static void main(String args[]) {
      
      try {
         //Load Default Desktop Document
         ((DesktopService)getDefaultService()).loadDesktop();
         
         //Setup the ShutDown Thread
         Runtime.getRuntime().addShutdownHook(new ShutDownThread());
      }
      catch(Throwable exp) {
         exp.printStackTrace();
      }
   }
   
   /**
    * Load the Default Desktop
    */
   private void loadDesktop() {
      DesktopImpl desktopImpl = null;
      
      new BusyWorker() {
         public Object work() {
            
            //First get the Default Desktop Document from the Doc Service
            try {
               
               srvLogger.info(resources.getString("loading.msg"));
               Document document = docService.getDocument(DESKTOP_PATH + docService.PATH_SEPERATOR + "desktop" + BLXElement.FILE_EXT);
               URL desktopURL = new URL(docService.PROTOCOL, "localhost", docService.PATH_SEPERATOR + DESKTOP_PATH + docService.PATH_SEPERATOR);
               return (DesktopImpl)componentFactory.getObject(new BLXDocument(document, desktopURL, null));
            }
            catch(XMLDocumentNotFound notFound) {
               return getDefaultDesktop();
            }
            catch(Throwable exp) {
               exp.printStackTrace();
               return getBasicDesktop();
            }
            
         }
         
         public void finished(Object data) {
            setCurrentDesktop((DesktopImpl)data);
         }
      }.invokeLater();
   }
   
   private DesktopImpl getDefaultDesktop() {
      try {
         srvLogger.info(resources.getString("loading.default.msg"));
         URL desktopURL = new URL(codeBase, "default_desktop.blx");
         DesktopImpl desktopImpl = (DesktopImpl)componentFactory.getObject(desktopURL);
         
         //Complete Desktop needs to be saved
         saveAllChildren = true;
         
         return desktopImpl;
      }
      catch(Throwable exp) {
         //If there are any errors then load the BasicDesktop
         return getBasicDesktop();
      }
   }
   
   private DesktopImpl getBasicDesktop() {
      
      srvLogger.info(resources.getString("loading.basic.msg"));
      DesktopImpl desktopImpl = new DesktopImpl();
      desktopImpl.loadDefault();
      
      //Complete Desktop needs to be saved
      saveAllChildren = true;
      
      return desktopImpl;
   }
   
   
   /**
    * Center a Frame or Window on the Desktop
    * @param Window to Center
    */
   public void centerWindow(Window window) {
      
      Dimension win_size = window.getSize();
      Dimension desktop_size = getDesktopSize();
      int x = (desktop_size.width/2) - (win_size.width/2);
      int y = (desktop_size.height/2) - (win_size.height/2);
      window.setLocation(x,y);
   }
   
   /**
    * Get the Main Desktop Frame
    */
   public Frame getMainFrame() {
      
      return DCComponent.getDesktopFrame();
   }
   
   /**
    *
    *
    */
   public JDialog getDesktopDialog(String title, boolean modal) {
      return new JDialog(getMainFrame(), title, modal);
      
   }
   
   
   /**
    * Store the Current Desktop into a Document file
    * Stores in current Thread don't call from Event Dispatch Thread. If you do place in a busy worker
    */
   private void storeCurrentDesktop() {
      
      BLXStorageHandler docFactory = DocSrvBLXStorageHandler.getRootDocHandler();
      docFactory = docFactory.getChildHandler(DESKTOP_PATH);
      
      try {
         if(currentDesktop.isDirty() || saveAllChildren) {
            currentDesktop.store(saveAllChildren, docFactory);
         }
      }
      catch(Exception _exp) {
         _exp.printStackTrace();
      }
   }
   
   public void setCurrentDesktop(Desktop pDesktop) {
      //Notify Desktops that they are being Uninstalled and Installed
      if(currentDesktop != null) currentDesktop.desktopUninstalled();
      
      currentDesktop = pDesktop;
      currentDesktop.desktopInstalled();
   }
   
   public Desktop getCurrentDesktop() {
      return currentDesktop;
   }
   
   private static void setupDesktopTheme() {
      desktopTheme = new DesktopTheme();
      
      //Setup the Icon for the Main Frame
      ImageIcon _icon = new ImageIcon(DesktopService.class.getResource("/org/xito/launcher/images/xito16.gif"));
      DCComponent.setDesktopFrameIconImage(_icon.getImage());
      DCComponent.setDesktopFrameTitle("Xito");
      DCComponent.setDesktopFrameVisible(true);
      
      //Add A listener for closing of the desktop frame
      /*
      DCComponent.addDesktopFrameListener(new WindowAdapter(){
         public void windowClosing(WindowEvent evt) {
            if(Shell.getShell() == null) {
               System.exit(0);
            }
            else {
               Shell.getShell().endSession(false);
            }
         }});
       */
         
         //Get the Look and Feel
         LookAndFeel _laf = UIManager.getLookAndFeel();
         if(_laf instanceof javax.swing.plaf.metal.MetalLookAndFeel) {
            //Set the Theme to the Desktop Theme
            ((javax.swing.plaf.metal.MetalLookAndFeel)_laf).setCurrentTheme(desktopTheme);
            
            //ReSet the Look and Feel
            try {
               UIManager.setLookAndFeel(_laf);
               
               //Update the Components of all Frames
               Frame[] _frames = Frame.getFrames();
               for(int i=0;i<_frames.length;i++) {
                  SwingUtilities.updateComponentTreeUI(_frames[i]);
               }
            }
            catch(UnsupportedLookAndFeelException _exp) {
               _exp.printStackTrace();
            }
         }
   }
   
   public Dimension getDesktopSize() {
      return Toolkit.getDefaultToolkit().getScreenSize();
   }
   
   public Insets getDesktopInsets() {
       return Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
   }
   
   /**
    * Add NewItemFactory
    */
   public void addNewItemFactory(NewItemFactory factory) {
      if(newItemFactories.contains(factory)) return;
      //Add the Factory
      newItemFactories.add(factory);
   }
   
   /**
    * Remove NewItemFactory
    */
   public void removeNewItemFactory(NewItemFactory factory) {
      newItemFactories.remove(factory);
   }
   
   /**
    * This Thread is used to shutdown the Desktop Service Basically it stores the
    * Current Desktop and performs other shutdown operations for the Desktop
    */
   private static class ShutDownThread extends Thread {
      
      /**
       * Run Method for ShutDown Thread
       */
      public void run() {
         
         //Store the Current Desktop
         singleton.storeCurrentDesktop();
      }
   }
   
}

