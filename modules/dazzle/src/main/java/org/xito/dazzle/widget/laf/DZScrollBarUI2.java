/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xito.dazzle.widget.laf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;

import org.xito.dazzle.utilities.DrawUtilities;
import org.xito.dazzle.utilities.UIUtilities;

/**
 *
 * @author deane
 */
public class DZScrollBarUI2 extends BasicScrollBarUI {

    public static void main(String args[]) {
        JFrame f = new JFrame("test");
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        StringBuilder text = new StringBuilder();
        for(int i=0;i<100;i++) {
            text.append("This is a lot of text for a lot of testing and stuff " + System.currentTimeMillis() + "\n");
        }


        JTextArea ta = new JTextArea();
        ta.setText(text.toString());
        
        JScrollBar vScrollBar = new JScrollBar(JScrollBar.VERTICAL);
        vScrollBar.setUI(new DZScrollBarUI2());

        JScrollBar hScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        hScrollBar.setUI(new DZScrollBarUI2());

        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUI(new DZScrollBarUI2());
        sp.getHorizontalScrollBar().setUI(new DZScrollBarUI2());
        //sp.setVerticalScrollBar(vScrollBar);
        //sp.setHorizontalScrollBar(hScrollBar);

        f.getContentPane().add(sp);
        f.setSize(400,400);
        f.setVisible(true);


    }

    protected Color THUMB_LIGHT_GRADIENT = new Color(148, 167, 189);
    protected Color THUMB_DARK_GRADIENT = new Color(95, 118, 151);
    protected Color THUMB_OUTLINE_COLOR = new Color(64, 86, 118);

