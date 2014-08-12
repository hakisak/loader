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

package org.xito.appmanager;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.net.*;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import org.xito.appmanager.store.ApplicationNode;
import org.xito.dazzle.worker.WindowBlockingBusyWorker;
import org.xito.launcher.*;

/**
 *
 * @author DRICHAN
 */
public class AppTree extends JTree {
   
   public static final int RADIUS = 8;
   
   DropData currentDropData = null;
   Color dragColor = new Color(.5f, .5f, .5f, .15f);
   Color dragBorderColor = new Color(.5f, .5f, .5f, .5f);
   Color selectedTopColor = new Color(171,185,207);
   Color selectedBottomColor = new Color(153,170,197);
   Color selectedBottomBorder = new Color(140, 152, 176);
   Color backGroundColor = new Color(230, 237, 247);
   String id;
   
   
   /** Creates a new instance of AppTree */
   public AppTree(String id) {
      super(new Vector());
      new TreeDNDHandler();
      this.id = id;
      setOpaque(false);
            
      //fix for selection paint problem
      addTreeSelectionListener(new TreeSelectionListener() {
         public void valueChanged(TreeSelectionEvent e) {
            AppTree.this.repaint();
         }
      });

      
   }

   public void initExpansion() {
      //read expansion preferences
      final Preferences prefs = Preferences.userNodeForPackage(AppTree.class);
      expandPaths(prefs, new TreePath(super.getModel().getRoot()));
      
      addTreeExpansionListener(new TreeExpansionListener() {
         public void treeCollapsed(TreeExpansionEvent event) {
            prefs.putBoolean(getPrefName(event.getPath()), false);
            storePrefs();
         }
         public void treeExpanded(TreeExpansionEvent event) {
            prefs.putBoolean(getPrefName(event.getPath()), true);
            storePrefs();
         }
         
         public void storePrefs() {
           try {
               prefs.flush();
            }
            catch(BackingStoreException exp) {
               exp.printStackTrace();
            }
         }
      });
   }
   
   private String getPrefName(TreePath path) {
      return id+".expanded"+path.toString();
   }
   
   private void expandPaths(Preferences prefs, TreePath parentPath) {
      
      if(parentPath == null) return;
      TreeNode parentNode = (TreeNode)parentPath.getLastPathComponent();
            
      Enumeration children = parentNode.children();
      while(children.hasMoreElements()) {
         TreeNode node = (TreeNode)children.nextElement();
         TreePath nodePath = parentPath.pathByAddingChild(node);
         String prefName = getPrefName(nodePath);
         boolean expanded = prefs.getBoolean(prefName, false);
         if(expanded) {
            this.expandPath(nodePath);
         }
         
         if(node.getChildCount()>0) {
            expandPaths(prefs, nodePath);
         }
      }
   }
   
   /**
    * new Paint method used to paint drag highlights
    */
   public void paintComponent(Graphics g) {

      Graphics2D g2 = (Graphics2D)g;
      g2.setColor(backGroundColor);
      g2.fillRect(0, 0, getWidth(), getHeight());
            
      //paint selected rows
      if(getSelectionCount()>0) {
         int y = 0;
         int h = getRowHeight();
         int padding = h/5;
         int[] rows = getSelectionRows();
         for(int i=0;i<rows.length;i++) {
            y = h*rows[i];
            GradientPaint selectedColor = new GradientPaint(0, y, selectedTopColor, 0, y+h+padding, selectedBottomColor);
            g2.setPaint(selectedColor);
            g2.fillRect(0, y, getWidth(), h+padding);
            g2.setPaint(selectedBottomBorder);
            g2.fillRect(0, y+h+padding, getWidth(), 1);
         }
      }
      
      super.paintComponent(g);
            
      //paint drop data
      if(currentDropData != null) {
         g2.setColor(dragColor);
         Rectangle r = currentDropData.dragRect;
         g2.fillRoundRect(r.x, r.y, r.width-1, r.height-1, RADIUS, RADIUS);
         g2.setColor(dragBorderColor);
         g2.drawRoundRect(r.x, r.y, r.width-1, r.height-1, RADIUS, RADIUS);
      }
      
   }
   
   /**
    * Process the Dropped URL
    */
   protected void processDrop(final URL url, final DropData dropData) {

      //This could block so we use a busy worker
      new WindowBlockingBusyWorker<LauncherAction>((Frame)SwingUtilities.getWindowAncestor(this)) {

         @Override
         public LauncherAction work() {
            
            //Create Action
            return LauncherService.createActionFromObject((Frame)SwingUtilities.getWindowAncestor(AppTree.this), url);
         }
         
         @Override
         public void finished(LauncherAction data) {
            
            if(data != null) {
               AppTreeModel model = (AppTreeModel)getModel();
               
               ApplicationTreeNodeWrapper actionNode = new ApplicationTreeNodeWrapper(null, new ApplicationNode(data));
               model.insertNode((MutableTreeNode)actionNode, dropData.path, dropData.above);
               
            }
         }
      }.invokeLater();
      
   }
   
   /****************************************************************
    *Class Used for DropData
    ****************************************************************/
   class DropData {
      TreePath path = null;
      TreeNode selectedNode = null;
      boolean above = false;
      Rectangle dragRect = null;
   }
   
   /****************************************************************
    *Class Used for DND Events
    ****************************************************************/
   class TreeDNDHandler extends DropTargetAdapter implements DragGestureListener {
   
      private DragSource dragSource;
      private DropTarget dropTarget;

      /** Creates a new instance of TreeDNDHandler */
      public TreeDNDHandler() {
         
         dragSource = new DragSource();
         dragSource.createDefaultDragGestureRecognizer(AppTree.this, DnDConstants.ACTION_COPY_OR_MOVE, this);
         dropTarget = new DropTarget(AppTree.this, this);
      }

