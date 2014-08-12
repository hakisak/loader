package org.xito.launcher.applet;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

/**
 * Used to place a component into a full screen mode.
 * 
 * @author drichan
 *
 */
public class FullScreenContainer {

   private FullScreenContainerListener fullScreenListener;
   private JFrame fullScreenFrame;
   private Component fullScreenComp;
   private boolean is_fullScreen;
   private GraphicsDevice fullScreenDevice;
   private DisplayMode defaultDisplayMode;
   
   /**
    * 
    * @param comp to display full screen
    */
   public FullScreenContainer(Component comp, FullScreenContainerListener listener) {
      if(comp == null) throw new NullPointerException("comp can not be null");
      
      fullScreenComp = comp;
      fullScreenListener = listener;
   }
   
   /**
    * Determine the best display mode to use for the fullScreenComp
    * 
    * @return chosen display mode
    */
   private DisplayMode getBestDisplayMode() {
      
      ArrayList modes = new ArrayList();
      modes.addAll(Arrays.asList(fullScreenDevice.getDisplayModes()));
      Collections.sort(modes, new DisplayModeComparator());
      
      DisplayMode bestMode = null;
      int i = 0;
      
      while(bestMode == null) {
         i++;
         DisplayMode mode = (DisplayMode)modes.get(i);
         
         //check size
         int w = fullScreenComp.getWidth();
         int h = fullScreenComp.getHeight();
         
         if(w>mode.getWidth()) continue;
         if(h>mode.getHeight()) continue;
         
         //now check for other bit depths 
         bestMode = mode;
         
         for(int j=i;j<modes.size();j++) {
            mode = (DisplayMode)modes.get(j);
            if(mode.getWidth() == bestMode.getWidth() && mode.getHeight() == bestMode.getHeight()) {
               if(mode.getBitDepth() > bestMode.getBitDepth()) {
                  bestMode = mode;
               }
            }
            else {
               break;
            }
         }
      }
      
      return bestMode;
   }
   
   /**
    * Set FullScreen Mode. 
    * @param wantFullScreen true if should be switched to full screen mode false to switch to window mode
    */
   public void setFullScreen(boolean wantFullScreen) {
      
      if(wantFullScreen) { 
         if(is_fullScreen) return; //already full screen
         
         Window parentWindow = SwingUtilities.getWindowAncestor(fullScreenComp);
         if(parentWindow != null) {
            fullScreenDevice = parentWindow.getGraphicsConfiguration().getDevice();
         }
         else {
            fullScreenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
         }
         
         defaultDisplayMode = fullScreenDevice.getDisplayMode();
         DisplayMode bestMode = getBestDisplayMode();
         
         //we now have the best display mode to switch too
         System.out.println("selected displayMode:"+bestMode.getWidth()+"x"+bestMode.getHeight()+":"+bestMode.getBitDepth());
         setupFullScreenWindow(bestMode);
         is_fullScreen = true;
      }
      else {
         //already not in windowed
         if(!is_fullScreen) return;
         
         fullScreenDevice.setDisplayMode(defaultDisplayMode);
         fullScreenDevice.setFullScreenWindow(null);
         
         fullScreenFrame.dispose();
         is_fullScreen = false;
      }
      
      fullScreenListener.switchedFullScreenMode(this, is_fullScreen);
   }
   
   /**
    * Setup the FullScreen Window using the DisplayMode specified
    * @param displayMode
    */
   private void setupFullScreenWindow(DisplayMode displayMode) {
      
      fullScreenFrame = new JFrame();
      fullScreenFrame.getContentPane().setBackground(Color.BLACK);
      final ActionListener actionListener = new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            setFullScreen(false); //turn off fullscreen mode when ESCAPE is pressed
         }
      };
      
      fullScreenFrame.getRootPane().registerKeyboardAction(actionListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
      
      Panel bottomPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
      bottomPanel.setBackground(Color.BLACK);
      
      Label exitLbl = new Label("Exit Full Screen", JLabel.CENTER);
      exitLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      exitLbl.setForeground(Color.WHITE);
      exitLbl.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent arg0) {
            actionListener.actionPerformed(null);
         }
      });
      
      bottomPanel.add(exitLbl);
      
      fullScreenFrame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
      fullScreenFrame.setUndecorated(true);
      fullScreenFrame.add(fullScreenComp);
      fullScreenDevice.setFullScreenWindow(fullScreenFrame);
      fullScreenDevice.setDisplayMode(displayMode);
      fullScreenFrame.setLocation(fullScreenDevice.getDefaultConfiguration().getBounds().getLocation());
   }
   
   /**
    * Return true if the the display device is in FullScreen Mode
    * @return
    */
   public boolean isFullScreen() {
      return is_fullScreen;
   }
   
//--------------------------------------------------
   
   public class DisplayModeComparator implements Comparator {

      public int compare(Object d1, Object d2) {
         
         //put non display modes at bottom
         if(!(d1 instanceof DisplayMode)) {
            return 1;
         }
         
         if(!(d2 instanceof DisplayMode)) {
            return 1;
         }
         
         //now compare bit depth and resolution
         DisplayMode mode1 = (DisplayMode)d1;
         DisplayMode mode2 = (DisplayMode)d2;
         
         int w1 = mode1.getWidth();
         int w2 = mode2.getWidth();
         
         if(w1<w2) return -1;
         if(w1>w2) return 1;
         
         int h1 = mode1.getHeight();
         int h2 = mode2.getHeight();
         
         if(h1<h2) return -1;
         if(h1>h2) return 1;
         
         int bitd1 = mode1.getBitDepth();
         int bitd2 = mode2.getBitDepth();
         
         if(bitd1<bitd2) return -1;
         if(bitd1>bitd2) return 1;
         
         return 0;
      }
   }
   
}