    protected GradientPaint verticalThumbGradient = new GradientPaint(0,0,THUMB_LIGHT_GRADIENT, 16,0, THUMB_DARK_GRADIENT);
    protected GradientPaint horizontalThumbGradient = new GradientPaint(0,0,THUMB_LIGHT_GRADIENT, 0,16, THUMB_DARK_GRADIENT);


    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new MyArrowButton(orientation);
    //super.createIncreaseButton(orientation);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new MyArrowButton(orientation);
    }

    /**
     * Paint the Track
     * @param g
     * @param c
     * @param trackBounds
     */
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = DrawUtilities.getGraphics2DWithAntiAliasing(g);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, trackBounds.width, trackBounds.height);
        g2.setColor(Color.BLACK);
        g2.drawLine(0, trackBounds.y, 0, trackBounds.height);
    }


    /**
     * Paint the Thumb Control
     * @param g
     * @param c
     * @param thumbBounds
     */
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {

        Graphics2D g2 = DrawUtilities.getGraphics2DWithAntiAliasing(g);

        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        int w = thumbBounds.width;
        int h = thumbBounds.height;
        int arc = 0;
        GradientPaint gradientPaint = null;
        
        if(scrollbar.getOrientation() == SwingConstants.VERTICAL) {
            gradientPaint = verticalThumbGradient;
            arc = w;
        }
        else {
            gradientPaint = horizontalThumbGradient;
            arc = h;
        }

        g2.translate(thumbBounds.x, thumbBounds.y);

        //g.setColor(thumbDarkShadowColor);
        //g.drawRect(0, 0, w - 1, h - 1);
        g2.setPaint(gradientPaint);
        g2.fillRoundRect(1, 0, w - 2, h - 1, arc, arc);

        g2.setPaint(THUMB_OUTLINE_COLOR);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(1, 0, w - 2, h - 1, arc, arc);

        //g.setColor(thumbHighlightColor);
        //g.drawLine(1, 1, 1, h - 2);
        //g.drawLine(2, 1, w - 3, 1);

        //g.setColor(thumbLightShadowColor);
        //g.drawLine(2, h - 2, w - 2, h - 2);
        //g.drawLine(w - 2, 1, w - 2, h - 3);

        g2.translate(-thumbBounds.x, -thumbBounds.y);
    }

    protected class MyArrowButton extends JButton {

        protected Color LIGHT_EDGE_COLOR = new Color(169, 169, 169);
        protected Color DARK_EDGE_COLOR = new Color(64, 64, 64);
        protected Color LIGHT_GRADIENT = new Color(214, 214, 214);
        protected Color DARK_GRADIENT = new Color(171, 171, 171);
        protected Color PRESSED_LIGHT_GRADIENT = new Color(148, 167, 189);
        protected Color PRESSED_DARK_GRADIENT = new Color(95, 118, 151);
        protected GradientPaint unPressedGradient;
        protected GradientPaint pressedGradient;
        protected int orientation;

        protected Polygon arrowShapeSouth = new Polygon(new int[]{0, 3, 6}, new int[]{0, 5, 0}, 3);
        protected Polygon arrowShapeNorth = new Polygon(new int[]{0, 3, 6}, new int[]{5, 0, 5}, 3);

        public MyArrowButton(int orientation) {
            this.orientation = orientation;
            initGradients();

        }

        protected void initGradients() {
            Dimension ps = getPreferredSize();

            //NORTH or SOUTH
            if (orientation == SwingConstants.NORTH || orientation == SwingConstants.SOUTH) {
                unPressedGradient = new GradientPaint(0, 0, LIGHT_GRADIENT, ps.width, 0, DARK_GRADIENT);
                pressedGradient = new GradientPaint(0, 0, PRESSED_LIGHT_GRADIENT, ps.width, 0, PRESSED_DARK_GRADIENT);
            }
            //EAST or WEST
            else {
                unPressedGradient = new GradientPaint(0, 0, LIGHT_GRADIENT, 0, ps.height, DARK_GRADIENT);
                pressedGradient = new GradientPaint(0, 0, PRESSED_LIGHT_GRADIENT, 0, ps.height, PRESSED_DARK_GRADIENT);
            }
        }

        /**
         * Return true if button is pressed
         * @return
         */
        public boolean isPressed() {
            return getModel().isPressed();
        }

        @Override
        public Dimension getPreferredSize() {

            if (orientation == SwingConstants.NORTH || orientation == SwingConstants.SOUTH) {
                return new Dimension(16, 22);
            }
            else {
                return new Dimension(22, 16);
            }
        }

        /**
         * Paint this button
         * @param g
         */
        @Override
        protected void paintComponent(Graphics g) {

            switch (orientation) {
                case SwingConstants.NORTH:
                    paintNorthSouth(g);

                case SwingConstants.SOUTH:
                    paintNorthSouth(g);

                case SwingConstants.EAST:
                    paintEast(g);

                case SwingConstants.WEST:
                    paintWest(g);
            }
        }

        /**
         * Paint a North or South orientation
         * @param g
         */
        protected void paintNorthSouth(Graphics g) {

            Graphics2D g2 = DrawUtilities.getGraphics2DWithAntiAliasing(g);

            if (isPressed()) {
                g2.setPaint(pressedGradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            else {
                g2.setPaint(unPressedGradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            //light edge
            g2.setColor(LIGHT_EDGE_COLOR);
            g2.drawLine(0, 0, 0, getHeight() - 1);

            //dark edge
            g2.setColor(DARK_EDGE_COLOR);
            g2.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
            g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight() - 1);

            if(orientation == SwingConstants.SOUTH) {

                double x = (getWidth() / 2) - (arrowShapeSouth.getBounds().getWidth() / 2);
                double y = (getHeight() / 2) - (arrowShapeSouth.getBounds().getHeight() / 2);

                g2.setColor(Color.BLACK);
                g2.translate(x, y);
                g2.fill(arrowShapeSouth);
            }

            else {
                double x = (getWidth() / 2) - (arrowShapeNorth.getBounds().getWidth() / 2);
                double y = (getHeight() / 2) - (arrowShapeNorth.getBounds().getHeight() / 2);

                g2.setColor(Color.BLACK);
                g2.translate(x, y);
                g2.fill(arrowShapeNorth);
            }

        }

        protected void paintEast(Graphics g) {
        }

        protected void paintWest(Graphics g) {
        }
    }
}
