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

import java.net.*;
import org.w3c.dom.*;

/**
 *
 * @author  Deane
 */
public class BLXDocument {
   
   private BLXAliasManager aliasManager;
   private BLXElement blxElement;
   
   /** Creates a new instance of BLXDocument */
   public BLXDocument(Document doc, URL contextURL, BLXAliasManager aliasManager)  throws DOMException, InvalidBLXXMLException {
      this.aliasManager = aliasManager;
      processDoc(doc, contextURL);
   }
   
   /**
    * Gets the Root BLXElement of this Document
    */
   public BLXElement getBLXElement() {
      return blxElement;
   }
   
   /**
    * Gets this Documents AliasManager
    */
   public BLXAliasManager getAliasManager() {
      return aliasManager;
   }
   
   /**
    * Process this document pulling out first alias entries and looking for first BLXElement
    */
   private void processDoc(Document doc, URL contextURL) throws DOMException, InvalidBLXXMLException {
      Element docE = doc.getDocumentElement();
      
      //Check to see if it is a BLX document
      String nodeName = BLXUtility.getLocalNodeName(docE);
      if(nodeName.equals(BLXElement.BLX_DOCUMENT_NAME) == false) {
         throw new InvalidBLXXMLException("Document does not define a BLX Document. No <"+
               BLXElement.BLX_NS+":"+BLXElement.BLX_DOCUMENT_NAME+"> defined. Found element: <"+docE.getNodeName()+"> instead.");
      }
      
      NodeList nodes = docE.getChildNodes();
      for(int i=0;i<nodes.getLength();i++) {
         Node child = nodes.item(i);
         
         //Skip all nodes but elements
         if(child.getNodeType() != Node.ELEMENT_NODE) continue;
         
         Element e = (Element)child;
         nodeName = BLXUtility.getLocalNodeName(e);
         //Process Alias Elements
         if(nodeName.equals(BLXExtAliasElement.BLX_ALIAS_NODE_NAME)) {
            processAlias(e, contextURL);
         }
         
         //Process first BLXElement
         if(nodeName.equals(BLXElement.BLX_OBJ_NODE_NAME) || e.getNodeName().equals(BLXElement.BLX_COMP_NODE_NAME)) {
            blxElement = new BLXElement(e, contextURL);
            return;
         }
      }
      
      //If I got here then I didn't get a BLXElement and this is bad
      throw new InvalidBLXXMLException("BLX Document does not contain a blx object description");
   }
   
   /**
    * Process an Extension Alias Element
    */
   private void processAlias(Element aliasElement, URL contextURL) throws DOMException, InvalidBLXXMLException {
      
      aliasManager.addExtAlias(new BLXExtAliasElement(aliasElement, contextURL));
   }
}
