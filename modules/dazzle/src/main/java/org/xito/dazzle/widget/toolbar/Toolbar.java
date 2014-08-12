/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.widget.toolbar;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

import org.xito.dazzle.utilities.DrawUtilities;
import org.xito.dazzle.utilities.MacApplicationUtilities;
import org.xito.dazzle.widget.DecorationComponent;
import org.xito.dazzle.widget.DefaultStyle;
import org.xito.dazzle.widget.laf.toolbar.ToolbarUI;
import org.xito.dazzle.widget.panel.GradientPanel;
import org.xito.dialog.TableLayout;

/**
 * 
 * @author deane
 */
public class Toolbar extends DecorationComponent {

   protected ArrayList<ToolbarItem> items = new ArrayList<ToolbarItem>();

   protected final static int DEFAULT_HEIGHT = 56;

   private UIDefaults styleMap;

   protected boolean shouldPaint_flag = true;

   protected TableLayout layout = new TableLayout();

   public Toolbar() {
      init();
   }

   protected void init() {

      styleMap = DefaultStyle.getDefaults();
      
      setUI(new ToolbarUI());

      Dimension size = new Dimension(100, DEFAULT_HEIGHT);
      setPreferredSize(size);
      setMinimumSize(size);

      setLayout(layout);

      //Install draggable window mouse listeneron Mac Only
      if(MacApplicationUtilities.isRunningOnMac()) {
         DraggableWindowMouseAdapter ma = new DraggableWindowMouseAdapter();
         addMouseListener(ma);
         addMouseMotionListener(ma);
      }

   }

   /**
    * Create a frame that has a toolbar at the NORTH location of the
    * contentPane. For mac this will create a brushed metal style frame
    * 
    * @return
    */
   public static ToolbarFrame createToolbarFrame() {
      return new ToolbarFrame();
   }

   public void addItem(ToolbarItem item) {
      items.add(item);
      rebuildLayout();
   }

   /**
    * Add an Item
    * 
    * @param index
    * @param item
    */
   public void addItem(int index, ToolbarItem item) {
      items.add(index, item);
      rebuildLayout();
   }

   public void addItemSpacer() {
      addItem(ToolbarItem.createSpacer());
   }

   public void addItemSpacer(int index) {
      addItem(index, ToolbarItem.createSpacer());
   }

   public void addItemFlexibleSpacer() {
      addItem(ToolbarItem.createFlexibleSpacer());
   }

   public void addItemFlexibleSpacer(int index) {
      addItem(index, ToolbarItem.createFlexibleSpacer());
   }

   public void addItemSeparator() {
      addItem(ToolbarItem.createSeparator());
   }

   public void addItemSeparator(int index) {
      addItem(index, ToolbarItem.createSeparator());
   }

   public void removeItem(int index) {
      items.remove(index);
      rebuildLayout();
   }

   public int findItem(String name) {
      for (int i = 0; i < items.size(); i++) {
         ToolbarItem item = items.get(i);
         if (item.getName().equals(name)) {
            return i;
         }
      }

      // not found
      return -1;
   }

   /**
    * Rebuild all UI components in the layout
    */
   protected void rebuildLayout() {

      // remove all components
      removeAll();

      // remove existing row
      if (layout.getRowCount() == 1)
         layout.removeRow(0);

      TableLayout.Row row = new TableLayout.Row();

      // add first padding
      row.addCol(new TableLayout.Column(ToolbarItem.PADDING));

      // add all items
      int sepCount = 0;
      for (ToolbarItem item : Toolbar.this.items) {
         TableLayout.Column col = new TableLayout.Column();

         if (item.isItem() || item.isComponent()) {
            col.name = item.getName();
            row.addCol(col);
         }

         else if (item.isSpacer()) {
            row.addCol(new TableLayout.Column(30));
         }

         else if (item.isSeparator()) {
            col.name = "sep" + sepCount++;
            row.addCol(col);
         } else if (item.isFlexibleSpacer()) {
            row.addCol(new TableLayout.Column(0.999f));
         }
      }

      // add last padding
      row.addCol(new TableLayout.Column(ToolbarItem.PADDING));

      layout.addRow(row);

      // add the components
      sepCount = 0;
      for (ToolbarItem item : Toolbar.this.items) {
         if (item.isItem() || item.isComponent()) {
            this.add(item.getName(), item);
         } else if (item.isSeparator()) {
            this.add("sep" + sepCount++, item);
         }
      }

      invalidate();
      repaint();
   }

   /**
    * @return the shouldPaint_flag
    */
   public boolean shouldPaint() {
      return shouldPaint_flag;
   }

   /**
    * @param shouldPaintFlag
    *           the shouldPaint_flag to set
    */
   protected void setShouldPaint(boolean shouldPaintFlag) {
      shouldPaint_flag = shouldPaintFlag;
   }


   /**
    * Class to listen for mouse drag events and move window
    */
   protected static class DraggableWindowMouseAdapter extends MouseAdapter implements MouseMotionListener {

      Point startLoc;
      Point startWinLoc;

      @Override
      public void mousePressed(MouseEvent e) {
         Point p = e.getPoint();
         SwingUtilities.convertPointToScreen(p, e.getComponent());
         startLoc = p;
         startWinLoc = SwingUtilities.getWindowAncestor(e.getComponent()).getLocation();
      }


      @Override
      public void mouseDragged(MouseEvent e) {
         Point p = e.getPoint();
         SwingUtilities.convertPointToScreen(p, e.getComponent());
         int dx = p.x - startLoc.x;
         int dy = p.y - startLoc.y;

         SwingUtilities.getWindowAncestor(e.getComponent()).setLocation(startWinLoc.x + dx, startWinLoc.y + dy);
      }
   }

}
