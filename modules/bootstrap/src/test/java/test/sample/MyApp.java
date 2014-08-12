package test.sample;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.logging.*;
import javax.swing.*;

import org.xito.boot.*;
import org.xito.dialog.*;

/**
 *
 * @author  Deane
 */
public class MyApp extends JPanel implements ActionListener {
   
   Logger logger = Logger.getLogger(MyApp.class.getName());
   
   JCheckBox gameExternalCB;
   JCheckBox swingSetExternalCB;
   
   JButton gameBtn;
   JButton swingSetBtn;
   AppDesc swingSetDesc;
   AppDesc gameDesc;
   
   public MyApp() {
      super();
      
      setLayout(new TableLayout(MyApp.class.getResource("layout.html")));
      //setPaintBorderLines(true);      
      JLabel lbl = new JLabel();
      String html = "<html><p>This is a sample application that demonstrates " +
         "applications running in the Xito BootStrap.</p><p>Click a button below to launch an application.</p>" +
         "<p><br><b>note:</b> Each application runs in the same VM as the BootStrap</p></html>";
      
      lbl.setText(html);
      add("description", lbl);
            
      gameBtn = new JButton("Launch Asteroids");
      gameBtn.addActionListener(this);
      gameExternalCB = new JCheckBox("Use Seperate VM");
      add("game_btn", gameBtn);
      add("game_cb", gameExternalCB);
      
      swingSetBtn = new JButton("Launch SwingSet");
      swingSetBtn.addActionListener(this);
      swingSetExternalCB = new JCheckBox("Use Seperate VM");
      add("swingset_btn", swingSetBtn);
      add("swingset_cb", swingSetExternalCB);
      
      try {
         //Setup SwingSet
         swingSetDesc = new AppDesc("SwingSet2", "SwingSet2");
         String codebase = "http://java.sun.com/products/jfc/jws/";
         swingSetDesc.addClassPathEntry(new ClassPathEntry(new URL(codebase+"SwingSet2.jar")));
         swingSetDesc.setPermissions(swingSetDesc.getAllPermissions());
         swingSetDesc.setMainClass("SwingSet2");
         
         //Setup Asteroids
         gameDesc = new AppDesc("Asteroids", "Asteroids");
         codebase = "http://xito.sourceforge.net/apps/games/asteroids/";
         gameDesc.addClassPathEntry(new ClassPathEntry(new URL(codebase+"asteroids.jar")));
         gameDesc.setPermissions(gameDesc.getAllPermissions());
         gameDesc.setMainClass("org.xito.asteroids.MainApp");
         
      } catch(Exception exp) {
         exp.printStackTrace();
         DialogManager.showError(null, "Error", "Error creating Sample", exp);
      }
   }
   
   public void actionPerformed(ActionEvent e) {
      
      try {
         AppLauncher launcher = new AppLauncher();
         if(e.getSource() == gameBtn) {
            if(gameExternalCB.isSelected())
               launcher.launchExternal(gameDesc);
            else
               launcher.launchBackground(gameDesc);
         } 
         else if(e.getSource() == swingSetBtn) {
            if(swingSetExternalCB.isSelected())
               launcher.launchExternal(swingSetDesc);
            else
               launcher.launchBackground(swingSetDesc);
         }
      }
      catch(IOException ioExp) {
         Boot.showError("Error", ioExp.getMessage(), ioExp);
      }
   }
   
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      
      System.out.println("************************");
      System.out.println("Sample BootStrap Application");
      System.out.println("************************");
            
      MyApp p = new MyApp();
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setTitle("Xito BootStrap Sample");
      desc.setSubtitle("Sample Application to demonstrate BootStrap");
      desc.setMessageType(DialogManager.INFO_MSG);
      desc.setButtonTypes(new ButtonType[]{new ButtonType("Close", 1)});
      desc.setCustomPanel(p);
      desc.setWidth(450);
      desc.setHeight(350);
      desc.setResizable(true);
      desc.setShowButtonSeparator(true);
      
      AlertDialog dialog = new AlertDialog((Frame)null, desc, false);
      dialog.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            Boot.endSession(true);
         }
      });
      
      dialog.show();
      
   }
   
}
