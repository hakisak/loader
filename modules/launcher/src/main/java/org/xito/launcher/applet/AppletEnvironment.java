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

package org.xito.launcher.applet;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.lang.ref.*;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

import org.xito.boot.*;
import org.xito.dialog.TableLayout;

/**
 * The applet environment including stub, context, and frame.  The
 * default environment puts the applet in a non-resiable frame;
 * this can be changed by obtaining the frame and setting it
 * resizable.
 *
 * If an HTML file contains more then one Applet it will be shown in
 * the same frame
 *
 * @author Deane Richan
 * @version $Revision: 1.19 $
 */
public class AppletEnvironment implements AppletContext, AppletStub, FullScreenContainerListener {
   
   private static Logger logger = Logger.getLogger(AppletEnvironment.class.getName());
   
   /** applet container **/
   private AppletContainerPanel appletContainer;
   
   /** reflection panel **/
   private ReflectionPanel reflectionPanel;
   
   /** the applet desc */
   private AppletDesc appletDesc;
   
   /** the applet instance */
   private AppletInstance appletInstance;
   
   private StatusPanel statusPanel;
   
   /** the applet */
   private Applet applet;
   
   /** the applet frame */
   private JFrame frame;
   
   /** the default graphics device */
   private FullScreenContainer fullScreenContainer;
   
   /** soft references to the audio clips */
   private HashMap audioClips = new HashMap();
   
   /** whether the applet has been started / displayed */
   private boolean appletStarted = false;
   
   /** whether the applet has been destroyed */
   private boolean destroyed = false;
      
   /**
    * Create a new applet environment for the applet specified by
    * the Applet Descriptor.
    */
   public AppletEnvironment(AppletDesc appletDesc, AppletInstance appletInstance) {
      
      this.appletDesc = appletDesc;
      this.appletInstance = appletInstance;
      this.applet = appletInstance.getApplet();
      frame = new JFrame(appletDesc.toString());
      frame.setIconImage(AppletActionBeanInfo.icon16.getImage());
      //frame.setLayout(new BorderLayout());
      statusPanel = new StatusPanel();
      frame.setContentPane(new Panel(new BorderLayout()));
      frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
      
      //frame.setResizable(appletDesc.isResizable());
      this.appletInstance.addWindow(frame);
      
      // may not need this once security manager can close windows
      // that do not have app code on the stack
      WindowListener closer = new WindowAdapter() {
         public void windowClosing(WindowEvent event) {
            frame.dispose();
         }
         
         public void windowClosed(WindowEvent event) {
            storeLocation();
            AppletEnvironment.this.appletInstance.destroy();
            //could this be moved to Boot somehow 
            if(Boot.isLaunchingExternal())
               org.xito.boot.Boot.endSession(true);
         }
      };
      frame.addWindowListener(closer);
   }
   
   /**
    * Show the Applet Full Screen set to false to return to normal window
    * @param wantsfullScreen
    */
   public void setFullScreenMode(boolean wantsfullScreen) {
     
      if(fullScreenContainer == null) {
         fullScreenContainer = new FullScreenContainer(appletContainer, this);
      }
      
      //set full screen mode or set back to windowed mode
      fullScreenContainer.setFullScreen(wantsfullScreen);
   }
   
   /**
    * Called by FullScreenContainer when full screen mode is switched
    */
   public void switchedFullScreenMode(FullScreenContainer fullScreenContainer, boolean isFullScreen) {
      if(!isFullScreen) {
         frame.getContentPane().add(appletContainer);
         frame.pack();
         frame.toFront();
      }
   }

   /**
    * Store the Current Frame Location
    */
   private Point getStoredLocation() {
      
      Preferences prefs = Preferences.userNodeForPackage(AppletEnvironment.class);
      int x = prefs.getInt(appletDesc.getUniqueID()+".x", -1);
      int y = prefs.getInt(appletDesc.getUniqueID()+".y", -1);
      
      return new Point(x,y);
   }
   
