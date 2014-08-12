package org.xito.dazzle.widget.panel;

import java.awt.Graphics;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

public class RoundRectPanel extends JPanel {
   
   protected int padding;
   protected int radius;
   
   public RoundRectPanel(int radius, int padding) {
      this.radius = radius;
      this.padding = padding;
   }
   
   public void paintComponent(Graphics g) {
      
      float arc = radius * 2;
      RoundRectangle2D.Float shape = new RoundRectangle2D.Float(padding, padding, 
            getWidth()-(1+padding*2), getHeight()-(1+padding*2), 
            arc, arc);
      
      g.setClip(shape);
      
      super.paintComponent(g);
      
   }
   

}
