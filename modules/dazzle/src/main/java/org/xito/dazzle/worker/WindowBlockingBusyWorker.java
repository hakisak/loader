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

package org.xito.dazzle.worker;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import javax.swing.*;

import org.xito.dazzle.widget.progress.ProgressSpinnerPanel;
import org.xito.dazzle.widget.progress.ProgressSpinner;
import org.xito.dialog.TableLayout;

/**
 * Will optionally block a Window from receiving input will the 
 * BusyWorker is executing
 *  
 * @author drichan
 */
public abstract class WindowBlockingBusyWorker<T> extends BusyWorker<T> {
   
   protected BusyGlassPane busyGlassPane;
   protected Component orgGlassPane;
   protected JMenuBar orgMenuBar;
   protected boolean showBusyPanel = true;
   protected Window blockWindow;
   private boolean cancelEnabled;
   
   public WindowBlockingBusyWorker(Window blockWindow) {
      this.blockWindow = blockWindow;
   }
   
   protected void preWork() {
      try {
         SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
               installGlassPane();
            }
         });
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   protected void postWork() {
      //not used
   }

   protected void preFinished() {
      //not used
   }

   protected void postFinished() {
      uninstallGlassPane();
   }
   
   /**
    * Set to true if the busy panel should be shown while doing work
    * @param showBusyPanel
    */
   public void setShowBusyPanel(boolean showBusyPanel) {
      this.showBusyPanel = showBusyPanel;
   }

   /**
    * Return true if the busy panel should be shown
    * @return
    */
   public boolean getShowBusyPanel() {
      return this.showBusyPanel;
   }
   
   /**
    * Set the cancel enabled flag. Setting to true will provide a cancel button 
    * on the busy panel that can be clicked by the user to interrupt the work in the busy worker.
    * @param cancelEnabled
    */
   public void setCancelEnabled(boolean cancelEnabled) {
      this.cancelEnabled = cancelEnabled;
   }
   
   /**
    * Return true if cancel is enabled for this busy worker
    * @return
    */
   public boolean isCancelEnabled() {
      return this.cancelEnabled;
   }
   
   /**
    * Get the current GlassPane
    * 
    * @return
    */
   private Component getGlassPane() {
      if(blockWindow instanceof JFrame) {
         return ((JFrame)blockWindow).getGlassPane();
      }
      else if(blockWindow instanceof JWindow) {
         return ((JWindow)blockWindow).getGlassPane();
      }
      else if(blockWindow instanceof JDialog) {
         return ((JDialog)blockWindow).getGlassPane();
      }
      else {
         return null;
      }
   }
   
   /**
    * Set the  GlassPane
    * 
    * @return
    */
   private void setGlassPane(Component gp) {
      
      if(blockWindow instanceof JFrame) {
         ((JFrame)blockWindow).setGlassPane(gp);
      }
      else if(blockWindow instanceof JWindow) {
         ((JWindow)blockWindow).setGlassPane(gp);
      }
      else if(blockWindow instanceof JDialog) {
         ((JDialog)blockWindow).setGlassPane(gp);
      }
   }
   
   /**
    * Disable the blocked frames menu bar
    */
   protected void disableFrameMenu() {
      if(blockWindow instanceof JFrame) {
         orgMenuBar = ((JFrame)blockWindow).getJMenuBar();
         if(orgMenuBar != null) {
             JMenuBar dummyMenuBar = new JMenuBar();
             dummyMenuBar.add(new JMenu("File"));
             ((JFrame)blockWindow).setJMenuBar(dummyMenuBar);
         }
      }
   }

   /**
    * Restore the blocked frames menu bar
    */
   protected void restoreFrameMenu() {
       if(blockWindow instanceof JFrame) {
         ((JFrame)blockWindow).setJMenuBar(orgMenuBar);
      }
   }

   /**
    * Install the GlassPane
    */
   protected void installGlassPane() {
      
      Component gp = getGlassPane();
      
      //don't set glasspane if already a BusyGlassPane
      if(BusyGlassPane.class.isInstance(gp)) return;
      
      orgGlassPane = gp;
      busyGlassPane = new BusyGlassPane();
      setGlassPane(busyGlassPane);
      disableFrameMenu();
      busyGlassPane.setVisible(true);
      busyGlassPane.start(null);
   }

   /**
    * Uninstall the GlassPane
    */
   protected void uninstallGlassPane() {
      
      if(busyGlassPane != null) {
         busyGlassPane.stop(new Runnable() {
             public void run() {
                 busyGlassPane.setVisible(false);
                 if(orgGlassPane != null) {
                     restoreFrameMenu();
                     setGlassPane(orgGlassPane);
                  }
             }
         });
      }
      
   }
   
   
   /**
    * Glass Pane used to block input 
    * @author deane
    */
   protected class BusyGlassPane extends JPanel {
      
      private ProgressSpinnerPanel busyPanel;
      
      public BusyGlassPane() {

         //add empty mouse and keylistener to swallow events
         addMouseListener(new MouseAdapter(){});
         addKeyListener(new KeyAdapter(){});

         busyPanel = new ProgressSpinnerPanel(ProgressSpinner.blackTheme);
         busyPanel.setCancelEnabled(cancelEnabled);
         
         TableLayout layout = new TableLayout("<html><table>"
               + "<tr height=\"50%\"><td width=\"50%\"></td><td></td><td width=\"50%\"></td></tr>"
               + "<tr><td></td><td align=\"center\">panel</td><td></td></tr>" + "<tr height=\"50%\"></tr>"
               + "</table></html>");

         setLayout(layout);
         
         setOpaque(false);
         //setBackground(new Color(50,50,50,50));
         if(showBusyPanel) {
            add("panel", busyPanel);
         }
      }
      
      public ProgressSpinnerPanel getBusyPanel() {
         return busyPanel;
      }
      
      public void start(Runnable beforeStartTask) {
         busyPanel.setBeforeStartTask(beforeStartTask);
         
         //install cancel support
         if(cancelEnabled) {
            busyPanel.setCancelTask(new Runnable(){
               public void run() {
                  cancel();
               }
            });
         }
         
         busyPanel.start();
      }
      
      public void stop(Runnable afterStopTask) {
         busyPanel.setAfterStopTask(afterStopTask);
         busyPanel.stop();
      }
   }

}