   /**
    * Store the Current Frame Location
    */
   private void storeLocation() {
      Point p = frame.getLocation();
      Preferences prefs = Preferences.userNodeForPackage(AppletEnvironment.class);
      prefs.putInt(appletDesc.getUniqueID()+".x", p.x);
      prefs.putInt(appletDesc.getUniqueID()+".y", p.y);
      try {
         prefs.flush();
      }
      catch(BackingStoreException exp) {
         logger.warning("Exception storing preferences: "+exp.getMessage());
      }
   }
   
   /**
    * Checks whether the applet has been destroyed, and throws an
    * IllegalStateException if the applet has been destroyed of.
    *
    * @throws IllegalStateException
    */
   private void checkDestroyed() {
      if (destroyed)
         throw new IllegalStateException("Illegal applet stub/context access: applet destroyed.");
   }
   
   /**
    * Disposes the applet's resources and disables the applet
    * environment from further use; after calling this method the
    * applet stub and context methods throw IllegalStateExceptions.
    */
    public void destroy() {
        destroyed = true;
     
        Iterator it = audioClips.values().iterator();
        while(it.hasNext()) {
           ((SoftReference)it.next()).clear();
        }
    }
   
   /**
    * Returns the frame that contains the applet.  Disposing this
    * frame will destroy the applet.
    */
   public Frame getAppletFrame() {
      return frame;
   }
   
   /**
    * Initialize, start, and show the applet.
    */
    public void startApplet() {
     
      checkDestroyed();

      if (appletStarted)
         return;

      appletStarted = true;

      try {
         applet.setStub(this);
         applet.setSize(appletDesc.getWidth(), appletDesc.getHeight());
         TableLayout appletLayout = null;
         
         if(this.appletDesc.isResizable()) {
            frame.getContentPane().add(applet);
         }
         else {
            appletLayout = new TableLayout(AppletEnvironment.class.getResource("applet_container_layout.html"));
            appletLayout.getRow(1).height = appletDesc.getHeight();
            appletLayout.getRow(1).getColumn("applet").width = appletDesc.getWidth();
            
            //for some reason we have to do this
            appletContainer = new AppletContainerPanel(appletLayout);
            Panel appletHolder = new Panel(null);
            appletHolder.add(applet);
            
            appletContainer.add("applet", appletHolder);
            frame.getContentPane().add(appletContainer);
         }
         
         
         frame.pack();
         
         //add reflection after packing
         if(!this.appletDesc.isResizable()) {
            reflectionPanel = new ReflectionPanel();
            appletContainer.add("reflection", reflectionPanel);
         }
         else {
            Insets insets = frame.getInsets();
            frame.setSize(appletDesc.getWidth() + insets.left + insets.right,
                        appletDesc.getHeight() + insets.top + insets.bottom + statusPanel.getHeight());
         }
         
         Point p = getStoredLocation();
         if(p.x != -1 && p.y != -1) {
            frame.setLocation(p);
         }
         else {
            centerFrame(frame);
         }
         
         frame.show();
         
         applet.init();
         applet.start();

         frame.invalidate(); 
         frame.validate();   
         frame.repaint();
         
         frame.toFront();
         applet.requestFocusInWindow();
      }
      catch (Exception ex) {
        logger.log(Level.SEVERE, ex.getMessage(), ex);
      }
   }
    
   private void centerFrame(Frame frame) {
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      frame.setLocation((screen.width - frame.getWidth())/2, (screen.height - frame.getHeight())/2);
   }
   
   /**
    * Returns the applet if the applet's name is specified,
    * otherwise return null.
    */
   public Applet getApplet(String name) {
      checkDestroyed();
      
        if (name != null && name.equals(appletDesc.getName()))
            return applet;
        else
            return null;
   }
   
