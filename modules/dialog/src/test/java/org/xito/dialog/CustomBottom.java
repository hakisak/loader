package org.xito.dialog;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class CustomBottom {

   public static void main(String args[]) {
      String html = "<html>This is a test of a <b>Custom Bottom</b> org.xito </html>";

      JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      bottomPanel.setOpaque(false);
      bottomPanel.add(new JLabel("Bottom Panel"));
      
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
      desc.setBottomPanel(bottomPanel);
      desc.setIcon(DialogManager.getInfoIcon());

      DialogManager.showDialog(desc);
   }

}
