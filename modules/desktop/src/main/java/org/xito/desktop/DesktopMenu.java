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

package org.xito.desktop;

import org.w3c.dom.*;
import org.xito.blx.*;
import org.xito.dcf.*;
import org.xito.xmldocs.*;

/**
 *
 *
 */
public class DesktopMenu extends DCMenu {
  
  public static final String MENU_NODE = "menu";
  public static final String IS_TORN_OFF_ATTR = "isTornOff";
  public static final String MODEL_ID_ATTR = "modelID";
    
  /**
   * Creates a new DesktopMenu required for BLX Support
   */ 
  public DesktopMenu() {
    super();
  }
  
  /**
   * Get the XML Data associated with this Object. The XML Data should is a single element
   * that this object uses to persist its state
   * @return the XML Data Element for this Component
   */
  public Element getDataElement() {
    
    DCMenuModel model = getModel();
    
    //Can't store non-BLX Models for Now
    if((model instanceof BLXObject)==false) return null;
    BLXObject blxModel = (BLXObject)model;
    String model_id = blxModel.getBLXId();
         
    try {
       /*
      
      
      String name = DesktopService.DESKTOP_PATH + IXMLDocumentService.PATH_SEPERATOR + model_id + BLXElement.FILE_EXT;
      
      //First store the Model if its Dirty
      if(blxModel.isDirty()) {
        IXMLDocumentService docService = XMLDocumentService.getDefaultService();
        
        //Store the Document
        Document document = CompFactory.getDefaultFactory().getBLXDocument(blxModel);
        docService.storeDocument(name, document);
      }
      */
      
      //Now create our Data Element
      Document doc = BLXUtility.createDOMDocument();
      Element data = doc.createElement(MENU_NODE);
      data.setAttribute(IS_TORN_OFF_ATTR, ""+this.isTearOff());
      data.setAttribute(MODEL_ID_ATTR, model_id);
      
      return data;
    }
    //Catch Throwable because if we can't write One Object we still want to write the rest
    catch(Throwable exp) {
      exp.printStackTrace();
    }
    
    return null;
  }
  
  
  /**
   * Store the BLX Object. This will store the objects entire child state or 
   * its nested children could use the optional BLXDocumentFactory 
   * to persist each of its children. 
   * @param allChildren true causes this object to call getDataElement on all its children false means 
   *   only dirty children
   * @param BLXDocumentFactory child objects can optionally have their state stored in seperate
   *  documents. 
   * @return the XML Data Element for this Component
   */
  //public void store(boolean allChildren, IBLXDocumentFactory docFactory) {
    /*
    //We Store this Menus Model in the Desktop Directory
    DCMenuModel model = getModel();
    //Can't store non-BLX Models for Now
    if((model instanceof IBLXObject)==false) return null;
    IBLXObject blxModel = (IBLXObject)model;
    
    try {
      String model_id = blxModel.getBLXId();
      
      String name = DesktopService.XML_DOC_DIR + IXMLDocumentService.PATH_SEPERATOR + model_id + BLXElement.FILE_EXT;
      
      //First store the Model if its Dirty
      if(blxModel.isDirty()) {
        IXMLDocumentService docService = XMLDocumentService.getDefaultService();
        
        //Store the Document
        Document document = CompFactory.getDefaultFactory().getBLXDocument(blxModel);
        docService.storeDocument(name, document);
      }
      
      //Now create our Data Element
      Document doc = BLXUtility.createDOMDocument();
      Element data = doc.createElement(MENU_NODE);
      data.setAttribute(IS_TORN_OFF_ATTR, ""+this.isTearOff());
      data.setAttribute(MODEL_ID_ATTR, model_id);
      
      return data;
    }
    //Catch Throwable because if we can't write One Object we still want to write the rest
    catch(Throwable exp) {
      exp.printStackTrace();
    }
    
    return null;
    */
  //}
  
  /**
   * Set the BLX Element for this Component or Object
   * This should only be called when the object is first being created. Which
   * would normally be directly after the default constructor has been called.
   * @param pElement for this
   */
  public void setBLXElement(BLXElement pElement) {
    super.setBLXElement(pElement);
    Element data = pElement.getDataElement();
    
    //Set the Torn Off State
    String tornOff = data.getAttribute(IS_TORN_OFF_ATTR);
    setTearOff(Boolean.valueOf(tornOff).booleanValue());
    
    //Set the Model    
    String model_id = data.getAttribute(MODEL_ID_ATTR);
    if(model_id != null) {
      setModel(DesktopMenuModel.getMenuModel(model_id));
    }
  }
}


