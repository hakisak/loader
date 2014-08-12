package test.xito.dazzle;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.xito.dazzle.widget.border.RoundRectBorder;
import org.xito.dazzle.widget.panel.RoundRectPanel;

public class RoundRectBorderTest {
   
   public static void main(String[] args) {
      
      JFrame frame = new JFrame("border test");
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.setSize(400,400);
      //frame.setBackground(new Color(0,0,0,0));
      JPanel testPanel = new JPanel();
      //testPanel.setBackground(new Color(40,40,40,40));
      testPanel.setBorder(new RoundRectBorder(new Color(200, 20, 250, 150), 20, 3));
      //testPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));
      
      Insets insets = testPanel.getBorder().getBorderInsets(testPanel);
      
      JPanel testComp = new JPanel();
      testComp.setBounds(new Rectangle(insets.left, insets.top,50,50));
      testComp.setBackground(Color.RED);
      
      testPanel.add(testComp);
      
      RoundRectPanel rrPanel = new RoundRectPanel(20, 3);
      rrPanel.setLayout(null);
      rrPanel.setBorder(new RoundRectBorder(Color.BLACK, 20, 3));
      rrPanel.setBackground(new Color(150,150,150));
      testComp.setLocation(0,0);
      rrPanel.add(testComp);
      
      //frame.setContentPane(rrPanel);
      frame.setContentPane(testPanel);

      for(int i=0;i<10;i++) {
          testPanel.add(new JButton("test"+i));
      }

      frame.setVisible(true);
      
   }

}
