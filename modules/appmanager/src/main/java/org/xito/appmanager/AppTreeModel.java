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

import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.*;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;

import org.xito.appmanager.store.AppStoreNode;
import org.xito.appmanager.store.ApplicationNode;
import org.xito.appmanager.store.ApplicationStore;
import org.xito.appmanager.store.GroupNode;
import org.xito.appmanager.store.StoreException;

/**
 *
 * @author Deane Richan
 */
public class AppTreeModel extends DefaultTreeModel {
   
   private static final Logger logger = Logger.getLogger(AppTreeModel.class.getName());
   protected static final String APPMANAGER_DIR = "/appmanager";

   protected HashSet removedNodes = new HashSet();
   
   /** Creates a new instance of AppTreeModel */
   public AppTreeModel() {
      
      super(null);
      addTreeModelListener(new TreeModelListener() {

         public void treeNodesChanged(TreeModelEvent evt) {
            markDirty(evt.getChildren());
         }

         public void treeNodesInserted(TreeModelEvent evt) {
            
         }

         public void treeNodesRemoved(TreeModelEvent evt) {
            
         }

         public void treeStructureChanged(TreeModelEvent evt) {
              
         }
         
      });
   }
   
   public void removeNode(DefaultMutableTreeNode node) {
      
      //Get current Parent
      GroupTreeNodeWrapper parentNode = (GroupTreeNodeWrapper)node.getParent();
      
      //Get current AppStoreNode
      AppStoreNode storeNode = (AppStoreNode)node.getUserObject();
      parentNode.getGroupNode().remove(storeNode);
      
      //mark parent as dirty
      markDirty(new Object[]{parentNode});
      System.out.println("node: " + node.toString() + " removed from:" + parentNode.toString());

      //Add to removed set
      removedNodes.add(storeNode);
      
      //now remove from this model
      super.removeNodeFromParent(node);
   }
   
   /**
    * Mark a Node as being dirty
    * @param node
    */
   private void markDirty(Object treeNodes[]) {
      for(int i=0;i<treeNodes.length;i++) {
         if(treeNodes[i] instanceof ApplicationTreeNodeWrapper) {
            ((ApplicationTreeNodeWrapper)treeNodes[i]).getApplicationNode().setDirty(true);
         }
         else if(treeNodes[i] instanceof GroupTreeNodeWrapper) {
            ((GroupTreeNodeWrapper)treeNodes[i]).getGroupNode().setDirty(true);
         }
      }
   }
   
   private void markDirty(TreePath path) {
      
      TreeNode node = (TreeNode)path.getLastPathComponent();
      
      if(node instanceof ApplicationTreeNodeWrapper) {
         ((ApplicationTreeNodeWrapper)node).getApplicationNode().setDirty(true);
      }
      else if(node instanceof GroupTreeNodeWrapper) {
         ((GroupTreeNodeWrapper)node).getGroupNode().setDirty(true);
      }
   }
   
   private void markRemoved(Object nodes[]) {
      for(int i=0;i<nodes.length;i++) {
         if(nodes[i] instanceof ApplicationTreeNodeWrapper) {
            
         }
         else if(nodes[i] instanceof GroupTreeNodeWrapper) {
         
         }
      }
   }
   
   public void insertNode(MutableTreeNode node, TreePath destPath, boolean above) {
      GroupTreeNodeWrapper parentNode = null;
      int index = 0;
            
      //If no path then use Root
      if(destPath == null) {
         parentNode = (GroupTreeNodeWrapper)getRoot();
         index = parentNode.getChildCount();
      }
      //Find Parent node using Path
      else {
         MutableTreeNode selectedNode = (MutableTreeNode)destPath.getLastPathComponent();
         if(selectedNode instanceof ApplicationTreeNodeWrapper) {
            parentNode = (GroupTreeNodeWrapper)selectedNode.getParent();
            index = parentNode.getIndex(selectedNode);
            if(!above) {
               index++;
            }
         }
         else {
            parentNode = (GroupTreeNodeWrapper)selectedNode;
         }
      }
      
      markDirty(new Object[]{parentNode, node});      
      System.out.println("adding node: " + node.toString() + " to:"+parentNode.toString()+" index:"+index);
      insertNodeInto(node, parentNode, index);
   }
   
