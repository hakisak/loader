
package org.xito.dialog.layout;

import java.awt.Dimension;
import javax.swing.*;

import org.xito.dialog.TableLayout;
import org.xito.dialog.TablePanel;

public class RightButtonsLayout {
   
   public static void main(String args[]) {
      
      JFrame f = new JFrame("RightButtonsLayout");
      f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      f.setSize(400,400);
      
      TablePanel contentPane = new TablePanel(new TableLayout(RightButtonsLayout.class.getResource("right_buttons_layout.html")));
      contentPane.setPaintBorderLines(true);
      f.getContentPane().add(contentPane);

      //contentPane.add("list", new JScrollPane(new JList()));
      JScrollPane sp = new JScrollPane(new JList());
      sp.setPreferredSize(new Dimension(0,0));
      contentPane.add("list", sp);
      contentPane.add("add_btn", new JButton("Add"));
      contentPane.add("del_btn", new JButton("Delete"));
                  
      f.setVisible(true);
   }
   
}
