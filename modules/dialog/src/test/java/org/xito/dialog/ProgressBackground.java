
package org.xito.dialog;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;

public class ProgressBackground {
   
   private static ProgressDialog dialog;
   
   public static void main(String args[]) {
      
      //If there is already a org.xito with a running task just display that
      if(dialog != null && dialog.getTaskThread() != null && dialog.getTaskThread().isAlive()) {
         dialog.setVisible(true);
         return;
      }
      
      ProgressDialogDescriptor desc = new ProgressDialogDescriptor();
      desc.setTitle("Test Progress");
      desc.setSubtitle("Progress Dialog that can be Hidden and then Reopened");
      desc.setButtonTypes(new ButtonType[]{new ButtonType("Hide", 99), new ButtonType("Cancel", ButtonType.CANCEL)});
                  
      desc.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            JButton btn = (JButton)e.getSource();
            //Cancel
            if(btn.getClientProperty(DialogManager.RESULT_KEY).equals(new Integer(ButtonType.CANCEL))) {
               dialog.cancelRunnableTask();
               dialog.dispose();
               dialog = null;
            }
            else {
               dialog.setVisible(false);
            }
         }
      });
            
      //Create a Custom Panel to show a dynamic message
      JPanel panel = new JPanel(new BorderLayout());
      final String msg = "<html>This Dialog is counting down: {0,number,integer}</html>";
      final int total_secs = 30;
      final JLabel lbl = new JLabel();
      lbl.setHorizontalAlignment(SwingConstants.CENTER);
      lbl.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
      panel.add(lbl);
      desc.setCustomPanel(panel);
      desc.setRunnableTask(new Runnable() {
         public void run() {
            try {
               //Count down
               for(int i=total_secs;i>=0;i--) {
                  if(Thread.currentThread().isInterrupted()) {
                     DialogManager.showMessage(null, "Test", "Interrupted!");
                     break;
                  }
                  
                  String updatedMsg = MessageFormat.format(msg, new Object[]{new Integer(i)});
                  lbl.setText(updatedMsg);
                  lbl.getParent().invalidate();
                  System.out.println(i);
                  
                  Thread.currentThread().sleep(1000);
               }
               lbl.setText("Completed!");
            }
            catch(InterruptedException exp) {
               System.out.println("Interrupted!");
               DialogManager.showMessage(null, "Test", "Interrupted!");
            }
         }
      });
            
      dialog = new ProgressDialog(null, desc, false);
      dialog.setVisible(true);
   }
   
}
