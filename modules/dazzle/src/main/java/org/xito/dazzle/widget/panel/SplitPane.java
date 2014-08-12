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

import org.xito.dazzle.utilities.DrawUtilities;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class SplitPane extends JSplitPane {

   public enum DividerStyle {
      THIN, THICK
   }


   /**
    * Default Serial Version ID
    */
   private static final long serialVersionUID = 1L;

   private DividerStyle dividerStyle = DividerStyle.THIN;

   public SplitPane(int orientation) {
      super(orientation);
      dividerStyle = DividerStyle.THIN;
      init();
   }

   public SplitPane(int orientation, DividerStyle dividerStyle) {
      super(orientation);
      this.dividerStyle = dividerStyle;
      init();
   }

   private void init() {

      //set border to 0
      setUI(new MySplitPaneUI());
      setBorder(BorderFactory.createEmptyBorder());
      if(dividerStyle == DividerStyle.THIN)
         setDividerSize(1);
      else {
         setDividerSize(10);
      }

      setContinuousLayout(true);
   }

   /**
    *  Divider UI
    */
   public class MySplitPaneUI extends BasicSplitPaneUI {

      BasicSplitPaneDivider divider = null;
      public BasicSplitPaneDivider createDefaultDivider() {
         if(SplitPane.this.dividerStyle == DividerStyle.THIN) {
             divider = new BasicSplitPaneDivider(this) {
               public void paint(Graphics g) {
                  g.setColor(new Color(175,175,175));
                  g.fillRect(0, 0, this.getWidth(), this.getHeight());
               }
            };
         }
         else if(SplitPane.this.dividerStyle == DividerStyle.THICK) {
            divider = new BasicSplitPaneDivider(this) {
               public void paint(Graphics g) {

                  Graphics2D g2 = DrawUtilities.getGraphics2DWithAntiAliasing(g);

                  if(SplitPane.this.getOrientation() == JSplitPane.VERTICAL_SPLIT) {

                     g2.setPaint(new GradientPaint(0,0, new Color(252,252,252), 0, getHeight()-1, new Color(225,225,225)));
                     g2.fillRect(0, 0, getWidth()-1, getHeight()-1);

                     g2.setColor(new Color(175,175,175));
                     g2.drawLine(0, 0, this.getWidth()-1, 0);
                     g2.drawLine(0, this.getHeight()-1, this.getWidth()-1, this.getHeight()-1);
                  }
                  else {
                     g2.setPaint(new GradientPaint(0,0, new Color(252,252,252), getWidth()-1, 0, new Color(225,225,225)));
                     g2.fillRect(0, 0, getWidth()-1, getHeight()-1);

                     g2.setColor(new Color(175,175,175));
                     g2.drawLine(0, 0, 0, getHeight()-1);
                     g2.drawLine(getWidth()-1, 0, this.getWidth()-1, this.getHeight()-1);
                  }

                  int centerX = getWidth()/2;
                  int centerY = getHeight()/2;

                  g2.setColor(new Color(225,225,225));
                  g2.fillOval(centerX-2, centerY-2, 4, 4);
                  g2.setColor(new Color(175,175,175));
                  g2.drawOval(centerX-2, centerY-2, 4, 4);

               }
            };
         }

         return divider;
      }
   }
}
