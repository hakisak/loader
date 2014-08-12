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

import org.xito.dazzle.widget.DefaultStyle;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

/**
 * Provides a ||| Split Control to control SplitPanes,
 * 
 * @author Deane Richan
 */
public class SplitPaneControl extends JComponent {

   /**
    * Default Serial Version ID
    */
   private static final long serialVersionUID = 1L;

   private JSplitPane splitPane;

   private SplitControlMouseAdapter splitListener;

   private UIDefaults styleMap;

   public SplitPaneControl() {
      this(null);      
   }

   public SplitPaneControl(JSplitPane splitPane) {

      initStyle();

      this.setOpaque(false);

      // Setup listeners
      splitListener = new SplitControlMouseAdapter(splitPane);
      addMouseListener(splitListener);
      addMouseMotionListener(splitListener);

      // default size of a split control
      setPreferredSize(new Dimension(12, 16));

      setSize(getPreferredSize());
      setSplitPane(splitPane);
   }

   private void initStyle() {
      if (styleMap == null) {
         styleMap = DefaultStyle.getDefaults();
      }
   }

   public void setSplitPane(JSplitPane splitPane) {

      this.splitPane = splitPane;
      this.splitListener.setSplitPane(splitPane);

      if (splitPane == null) {
         setVisible(false);
         return;
      } else {
         setVisible(true);
      }

      // horizontal split
      if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
         setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
      }
      // vertical split
      else {
         setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
      }
   }

   public JSplitPane getSplitPane() {
      return splitPane;
   }

   public void paintComponent(Graphics g) {

      if (splitPane == null || splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
         paintComponentHorizontal(g);
      } else {
         paintComponentVertical(g);
      }
   }

   public void paintComponentHorizontal(Graphics g) {

      // Draw Vertical Lines
      Graphics2D g2 = (Graphics2D) g;

      int x1 = 0;
      int x2 = 3;
      int x3 = 6;

      int y = 7;
      int h = getHeight() - y;

      // Draw split lines
      g2.setColor(styleMap.getColor(DefaultStyle.CTRL_BORDER_COLOR_KEY));
      g2.drawLine(x1, y, x1, h);
      g2.drawLine(x2, y, x2, h);
      g2.drawLine(x3, y, x3, h);

      // Draw split lines highlight
      g2.setColor(DefaultStyle.CTRL_BEVEL_HIGHLIGHT);
      g2.drawLine(x1 + 1, y, x1 + 1, h);
      g2.drawLine(x2 + 1, y, x2 + 1, h);
      g2.drawLine(x3 + 1, y, x3 + 1, h);
   }

   public void paintComponentVertical(Graphics g) {

      // Draw Horizontal Lines
      Graphics2D g2 = (Graphics2D) g;

      // expect 16px high component if larger then add padding
      int y = 4;
      int padding = 0;
      if (getHeight() - 16 > 0)
         padding = (getHeight() - 16) / 2;

      int y1 = y + padding;
      int y2 = y * 2 + padding;
      int y3 = y * 3 + padding;

      int x = 5;
      int w = getWidth() - x;

      g2.setColor(styleMap.getColor(DefaultStyle.CTRL_BORDER_COLOR_KEY));
      g2.drawLine(x, y1, w, y1);
      g2.drawLine(x, y2, w, y2);
      g2.drawLine(x, y3, w, y3);

      g2.setColor(DefaultStyle.CTRL_BEVEL_HIGHLIGHT);
      g2.drawLine(x, y1 + 1, w, y1 + 1);
      g2.drawLine(x, y2 + 1, w, y2 + 1);
      g2.drawLine(x, y3 + 1, w, y3 + 1);
   }

   // ---------------------------------------------------------

   /**
    * 
    */
   public static class SplitControlMouseAdapter extends MouseAdapter implements MouseMotionListener {

      private int startX;

      private int startSplitX;

      private int startY;

      private int startSplitY;

      private JSplitPane splitPane;

      public SplitControlMouseAdapter(JSplitPane splitPane) {
         setSplitPane(splitPane);
      }

      public void setSplitPane(JSplitPane splitPane) {
         this.splitPane = splitPane;
      }

      public void mouseDragged(MouseEvent e) {

         // if no split just return
         if (splitPane == null) {
            return;
         }

         int minLoc;
         int maxLoc;
         int newLoc;

         if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            Dimension minSize;
            Dimension minSize2;

            minSize = splitPane.getLeftComponent().getMinimumSize();
            minSize2 = splitPane.getRightComponent().getMinimumSize();

            minLoc = minSize.width;
            maxLoc = splitPane.getWidth() - minSize2.width;

            int x = e.getComponent().getLocationOnScreen().x + e.getPoint().x;
            int deltaX = x - startX;
            newLoc = startSplitX + deltaX;
         } else {
            Dimension minSize;
            Dimension minSize2;

            minSize = splitPane.getTopComponent().getMinimumSize();
            minSize2 = splitPane.getBottomComponent().getMinimumSize();

            minLoc = minSize.height;
            maxLoc = splitPane.getHeight() - minSize2.height;

            int y = e.getComponent().getLocationOnScreen().y + e.getPoint().y;
            int deltaY = y - startY;
            newLoc = startSplitY + deltaY;
         }

         // Make sure we leave the required room for the "max" component.
         if (newLoc > maxLoc) {
            newLoc = maxLoc;
         }

         // Important Note: By checking the min last, we ensure that we
         // honor the min setting over the max setting when there is not
         // room enough for both. This avoids odd jumping behavior.

         // Make sure we leave the required room for the "min" component.
         if (newLoc < minLoc) {
            newLoc = minLoc;
         }

         splitPane.setDividerLocation(newLoc);
      }

      // Not Used
      public void mouseMoved(MouseEvent e) {

      }

      public void mousePressed(MouseEvent e) {

         // if no split just return
         if (splitPane == null) {
            return;
         }

         if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {

            startX = e.getComponent().getLocationOnScreen().x + e.getPoint().x;
            startSplitX = splitPane.getDividerLocation();
         } else {
            startY = e.getComponent().getLocationOnScreen().y + e.getPoint().y;
            startSplitY = splitPane.getDividerLocation();
         }
      }

      public void mouseReleased(MouseEvent e) {

         // if no split just return
         if (splitPane == null) {
            return;
         }

         startX = 0;
         startY = 0;
      }
   }
}
