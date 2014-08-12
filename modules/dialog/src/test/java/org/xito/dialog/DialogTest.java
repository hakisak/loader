package org.xito.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.event.*;

public class DialogTest {
   
   /** Creates a new instance of TestAlerts */
   public DialogTest() {
   }
   
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) throws Exception {
  
      //Use native Look and feel
      //UIManager.setLookAndFeel("net.java.plaf.windows.WindowsLookAndFeel");
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      
      Toolkit.getDefaultToolkit().setDynamicLayout(true);
      final JFrame frame = new JFrame("Xito Dialog Test");
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setWindowTitle("Xito Dialog Test");      
      desc.setTitle("Xito Dialog Test");      
      desc.setSubtitle("Displays Test Dialogs created by the Xito Dialog API");
      desc.setCustomPanel(new MainPanel());
      desc.setResizable(true);
      desc.setButtonTypes(new ButtonType[]{new ButtonType("Close", 1, true)});
      desc.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           shutdown();
         }
      });
      
      frame.setSize(500, 400);
      
      DialogPanel panel = new DialogPanel(desc);
      frame.setContentPane(panel);
      panel.initDefaultButton();
      
      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      frame.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent e) {
            shutdown();
         }
      });
      
      DialogManager.centerWindowOnScreen(frame);
      frame.setVisible(true);
   }
   
   private static void shutdown() {
      System.out.println("Shutdown Application");
      try {
         Class.forName("org.xito.boot.AppShutdownHelper").newInstance();
      }
      catch(Exception exp) {
        System.exit(0);
      }
   }
   
   /********************************************
    * Main Panel of the Test Application
    *********************************************/   
   public static class MainPanel extends TablePanel implements HyperlinkListener {
      
      private JEditorPane contentPane;
      
      public MainPanel() {
         init();
      }
      
      private void init() {
         try {
             
                        
            setLayout(new TableLayout(DialogTest.class.getResource("dialog_test_layout.html")));
            add("description", new JLabel("<html>Displays examples and tests of various features of the Dialog API. Click a Link below to see an example.</html>"));
            
            contentPane = new JEditorPane();
            contentPane.setPage(DialogTest.class.getResource("test_list.html"));
            contentPane.addHyperlinkListener(this);
            contentPane.setEditable(false);
            JScrollPane sp = new JScrollPane(contentPane);
            add("content", sp);
         }
         catch(IOException ioExp) {
            ioExp.printStackTrace();
            DialogManager.showError(null, "Error", ioExp.getMessage(), ioExp);
         }
      }
      
      /**
       * Listen for HyperLink Clicks
       */
      public void hyperlinkUpdate(HyperlinkEvent e) {
         if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            String desc = e.getDescription();
            if(desc.startsWith("mainclass:")) {
               executeTest(desc.substring(desc.indexOf(':')+1));
            }
            else if(desc.startsWith("source:")) {
               viewSource(desc.substring(desc.indexOf(':')+1));
            }
         }
      }
      
      public void viewSource(String source) {
      
         CodeViewerDialog d = new CodeViewerDialog(null, source);
         d.setVisible(true);
         d.getResult();
      }
      
      public void executeTest(String mainClass) {
         
        if(mainClass == null || "".equals(mainClass)) {
           
           DialogManager.showMessage((Frame)SwingUtilities.getWindowAncestor(this), "Centered Dialog", 
                 "This org.xito should be centered on the parent");
           
           return;
        }
         
         
         try {
            Class mc = DialogTest.class.getClassLoader().loadClass(mainClass);
            Method mainM = mc.getMethod("main", new Class[]{String[].class});
            mainM.invoke(null, new Object[]{new String[0]});
         }
         catch(Exception e) {
            e.printStackTrace();
            DialogManager.showError(null, "Error Running Test", e.getMessage(), e); 
         }
      }
   }
}
