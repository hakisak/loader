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
import java.net.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import org.xito.dialog.*;
import org.xito.boot.Boot;
import org.xito.launcher.*;

/**
 *
 * @author Deane Richan
 */
public class JavaRemoteConfigDialog extends CustomDialog {
   
   private static final Logger logger = Logger.getLogger(JavaRemoteConfigDialog.class.getName());
   
   private JavaAppDesc appDesc;
   private MainPanel mainPanel;
   
   /** Creates a new instance of JavaRemoteConfigDialog */
   public JavaRemoteConfigDialog(Frame owner, JavaAppDesc appDesc) {
      super(owner);
      if(appDesc == null) 
         this.appDesc = new JavaAppDesc();
      else
         this.appDesc = appDesc;
            
      descriptor = createDialogDesc();
      super.setModal(true);
      super.setTitle(Resources.javaBundle.getString("remote.config.xito.title"));
      super.init();
   }
   
   /**
    * Create the Dialog Desc for this Dialog
    */
   private DialogDescriptor createDialogDesc() {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setType(DialogManager.OK_CANCEL);
      desc.setTitle(Resources.javaBundle.getString("remote.config.xito.title"));
      desc.setSubtitle(Resources.javaBundle.getString("remote.config.xito.subtitle"));
      desc.setIcon(new ImageIcon(this.getClass().getResource("/org/xito/launcher/images/java_32.png")));
      desc.setShowButtonSeparator(true);
      desc.setResizable(true);
      desc.setWidth(Resources.getIntForOS(Resources.javaBundle, "remote.config.xito.width", Boot.getCurrentOS(), 350));
      desc.setHeight(Resources.getIntForOS(Resources.javaBundle, "remote.config.xito.height", Boot.getCurrentOS(), 300));
            
      mainPanel = new MainPanel();
      desc.setCustomPanel(mainPanel);
      
      return desc;
   }
   
   /**
    * Get this Dialogs JavaAppDesc
    */
   public JavaAppDesc getAppDesc() {
      return this.appDesc;
   }
   
   /**
    * Show the org.xito and update the contents of LocalAppDesc if OK was pressed
    */
   public void show(boolean b) {
      super.show(b);

      int result = super.getResult();
      if(result == DialogManager.OK) {
         mainPanel.updateAppDesc();
      }
      
      return;
   }
   
   /**
    * The Main Panel of the Dialog
    */
   private class MainPanel extends JPanel {
      
      private JLabel errorLbl;
      private JTextField addressTF;
      private JTextField nameTF;
      private JTextField titleTF;
            
      private JCheckBox seperateVMCB;
            
      public MainPanel() {
         init();
      }
      
      private void updateAppDesc() {
         
         appDesc.setName(nameTF.getText());
         appDesc.setTitle(titleTF.getText());
         appDesc.setSeperateVM(seperateVMCB.isSelected());
         
         String address = addressTF.getText();
         if(address != null && !address.equals("")) {
            try {
               appDesc.setAppDescURL(new URL(address));
            }
            catch(MalformedURLException badURL) {
               logger.log(Level.WARNING, badURL.getMessage(), badURL);
               Boot.showError("Java Application Error", Resources.javaBundle.getString("remote.config.org.xito.address.error"), badURL);
            }
         }
  
      }
      
      /**
       * Build the Panel
       */
      private void init() {
         
         setLayout(new TableLayout(JavaRemoteConfigDialog.class.getResource("java_remote_layout.html")));
         
         //Description
         JLabel lbl = new JLabel(Resources.javaBundle.getString("remote.config.xito.desc.lbl"));
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
         lbl = new JLabel(Resources.javaBundle.getString("remote.config.xito.address.lbl"));
         add("address_lbl", lbl);
         addressTF = new JTextField();
         add("address", addressTF);
         
         //Name
         lbl = new JLabel(Resources.javaBundle.getString("config.name.lbl"));
         add("name_lbl", lbl);
         nameTF = new JTextField();
         nameTF.setDocument(new ValidatableDocument(BaseLaunchDesc.MAX_NAME_LENGTH));
         add("name", nameTF);
         
         //Title
         lbl = new JLabel(Resources.javaBundle.getString("config.title.lbl"));
         add("title_lbl", lbl);
         titleTF = new JTextField();
         titleTF.setDocument(new ValidatableDocument(BaseLaunchDesc.MAX_DISPLAYNAME_LENGTH));
         add("title", titleTF);
                         
         //SeperateVM
         seperateVMCB = new JCheckBox(Resources.javaBundle.getString("config.seperatevm.lbl"));
         seperateVMCB.setHorizontalTextPosition(SwingConstants.LEFT);
         seperateVMCB.setBorder(null);
         add("separate_vm", seperateVMCB);
               
         readDesc();
      }
      
      /**
       * Read the Settings from the Desc and populate the Panels
       */
      private void readDesc() {
         if(appDesc.getAppDescURL() != null)
            addressTF.setText(appDesc.getAppDescURL().toString());
         
         nameTF.setText(appDesc.getName());
         titleTF.setText(appDesc.getTitle());
         seperateVMCB.setSelected(appDesc.useSeperateVM());
      }
      
   }
}
