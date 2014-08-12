/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.widget.border;

import org.xito.dazzle.widget.DefaultStyle;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.Border;

/**
 *
 * @author deane
 */
public class ShadowBorder implements Border {

    protected boolean drawOutline;
    protected int shadowWidth;
    protected Insets insets;
    protected Color shadowColor;
    protected Color outlineColor = new Color(175,175,175);

    public ShadowBorder() {
        this(true, 4);
    }

    public ShadowBorder(boolean drawOutline, int width) {
        this.drawOutline = drawOutline;
        this.shadowWidth = width;
        init();
    }

    protected void init() {
        shadowColor = new Color(0,0,0,50);
        if(drawOutline) {
            insets = new Insets(0, 1, 1 + shadowWidth, 1 + shadowWidth);
        }
        else {
            insets = new Insets(0,1,1,1);
        }
    }

    public Insets getBorderInsets(Component c) {
        return insets;
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        //draw border
        if(drawOutline) {
            g.setColor(outlineColor);
            g.drawLine(x, y, x, height-shadowWidth-1);
            g.drawLine(x, height-shadowWidth-1, width-shadowWidth-1, height-shadowWidth-1);
            g.drawLine(width-shadowWidth-1, 0, width-shadowWidth-1, height-shadowWidth-1);
        }

        //draw Shadow
        g.setColor(shadowColor);
        g.fillRect(x+shadowWidth, height-shadowWidth, width, height);
        g.fillRect(width-shadowWidth, 0, width, height-shadowWidth);
    }



}
