// Copyright (C) 2002 Xito.org. http://www.xito.org
//
// This file is part of Xito(tm).
//
// This is free software; you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License (LGPL)
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// It is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this application.
//
// Information about the GNU LGPL License can be obtained at
// http://www.gnu.org/licenses/

package org.xito.desktop.menu_designer;

import java.util.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 *
 *
 *
 */
public class MenuTreeModel implements TreeModel {
   
   private org.xito.desktop.DesktopMenuModel menuModel;
   private MenuModelNode root;
   private HashSet listeners = new HashSet();
   
   public MenuTreeModel(org.xito.desktop.DesktopMenuModel menuModel) {
      this.menuModel = menuModel;
      root = new MenuModelNode(null, menuModel);
   }
   
   protected org.xito.desktop.DesktopMenuModel getRootMenuModel() {
      return (org.xito.desktop.DesktopMenuModel)getRoot();
   }
   
   public Object getRoot() {
      return root;
   }
   
   /** Returns the child of <code>parent</code> at index <code>index</code>
    * in the parent's
    * child array.  <code>parent</code> must be a node previously obtained
    * from this data source. This should not return <code>null</code>
    * if <code>index</code>
    * is a valid index for <code>parent</code> (that is <code>index >= 0 &&
    * index < getChildCount(parent</code>)).
    *
    * @return the child of <code>parent</code> at index <code>index</code>
    * @param index
    * @param parent a node in the tree, obtained from this data source
    */
   public Object getChild(Object parent, int index) {
      return ((MenuModelNode)parent).getChildAt(index);
   }
   
   /** Returns the number of children of <code>parent</code>.
    * Returns 0 if the node
    * is a leaf or if it has no children.  <code>parent</code> must be a node
    * previously obtained from this data source.
    *
    * @param   parent  a node in the tree, obtained from this data source
    * @return  the number of children of the node <code>parent</code>
    *
    */
   public int getChildCount(Object parent) {
      return ((MenuModelNode)parent).getChildCount();
   }
   
   /** Returns the index of child in parent.  If <code>parent</code>
    * is <code>null</code> or <code>child</code> is <code>null</code>,
    * returns -1.
    *
    * @param parent a note in the tree, obtained from this data source
    * @param child the node we are interested in
    * @return the index of the child in the parent, or -1 if either
    *    <code>child</code> or <code>parent</code> are <code>null</code>
    *
    */
   public int getIndexOfChild(Object parent, Object child) {
      return ((MenuModelNode)parent).getIndex((javax.swing.tree.TreeNode)child);
   }
   
   /** Returns <code>true</code> if <code>node</code> is a leaf.
    * It is possible for this method to return <code>false</code>
    * even if <code>node</code> has no children.
    * A directory in a filesystem, for example,
    * may contain no files; the node representing
    * the directory is not a leaf, but it also has no children.
    *
    * @param   node  a node in the tree, obtained from this data source
    * @return  true if <code>node</code> is a leaf
    *
    */
   public boolean isLeaf(Object node) {
      if(node instanceof MenuItemNode) return true;
      else return false;
   }
   
   public void refresh() {
      ((MenuModelNode)getRoot()).refresh();
      //fireTreeStructureChanged(getRoot(), new Object[]{getRoot()}, null, null);
   }
   
   /** Adds a listener for the <code>TreeModelEvent</code>
    * posted after the tree changes.
    *
    * @param   l       the listener to add
    * @see     #removeTreeModelListener
    *
    */
   public void addTreeModelListener(TreeModelListener listener) {
      listeners.add(listener);
   }
   
   /** Removes a listener previously added with
    * <code>addTreeModelListener</code>.
    *
    * @see     #addTreeModelListener
    * @param   l       the listener to remove
    *
    */
   public void removeTreeModelListener(TreeModelListener listener) {
      listeners.remove(listener);
   }
   
   /** Messaged when the user has altered the value for the item identified
    * by <code>path</code> to <code>newValue</code>.
    * If <code>newValue</code> signifies a truly new value
    * the model should post a <code>treeNodesChanged</code> event.
    *
    * @param path path to the node that the user has altered
    * @param newValue the new value from the TreeCellEditor
    *
    */
   public void valueForPathChanged(TreePath path, Object newValue) {
   }
   
}