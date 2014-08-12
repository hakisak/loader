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
import java.awt.dnd.*;
import java.awt.datatransfer.*;

/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.5 $
 * @since $Date: 2007/11/28 03:52:39 $
 */
/**
 * Class used to Create a DropTargetDrop Event and bypass DropTargetContext
 */
public class DCDropTargetDropEvent extends DropTargetDropEvent {
   DropTarget dropTarget;
   DragSource dragSource;
   Transferable transferObject;
   int dropAction;
   DragGestureEvent trigger;
   
   public DCDropTargetDropEvent(DragSource pSource,
   DropTarget pTarget,
   Point pCursorLocn,
   int pDropAction,
   int pSrcActions,
   boolean pIsLocal,
   Transferable pTransfer,
   DragGestureEvent pTrigger) {
      //This DropTargetContext is not used. I just have to pass one to the base class
      super(pTarget.getDropTargetContext(), pCursorLocn, pDropAction, pSrcActions, pIsLocal);
      
      dropTarget = pTarget;
      dragSource = pSource;
      transferObject = pTransfer;
      trigger = pTrigger;
   }
   
   public DragGestureEvent getTrigger() {
      return trigger;
   }
   
   public int getDropAction() {
      return dropAction;
   }
   
   public void acceptDrop(int pDropAction) {
      dropAction = pDropAction;
   }
   
   public void dropComplete(boolean pSuccess) {
      DragSourceContext _ctx = DropManagerImpl.getDropManager().getCurrentDragSourceContext();
      DragSourceDropEvent _event = new DragSourceDropEvent(_ctx, dropAction, pSuccess);
      _ctx.dragDropEnd(_event);
   }
   
   public Transferable getTransferable() {
      return transferObject;
   }
   
   public void rejectDrop() {
      DragSourceContext _ctx = DropManagerImpl.getDropManager().getCurrentDragSourceContext();
      DragSourceDropEvent _event = new DragSourceDropEvent(_ctx, dropAction, false);
      _ctx.dragDropEnd(_event);
   }
   
   public boolean isDataFlavorSupported(DataFlavor pFlavor) {
      return transferObject.isDataFlavorSupported(pFlavor);
   }
   
   
}
