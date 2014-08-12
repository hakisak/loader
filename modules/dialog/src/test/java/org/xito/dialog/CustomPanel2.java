package org.xito.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.SwingUtilities;

public class CustomPanel2 {

   public static void main(String args[]) {
      String html = "<html>This is a test of a <b>Custom Panel</b> with its own buttons </html>";

      final JPanel customPanel = new JPanel(new BorderLayout());
      customPanel.setBackground(Color.RED);
      customPanel.add(BorderLayout.NORTH, new JLabel("Custom Panel2"));

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JButton btn = new JButton("Click Me");
      btn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            customPanel.putClientProperty("result", "ClickMe");
            Window w = SwingUtilities.getWindowAncestor((Component)evt.getSource());
            w.dispose();
        }
      });

      JButton btn2= new JButton("Don't Click Me");
      btn2.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            customPanel.putClientProperty("result", "Don't ClickMe");
            Window w = SwingUtilities.getWindowAncestor((Component)evt.getSource());
            w.dispose();
        }
      });

      buttonPanel.add(btn);
      buttonPanel.add(btn2);
      customPanel.add(BorderLayout.SOUTH, buttonPanel);
      
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
      desc.setType(DialogManager.NONE);
      desc.setCustomPanel(customPanel);
      desc.setIcon(DialogManager.getInfoIcon());

      DialogManager.showDialog(desc);
      String value = (String)customPanel.getClientProperty("result");

      DialogManager.showMessage(null, "Result", value);

   }

}
