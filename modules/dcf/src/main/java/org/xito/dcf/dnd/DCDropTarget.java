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

import java.awt.dnd.*;
import java.awt.dnd.peer.*;
import java.awt.datatransfer.*;
import java.awt.*;

/**
 *
 * @author $Author: drichan $
 * @version $Revision: 1.5 $
 * @since $Date: 2007/11/28 03:52:39 $
 */
public class DCDropTarget extends DropTarget {
   //static DropTargetContext systemDropTargetContext;
   DropTargetContext dropTargetContext;
   
   /** Creates new DropTarget */
   public DCDropTarget() {
      super();
      dropTargetContext = this.createDropTargetContext();
   }
   
   public DCDropTarget(Component pComp, DropTargetListener pListener) {
      super(pComp, pListener);
      dropTargetContext = this.createDropTargetContext();
   }
   
   public DropTargetContext getDropTargetContext() {
            
      return dropTargetContext;
   }
   
   public DropTargetContext createDropTargetContext() {
      if(dropTargetContext == null) {
         dropTargetContext = super.createDropTargetContext();
      }
      
      dropTargetContext.removeNotify();
      dropTargetContext.addNotify(new MyDropTargetContextPeer(this, DropManagerImpl.getDropManager().getCurrentTransferable()));
      
      return dropTargetContext;
   }
   
   /**   * Peer of DropTargetContext
    */
   class MyDropTargetContextPeer implements DropTargetContextPeer {
      DropTarget dropTarget;
      Transferable transferObject;
      
      public MyDropTargetContextPeer(DropTarget pTarget, Transferable pTransfer) {
         dropTarget = pTarget;
         transferObject = pTransfer;
      }
      
      public void rejectDrag() {
      }
      
      public void dropComplete(boolean pComplete) {
      }
      
      public void acceptDrag(int pAction) {
         System.out.println("here I am");
      }
      
      public Transferable getTransferable() throws InvalidDnDOperationException {
         return transferObject;
      }
      
      public boolean isTransferableJVMLocal() {
         return true;
      }
      
      public void acceptDrop(int pAction) {
      }
      
      public void setTargetActions(int pAction) {
      }
      
      public DropTarget getDropTarget() {
         return dropTarget;
      }
      
      public void rejectDrop() {
      }
      
      public int getTargetActions() {
         return dropTarget.getDefaultActions();
      }
      
      public DataFlavor[] getTransferDataFlavors() {
         return transferObject.getTransferDataFlavors();
      }
      
   }
}
