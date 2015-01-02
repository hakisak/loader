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
public class JNLPConfigDialog extends CustomDialog {
   
   private static final Logger logger = Logger.getLogger(JNLPConfigDialog.class.getName());
   
   private JNLPAppDesc appDesc;
   private MainPanel mainPanel;
   
   /** Creates a new instance of JNLPConfigDialog */
   public JNLPConfigDialog(Frame owner, JNLPAppDesc appDesc) {
      super(owner);
      if(appDesc == null) 
         this.appDesc = new JNLPAppDesc();
      else
         this.appDesc = appDesc;
            
      descriptor = createDialogDesc();
      super.setModal(true);
      super.setTitle(Resources.jnlpBundle.getString("config.title"));
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
      desc.setTitle(Resources.jnlpBundle.getString("config.title"));
      desc.setSubtitle(Resources.jnlpBundle.getString("config.subtitle"));
      desc.setIcon(new ImageIcon(this.getClass().getResource("/org/xito/launcher/images/java_32.png")));
      desc.setShowButtonSeparator(true);
      desc.setResizable(true);
      desc.setWidth(Resources.getIntForOS(Resources.jnlpBundle, "config.width", Boot.getCurrentOS(), 350));
      desc.setHeight(Resources.getIntForOS(Resources.jnlpBundle, "config.height", Boot.getCurrentOS(), 300));
            
      mainPanel = new MainPanel();
      desc.setCustomPanel(mainPanel);
      
      return desc;
   }
   
   /**
    * Get this Dialogs JNLPAppDesc
    */
   public JNLPAppDesc getAppDesc() {
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
      private JCheckBox useWebStartCB;
            
      public MainPanel() {
         init();
      }
      
      private void updateAppDesc() {
         
         appDesc.setName(nameTF.getText());
         appDesc.setTitle(titleTF.getText());
         appDesc.setUseWebStart(useWebStartCB.isSelected());
         appDesc.setSeperateVM(seperateVMCB.isSelected());
         
         String address = addressTF.getText();
         if(address != null && !address.equals("")) {
            try {
               appDesc.setJNLPAddress(new URL(address));
            }
            catch(MalformedURLException badURL) {
               logger.log(Level.WARNING, badURL.getMessage(), badURL);
               Boot.showError("JNLP Application Error", Resources.jnlpBundle.getString("config.address.error"), badURL);
            }
         }
  
      }
      
      /**
       * Build the Panel
       */
      private void init() {
         
         setLayout(new TableLayout(JNLPConfigDialog.class.getResource("jnlp_layout.html")));
         
         //Description
         JLabel lbl = new JLabel(Resources.jnlpBundle.getString("config.desc.lbl"));
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
         lbl = new JLabel(Resources.jnlpBundle.getString("config.address.lbl"));
         add("address_lbl", lbl);
         addressTF = new JTextField();
         add("address", addressTF);
         
         //Name
         lbl = new JLabel(Resources.jnlpBundle.getString("config.name.lbl"));
         add("name_lbl", lbl);
         nameTF = new JTextField();
         nameTF.setDocument(new ValidatableDocument(BaseLaunchDesc.MAX_NAME_LENGTH));
         add("name", nameTF);
         
         //Title
         lbl = new JLabel(Resources.jnlpBundle.getString("config.title.lbl"));
         add("title_lbl", lbl);
         titleTF = new JTextField();
         titleTF.setDocument(new ValidatableDocument(BaseLaunchDesc.MAX_DISPLAYNAME_LENGTH));
         add("title", titleTF);
                  
         //Launch in WebStart
         useWebStartCB = new JCheckBox(Resources.jnlpBundle.getString("config.usewebstart.lbl"));
         useWebStartCB.setHorizontalTextPosition(SwingConstants.LEFT);
         useWebStartCB.setBorder(null);
         add("web_start", useWebStartCB);
         
         //SeperateVM
         seperateVMCB = new JCheckBox(Resources.jnlpBundle.getString("config.seperatevm.lbl"));
         seperateVMCB.setHorizontalTextPosition(SwingConstants.LEFT);
         seperateVMCB.setBorder(null);
         add("separate_vm", seperateVMCB);
               
         readDesc();
      }
      
      /**
       * Read the Settings from the Desc and populate the Panels
       */
      private void readDesc() {
         if(appDesc.getJNLPAddress() != null)
            addressTF.setText(appDesc.getJNLPAddress().toString());
         
         nameTF.setText(appDesc.getName());
         titleTF.setText(appDesc.getTitle());
         useWebStartCB.setSelected(appDesc.useWebStart());
         seperateVMCB.setSelected(appDesc.useSeperateVM());
      }
      
   }
}
