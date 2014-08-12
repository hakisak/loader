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

package org.xito.controlpanel;

import javax.swing.*;

/**
 *
 * @author Deane Richan
 */
public abstract class ControlPanelAction extends AbstractAction {
   
   public static String LARGE_ICON = "large.icon";
   
   public ControlPanelAction() {
      super.putValue(Action.SMALL_ICON, new ImageIcon(ControlPanelAction.class.getResource("org.xito.launcher.images/misc16.png")));
      super.putValue(ControlPanelAction.LARGE_ICON, new ImageIcon(ControlPanelAction.class.getResource("org.xito.launcher.images/misc32.png")));
   }
   
   public String toString() {
      return (String)getValue(Action.NAME);
   }
}
