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

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.dnd.peer.*;
import java.awt.datatransfer.*;

import org.xito.dcf.*;

/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.5 $
 * @since $Date: 2007/11/28 03:52:39 $
 */
public class DCDragSource extends DragSource {
   private static DCDragSource singleton = new DCDragSource();
   
   /**
    * Get the Default DragSource Object
    */
   public static DragSource getDefaultDragSource() {
      return (DragSource)singleton;
   }
   
   /**
    * Create a DragGestureRecognizer
    * @param pComp Component to Listen on
    * @param pActions DnD Actions to Listen for
    * @param pListener DragGestureListener
    */
   public DragGestureRecognizer createDefaultDragGestureRecognizer(Component pComp, int pActions, DragGestureListener pListener) {
      return super.createDefaultDragGestureRecognizer(pComp, pActions, pListener);
   }
   
   /**
    * Create the DragSource Context for this Drag Operation
    * @param pPeer
    * @param pEvent
    * @param pCursor
    * @param pImage
    * @param pOffset
    * @param pTransferable
    * @param pListener
    */
   protected DragSourceContext createDragSourceContext(DragSourceContextPeer pPeer, DragGestureEvent pEvent, Cursor pCursor, Image pImage, Point pOffset, Transferable pTransferable, DragSourceListener pListener) {
      
      return new DragSourceContext(pPeer, pEvent, pCursor, pImage, pOffset, pTransferable, pListener);
   }
   
   /**
    * Start the Drag Operation
    * @param pEvent
    * @param pCursor
    * @param pImage
    * @param pPoint location to offset dragImage from the DragOrigin or null to use the Drag Component's location
    * @param pTransferable
    * @param pFlavorMap
    */
   public void startDrag(DragGestureEvent pEvent, Cursor pCursor, Image pImage, Point pPoint, Transferable pTransferable, DragSourceListener pListener, FlavorMap pFlavorMap) throws InvalidDnDOperationException {
      Component target = null;
      
      //Start Dragged Called. First see if the component is a Component
      try {
         target = (Component)pEvent.getComponent();
      }
      catch(ClassCastException _exp) {
         super.startDrag(pEvent, pCursor, pImage, pPoint, pTransferable, pListener, pFlavorMap);
      }
      
      //If it is a Shift Drag then default to System Drag and Drop
      if((((MouseEvent)pEvent.getTriggerEvent()).getModifiersEx() & MouseEvent.ALT_DOWN_MASK) > 0) {
         System.out.println("************* Starting Native Drag");
         super.startDrag(pEvent, pCursor, pImage, pPoint, pTransferable, pListener, pFlavorMap);
      }
      
      //Create Drag Context
      DragSourceContext context = new DragSourceContext(new MyDragSourceContextPeer(), pEvent, pCursor, pImage, pPoint, pTransferable, pListener);
            
      //Tell DCComponent it is about to be dragged
      if(target instanceof DCComponent) {
         //((DCComponent)target).dragStarted(pPoint);
      }
    
      //Notify the DropManager to Start the Drag
      DropManagerImpl.getDropManager().dragStarted(context, pImage, pPoint);
   }
   
   /**
    * Start the Drag Operation
    * @param pEvent DragGestureEvent that triggered this Operation
    * @param pCursor Cursor to use during Drag operation
    * @param pTransferable transferable object to use for data transfers
    * @param pListener DragSource Listener
    * @param pFlavorMap map of Data Flavors available in this transfer
    */
   public void startDrag(DragGestureEvent pEvent, Cursor pCursor, Transferable pTransferable, DragSourceListener pListener, FlavorMap pFlavorMap) throws InvalidDnDOperationException {
      this.startDrag(pEvent, pCursor, null, null, pTransferable, pListener, pFlavorMap);
   }
   
   /**
    * Start the Drag Operation
    * @param pEvent DragGestureEvent that triggered this Operation
    * @param pCursor Cursor to use during Drag operation
    * @param pImage image to show during Drag Operation
    * @param pPoint
    * @param pTransferable transferable object to use for data transfers
    * @param pListener DragSource Listener
    */
   public void startDrag(DragGestureEvent pEvent, Cursor pCursor, Image pImage, Point pPoint, Transferable pTransferable, DragSourceListener pListener) throws InvalidDnDOperationException {
      this.startDrag(pEvent, pCursor, pImage, pPoint, pTransferable, pListener, null);
      //super.startDrag(dragGestureEvent, cursor, image, point, transferable, dragSourceListener);
   }
   
   /**
    * Start the Drag Operation
    * @param pEvent DragGestureEvent that triggered this Operation
    * @param pCursor Cursor to use during Drag operation
    * @param pTransferable transferable object to use for data transfers
    * @param pListener DragSource Listener
    */
   public void startDrag(DragGestureEvent pEvent, Cursor pCursor, Transferable pTransferable, DragSourceListener pListener) throws InvalidDnDOperationException {
      this.startDrag(pEvent, pCursor, null, null, pTransferable, pListener);
      //super.startDrag(dragGestureEvent, cursor, transferable, dragSourceListener);
   }
   
   /*******************
    * My DragSourceContextPeer
    *******************************/
   class MyDragSourceContextPeer implements DragSourceContextPeer {
      
      /**
       * start a drag
       */
      public void startDrag(DragSourceContext dsc, Cursor c, Image dragImage, Point imageOffset) throws InvalidDnDOperationException {
         System.out.println("peer start Drag");
      }
      
      /**
       * return the current drag cursor
       */
      public Cursor getCursor() {
         return null;
      }
      
      /**
       * set the current drag cursor
       */
      public void setCursor(Cursor c) throws InvalidDnDOperationException {
         System.out.println("peer setCursor");
      }
      
      /**
       * notify the peer that the Transferables DataFlavors have changed
       */
      public void transferablesFlavorsChanged() {
         System.out.println("peer transferablesFlavorsChanged");
      }
      
   }
   
}
