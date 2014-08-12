
package org.xito.dialog.layout;

import java.awt.*;
import javax.swing.*;

import org.xito.dialog.DialogManager;
import org.xito.dialog.TableLayout;
import org.xito.dialog.TablePanel;

public class TitlePanelLayout {
   
   public static void main(String args[]) {
      
      JFrame f = new JFrame("TitlePanelLayout");
      f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      f.setSize(400,400);
            
      TablePanel contentPane = new TablePanel(TableLayout.createLayout(TableLayout.TITLE_LAYOUT));
      contentPane.setPaintBorderLines(true);
      f.getContentPane().add(contentPane, BorderLayout.NORTH);

      contentPane.add("title", new JLabel("<html><b>The Title Goes Here</b></html>"));
      contentPane.add("subtitle", new JLabel("<html>The longer subtitle goes here</html>"));
      contentPane.add("icon", new JLabel(DialogManager.getInfoIcon()));

      f.setVisible(true);
   }
   
}
