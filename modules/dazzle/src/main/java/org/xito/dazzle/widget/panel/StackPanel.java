package org.xito.dazzle.widget.panel;

import org.xito.dazzle.widget.DefaultStyle;
import org.xito.dazzle.*;
import org.xito.dazzle.widget.button.ImageButton;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import org.xito.dazzle.animation.timing.Animator;
import org.xito.dazzle.animation.timing.TimingTargetAdapter;

/**
 * Provides a Vertical StackedPanel. This components added to the StackPanel as items are vertically
 * layed out with dividers. If this StackedPanel is placed in a SplitPane then split controls will appear
 * on the item panels.
 *
 * @author deane
 */
public class StackPanel extends JPanel {

    // Serial Version ID
    private static final long serialVersionUID = 1L;

    public static final String HEADER_BORDER_COLOR_KEY = "stackedpanel.header.border.color.key";
    public static final String HEADER_TITLE_FONT_KEY = "stackedpanel.header.title.font.key";
    public static final String HEADER_TITLE_COLOR_KEY = "stackedpanel.header.title.color.key";
    public static final String HEADER_TITLE_BEVEL_COLOR_KEY = "stackedpanel.header.title.bebel.color.key";
    public static final String HEADER_TOP_GRADIENT_KEY = "stackedpanel.header.top.gradient.key";
    public static final String HEADER_BOTTOM_GRADIENT_KEY = "stackedpanel.header.bottom.gradient.key";
    public static final String BACKGROUND_KEY = "stackedpanel.background.key";
    public static final String SPLIT_BORDER_COLOR_KEY = "stackedpanel.split.border.color.key";

    protected Font headerFont = null;
    protected Color headerTitleColor = null;
    protected Color headerTitleBevelColor = null;
    protected Color headerTopGradColor = null;
    protected Color headerBottomGradColor = null;
    protected Color splitBorderColor = null;
    protected Color backgroundColor = null;
    protected Color headerBorderColor = null;
    protected UIDefaults styleMap;
    protected ArrayList panelList = new ArrayList();


    public static final int HEADER_HEIGHT = 20;
    public static final int MINIMUM_WIDTH = 70;
    private ItemPanel headItem;
    private boolean enableSplitControl = true;
    private boolean accordionType = false;

    /**
     * Default Constructor
     */
    public StackPanel() {
        init();
    }

    public StackPanel(boolean accordionType) {
        this.accordionType = accordionType;
        init();
    }

    /**
     * Style Constructor
     */
    public StackPanel(UIDefaults styleMap, boolean accordionType) {
        this.styleMap = styleMap;
        this.accordionType = accordionType;
        init();
    }

    /**
     * Initialize the Component
     */
    private void init() {
        initStyle();
        setOpaque(true);
        setBackground(backgroundColor);
        setMinimumSize(new Dimension(100, 100));
        setLayout(new StackPanelLayout());
    }

    protected void initDefaultStyle() {

        if (this.styleMap == null) {
            this.styleMap = DefaultStyle.getDefaults();
        }

        //background
        if (!styleMap.containsKey(BACKGROUND_KEY))
            styleMap.put(BACKGROUND_KEY, new Color(209, 215, 226));

        //header title font
        if (!styleMap.containsKey(HEADER_TITLE_FONT_KEY))
            styleMap.put(HEADER_TITLE_FONT_KEY, styleMap.getFont(DefaultStyle.CTRL_TITLE_FONT_KEY));

        //header title color
        if (!styleMap.containsKey(HEADER_TITLE_COLOR_KEY))
            styleMap.put(HEADER_TITLE_COLOR_KEY, DefaultStyle.STACKED_PANEL_HEADER_COLOR);

        //header title bevel color
        if (!styleMap.containsKey(HEADER_TITLE_BEVEL_COLOR_KEY))
            styleMap.put(HEADER_TITLE_BEVEL_COLOR_KEY, DefaultStyle.CTRL_BEVEL_HIGHLIGHT);

        //header border color
        if (!styleMap.containsKey(HEADER_BORDER_COLOR_KEY))
            styleMap.put(HEADER_BORDER_COLOR_KEY, styleMap.getColor(DefaultStyle.CTRL_BORDER_COLOR_KEY));

        //header top gradient
        if (!styleMap.containsKey(HEADER_TOP_GRADIENT_KEY))
            styleMap.put(HEADER_TOP_GRADIENT_KEY, DefaultStyle.STACKED_PANEL_HEADER_TOP);

        //header bottom gradient
        if (!styleMap.containsKey(HEADER_BOTTOM_GRADIENT_KEY))
            styleMap.put(HEADER_BOTTOM_GRADIENT_KEY, DefaultStyle.STACKED_PANEL_HEADER_BOTTOM);

        //split border color
        if (!styleMap.containsKey(SPLIT_BORDER_COLOR_KEY)) {
            styleMap.put(SPLIT_BORDER_COLOR_KEY, styleMap.getColor(DefaultStyle.CTRL_BORDER_COLOR_KEY));
        }

    }

