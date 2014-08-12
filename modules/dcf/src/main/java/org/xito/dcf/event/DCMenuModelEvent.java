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

package org.xito.dcf.event;

import java.util.*;
import javax.swing.*;import org.xito.dcf.*;

/**
 * 
 *
 */
public class DCMenuModelEvent extends java.util.EventObject {
  
  public static final int ADD_EVENT = 0;
  public static final int REMOVE_EVENT = 1;
  
  protected Object item;
  protected int index;
  protected int type;
  
  /**
   * Create a DCMenuModelEvent for adding or removing an Action 
   *
   * @param type either ADD_EVENT or REMOVE_EVENT
   * @param index location in the item list that where the action was added or removed
   * @param action that was added or removed from the model
   * @param source the model that was modified
   */
  public DCMenuModelEvent(int type, int index, Action action, DCMenuModel source) {
    super(source);
    this.type = type;
    this.item = action;
    this.index = index;
  }
  
  /**
   * Create a DCMenuModelEvent for adding or removing a SubMenu
   *
   * @param type either ADD_EVENT or REMOVE_EVENT
   * @param index location in the item list that where the action was added or removed
   * @param subMenu that was added or removed from the model
   * @param source the model that was modified
   */
  public DCMenuModelEvent(int type, int index, DCMenuModel subMenu, DCMenuModel source) {
    super(source);
    this.type = type;
    this.item = subMenu;
    this.index = index;
  }
  
  /**
   * Create a DCMenuModelEvent for adding or removing a SEPERATOR
   *
   * @param type either ADD_EVENT or REMOVE_EVENT
   * @param index location in the item list that where the action was added or removed
   * @param seperator should = DCMenuModel.SEPERATOR
   * @param source the model that was modified
   */
  public DCMenuModelEvent(int type, int index, String seperator, DCMenuModel source) {
    super(source);
    this.type = type;
    this.item = DCMenuModel.SEPERATOR;
    this.index = index;
  }
  
  /**
   * Get the index location where the item was added or removed
   * @return index
   */
  public int getIndex() {
    return index;
  }
  
  /**
   * Get the Item that was added or removed.
   * @return Action, DCMenuModel or DCMenuModel.SEPERATOR depending on what was added or removed
   */
  public Object getItem() {
    return item;
  }
  
  /**
   * Get the type of this event either ADD_EVENT or REMOVE_EVENT
   * @return type
   */
  public int getType() {
    return type;
  }
}


