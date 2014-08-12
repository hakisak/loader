// Copyright 2007 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.dcf;

import java.util.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;

import org.xito.blx.*;
import org.xito.dazzle.widget.DefaultStyle;
import org.xito.dcf.property.*;
import org.xito.dcf.dnd.*;
import org.xito.dialog.TableLayout;

/**
 * The DCMenuItem is used internally by the DCMenu to display an Action on the Menu.
 * The DCMenuItem is also used to display SubMenus in DCMenus.
 *
 *
 *
 */
public class DCMenuItem extends JComponent implements PropertyChangeListener, MouseListener {
   
   public static final int EAST = 0;
   public static final int WEST = 1;
      
   protected static ImageIcon subMenuArrow_east;
   protected static ImageIcon subMenuArrow_west;
   protected MenuItemBorder defaultBorder = new MenuItemBorder();
   protected TableLayout layout;
   protected JLabel textLabel;
   protected JLabel iconLabel;
   protected Action action;
   protected DCPopupMenu actionItemMenu;
   protected JMenuItem propertiesMI;
   protected MyMenuListener menuListener;
   
   protected ArrayList actionListeners = new ArrayList();
   protected DCMenu parentMenu;
   protected DCMenu menu;
   protected JLabel arrowLblLeft;
   protected JLabel arrowLblRight;
   protected JProgressBar progressBar;
   
   protected GradientPaint gradientBG;
   protected Color topColor;
   protected Color bottomColor;
   
   protected Color textColor;
   protected Color selectedColor;
   protected Color selectedBGColor;
   protected Color borderColor;
   protected boolean selected = false;
   
   protected DragSource dragSource;
   protected MyDragListenerAdapter dragListenerAdapter;
   protected DragGestureRecognizer dragRecognizer;
   
   private boolean showBusy_flag;
   
   /**
    * Static initializers
    */
   static {
      subMenuArrow_east = new ImageIcon(DCMenuItem.class.getResource("/org/xito/launcher/images/menu_right.gif"));
      subMenuArrow_west = new ImageIcon(DCMenuItem.class.getResource("/org/xito/launcher/images/menu_left.gif"));
   }
   
   /**
    * Create a Generic DCMenuItem
    */
   protected DCMenuItem() {
      init();
   }
   
   /**
    * Create a MenuItem for an Action
    */
   public DCMenuItem(Action action) {
      this.action = action;
      action.addPropertyChangeListener(this);
      init();
   }
   
   public Dimension getPreferredSize() {
      
      Dimension ps = textLabel.getPreferredSize();
      ps.width+=50;
      ps.height = super.getPreferredSize().height;
      return ps;
   }
   
   public Dimension getMaximumSize() {
      Dimension ps = getPreferredSize();
      ps.width = getParent().getWidth();
      return ps;
   }
   
   /**
    * Create a MenuItem for a Sub Menu
    */
   protected DCMenuItem(DCMenu menu) {
      //Create Action for this SubMenuItem
      this.menu = menu;
      this.action = new MenuAction();
      this.action.addPropertyChangeListener(this);
      
      init();
   }
   
   /**
    * Setup the Layout of the Menu Item
    */
   private void init() {
      //this.setPaintBorderLines(true);
      //layout
      layout = new TableLayout();
      TableLayout.Row row = new TableLayout.Row(20);
      
      row.addCol(new TableLayout.Column("<"));
      row.addCol(new TableLayout.Column("icon"));
      
      TableLayout.Column nameCol = new TableLayout.Column("text", .9999f);
      nameCol.hAlign = TableLayout.LEFT;
      row.addCol(nameCol);
      
      row.addCol(new TableLayout.Column(">"));
      layout.addRow(row);
      setLayout(layout);
      
      //Icon
      Icon icon = (Icon)action.getValue(Action.SMALL_ICON);
      iconLabel = new JLabel(icon);
      if(icon != null) {
         add("icon", iconLabel);
      }
      else {
         add("icon", Box.createHorizontalStrut(16));
      }
         
      
      //Text
      textLabel = new JLabel((String)action.getValue(Action.NAME));
      textLabel.setFont(UIManager.getDefaults().getFont("MenuItem.font"));
      add("text", textLabel);
      
      //Progress
      progressBar = new JProgressBar();
      progressBar.setIndeterminate(true);
      
      //arrows
      arrowLblLeft = new JLabel(subMenuArrow_west);
      arrowLblLeft.setVisible(false);
      arrowLblRight = new JLabel(subMenuArrow_east);
      arrowLblRight.setVisible(false);
            
      add("<", arrowLblLeft);
      add(">", arrowLblRight);
      
      setBorder(defaultBorder);
      
      //Mouse Listener
      addMouseListener(this);
      
      //Create Action Item Popup Menu
      actionItemMenu = new DCPopupMenu();
      menuListener = new MyMenuListener();
      propertiesMI = new JMenuItem("Properties");
      propertiesMI.addActionListener(menuListener);
      actionItemMenu.add(propertiesMI);
      
      //Setup Drag Source
      dragSource = (DragSource)DCDragSource.getDefaultDragSource();
      dragListenerAdapter = new MyDragListenerAdapter();
      dragRecognizer = dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, dragListenerAdapter);
      
