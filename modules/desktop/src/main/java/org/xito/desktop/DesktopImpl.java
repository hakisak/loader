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

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.util.*;
import java.util.logging.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.plaf.*;
import org.w3c.dom.*;

import org.xito.dialog.*;
import org.xito.xmldocs.*;
import org.xito.dcf.*;
import org.xito.dcf.dnd.*;
import org.xito.dcf.event.*;
import org.xito.blx.*;


/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.6 $
 * @since $Date: 2007/06/30 23:18:06 $
 */
public class DesktopImpl extends AbstractBLXObject implements Desktop {
   
   private DCDropTarget desktopDropTarget;
   private DropTargetListener dropListener;
   private Vector dropListeners = new Vector();
   private Frame dialogOwner = new Frame();
   
   private BLXCompFactory componentFactory;
   private DesktopService desktopService;
   private XMLDocumentService docService;
   
   private MyComponentListener compListener;
   private BLXCollection compCollection = new BLXCollection(BLXCollection.LIST);
   private boolean isInstalled_flag = false;
   private MasterTile masterTile;
   private BLXElement blxElement;
   private Node desktopElement;
   private boolean processedXML_flag = false;
   private boolean dirty_flag = true;
   private DesktopLayout layout;
   private ArrayList removedItems;
   
   private Logger logger = Logger.getLogger(DesktopImpl.class.getName());
   
   /** Creates new Desktop */
   public DesktopImpl() {
   
      super();
      
      //Create Component Listener
      compListener = new MyComponentListener();
      setDesktopLayout(new DesktopGridLayout());
    }
   
   /**
    * This will return a Node that represents this Components BLX Settings
    * This can be altered by the Container or Parent
    * @return dcf Node
    */
   public BLXElement getBLXElement() {
      //First Create the dcf Node for this Desktop
      if(blxElement !=null) return blxElement;
      
      blxElement = blxHelper.getBLXObjectElement();
      return blxElement;
   }
   
   /**
    * This Method is called by the Desktop Service
    * When this Desktop has been made the current Desktop
    */
   public void desktopInstalled() {
      isInstalled_flag = true;
      
      //Setup DND Support
      setupDNDSupport();
      
      //If we haven't processed OUR XML yet then do so now
      if(processedXML_flag == false) {
         if(blxElement != null && blxElement.getObjectType() == BLXElement.OBJECT_TYPE && blxElement.getClassName().equals(this.getClass().getName())) {
            //Load the Components from the XML Node Passed
            loadComponentsFromXML();
         }
         processedXML_flag = true;
      }
      
      //Show All Managed Components
      BLXObject _objs[] = compCollection.getManagedObjects();
      for(int i=0;i<_objs.length;i++) {
         BLXObject _obj = _objs[i];
         if(_obj instanceof DCComponent)
            ((DCComponent)_obj).setVisible(true);
      }
   }
   
   /**
    * Get the Desktop Element which is a child of the dcf:component Desktop Node
    */
   private Element getDesktopElement(Node pNode) {
      NodeList _nodes = pNode.getChildNodes();
      for(int i=0;i<_nodes.getLength();i++) {
         if((_nodes.item(i).getNodeType()==Node.ELEMENT_NODE) && (_nodes.item(i).getNodeName().equals(XML_NODE_NAME))) {
            return (Element)_nodes.item(i);
         }
      }
      
      return null;
   }
   
   /**
    * This Method is called by the Desktop Service when
    * This Desktop has been removed as the current Desktop
    */
   public void desktopUninstalled() {
      isInstalled_flag = false;
      
      //Remove DND Support
      removeDNDSupport();
      
      //Hide All Managed Components
      Iterator _objs = compCollection.getObjectMap().values().iterator();
      while(_objs.hasNext()) {
         Object _obj = _objs.next();
         if(_obj instanceof Component)
            ((Component)_obj).setVisible(false);
      }
   }
   
   /**
    * Returns True if this Desktop is Currently
    * Installed as the Current Desktop
    * @return installed status
    */
   public boolean isInstalled() {
      return isInstalled_flag;
   }
   
