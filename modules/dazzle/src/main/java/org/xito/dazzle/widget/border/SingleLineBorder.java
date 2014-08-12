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

package org.xito.dazzle.widget.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;

/**
 * Border that uses a single line on the side of a component
 * 
 * @author Deane Richan
 */
public class SingleLineBorder extends AbstractBorder {

   protected int location;
   protected Color borderColor;
   protected int width;
   
   public static final int NORTH = SwingConstants.NORTH;
   public static final int SOUTH = SwingConstants.SOUTH;
   public static final int WEST = SwingConstants.WEST;
   public static final int EAST = SwingConstants.EAST;
   
   public SingleLineBorder(int location, Color color, int width) {
      this.location = location;
      this.width = width;
      this.borderColor = color;
   }
   
   public Insets getBorderInsets(Component comp) {
      Insets ins = new Insets(0,0,0,0);
      
      switch(location) {
         case NORTH: ins.top = width; break;
         case SOUTH: ins.bottom = width; break;
         case WEST:  ins.left = width; break;
         case EAST:  ins.right = width; break;
      }

      return ins;
   }

   public boolean isBorderOpaque() {
      return true;
   }

   public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h) {
      g.setColor(borderColor);
      switch(location) {
         case NORTH: g.fillRect(0, 0, w, width); break;
         case SOUTH: g.fillRect(0, h-width, w, h); break;
         case WEST:  g.fillRect(0, 0, width, h); break;
         case EAST:  g.fillRect(w-width, 0, w, h); break;
      }
   }

}
