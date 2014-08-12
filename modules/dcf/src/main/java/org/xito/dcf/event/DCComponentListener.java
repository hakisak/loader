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

import java.awt.event.*;
import java.util.*;

import org.xito.dcf.*;

/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.3 $
 * @since $Date: 2007/11/28 03:52:39 $
 */
public interface DCComponentListener extends ComponentListener {
  
  /**
   * This event is fired when a Component is disposed
   */
  public void componentDisposed(DCComponentEvent event);
  
  /**
   * This event is fired when a DCComponent is placed on the Desktop.
   */
  public void componentPlacedOnDesktop(DCComponentEvent event);
  
  /**
   * This event is fired when a DCComponent is placed on the Desktop.
   */
  public void componentRemovedFromDesktop(DCComponentEvent event);
  
}
  

