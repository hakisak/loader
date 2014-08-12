/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.widget;

import org.xito.dazzle.widget.PaintDecorator;
import org.xito.dazzle.widget.PaintDecoratable;
import org.xito.dazzle.*;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JComponent;

/**
 *
 * @author deane
 */
public class DecorationComponent extends JComponent implements PaintDecoratable {

    protected ArrayList<PaintDecorator> paintDecorators = new ArrayList<PaintDecorator>();

    public void addPaintDecorator(PaintDecorator decorator) {
        paintDecorators.add(decorator);
    }

    public void processPostPaintDecorators(Graphics g, JComponent comp) {
       
       //reverse the list to go through the paint decorators backwards
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
    public void paint(Graphics g) {
        processPrePaintDecorators(g, this);
        super.paint(g);
        processPostPaintDecorators(g, this);
    }


}
