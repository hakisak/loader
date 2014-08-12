/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.widget;

import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.JComponent;

/**
 *
 * @author deane
 */
public class ShapeDecorator implements PaintDecorator {

    protected Shape shape;

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public void postPaintComponent(Graphics g, JComponent comp) {
        g.setClip(null);
    }

    public void prePaintComponent(Graphics g, JComponent comp) {
        g.setClip(shape);
    }



}
