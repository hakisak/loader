package org.xito.appmanager.stackgrouppanel;

import java.awt.BorderLayout;

import javax.swing.*;

public class StackedGroupPanel extends JComponent {

   private JScrollPane scrollPane;
   
   public StackedGroupPanel() {
      
   }
   
   private void init() {
      setLayout(new BorderLayout());
      scrollPane = new JScrollPane();
      add(scrollPane);
      
      
      
   }
   
   
   public static void main(String args[]) {
      JFrame f = new JFrame();
      f.setSize(300,500);
      f.setVisible(true);
      
      f.getContentPane().add(new StackedGroupPanel());
   }
}
