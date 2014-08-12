package org.xito.dazzle.widget.table;

import org.xito.dazzle.widget.DefaultStyle;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ScrollTableCellRenderer extends DefaultTableCellRenderer {

   protected Font rendererFont;

   public void setRendererFont(Font f) {
      rendererFont = f;
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      JComponent comp = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (rendererFont != null) comp.setFont(rendererFont);

      if (row % 2 == 0) {
         comp.setBackground(DefaultStyle.TABLE_EVEN_ROW);
      } else {
         comp.setBackground(DefaultStyle.TABLE_ODD_ROW);
      }

      if (isSelected) {
         comp.setBackground(DefaultStyle.SELECTED_ROW_BACKGROUND);
         comp.setForeground(DefaultStyle.SELECTED_ROW_FOREGROUND);
      } else {
         comp.setForeground(Color.BLACK);
      }

      return comp;
   }
}
