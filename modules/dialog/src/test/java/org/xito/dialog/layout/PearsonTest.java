package org.xito.dialog.layout;

import org.xito.dialog.TableLayout;
import org.xito.dialog.TablePanel;

import javax.swing.*;
import java.awt.*;

public class PearsonTest {
   
   public static void main(String args[]) {
      
      JFrame f = new JFrame("PearsonTest");
      f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                  
      TablePanel contentPane = new TablePanel(new TableLayout(PearsonTest.class.getResource("pearson_test_layout.html")));
      contentPane.setPaintBorderLines(true);
      f.getContentPane().add(contentPane);

      JLabel lbl1 = new JLabel("Label 1");
      lbl1.setOpaque(true);
      lbl1.setBackground(Color.BLUE);
      contentPane.add("lbl1", lbl1);
      contentPane.add("btn1", new JButton("Button A"));
      contentPane.add("btn2", new JButton("Button B"));

      f.pack();      
            
      f.setVisible(true);
   }
   
}
