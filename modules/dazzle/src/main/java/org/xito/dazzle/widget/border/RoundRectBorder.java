package org.xito.dazzle.widget.border;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.border.Border;

public class RoundRectBorder implements Border {

   protected Color color;
   protected int radius;
   protected int padding = 3;
   protected Stroke stroke = new BasicStroke(2);
   
   public RoundRectBorder(Color color, int radius) {
      this.color = color;
      this.radius = radius;
   }
   
   public RoundRectBorder(Color color, int radius, int padding) {
      this(color, radius);
      this.padding = padding;
   }
   
   /* (non-Javadoc)
    * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
    */
   public Insets getBorderInsets(Component comp) {
      
      int value = radius/2 + padding;
      return new Insets(value, value, value, value);
   }

   /* (non-Javadoc)
    * @see javax.swing.border.Border#isBorderOpaque()
    */
   public boolean isBorderOpaque() {
      return false;
   }

   /* (non-Javadoc)
    * @see javax.swing.border.Border#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
    */
   public void paintBorder(Component comp, Graphics g, int x, int y, int width, int height) {
      
      int arc = radius * 2;
      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      g.setColor(color);
      if(stroke != null) {
         g2.setStroke(stroke);
      }
      
      g.drawRoundRect(x + padding , y + padding , width-(1+padding*2), height-(1+padding*2), arc, arc);
   }
   
   
   

}