   private void loadComponentsFromXML() {
      
      //First Child should be a Desktop Tag
      if(desktopElement == null) {
         logger.info("No Desktop in XML");
         return;
      }
      
      Element _containerElement = null;
      //Walk through All Children and Load Them up
      NodeList _list = desktopElement.getChildNodes();
      for(int i=0;i<_list.getLength();i++) {
         
         Node _node = _list.item(i);
         //Only Process Elements
         if(_node.getNodeType() != Node.ELEMENT_NODE) continue;
         //Check for Container Element
         if(_node.getNodeName().equals(compCollection.CONTAINER_NODE_NAME)) {
            _containerElement = (Element)_node;
         }
      }
      
      //Load the Container
      boolean successful = compCollection.setXMLElement(_containerElement, blxHelper.getContextURL());
      
      if(successful) {
         //Place all Components on the Desktop
         BLXObject _objs[] = compCollection.getManagedObjects();
         for(int i=0;i<_objs.length;i++) {
            try {
               addDesktopComponent((DCComponent)_objs[i]);
               //Set the xml has changed flag to false because we just loaded it.
               ((DCComponent)_objs[i]).setIsDirty(false);
            }
            catch(ClassCastException _exp) {
               //This is not a DC Component so just keep it in the compCollection to be persited later
            }
         }
         dirty_flag = false;
      }
      else dirty_flag = true;
   }
   
   private void removeDNDSupport() {
      //Drop Target Support
      dropListener = null;
      desktopDropTarget = null;
      
      //Add self as default Drop Target
      DropManagerImpl.getDropManager().setDefaultDropTarget(null);
   }
   
   private void setupDNDSupport() {
      //Drop Target Support
      dropListener = new MyDropTargetListener();
      desktopDropTarget = new DCDropTarget(null, dropListener){
         public String toString() {return "desktop target";}};
         
         //Add self as default Drop Target
         DropManagerImpl.getDropManager().setDefaultDropTarget(desktopDropTarget);
   }
   
   /**
    * Add a Drop Listener to the Desktop
    * A Drop Listener is notified when anything is dropped on the Desktop
    * @param Listener to receive events
    */
   public void addDesktopDropListener(DesktopDropListener pListener) {
      if(dropListeners.contains(pListener)==false) {
         dropListeners.addElement(pListener);
      }
   }
   
   /**
    * Remove a Drop Listener
    * @param Listener to Remove
    */
   public void removeDesktopDropListener(DesktopDropListener pListener) {
      if(dropListeners.contains(pListener)==true) {
         dropListeners.removeElement(pListener);
      }
   }
   
   /**
    * Fires Drop Events to all Drop Listeners. This occurs when Items are Dropped on the Desktop
    * @param Component that was dropped on the Desktop or created because of a Drop
    */
   private void fireDropEvents(DCComponent pComp) {
      for(int i=0;i<dropListeners.size();i++) {
         ((DesktopDropListener)dropListeners.elementAt(i)).drop(pComp);
      }
   }
   
   /**
    * Loads this Desktops Default Configuration
    */
   public void loadDefault() {
      
      if(masterTile == null) {
         
         //Get the Desktop Service if we haven't already
         if(desktopService == null) {
            desktopService = DesktopService.getDefaultService();
         }
         
         //Add Default Root Menu
         addDesktopObject(DesktopMenuModel.getRootModel());
         
         //Add Default Master Tile
         masterTile = new MasterTile();
         masterTile.setClosed(false);
         masterTile.setOnDesktop(true);
         masterTile.setLocation(desktopService.getDesktopSize().width - masterTile.getWidth(), 0);
         addDesktopComponent(masterTile);
      }
   }
   
   /**
    * Add an Object to this Container
    * @param pObject to add
    */
   public void addDesktopObject(BLXObject pObject) {
      
      //Add to Container
      if(compCollection.contains(pObject) == false) {
         compCollection.addManagedObject(0, pObject);
         dirty_flag = true;
      }
   }
   
   /**
    * Add an Object to this Container
    * @param pObject to add
    */
   public void addDesktopComponent(DCComponent pComp) {
      
      //Add to Container
      if(compCollection.contains(pComp) == false) {
         compCollection.addManagedObject(pComp);
         dirty_flag = true;
         
         //Setup Tiles Title
         //if(pComp instanceof DCTile) {
         //   ((DCTile)pComp).setShowTitle(true);
         //}
      }
      
      //place on Desktop
      if(pComp.isOnDesktop()==false) pComp.setOnDesktop(true);
            
      //Add Dispose Listener
      pComp.addDCComponentListener(compListener);
      
      //Let the DesktopLayout position the added Component
      if(layout != null) layout.positionComp(pComp);
      
      ((DCComponent)pComp).setVisible(true);
   }
   