   /**
    * Returns an enumeration that contains only the applet
    * from the JNLP file.
    */
   public Enumeration getApplets() {
      checkDestroyed();
      
      return Collections.enumeration( Arrays.asList(new Applet[] { applet }) );
   }
   
   /**
    * Returns an audio clip.
    */
   public AudioClip getAudioClip(URL location) {
      checkDestroyed();
      
      SoftReference ref = (SoftReference)audioClips.get(location);
      if(ref != null && ref.get() != null) {
         return (AudioClip)ref.get();
      }
      
      AudioClip clip = null;
      try {
         Boot.getCacheManager().downloadResource(location, null);
         File f = Boot.getCacheManager().getCachedFileForURL(location);
         FileInputStream in = new FileInputStream(f);
         clip = new com.sun.media.sound.JavaSoundAudioClip(in);
         
         //clip = new AppletAudioClip(Boot.getCacheManager().getResource(location, null, null));
         audioClips.put(location, new SoftReference(clip));
      }
      catch(IOException ioExp) {
         logger.log(Level.WARNING, ioExp.toString(), ioExp);
      }
      
      return clip;
   }
   
   /**
    * Return an image loaded from the specified location.
    */
   public Image getImage(URL location) {
      checkDestroyed();
      
      //return Toolkit.getDefaultToolkit().createImage(location);
      Image image = (new ImageIcon(location)).getImage();
      
      return image;
   }
   
   /**
    * Not implemented yet.
    */
   public void showDocument(java.net.URL uRL) {
      checkDestroyed();
      
   }
   
   /**
    * Not implemented yet.
    */
   public void showDocument(java.net.URL uRL, java.lang.String str) {
      checkDestroyed();
      
   }
   
   /**
    * Not implemented yet.
    */
   public void showStatus(java.lang.String str) {
      checkDestroyed();
      
   }
   
   /**
    * Required for JRE1.4, but not implemented yet.
    */
   public void setStream(String key, InputStream stream) {
      checkDestroyed();
      
   }
   
   /**
    * Required for JRE1.4, but not implemented yet.
    */
   public InputStream getStream(String key) {
      checkDestroyed();
      
      return null;
   }
   
   /**
    * Required for JRE1.4, but not implemented yet.
    */
   public Iterator getStreamKeys()  {
      checkDestroyed();
      
      return null;
   }
   
   // stub methods
   
   public void appletResize(int width, int height) {
      checkDestroyed();
      
      Insets insets = frame.getInsets();
      
      frame.setSize(width + insets.left + insets.right,
      height + insets.top + insets.bottom);
   }
   
   public AppletContext getAppletContext() {
      checkDestroyed();
      
      return this;
   }
   
   public URL getCodeBase() {
      checkDestroyed();
      
      return appletDesc.getCodeBaseURL();
   }
   
   public URL getDocumentBase() {
      checkDestroyed();
      
      return appletDesc.getDocumentBaseURL();
   }
   
   public String getParameter(String name) {
      checkDestroyed();
      if(name == null) return null;
      
      return (String) appletDesc.getParameters().get(name.toLowerCase());
   }
   
   public boolean isActive() {
      checkDestroyed();
      
      // it won't be started or stopped, so if it can call it's running
      return true;
   }
   
   private class ReflectionPanel extends JPanel {

      private HashMap renderingHints = new HashMap();
      private BufferedImage bufImage;
      private GradientPaint bottomGradient;
      
      public ReflectionPanel() {
         
         bottomGradient = new GradientPaint(0, 0, new Color(0,0,0,150), 0, 180, new Color(0,0,0));
         renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         bufImage = new BufferedImage(applet.getWidth(), applet.getHeight(), BufferedImage.TYPE_INT_RGB);
         
         Thread t = new Thread() {
            public void run() {
               try {
                  long lastPaint = System.currentTimeMillis(); 
                  while(appletInstance.isRunning()) {
                     if(applet.isVisible()) {
                        long delay = System.currentTimeMillis() - lastPaint;
                        if(delay<100) {
                           Thread.sleep(50);
                        }
                        
                        ReflectionPanel.this.repaint();
                        lastPaint = System.currentTimeMillis();
                     }
                  }
               }
               catch(Exception exp){}
            }
         };
         
         t.start();
      }
      
