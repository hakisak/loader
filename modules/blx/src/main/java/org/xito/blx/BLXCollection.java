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

import java.awt.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.net.*;
import java.io.*;

import org.w3c.dom.*;

/**
 * The BLXCollection class represents a BLX Object representation of different collections.  The BLXCollection
 * can serve as a map, list, or set.  The contents of a BLXCollection should all be BLXObjects. The contents
 * of a BLXCollection can be serialized to XML like all other BLX Objects.
 *
 * @author drichan
 */
public class BLXCollection {

  private final static Logger logger = Logger.getLogger(BLXCollection.class.getName());
   
  /** Name of the node that will contain Container data */
  public final static String CONTAINER_NODE_NAME = "container";
  /** attribute name for the container type MAP or LIST */
  public final static String CONTAINER_TYPE_ATTR = "type";
  /** Name of the node that will contain each item */
  public final static String ITEM_NODE_NAME = "item";
  public final static String ITEM_ID_ATTR = "id";
  public final static String ITEM_INDEX_ATTR = "index";

  public final static String LIST_STR = "LIST";
  public final static String MAP_STR = "MAP";
  public final static String SET_STR = "SET";

  public final static int LIST = 1;
  public final static int MAP = 2;
  public final static int SET = 3;

  protected HashMap objectMap;
  protected ArrayList objectList;
  protected HashSet objectSet;
  private HashSet listeners = new HashSet();
  protected int type = MAP;

  /**
   * Create a new DCContainer based on a Map
   * @param pType LIST, MAP, SET
   */
  public BLXCollection(int pType) {
    if(pType == LIST || pType == MAP || pType == SET) type = pType;
    else type = MAP;

    if(type == MAP) objectMap = new HashMap();
    else if(type == LIST) objectList = new ArrayList();
    else if(type == SET) objectSet = new HashSet();
  }

  /**
   * Create a new DCContainer based on a MAP
   */
  public BLXCollection() {
    this(MAP);
  }

  /**
   * Add a Container Listener
   */
  public void addCollectionListener(BLXCollectionListener listener) {
    listeners.add(listener);
  }

  /**
   * Remove a Container Listener
   */
  public void removeCollectionListener(BLXCollectionListener listener) {
    listeners.remove(listener);
  }

  /**
   * Get the persistable Components that this Container contains
   */
  public BLXObject[] getManagedObjects() {

    if(type == MAP) {
      BLXObject objects[] = new BLXObject[objectMap.size()];
      return (BLXObject[])objectMap.values().toArray(objects);
    }
    else if(type == LIST) {
      BLXObject objects[] = new BLXObject[objectList.size()];
      return (BLXObject[])objectList.toArray(objects);
    }
    else {
      BLXObject objects[] = new BLXObject[objectSet.size()];
      return (BLXObject[])objectSet.toArray(objects);
    }
  }

  /**
   * Get the persistable Component by Index
   */
  public BLXObject getManagedObject(int pIndex) {
    if(type == MAP) {
      BLXObject _objs[] = getManagedObjects();
      if(pIndex < _objs.length) return _objs[pIndex];
      else return null;
    }
    else if(type == LIST) {
      if(pIndex < objectList.size()) return (BLXObject)objectList.get(pIndex);
      else return null;
    }
    else if(type == SET) {
      Iterator _it = objectSet.iterator();
      for(int i = 0;i <= pIndex;i++) {
        if(_it.hasNext()==false) return null;
        if(i == pIndex) return (BLXObject)_it.next();
      }
    }

    return null;
  }

  /**
   * Get the persistable Component for an ID
   * Map type Container only
   */
  public BLXObject getManagedObject(String pID) {

    if(type == MAP) return (BLXObject)objectMap.get(pID);
    else return null;
  }

