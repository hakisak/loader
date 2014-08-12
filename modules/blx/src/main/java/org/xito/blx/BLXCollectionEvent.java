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

import java.util.*;

/**
 * The BLXCollectionEvent is fired when events occur on a BLXCollection. See BLXCollectionListener 
 * for more information.
 * 
 * @author  drichan
 * @version
 */
public class BLXCollectionEvent extends EventObject {

  /** Component Added Event Type */
  public static final int COMPONENT_ADDED = 1;
  /** Component Removed Event Type */
  public static final int COMPONENT_REMOVED = 2;

  private int index = -1;
  private int eventID = 1;
  private String objectID;
  private Object object;

  /**
   * @param source Container that fired the event
   * @param pID of the Event (COMPONENT_ADDED, COMPONENT_REMOVED)
   * @param pObject managed Object that was added or removed
   */
  public BLXCollectionEvent(BLXCollection source, int pEventID, Object pObject, String pObjectID, int pIndex) {

    super(source);
    object = pObject;

    if(pEventID == COMPONENT_ADDED || pEventID == COMPONENT_REMOVED) {
      eventID = pEventID;
      objectID = pObjectID;
      index = pIndex;
    }
  }

  /**
   * Get the ID of this Event
   * @return COMPONENT_ADDED, COMPONENT_REMOVED
   */
  public int getEventID() {
    return eventID;
  }

  /**
   * Get the Object ID of the object that was added or removed
   * @return COMPONENT_ADDED, COMPONENT_REMOVED
   */
  public String getObjectID() {
    return objectID;
  }

  /**
   * Get the Object index of the object that was added or removed
   * @return COMPONENT_ADDED, COMPONENT_REMOVED
   */
  public int getObjectIndex() {
    return index;
  }

  /**
   * Get the Container that fired this Event
   */
  public BLXCollection getContainer() {
    return (BLXCollection)getSource();
  }

  /**
   * Get the Managed Object that was removed or Added
   */
  public Object getManagedObject() {
    return object;
  }
}