      public Dimension getPreferredSize() {
         return new Dimension(getWidth(), 200);
      }
      
      public void paint(Graphics g) {
         
         Graphics2D g2 = (Graphics2D)g;
         g2.addRenderingHints(renderingHints);
         
         //paint the initial background
         g2.setColor(Color.BLACK);
         g2.fillRect(0,0,getWidth(), getHeight());
         
         //create a reflection image of the applet
         AffineTransform transform = new AffineTransform(1, 0, 0, -1, 0, applet.getHeight());
         Graphics2D imageG = bufImage.createGraphics();
         imageG.setTransform(transform);
         applet.update(imageG);
         
         //move down and then paint the reflected image
         g2.translate(0, 5);
         g2.drawImage(bufImage, 0, 0, bufImage.getWidth(), bufImage.getHeight(), null);
         g2.setPaint(bottomGradient);
         g2.fillRect(0,0,getWidth(), 200);
      }
   }
   
   
   //------------------------------------------------------
   
   private class AppletContainerPanel extends Panel {
      
      public AppletContainerPanel(LayoutManager layout) {
         super(layout);
      }
      
      public void paint(Graphics g) {
         Graphics2D g2 = (Graphics2D)g;
         HashMap map = new HashMap();
         map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.addRenderingHints(map);
                  
         GradientPaint gp = new GradientPaint(0,0, new Color(25,25,25), 0, getHeight()/2, new Color(50,50,50));
         g2.setPaint(gp);
         g2.fillRect(0,0,getWidth(), getHeight());
         
         g2.setPaint(Color.BLACK);
         g2.fillRect(0, 0, getWidth(), getHeight());
         //g2.fillRect(0, getHeight()/2, getWidth(), getHeight());
         //g2.fillPolygon(new int[]{0,getWidth(), getWidth()}, new int[]{getHeight(), 0, getHeight()}, 3);
         
         g.setClip(0,0,getWidth(), getHeight());
         //do default painting
         super.paint(g);
      }
   }
   
   //------------------------------------------------------
   
   /**
    * Panel to show applet status at the bottom of the Frame
    */
   private class StatusPanel extends Panel implements ActionListener{
      
      private Font lblFont; 
      private JPopupMenu optionsMnu;
      private JMenuItem fullScreenModeMI;
      
      public StatusPanel() {
         init();
      }
      
      private void init() {
         setLayout(null);

         //Full Screen mode for Appets isn't working right
         /*
         lblFont = new JLabel().getFont().deriveFont(12f);
         optionsMnu = new JPopupMenu();
         fullScreenModeMI = new JMenuItem("Full Screen");
         fullScreenModeMI.addActionListener(this);
         optionsMnu.add(fullScreenModeMI);
         
         this.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent evt) {
               optionsMnu.show(StatusPanel.this, evt.getX(), evt.getY());
            }
            
         });
         */
         
      }
      
      public Dimension getPreferredSize() {
         return new Dimension(100, 25);
      }
      
      public void actionPerformed(ActionEvent evt) {
         if(evt.getSource() == fullScreenModeMI) {
            AppletEnvironment.this.setFullScreenMode(true);
         }
      }

      public void paint(Graphics g) {
         Graphics2D g2 = (Graphics2D)g;
         
         GradientPaint gp = new GradientPaint(0,0, new Color(150,150,150), 0, 25, new Color(75,75,75));
         g2.setPaint(gp);
         g2.fillRect(0,0,getWidth(), getHeight());
         
         g2.setColor(Color.WHITE);
         g2.drawString(System.getProperty("awt.appletWarning"), 10,16);
         //g.fillRect(0,0,getWidth(), getHeight());
      }
   }
}