  /**
   * Add an Object to this Container. ID is ignored for List or Set type Containers
   * @param id of the Object
   * @param pObject to add
   */
  public void addManagedObject(String id, BLXObject pObject) {

    //Skip if we already contain the Object
    if(contains(pObject)) return;

    if(type == MAP) {
      objectMap.put(id, pObject);
    }
    else if(type == LIST) {
      objectList.add(pObject);
    }
    else if(type == SET) {
      objectSet.add(pObject);
    }

    //Fire Event
    fireManagedObjectAdded(pObject, id);
  }

  /**
   * Add an Object to this Container. Index is used as the ID for a MAP type container.
   * Index is ignored for Set Types
   * @param index of the Object
   * @param pObject to add
   */
  public void addManagedObject(int index, BLXObject pObject) {

    //Skip if we already contain the Object
    if(contains(pObject)) return;

    if(type == MAP) {
      objectMap.put(index + "", pObject);
    }
    else if(type == LIST) {
      //Index is invalid so just add without using Index
      if(index >= objectList.size()) objectList.add(pObject);
      else objectList.add(index, pObject);
    }
    else if(type == SET) {
      objectSet.add(pObject);
    }

    //Fire Event
    fireManagedObjectAdded(pObject, index);
  }
  
  /**
   * Add an Object to this Container. For Sets the Object is simple added to the set.
   * For Lists the object is added to the end of the list. If the type is Map a runtime
   * Exception will be thrown
   * @param pObject to add
   */
  public void addManagedObject(BLXObject pObject) {

    //Skip if we already contain the Object
    if(contains(pObject)) return;
        
    if(type == MAP) {
      throw new RuntimeException("method addManagedObject(Object) can not be called on Map Collections");
    }
    else if(type == LIST) {
      //Index is invalid so just add without using Index
      objectList.add(pObject);
      
    }
    else if(type == SET) {
      objectSet.add(pObject);
    }

    //Fire Event
    fireManagedObjectAdded(pObject, this.size());
  }

  /**
   * remove an Object from this Container
   * @param pObject to remove
   */
  public void removeManagedObject(BLXObject pObject) {

    if(type == MAP && objectMap.values().contains(pObject)) {
      objectMap.values().remove(pObject);
      fireManagedObjectRemoved(pObject);
    }
    else if(type == LIST && objectList.contains(pObject)) {
      objectList.remove(pObject);
      fireManagedObjectRemoved(pObject);
    }
    else if(type == SET && objectSet.contains(pObject)) {
      objectList.remove(pObject);
      fireManagedObjectRemoved(pObject);
    }
  }

  /**
   * remove an Object from this Container Map Type Containers Only
   * @param pObject to remove
   */
  public void removeManagedObject(String pID) {

    if(type == MAP) {
      BLXObject _obj = (BLXObject)objectMap.get(pID);
      objectMap.remove(pID);
      fireManagedObjectRemoved(_obj);
    }
  }

  /**
   * Get the Map of Components
   */
  public Map getObjectMap() {
    return objectMap;
  }

  /**
   * Get the Map of Components
   */
  public java.util.List getObjectList() {
    return objectList;
  }

  /**
   * Get the Set of Components
   */
  public java.util.Set getObjectSet() {
    return objectSet;
  }

  /**
   * Get Type MAP or LIST
   */
  public int getType() {
    return type;
  }

  /**
   * Fire object added Event
   */
  protected void fireManagedObjectAdded(Object pObject, String id) {
    Iterator _listeners = listeners.iterator();
    while(_listeners.hasNext()) {
      ((BLXCollectionListener)_listeners.next()).managedObjectAdded(
      new BLXCollectionEvent(this, BLXCollectionEvent.COMPONENT_ADDED, pObject, id, -1));
    }
  }

  /**
   * Fire object added Event
   */
  protected void fireManagedObjectAdded(Object pObject, int index) {
    Iterator _listeners = listeners.iterator();
    while(_listeners.hasNext()) {
      ((BLXCollectionListener)_listeners.next()).managedObjectAdded(
      new BLXCollectionEvent(this, BLXCollectionEvent.COMPONENT_ADDED, pObject, null, index));
    }
  }

