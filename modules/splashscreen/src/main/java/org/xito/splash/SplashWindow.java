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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.util.logging.*;
import org.xito.boot.*;


/**
 * SplashWindow is used to Display the splash Screen when the application starts.
 *<p>
 * The Splash Window is always 500 x 300.
 *</p><p>
 * The Service Messages logged to the org.xito.shell category 
 * will be displayed along the bottom of the Splash Window
 *</p>
 *
 * @author  drichan
 * @version $Revision: 1.8 $
 * @since   $Date: 2007/09/02 01:10:29 $
 */
public class SplashWindow extends JPanel implements StartupListener {
   
   private URL imageURL;
   private ImageIcon bkImage;
   private MyWindow window;
   private JPanel mainPanel;
   private JLabel lblMessage;
   
   private int splashWidth, splashHeight;
      
   /**
    * Create the SplashWindow
    * @param pImageURL url for the Splash Image
    */
   public SplashWindow(URL pImageURL, int width, int height) {
      
      imageURL = pImageURL;
      splashWidth = width;
      splashHeight = height;
      window = new MyWindow(new Frame());
      init();
   }
   
   /**
    * Build the Splash Window
    */
   private void init() {
      
      //Add Shell Listener
      Boot.getServiceManager().addStartupListener(this);
            
      if(imageURL != null) {
         try {
            imageURL = Boot.getCacheManager().getResource(imageURL, null, null);
            bkImage = new ImageIcon(imageURL);
         }
         catch(IOException ioExp) {
            ioExp.printStackTrace();
         }
      }
      
      mainPanel = new MyPanel();
      mainPanel.setBackground(Color.white);
      window.setSize(splashWidth, splashHeight);
      window.setLayout(null);
      window.add(mainPanel);
      mainPanel.setBounds(0, 0, splashWidth, splashHeight);
      
      //Center Window
      Dimension _size = Toolkit.getDefaultToolkit().getScreenSize();
      int x = (_size.width / 2) - window.getWidth()/2;
      int y = (_size.height / 2) - window.getHeight()/2;
      window.setLocation(x, y);
     
      //Message Label
      lblMessage = new JLabel("Starting "+Boot.getAppDisplayName());
      lblMessage.setFont(new Font("SansSerif", Font.PLAIN, 10));
      lblMessage.setBorder(new EmptyBorder(0, 4, 2, 0));
      //lblMessage.setBounds(5, mainPanel.getHeight()-20, mainPanel.getWidth()-5, mainPanel.getHeight());
      mainPanel.add(lblMessage, BorderLayout.SOUTH);
      
      //Add Log Handler
      //Logger.getLogger(Boot.class.getPackage().getResourceName()).addHandler(new MyLogHandler());
   }
   
   /**
    * Dispose of the SplashWindow
    */
   public void dispose() {
      window.dispose();
   }
   
   /**
    * Show the Splash Window
    */
   public void showWindow() {
      window.show();
      window.toFront();
   }
   
   /**
    * Called when the Service Manager is about to Start a Service
    */
   public void serviceStarting(ServiceDesc service) {
      lblMessage.setText("Starting: "+service.getDisplayName());
      window.toFront();
   }
   
   /**
    * Called when the Service Manager has Booted
    * This is fired after all Boot Services have started
    */
   public void sessionBooted() {
   }
   
   /**
    * Called when the Service Manager has Started
    * This is fired after all Startup Services have started
    */
   public void sessionStarted() {
      dispose();
   }
   
   /**
    * Called when the Service Manager is Ending
    * This is fired before all Services have been stopped
    */
   public void sessionEnded() {
   }
   
   /**
    * Log message Handler
    */
   class MyLogHandler extends Handler {
      
      public void close() throws SecurityException {
      }
      
      public void flush() {
      }
      
      public void publish(LogRecord record) {
         SplashWindow.this.lblMessage.setText(record.getMessage());
      }
   }
      
   /**
    * Window used for the Splash Screen
    */
   public class MyWindow extends Window implements FocusListener {
      public MyWindow(Frame pParent) {
         super(pParent);
         this.addFocusListener(this);
      }
      
      public void focusLost(FocusEvent e) {
      }
      
      public void focusGained(FocusEvent e) {
         dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_ACTIVATED));
      }
   }
   
   /**
    * Class Used to Paint the BackGround Image for the Splash Screen
    */
   class MyPanel extends JPanel {
      public MyPanel() {
         super();
         setLayout(new BorderLayout());
      }
      
      /**
       * Paint the Component with the Background Image
       */
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         if(bkImage != null) {
            g.drawImage(bkImage.getImage(), 0, 0, null);
         }
         
         super.paintChildren(g);
      }
      
      public Dimension getPreferredSize() {
         return getSize();
      }
   }
}
