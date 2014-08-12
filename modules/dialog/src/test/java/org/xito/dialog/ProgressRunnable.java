package org.xito.dialog;

import java.awt.*;
import java.text.*;
import javax.swing.*;

public class ProgressRunnable implements Runnable {

    private JLabel lbl;
    
    public static void main(String args[]) {

        ProgressDialogDescriptor desc = new ProgressDialogDescriptor();
        desc.setTitle("Test Progress");
        desc.setSubtitle("Test of a Progress Dialog with a Runnable Task");
        desc.setType(DialogManager.CANCEL);
        desc.setPack(true);

        //Create a Custom Panel to show a dynamic message
        JPanel panel = new JPanel(new BorderLayout());

        JLabel lbl = new JLabel("Count down org.xito");
        lbl.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lbl);
        desc.setCustomPanel(panel);
        desc.setRunnableTask(new ProgressRunnable(lbl));

        ProgressDialog dialog = new ProgressDialog(null, desc, false);
        dialog.setVisible(true);
        if (dialog.getResult() == DialogManager.CANCEL) {
            dialog.cancelRunnableTask();
        }
        
    }

    public ProgressRunnable(JLabel lbl) {
        this.lbl = lbl;
    }
    
    public void run() {
        try {
            String msg = "<html>This Dialog is counting down: {0,number,integer}</html>";
            int total_secs = 20;

            //Count down
            for (int i = total_secs; i >= 0; i--) {
                if (Thread.currentThread().isInterrupted()) {
                    SwingUtilities.invokeAndWait(new Runnable() {

                        public void run() {
                            DialogManager.showMessage(null, "Test", "Interrupted!");
                        }
                    });
                    break;
                }

                //System.out.println(i);
                final String updatedMsg = MessageFormat.format(msg, new Object[]{new Integer(i)});
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        lbl.setText(updatedMsg);
                    }
                });

                Thread.currentThread().sleep(1000);
            }
            lbl.setText("Completed!");
        } catch (InterruptedException exp) {
            System.out.println("Interrupted!");
            DialogManager.showMessage(null, "Test", "Interrupted!");
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}
