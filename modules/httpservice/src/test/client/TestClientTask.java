package test.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.xito.httpservice.client.ServiceClientTask;
import org.xito.httpservice.client.ServiceClientTaskManager;
import org.xito.httpservice.client.ServiceResponse;
import org.xito.httpservice.client.ServiceStub;

public class TestClientTask {
   
   private static ServiceClientTaskManager taskManager;
   
   public static void main(String args[]) {
      
      JFrame frame = new JFrame("test");
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      
      JPanel panel = new JPanel();
      
      frame.getContentPane().add(panel, BorderLayout.CENTER);
      
      JProgressBar progressBar = new JProgressBar();
      
      frame.getContentPane().add(progressBar, BorderLayout.NORTH);
      
      taskManager = new ServiceClientTaskManager();
      taskManager.start();
      
      JButton testStubBtn = new JButton("Test Stub");
      new StubTestTask(progressBar, testStubBtn);
      panel.add(testStubBtn);
      
      JButton testProxyBtn = new JButton("Test Proxy");
      new StubProxyTask(progressBar, testProxyBtn);
      panel.add(testProxyBtn);
      
      frame.pack();
      frame.setVisible(true);
   }
   
   //---------------------------------------------------------------
   public static class StubTestTask extends ServiceClientTask {

      JProgressBar progressBar;
      JButton button;
      
      public StubTestTask(JProgressBar bar, JButton btn) {
         progressBar = bar;
         button = btn;
         button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               taskManager.addTask(StubTestTask.this);
            }
         });
      }
            
      
      protected void init_construct() {
         
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               progressBar.setIndeterminate(true);
            }
         });
         
         super.init_construct();
      }


      public Object construct() {
         
         try{Thread.sleep(3000);}catch(Exception exp){}
         TestStubClient.main(new String[0]);
         
         return null;
      }
      
      public void finished() {
         
         progressBar.setIndeterminate(false);
      }
      
   }
   
   //---------------------------------------------------------------
   public static class StubProxyTask extends ServiceClientTask {

      JProgressBar progressBar;
      JButton button;
      
      public StubProxyTask(JProgressBar bar, JButton btn) {
         progressBar = bar;
         button = btn;
         button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               taskManager.addTask(StubProxyTask.this);
            }
         });
      }
            
      
      protected void init_construct() {
         
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               progressBar.setIndeterminate(true);
            }
         });
         
         super.init_construct();
      }


      public Object construct() {
         
         try{Thread.sleep(3000);}catch(Exception exp){}
         TestStubClient.main(new String[0]);
         
         return null;
      }
      
      public void finished() {
         
         progressBar.setIndeterminate(false);
      }
      
   }


}
