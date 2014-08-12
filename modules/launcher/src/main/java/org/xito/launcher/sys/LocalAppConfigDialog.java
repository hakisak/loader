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

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import org.xito.dialog.*;
import org.xito.launcher.*;

/**
 *
 * @author Deane Richan
 */
public class LocalAppConfigDialog extends CustomDialog {
   
   private LocalAppDesc appDesc;
   private MainPanel mainPanel;
   
   /** Creates a new instance of LocalAppConfigDialog */
   public LocalAppConfigDialog(Frame owner, LocalAppDesc appDesc) {
      super(owner);
      if(appDesc == null) 
         this.appDesc = new LocalAppDesc();
      else
         this.appDesc = appDesc;
      
      
      super.descriptor = createDialogDesc();
      super.setModal(true);
      super.setTitle(Resources.sysBundle.getString("config.title"));
      super.init();
   }
   
   /**
    * Create the Dialog Desc for this Dialog
    */
   private DialogDescriptor createDialogDesc() {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setType(DialogManager.OK_CANCEL);
      desc.setGradiantColor(org.xito.boot.ui.Defaults.DIALOG_GRAD_COLOR);
      desc.setGradiantOffsetRatio(org.xito.boot.ui.Defaults.DIALOG_GRAD_OFFSET);
      desc.setTitle(Resources.sysBundle.getString("config.title"));
      desc.setSubtitle(Resources.sysBundle.getString("config.subtitle"));
      desc.setIcon(new ImageIcon(this.getClass().getResource("/org/xito/launcher/images/exec_32.png")));
      desc.setShowButtonSeparator(true);
      desc.setWidth(400);
      desc.setHeight(350);
      
      mainPanel = new MainPanel();
      desc.setCustomPanel(mainPanel);
      
      return desc;
   }
   
   /**
    * Get this Dialogs AppDesc
    */
   public LocalAppDesc getAppDesc() {
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
   private class MainPanel extends JPanel implements ActionListener {
      
      private JLabel errorLbl;
      private JTextField appTF;
      private JTextField nameTF;
      private JTextField titleTF;
      private JTextField startInTF;
      private JTextField argsTF;
      
      private JButton appBrowseBtn;
      private JButton dirBrowseBtn;
      
      public MainPanel() {
         init();
      }
      
      private void updateAppDesc() {
         
         appDesc.setExecutableCmd(appTF.getText());
         appDesc.setName(nameTF.getText());
         appDesc.setTitle(titleTF.getText());
         if(startInTF.getText() != null && !startInTF.getText().equals("")) {
            appDesc.setStartInDir(new File(startInTF.getText()));
         }
         else {
            appDesc.setStartInDir(null);
         }
         
         if(argsTF.getText() != null && !argsTF.getText().equals("")) {
            appDesc.setArgs(argsTF.getText());
         }
         else {
            appDesc.setArgs(null);
         }
      }
      
      /**
       * Build the Panel
       */
      private void init() {
         
         setLayout(new TableLayout(LocalAppConfigDialog.class.getResource("sys_layout.html")));
                  
         //Description
         JLabel lbl = new JLabel(Resources.sysBundle.getString("config.desc.lbl"));
         add("description", lbl);
         
         //Error Label
         errorLbl = new JLabel();
         errorLbl.setOpaque(true);
         errorLbl.setBorder(new LineBorder(SystemColor.controlShadow));
         errorLbl.setBackground(SystemColor.textHighlight);
         errorLbl.setForeground(SystemColor.textHighlightText);
         errorLbl.setVisible(false);
         add("error_lbl", errorLbl);
         
         //Application
         lbl = new JLabel(Resources.sysBundle.getString("config.app.lbl"));
         add("application_lbl", lbl);
         appTF = new JTextField();
         add("application", appTF);
         appBrowseBtn = new JButton(Resources.sysBundle.getString("config.browse.text"));
         appBrowseBtn.addActionListener(this);
         add("app_browse_btn", appBrowseBtn);
         
         //Name
         lbl = new JLabel(Resources.sysBundle.getString("config.name.lbl"));
         add("name_lbl", lbl);
         nameTF = new JTextField();
         nameTF.setDocument(new ValidatableDocument(BaseLaunchDesc.MAX_NAME_LENGTH));
         add("name", nameTF);
         
         //Title
         lbl = new JLabel(Resources.sysBundle.getString("config.title.lbl"));
         add("title_lbl", lbl);
         titleTF = new JTextField();
         titleTF.setDocument(new ValidatableDocument(BaseLaunchDesc.MAX_DISPLAYNAME_LENGTH));
         add("title", titleTF);
                  
         //Horizontal Line
         add("separator_1", new JSeparator(SwingConstants.HORIZONTAL));
                  
         //Start In
         lbl = new JLabel(Resources.sysBundle.getString("config.startin.lbl"));
         add("start_in_lbl", lbl);
         startInTF = new JTextField();
         add("start_in", startInTF);
         dirBrowseBtn = new JButton(Resources.sysBundle.getString("config.browse.text"));
         dirBrowseBtn.addActionListener(this);
         add("start_in_browse_btn", dirBrowseBtn);
                  
         //Arguments
         lbl = new JLabel(Resources.sysBundle.getString("config.args.lbl"));
         add("args_lbl", lbl);
         argsTF = new JTextField();
         add("args", argsTF);
         
         readDesc();
      }
      
      /**
       * Read the Settings from the Desc and populate the Panels
       */
      private void readDesc() {
         appTF.setText(appDesc.getExecutableCmd());
         nameTF.setText(appDesc.getName());
         titleTF.setText(appDesc.getTitle());
         if(appDesc.getStartInDir() != null) {
            startInTF.setText(appDesc.getStartInDir().toString());
         }
         argsTF.setText(appDesc.getArgs());
      }
      
      /**
       * Action Performed
       */
      public void actionPerformed(ActionEvent evt) {
         
         String btnText = Resources.sysBundle.getString("config.browse.org.xito.btn.text");
         
         if(evt.getSource() == appBrowseBtn) {
            JFileChooser fd = new JFileChooser();
            fd.setDialogTitle(Resources.sysBundle.getString("config.app.browse.title"));
            fd.setFileSelectionMode(fd.FILES_ONLY);
            fd.showDialog(LocalAppConfigDialog.this.getOwner(), btnText);
            File app = fd.getSelectedFile();
            if(app != null && app.isFile()) {
               appTF.setText(app.toString());
            }
         }
         else if(evt.getSource() == dirBrowseBtn) {
            JFileChooser fd = new JFileChooser();
            fd.setDialogTitle(Resources.sysBundle.getString("config.dir.browse.title"));
            fd.setFileSelectionMode(fd.DIRECTORIES_ONLY);
            fd.showDialog(LocalAppConfigDialog.this.getOwner(), btnText);
            File dir = fd.getSelectedFile();
            if(dir != null && dir.isDirectory()) {
               startInTF.setText(dir.toString());
            }
         }
      }
   }
}
