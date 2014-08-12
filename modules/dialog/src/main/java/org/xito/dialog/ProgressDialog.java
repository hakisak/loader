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

package org.xito.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;

/**
 * Implements an Progress Dialog with Animation
 *
 * @author  Deane Richan
 */
public class ProgressDialog extends CustomDialog {
   
   private Thread taskThread;
   private ProgressDialogDescriptor progressDescriptor;
   
   /**
    * Used by Sub-classes
    */
   protected ProgressDialog(Frame owner) {
      this(owner, null, false);
   }
   
   /** Creates a new instance of AlertDialog */
   public ProgressDialog(Frame owner, ProgressDialogDescriptor descriptor) {
      this(owner, descriptor, true);
   }
   
   /** Creates a new instance of AlertDialog */
   public ProgressDialog(Frame owner, ProgressDialogDescriptor descriptor, boolean modal) {
      super(owner, descriptor, modal);
   }
   
   /**
    * Build the Dialog, Overrides CustomDialog's init
    */
   protected void init() {
      progressDescriptor = (ProgressDialogDescriptor)descriptor;
      
      initRunnableTask();
      
      super.init();
   }
   
   /**
    * Initialize the Thread for the Runnable Task. The Task will not start until the Dialog is displayed
    */
   protected void initRunnableTask() {
      
      if(taskThread != null) {
         new IllegalStateException("The Task Thread has already been created");
      }
      
      //Create the Thread but don't start it
      if(progressDescriptor.getRunnableTask() != null) {
         taskThread = new Thread(progressDescriptor.getRunnableTask());
      }
      
      //Add a Listener to start the Thread when the Dialog is Opened
      this.addWindowListener(new WindowAdapter() {
         
         public void windowClosed(WindowEvent e) {
            
            //stop spinner
            if(progressDescriptor.getIconComp() instanceof BusySpinner) {
               ((BusySpinner)progressDescriptor.getIconComp()).stop();
            }
         }
         
         public void windowOpened(WindowEvent e) {
            
            //start spinner
            if(progressDescriptor.getIconComp() instanceof BusySpinner) {
               ((BusySpinner)progressDescriptor.getIconComp()).start();
            }
            
            //bail early if no taskThred
            if(taskThread == null) return;
            
            if(!taskThread.isAlive()) {
               taskThread.start();
               
               //Wait for Completion and then dispose org.xito
               if(progressDescriptor.disposeOnComplete()) {
                  Thread waitThread = new Thread(){
                     public void run() {
                        try {
                           taskThread.join();
                        }
                        catch(InterruptedException exp) {
                           System.out.println("Interrupted Wait Thread!");
                        }
                        ProgressDialog.this.dispose();
                        taskThread = null;
                     }
                  };
                  waitThread.start();
               }
            }
         }
      });
   }
   
   /**
    * Get the Thread that executes this Dialogs Runnable Task
    */
   public Thread getTaskThread() {
      
      return taskThread;
   }
   
   /**
    * Cancel a Runnable Task by interupting the Thread
    */
   public void cancelRunnableTask() {
      if(taskThread != null && taskThread.isAlive()) {
         taskThread.interrupt();
      }
   }
 
   /**
    * Spinner UI Animation
    */
   public static class BusySpinner extends JComponent implements Runnable {

      //SerialVersionID - update if the structure of the class changes
      private static final long serialVersionUID = 1L;
      
      private double[] stepLocations; // in radians
      private int steps = 12;
      private int step = 0;
      private int stepSleep = 50;
      private Thread spinnerThread;
      private Dimension mySize;
      private int handWidth;
      private int handHeight;

      private int delta = 360 / steps;

      private float handRatio = .5f;
      private float handHeightRatio = .3f;
      private int handXPad;
      private int handYPad;

      private int handX;
      private int handY;

      private boolean drawOutlines = true;
      private Stroke outlineStroke = new BasicStroke(.25f);
      private Color outlineColor = new Color(150,150,150);
      private Color stepHandColor = Color.BLACK;
      private Color stepHandColor1 = new Color(50,50,50);
      private Color stepHandColor2 = new Color(100,100,100);
      private Color stepHandColor3 = new Color(150,150,150);
      private Color handColor = new Color(225,225,225);

      private SpinnerTheme myTheme;

      public BusySpinner() {
          this(defaultTheme);
      }

      public BusySpinner(SpinnerTheme theme) {
          myTheme = theme;
          initValues();
      }

