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

/**
 *
 *
 */
public interface DCMenuModelListener extends java.util.EventListener {
  
  /**
   * The Menu Model has been changed. 
   * @param event describing the change that was made to the Menu Model
   */
  public void menuChanged(DCMenuModelEvent event);
  
}