   /**
    * remove an Object from this Container
    * @param pObject to remove
    */
   public void removeDesktopComponent(DCComponent pComp) {
      
      if(compCollection.contains(pComp)) {
         compCollection.removeManagedObject(pComp);
         pComp.removeDCComponentListener(compListener);
         
         dirty_flag = true;
         
         if(removedItems == null) removedItems = new ArrayList();
         
         removedItems.add(pComp.getBLXId());
      }
   }
   
   /**
    * Get the XML Data associated with this Object. The XML Data should is a single element
    * that this object uses to persist its state
    * @return the XML Data Element for this Component
    */
   public Element getDataElement() {
      
      Document _doc = BLXUtility.createDOMDocument();
      Element _element = _doc.createElement(Desktop.XML_NODE_NAME);
      
      //Get the Containers XML passing null to getXMLElement on the Collection
      //will cause all objects data element to be appended as well
      _element.appendChild(_doc.importNode(compCollection.getXMLElement(null), true));
      
      return _element;
   }
   
   /**
    * Return true if this components state has changed in a way that
    * Requires the objects compCollection to fetch new XML Data for the Object.
    * @return true if component has changed
    */
   public boolean isDirty() {
      if(dirty_flag) return true;
      
      //Ask All Children if they have changed
      BLXObject _objs[] = compCollection.getManagedObjects();
      for(int i=0;i<_objs.length;i++) {
         BLXObject _obj = _objs[i];
         if(_obj.isDirty()) return true;
      }
      
      return false;
   }
   
   /**
    * Set the BLX Element for this Component or Object
    * This should only be called when the object is first being created. Which
    * would normally be directly after the default constructor has been called.
    * @param blxElement for this
    */
   public void setBLXElement(BLXElement blxElement) {
      
      try {
         //Get the Desktop Service if we haven't already
         if(desktopService == null) {
            desktopService = DesktopService.getDefaultService();
         }
         
         //Get the Component Factory if we haven't already Done So
         if(componentFactory == null) {
            componentFactory = BLXCompFactory.getInstance();
         }
         
         //Get the Document Service
         if(docService == null) {
            docService = DefaultXMLDocumentService.getDefaultService();
         }
         
         //Create XMLWrapper
         if(blxElement != null) {
            blxHelper.setBLXElement(blxElement);
            desktopElement = blxElement.getDataElement();
            loadComponentsFromXML();
         }
         
         dirty_flag = false;
      }
      catch(Exception _exp) {
         //This is Bad both of these services are required
         _exp.printStackTrace();
         return;
      }
   }
   
   /**
    * Get the BLX Object instance ID for this object or null if the object does not have
    * an Id.
    * @return id
    */
   public String getBLXId() {
      return "desktop";
   }
   
   /**
    * Get all Desktop Components that are located between two points
    * @param rect area to get components for or null for all Components
    * @return array of DCComponents
    */
   public DCComponent[] getDesktopComponents(Rectangle rect) {
      ArrayList list = new ArrayList();
      BLXObject objs[] = compCollection.getManagedObjects();
      for(int i=0;i<objs.length;i++) {
         try {
            DCComponent comp = (DCComponent)objs[i];
            Point loc = comp.getLocation();
            if(rect == null) list.add(comp);
            else if(rect.contains(loc)) list.add(comp);
         }
         catch(ClassCastException castExp) {
            //Ignore it
            continue;
         }
      }
      
      DCComponent comps[] = new DCComponent[0];
      comps = (DCComponent[])list.toArray(comps);
      
      return comps;
   }
   
   /** Sets the Layout for the Desktop
    * @param DesktopLayout or null for no layout
    *
    */
   public void setDesktopLayout(DesktopLayout layout) {
      this.layout = layout;
      layout.setDesktop(this);
   }
   
   /**
    * Get the current Desktop Layout
    * @return IDesktopLayout
    */
   public DesktopLayout getDesktopLayout() {
      return this.layout;
   }
   
