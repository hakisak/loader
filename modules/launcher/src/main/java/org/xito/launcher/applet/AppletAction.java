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

package org.xito.launcher.applet;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.logging.*;
import org.xito.dialog.*;
import org.xito.launcher.*;
import org.xito.boot.LaunchException;

/**
 *
 * @author Deane Richan
 */
public class AppletAction extends LauncherAction {
   
   private static final Logger logger = Logger.getLogger(AppletAction.class.getName());
      
   /** Creates a new instance of AppletAction */
   public AppletAction(AppletActionFactory factory) {
      this(factory, new AppletDesc());
   }
   
   /** Creates a new instance of AppletAction */
   public AppletAction(AppletActionFactory factory, AppletDesc desc) {
      super(factory);
      setLaunchDesc(desc);
      super.putValue(LauncherAction.SMALL_ICON, AppletActionBeanInfo.icon16);
   }
   
   public void actionPerformed(ActionEvent evt) {
      AppletDesc appletDesc = (AppletDesc)getLaunchDesc();
      AppletLauncher launcher = new AppletLauncher();
      try {
         launcher.launchInternal(appletDesc);
      }
      catch(LaunchException exp) {
          String msg = MessageFormat.format(Resources.appletBundle.getString("launch.error.message"), exp.getMessage()); 
          DialogManager.showError(null, Resources.appletBundle.getString("launch.error.title"), msg, exp);
      }
   }

   /**
    * Edit this Action
    */
   public boolean edit(Frame parentFrame) {
      
      AppletDesc appletDesc = (AppletDesc)getLaunchDesc();
      AppletConfigDialog dialog = new AppletConfigDialog(parentFrame, appletDesc);
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