    /**
     * Initialize the Style
     */
    protected void initStyle() {

        initDefaultStyle();

        //Read Style Colors
        backgroundColor = styleMap.getColor(BACKGROUND_KEY);
        headerFont = styleMap.getFont(HEADER_TITLE_FONT_KEY);
        headerTitleColor = styleMap.getColor(HEADER_TITLE_COLOR_KEY);
        headerTitleBevelColor = styleMap.getColor(HEADER_TITLE_BEVEL_COLOR_KEY);
        headerTopGradColor = styleMap.getColor(HEADER_TOP_GRADIENT_KEY);
        headerBottomGradColor = styleMap.getColor(HEADER_BOTTOM_GRADIENT_KEY);
        headerBorderColor = styleMap.getColor(HEADER_BORDER_COLOR_KEY);
        splitBorderColor = styleMap.getColor(SPLIT_BORDER_COLOR_KEY);
    }

    /**
     * Override addNotify so that we will know when we are placed in a SplitPane
     */
    public void addNotify() {
        super.addNotify();
        if (getParent() instanceof JSplitPane) {
            setEnableSplitControl(true);
        } else {
            setEnableSplitControl(false);
        }
    }

    private JSplitPane getParentSplitPane() {

        if (getParent() instanceof JSplitPane) {
            return (JSplitPane) getParent();
        } else {
            return null;
        }
    }

    public void setEnableSplitControl(boolean b) {
        enableSplitControl = b;
        // check to see if we can enable the split control
        JSplitPane parent = null;
        if (b) {
            parent = getParentSplitPane();
        }

        //configure split
        if (parent != null) {
            parent.setDividerSize(0);
            parent.setContinuousLayout(true);
            parent.setBorder(null);
        }

        // hide/show the split control on embedded panels
        ItemPanel item = headItem;
        while (item != null) {
            item.setSplitPane(parent);
            item = item.nextItem();
        }
    }

    /**
     * Add an Item component to the StackPanel
     *
     * @param name
     * @param comp
     */
    public void addItem(String name, JComponent comp) {

        ItemPanel itemPanel = new ItemPanel(getTailItem(), name, comp);
        int y = 0;
        if (getTailItem() != null) {
            y = itemPanel.previousItem.getY() + itemPanel.previousItem.getHeight();
        }

        itemPanel.setBounds(0, y, 200, 100);
        add(itemPanel);

        if (headItem == null) headItem = itemPanel;

        setEnableSplitControl(enableSplitControl);
        revalidate();
        repaint();
    }

    public void moveItem(String name, int y) {

        //if we don't have any items or the headItem is the one to collapse
        //just return
        if(headItem == null) return;
        if(headItem.getName().equals(name)) return;

        //First locate the item
        ItemPanel itemPanel = findItemPanel(name);

        int currentY = itemPanel.getY();
        int deltaY = y - currentY;

        itemPanel.move(deltaY);
    }

    /**
     * Collapse an Item
     *
     * @param name
     */
    public void collapseItem(String name) {

        //if we don't have any items or the headItem is the one to collapse
        //just return
        if(headItem == null) return;
        if(headItem.getName().equals(name)) return;

        //First locate the item
        ItemPanel itemPanel = findItemPanel(name);
        itemPanel.collapse();
    }