  /**
   * Fire object Removed Event
   */
  protected void fireManagedObjectRemoved(Object pObject) {
    Iterator _listeners = listeners.iterator();
    while(_listeners.hasNext()) {
      ((BLXCollectionListener)_listeners.next()).managedObjectRemoved(
      new BLXCollectionEvent(this, BLXCollectionEvent.COMPONENT_REMOVED, pObject, null, -1));
    }
  }

  /**
   * Get an XML Document for a Single BLXObject object
   * @param pObject to get Document For
   * @return Document
   */
  public Document getObjectDocument(BLXObject pObject) {

    Document _document = BLXUtility.createDOMDocument();
    Element _root = _document.createElementNS("blx", BLXObject.BLX_NS+":"+BLXObject.BLX_DOCUMENT_NAME);
    _root.setAttribute("xmlns:"+BLXObject.BLX_NS,BLXObject.BLX_NS);
    Element _dcf = (Element)_document.importNode(pObject.getBLXElement().getDOMElement(), true);
    Element _data = pObject.getDataElement();
    if(_data != null) _dcf.appendChild(_document.importNode(_data, true));

    _root.appendChild(_dcf);
    _document.appendChild(_root);

    return _document;
  }

  /**
   * Get the Index of an Object
   */
  public int getManagedObjectIndex(BLXObject pObject) {

    if(type == LIST) {
      return objectList.indexOf(pObject);
    }
    else {
      int i=0;
      Iterator _it = (type == MAP)?objectMap.values().iterator():objectSet.iterator();
      while(_it.hasNext()) {
        if(pObject == _it.next()) break;
        i++;
      }

      return i;
    }
  }

  /**
   * return true if this container contains the Object
   */
  public boolean contains(BLXObject pObject) {
    if(type == MAP) return objectMap.values().contains(pObject);
    else if(type == LIST) return objectList.contains(pObject);
    else if(type == SET) return objectSet.contains(pObject);
    else return false;
  }

  /**
   * The number of elements this Container is managing
   */
  public int size() {
    if(type == MAP) return objectMap.size();
    else if(type == LIST) return objectList.size();
    else if(type == SET) return objectSet.size();
    else return -1;
  }

  /**
   * Get an XML Container Element that contains all managed Objects
   * @param HREFPrefix the href that the <id>.blx will be appended to as an Href for the
   * Data location for each Component Node. Or null if the Objects data should be returned
   * in the XML Element
   * @return XML Element that represents the contents of this Collection
   */
  public Element getXMLElement(String HREFPrefix) {

    Document doc = BLXUtility.createDOMDocument();
    Element containerE = doc.createElement(CONTAINER_NODE_NAME);
    if(type == MAP) containerE.setAttribute(CONTAINER_TYPE_ATTR, MAP_STR);
    else containerE.setAttribute(CONTAINER_TYPE_ATTR, LIST_STR);

    //Walk Through Each Managed Component and Get Nodes
    Iterator it = null;
    if(type == MAP) it = objectMap.keySet().iterator();
    else if(type == LIST) it = objectList.iterator();
    else if(type == SET) it = objectSet.iterator();

    int index = -1;
    while(it.hasNext()) {

      String key = null;
      BLXObject blxObj = null;

      //Map
      if(type == MAP) {
        key = (String)it.next();
        blxObj = getManagedObject(key);
      }
      //List or Set
      else{
        try {
          Object obj = it.next();
          blxObj = (BLXObject)obj;
        }
        catch(ClassCastException exp) {
          //Go to Next Item
          continue;
        }
      }

      //Create an Item Element
      Element itemE = doc.createElement(ITEM_NODE_NAME);
      if(type == MAP) itemE.setAttribute(ITEM_ID_ATTR, key);
      
      Element blxNode = (Element)doc.importNode(blxObj.getBLXElement().getDOMElement(), true);
      itemE.appendChild(blxNode);

      //Set the href for the Data if specified and the BLXObject has an ID
      //Otherwise store the data in this XML
      if(HREFPrefix != null && blxObj.getBLXId()!=null) {
        blxNode.setAttribute(BLXObject.BLX_HREF_ATTR, blxObj.getBLXId() + BLXObject.FILE_EXT);
      }
      else {
        //append the blx Node's xml Data to the BLX Node
        Element data = blxObj.getDataElement();
        if(data != null) blxNode.appendChild(doc.importNode(blxObj.getDataElement(), true));
      }
      
      //append the blx Node to the Container Element
      containerE.appendChild(doc.importNode(itemE, true));
    }

    return containerE;
  }

