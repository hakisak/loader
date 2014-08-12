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

package org.xito.dazzle.dialog;

import java.awt.Graphics;

import org.xito.dazzle.utilities.DrawUtilities;
import org.xito.dazzle.utilities.ImageUtilities;
import org.xito.dazzle.utilities.UIUtilities;
import org.xito.dazzle.widget.border.ShadowBorder;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ContainerListener;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JWindow;
import org.xito.dazzle.animation.timing.Animator;
import org.xito.dazzle.animation.timing.TimingTargetAdapter;
import org.xito.dazzle.widget.TransparencyDecorator;
import org.xito.dazzle.widget.DecorationComponent;
import org.xito.dazzle.widget.TransparencyDecorator.FadeAnimationTarget;
import org.xito.dialog.DialogDescriptor;
import org.xito.dialog.DialogManager;
import org.xito.dialog.DialogPanel;
import org.xito.dialog.Validatable;

/**
 * Dialog that reveals using the Apple Sheet Style
 * 
 * @author deane
 */
public class SheetDialog extends JComponent {

   /** owner of the SheetDialog sould be JWindow or JFrame */
   protected Object owner;

   /** true if the original owner had a visible glass pane */
   protected boolean showOriginalGlassPane = false;
   
   /** orignal owner content pane if using heavy mode */
   protected Container orgOwnerContentPane;

   /** the glasspane we use while the sheet is revealed */
   protected GlassPane glassPane;
   
   /** custom panel placed into the sheet */
   protected JPanel customPanel;

   /** DialogPanel used if we are using a org.xito descriptor */
   protected DialogPanel dialogPanel;

   /** the org.xito description */
   protected DialogDescriptor dialogDescriptor;

   /** the result of the org.xito */
   protected int dialogResult;

   /** listener notified when sheet is dismissed */
   protected ConfirmSheetActionListener defaultActionListener;

   /** listens for sheet events */
   protected SheetPaneListener sheetPaneListener;

   /** glass pane paint used to overlay the contents of th owner */
   protected Paint overlayPaint = Color.BLACK;

   /** if fade glass is used this is the percent alpha we will fade to */
   protected float fadeGlassPanePercent = 0.1f;

   /** true if glass pane should be faded in */
   protected boolean fadeGlassPane = false;

   /** if using heavy mode the owner frame's resizeable setting is stored here */
   protected boolean ownerIsResiable = true;
   
   /** true if using heavy mode */
   protected boolean useHeavyWeightSupport = false;
   
   /**
    * Create a SheetDialog
    */
   protected SheetDialog() {
      // only used by sub-classes
   }

   /**
    * Create a sheet using a org.xito descriptor
    * 
    * @param desc
    */
   public SheetDialog(DialogDescriptor desc) {
      setDialogDescriptor(desc);
   }

   /**
    * set DialogDescriptor
    */
   protected void setDialogDescriptor(DialogDescriptor desc) {

      this.dialogDescriptor = desc;
      setLayout(new BorderLayout());

      // add action listeners to buttons
      if (desc.getActionListeners().isEmpty()) {
         defaultActionListener = new ConfirmSheetActionListener();
         desc.addActionListener(defaultActionListener);
      }

      // create the org.xito panel
      dialogPanel = new DialogPanel(desc);

      // set size
      int w = (desc.getWidth() != 0) ? desc.getWidth() : dialogPanel.getPreferredSize().width;
      int h = (desc.getHeight() != 0) ? desc.getHeight() : dialogPanel.getPreferredSize().height;

      setSize(w, h);

      add(dialogPanel);
      initBorder();
   }

   /**
    * Create a Sheet that uses a specific custom panel
    * 
    * @param customPanel
    */
   public SheetDialog(JPanel customPanel) {
      this.customPanel = customPanel;
      setLayout(new BorderLayout());
      setSize(customPanel.getPreferredSize());
      add(this.customPanel);
      initBorder();
   }