    public ItemPanel findItemPanel(String name) {
        if(headItem == null) return null;

        ItemPanel itemPanel = headItem;
        while(itemPanel != null) {
            if(itemPanel.getName().equals(name))
                break;

            itemPanel = itemPanel.nextItem();
        }

        return itemPanel;
    }


    public ItemPanel getHeadItem() {
        return headItem;
    }

    public ItemPanel getTailItem() {
        if (headItem == null) return null;

        ItemPanel item = headItem;
        ItemPanel tailItem = null;
        while (item != null) {
            if (item.nextItem() == null) {
                return item;
            } else {
                item = item.nextItem();
            }
        }

        //shouldn't really happen
        return null;
    }

    /**
     * Get the number of items in this StackedPanel
     *
     * @return
     */
    public int getItemCount() {
        int i = 0;

        if (headItem == null) return i;

        ItemPanel item = headItem;
        ItemPanel tailItem = null;
        while (item != null) {
            i++;
            if (item.nextItem() == null) {
                return i;
            } else {
                item = item.nextItem();
            }
        }

        return i;
    }

    public void paint(Graphics g) {
        super.paint(g);

        //paint right border
        g.setColor(splitBorderColor);
        g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
    }

    public class MySplitPaneUI extends BasicSplitPaneUI {
        public MySplitPaneUI() {
            divider = new BasicSplitPaneDivider(this);
        }
    }

    //--------------------------------------------------------------

    /**
     * Internal Layout used to layout the item panels when the StackedPanel is resized
     */
    public static class StackPanelLayout implements LayoutManager {

        public StackPanelLayout() {

        }

        /* (non-Javadoc)
        * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
        */
        public void addLayoutComponent(String info, Component comp) {
            ItemPanel itemPanel = (ItemPanel) comp;
        }

        /* (non-Javadoc)
        * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
        */
        public void layoutContainer(Container parent) {

            StackPanel stackedPanel = (StackPanel) parent;
            //no items to resize
            if (stackedPanel.getItemCount() == 0) return;

            /*
            //Calculate total height of all items
            int stackedPanelHeight = stackedPanel.getHeight();
            int totalHeight = 0;
            ItemPanel item = stackedPanel.getHeadItem();
            while(item != null) {
               totalHeight = totalHeight + item.getHeight();
               item = item.nextItem();
            }

            //get deltaHeight to give to each item panel during resize
            int deltaHeight = 0;
            if(totalHeight > stackedPanelHeight) {
               deltaHeight = (stackedPanelHeight - totalHeight) / stackedPanel.getItemCount();
            }

            item = stackedPanel.getHeadItem();
            int y = 0;
            int height = 0;
            while(item != null) {
               if(item.previousItem == null) {
                  height = item.getHeight() + deltaHeight;
                  y = 0;
               }
               else {
                  y = item.getY() + deltaHeight;
               }
               item.setLocation(0, y);
               item.setSize(stackedPanel.getWidth(), height);
               item = item.nextItem();
            }
            */

            //resize only first component
            int y = stackedPanel.getHeight();
            ItemPanel item = stackedPanel.getTailItem();
            if (item == null) return;
            while (item != null) {
                int height = item.getHeight();
                if (item.previousItem == null) {
                    height = y;
                    y = 0;
                } else {
                    y = y - item.getHeight();
                }
                item.setLocation(0, y);
                //we width-1 because we want 1 pixel left for the vertical left line
                item.setSize(stackedPanel.getWidth() - 1, height);
                item = item.previousItem();
            }
        }

        /* (non-Javadoc)
        * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
        */
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(StackPanel.MINIMUM_WIDTH, ((StackPanel) parent).getItemCount() * StackPanel.HEADER_HEIGHT);
        }

        /* (non-Javadoc)
        * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
        */
        public Dimension preferredLayoutSize(Container parent) {

            return new Dimension(0, 0);
        }

