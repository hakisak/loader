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
import org.xito.dcf.*;

/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version
 */
public class DCComponentEvent extends EventObject {
  
  public static final int COMPONENT_DISPOSED = 100;
  
  public static final int COMPONENT_PLACED_ON_DESKTOP = 101;
  
  public static final int COMPONENT_REMOVED_FROM_DESKTOP = 102;
  
  //Type of Event
  private int id;
  
  /** Creates new DCComponentEvent */
  public DCComponentEvent(DCComponent pSource, int id) {
    super(pSource);
    
    //Check for Correct ID
    if(id < 100 && id > 102) throw new IllegalArgumentException("Invalid ID");
    
    this.id = id;
  }
  
  /**
   * Get the DCComponent Source for this Event
   */
  public DCComponent getComponent() {
    return (DCComponent)super.getSource();
  }
  
  /**
   * Get the Type of Event
   */
  public int getID() {
    return id;
  }
  
}
