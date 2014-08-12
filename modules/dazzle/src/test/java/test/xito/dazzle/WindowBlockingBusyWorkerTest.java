package test.xito.dazzle;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.xito.dazzle.worker.WindowBlockingBusyWorker;
import org.xito.dialog.DialogManager;

public class WindowBlockingBusyWorkerTest {

   /**
    * @param args
    */
   public static void main(String[] args) {

      System.setProperty("apple.laf.useScreenMenuBar", "true");

      final JFrame f = new JFrame("Test Window Blocking Busy Worker");
      f.getContentPane().setLayout(new BorderLayout());
      JMenuBar menuBar = new JMenuBar();
      JMenu fileMnu = new JMenu("File");
      menuBar.add(fileMnu);
      menuBar.add(new JMenu("Edit"));
      fileMnu.add(new JMenuItem("Test"));
      f.setJMenuBar(menuBar);

      f.setSize(500, 300);
      f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      final WindowBlockingBusyWorker busyWorker = new WindowBlockingBusyWorker(f) {

         /**
          * Do some Work
          */
         public Object work() {
            try {
               for(int i=5;i>=0;i--) {
                  super.busyGlassPane.getBusyPanel().setText("Working "+i);
                  Thread.currentThread().sleep(1000); //wait 1 second;
               }
            }
            catch(Exception exp) {
               exp.printStackTrace();
            }
            
            return null;
         }

         /**
          * Update UI after Work
          */
         public void finished(Object data) {
            
         }
      };
      
      final WindowBlockingBusyWorker busyWorker2 = new WindowBlockingBusyWorker(f) {

         /**
          * Do some Work
          */
         public Object work() {
            try {
               for(int i=25;i>=0;i--) {
                  super.busyGlassPane.getBusyPanel().setText("Working "+i);
                  Thread.currentThread().sleep(1000); //wait 1 second;
               }
            }
            catch(Exception exp) {
               exp.printStackTrace();
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     DialogManager.showMessage(f, "Test", "Interrupted!");
                  }
               });
            }
            
            return null;
         }

         /**
          * Update UI after Work
          */
         public void finished(Object data) {
            
         }
      };
      
      JPanel top = new JPanel(new FlowLayout());
      JButton startBtn = new JButton("Start");
      startBtn.addActionListener(new ActionListener() {

         public void actionPerformed(ActionEvent e) {
            busyWorker.invokeLater();
         }
      });
      top.add(startBtn);
      
      JButton startBtn2 = new JButton("Start Interruptable");
      startBtn2.addActionListener(new ActionListener() {

         public void actionPerformed(ActionEvent e) {
            busyWorker2.setCancelEnabled(true);
            busyWorker2.invokeLater();
         }
      });
      top.add(startBtn2);
      
      
      f.getContentPane().add(top, BorderLayout.NORTH);

      JTextArea ta = new JTextArea();
      f.getContentPane().add(new JScrollPane(ta));

      f.setVisible(true);
   }

}
