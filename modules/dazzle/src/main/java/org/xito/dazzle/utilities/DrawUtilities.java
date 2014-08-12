package org.xito.dazzle.utilities;

import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

public class DrawUtilities {

   /**
    * Paints Red lines on the borders of this component
    * 
    * @param g
    * @param c
    */
   public static void paintDebugLines(Graphics g, Component c) {
      g.setColor(Color.RED);
      g.drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
   }

   /**
    * Add Anti-Aliasing to the given Graphics Context
    * 
    * @param g2
    */
   public static void addAntiAliasing(Graphics2D g2) {
      g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
   }

   /**
    * Get the Font Metrics for a give font and sample text
    * @param f font
    * @return font metrics
    */
   public static FontMetrics getFontMetrics(Font f, String sampleText) {
      VolatileImage img = ImageUtilities.createCompaibleVolatileImage(10,10, Transparency.OPAQUE);
      return img.getGraphics().getFontMetrics(f);
   }

   /**
    * Get a Strings bounded rectangle for a given font
    * @param f
    * @param sampleText
    * @return
    */
   public static Rectangle2D getStringBounds(Font f, String sampleText) {
      VolatileImage img = ImageUtilities.createCompaibleVolatileImage(10,10, Transparency.OPAQUE);
      Graphics2D g2 = (Graphics2D)img.getGraphics();

      return f.getStringBounds(sampleText, g2.getFontRenderContext());
   }

   /**
    * Cast a Graphics object into a Graphics2D with anti aliasing turned on
    * 
    * @param g
    * @return
    */
   public static Graphics2D getGraphics2DWithAntiAliasing(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      addAntiAliasing(g2);

      return g2;
   }
   
   /**
    * Draws a string center on the X, Y location
    * @param g2
    * @param text
    * @param centerX
    * @param centerY
    */
   public static void drawStringCenteredOnPoint(Graphics2D g2, String text, int centerX, int centerY) {
    
      FontMetrics fm = g2.getFontMetrics();
      Rectangle2D bounds = fm.getStringBounds(text, g2);

      int x = (int)(centerX - bounds.getWidth()/2);
      int y = (int)(centerY + bounds.getHeight()/2);
      g2.drawString(text, x, y);
   }

}