   /** Return true if the Desktop contains the Specified Object
    * @param blxObject
    * @return true if this Desktop contains the blxObject
    *
    */
   public boolean contains(BLXObject obj) {
      return compCollection.contains(obj);
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
      
      Element blxElement = getBLXElement().getDOMElement();
      Document doc = BLXUtility.createBLXDocument();
      blxElement = (Element)doc.importNode(blxElement,true);
      doc.getDocumentElement().appendChild(blxElement);
      Element dataElement = doc.createElement(Desktop.XML_NODE_NAME);
      
      //Get the Containers XML
      String currentPath = storageHandler.getCurrentPath();
      dataElement.appendChild(doc.importNode(compCollection.getXMLElement(currentPath + docService.PATH_SEPERATOR), true));
      blxElement.appendChild(dataElement);
      
      //Store the Document
      boolean successful = false;
      try {
         storageHandler.storeDoc(getBLXId() + BLXElement.FILE_EXT, doc);
         successful = true;
      }
      catch(IOException ioExp) {
         ioExp.printStackTrace();
         successful = false;
      }
      
      //Walk Through Each Managed Component and Store There BLX XML
      BLXObject comps[] = compCollection.getManagedObjects();
      for(int i=0;i<comps.length;i++) {
         
         BLXObject blxObj = comps[i];
         String id = blxObj.getBLXId();
         Element blxNode = blxObj.getBLXElement().getDOMElement();
         
         if(blxObj.isDirty() || allChildren) {
            String name = id + BLXElement.FILE_EXT;
            try {
               blxObj.store(allChildren, storageHandler);
            }
            //Catch Throwable because if we can't write One Object we still want to write the rest
            catch(Throwable exp) {
               exp.printStackTrace();
               successful = false;
            }
         }
      }
      
      //Remove Removed Items
      if(removedItems != null && removedItems.isEmpty()==false) {
         int count = removedItems.size();
         for(int i=count-1;i>=0;i--) {
            try {
               String name = (String)removedItems.get(i);
               storageHandler.removeDoc(name + this.FILE_EXT);
               removedItems.remove(i);
            }
            catch(IOException ioExp) {
               ioExp.printStackTrace();
            }
         }
      }
      
      //Update Dirty Flag because it has been stored
      if(successful) {
         dirty_flag = false;
      }
   }
   
   /***************************
    * DCComponentListener for the Desktop
    ***************************/
   class MyComponentListener implements DCComponentListener {
      
      /**
       * Invoked when the component has been made visible.
       */
      public void componentShown(ComponentEvent event) {
      }
      
      /**
       * Invoked when the component's position changes.
       */
      public void componentMoved(ComponentEvent event) {
      }
      
      /**
       * This event is fired when a DCComponent is placed on the Desktop.
       */
      public void componentPlacedOnDesktop(DCComponentEvent event) {
      }
      
      /**
       * This event is fired when a DCComponent is placed on the Desktop.
       */
      public void componentRemovedFromDesktop(DCComponentEvent event) {
         //Remove it from our Managed Components
         removeDesktopComponent(event.getComponent());
      }
      
      /**
       * Invoked when the component's size changes.
       */
      public void componentResized(ComponentEvent event) {
      }
      
      /**
       * Invoked when the component has been made invisible.
       */
      public void componentHidden(ComponentEvent event) {
      }
      
      /**
       * This event is fired when a Component is disposed
       */
      public void componentDisposed(DCComponentEvent event) {
         //Remove it from our Managed Components
         removeDesktopComponent(event.getComponent());
      }
   }
   
   /***************************
    * Drop Target Listener for the Desktop
    ***************************/
   class MyDropTargetListener implements DropTargetListener {
      
      public void dragExit(DropTargetEvent pEvent) {
         logger.fine("Drag Exit Desktop");
      }
      
      public void dragEnter(DropTargetDragEvent evt) {
         logger.fine("Drag Enter Desktop");
      }
      
