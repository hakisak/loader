package test.xito.dazzle;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.xito.dazzle.utilities.UIUtilities;
import org.xito.dazzle.widget.toolbar.Toolbar;

public class TransparentWindowTest {
   
   public static void main(String[] args) {
      
      final JFrame frame = new JFrame("Test ToolBar");
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      UIUtilities.setWindowAlpha(frame, .5f);
      
      frame.setSize(600,600);
      frame.setVisible(true);
      
   }

}
