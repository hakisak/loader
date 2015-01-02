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
import javax.swing.*;
import javax.swing.border.*;
import org.xito.boot.Boot;
import org.xito.dialog.*;
import org.xito.launcher.*;

/**
 *
 * @author Deane Richan
 */
public class WebConfigDialog extends CustomDialog {
   
   private WebDesc webDesc;
   private MainPanel mainPanel;
   
   /** Creates a new instance of LocalAppConfigDialog */
   public WebConfigDialog(Frame owner, WebDesc webDesc) {
      super(owner);
      if(webDesc == null) 
         this.webDesc = new WebDesc();
      else
         this.webDesc = webDesc;
      
      descriptor = createDialogDesc();
      super.setModal(true);
      super.setTitle(Resources.webBundle.getString("config.title"));
      super.init();
   }
   
   /**
    * Create the Dialog Desc for this Dialog
    */
   private DialogDescriptor createDialogDesc() {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setType(DialogManager.OK_CANCEL);
      //desc.setGradiantColor(org.xito.boot.ui.Defaults.DIALOG_GRAD_COLOR);
      //desc.setGradiantOffsetRatio(org.xito.boot.ui.Defaults.DIALOG_GRAD_OFFSET);
      desc.setTitle(Resources.webBundle.getString("config.title"));
      desc.setSubtitle(Resources.webBundle.getString("config.subtitle"));
      desc.setIcon(new ImageIcon(this.getClass().getResource("/org/xito/launcher/images/web_32.png")));
      desc.setShowButtonSeparator(true);
      desc.setResizable(true);
      desc.setWidth(Resources.getIntForOS(Resources.webBundle, "config.org.xito.width", Boot.getCurrentOS(), 300));
      desc.setHeight(Resources.getIntForOS(Resources.webBundle, "config.org.xito.height", Boot.getCurrentOS(), 300));
      
      
      mainPanel = new MainPanel();
      desc.setCustomPanel(mainPanel);
      
      return desc;
   }
   
   /**
    * Get this Dialogs WebDesc
    */
   public WebDesc getWebDesc() {
      return this.webDesc;
   }
   
   /**
    * Show the org.xito and update the contents of LocalAppDesc if OK was pressed
    */
   public void show(boolean b) {
      super.show(b);

      int result = super.getResult();
      if(result == DialogManager.OK) {
         mainPanel.updateWebDesc();
      }
      
      return;
   }
   
   /**************************************************
    * The Main Panel of the Dialog
    **************************************************/
   private class MainPanel extends JPanel {
      
      private JLabel errorLbl;
      private JTextField addressTF;
      private JTextField nameTF;
      private JTextField titleTF;
      private JCheckBox useNewBrowserCB;
     
      public MainPanel() {
         init();
      }
      
      private void updateWebDesc() {
         
         webDesc.setAddress(addressTF.getText());
         webDesc.setName(nameTF.getText());
         webDesc.setTitle(titleTF.getText());
         webDesc.setUseNewBrowser(useNewBrowserCB.isSelected());
      }
      
      /**
       * Build the Panel
       */
      private void init() {
         
         setLayout(new TableLayout(WebConfigDialog.class.getResource("web_layout.html")));
                  
         //Description
         JLabel lbl = new JLabel(Resources.webBundle.getString("config.desc.lbl"));
         add("description", lbl);
         
         //Error Label
         errorLbl = new JLabel();
         errorLbl.setOpaque(true);
         errorLbl.setBorder(new LineBorder(SystemColor.controlShadow));
         errorLbl.setBackground(SystemColor.textHighlight);
         errorLbl.setForeground(SystemColor.textHighlightText);
         errorLbl.setVisible(false);
         add("error_lbl", errorLbl);
         
         //Address
         lbl = new JLabel(Resources.webBundle.getString("config.address.lbl"));
         add("address_lbl", lbl);
         addressTF = new JTextField();
         add("address", addressTF);
         
         //Name
         lbl = new JLabel(Resources.webBundle.getString("config.name.lbl"));
         add("name_lbl", lbl);
         nameTF = new JTextField();
         nameTF.setDocument(new ValidatableDocument(BaseLaunchDesc.MAX_NAME_LENGTH));
         add("name", nameTF);
         
         //Title
         lbl = new JLabel(Resources.webBundle.getString("config.title.lbl"));
         add("title_lbl", lbl);
         titleTF = new JTextField();
         titleTF.setDocument(new ValidatableDocument(BaseLaunchDesc.MAX_DISPLAYNAME_LENGTH));
         add("title", titleTF);
         
         //Use new Browser
         useNewBrowserCB = new JCheckBox(Resources.webBundle.getString("config.use_new_browser.lbl"));
         add("new_browser", useNewBrowserCB);
         
         //Read Settings from Desc and populate Panel
         readDesc();
      }
      
      /**
       * Read the Settings from the Desc and populate the Panels
       */
      private void readDesc() {
         addressTF.setText(webDesc.getAddress());
         nameTF.setText(webDesc.getName());
         titleTF.setText(webDesc.getTitle());
         useNewBrowserCB.setSelected(webDesc.useNewBrowser());
      }
   }
}
