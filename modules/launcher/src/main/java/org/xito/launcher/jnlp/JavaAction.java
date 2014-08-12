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

package org.xito.launcher.jnlp;

import java.security.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;

import org.xito.boot.Boot;
import org.xito.boot.NativeLibDesc;
import org.xito.boot.AppLauncher;
import org.xito.boot.LaunchException;

import org.xito.dialog.*;
import org.xito.launcher.*;


/**
 *
 * @author Deane Richan
 */
public class JavaAction extends LauncherAction {

   private static final Logger logger = Logger.getLogger(JavaAction.class.getName());
   
   /** Creates a new instance of AppletAction */
   public JavaAction(JavaActionFactory factory) {
      this(factory, new JavaAppDesc());
   }
   
   /** Creates a new instance of AppletAction */
   public JavaAction(JavaActionFactory factory, JavaAppDesc desc) {
      super(factory);
      setLaunchDesc(desc);
      super.putValue(super.SMALL_ICON, JavaActionBeanInfo.icon16);
   }
   
   public String getElementName() {
      return factory.getElementName();
   }
   
   public void actionPerformed(ActionEvent evt) {
      
      JavaAppDesc appDesc = (JavaAppDesc)getLaunchDesc();
      JavaLauncher launcher = new JavaLauncher();
      try {
         launcher.launchInternal(appDesc, true);
      }
      catch(LaunchException exp) {
          String msg = MessageFormat.format(Resources.javaBundle.getString("launch.error.message"), exp.getMessage()); 
          DialogManager.showError(null, Resources.javaBundle.getString("launch.error.title"), msg, exp);
      }
      
   }

   /**
    * Edit this Action
    */
   public boolean edit(Frame parentFrame) {
      JavaAppDesc appDesc = (JavaAppDesc)getLaunchDesc();
      JavaConfigDialog dialog = new JavaConfigDialog(parentFrame, appDesc);
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
