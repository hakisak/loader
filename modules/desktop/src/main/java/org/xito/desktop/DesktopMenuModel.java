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

import java.beans.*;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import org.xito.blx.*;
import org.xito.xmldocs.*;
import org.xito.dcf.*;
import org.xito.dcf.event.*;
import org.w3c.dom.*;

/**
 *
 * @author  Deane
 */
public class DesktopMenuModel extends DCMenuModel implements BLXObject, DCMenuModelListener {
   
   public static final String NODE_NAME_ATTR = "name";
   public static final String NODE_NAME = "menu_model";
   
   public static final String ROOT_MENU_ID = "xito_root_menu";
   public static final String ROOT_MENU_NAME = "Xito";
   public static final String NEW_ITEMS_MENU_NAME ="New";
   public static final String NEW_ITEMS_MENU_ID ="new_items_menu";
   
   private static HashMap loadedMenus = new HashMap();
   private static DesktopMenuModel rootModel;
   private static DesktopMenuModel newItemsModel;
      
   protected boolean dirty_flag;
   
   protected BLXHelper blxHelper;
   
   /** Creates a new instance of DesktopMenuModel */
   public DesktopMenuModel() {
      this(null, null);
   }
   
   /** Creates a new instance of DesktopMenuModel */
   public DesktopMenuModel(String name) {
      this(name, null);
   }
   
   /** Creates a new instance of DesktopMenuModel */
   public DesktopMenuModel(String name, String id) {
      super(name, id);
      blxHelper = new BLXHelper(this, id);
      
      //Place this model in the list of loaded menus
      loadedMenus.put(this.id, this);
      
      //Listen for Menu Change Events
      addMenuModelListener(this);
   }
   
   /**
    * Get a Menu Model for the Specified id
    */
   public static DesktopMenuModel getMenuModel(String id) {
      
      //Load it from loaded Menus
      if(loadedMenus.containsKey(id)) {
         return (DesktopMenuModel)loadedMenus.get(id);
      }
      
      //Load it from Docs Service
      String u = XMLDocumentService.PROTOCOL + ":" + Desktop.XML_DOC_DIR + XMLDocumentService.PATH_SEPERATOR + id + BLXObject.FILE_EXT;
      try {
         URL menuURL = new URL(u);
         DesktopMenuModel model = (DesktopMenuModel)BLXCompFactory.getInstance().getObject(menuURL);
         loadedMenus.put(model.getBLXId(), model);
         
         return model;
      }
      catch(Exception exp) {
         exp.printStackTrace();
      }
      
      return null;
   }
   
   /**
    * Get the Default Root Menu Model
    */
   public static synchronized DesktopMenuModel getRootModel() {
      if(rootModel != null) return rootModel;
      
      //Create the Default RootMenu
      rootModel = new DesktopMenuModel(ROOT_MENU_NAME, ROOT_MENU_ID);
      
      //Create the Default new Items Menu
      newItemsModel = new DesktopMenuModel(NEW_ITEMS_MENU_NAME, NEW_ITEMS_MENU_ID);
      newItemsModel.add(new CreateTileAction());
      newItemsModel.add(new CreateTileSetAction());
      newItemsModel.add(new CreateStickyNoteAction());
      newItemsModel.dirty_flag = true;
      rootModel.add(newItemsModel);
      
      rootModel.add(new SetupMenuAction());
      rootModel.add(new AboutAction());
      //rootModel.addSeperator();
      rootModel.add(new ExitAction());
      
      //Mark the RootMenu as Dirty because we didn't load it from XML
      rootModel.dirty_flag = true;
      
      return rootModel;
   }
   
   /** Get the BLX Element for this Component or Object
    * @return the BLXElement object that describes this type of Component.
    *
    */
   public BLXElement getBLXElement() {
      
      return blxHelper.getBLXObjectElement();
   }
   
   /**
    * Get the XML Data associated with this Object. The XML Data should is a single element
    * that this object uses to persist its state
    * @return the XML Data Element for this Component
    *
    */
   public Element getDataElement() {
      
      BLXCollection list = new BLXCollection(BLXCollection.LIST);
      Iterator it = items.iterator();
      while(it.hasNext()) {
         BLXObject blxObj = (BLXObject)it.next();
         String blxId = blxObj.getBLXId();
         Element blxNode = blxObj.getBLXElement().getDOMElement();
         
         list.addManagedObject(blxObj);
      }
      
    /*
      //Save the objects contents if required
      if(blxObj.isDirty()) {
        String fileName = DesktopService.XML_DOC_DIR + docService.PATH_SEPERATOR + blxId + BLXElement.FILE_EXT;
        try {
          Document itemDoc = list.getObjectDocument(blxObj);
     
          //Store the Document
          docService.storeDocument(fileName, itemDoc);
        }
        //Catch Throwable because if we can't write One Object we still want to write the rest
        catch(Throwable exp) {
          exp.printStackTrace();
        }
      }
    }
     */
      
      Document doc = BLXUtility.createDOMDocument();
      Element element = doc.createElement(NODE_NAME);
      element.setAttribute(NODE_NAME_ATTR, getName());
      
      //Get the Containers XML
      element.appendChild(doc.importNode(list.getXMLElement(null), true));
      
      return element;
   }
   
