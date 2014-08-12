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

package org.xito.dazzle.widget.panel;

import java.awt.*;
import javax.swing.*;

import org.xito.dazzle.widget.DecorationComponent;

/**
 * Panel that uses a Gradient Background. The gradient can flow from the 8 points of the compass.
 * 
 * @author Deane Richan
 */
public class GradientPanel extends DecorationComponent {
   
   protected Color color1;
   protected Color color2;
   protected float offsetRatio;
   protected int direction = SwingConstants.EAST;
   
   /**
    * Used by subclasses
    */
   protected GradientPanel() {

   }

   /** 
    * Creates a new instance of GradientPanel 
    * @param c1 first color
    * @param c2 second color
    * @param offsetRatio value between 0 - 1.0f where 0 is closest to c1, and 1.0 is closest to c2
    * @param dir of gradient SwingConstants NORTH, SOUTH, EAST, WEST, NW, NE, SE, SW 
    */
   public GradientPanel(Color c1, Color c2, float offsetRatio, int dir) {
      initGradientValues(c1, c2, offsetRatio, dir);
   }

   protected void initGradientValues(Color c1, Color c2, float offsetRatio, int dir) {
      if(offsetRatio == 0) offsetRatio = 0.5f;
      this.offsetRatio = offsetRatio;
      this.color1 = c1;
      this.color2 = c2;
      this.direction = dir;
   }
   
   /**
    * Paint this GradientPanel
    */
   public void paintComponent(Graphics g) {
      
      Graphics2D g2D = (Graphics2D)g;
                  
      //Now paint my gradiant
      GradientPaint gp = new GradientPaint(getX1(),getY1(), color1, getX2(),getY2(), color2);
      g2D.setPaint(gp);
      g2D.fillRect(0,0, getWidth(), getHeight());
   }
   
   private int getX1() {
      int offsetX = (int)(getWidth() * offsetRatio);
            
      if(direction == SwingConstants.EAST || 
            direction == SwingConstants.NORTH_EAST || 
            direction == SwingConstants.SOUTH_EAST) 
         return getWidth()-offsetX;
      
      else if(direction == SwingConstants.WEST || 
            direction == SwingConstants.NORTH_WEST || 
            direction == SwingConstants.SOUTH_WEST) 
         return offsetX;
      else 
         return 0;
   }
   
   private int getX2() {
      
      if(direction == SwingConstants.EAST || 
            direction == SwingConstants.NORTH_EAST || 
            direction == SwingConstants.SOUTH_EAST) 
         return getWidth();
      else 
         return 0;
   }
   
   private int getY1() {
      int offsetY = (int)(getHeight() * offsetRatio);
      
      if(direction == SwingConstants.NORTH || 
            direction == SwingConstants.NORTH_WEST || 
            direction == SwingConstants.NORTH_EAST) 
         return offsetY;
      
      else if(direction == SwingConstants.SOUTH || 
            direction == SwingConstants.SOUTH_WEST || 
            direction == SwingConstants.SOUTH_EAST) 
         return getHeight()-offsetY;
      else return 0;
   }
   
   private int getY2() {
      
      if(direction == SwingConstants.SOUTH || direction == SwingConstants.SOUTH_WEST || direction == SwingConstants.SOUTH_EAST) 
         return getHeight();
      else 
         return 0;
   }
   
}
