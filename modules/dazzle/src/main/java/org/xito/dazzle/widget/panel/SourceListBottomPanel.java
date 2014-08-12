package org.xito.dazzle.widget.panel;

import org.xito.dazzle.utilities.DrawUtilities;

import javax.swing.*;
import java.awt.*;

/**
 * SourceListBottomPanel
 *
 * @author drichan
 */
public class SourceListBottomPanel extends JComponent {

   private SplitPaneControl splitCtrl;

   public SourceListBottomPanel() {
      setPreferredSize(new Dimension(30, 23));
      setLayout(new BorderLayout());
      splitCtrl = new SplitPaneControl();
      add(splitCtrl, BorderLayout.EAST);
   }

   public void setSplitPane(JSplitPane splitPane) {
      splitCtrl.setSplitPane(splitPane);
   }

   public void paintComponent(Graphics g) {

      Graphics2D g2 = DrawUtilities.getGraphics2DWithAntiAliasing(g);
      g2.setColor(new Color(251,251,251));
      g2.fillRect(0,0, getWidth(), getHeight()-1);
      g2.setColor(new Color(235,235,235));
      g2.fillRect(0,getHeight()/2, getWidth(), getHeight()-1);

      //line across top
      g2.setColor(new Color(213,213,213));
      g2.drawLine(0,0,getWidth(), 0);

   }

}
