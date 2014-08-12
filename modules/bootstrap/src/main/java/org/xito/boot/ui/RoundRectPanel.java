package org.xito.boot.ui;

import javax.swing.*;
import java.awt.*;

/**
 * RoundRectPanel
 *
 * @author: drichan
 */
public class RoundRectPanel extends JPanel {

   public void paintComponent(Graphics g) {

      Graphics2D g2 = (Graphics2D)g;

      g2.setColor(new Color(240,240,240));
      g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

      g2.setColor(new Color(230,230,230));
      g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
   }

}
