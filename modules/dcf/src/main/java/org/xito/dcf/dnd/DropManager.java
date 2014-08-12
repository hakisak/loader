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

package org.xito.dcf.dnd;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;


/**
 * << License Info Goes HERE >> All Rights Reserved.
 * DragListenerAdapter
 * Description:
 *
 * @author  $Author: drichan $
 * @version $Revision: 1.9 $
 * @since   $Date: 2007/11/28 03:52:39 $
 */
 public interface DropManager
 {

   /**
    * Sets the default drop target if a drag occurs over any item that is not a Drop
    * Target. This should usually be the desktop of the system.
    * @param default Drop Target
    */
   public void setDefaultDropTarget(DropTarget pDropTarget);

   /**
    *  Return a Drop Component for the component at the Current Location
    *  This method should search up the component hierarchy until it finds a Drop Target
    * @param Location look for the Drop Target in Screen Corrdinates
    */
   public DropTarget getDropTarget(Point pLocation);

   /**
    * Returns the current DragSource Context of the current Drag Operation or null
    * if a drag operation is not going on
    * @return DragSourceContext
    */
   public DragSourceContext getCurrentDragSourceContext();

   /**
    * Start the Drag opertation for a certain Context
    * @param DragSourceContext of this Drag
    * @param image that should be used during Drag
    * @param pImageOffset offSet of image location from Cursor or null to use Drag Component's Location
    */
   public void dragStarted(DragSourceContext pContext, Image pDragImage, Point pOffSet);

   /**
    * Get the Current Transferable Object for the current Drag operation
    * @return Transferable
    */
   public Transferable getCurrentTransferable();
   
 }


