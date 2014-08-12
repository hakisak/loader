package org.xito.dialog;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class CustomPanel {

   public static void main(String args[]) {
      String html = "<html>This is a test of a <b>Custom Panel</b> org.xito </html>";

      JPanel customPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      customPanel.setBackground(Color.RED);
      customPanel.add(new JLabel("Custom Panel"));
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setWidth(400);
      desc.setHeight(400);
      desc.setWindowTitle("Window Title Goes Here");
      desc.setTitle("Title Goes Here");
      desc.setSubtitle("subtitle goes Here");
      desc.setMessage(html);
      desc.setShowButtonSeparator(true);
      desc.setGradiantColor(new Color(100, 100, 200, 50));
      desc.setGradiantOffsetRatio(0.5f);
      desc.setType(DialogManager.OK_CANCEL);
      desc.setCustomPanel(customPanel);
      desc.setIcon(DialogManager.getInfoIcon());

      DialogManager.showDialog(desc);
   }

}
