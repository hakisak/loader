package org.xito.dazzle.widget;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

public class GradientFillDecorator implements PaintDecorator {

   protected Color color1;
   protected Color color2;
   protected float offsetRatio;
   protected int direction = SwingConstants.EAST;
   
   public GradientFillDecorator(Color c1, Color c2, float offsetRatio, int dir) {
      
      if(offsetRatio == 0) offsetRatio = 0.5f;
      this.offsetRatio = offsetRatio;
      this.color1 = c1;
      this.color2 = c2;
      this.direction = dir;
   }
   
   /* (non-Javadoc)
    * @see org.xito.dazzle.widget.PaintDecorator#postPaintComponent(java.awt.Graphics, javax.swing.JComponent)
    */
   public void postPaintComponent(Graphics g, JComponent comp) {
      // TODO Auto-generated method stub
   }
   
   public void prePaintComponent(Graphics g, JComponent comp) {
      
      Graphics2D g2D = (Graphics2D)g;
                  
      //Now paint my gradiant
      GradientPaint gp = new GradientPaint(getX1(comp), getY1(comp), color1, getX2(comp), getY2(comp), color2);
      g2D.setPaint(gp);
      g2D.fillRect(0, 0, comp.getWidth(), comp.getHeight());
   }
   
   private int getX1(JComponent comp) {
      
      int offsetX = (int)(comp.getWidth() * offsetRatio);
            
      if(direction == SwingConstants.EAST || 
            direction == SwingConstants.NORTH_EAST || 
            direction == SwingConstants.SOUTH_EAST) 
         return comp.getWidth()-offsetX;
      
      else if(direction == SwingConstants.WEST || 
            direction == SwingConstants.NORTH_WEST || 
            direction == SwingConstants.SOUTH_WEST) 
         return offsetX;
      else 
         return 0;
   }
   
   private int getX2(JComponent comp) {
      
      if(direction == SwingConstants.EAST || 
            direction == SwingConstants.NORTH_EAST || 
            direction == SwingConstants.SOUTH_EAST) 
         return comp.getWidth();
      else 
         return 0;
   }
   
   private int getY1(JComponent comp) {
      
      int offsetY = (int)(comp.getHeight() * offsetRatio);
      
      if(direction == SwingConstants.NORTH || 
            direction == SwingConstants.NORTH_WEST || 
            direction == SwingConstants.NORTH_EAST) 
         return offsetY;
      
      else if(direction == SwingConstants.SOUTH || 
            direction == SwingConstants.SOUTH_WEST || 
            direction == SwingConstants.SOUTH_EAST) 
         return comp.getHeight()-offsetY;
      else return 0;
   }
   
   private int getY2(JComponent comp) {
      
      if(direction == SwingConstants.SOUTH || 
            direction == SwingConstants.SOUTH_WEST || 
            direction == SwingConstants.SOUTH_EAST) 
         return comp.getHeight();
      else 
         return 0;
   }
      
   
}
