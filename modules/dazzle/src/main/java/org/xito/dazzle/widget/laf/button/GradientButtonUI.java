package org.xito.dazzle.widget.laf.button;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicButtonUI;

import org.xito.dazzle.utilities.DrawUtilities;
import org.xito.dazzle.utilities.UIUtilities;
import org.xito.dazzle.widget.button.BasicButton;

public class GradientButtonUI extends StyleButtonUI {

   protected Color TEXT_COLOR = Color.BLACK;
   protected Color BORDER_COLOR = new Color(150,150,150);
   protected Color BG_TOP_COLOR = new Color(253, 253, 253);
   protected Color BG_BOTTOM_COLOR = new Color(245, 245, 245);
   protected Color BG_PAINT_PRESSED = new Color(190,190,190);
   
   protected Font textFont;
   
   public GradientButtonUI() {
      textFont = new JLabel().getFont();
   }
   
   /* (non-Javadoc)
    * @see javax.swing.plaf.basic.BasicButtonUI#installDefaults(javax.swing.AbstractButton)
    */
   @Override
   protected void installDefaults(AbstractButton b) {
      // TODO Auto-generated method stub
      //super.installDefaults(b);
      b.setMargin(new InsetsUIResource(0,2,0,2));
      b.setBorder(new EmptyBorder(8,11,8,11));
      
   }

   /* (non-Javadoc)
    * @see javax.swing.plaf.basic.BasicButtonUI#getPreferredSize(javax.swing.JComponent)
    */
//   @Override
//   public Dimension getPreferredSize(JComponent c) {
//      System.out.println(c.getInsets());
//      AbstractButton b = (AbstractButton)c;
//      Dimension ps = new JLabel(b.getText()).getPreferredSize();
//      ps.width = ps.width + 10;
//      //b.getInsets();
//      return ps;
//   }

   /* (non-Javadoc)
    * @see javax.swing.plaf.basic.BasicButtonUI#paint(java.awt.Graphics, javax.swing.JComponent)
    */
   @Override
   public void paint(Graphics g, JComponent c) {
      
      Graphics2D g2 = DrawUtilities.getGraphics2DWithAntiAliasing(g);
      BasicButton button = (BasicButton)c;
      
      Insets insets = button.getInsets();
      
      g2.setColor(Color.RED);
      g2.fillRect(0,0,c.getWidth(), c.getHeight());
      
      int x = insets.left/2 - 1;
      int y = insets.top/2;
      int w = button.getWidth() - x*2 - 1;
      int h = button.getHeight()/2 - insets.top/2;
      
      g2.setPaint(BG_TOP_COLOR);
      g2.fillRect(x, y, w, h);
      
      y = button.getHeight()/2;
      h = button.getHeight()/2 - insets.top/2;
      
      g2.setPaint(BG_BOTTOM_COLOR);
      g2.fillRect(x, y, w, h);
      
      x = insets.left/2 - 1;
      y = insets.top/2;
      h = button.getHeight() - insets.bottom -1;
      
      g2.setColor(BORDER_COLOR);
      g2.drawRect(x, y, w, h);
      
      // TODO Auto-generated method stub
      super.paint(g, c);
   }

   /* (non-Javadoc)
    * @see javax.swing.plaf.basic.BasicButtonUI#paintButtonPressed(java.awt.Graphics, javax.swing.AbstractButton)
    */
   @Override
   protected void paintButtonPressed(Graphics g, AbstractButton b) {
      Graphics2D g2 = DrawUtilities.getGraphics2DWithAntiAliasing(g);
      
      g2.setPaint(BG_PAINT_PRESSED);
      g2.fillRect(0, 0, b.getWidth(), b.getHeight());
      g2.setColor(BORDER_COLOR);
      g2.drawRect(0, 0, b.getWidth()-1, b.getHeight()-1);
      
      super.paintButtonPressed(g, b);
   }

   /* (non-Javadoc)
    * @see javax.swing.plaf.basic.BasicButtonUI#paintFocus(java.awt.Graphics, javax.swing.AbstractButton, java.awt.Rectangle, java.awt.Rectangle, java.awt.Rectangle)
    */
   @Override
   protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
      // TODO Auto-generated method stub
      super.paintFocus(g, b, viewRect, textRect, iconRect);
   }


   /* (non-Javadoc)
    * @see javax.swing.plaf.basic.BasicButtonUI#paintIcon(java.awt.Graphics, javax.swing.JComponent, java.awt.Rectangle)
    */
   @Override
   protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
      // TODO Auto-generated method stub
      super.paintIcon(g, c, iconRect);
   }

   /* (non-Javadoc)
    * @see javax.swing.plaf.basic.BasicButtonUI#paintText(java.awt.Graphics, javax.swing.JComponent, java.awt.Rectangle, java.lang.String)
    */
   @Override
   protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
      // TODO Auto-generated method stub
      super.paintText(g, c, textRect, text);
   }

/*
   @Override
   public void paint(Graphics g, JComponent c) {
      
      StyleButton button = (StyleButton)c;
      Graphics2D g2 = UIUtilities.getGraphics2DWithAntiAliasing(g);
      boolean pressed = button.getModel().isPressed();
      
      if(pressed) {
         paintPressed(g2, button);
      }
      else {
         g2.setPaint(BG_TOP_COLOR);
         g2.fillRect(0,0, button.getWidth(), button.getHeight()/2);
         g2.setPaint(BG_BOTTOM_COLOR);
         g2.fillRect(0, button.getHeight()/2, button.getWidth(), button.getHeight());
         g2.setColor(BORDER_COLOR);
         g2.drawRect(0,0, button.getWidth()-1, button.getHeight()-1);
         
         paintText(g2, TEXT_COLOR, button);
      }
   }
   
   protected void paintPressed(Graphics2D g2, StyleButton button) {
      
      g2.setPaint(BG_PAINT_PRESSED);
      g2.fillRect(0, 0, button.getWidth(), button.getHeight());
      g2.setColor(BORDER_COLOR);
      g2.drawRect(0, 0, button.getWidth()-1, button.getHeight()-1);
      
      paintText(g2, TEXT_COLOR, button);
   }
   
   protected void paintText(Graphics2D g2, Color color, StyleButton button) {
      g2.setPaint(TEXT_COLOR);
      FontMetrics fm = g2.getFontMetrics(textFont);
      Rectangle2D textBounds = fm.getStringBounds(button.getText(), g2);
      
      //center the text
      int bw = button.getWidth();
      int bh = button.getHeight();
      int x = (int)(bw - textBounds.getWidth())/2;
      int y = (int)(textBounds.getHeight());
      g2.drawString(button.getText(), x, y);
   }
   
   */
}
 
