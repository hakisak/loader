/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.widget;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 *
 * @author deane
 */
public interface PaintDecorator {

    public void prePaintComponent(Graphics g, JComponent comp);

    public void postPaintComponent(Graphics g, JComponent comp);
}
