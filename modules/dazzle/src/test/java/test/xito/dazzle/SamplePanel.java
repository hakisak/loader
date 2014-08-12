package test.xito.dazzle;

import org.xito.dazzle.widget.panel.GradientPanel;
import org.xito.dazzle.widget.border.SingleLineBorder;
import java.awt.*;
import javax.swing.*;
import org.xito.dialog.*;
import org.xito.dazzle.*;

public class SamplePanel extends GradientPanel {

   private JLabel titleLbl;
   private JLabel descLbl;
   
   public SamplePanel() {
      super(Color.WHITE, new Color(240,240,240), .25f, SwingConstants.SOUTH);
      init();
   }
   
   private void init() {
      
      setLayout(new TableLayout("<html><body><table>" +
         "<tr><td></td><td id=\"title\"></td></tr>" + 
         "<tr><td></td><td id=\"description\"></td></tr>" +
         "</table></body></html>"));
      
      setPreferredSize(new Dimension(400, 150));
      setBorder(new SingleLineBorder(SingleLineBorder.SOUTH, Color.BLACK, 1));
      
      titleLbl = new JLabel();
      add("title", titleLbl);
      descLbl = new JLabel();
      add("desc", descLbl);
   }
   
   public void setTitle(String title) {
      titleLbl.setText(title);
   }
}
