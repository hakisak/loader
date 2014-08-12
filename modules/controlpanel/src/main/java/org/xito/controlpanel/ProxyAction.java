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

import java.awt.event.*;
import javax.swing.*;

import org.xito.boot.*;

/**
 *
 * @author Deane Richan
 */
public class ProxyAction extends ControlPanelAction {
   
   /** Creates a new instance of ProxyAction */
   public ProxyAction() {
      
      super.putValue(Action.NAME, "Proxy Settings");
      super.putValue(Action.SHORT_DESCRIPTION, "Configure Proxy Settings");
      super.putValue(Action.SMALL_ICON, new ImageIcon(ProxyConfig.class.getResource("ui/org.xito.launcher.images/proxy16.gif")));
      super.putValue(ControlPanelAction.LARGE_ICON, new ImageIcon(ProxyConfig.class.getResource("ui/org.xito.launcher.images/proxy32.gif")));
   }
   
   /**
    * Show the Proxy Config Dialog
    */
   public void actionPerformed(ActionEvent evt) {
      ProxyConfig.getConfig().showProxyDialog();
   }

}
