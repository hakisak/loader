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

import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

import org.xito.desktop.*;

/**
 *
 *
 * @author drichan
 */
public class MenuModelNode implements TreeNode {
   
   private TreeNode parent;
   private DesktopMenuModel menuModel;
   private ArrayList childNodes = new ArrayList();
   private boolean hasRefreshed_flag = false;
   
   /**
    * Create a new MenuModel Node
    * @param parent of this Node
    * @param Model the node will be placed into
    */
   public MenuModelNode(MenuModelNode parent, DesktopMenuModel menuModel) {
      
      this.menuModel = menuModel;
      this.parent = parent;
   }
   
   /** 
    * Returns the children of the receiver as an <code>Enumeration</code>.
    *
    */
   public Enumeration children() {
      if(hasRefreshed_flag == false) refresh();
      
      return Collections.enumeration(childNodes);
   }
   
   
   /** 
    * Returns the child <code>TreeNode</code> at index
    * <code>childIndex</code>.
    *
    */
   public TreeNode getChildAt(int childIndex) {
      if(hasRefreshed_flag == false) refresh();
      
      return (TreeNode)childNodes.get(childIndex);
   }
   
   /** 
    * Returns the number of children <code>TreeNode</code>s the receiver
    * contains.
    *
    */
   public int getChildCount() {
      if(hasRefreshed_flag == false) refresh();
      
      return childNodes.size();
   }
   
   /** Returns the index of <code>node</code> in the receivers children.
    * If the receiver does not contain <code>node</code>, -1 will be
    * returned.
    *
    */
   public int getIndex(TreeNode node) {
      if(hasRefreshed_flag == false) refresh();
      
      return childNodes.indexOf(node);
   }
   
   /** Returns the parent <code>TreeNode</code> of the receiver.
    *
    */
   public TreeNode getParent() {
      return parent;
   }
   
   /** Returns true if the receiver is a leaf.
    *
    */
   public boolean isLeaf() {
      return false;
   }
   
   /**
    *
    */
   public synchronized void refresh() {
      
      childNodes.clear();
      Iterator it = menuModel.iterator();
      while(it.hasNext()) {
         Object item = it.next();
         if(item instanceof Action) childNodes.add(new MenuItemNode((Action)item));
      }
      
      hasRefreshed_flag = true;
   }
   
   /** 
    * Returns true if the receiver allows children.
    *
    */
   public boolean getAllowsChildren() {
      return true;
   }
   
}