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
 *
 */
public class MenuItemNode extends DefaultMutableTreeNode  {
   
   protected MenuItemNode(Action action) {
      super(action, false);
   }
   
   public String toString() {
      if(getUserObject() instanceof Action) {
         Action action = (Action)getUserObject();
         return (String)action.getValue(Action.NAME);
      }
      else {
         return super.toString();
      }
   }
}