package org.xito.dazzle.widget.laf.toolbar;

import java.awt.Color;
import java.awt.Graphics;

//import javax.media.jai.operator.AddDescriptor;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.plaf.ComponentUI;

import org.xito.dazzle.widget.DefaultStyle;
import org.xito.dazzle.widget.GradientFillDecorator;
import org.xito.dazzle.widget.laf.DecorationComponentUI;
import org.xito.dazzle.widget.toolbar.Toolbar;

public class ToolbarUI extends DecorationComponentUI {

   private UIDefaults styleMap;
   
   public ToolbarUI() {

      // setup colors
      styleMap = DefaultStyle.getDefaults();

      // top panel
      Color c1 = styleMap.getColor(DefaultStyle.CTRL_TOP_GRADIENT_COLOR_KEY);
      Color c2 = styleMap.getColor(DefaultStyle.CTRL_BOTTOM_GRADIENT_COLOR_KEY);
      float offsetRatio = 1.0f;
      int dir = SwingUtilities.SOUTH;

      addPaintDecorator(new GradientFillDecorator(c1, c2, offsetRatio, dir));
   }
   
   @Override
   public void paint(Graphics g, JComponent c) {
      Toolbar toolbar = (Toolbar)c;
      if (toolbar.shouldPaint()) {
         super.paint(g, c);
      }
      
      g.setColor(styleMap.getColor(DefaultStyle.CTRL_BORDER_COLOR_KEY));
      g.drawLine(0, c.getHeight() - 1, c.getWidth() - 1, c.getHeight() - 1);
   }

}