      private void initValues() {

          mySize = new Dimension(myTheme.size, myTheme.size);
          steps = myTheme.steps;
          drawOutlines = myTheme.drawHandOutlines;
          outlineStroke = myTheme.handStroke;
          outlineColor = myTheme.handOutlineColor;
          stepHandColor = myTheme.stepHandColor;
          stepHandColor1 = myTheme.stepHandColor1;
          stepHandColor2 = myTheme.stepHandColor2;
          stepHandColor3 = myTheme.stepHandColor3;
          handColor = myTheme.handColor;


          // set sizes
          setSize(getPreferredSize());
          setMinimumSize(getPreferredSize());
          setMaximumSize(getPreferredSize());

          // get step rotations
          stepLocations = new double[steps];
          for(int i=0;i<steps;i++) {
              stepLocations[i] = Math.toRadians(delta * i);
          }

          // set sizes
          handWidth = (int)((mySize.width/2) * handRatio) - 1;
          handHeight = (int)(handWidth * handHeightRatio);

          // set paddings
          handXPad = handWidth;
          handYPad = -(handHeight/2);

          handX= (mySize.width/2) + handXPad;
          handY= (mySize.height/2) + handXPad;
      }

      public void start() {
          if(spinnerThread != null && spinnerThread.isAlive()) {
              spinnerThread.interrupt();
          }

          spinnerThread = new Thread(this);
          spinnerThread.start();
      }

      public void stop() {
          if(spinnerThread != null) {
              spinnerThread.interrupt();
          }
      }

      public Dimension getPreferredSize() {
          Dimension totalSize = new Dimension(mySize);
          if(this.getBorder() != null) {
            Insets borderInsets = this.getBorder().getBorderInsets(this);  
            totalSize.width += borderInsets.left + borderInsets.right;
            totalSize.height += borderInsets.top + borderInsets.bottom;
          }
          
          return totalSize;
      }

      public void run() {

          try {
              while(true) {
                  this.repaint();
                  Thread.sleep(stepSleep);
                  step++;
                  if(step == steps) step = 0;
              }
          }
          catch(InterruptedException exp) {
              // expected
          }
      }

      private Color getStepColor(int currentStep, int i) {

          int delta = currentStep-i;
          if(delta < 0) {
              delta = steps + delta;
          }

          switch(delta) {
              case 0: return stepHandColor;
              case 1: return stepHandColor1;
              case 2: return stepHandColor2;
              case 3: return stepHandColor3;
              default: return handColor;
          }
      }

      public void paintComponent(Graphics g) {

          
          Graphics2D g2 = (Graphics2D)g;

          if(getBorder() != null) {
            Insets borderInsets = getBorder().getBorderInsets(this);
            g2.translate(borderInsets.left, borderInsets.top);
          }
          
          HashMap hints = new HashMap();
          hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g2.setRenderingHints(hints);
          g2.setStroke(outlineStroke);

          // move to center
          g2.translate(handX - handXPad, handY - handXPad);

          int currentStep = step;

          for(int i=0;i<steps;i++) {
              g2.rotate(stepLocations[i]);

              // fill all hands
              g2.setPaint(getStepColor(currentStep, i));
              g2.fillRoundRect(handXPad, handYPad, handWidth, handHeight, handHeight, handHeight);

              // draw outline of all hands
              if(drawOutlines) {
                  g2.setPaint(outlineColor);
                  g2.drawRoundRect(handXPad, handYPad, handWidth, handHeight, handHeight, handHeight);
              }

              // rotate back to origin
              g2.rotate(-stepLocations[i]);
          }
      }
      
      public static class SpinnerTheme {

         public int size = 100;
         public int steps = 12;
         public boolean drawHandOutlines = true;
         public int panelOutline = 4;
         public int panelRadius = 40;
         public Color panelBackground = Color.WHITE;
         public boolean drawPanelOutline = true;
         public Color panelOutlineColor = new Color(150,150,150);
         public Stroke handStroke = new BasicStroke(.25f);
         public Color stepHandColor = Color.BLACK;
         public Color stepHandColor1 = new Color(50,50,50);
         public Color stepHandColor2 = new Color(100,100,100);
         public Color stepHandColor3 = new Color(150,150,150);
         public Color handColor = new Color(225,225,225);
         public Color handOutlineColor = new Color(150,150,150);
         public float transparency = 1.0f;
     }

     public static SpinnerTheme defaultTheme = new SpinnerTheme();
     public static SpinnerTheme dialogTheme = new SpinnerTheme();
     public static SpinnerTheme whiteTheme = defaultTheme;
     public static SpinnerTheme blackTheme = new SpinnerTheme();

     //black theme
     static {
         blackTheme.drawHandOutlines = false;
         blackTheme.stepHandColor = Color.WHITE;
         blackTheme.handOutlineColor = new Color(225,225,225);
         blackTheme.stepHandColor1 = new Color(200,200,200);
         blackTheme.stepHandColor2 = new Color(150,150,150);
         blackTheme.stepHandColor3 = new Color(100,100,100);
         blackTheme.handColor = new Color(50,50,50);
         blackTheme.transparency = .90f;
         blackTheme.panelBackground = Color.BLACK;
         blackTheme.drawPanelOutline = false;
         blackTheme.panelOutlineColor = blackTheme.panelBackground;
         
         dialogTheme.size = 48;
     }

   }


   
   
}
