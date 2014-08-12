package test.xito.dazzle;

import java.awt.*;
import javax.swing.*;

import org.xito.dazzle.widget.panel.StackPanel;

public class TestStackPanel {
   
public static void main(String args[]) {
    testAccordion();
    testBasic();
}

public static void testAccordion() {

    JFrame f = new JFrame("Test Accordion");
      f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      f.setSize(400,400);

      JPanel panel = new JPanel();
      panel.setBackground(Color.WHITE);
      StackPanel stackedPanel = new StackPanel(true);
      JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, stackedPanel, panel);

      f.getContentPane().add(mainSplit);
      f.setVisible(true);

      JPanel p1 = new JPanel();
      p1.setBackground(Color.WHITE);
      stackedPanel.addItem("item1", p1);

      JPanel p2 = new JPanel();
      p2.setBackground(Color.WHITE);
      stackedPanel.addItem("item2", p2);

      JPanel p3 = new JPanel();
      p3.setBackground(Color.WHITE);
      stackedPanel.addItem("item3", p3);

      JPanel p4 = new JPanel();
      p4.setBackground(Color.WHITE);
      stackedPanel.addItem("item4", p4);


}

public static void testBasic() {

      JFrame f = new JFrame("Test Basic");
      f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      f.setSize(400,400);
     
      JPanel panel = new JPanel();
      panel.setBackground(Color.WHITE);
      StackPanel stackedPanel = new StackPanel();
      JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, stackedPanel, panel);
      
      f.getContentPane().add(mainSplit);
      f.setVisible(true);
      
      JPanel p1 = new JPanel();
      p1.setBackground(Color.WHITE);
      stackedPanel.addItem("item1", p1);
            
      JPanel p2 = new JPanel();
      p2.setBackground(Color.WHITE);
      stackedPanel.addItem("item2", p2);

      JPanel p3 = new JPanel();
      p3.setBackground(Color.WHITE);
      stackedPanel.addItem("item3", p3);

      JPanel p4 = new JPanel();
      p4.setBackground(Color.WHITE);
      stackedPanel.addItem("item4", p4);
   }

}