   /**
    * Get the Dialog Panel used in this sheet org.xito
    * @return
    */
   public DialogPanel getDialogPanel() {
      return dialogPanel;
   }

   /**
    * Initialize the border, default is to use a shadow
    */
   protected void initBorder() {
      setOpaque(false);
      this.setBorder(new ShadowBorder());
   }
   
   /**
    * @return the useHeavyWeightSupport
    */
   public boolean isUseHeavyWeightSupport() {
      return useHeavyWeightSupport;
   }

   /**
    * If set to true the owner windows content pane will be removed and replaced with a screen shot
    * of the window. This will enable heavy weight components to live in the content pane and have the 
    * sheet still work correctly. When in heavy weight mode the frame cannot be resized while the sheet
    * is revealed.
    * 
    * @param useHeavyWeightSupport the useHeavyWeightSupport to set
    */
   public void setUseHeavyWeightSupport(boolean useHeavyWeightSupport) {
      this.useHeavyWeightSupport = useHeavyWeightSupport;
   }

   /**
    * Returns the org.xito result if using a DialogDescriptor
    * 
    * @return
    */
   public int getDialogResult() {
      return dialogResult;
   }

   /**
    * Set the owner to a frame
    * 
    * @param owner
    */
   public void setOwner(JFrame owner) {
      this.owner = owner;
   }

   /**
    * Set the owner to a window
    * 
    * @param owner
    */
   public void setOwner(JWindow owner) {
      this.owner = owner;
   }

   /**
    * Gets the JRootPane from the owner
    * @return
    */
   protected JRootPane getOwnerRootPane() {

      if (owner instanceof JWindow) {
         return ((JWindow) owner).getRootPane();
      } else if (owner instanceof JFrame) {
         return ((JFrame) owner).getRootPane();
      } else {
         return null;
      }
   }
   
   /**
    * return true if the owner is resizable
    * @return
    */
   protected boolean getOwnerIsResizable() {
      if (owner instanceof JFrame) {
         return ((JFrame) owner).isResizable();
      } else {
         return false;
      }
   }

   /**
    * Check to see if an owner window is specified
    * 
    * @throws RuntimeException
    *            if owner window is not specified
    */
   protected void checkOwnerSpecified() {
      if (owner == null) {
         throw new RuntimeException("owner window is not specified");
      }
   }

   /**
    * Get the Owners glass pane component if there is one
    * @return
    */
   protected Component getOwnerGlassPane() {

      if (getOwnerRootPane() != null) {
         return getOwnerRootPane().getGlassPane();
      } else {
         return null;
      }
   }

   /**
    * Set the new glass pane on the owner. This actually places the glasspane used by the sheet
    * in the MODAL_LAYER of the owner window's JLayer Pane
    * @param gp
    */
   protected void setOwnerGlassPane(Component gp) {
      
      //get the original glass pane
      showOriginalGlassPane = getOwnerGlassPane() != null && getOwnerGlassPane().isVisible();

      //install new glass pane at the MODEL layer of the layered pane
      if (getOwnerRootPane() != null) {
         gp.setSize(getOwnerRootPane().getSize());
         getOwnerRootPane().getLayeredPane().add(gp, JLayeredPane.MODAL_LAYER);
      }
   }
   
   /**
    * Get the rectangle dimensions of the owner's content area
    * @return
    */
   protected Rectangle getOwnerRect() {
      if (owner instanceof JWindow) {
         return ((JWindow) owner).getRootPane().getBounds();
      } else if (owner instanceof JFrame) {
         return ((JFrame) owner).getRootPane().getBounds();
      } else {
         return null;
      }
   }
   
   /**
    * get the location on screen of the owner window's content
    * @return
    */
   protected Point getOwnerLocation() {
      if (owner instanceof JWindow) {
         return ((JWindow) owner).getRootPane().getLocationOnScreen();
      } else if (owner instanceof JFrame) {
         return ((JFrame) owner).getRootPane().getLocationOnScreen();
      } else {
         return null;
      }
   }
   
