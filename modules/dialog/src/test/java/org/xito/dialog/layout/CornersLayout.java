
package org.xito.dialog.layout;

import javax.swing.*;

import org.xito.dialog.TablePanel;

public class CornersLayout {
   
   public static void main(String args[]) {
      
      JFrame f = new JFrame("CornersLayout");
      f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      f.setSize(400,400);
            
      TablePanel contentPane = new TablePanel(CornersLayout.class.getResource("corners.html"));
      contentPane.setPaintBorderLines(true);
      
      f.setContentPane(contentPane);
      contentPane.add("nw", new JButton("NW"));
      contentPane.add("ne", new JButton("NE"));
      contentPane.add("sw", new JButton("SW"));
      contentPane.add("se", new JButton("SE"));
      contentPane.add("center", new JButton("CENTER"));
                  
      f.setVisible(true);
   }
   
}
