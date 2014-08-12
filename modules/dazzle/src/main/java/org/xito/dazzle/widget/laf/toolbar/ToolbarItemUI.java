package org.xito.dazzle.widget.laf.toolbar;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIDefaults;

import org.xito.dazzle.widget.DefaultStyle;
import org.xito.dazzle.widget.laf.DecorationComponentUI;
import org.xito.dazzle.widget.toolbar.ToolbarItem;


public class ToolbarItemUI extends DecorationComponentUI {

   protected UIDefaults styleMap;
   protected Font ctrlFont;
   
   public ToolbarItemUI(UIDefaults styleMap) {
      this.styleMap = styleMap;
      ctrlFont = styleMap.getFont(DefaultStyle.CTRL_TITLE_FONT_KEY);
   }

   public Rectangle2D getTitleBounds(ToolbarItem item) {

      BufferedImage bufImg = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = (Graphics2D) bufImg.getGraphics();
      g2.setFont(ctrlFont);
      FontRenderContext frc = g2.getFontRenderContext();

      return ctrlFont.getStringBounds(item.getText(), frc);
   }
   
   /* (non-Javadoc)
    * @see org.xito.dazzle.widget.laf.DecorationComponentUI#paint(java.awt.Graphics, javax.swing.JComponent)
    */
   @Override
   public void paint(Graphics g, JComponent c) {
      super.paint(g, c);
      
      ToolbarItem tbi = (ToolbarItem)c;
      
      int x = c.getWidth() / 2 - (int) getTitleBounds(tbi).getWidth() / 2;
      int y = c.getHeight() - ToolbarItem.PADDING;

      // Draw Title
      g.setFont(ctrlFont);
      if (!tbi.isPressed()) {
         g.setColor(styleMap.getColor(DefaultStyle.CTRL_TITLE_HIGHLIGHT_COLOR_KEY));
         g.drawString(tbi.getText(), x, y);
      }

      g.setColor(styleMap.getColor(DefaultStyle.CTRL_TITLE_COLOR_KEY));
      g.drawString(tbi.getText(), x, y - 1);

      // DrawIcon
      y = ToolbarItem.PADDING;
      ImageIcon icon = tbi.getLargeIcon();
      if (icon != null) {
         x = tbi.getWidth() / 2 - icon.getIconWidth() / 2;
         g.drawImage(icon.getImage(), x, y, null);
      }
   }
   
   
   
}
