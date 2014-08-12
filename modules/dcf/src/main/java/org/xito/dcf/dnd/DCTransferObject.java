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

import java.util.*;
import java.io.*;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.datatransfer.*;
import org.xito.dcf.*;
import org.xito.blx.*;

/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.5 $
 * @since $Date: 2007/11/28 03:52:39 $
 */
public class DCTransferObject extends BLXTransferObject {
   
   /**
    * Creates new TransferObject for a Specified DCComponent
    * @param pComp Component to maintain data transfer data for
    */
   public DCTransferObject(DCComponent pComp) {
      super(pComp);
      dataFlavors.add(DCComponent.DCF_FLAVOR);
      dataFlavors.add(DCComponent.DCF_REF_FLAVOR);
   }
   
   /**
    * Gets the Transfer Data for a given DataFlavor
    * @param pFlavor requested
    * @return Data for the specified flavor
    */
   public Object getTransferData(DataFlavor pFlavor) throws UnsupportedFlavorException, IOException {
      if(isDataFlavorSupported(pFlavor)==false) throw new UnsupportedFlavorException(pFlavor);
      
      //If it is the Local Ref flavor then Return the Object Itself
      if(pFlavor.equals(DCComponent.DCF_REF_FLAVOR)) {
         return dataObj;
      }
      
      //DCF Component just return data for BLX XML 
   if(pFlavor.equals(DCComponent.DCF_FLAVOR)) {
         return super.getTransferData(BLXDataFlavor.XML_FLAVOR);
      }
      
      return super.getTransferData(pFlavor);
   }
         
   /**
    * Check to see if the DCComponent Data Flavor is supported in this 
    * Drop Event
    * @param evt
    * @return true if DCComponent Data Flavor is supported
    */
   public static boolean isDCF_FlavorSupported(DropTargetDropEvent evt) {
      if(evt.isDataFlavorSupported(DCComponent.DCF_FLAVOR))
         return true;
      else
         return false;
   }
   
   /**
    * Check to see if the DCComponent Data Flavor is supported in this 
    * Drop Event
    * @param evt
    * @return true if DCComponent Data Flavor is supported
    */
   public static boolean isDCF_REF_FlavorSupported(DropTargetDropEvent evt) {
      if(evt.isDataFlavorSupported(DCComponent.DCF_REF_FLAVOR))
         return true;
      else
         return false;
   }
}