        /* (non-Javadoc)
        * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
        */
        public void removeLayoutComponent(Component comp) {
            //relink remaining items
        }
    }

    //------------------------------------------------------------

    /**
     * ItemPanel contains the comonent to place in the stacked panel along with a Header
     */
    public class ItemPanel extends JComponent {

        private ItemPanel nextItem;
        private ItemPanel previousItem;
        private ItemHeader header;
        private ArrayList listeners = new ArrayList();

        public ItemPanel(ItemPanel previousItem, String name, JComponent comp) {
            setName(name);
            setLayout(new BorderLayout());

            this.previousItem = previousItem;
            if (previousItem != null) previousItem.setNextItem(this);

            //need to set previousItem before creating header
            header = new ItemHeader(this);
            add(header, BorderLayout.NORTH);
            add(comp);
        }

        public ItemPanel nextItem() {
            return nextItem;
        }

        public void setPreviousItem(ItemPanel itemPanel) {
            previousItem = itemPanel;
        }

        public void setNextItem(ItemPanel itemPanel) {
            nextItem = itemPanel;
        }

        public ItemPanel previousItem() {
            return previousItem;
        }

        public Dimension getMinimumSize() {
            return header.getMinimumSize();
        }

        private void setSplitPane(JSplitPane splitPane) {
            header.setSplitPane(splitPane);
        }

        public void addItemPanelListener(ItemPanelListener listener) {
            listeners.add(listener);
        }

        private void fireCollapsedEvent() {
            //System.out.println(getName() + " collapsed");
            Iterator it = listeners.iterator();
            while(it.hasNext()) {
                ItemPanelListener listener = (ItemPanelListener)it.next();
                listener.itemCollapsed(this);
            }
        }

        /**
         * Collapse this item and animate its motion
         */
        public void collapse() {
            collapse(true);
        }

        /**
         * Collapse this item
         * @param animate true if the collapse movement should be animated
         */
        protected void collapse(boolean animate) {

            int y = getY();
            int deltaY = 0;
            if(nextItem != null) {
                deltaY = nextItem.getY() - y - header.getHeight();
            }
            else {
                deltaY = StackPanel.this.getHeight() - y - header.getHeight();
            }

            if(deltaY == 0 && nextItem != null) {
                nextItem.collapse(false);
                this.collapse(animate);
            }

            if(animate)
                animateMove(deltaY);
            else
                move(deltaY);

            fireCollapsedEvent();
            header.switchToExpandAction();
        }

        public void expand() {
            expand(true);
        }

        protected void expand(boolean animate) {

            int y = getY();
            int deltaY = 0;

            //can't expand because we are the top item
            if(previousItem == null) return;

            int expandRatio = (accordionType) ? 1 : 2;

            int newY = (previousItem.getY() + header.getHeight())
                    + ((previousItem.getHeight() - header.getHeight())/expandRatio);
            deltaY = -1 * (y - newY);

            if(deltaY > -100) {
                deltaY = -1 * (y - previousItem.getY() - header.getHeight());
            }

            if(deltaY == 0 && previousItem != headItem) {
                previousItem.expand(false);
                this.expand(animate);
            }

            if(animate)
                animateMove(deltaY);
            else
                move(deltaY);

            header.switchToCollapseAction();
        }

        protected void animateMove(final int deltaY) {

            Animator anim = new Animator(200, new TimingTargetAdapter(){

                int moved = 0;

                @Override
                public void timingEvent(float fraction) {
                    int step = (int)(deltaY * fraction) - moved;
                    moved=moved+step;
                    move(step);
                }

            });

            anim.setDeceleration(1.0f);
            anim.start();

        }

        /**
         *
         * @param deltaY - value to move up, positive value to move down
         */
        protected void move(int deltaY) {
            int x = getX();
            int y = getY() + deltaY;

            int w = getWidth();
            int h = getHeight() - deltaY;

            //adjust this panel
            setBounds(x, y, w, h);

            //adjust previous panel
            if (previousItem != null) {
                x = previousItem.getX();
                y = previousItem.getY() - deltaY;
                w = previousItem.getWidth();
                h = previousItem.getHeight() + deltaY;

                previousItem.setBounds(x, y, w, h);
            }

            StackPanel.this.revalidate();
            StackPanel.this.repaint();
        }
    }

    /**
     * Provides an Item Header
     *
     * @author deane
     */
    public class ItemHeader extends JComponent {

        private ItemPanel itemPanel;
        private Dimension preferredSize;
        private SplitPaneControl splitCtrl;
        private ImageButton toggleBtn;
        private boolean collapseFlag = true;

        public ItemHeader(ItemPanel itemPanel) {
            this.itemPanel = itemPanel;
            setLayout(new BorderLayout());
            
            //setup mouse support
            MyMouseListener ml = new MyMouseListener(this);
            if(!accordionType) {
                addMouseMotionListener(ml);
            }
            addMouseListener(ml);

            //listen for component changes
            itemPanel.addComponentListener(new ComponentAdapter() {

                public void componentResized(ComponentEvent componentEvent) {
                    Component comp = componentEvent.getComponent();
                    ItemPanel panel = (ItemPanel)comp;
                    if(panel.getHeight() <= HEADER_HEIGHT) {
                        switchToExpandAction();
                    }

                    if(panel.previousItem != null && panel.previousItem.getHeight() <= HEADER_HEIGHT) {
                        switchToCollapseAction();
                    }
                    
                }
            });

            //setup collapse button
            toggleBtn = new ImageButton(ImageManager.class.getResource("org.xito.launcher.images/collapse.png"),
                    ImageManager.class.getResource("org.xito.launcher.images/collapse_pressed.png"));
            toggleBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    toggleAction();
                }
            });
            toggleBtn.setName(itemPanel.getName() + ":" + "toggleBtn");

            int border = (HEADER_HEIGHT - toggleBtn.getPreferredSize().height) / 2;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,0));
            buttonPanel.setBorder(new EmptyBorder(border, border, border, border));
            buttonPanel.setOpaque(false);
            buttonPanel.add(toggleBtn);

            //if not head item then setup drag cursor and collapse button
            if (itemPanel.previousItem != null) {
                add(buttonPanel, BorderLayout.WEST);
                if(!accordionType) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                }
            } else {
                setCursor(null);
            }

            splitCtrl = new SplitPaneControl(null);
            add(splitCtrl, BorderLayout.EAST);
        }

        private void switchToCollapseAction() {
            collapseFlag = true;
            toggleBtn.setIcons(
                    new ImageIcon(ImageManager.class.getResource("org.xito.launcher.images/collapse.png")),
                    new ImageIcon(ImageManager.class.getResource("org.xito.launcher.images/collapse_pressed.png")), null);
        }

        private void switchToExpandAction() {
            collapseFlag = false;
            toggleBtn.setIcons(
                    new ImageIcon(ImageManager.class.getResource("org.xito.launcher.images/expand.png")),
                    new ImageIcon(ImageManager.class.getResource("org.xito.launcher.images/expand_pressed.png")), null);
        }

        private void toggleAction() {
            if(collapseFlag) {
                ItemHeader.this.itemPanel.collapse();
            }
            else {
                ItemHeader.this.itemPanel.expand();
            }
        }

        private void setSplitPane(JSplitPane splitPane) {
            splitCtrl.setSplitPane(splitPane);
        }

        public Dimension getPreferredSize() {
            return new Dimension(20, HEADER_HEIGHT);
        }

        public Dimension getMinimumSize() {
            return new Dimension(20, HEADER_HEIGHT);
        }

        public Dimension getMaximumSize() {
            return new Dimension(20, HEADER_HEIGHT);
        }

        public String getTitle() {
            return itemPanel.getName();
        }

        /**
         * Paint the Header Panel
         *
         * @param g
         */
        public void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;

            // get title font info
            FontRenderContext fc = g2.getFontRenderContext();
            LineMetrics lm = headerFont.getLineMetrics(getTitle(), fc);
            Rectangle2D titleBounds = headerFont.getStringBounds(getTitle(), fc);
            setFont(headerFont);

            // calc preferred size
            if (preferredSize == null) {
                preferredSize = new Dimension((int) (titleBounds.getWidth() + 10), (int) (titleBounds.getHeight() + 4));
                setPreferredSize(preferredSize);
                this.setSize(getWidth(), preferredSize.height);
            }

            // Draw Background
            GradientPaint grad = new GradientPaint(0, 0, headerTopGradColor, 0, getHeight(), headerBottomGradColor);
            g2.setPaint(grad);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Draw Border
            g2.setPaint(headerBorderColor);
            g2.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);

            // Draw Title - Bevel First
            g2.setColor(headerTitleBevelColor);
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            float tx = (float) (cx - titleBounds.getCenterX());
            float ty = (float) (cy + lm.getHeight() / 3);
            g2.drawString(getTitle(), tx, ty);

            // ty = ty - (float)(lm.getHeight()/10);
            g2.setColor(headerTitleColor);
            g2.drawString(getTitle(), tx - 1, ty - 1);
        }

    }

    private class MyMouseListener extends MouseAdapter implements MouseMotionListener {
        private ItemHeader header;

        private int startH = 0;
        private int startY = 0;
        private int startDragY = 0;
        private int prevStartY = 0;
        private int prevStartH = 0;

        public MyMouseListener(ItemHeader header) {
            this.header = header;
        }

        public ItemPanel getItemPanel() {
            return this.header.itemPanel;
        }

        public void mouseMoved(MouseEvent e) {

        }

        public void mouseDragged(MouseEvent e) {
            //this item panel is the head item so just return
            //we can't move the head item
            if (getItemPanel().previousItem == null) {
                return;
            }

            Point loc = e.getPoint();
            SwingUtilities.convertPointToScreen(loc, (Component) e.getSource());

            int deltaY = loc.y - startDragY;

            //make sure deltaY isn't too small
            if ((startY + deltaY) < (getItemPanel().previousItem.getY() + HEADER_HEIGHT)) {
                deltaY = (getItemPanel().previousItem.getY() + HEADER_HEIGHT) - startY;
            }

            //make sure deltaY isn't too big
            if (getItemPanel().nextItem == null) {
                if ((startY + deltaY) > StackPanel.this.getHeight() - HEADER_HEIGHT) {
                    deltaY = (StackPanel.this.getHeight() - HEADER_HEIGHT) - startY;
                }
            } else if ((startY + deltaY) > (getItemPanel().nextItem.getY() - HEADER_HEIGHT)) {
                deltaY = (getItemPanel().nextItem.getY() - HEADER_HEIGHT) - startY;
            }

            int x = getItemPanel().getX();
            int y = startY + deltaY;

            int w = getItemPanel().getWidth();
            int h = startH - deltaY;

            //adjust this panel
            getItemPanel().setBounds(x, y, w, h);

            //adjust previous panel
            if (getItemPanel().previousItem != null) {
                x = getItemPanel().previousItem.getX();
                y = prevStartY - deltaY;
                w = getItemPanel().previousItem.getWidth();
                h = prevStartH + deltaY;

                getItemPanel().previousItem.setBounds(x, y, w, h);
            }

            //StackedPanel2.this.getLayout().layoutContainer(StackedPanel2.this);

            StackPanel.this.revalidate();
            //StackedPanel2.this.invalidate();
            StackPanel.this.repaint();

            //if bottom component
            if(getItemPanel().nextItem == null) {
                if((getItemPanel().getY() + HEADER_HEIGHT) == StackPanel.this.getHeight()) {
                    getItemPanel().fireCollapsedEvent();
                }
            }
            else {
                if((getItemPanel().getY() + HEADER_HEIGHT) == getItemPanel().nextItem.getY()) {
                    getItemPanel().fireCollapsedEvent();
                }
            }


        }

        public void mousePressed(MouseEvent e) {

            Point loc = e.getPoint();
            SwingUtilities.convertPointToScreen(loc, (Component) e.getSource());
            startDragY = loc.y;

            startY = this.header.itemPanel.getY();
            startH = this.header.itemPanel.getHeight();

            //get the location of the Prev panels Y and Height
            //also calculate minimum DeltaY
            if (this.header.itemPanel.previousItem != null) {
                prevStartY = this.header.itemPanel.previousItem.getY();
                prevStartH = this.header.itemPanel.previousItem.getHeight();
            }
        }

        public void mouseReleased(MouseEvent e) {
            //this item panel is the head item so just return
            if (getItemPanel().previousItem == null) {
                return;
            }
        }

        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2)
              header.toggleAction();
        }
   }

    public interface ItemPanelListener {

        public void itemCollapsed(ItemPanel panel);
    }

}
