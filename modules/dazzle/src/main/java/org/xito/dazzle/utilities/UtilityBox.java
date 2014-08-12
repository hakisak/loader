package org.xito.dazzle.utilities;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;

/**
 * Utility box class used to create colored boxes of preferred sizes. Used for UI tests
 * @author deane
 */
public class UtilityBox extends JComponent {
   
   public UtilityBox(Color color, int preferredWidth, int preferredHeight) {
      setOpaque(true);
      setBackground(color);
      setPreferredSize(new Dimension(preferredWidth, preferredHeight));
   }
}
