/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.widget.laf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ScrollBarUI;

/**
 *
 * @author deane
 */
public class DZScrollBarUI extends ScrollBarUI implements ChangeListener, ComponentListener {

    public static void main(String args[]) {
        JFrame f= new JFrame("test");
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JTextArea ta = new JTextArea();
        DZScrollBarUI ui = new DZScrollBarUI();

        JScrollBar vScrollBar = new JScrollBar(JScrollBar.VERTICAL);
        vScrollBar.setUI(ui);

        JScrollBar hScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        hScrollBar.setUI(ui);

        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(null);
        sp.setVerticalScrollBar(vScrollBar);
        sp.setHorizontalScrollBar(hScrollBar);

        f.getContentPane().add(sp);
        f.setVisible(true);
    }

    protected int maxWidthHeight = 20;
    protected GradientPaint verticalTrackPaint = new GradientPaint(0,0, Color.RED, maxWidthHeight, 0, Color.BLUE);
    protected GradientPaint horizontalTrackPaint = new GradientPaint(0,0, Color.RED, 0, maxWidthHeight, Color.BLUE);
    protected JScrollBar scrollBar;

    /**
     * Get the PreferredSize of the Component
     * @param c
     * @return
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension ps = super.getPreferredSize(c);

        if(isHorizontalScroll(c)) {
            ps = new Dimension(c.getWidth(), maxWidthHeight);
        }
        else {
            ps = new Dimension(maxWidthHeight, c.getHeight());
        }
        
        return ps;
    }

    /**
     * Install the UI into the scrollBar
     */
    @Override
    public void installUI(JComponent c) {

        scrollBar = castToScrollBar(c);

        scrollBar.setLayout(null);
        scrollBar.addComponentListener(this);
        scrollBar.getModel().addChangeListener(this);

                //this will add the scroll buttons to the scrollbar
        ScrollButton sbtn1 = new ScrollButton(scrollBar);
        ScrollButton sbtn2 = new ScrollButton(scrollBar);
        ThumbComponent thmComp = new ThumbComponent(scrollBar);
        //Note: thumbComp gets its orientaion from the scrollbar in the
        //ThumbComponent constructor

        if(isVerticalScroll(scrollBar)) {
            sbtn1.setType(SwingConstants.NORTH);
            sbtn2.setType(SwingConstants.SOUTH);
        }
        else {
            sbtn1.setType(SwingConstants.WEST);
            sbtn2.setType(SwingConstants.EAST);
        }
    }

    /**
     * Uninstall the UI from the scrollBar
     * @param c
     */
    @Override
    public void uninstallUI(JComponent c) {

        scrollBar = castToScrollBar(c);

        scrollBar.setLayout(null);
        scrollBar.removeComponentListener(this);
        scrollBar.getModel().removeChangeListener(this);

        //find scroll button and thumbcomponents
        ArrayList<Component> removeCompList = new ArrayList<Component>();
        for(Component childC: scrollBar.getComponents()) {
            if(childC instanceof ScrollButton ||
                    childC instanceof ThumbComponent) {
                removeCompList.add(childC);
            }
        }

        //remove the components
        for(Component childC : removeCompList) {
            scrollBar.remove(childC);
        }

    }

    /**
     * Called when the model is updated
     */
    private void modelUpdated() {
        BoundedRangeModel model = scrollBar.getModel();
        int value = model.getValue();
        int extent = model.getExtent();
        int min = model.getMinimum();
        int max = model.getMaximum();

        String msg = "value={0}, exten={1}, min={2}, max={3}";
        System.out.println(MessageFormat.format(msg, value, extent, min, max));
    }

    //LISTENER Methods

    public void stateChanged(ChangeEvent e) {
        if(e.getSource() == scrollBar.getModel()) {
            modelUpdated();
        }
    }

    public void componentHidden(ComponentEvent e) {
        //not used
    }

    public void componentMoved(ComponentEvent e) {
        //not used
    }

    public void componentShown(ComponentEvent e) {
        //not used
    }

    public void componentResized(ComponentEvent e) {
        Component src = e.getComponent();
        if(src instanceof JScrollBar) {
            scrollBarResized(castToScrollBar(src));
        }
    }

    protected void scrollBarResized(JScrollBar sbar) {
        for(Component c : sbar.getComponents()) {
            if(c instanceof ScrollButton) {
                ((ScrollButton)c).updateLocation();
            }
        }
    }

    /**
     * Return true if Horizontal
     * @param c
     * @return
     */
    protected boolean isHorizontalScroll(JComponent c) {
        JScrollBar sbar = castToScrollBar(c);
        return sbar.getOrientation() == JScrollBar.HORIZONTAL;
    }

    /**
     * Return true if Vertical
     * @param c
     * @return
     */
    protected boolean isVerticalScroll(JComponent c) {
        JScrollBar sbar = castToScrollBar(c);
        return sbar.getOrientation() == JScrollBar.VERTICAL;
    }

    /**
     * Cast a JComponent to a JScrollBar
     * @param c
     * @return
     */
    protected JScrollBar castToScrollBar(Component c) {
        return (JScrollBar)c;
    }

