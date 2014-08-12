package test.xito.dazzle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.xito.dazzle.widget.button.ButtonStyle;
import org.xito.dazzle.widget.button.BasicButton;

public class ButtonTest {

   /**
    * @param args
    */
   public static void main(String[] args) {
      
      JFrame f = new JFrame("ButtonTest");
      f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            
      Container container = f.getContentPane();
      container.add(getAppleButtons(), BorderLayout.NORTH);
      container.add(getDazzleButtons(), BorderLayout.SOUTH);
      
      
      f.setSize(600,600);
      f.setVisible(true);
   }
   
   public static JPanel getDazzleButtons() {
      JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT,2,2));
      
//      StyleButton sb = new StyleButton(ButtonStyle.SQUARE);
//      sb.setText("Square Button");
//      container.add(sb);
      //System.out.println(sb.getComponent().getPreferredSize());
      
      BasicButton gb = new BasicButton(ButtonStyle.GRADIENT);
      gb.setText("Gradient Button");
      container.add(gb);
      System.out.println(gb.getInsets());
      System.out.println(gb.getPreferredSize());
      /*
      RoundedRectButton rb = new RoundedRectButton("Round Rect Button");
      rb.addToContainer(container);
      */
      return container;
   }
   
   public static JPanel getAppleButtons() {
      JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT,2,2));
      
//      JButton squareBtn = new JButton("Square Button") {
//         public void paint(Graphics g) {
//            g.setColor(Color.RED);
//            g.fillRect(0,0,getWidth(), getHeight());
//            super.paint(g);
//         }
//      };
//      squareBtn.putClientProperty("JButton.buttonType", "square");
//      container.add(squareBtn);
            
      JButton gradientBtn = new JButton("Gradient Button"){
         public void paint(Graphics g) {
            g.setColor(Color.RED);
            g.fillRect(0,0,getWidth(), getHeight());
            super.paint(g);
         }
      };
      
      gradientBtn.putClientProperty("JButton.buttonType", "gradient");
      container.add(gradientBtn);
      System.out.println(gradientBtn.getInsets());
      System.out.println(gradientBtn.getPreferredSize());
      
//      JButton roundRectBtn = new JButton("Roundrect Button");
//      roundRectBtn.putClientProperty("JButton.buttonType", "roundRect");
//      container.add(roundRectBtn);
      
      return container;
      
   }

}