      public void dragGestureRecognized(DragGestureEvent evt) {
         int action = evt.getDragAction();
         TreePath path = AppTree.this.getSelectionPath();
         if(path == null) {
            return;
         }

         TreeNode node = (TreeNode)path.getLastPathComponent();
         ItemTransferable transferable = new ItemTransferable(node);
         if(action == DnDConstants.ACTION_MOVE) {

            evt.startDrag(DragSource.DefaultMoveDrop, transferable, null);
         }
      }

      /*****************************************
       * DropTargetListener Events
       ****************************************/
      public void dragEnter(DropTargetDragEvent evt) {
         evt.acceptDrag(evt.getDropAction());
      }

      public void dragExit(DropTargetEvent evt) {
         currentDropData = null;
      }

      /**
       * DragOver Event Handler
       */
      public void dragOver(DropTargetDragEvent evt) {

         //Make Frame goto Front
         SwingUtilities.getWindowAncestor(AppTree.this).toFront();
         currentDropData = new DropData();
         
         Point loc = evt.getLocation();
         //System.out.println(loc);

         //Get Selected Highlighted Component
         TreePath path = getPathForLocation(loc.x, loc.y);
         TreeNode selectedNode = null;
         if(path != null) {
            selectedNode = (TreeNode)path.getLastPathComponent();
         }
         else {
            selectedNode = (TreeNode)AppTree.this.getModel().getRoot();
         }

         //Get Row Information
         Rectangle dr = null;
         int row = getRowForLocation(loc.x, loc.y);
         int center = 0;
         if(row >=0) {
            dr = getRowBounds(row);
            center = dr.y + dr.height/2;
         }
         //Not selecting any row so just place at bottom
         else {
            dr = getRowBounds(getRowCount()-1);
            dr = new Rectangle(dr.x, dr.y + dr.height-1, dr.width, 2);
         }

         dr.x = 0;
         dr.width = getWidth();
         
         //Determine if its a Group
         if(selectedNode instanceof ApplicationTreeNodeWrapper) {
            //above
            if(loc.y < center) {
               dr = new Rectangle(dr.x, dr.y-1, dr.width, 2);
               currentDropData.above = true;
            }
            //below
            else {
               dr = new Rectangle(dr.x, dr.y + dr.height-1, dr.width, 2);
               currentDropData.above = false;
            }
         }
         
         currentDropData.dragRect = dr;
         currentDropData.selectedNode = selectedNode;
         currentDropData.path = path;
                           
         invalidate();
         repaint();
         evt.acceptDrag(evt.getDropAction());
      }

      /**
       * Drop Event Handler
       */
      public void	drop(DropTargetDropEvent evt) {

         evt.acceptDrop(evt.getDropAction());
         Transferable t = evt.getTransferable();
         
         //Clear current drop data and repaint tree
         DropData dropData = AppTree.this.currentDropData;
         dndEnd();
         if(dropData == null) {
            evt.rejectDrop();
            evt.dropComplete(false);
            return;
         }
         
         //print out all flavors supported
         DataFlavor flavors[] = t.getTransferDataFlavors();
         for(int i=0;i<flavors.length;i++) {
            System.out.println(flavors[i]);
         }
         
         //Try and obtain Tree Node
         try {
            DataFlavor refFlavor = new DataFlavor("application/x-java-jvm-local-objectref");
            Object obj = t.getTransferData(refFlavor);
            if(obj instanceof MutableTreeNode) {
               evt.dropComplete(true);
               AppTreeModel model = (AppTreeModel)AppTree.this.getModel();
               model.moveNode((MutableTreeNode)obj, dropData.path, dropData.above);
               return;
            }
         }
         catch(UnsupportedFlavorException unsupported){}
         catch(Exception exp) {
            exp.printStackTrace();
         }

         //Try to obtain a URL
         try {
            DataFlavor urlFlavor = new DataFlavor("application/x-java-url; class=java.net.URL");
            URL url = (URL)t.getTransferData(urlFlavor);
            evt.dropComplete(true);
            
            processDrop(url, dropData);
            return;
         }
         catch(UnsupportedFlavorException unsupported){}
         catch(Exception exp) {
            exp.printStackTrace();
         }
         
         //Try to convert String to URL
         try {
            DataFlavor stringFlavor = new DataFlavor("text/plain; class=java.lang.String");
            String s = (String)t.getTransferData(stringFlavor);
            URL url = new URL(s);
            evt.dropComplete(true);
            
            processDrop(url, dropData);
            return;
         }
         catch(UnsupportedFlavorException unsupported){}
         catch(Exception exp) {
            exp.printStackTrace();
         }
         
         //If we made it down here then we can't process the Drop
         evt.dropComplete(false);
      }

      public void dndEnd() {
         System.out.println("DND END!");
         AppTree.this.currentDropData = null;
         AppTree.this.invalidate();
         AppTree.this.repaint();
      }

            
      /*****************************************
       * Transferable Item for Tree Nodes
       ****************************************/
      public class ItemTransferable implements Transferable {  

         Object transferItem;
         DataFlavor flavor;

         public ItemTransferable(Object item) {
            transferItem = item;
            try {
               flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
            }
            catch(Exception exp) {
               exp.printStackTrace();
            }
         }

         public Object getTransferData(DataFlavor flavor) {
            if(this.flavor.equals(flavor)) {
               return transferItem;
            }
            else {
               return transferItem.toString();
            }
         }

         public DataFlavor[]	getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[]{flavor};
            return flavors;
         }

         public boolean isDataFlavorSupported(DataFlavor flavor) {
            return this.flavor.equals(flavor);
         } 
      }
   }

   
}
