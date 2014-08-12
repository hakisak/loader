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
public class ControlPanelService {
   
   private static ControlPanelModel model = new ControlPanelModel();
   private static ControlPanelFrame controlFrame;
   
   /** Creates a new instance of ControlPanelService */
   public ControlPanelService() {
   }
   
   public static void main(String args[]) {
      
      addControlAction(new ProxyAction());
      //addControlAction(new CacheAction());
      //addControlAction(new SecurityAction());
      //addControlAction(new ServicesAction());
      
      controlFrame = new ControlPanelFrame();
   }
   
   public static void addControlAction(Action a) {
      model.addItem(a);
   }
   
   public static ControlPanelModel getModel() {
      return model;
   }
   
   public static ControlPanelFrame getFrame() {
      return controlFrame;
   }
   
}