    protected Rectangle getTrackBounds(Rectangle sbBounds) {
        sbBounds.x = 0;
        sbBounds.y = 0;
        
        return sbBounds;
    }

    @Override
    public void update(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D)g;
        JScrollBar sbar = castToScrollBar(c);
        paintTrack(g2, sbar, getTrackBounds(sbar.getBounds()));
    }
   
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        
        Graphics2D g2 = (Graphics2D)g;
        JScrollBar sbar = (JScrollBar)c;
        
        if(isHorizontalScroll(c)) {
            paintHorizontalTrack(g2, sbar, trackBounds);
        }
        else {
            paintVerticalTrack(g2, sbar, trackBounds);
        }
    }

    /**
     * Paint Horizontal Track
     * @param g2
     * @param sbar
     * @param trackBounds
     */
    protected void paintHorizontalTrack(Graphics2D g2, JScrollBar sbar, Rectangle trackBounds) {

        int x = trackBounds.x;
        int y = trackBounds.y;
        int w = trackBounds.width;
        int h = trackBounds.height;

        g2.setPaint(horizontalTrackPaint);
        g2.fillRect(x, y, w, h);
    }

    /**
     * Paint Vertical Track
     * @param g2
     * @param sbar
     * @param trackBounds
     */
    protected void paintVerticalTrack(Graphics2D g2, JScrollBar sbar, Rectangle trackBounds) {

        int x = trackBounds.x;
        int y = trackBounds.y;
        int w = trackBounds.width;
        int h = trackBounds.height;
        
        g2.setPaint(verticalTrackPaint);
        g2.fillRect(x, y, w, h);
    }

    //-------------------------------------------------------------------------

    /**
     * ScrollButton provides a component for the north, south, east, or west buttons
     * on a scroll bar, that move the scroll up or down or side to side.
     */
    private class ScrollButton extends JComponent implements MouseListener {

        private JScrollBar scrollBar;
        private int type;
        private Timer pressedTimer;

        /**
         * Constructor
         * @param sbar
         */
        public ScrollButton(JScrollBar sbar) {
            scrollBar = sbar;
            scrollBar.add(this);
            setPreferredSize(new Dimension(maxWidthHeight, maxWidthHeight));
            setSize(getPreferredSize());
            this.addMouseListener(this);
            
            //timer to process when mouse is down over a button
            pressedTimer = new Timer(250, new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    processPressedTimerEvent();
                }
            });
        }

        /**
         * set the type, SwingConstants.NORTH, SOUTH, EAST, WEST
         * @param type
         */
        public void setType(int type) {
            if(type != SwingConstants.NORTH &&
                    type != SwingConstants.SOUTH &&
                    type != SwingConstants.EAST &&
                    type != SwingConstants.WEST) {
                throw new IllegalArgumentException("invalid type");
            }

            this.type = type;

            //update the location
            updateLocation();
        }

        /**
         * Set the correct location for the buttons based on the type
         */
        private void updateLocation() {
            switch(type) {
                case SwingConstants.NORTH:
                    setLocation(0,0);
                    break;
                case SwingConstants.SOUTH:
                    setLocation(0, scrollBar.getHeight()-getHeight());
                    break;
                case SwingConstants.EAST:
                    setLocation(scrollBar.getWidth() - getWidth(), 0);
                    break;
                case SwingConstants.WEST:
                    setLocation(0, 0);
                    break;
            }
        }

        /**
         * Paint the Component
         * @param g
         */
        @Override
        public void paintComponent(Graphics g) {
            g.setColor(Color.GRAY);
            g.fillRect(0,0,getWidth(), getHeight());
        }

        //Mouse Listener Methods

        public void mouseClicked(MouseEvent e) {
            processPressedTimerEvent();
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            processMousePresssed();
        }

        public void mouseReleased(MouseEvent e) {
            processMouseReleased();
        }

        /**
         * Process a mouse pressed event
         */
        protected void processMousePresssed() {
            pressedTimer.start();
        }

        protected void processMouseReleased() {
            pressedTimer.stop();
        }

        protected void processPressedTimerEvent() {

            int v = scrollBar.getValue();
            int bi = scrollBar.getBlockIncrement();
            if(type == SwingConstants.NORTH) {
                System.out.println("North Pressed Down:"+System.currentTimeMillis());
                scrollBar.setValue(v - bi);
            }
            else if(type == SwingConstants.SOUTH) {
                System.out.println("South Pressed Down:"+System.currentTimeMillis());
                scrollBar.setValue(v + bi);
            }
            else if(type == SwingConstants.WEST) {
                System.out.println("West Pressed Down:"+System.currentTimeMillis());
                scrollBar.setValue(v - bi);
            }
            else if(type == SwingConstants.EAST) {
                System.out.println("East Pressed Down:"+System.currentTimeMillis());
                scrollBar.setValue(v + bi);
            }
        }

    }

    //-------------------------------------------------------------------------

    private class ThumbComponent extends JComponent {

        private JScrollBar scrollBar;

        public ThumbComponent(JScrollBar sbar) {
            scrollBar = sbar;
            scrollBar.add(this);
        }

    }
}
