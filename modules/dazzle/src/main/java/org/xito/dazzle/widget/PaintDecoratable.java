/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.widget;

import java.awt.Graphics;

import javax.swing.JComponent;

/**
 *
 * @author deane
 */
public interface PaintDecoratable {

    public void processPrePaintDecorators(Graphics g, JComponent comp);

    public void processPostPaintDecorators(Graphics g, JComponent comp);

    public void addPaintDecorator(PaintDecorator decorator);

    public void removePaintDecorator(PaintDecorator decorator);
}
