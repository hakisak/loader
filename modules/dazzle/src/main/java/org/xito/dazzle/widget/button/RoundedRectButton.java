/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.widget.button;

import java.awt.BorderLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.Box;

import org.xito.dazzle.utilities.UIUtilities;

/**
 *
 * @author deane
 */
public class RoundedRectButton {
  /* 
   public RoundedRectButton(String text) {
      super();
      
      //want to set the text after we install the correct component
      setComponent(new RoundedRectButtonComponent());
      setText(text);
   }
   
   //-------------------------------------------------------------------------
   protected class RoundedRectButtonComponent extends BasicButtonComponent {

      protected Paint bgPaint;
      
      @Override
      protected void rebuildLayout() {
         removeAll();
         add(Box.createVerticalStrut(textInsets.top), BorderLayout.NORTH);
         add(Box.createVerticalStrut(textInsets.bottom), BorderLayout.SOUTH);
         add(Box.createHorizontalStrut(textInsets.left), BorderLayout.EAST);
         add(Box.createHorizontalStrut(textInsets.right), BorderLayout.WEST);
         add(textLbl, BorderLayout.CENTER);
         
         bgPaint = new GradientPaint(0, 0, BG_TOP_COLOR, 0, getHeight(), BG_BOTTOM_COLOR);
         repaint();
      }

      @Override
      public void paintComponent(Graphics g) {
         
         Graphics2D g2 = UIUtilities.getGraphics2DWithAntiAliasing(g);
         
         if(pressed_flag) {
            paintPressed(g2);
         }
         else {
            g2.setPaint(bgPaint);
            int arc = getHeight();
            g2.fillRoundRect(0,0, getWidth()-1, getHeight()-1, arc, arc);
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);
         }
      }

      @Override
      protected void paintPressed(Graphics2D g2) {
         g2.setPaint(BG_PAINT_PRESSED);
         int arc = getHeight();
         g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);
         g2.setColor(BORDER_COLOR);
         g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);
      }
   
      
   }
*/
}
