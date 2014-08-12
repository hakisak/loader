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

public class BasicTreeNode implements TreeNode {
   
   /** Returns the children of the receiver as an <code>Enumeration</code>.
    *
    */
   public Enumeration children() {
      return null;
   }
   
   /** Returns true if the receiver allows children.
    *
    */
   public boolean getAllowsChildren() {
      return false;
   }
   
   /** Returns the child <code>TreeNode</code> at index
    * <code>childIndex</code>.
    *
    */
   public TreeNode getChildAt(int childIndex) {
      return null;
   }
   
   /** Returns the number of children <code>TreeNode</code>s the receiver
    * contains.
    *
    */
   public int getChildCount() {
      return 0;
   }
   
   /** Returns the index of <code>node</code> in the receivers children.
    * If the receiver does not contain <code>node</code>, -1 will be
    * returned.
    *
    */
   public int getIndex(TreeNode node) {
      return 0;
   }
   
   /** Returns the parent <code>TreeNode</code> of the receiver.
    *
    */
   public TreeNode getParent() {
      return null;
   }
   
   /** Returns true if the receiver is a leaf.
    *
    */
   public boolean isLeaf() {
      return true;
   }
   
}


