package test.xito.dazzle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.xito.dazzle.widget.button.ImageButton;

public class TestImageButton {
   
   public static final void main(String args[]) {
      
      JFrame f = new JFrame("test");
      f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      f.setSize(400,400);
      
      ImageButton imgBtn = new ImageButton(
            new ImageIcon(ImageButton.class.getResource("org.xito.launcher.images/plus.png")),
            new ImageIcon(ImageButton.class.getResource("org.xito.launcher.images/plus_pressed.png")));
      f.getContentPane().add(imgBtn);
      
      imgBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            System.out.println("Test");
         }
      });
           
      
      f.setVisible(true);
      
   }

}
