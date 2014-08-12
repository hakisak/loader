package test.xito.dazzle;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.xito.dazzle.widget.border.RoundRectBorder;

public class BusyWorkerTest {
   
   
   public static void main(String[] args) {
      
      JFrame frame = new JFrame("test frame");
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.setSize(400,400);
      
      JPanel testPanel = new JPanel(null);
      testPanel.setBorder(new RoundRectBorder(Color.BLACK, 20, 3));
      //testPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));
      
      Insets insets = testPanel.getBorder().getBorderInsets(testPanel);
      
      JPanel testComp = new JPanel();
      testComp.setBounds(new Rectangle(insets.left, insets.top,50,50));
      testComp.setBackground(Color.RED);
      
      testPanel.add(testComp);
      frame.setContentPane(testPanel);
      
      frame.setVisible(true);
      
   }

}
