package test.app;

import java.util.*;
import javax.swing.JFrame;
import org.xito.boot.*;

/**
 *
 * @author DRICHAN
 */
public class MemoryTestApp {
   
   private static Vector vector = new Vector();
   
   public static void main(String[] args) {
      
      JFrame f = new JFrame("Memory Test");
      f.setBounds(0,0,200,200);
      f.setVisible(true);
      
      for(int i=0;i<1000;i++) {
         vector.add(""+System.currentTimeMillis());
      }
      
      //Exit after 10 seconds
      Timer timer = new Timer(true);
      timer.schedule(new TimerTask() {
         public void run() {
            AppClassLoader loader = (AppClassLoader)this.getClass().getClassLoader();
            loader.getAppInstance().destroy();
         }
      }, 10000);
      
   }
   
}