  /**
   * Set an XML Container Element that contains all managed Objects
   * @param containerE the element for this container
   * @param relativeURL the href that the id.blx will be appended to as an Href for the
   * Data location for each Component Node
   * @return true if successful or false if there where Errors
   */
  public boolean setXMLElement(Element containerE, URL relativeURL) {
    
     
    //Must be a Container Node
    String nodeName = BLXUtility.getLocalNodeName(containerE);
    if(containerE == null || nodeName.equals(CONTAINER_NODE_NAME) == false) return false;

    //Setup Container type
    String _type = containerE.getAttribute(CONTAINER_TYPE_ATTR);
    if(_type != null && _type.equals(LIST_STR)) {
      this.type = LIST;
      this.objectMap = null;
      this.objectSet = null;
      this.objectList = new ArrayList();
    }
    else if(_type != null && _type.equals(MAP_STR)) {
      this.type = MAP;
      this.objectMap = new HashMap();
      this.objectList = null;
      this.objectSet = null;
    }
    else if(_type != null && _type.equals(SET_STR)) {
      this.type = SET;
      this.objectMap = null;
      this.objectList = null;
      this.objectSet = new HashSet();
    }

    //Walk through All Children and Load Them up
    boolean hadErrors = false;
    NodeList list = containerE.getChildNodes();
    for(int i=0;i<list.getLength();i++) {
      try {
        Node _node = list.item(i);
        //Only Process Elements
        if(_node.getNodeType() != Node.ELEMENT_NODE) continue;

        //Only Process Items
        if(_node.getNodeName().equals(ITEM_NODE_NAME) == false) continue;

        Element _itemE = (Element)_node;
        String _id = null;
        int _index = -1;

        //Get id or index
        if(type == MAP) {
          _id = _itemE.getAttribute(ITEM_ID_ATTR);
        }
        //else if(type == LIST){
        //  _index = Integer.parseInt(_itemE.getAttribute(ITEM_INDEX_ATTR));
        //}

        //Get the BLX Component
        NodeList _itemList = _itemE.getChildNodes();
        _node = null;
        for(int x=0;x<_itemList.getLength();x++) {
          _node = _itemList.item(x);
          if(_node.getNodeType() != Node.ELEMENT_NODE) {
             _node = null;
             continue;
          }
          if(!_node.getNodeName().startsWith(BLXObject.BLX_NS+":")) 
             _node = null;
          else 
             break;
        }

        //Only Process BLX Components get next ITEM Node
        if(_node == null) continue;
        
        //Get Object Wrapper
        BLXElement _blxElement = new BLXElement((Element)_node, relativeURL);

        //Ok Now we create the Object and Add it to the Container
        Object _obj = BLXCompFactory.getInstance().getObject(_blxElement, null);
        if((_obj instanceof BLXObject) == false) continue;
        if(type == MAP || type == SET) addManagedObject(_id, (BLXObject)_obj);
        else addManagedObject((BLXObject)_obj);
      }
      catch(ClassCastException exp) {
        logger.log(Level.SEVERE, "Error trying to load Component into Collection", exp);
        hadErrors = true;
      }
      catch(InvalidBLXXMLException _exp) {
        //Just skip it. We only work with BLX Objects
        _exp.printStackTrace();
        hadErrors = true;
        continue;
      }
      catch(Exception _exp) {
        //This is probably bad because it was a valid BLX Component
        _exp.printStackTrace();
        hadErrors = true;
      }
    }

    //If we had errors loading the Elements then set the Flag
    if(hadErrors) return false;
    else return true;
  }
}