      /**
       * Drop event for when objects are dropped on the DeskTop
       */
      public void drop(DropTargetDropEvent evt) {
         
         //check for DC Component Drop
         boolean complete = dropDCComp(evt);
         
         //check for BLX Action Drop
         if(!complete) {
            complete = dropBLXAction(evt);
         }
         
         //Data flavor not supproted
         if(!complete) {
            logger.fine("Data Flavor not Supported");
            //Reject the Drop
            evt.dropComplete(false);
            
            String title = DesktopService.resources.getString("drop.error.title");
            String msg = DesktopService.resources.getString("drop.dataflavor.notsupported");
            DialogManager.showError(null, title, msg, null);
         }
         
         return;
      }
      
      /**juu
       * Checked to see if a DCComponent is supported as a Dataflavor and complete the event
       * @param evt
       * @preturn true if the drop was completed false otherwise
       */
      private boolean dropDCComp(DropTargetDropEvent evt) {
         
         //Check to see if desktop component or BLX Action is supported
         if(DCTransferObject.isDCF_REF_FlavorSupported(evt)==false) {
            return false;
         }
         
         try {
            DCDropTargetDropEvent dcEvent = (DCDropTargetDropEvent)evt;
            Point point = dcEvent.getLocation();
            Point offset = dcEvent.getTrigger().getDragOrigin();
                    
            Transferable transfer = evt.getTransferable();
            DCComponent comp = (DCComponent)transfer.getTransferData(DCComponent.DCF_REF_FLAVOR);
                        
            //place component on desktop
            if(comp.isOnDesktop() == false) comp.setOnDesktop(true);
        
            comp.setLocation(point.x - offset.x, point.y - offset.y);
            comp.setVisible(false); //addManagedObject will set it to Visible
            
            //Fire Drop Events to Drop Listeners
            fireDropEvents(comp);
            addDesktopComponent(comp);
            logger.fine("DCComponent: " + comp.getClass().getName() + " dropped on Desktop at:" + point.toString());
            
            evt.dropComplete(true);
            
            return true;
         }
         catch(UnsupportedFlavorException badFlavor) {
            badFlavor.printStackTrace();
         }
         catch(IOException ioExp) {
            ioExp.printStackTrace();
         }
         
         //Some exception must have happened
         return false;
      }
      
      /**
       * Checked to see if a BLX Action is supported as a Dataflavor and complete the event
       * @param evt
       * @return true if the drop was completed false otherwise
       */
      private boolean dropBLXAction(DropTargetDropEvent evt) {
         
         //Check to see if desktop component or BLX Action is supported
         if(BLXTransferObject.isBLXActionDataFlavorSupported(evt)==false) {
            return false;
         }
         
         try {
            //Get Location of Drop Point
            Point point = evt.getLocation();
            
            Transferable transfer = evt.getTransferable();
            Object obj = BLXTransferObject.getBLXObjFromTransferable(transfer, BLXDataFlavor.ACTION_FLAVOR);
            Action action = (Action)obj;
            
            //Create a Tile for this Action
            DCTile tile = new DCTile();
            tile.setAction(action);
            tile.setOnDesktop(true);
            tile.setLocation(point);
                        
            tile.setVisible(false); //addDesktopComponent will set it to Visible
            
            //Fire Drop Events to Drop Listeners
            fireDropEvents(tile);
            addDesktopComponent(tile);
            logger.fine("BLXAction: " + action.getClass().getName() + " dropped on Desktop at:" + point.toString());
            return true;
         }
         catch(UnsupportedFlavorException badFlavor) {
            String title = DesktopService.resources.getString("drop.error.title");
            String msg = DesktopService.resources.getString("drop.dataflavor.notsupported");
            DialogManager.showError(null, title, msg, badFlavor);
         }
         catch(IOException ioExp) {
            String title = DesktopService.resources.getString("drop.error.title");
            String msg = DesktopService.resources.getString("drop.ioexpception");
            DialogManager.showError(null, title, msg, ioExp);
         }
         catch(Exception exp) {
            
            exp.printStackTrace();
            String title = DesktopService.resources.getString("drop.error.title");
            String msg = "Unknown Error";
            
            DialogManager.showError(null, title, msg, exp);
         }
         
         //Some exception must have happened
         return false;
      }
      
      /**
       * Drag Over
       */
      public void dragOver(DropTargetDragEvent pDragEvent) {
      }
      
      /**
       * Drop Action Changed
       */
      public void dropActionChanged(DropTargetDragEvent pEvent) {
      }
      
   }
}
