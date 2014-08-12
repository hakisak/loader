
package org.xito.dialog;

import java.awt.*;
import javax.swing.*;

public class ProgressBar {
   
   public static void main(String args[]) {
      
      ProgressDialogDescriptor desc = new ProgressDialogDescriptor();
      desc.setTitle("Test Progress");
      desc.setSubtitle("Test of a Progress Dialog with a Progress Bar");
      desc.setType(DialogManager.CANCEL);
                            
      //Create a Custom Panel to show a dynamic message
      JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      final int total = 20;
      final JProgressBar progressBar = new JProgressBar();
      progressBar.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
      progressBar.setMaximum(total);
      progressBar.setMinimum(0);
      panel.add(new JLabel("Progress:"));
      panel.add(progressBar);
      desc.setCustomPanel(panel);
      desc.setRunnableTask(new Runnable() {
         public void run() {
            try {
               //Count Up
               for(int i=0;i<=total;i++) {
                  if(Thread.currentThread().isInterrupted()) {
                     DialogManager.showMessage(null, "Test", "Interrupted!");
                     break;
                  }
                  
                  progressBar.setValue(i);
                  Thread.currentThread().sleep(250);
               }
            }
            catch(InterruptedException exp) {
               System.out.println("Interrupted!");
               DialogManager.showMessage(null, "Test", "Interrupted!");
            }
         }
      });
            
      ProgressDialog dialog = new ProgressDialog(null, desc, true);
      dialog.setVisible(true);
      if(dialog.getResult() == DialogManager.CANCEL) {
         dialog.cancelRunnableTask();
      }
   }
   
}
