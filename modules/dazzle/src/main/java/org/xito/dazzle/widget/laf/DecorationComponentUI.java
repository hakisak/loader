package org.xito.dazzle.widget.laf;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.xito.dazzle.widget.PaintDecoratable;
import org.xito.dazzle.widget.PaintDecorator;

public class DecorationComponentUI extends ComponentUI implements PaintDecoratable {

   protected ArrayList<PaintDecorator> paintDecorators = new ArrayList<PaintDecorator>();
   
   /* (non-Javadoc)
    * @see org.xito.dazzle.widget.PaintDecoratable#addPaintDecorator(org.xito.dazzle.widget.PaintDecorator)
    */
   public void addPaintDecorator(PaintDecorator decorator) {
      paintDecorators.add(decorator);
   }

   public void processPostPaintDecorators(Graphics g, JComponent comp) {
      
      //reverse the list to go through the paint decorator's backwards
      ArrayList<PaintDecorator> list = new ArrayList<PaintDecorator>(paintDecorators);
      Collections.reverse(list);
             
      for(PaintDecorator decorator : list) {
           decorator.postPaintComponent(g, comp);
      }
   }

   public void processPrePaintDecorators(Graphics g, JComponent comp) {
      
      for(PaintDecorator decorator : paintDecorators) {
           decorator.prePaintComponent(g, comp);
       }
   }

   public void removePaintDecorator(PaintDecorator decorator) {
       paintDecorators.remove(decorator);
   }
   
   @Override
   public void paint(Graphics g, JComponent c) {
      
      processPrePaintDecorators(g, c);
      super.paint(g, c);
      processPostPaintDecorators(g, c);
      
      super.paint(g, c);
   }
}
