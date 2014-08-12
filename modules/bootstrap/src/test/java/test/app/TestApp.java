package test.app;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author  Deane
 */
public class TestApp {
   
   static int count = 1;
   
   /** Creates a new instance of TestApp */
   public TestApp() {
   }
   
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      
      //for(int i=0;i<5;i++) {
         JFrame f = new JFrame("Testing:"+count++);
         f.setSize(400,400);
         f.setContentPane(new MyPanel());
         f.setVisible(true);
      //}
         
         System.out.println("ARGS:");
         for(int i=0;i<args.length;i++) {
            System.out.println(args[i]);
         }
   }
      
   public static class MyPanel extends JPanel implements ActionListener {
      
      public MyPanel() {
         Frame frames[] = Frame.getFrames();
         JButton exit = new JButton("Exit");
         add(exit);
         exit.addActionListener(this);
      }
      
      public void actionPerformed(ActionEvent evt) {
         System.exit(0);
      }
   }
}
