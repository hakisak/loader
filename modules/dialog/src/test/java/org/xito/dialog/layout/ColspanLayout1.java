
package org.xito.dialog.layout;

import java.awt.*;
import javax.swing.*;

import org.xito.dialog.TablePanel;

public class ColspanLayout1 {

   static int currentColorNum = 50;
   static int colorStep = 10;

   public static void main(String args[]) {
      
      JFrame f = new JFrame("Col Span Layout");
      f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      f.setSize(400,400);
            
      TablePanel contentPane = new TablePanel(ColspanLayout1.class.getResource("colspan_layout1.html"));
      contentPane.setPaintBorderLines(true);
      
      f.setContentPane(contentPane);
      contentPane.add("a1", new CellPanel("a1", nextColor()));
      contentPane.add("b1", new CellPanel("b1", nextColor()));
      contentPane.add("c1", new CellPanel("c1", nextColor()));
      contentPane.add("d1", new CellPanel("d1", nextColor()));
      contentPane.add("e1", new CellPanel("e1", nextColor()));
      contentPane.add("f1", new CellPanel("f1", nextColor()));

      contentPane.add("a2", new CellPanel("a2", nextColor()));
      contentPane.add("b2", new CellPanel("b2", nextColor()));
      contentPane.add("d2", new CellPanel("d2", nextColor()));
      contentPane.add("f2", new CellPanel("f2", nextColor()));

      contentPane.add("a3", new CellPanel("a3", nextColor()));
      contentPane.add("d3", new CellPanel("d3", nextColor()));

      contentPane.add("a4", new CellPanel("a4", nextColor()));
      contentPane.add("data", new CellPanel("data", nextColor()));
      contentPane.add("f4", new CellPanel("f4", nextColor()));
                  
      f.setVisible(true);
   }

   public static Color nextColor() {
       currentColorNum+=colorStep;
       return new Color(currentColorNum, currentColorNum, currentColorNum);
   }

   public static class CellPanel extends JPanel {

       public CellPanel(String name, Color bgColor) {
           setLayout(new BorderLayout());
           setName(name);
           add(new JLabel(name, JLabel.CENTER), BorderLayout.CENTER);
           setOpaque(true);
           setBackground(bgColor);
       }

   }
   
}
