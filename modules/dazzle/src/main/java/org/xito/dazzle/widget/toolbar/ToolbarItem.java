/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.widget.toolbar;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIDefaults;

import org.xito.dazzle.utilities.MacApplicationUtilities;
import org.xito.dazzle.widget.DebugLineDecorator;
import org.xito.dazzle.widget.DecorationComponent;
import org.xito.dazzle.widget.DefaultStyle;
import org.xito.dazzle.widget.laf.toolbar.ToolbarItemSeparatorUI;
import org.xito.dazzle.widget.laf.toolbar.ToolbarItemUI;

/**
 * 
 * @author deane
 */
public class ToolbarItem extends DecorationComponent {

   public static final int PADDING = 5;

   protected enum Type {
      SPACER, FLEXIBLE_SPACER, ITEM, SEPARATOR, COMPONENT
   }

   protected String name;
   protected String text;
   protected ImageIcon largeIcon;
   protected ImageIcon smallIcon;
   protected ActionListener action;
   protected Type type;
   protected JComponent component;
   protected boolean pressed_flag = false;
   protected UIDefaults styleMap;

   protected ToolbarItem(JComponent component) {
      this.component = component;
      this.type = Type.COMPONENT;
      init();
   }

   protected ToolbarItem(Type type) {
      this.type = type;
      init();
   }

   private void init() {
      
      styleMap = DefaultStyle.getDefaults();
      setOpaque(false);
      
      //setUI
      switch(type) {
         case ITEM:        setUI(new ToolbarItemUI(styleMap));
                           break;
         case SEPARATOR:   setUI(new ToolbarItemSeparatorUI());
                           break;
         case COMPONENT:   setLayout(new FlowLayout());
                           add(component);
                           break;
      }
      
      addMouseListener(new MyMouseListener());

      //Install draggable window mouse listener
      if(MacApplicationUtilities.isRunningOnMac()) {
         Toolbar.DraggableWindowMouseAdapter ma = new Toolbar.DraggableWindowMouseAdapter();
         addMouseListener(ma);
         addMouseMotionListener(ma);
      }
   }

   public static ToolbarItem createSeparator() {
      ToolbarItem item = new ToolbarItem(Type.SEPARATOR);
      
      return item;
   }

   /**
    * Create a Spacer
    * @return
    */
   public static ToolbarItem createSpacer() {

      ToolbarItem item = new ToolbarItem(Type.SPACER);
      
      return item;
   }

   /**
    * Create a Flexible Spacer
    * @return
    */
   public static ToolbarItem createFlexibleSpacer() {

      ToolbarItem item = new ToolbarItem(Type.FLEXIBLE_SPACER);

      return item;
   }

   public static ToolbarItem createItem(JComponent component) {
      ToolbarItem item = new ToolbarItem(component);
      item.name = component.toString();

      return item;
   }

   /**
    * Create an Item
    * @param name
    * @param text
    * @param action
    * @param largeIcon
    * @param smallIcon
    * @return
    */
   public static ToolbarItem createItem(String name, String text, ActionListener action, ImageIcon largeIcon,
         ImageIcon smallIcon) {

      ToolbarItem item = new ToolbarItem(Type.ITEM);
      
      item.name = name;
      item.text = text;
      item.largeIcon = largeIcon;
      item.smallIcon = smallIcon;
      item.action = action;

      return item;
   }

   public boolean isPressed() {
      return pressed_flag;
   }
   
   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name
    *           the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the text
    */
   public String getText() {
      return text;
   }

   /**
    * @param text
    *           the text to set
    */
   public void setText(String text) {
      this.text = text;
   }

   /**
    * @return the largeIcon
    */
   public ImageIcon getLargeIcon() {
      return largeIcon;
   }

   /**
    * @param largeIcon
    *           the largeIcon to set
    */
   public void setLargeIcon(ImageIcon largeIcon) {
      this.largeIcon = largeIcon;
   }

   public boolean isFlexibleSpacer() {
      return type == Type.FLEXIBLE_SPACER;
   }

   public boolean isSpacer() {
      return type == Type.SPACER;
   }

   public boolean isItem() {
      return type == Type.ITEM;
   }

   public boolean isComponent() {
      return type == Type.COMPONENT;
   }

   public boolean isSeparator() {
      return type == Type.SEPARATOR;
   }

   @Override
   public Dimension getPreferredSize() {

      if(type == Type.ITEM) {
         Dimension ps = new Dimension();
         // preferred width is based on title
         ps.width = (int) ((ToolbarItemUI)ui).getTitleBounds(this).getWidth() + (PADDING * 2);
   
         // preferred size is always HEIGHT;
         ps.height = Toolbar.DEFAULT_HEIGHT;
   
         return ps;
      }
      else if(type == Type.SEPARATOR){
         return new Dimension(10, Toolbar.DEFAULT_HEIGHT);
      }
      else if(type == Type.COMPONENT) {
         return new Dimension(component.getPreferredSize().width, Toolbar.DEFAULT_HEIGHT);
      }
      else {
         return new Dimension(0,0);
      }
   }
  
   //-------------------------------------------------------------------------
   
   
   /**
    * Handles Mouse Events      
    */
   public class MyMouseListener extends MouseAdapter {

      public void mouseClicked(MouseEvent e) {
         if (action != null) {
            ActionEvent ae = new ActionEvent(ToolbarItem.this, 0, name, e.getModifiers());
            action.actionPerformed(ae);
         }
      }

      public void mousePressed(MouseEvent e) {
         pressed_flag = true;
         repaint();
      }

      public void mouseReleased(MouseEvent e) {
         pressed_flag = false;
         repaint();
      }
   }
}
