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

package org.xito.boot.ui;

import java.awt.Frame;
import java.text.*;
import javax.swing.*;
import org.xito.boot.*;
import org.xito.dialog.*;

/**
 * Simple Permission Prompt Dialog used to prompt a user to grant a single specific permission
 */
public class SimplePermPromptDialog extends CustomDialog {

      private JLabel msgLabel;
      private JComboBox permCombo;
      
      public  SimplePermPromptDialog() {
         super((Frame)null);
         super.setModal(true);
         
         String title = Resources.bundle.getString("boot.security.warning.title");
         title = MessageFormat.format(title, Boot.getAppDisplayName());
         super.setTitle(title);
         
         super.descriptor = new DialogDescriptor();
         descriptor.setWindowTitle(title);
         descriptor.setTitle(title);
         descriptor.setSubtitle("<subtitle goes here>");
         descriptor.setMessage("<message goes here>");
         descriptor.setShowButtonSeparator(true);
         descriptor.setMessageType(DialogManager.WARNING_MSG);
         descriptor.setType(DialogManager.YES_NO);
         descriptor.setWidth(350);
         descriptor.setHeight(250); 
         
         JPanel mainPanel = new JPanel(new TableLayout(BootSecurityManager.class.getResource("ui/perm_prompt_layout.html")));
         msgLabel = new JLabel(descriptor.getMessage());
         mainPanel.add("msg", msgLabel);
         
         String justonce = Resources.bundle.getString("boot.security.justonce");
         String always = Resources.bundle.getString("boot.security.always");
                  
         permCombo = new JComboBox(new String[]{justonce, always});
         mainPanel.add("perms", permCombo);
         
         descriptor.setCustomPanel(mainPanel);
         super.init();
      }
      
      public void setMessageText(String msg) {
         msgLabel.setText(msg);
      }
      
      public int getSelectedPermOption() {
         int i = permCombo.getSelectedIndex();
         if(i == 0) {
            return BootPolicy.ONE_TIME_APP;
         }
         else {
            return BootPolicy.ALWAYS_APP;
         }
      }
      
      public void reset() {
         setTitles(null, "");
         msgLabel.setText("");
         permCombo.setSelectedIndex(0);
      }
   }