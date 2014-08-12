package org.xito.dazzle.widget.toolbar;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.xito.dazzle.utilities.MacApplicationUtilities;
import org.xito.dazzle.widget.toolbar.Toolbar;

/**
 * ToolbarFrame
 */
public class ToolbarFrame extends JFrame {

   protected Toolbar toolbar;

   public ToolbarFrame() {
       this(true);
   }

   public ToolbarFrame(boolean useBrushMetalLook) {
      toolbar = new Toolbar();
      getContentPane().add(toolbar, BorderLayout.NORTH);

      if(useBrushMetalLook && MacApplicationUtilities.isAtLeastMacOSVersionX5()) {
         getRootPane().putClientProperty("apple.awt.brushMetalLook", true);
         ((Toolbar)toolbar).setShouldPaint(false);
      }
   }
   
   public Toolbar getToolbar() {
      return toolbar;
   }
}