   /** Store the BLX Object. This will store the objects entire child state or
    * its nested children could use the optional IBLXStorageHandler
    * to persist each of its children.
    * @param allChildren true causes this object to call getDataElement on all its children false means
    *   only dirty children
    * @param IBLXStorageHandler child objects can optionally have their state stored in seperate
    *  documents using a Storage handler.
    * @return the XML Data Element for this Component
    *
    */
   public void store(boolean allChildren, BLXStorageHandler storageHandler) throws IOException {
      
      /**
       * The Default implementation calls CompFactory.getBLXDocument(this) and
       * stores a document that represents this objects entire child data state
       */
      
      Element blxElement = this.getBLXElement().getDOMElement();
      Document doc = blxElement.getOwnerDocument();
      blxElement.appendChild(doc.importNode(this.getDataElement(), true));
      
      doc = BLXCompFactory.getInstance().getBLXDocument(this);
      String name = this.getBLXId() + BLXElement.FILE_EXT;
      storageHandler.storeDoc(name, doc);
      
      this.dirty_flag = false;
   }
   
   /**
    * Return true if this components state has changed in a way that
    * Requires the objects container to fetch new XML Data for the Object.
    * @return true if component has changed
    *
    */
   public boolean isDirty() {
      if(dirty_flag) return true;
      
      //Now check all subitems
      Iterator it = items.iterator();
      while(it.hasNext()) {
         try {
            BLXObject blx = (BLXObject)it.next();
            if(blx.isDirty()) return true;
         }
         catch(ClassCastException exp) {
            //ignore this
         }
      }
      
      return false;
   }
   
   /**
    * Set the BLX Element for this Component or Object
    * This should only be called when the object is first being created. Which
    * would normally be directly after the default constructor has been called.
    * @param pElement for this
    *
    */
   public void setBLXElement(BLXElement blxElement) {
      
      Element data = null;
      
      try {
         blxHelper.setBLXElement(blxElement);
         data = blxElement.getDataElement();
      }
      catch(Throwable e) {
         e.printStackTrace();
         dirty_flag = true;
         return;
      }
      
      //Guess we have no Data
      if(data == null) return;
      
      //Set attributes on MenuModel
      super.name = data.getAttribute(NODE_NAME_ATTR);
      loadedMenus.remove(super.id);
      super.id = blxElement.getID();
      loadedMenus.put(super.id, this);
      
      //Now look for Container Element
      Element listElement = null;
      NodeList _list = data.getChildNodes();
      for(int i=0;i<_list.getLength();i++) {
         
         Node _node = _list.item(i);
         //Only Process Elements
         if(_node.getNodeType() != Node.ELEMENT_NODE) continue;
         //Check for Container Element
         if(_node.getNodeName().equals(BLXCollection.CONTAINER_NODE_NAME)) {
            listElement = (Element)_node;
         }
      }
      //Contains no Data
      if(listElement == null) return;
      
      //Load the Actions and Seperators from the collection
      BLXCollection collection = new BLXCollection(BLXCollection.LIST);
      boolean successful = collection.setXMLElement(listElement, blxElement.getContextURL());
      
      if(!successful) dirty_flag = true;
      else {
         //Place all Tiles into the TileSet
         BLXObject _objs[] = collection.getManagedObjects();
         for(int i=0;i<_objs.length;i++) {
            if(_objs[i] instanceof Action) {
               Action action = (Action)_objs[i];
               add(action);
            }
            if(_objs[i] instanceof DesktopMenuModel) {
               DesktopMenuModel subMenu = (DesktopMenuModel)_objs[i];
               add(subMenu);
            }
            else if(_objs[i].equals(DCMenuModel.SEPERATOR)) {
               addSeperator(i);
            }
         }
         
         dirty_flag = false;
      }
      
      //Set as Root
      if(rootModel == null && name.equals(ROOT_MENU_NAME))
         rootModel = this;
   }
   
   /**
    * The Menu Model has been changed.
    * @param event describing the change that was made to the Menu Model
    */
   public void menuChanged(DCMenuModelEvent event) {
      dirty_flag = true;
   }
   
   /**
    * Get the BLX Object instance ID for this object
    * @return id
    */
   public String getBLXId() {
      return blxHelper.getBLXId();
   }
   
}
