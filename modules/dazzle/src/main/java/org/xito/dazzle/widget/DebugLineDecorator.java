package org.xito.dazzle.widget;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

public class DebugLineDecorator implements PaintDecorator {

   protected Color color;
   
   /**
    * @return the color
    */
   public Color getColor() {
      return color;
   }

   /**
    * @param color the color to set
    */
   public void setColor(Color color) {
      this.color = color;
   }

   public void postPaintComponent(Graphics g, JComponent comp) {
      if(color == null) 
         g.setColor(Color.RED);
      else
         g.setColor(color);
      
      g.drawRect(0, 0, comp.getWidth()-1, comp.getHeight()-1);
      g.drawLine(0, 0, comp.getWidth()-1, comp.getHeight()-1);
      g.drawLine(comp.getWidth()-1, 0, 0, comp.getHeight()-1);
   }

   public void prePaintComponent(Graphics g, JComponent comp) {
   
   }

}
