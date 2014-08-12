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

package org.xito.launcher.web;

import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import org.w3c.dom.*;
import org.w3c.tidy.*;
import org.xito.boot.*;
import org.xito.dialog.*;
import org.xito.launcher.*;


/**
 *
 * @author Deane Richan
 */
public class WebAction extends LauncherAction {
   
   public static final String ELEMENT_NAME = "web-action";
   
   /** Creates a new instance of AppletAction */
   public WebAction(WebActionFactory factory) {
      this(factory, new WebDesc());
   }
   
   /** Creates a new instance of WebAction */
   public WebAction(WebActionFactory factory, WebDesc webDesc) {
      super(factory);
      
      setLaunchDesc(webDesc);
      updateIconsFromWebDesc();
   }
   
   public void updateIconsFromWebDesc() {
      WebDesc webDesc = (WebDesc)getLaunchDesc();
      
      if(webDesc.getAppleTouchIconURL() != null) {
         putValue(super.SMALL_ICON, new ImageIcon(webDesc.getAppleTouchIconURL()));
      }
      else if(webDesc.getFavIconURL() != null) {
         putValue(super.SMALL_ICON, new ImageIcon(webDesc.getFavIconURL()));
      }
      else {
         putValue(super.SMALL_ICON, WebActionBeanInfo.icon16);
      }
   }
      
   /**
    * Get a DOM Element name that this Action uses for persistence
    */
   public String getElementName() {
      return ELEMENT_NAME;
   }
   
   public void actionPerformed(ActionEvent evt) {
      
      WebDesc webDesc = (WebDesc)getLaunchDesc();
      String webAddress = webDesc.getAddress();
      if(webAddress == null) {
         Boot.showError("Web Address Error", "Web Address not specified.", null);
         return;
      }
      
      WebLauncher launcher = new WebLauncher();
      try {
         if(webDesc.useNewBrowser()) {
            launcher.launch(new URL(webAddress));
         }
         else {
            launcher.launch(new URL(webAddress));
         }
      }
      
      catch(IOException ioExp) {
         //perhaps they are missing the protocol so default to http://
         try {
            launcher.launch(new URL("http://"+webAddress));
         }
         catch(IOException ioExp2) {
            //Ok now show an Error
            Boot.showError("Web Address Error", "Cannot open Browser invalid address:"+webAddress, null);
         }
      }
   }
   
   /**
    * Edit this Action
    */
   public boolean edit(Frame parentFrame) {
      
      WebConfigDialog dialog = new WebConfigDialog(parentFrame, ((WebDesc)getLaunchDesc()));
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
