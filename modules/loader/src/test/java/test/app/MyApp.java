package test.app;

import java.io.*;
import java.awt.event.*;
import java.net.*;
import java.util.logging.*;
import javax.swing.*;

import org.xito.boot.*;

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

      try {
         //Setup Asteroids
         gameDesc = new AppDesc("Asteroids", "Asteroids");
         String codebase = "http://xito.sourceforge.net/apps/games/asteroids/";
         gameDesc.addClassPathEntry(new ClassPathEntry(new URL(codebase+"asteroids.jar")));
         gameDesc.setPermissions(gameDesc.getAllPermissions());
         gameDesc.setMainClass("org.xito.asteroids.MainApp");
         
      } catch(Exception exp) {
         exp.printStackTrace();
         Boot.showError(null, "Error", "Error creating Sample", exp);
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
            
      MyApp app = new MyApp();
   }
   
}
