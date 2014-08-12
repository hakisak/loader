
package org.xito.dialog.layout;

import javax.swing.*;

import org.xito.dialog.TableLayout;

public class ResizeRowsLayout {
   
   public static void main(String args[]) {
      
      JFrame f = new JFrame("ResizeRowsLayout");
      f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      f.setSize(400,400);
            
      JPanel contentPane = new JPanel(new TableLayout(ResizeRowsLayout.class.getResource("resize_rows.html")));
      f.setContentPane(contentPane);
      contentPane.add("row1", new JButton("row1"));
      contentPane.add("row2", new JButton("row2"));
      contentPane.add("row3", new JButton("row3"));
      contentPane.add("row4", new JButton("row4"));
      
            
      f.setVisible(true);
   }
   
}
