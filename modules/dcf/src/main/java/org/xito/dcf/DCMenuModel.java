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

package org.xito.dcf;

import java.util.*;
import javax.swing.*;
import org.xito.dcf.event.*;

/**
 * This provides a basic model for the DCMenu. This model is not BLX Aware so its state is not
 * automatically persisted. Sub-classes should be written to persist the contents of this Model
 * 
 * @author drichan
 */
public class DCMenuModel {

  public static final String SEPERATOR = "seperator";
  
  protected ArrayList items = new ArrayList();
  protected HashSet listeners = new HashSet();
  protected String name;
  protected String id;
  protected Icon icon;
  
  /**
   * Creates a  Menu Model with an auto generated id
   */
  public DCMenuModel() {
    this((String)null, (String)null);
  }
  
  /**
   * Creates a  Menu Model with the given Name and
   * auto generated id
   * @param name of this Menu.
   */
  public DCMenuModel(String name) {
    this(name, (String)null);
  }
  
  /**
   * Creates a  Menu Model with the given Name and id
   * @param name of this Menu.
   */
  public DCMenuModel(String name, String id) {
    setName(name);
    if(id != null)
      this.id = id;
    else
      this.id = DCComponent.generateID(this);
  }
  
  /**
   * Creates a  Menu Model with the given Name, Icon,
   * and auto generated id
   * @param name of this Menu.
   * @param icon of this Menu.
   */
  public DCMenuModel(String name, Icon icon) {
    this(name);
    setIcon(icon);
  }
  
  /**
   * Set the name of this Menu
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * Get the name of this Menu
   */
  public String getName() {
    return this.name;
  }
  
  /**
   * Get the id of this Menu
   */
  public String getId() {
    return id;
  }
  
  /**
   * Set the Icon for this Menu
   */
  public void setIcon(Icon icon) {
    this.icon = icon;
  }
  
  /**
   * Get the Icon for this Menu
   */
  public Icon getIcon() {
    return icon;
  }
  
  /**
   * Add a DCMenuModelListener to this Model
   */
  public void addMenuModelListener(DCMenuModelListener listener) {
    listeners.add(listener);
  }
  
  /**
   * Remove a DCMenuModelListener to this Model
   */
  public void removeMenuModelListener(DCMenuModelListener listener) {
    listeners.remove(listener);
  }
  
  /**
   * Fire a DCMenuModelEvent to all of this Models Listeners
   * @param event to fire to listeners
   */
  protected void fireMenuChanged(DCMenuModelEvent event) {
    
    synchronized(listeners) {
      Iterator it = listeners.iterator();
      while(it.hasNext()) {
        ((DCMenuModelListener)it.next()).menuChanged(event);
      }
    }
  }
  
  /**
   * Return the number of items this Model contains
   * @return 
   */
  public int size() {
    return items.size();
  }
  
  /**
   * Add an Action to this Menu Model
   * @param index where to add the action
   * @param action to add
   */
  public void add(int index, Action action) {
    synchronized(items) {
      items.add(index, action);
      DCMenuModelEvent event = new DCMenuModelEvent(DCMenuModelEvent.ADD_EVENT, index, action, this);
      fireMenuChanged(event);
    }
  }
    
  /**
   * Add an Action to the End of this Menu Model
   * @param action to add
   */
  public void add(DCMenuModel subMenuModel) {
    synchronized(items) {
      items.add(subMenuModel);
      DCMenuModelEvent event = new DCMenuModelEvent(DCMenuModelEvent.ADD_EVENT, items.size()-1, subMenuModel, this);
      fireMenuChanged(event);
    }
  }
    
  /**
   * Add a SubMenu to the End of this Menu Model
   * @param subMenuModel to add
   */
  public void add(int index, DCMenuModel subMenuModel) {
    synchronized(items) {
      items.add(subMenuModel);
      DCMenuModelEvent event = new DCMenuModelEvent(DCMenuModelEvent.ADD_EVENT, index, subMenuModel, this);
      fireMenuChanged(event);
    }
  }
  
  /**
   * Add a Seperator to this Menu Model
   * @param index where to add the seperator
   */
  public void addSeperator(int index) {
    synchronized(items) {
      items.add(index, DCMenuModel.SEPERATOR);
      DCMenuModelEvent event = new DCMenuModelEvent(DCMenuModelEvent.ADD_EVENT, index, DCMenuModel.SEPERATOR, this);
      fireMenuChanged(event);
    }
  }
  
  /**
   * Add a Seperator to the End of this Menu Model
   */
  public void addSeperator() {
    addSeperator(items.size()-1);
  }
  
  /**
   * Add an Action to the End of this Menu Model
   * @param action to add
   */
  public void add(Action action) {
    synchronized(items) {
      items.add(action);
      DCMenuModelEvent event = new DCMenuModelEvent(DCMenuModelEvent.ADD_EVENT, items.size()-1, action, this);
      fireMenuChanged(event);
    }
  }
        
  /**
   * Removes an item from this model
   * @param item to remove
   */
  public void remove(Object item) {
    synchronized(items) {
      int index = items.indexOf(item);
      items.remove(item);
      DCMenuModelEvent event = null;
      //Action
      if(item instanceof Action) {
        event = new DCMenuModelEvent(DCMenuModelEvent.ADD_EVENT, index, (Action)item, this);
      }
      //Menu Model
      else if(item instanceof DCMenuModel) {
        event = new DCMenuModelEvent(DCMenuModelEvent.ADD_EVENT, index, (DCMenuModel)item, this);
      }
      //Item is not correct type
      else {
        return;
      }
      fireMenuChanged(event);
    }
  }
  
  /**
   * Removes an item as a certain index in the model
   * @param index
   */
  public void remove(int index) {
    synchronized(items) {
      Object item = items.get(index);
      items.remove(index);
      DCMenuModelEvent event = null;
      //Seperatpr
      if(item.equals(DCMenuModel.SEPERATOR)) {
        event = new DCMenuModelEvent(DCMenuModelEvent.ADD_EVENT, index, DCMenuModel.SEPERATOR, this);
      }
      //Action
      else if(item instanceof Action) {
        event = new DCMenuModelEvent(DCMenuModelEvent.ADD_EVENT, index, (Action)item, this);
      }
      //Menu Model 
      else if(item instanceof DCMenuModel) {
        event = new DCMenuModelEvent(DCMenuModelEvent.ADD_EVENT, index, (DCMenuModel)item, this);
      }
      //Item is not correct type
      else {
        return;
      }
      fireMenuChanged(event);
    }
  }
  
  /**
   *
   */
  public Object get(int index) {
    return items.get(index);
  }
  
  /**
   *
   */
  public Iterator iterator() {
    return items.iterator();
  }
  
  /**
   * List contents
   */
  public void list() {
    for(int i=0;i<items.size();i++) {
      if(items.get(i) instanceof Action) {
        System.out.println("action:"+((Action)items.get(i)).getValue(Action.NAME));
      }
      else if(items.get(i) instanceof DCMenuModel) {
        System.out.println("menu:"+((DCMenuModel)items.get(i)).getName());
      }
      
    }
  }
}