   /**
    * Move a Tree Node to a new Path
    * @param node to Move
    * @param destPath
    * @param above true if node just be placed 1 index above desthPath node
    */
   public void moveNode(MutableTreeNode node, TreePath destPath, boolean above) {
      
      //Check to see if we are try to move a node onto itself
      if(destPath != null) {
         MutableTreeNode selectedNode = (MutableTreeNode)destPath.getLastPathComponent();
         if(node == selectedNode) {
            return;
         }
      }
      
      //Default parent to Root Node
      MutableTreeNode newParentNode = null;
            
      //First remove from the Parent
      //Removes from the tree model
      removeNodeFromParent(node); 
            
      int index = 0;
      //If no path then use Root
      if(destPath == null) {
         newParentNode = (MutableTreeNode)getRoot();
         index = newParentNode.getChildCount();
      }
      //Find Parent node using Path
      else {
         MutableTreeNode selectedNode = (MutableTreeNode)destPath.getLastPathComponent();
         if(selectedNode instanceof ApplicationTreeNodeWrapper) {
            newParentNode = (MutableTreeNode)selectedNode.getParent();
            if(newParentNode == null) {
               newParentNode = (MutableTreeNode)getRoot();
            }
            index = newParentNode.getIndex(selectedNode);
            if(!above) {
               index++;
            }
         }
         else {
            newParentNode = selectedNode;
            index = newParentNode.getChildCount();
         }
      }
      
      System.out.println("moving node: " + node.toString() + " to:"+newParentNode.toString()+" index:"+index);
      insertNodeInto(node, newParentNode, index);
   }
   
   /**
    * Add a Node
    * @param child
    * @return
    */
   public TreePath addNode(TreePath parentPath, AppStoreNode child) {
      
      DefaultMutableTreeNode parentNode = null;
      if (parentPath == null) {
         parentNode = (DefaultMutableTreeNode)getRoot();
      } else {
         parentNode = (DefaultMutableTreeNode)(parentPath.getLastPathComponent());
         if(parentNode.getAllowsChildren()==false) {
            parentNode = (DefaultMutableTreeNode)parentNode.getParent();
         }
      }
      
      return addNode(parentNode, child);
   }
   
   /**
    * Add a Node to a Parent
    * @param parent
    * @param child
    * @return
    */
   public TreePath addNode(DefaultMutableTreeNode parent, AppStoreNode child ) {
      
      MutableTreeNode childTreeNode = null;
      if(child instanceof ApplicationNode) {
         childTreeNode = new ApplicationTreeNodeWrapper(null, (ApplicationNode)child);
      }
      else if(child instanceof GroupNode) {
         childTreeNode = new GroupTreeNodeWrapper((GroupNode)child);
      }
      
      if(childTreeNode != null) {
         insertNodeInto(childTreeNode, parent, parent.getChildCount());
         markDirty(new Object[]{childTreeNode});
      }
            
      return new TreePath(childTreeNode);
   }
   
   public void storeDirtyNodes() {
      try {
         //store any dirty nodes
         GroupNode rootNode = ((GroupTreeNodeWrapper)getRoot()).getGroupNode();
         ApplicationStore.getInstance().storeAllDirty(rootNode);
         
         //remove deleted nodes
         Iterator it = removedNodes.iterator();
         while(it.hasNext()) {
            AppStoreNode removedNode = (AppStoreNode)it.next();
            ApplicationStore.getInstance().deleteTree(removedNode);
         }
         
         //clear the list of removed nodes
         removedNodes.clear();
      }
      catch(StoreException exp) {
         exp.printStackTrace();
      }
   }
  
}