   /**
    * Install the glasspane into the owner
    */
   protected void installGlassPane() {

      checkOwnerSpecified();
      
      glassPane = new GlassPane(null);
      setOwnerGlassPane(glassPane);
      glassPane.setVisible(true);
   }
   
   /**
    * Removes the existing content pane
    * so that the sheet will work with heavy weights in the content area
    * A window image is used for the glass pane now. Also resizing the owner
    * window is disabled if you use heavy weight support
    */
   protected void installHeavyContentPane() {
      
      checkOwnerSpecified();
      
      //get the original content pane
      orgOwnerContentPane = getOwnerRootPane().getContentPane();
      ownerIsResiable = getOwnerIsResizable();
      
      //turn off resizing
      if(owner instanceof JFrame) {
         ((JFrame)owner).setResizable(false);
      }
      
      //get the windows current image
      Rectangle rect = getOwnerRect();
      Point loc = getOwnerLocation();
      rect.setLocation(loc);
      BufferedImage windowImage = null;
      if(rect != null) {
         try {
            windowImage = new Robot().createScreenCapture(rect);
            windowImage = ImageUtilities.toCompatibleImage(windowImage, null);
         } catch (AWTException e) {
            e.printStackTrace();
         }
      }
      
      //remove the content pane temporarily
      JPanel tempContentPane = new JPanel();
      tempContentPane.setOpaque(true);
      getOwnerRootPane().setContentPane(tempContentPane);
      
      glassPane = new GlassPane(windowImage);
      setOwnerGlassPane(glassPane);
   }

   /**
    * uninstall the glass pane the sheet is placed on
    */
   protected void uninstallGlassPane() {
      
      //hide the glass pane
      if (glassPane != null) {
         glassPane.setVisible(false);
         getOwnerRootPane().getLayeredPane().remove(glassPane);
      }

      //show the original glass pane
      if (showOriginalGlassPane) {
         if(getOwnerGlassPane() != null) {
            getOwnerGlassPane().setVisible(showOriginalGlassPane);
         }
      }
   }
   
   /**
    * un-install the glass pane along with the heavy content pane replacement
    */
   protected void uninstallHeavyContentPane() {
      //uninstall glass pane
      uninstallGlassPane();
      
      //restore original content pane
      getOwnerRootPane().setContentPane(orgOwnerContentPane);
      
      if(owner instanceof JFrame) {
         ((JFrame)owner).setResizable(ownerIsResiable);
      }
      
   }
   
   /**
    * Get the fade glass pane percent. The default value is 10% or 0.1f
    * @return the fadeGlassPanePercent
    */
   public float getFadeGlassPanePercent() {
      return fadeGlassPanePercent;
   }

   /**
    * Set the fade glass pane percent. The percent is the level of transparency that the
    * frames content will be obscured by, where 1.0f would complete obscure the content 
    * and 0.0f would not obscured the content at all. The default value if not specified is 0.1f or 10%
    * @param fadeGlassPanePercent the fadeGlassPanePercent to set
    */
   public void setFadeGlassPanePercent(float fadeGlassPanePercent) {
      this.fadeGlassPanePercent = fadeGlassPanePercent;
   }

   /**
    * Get the paint used to overlay owner frame or window when fade glass pane
    * is set to true
    * 
    * @return the overlayPaint
    */
   public Paint getOverlayPaint() {
      return overlayPaint;
   }

   /**
    * Paint used to overlay owner frame or window when fade glass pane is set to
    * true
    * 
    * @param overlayPaint
    *           the overlayPaint to set
    */
   public void setOverlayPaint(Paint overlayPaint) {
      this.overlayPaint = overlayPaint;
   }

   /**
    * Returns true if frame or window contents are faded out during sheet reveal
    * Use overlaypaint to set the paint used for the fade out Use overlayPercent
    * to set the transparency level percent
    * 
    * @return the fadeGlassPane
    */
   public boolean getFadeGlassPane() {
      return fadeGlassPane;
   }

