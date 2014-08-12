package org.xito.dazzle.widget.laf.toolbar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import org.xito.dazzle.utilities.DrawUtilities;
import org.xito.dazzle.widget.laf.DecorationComponentUI;
import org.xito.dazzle.widget.toolbar.ToolbarItem;

public class ToolbarItemSeparatorUI extends DecorationComponentUI {

   @Override
   public void paint(Graphics g, JComponent c) {
      
      Graphics2D g2 = DrawUtilities.getGraphics2DWithAntiAliasing(g);
      g2.setColor(new Color(50, 50, 50, 200));
      g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { .05f, 3 }, 0));

      int x = c.getWidth() / 2;
      int y = ToolbarItem.PADDING;
      g2.drawLine(x, y, x, c.getHeight() - 8);
   
   }
}
