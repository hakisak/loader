
package org.xito.dialog.layout;

import java.awt.*;
import javax.swing.*;

import org.xito.dialog.TablePanel;

public class RowspanLayout1 {

   static int currentColorNum = 50;
   static int colorStep = 10;

   public static void main(String args[]) {
      
      JFrame f = new JFrame("Row Span Layout");
      f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      f.setSize(400,400);
            
      TablePanel contentPane = new TablePanel(RowspanLayout1.class.getResource("rowspan_layout1.html"));
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
      contentPane.add("c2", new CellPanel("c2", nextColor()));
      contentPane.add("d2", new CellPanel("d2", nextColor()));
      contentPane.add("e2", new CellPanel("e2", nextColor()));

      contentPane.add("a3", new CellPanel("a3", nextColor()));
      
      contentPane.add("a4", new CellPanel("a4", nextColor()));
      contentPane.add("b4", new CellPanel("b4", nextColor()));

      contentPane.add("a5", new CellPanel("a5", nextColor()));
      contentPane.add("b5", new CellPanel("b5", nextColor()));

      contentPane.add("a6", new CellPanel("a6", nextColor()));
      contentPane.add("b6", new CellPanel("b6", nextColor()));

      contentPane.add("a7", new CellPanel("a7", nextColor()));
      contentPane.add("b7", new CellPanel("b7", nextColor()));
      contentPane.add("f7", new CellPanel("f7", nextColor()));
                  
      f.setVisible(true);
   }

   public static Color nextColor() {
       currentColorNum+=colorStep;
       if(currentColorNum>255) currentColorNum = 50;
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
