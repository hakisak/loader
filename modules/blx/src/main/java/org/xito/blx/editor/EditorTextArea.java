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

package org.xito.blx.editor;

import java.io.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.swing.*;

import org.xito.blx.*;

/**
 *
 * @author  Deane
 */
public class EditorTextArea extends JTextArea implements DropTargetListener {
   
   DropTarget dropTarget;
   
   /** Creates a new instance of EditorTextArea */
   public EditorTextArea() {
      super();
      init();
   }
   
   private void init() {
      dropTarget = new DropTarget(this, this);
      setDropTarget(dropTarget);
   }
   
   public void dragEnter(DropTargetDragEvent dtde) {
   }
   
   public void dragExit(DropTargetEvent dte) {
   }
   
   public void dragOver(DropTargetDragEvent dtde) {
      
      dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
     
   }
   
   public void drop(DropTargetDropEvent evt) {
      if(evt.isDataFlavorSupported(BLXDataFlavor.XML_FLAVOR) == false) {
         evt.dropComplete(false);
      }
      
      try {
         evt.acceptDrop(evt.getDropAction());
         
         Reader reader = BLXDataFlavor.XML_FLAVOR.getReaderForText(evt.getTransferable());  
         StringBuffer strBuf = new StringBuffer();
         char buf[]=new char[1024];
         int cbyte = 0;
         cbyte = reader.read(buf);
         while(cbyte !=-1) {
            strBuf.append(buf,0,cbyte);
            cbyte = reader.read(buf);
         }
         reader.close();
         this.setText(strBuf.toString());
         
         evt.dropComplete(true);
         return;
      }
      catch(Exception exp) {
         exp.printStackTrace();
      }
      
      evt.dropComplete(false);
   }
   
   public void dropActionChanged(DropTargetDragEvent dtde) {
   }
   
}