   /**
    * If set to true the frame or window contents are faded out during sheet
    * reveal Use overlaypaint to set the paint used for the fade out Use
    * overlayPercent to set the transparency level percent
    * 
    * @param fadeGlassPane
    *           the fadeGlassPane to set
    */
   public void setFadeGlassPane(boolean fadeGlassPane) {
      this.fadeGlassPane = fadeGlassPane;
   }

   /**
    * reveal a sheet on the specified window
    * 
    * @param window
    * @param listener
    */
   public void revealSheet(JWindow window, SheetPaneListener listener) {
      setOwner(window);
      revealSheet();
   }

   /**
    * reveal a sheet on the specified frame with a listener for call backs
    * 
    * @param frame
    * @param listener
    */
   public void revealSheet(JFrame frame, SheetPaneListener listener) {
      setOwner(frame);
      revealSheet(listener);
   }

   /**
    * This requires the owner to be already set before hand
    * 
    * @param listener
    */
   public void revealSheet(SheetPaneListener listener) {
      this.sheetPaneListener = listener;

      checkOwnerSpecified();

      if(useHeavyWeightSupport) {
         installHeavyContentPane();
      }
      else {
         installGlassPane();
      }
      
      playRevealAnimation();

   }

   /**
    * This requires the owner to be already set before hand
    */
   public void revealSheet() {
      revealSheet((SheetPaneListener) null);
   }

   /**
    * Hides the Sheet
    */
   public void hideSheet() {
      checkOwnerSpecified();

      playHideAnimation();
   }

   /**
    * reveals the sheet using an animation
    */
   protected void playRevealAnimation() {

      final int startX = (glassPane.getWidth() / 2) - (this.getWidth() / 2);
      final int startY = -this.getHeight();
      this.setVisible(false);
      glassPane.addSheetDialog(this);
      this.setLocation(startX, startY);
      this.setVisible(true);

      // animate sheet sliding in
      Animator anim = new Animator(500);
      anim.setDeceleration(0.5f);
      anim.addTarget(new TimingTargetAdapter() {

         public void timingEvent(float fraction) {
            int x = SheetDialog.this.getX();
            float deltaY = SheetDialog.this.getHeight() * fraction;
            SheetDialog.this.setLocation(x, (int) (startY + deltaY));
         }

      });

      // if we are fading the glassPane then add that for the Animation Target
      if (fadeGlassPane) {
         anim.addTarget(glassPane.createFadeInTarget());
      }
      anim.start();

   }

   /**
    * hides the sheet using an animation
    */
   protected void playHideAnimation() {

      // animate sheet sliding out
      Animator anim = new Animator(300);
      anim.setAcceleration(1.0f);
      anim.addTarget(new TimingTargetAdapter() {

         public void timingEvent(float fraction) {
            int x = SheetDialog.this.getX();
            float deltaY = -1 * (SheetDialog.this.getHeight() * fraction);
            SheetDialog.this.setLocation(x, (int) (deltaY));
         }

         public void end() {
            glassPane.removeSheetDialog(SheetDialog.this);
            if(useHeavyWeightSupport) {
               uninstallHeavyContentPane();
            }
            else {
               uninstallGlassPane();
            }
            
            notifySheetListener();
         }

      });
      // anim.addTarget(glassPane.createFadeOutTarget());
      anim.start();

   }

   /**
    * Notify the sheetListener that the sheet was disposed
    */
   protected void notifySheetListener() {
      if (sheetPaneListener != null) {
         sheetPaneListener.sheetPaneDisposed(this);
      }
   }

   // -----------------------------------------------------------

   /**
    * Interface used to be notified when sheet is disposed
    */
   public static interface SheetPaneListener {
      public void sheetPaneDisposed(SheetDialog pane);
   }

   // -----------------------------------------------------------

   /**
    * GlassPane used to block window
    */
   protected class GlassPane extends DecorationComponent {

      private TransparencyDecorator transparencyDecorator = new TransparencyDecorator();
      private JLayeredPane layeredPane = new JLayeredPane();
      private DecorationComponent transparentPanel;
      private BufferedImage backgroundImage;