      initStyle();
   }
   
   /**
    * Initialize the style of the menu item
    */
   private void initStyle() {
      topColor = DefaultStyle.getDefaults().getColor(DefaultStyle.CTRL_TOP_GRADIENT_COLOR_KEY);
      bottomColor = DefaultStyle.getDefaults().getColor(DefaultStyle.CTRL_BOTTOM_GRADIENT_COLOR_KEY);
      borderColor = DefaultStyle.getDefaults().getColor(DefaultStyle.CTRL_BORDER_COLOR_KEY);
      
      textColor = Color.BLACK;
      selectedColor = Color.WHITE;
      selectedBGColor = Color.BLACK;
      
      textLabel.setFont(DefaultStyle.getDefaults().getFont(DefaultStyle.MENU_FONT_KEY));
      
      setSelected(false);
   }
   
   /**
    * Set this MenuItems Parent Menu
    */
   public void setParentMenu(DCMenu parent) {
      parentMenu = parent;
   }
   
   public void setSelected(boolean selected) {
      
      this.selected = selected;
      if(selected) {
         textLabel.setForeground(selectedColor);
      }
      else {
         textLabel.setForeground(textColor);
      }
      
      repaint();
   }
   
   public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      
      if(selected) {
         g2.setColor(selectedBGColor);
      }
      else {
         gradientBG = new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor);
         g2.setPaint(gradientBG);
      }
      
      g2.fillRect(0, 0, getWidth(), getHeight());
   }
   
   /**
    * Return true if this Menu Item represents a SubMenu Item
    */
   public boolean isSubMenuItem() {
      return (menu != null);
   }
   
   //Return this SubMenuItems Menu
   public DCMenu getMenu() {
      return menu;
   }
   
   /**
    * Get the Action for this Menu Item
    */
   public Action getAction() {
      return action;
   }
   
   /**
    * Return a String representation of the MenuItem. This will return the
    * Actions Name Property.
    */
   public String toString() {
      return (String)action.getValue(Action.NAME);
   }
   
   /**
    * Add an ActionListener to this MenuItem
    */
   public void addActionListener(ActionListener listener) {
      actionListeners.add(listener);
   }
   
   /**
    * Remove an Action Listener from this MenuItem
    */
   public void removeActionListener(ActionListener listener) {
      actionListeners.remove(listener);
   }
   
   /**
    * Set the Direction EAST or WEST that this menu should pull out
    */
   public void setDirection(int direction) {
      //If not a sub menu just return
      if(menu == null) return;
      
      if(direction == EAST) {
         arrowLblLeft.setVisible(false);
         arrowLblRight.setVisible(true);
      }
      else if(direction == WEST) {
         arrowLblLeft.setVisible(true);
         arrowLblRight.setVisible(false);
      }
   }
   
   /**
    * Fire an Action Performed event to all Action Listeners
    */
   private synchronized void fireAction() {
      final ActionEvent evt = new ActionEvent(this, 1001, (String)action.getValue(Action.NAME));
      
      //Start a Thread for this Action
      Thread _t = new Thread(new Runnable(){
         public void run() {
            showBusy_flag = true;
            //Repaint the MenuItem
            invalidate();
            repaint();
            
            action.actionPerformed(evt);
            showBusy_flag = false;
            
            //Repaint the MenuItem
            invalidate();
            repaint();
            
            //notify all listeners
            Iterator listeners = actionListeners.iterator();
            while(listeners.hasNext()) {
               ((ActionListener)listeners.next()).actionPerformed(evt);
            }
         }
      });
      
      //Start the Thread
      try {
         _t.start();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }
   
   /**
    * Show this menu Items popup menu
    */
   public void showItemPopup(MouseEvent evt) {
      actionItemMenu.show(evt.getComponent(), evt.getX(), evt.getY());
   }
   
   /**
    * We are a property Change listener for the Action.
    * If any properties of the Action are changed we will know about it.
    */
   public void propertyChange(PropertyChangeEvent evt) {
      //Name Change
      if(evt.getSource()==action && evt.getPropertyName().equals(Action.NAME)) {
         textLabel.setText((String)evt.getNewValue());
         invalidate();
         parentMenu.updateSizeToPreferredSize();
      }
   }
   
   /** Invoked when the mouse button has been clicked (pressed
    * and released) on a component.
    *
    */
   public void mouseClicked(MouseEvent evt) {
      //Action Item
      if(menu == null) {
         if(SwingUtilities.isLeftMouseButton(evt)) {
            if(!showBusy_flag)
               fireAction();
         }
         else if(SwingUtilities.isRightMouseButton(evt)){
            showItemPopup(evt);
         }
      }
      //Sub Menu Item
      //else{
      //}
   }
   
   /** Invoked when the mouse enters a component.
    *
    */
   public void mouseEntered(MouseEvent e) {
            
      //highlight
      setSelected(true);
      
      //Sub Menu Item
      if(menu != null) {
         menu.getParentMenu().showSubMenu(this);
      }
   }
   
   /** Invoked when the mouse exits a component.
    *
    */
   public void mouseExited(MouseEvent e) {
      
      //un-highlight
      setSelected(false);
   }
   
   /** Invoked when a mouse button has been pressed on a component.
    *
    */
   public void mousePressed(MouseEvent e) {
   }
   
   /** Invoked when a mouse button has been released on a component.
    *
    */
   public void mouseReleased(MouseEvent e) {
   }
   
   /**
    * Internal Action used for submenu items
    */
   public class MenuAction extends AbstractAction {
      public MenuAction() {
         super.putValue(Action.NAME, menu.getMenuTitle());
         super.putValue(Action.SMALL_ICON, menu.getIcon());
      }
      
      public void actionPerformed(ActionEvent evt) {}
   }
   
   /**
    * Border Used for MenuItems
    */
   private class MenuItemBorder extends AbstractBorder {
      
      private Color highlight = borderColor;
      private Color shadow = borderColor;
      
      
      /** Returns the insets of the border.
       * @param c the component for which this border insets value applies
       *
       */
      public Insets getBorderInsets(Component c) {
         return new Insets(1,1,0,0);
      }
      
      /** Paints the border for the specified component with the specified
       * position and size.
       * @param c the component for which this border is being painted
       * @param g the paint graphics
       * @param x the x position of the painted border
       * @param y the y position of the painted border
       * @param width the width of the painted border
       * @param height the height of the painted border
       *
       */
      public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
         
         Graphics2D g2 = (Graphics2D)g;
         
         //Draw highlight
         g2.setColor(highlight);
         g2.drawLine(x, height, x, y);
         g2.drawLine(x, y, width, y);
         
         //Draw Shadow
         g2.setColor(shadow);
         g2.drawLine(x, height-1, width-1, height-1);
         g2.drawLine(width-1, height-1, width-1, y);
      }
      
   }
   
   /************************************************************
    * This Class is the listener for the Popup Menu
    ************************************************************/
   class MyMenuListener implements ActionListener {
      
      public MyMenuListener() {
         
      }
      
      public void actionPerformed(ActionEvent evt) {
         
         //Delete
         
         //Properties
         if(evt.getSource() == propertiesMI) {
            PropertySheetDialog _dialog = null;
            _dialog = new PropertySheetDialog(action, "Tile Properties");
            _dialog.show();
         }
      }
   }
   
   /************************************************************
    * This Class is the Listener for DragSource Events
    * For when the Menu Item is Dragged Somewhere else
    ************************************************************/
   class MyDragListenerAdapter extends DragListenerAdapter {
      
      protected MyDragListenerAdapter(){}
      
      /**
       * Default implementation of Drag Recognized
       * This method will
       */
      public void dragGestureRecognized(DragGestureEvent evt) {
         
         //Only BLX Actions can be dragged
         if((DCMenuItem.this.action instanceof BLXObject) == false) {
            return;
         }
         
         //Only Drags of Primay Mouse Button are recognized
         if(SwingUtilities.isRightMouseButton((MouseEvent)evt.getTriggerEvent())) {
            return;
         }
         
         //Setup Action and Cursor
         Cursor cursor = DragSource.DefaultCopyDrop;
         
         //Drag Image
         BufferedImage image = new BufferedImage(DCMenuItem.this.getWidth(), DCMenuItem.this.getHeight(), BufferedImage.TYPE_INT_ARGB);
         DCMenuItem.this.paint(image.getGraphics());
               
         //Start Drag Event using Action Data
         BLXTransferObject transfer = new BLXTransferObject((BLXObject)action);
         
         evt.startDrag(cursor, image, new Point(), transfer, this);
      }
   }
}