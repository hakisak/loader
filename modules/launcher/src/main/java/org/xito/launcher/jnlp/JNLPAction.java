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

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.logging.*;
import javax.swing.*;

import org.xito.dialog.*;
import org.xito.launcher.*;

/**
 *
 * @author Deane Richan
 */
public class JNLPAction extends LauncherAction {

   private static final Logger logger = Logger.getLogger(JNLPAction.class.getName());
         
   /** Creates a new instance of AppletAction */
   public JNLPAction(JNLPActionFactory factory) {
      this(factory, new JNLPAppDesc());
   }
   
   /** Creates a new instance of AppletAction */
   public JNLPAction(JNLPActionFactory factory, JNLPAppDesc desc) {
      super(factory);
      setLaunchDesc(desc);
      super.putValue(Action.SMALL_ICON, JNLPActionBeanInfo.icon16);
   }
   
   /**
    * Get the Element Name this Action uses for XML Persitence
    */
   @Override
   public String getElementName() {
      return factory.getElementName();
   }
   
   public void actionPerformed(ActionEvent evt) {
      
      JNLPAppDesc jnlpDesc = (JNLPAppDesc)getLaunchDesc();
      JNLPLauncher launcher = new JNLPLauncher();
      try {
         launcher.launchBackground(jnlpDesc);
      }
      catch(Throwable exp) {
          String msg = MessageFormat.format(Resources.jnlpBundle.getString("launch.error.message"), exp.getMessage()); 
          DialogManager.showError(null, Resources.jnlpBundle.getString("launch.error.title"), msg, exp);
      }
   }

   /**
    * Edit this Action
    */
   @Override
   public boolean edit(Frame parentFrame) {
      JNLPAppDesc jnlpDesc = (JNLPAppDesc)getLaunchDesc();
      JNLPConfigDialog dialog = new JNLPConfigDialog(parentFrame, jnlpDesc);
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
