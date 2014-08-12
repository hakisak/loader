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

package org.xito.launcher.sys;

import java.awt.*;
import java.io.*;
import java.text.*;
import java.awt.event.*;
import javax.swing.*;
import org.xito.dialog.*;
import org.xito.launcher.*;

/**
 *
 * @author Deane Richan
 */
public class LocalAppAction extends LauncherAction {
   
   /** Creates a new instance of AppletAction */
   public LocalAppAction(LocalAppActionFactory factory) {
      this(factory, new LocalAppDesc());
   }
   
   /** Creates a new instance of AppletAction */
   public LocalAppAction(LocalAppActionFactory factory, LocalAppDesc appDesc) {
      super(factory);
      setLaunchDesc(appDesc);
      super.putValue(super.SMALL_ICON, LocalAppActionBeanInfo.icon16);
   }
   
   public String getElementName() {
      return factory.getElementName();
   }
   
   public void actionPerformed(ActionEvent evt) {
      
      LocalAppDesc appDesc = (LocalAppDesc)getLaunchDesc();
      
      try {
         LocalAppLauncher launcher = new LocalAppLauncher();
         launcher.launch(appDesc);
      }
      catch(IOException exp) {
         String msg = MessageFormat.format(Resources.sysBundle.getString("launch.error.message"), appDesc.getExecutableCmd(), exp.getMessage()); 
         DialogManager.showError(null, Resources.sysBundle.getString("launch.error.title"), msg, exp);
      }
   }
   
   /**
    * Edit this Action
    */
   public boolean edit(Frame parentFrame) {
      
      LocalAppDesc appDesc = (LocalAppDesc)getLaunchDesc();
      LocalAppConfigDialog dialog = new LocalAppConfigDialog(parentFrame, appDesc);
      dialog.setVisible(true);
      
      if(dialog.getResult() == DialogManager.OK) {
         dirty_flag = true;
         return true;
      }
      else {
         return false;
      }
   }
}
