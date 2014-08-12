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

package org.xito.launcher.jnlp.service;

import java.awt.*;
import java.awt.datatransfer.*;
import java.text.*;
import java.security.*;
import java.util.*;
import javax.jnlp.*;
import org.xito.boot.*;
import org.xito.dialog.*;
import org.xito.launcher.Resources;

/**
 *
 * @author Deane Richan
 */
public class ClipboardServiceImpl extends AbstractServiceImpl implements ClipboardService{
   
   /** Creates a new instance of ClipboardServiceImpl */
   public ClipboardServiceImpl(AppInstance appInstance) {
      super(appInstance);
   }

   /**
    * Set the Contents of the Clipboard
    */
   public void setContents(final Transferable transferable) {
      
      final AWTPermission perm = new AWTPermission("accessClipboard");
      boolean havePerm = checkPermission(perm);
      if(!havePerm) {
         String subtitle = Resources.jnlpBundle.getString("clipboard.access.subtitle");
         String msg = Resources.jnlpBundle.getString("clipboard.access.msg");
         msg = MessageFormat.format(msg, appInstance.getAppDesc().getDisplayName());
         havePerm = promptForPermission(subtitle, msg, perm);
      }
                  
      if(havePerm) {
         //Set the Contents in a PrivilegedAction
         AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
               return null;
            }
         });
      }
   }

   /**
    * Get the Contents of the Clipboard
    */
   public Transferable getContents() {
      
      final AWTPermission perm = new AWTPermission("accessClipboard");
      boolean havePerm = checkPermission(perm);
      if(!havePerm) {
         String subtitle = Resources.jnlpBundle.getString("clipboard.access.subtitle");
         String msg = Resources.jnlpBundle.getString("clipboard.access.msg");
         msg = MessageFormat.format(msg, appInstance.getAppDesc().getDisplayName());
         havePerm = promptForPermission(subtitle, msg, perm);
      }
      
      Transferable transferable=null;
      if(havePerm) {
         //Get the Contents in a PrivilegedAction
         transferable = (Transferable)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               return Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            }
         });
      }
      
      return transferable;
   }
   
}
