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

package org.xito.blx;

import java.awt.dnd.DropTargetDropEvent;
import java.awt.datatransfer.*;
import java.util.*;
import java.io.*;
import org.w3c.dom.*;

/**
 *
 * @author $Author: drichan $
 * @version $Revision: 1.6 $
 * @since $Date: 2007/09/02 00:42:59 $
 */
public class BLXTransferObject implements Transferable {
   
   protected HashSet dataFlavors = new HashSet();
   protected transient BLXObject dataObj;
   
   /**
    * Create a Default BLXTransfer Object
    * @param obj that this transferable is for
    */
   public BLXTransferObject(BLXObject obj) {
      addAllBLXFlavors(obj);
   }
   
   /**
    * Add all Basic BLX Data Flavors specified in the BLXDataFlavor Class
    * @param obj
    */
   private void addAllBLXFlavors(BLXObject obj) {
      dataObj = obj;
      
      //XML Data Flavor
      dataFlavors.add(BLXDataFlavor.XML_FLAVOR);
      
      //Action Flavor
      if(obj instanceof javax.swing.Action) {
         dataFlavors.add(BLXDataFlavor.ACTION_FLAVOR);
      }
   }
   
   /**
    * Gets the Transfer Data for a given DataFlavor. This class supports the
    * basic flavors specified in the BLXDataFlavor class.
    * @param pFlavor requested
    * @return Data for the specified flavor
    */
   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
      if(isDataFlavorSupported(flavor)==false) throw new UnsupportedFlavorException(flavor);
      
      //Get the Objects BLX XML as the Data
      if(flavor.equals(BLXDataFlavor.XML_FLAVOR)) {
         
         StringWriter writer = new StringWriter();
         BLXUtility.writeBLXObject(dataObj, writer);
         
         return new ByteArrayInputStream(writer.toString().getBytes());
      }
      
      throw new UnsupportedFlavorException(flavor);
   }
   
   /**
    * Get all supported DataFlavors
    * @return array of DataFlavors
    */
   public DataFlavor[] getTransferDataFlavors() {
      if(dataFlavors == null) {
         return new DataFlavor[0];
      }
      
      DataFlavor[] _array = new DataFlavor[dataFlavors.size()];
      return (DataFlavor[])dataFlavors.toArray(_array);
   }
   
   /**
    * Returns true if a given DataFlavor is Supported
    * @return boolean
    */
   public boolean isDataFlavorSupported(DataFlavor flavor) {
      if(dataFlavors.contains(flavor)) return true;
      else return false;
   }
   
   /**
    * Adds a Dataflavor to the Transferable
    * @param pFlavor to Add
    * @param pData for the Flavor
    */
   protected void addDataFlavor(DataFlavor flavor) {
      //Check for Desktop Component Support
      dataFlavors.add(flavor);
   }
   
   /**
    * Check to see if the BLXDataFlavor is supported in a DropTargetDropEvent. This is
    * a helper method for DropTargets to use during Drop Operations
    *
    * @param evt
    * @return true if the BLXDataFlavor is supported in this drop event
    */
   public static boolean isBLXDataFlavorSupported(DropTargetDropEvent evt) {
      if(evt.isDataFlavorSupported(BLXDataFlavor.XML_FLAVOR))
         return true;
      else
         return false;
   }
   
   /**
    * Check to see if a BLXActionDataFlavor is supported in a DropTargetDropEvent
    * @param evt
    * @return true if the BLXActionDataFlavor is supported
    */
   public static boolean isBLXActionDataFlavorSupported(DropTargetDropEvent evt) {
      if(evt.isDataFlavorSupported(BLXDataFlavor.ACTION_FLAVOR))
         return true;
      else
         return false;
   }
   
   /**
    * Helper method to read in the BLX_FLAVOR data from
    * a Transferables getTransferData and return a BLXObject for it
    * @param transferable
    * @return BLXObject
    */
   public static BLXObject getBLXObjFromTransferable(Transferable transfer)
   throws ClassNotFoundException, InstantiationException, InvalidBLXXMLException, UnsupportedFlavorException, IOException {
      
      return getBLXObjFromTransferable(transfer, BLXDataFlavor.XML_FLAVOR);
   }
   
   /**
    * Helper method to read in the BLX_FLAVOR data from
    * a Transferables getTransferData and return a BLXObject for it
    * @param transferable
    * @return BLXObject
    */
   public static BLXObject getBLXObjFromTransferable(Transferable transfer, DataFlavor flavor)
   throws ClassNotFoundException, InstantiationException, InvalidBLXXMLException, UnsupportedFlavorException, IOException {
      
      Reader reader = flavor.getReaderForText(transfer);
      return (BLXObject)BLXCompFactory.getInstance().getObject(reader, null);
   }
   
}

