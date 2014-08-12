
package org.xito.dialog.layout;

import java.awt.*;
import javax.swing.*;

import org.xito.dialog.TableLayout;

public class DialogSampleLayout {
   
   public static void main(String args[]) {
      
      JFrame f = new JFrame("DialogSampleLayout");
      f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      f.setSize(400,400);
           
      JPanel contentPane = new JPanel(new TableLayout(DialogSampleLayout.class.getResource("dialog_sample.html")));
      f.setContentPane(contentPane);
      contentPane.add("label1", new JLabel("Label One:"));
      contentPane.add("label2", new JLabel("Label Two:"));
      contentPane.add("label3", new JLabel("Label Three:"));
      
      JTextField f1 = new JTextField();
      f1.setMaximumSize(new Dimension(400, f1.getPreferredSize().height));
      contentPane.add("field1", f1);
      
      JTextField f2 = new JTextField();
      f2.setMaximumSize(new Dimension(400, f1.getPreferredSize().height));
      contentPane.add("field2", f2);
      
      JTextField f3 = new JTextField();
      f3.setMaximumSize(new Dimension(400, f1.getPreferredSize().height));
      contentPane.add("field3", f3);

      contentPane.add("seperator1", new JSeparator(JSeparator.HORIZONTAL));

      JScrollPane sp = new JScrollPane(new JTextArea());
      contentPane.add("textarea", sp);
      
      f.setVisible(true);
   }
   
}