      /**
       * Create the glasspane with an optional background image
       * @param image background image or null
       */
      public GlassPane(BufferedImage image) {
         
         backgroundImage = image;
         
         // want layered pane to take up the entire glassPane
         setLayout(new BorderLayout());
         add(layeredPane);

         // add empty mouse and keylistener to swallow events
         addMouseListener(new MouseAdapter() {
         });
         addKeyListener(new KeyAdapter() {
         });

         transparentPanel = new DecorationComponent() {
            @Override
            protected void paintComponent(Graphics g) {
               Graphics2D g2 = DrawUtilities.getGraphics2DWithAntiAliasing(g);
               g2.setPaint(overlayPaint);
               g2.fillRect(0, 0, getWidth(), getHeight());
            }
         };

         // add the transparency decorator
         transparentPanel.addPaintDecorator(transparencyDecorator);

         layeredPane.add(transparentPanel, JLayeredPane.DEFAULT_LAYER);
         
         //Listen for changes to this panels parent
         addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
            public void ancestorResized(HierarchyEvent e) {
               if(GlassPane.this.getParent() != null) {
                  Dimension parentSize = getOwnerRootPane().getSize();
                  if(fadeGlassPane) {
                     GlassPane.this.setSize(parentSize);
                     transparentPanel.setSize(parentSize);
                  }
                  
                  //move the sheet org.xito to the center
                  for(int i=0;i<layeredPane.getComponentCount();i++) {
                     if(layeredPane.getComponent(i) instanceof SheetDialog) {
                        int x = parentSize.width/2 - layeredPane.getComponent(i).getWidth()/2;
                        int y = layeredPane.getComponent(i).getLocation().y;
                        layeredPane.getComponent(i).setLocation(x,y);
                        break;
                     }
                  }
               }
            }
         });
      }

      /**
       * create the fade in target
       * @return
       */
      public FadeAnimationTarget createFadeInTarget() {
         transparentPanel.setSize(GlassPane.this.getSize());
         return transparencyDecorator.createFadeIn(transparentPanel, fadeGlassPanePercent);
      }

      /**
       * create the fade out target
       * @return
       */
      public FadeAnimationTarget createFadeOutTarget() {
         return transparencyDecorator.createFadeOut(transparentPanel, fadeGlassPanePercent);
      }

      /**
       * add the sheet to the layered pane of the glass pane
       * @param sheet
       */
      public void addSheetDialog(SheetDialog sheet) {
         layeredPane.add(sheet, JLayeredPane.MODAL_LAYER);
      }

      /**
       * remove the sheet org.xito from the glass pane
       * @param sheet
       */
      public void removeSheetDialog(SheetDialog sheet) {
         layeredPane.remove(sheet);
      }

      /**
       * get the glass panes layered pane
       * @return
       */
      public JLayeredPane getLayeredPane() {
         return layeredPane;
      }
      
      /**
       * paint the glass pane
       */
      public void paintComponent(Graphics g) {
         if(backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, null);
         }
      }

   }

   // -----------------------------------------------------------

   /**
    * Used by Dialog descriptors for checking in 
    */
   private class ConfirmSheetActionListener implements ActionListener {

      public void actionPerformed(ActionEvent pEvent) {

         // Check to see if the Dialog Panel is Valid
         // If not Valid then the org.xito will not close
         // it is up to the hasValidData method to display information
         // to the user that lets them know the content is not valid
         int type = -1;
         Integer value = (Integer) ((JComponent) pEvent.getSource()).getClientProperty(DialogManager.RESULT_KEY);
         if (value != null) {
            type = value.intValue();
         }

         JPanel p = dialogDescriptor.getCustomPanel();
         if (p != null && p instanceof Validatable && type != DialogManager.CANCEL) {
            if (((Validatable) p).hasValidData() == false) {
               return;
            }
         }

         dialogResult = type;
         hideSheet();
      }
   }
}
