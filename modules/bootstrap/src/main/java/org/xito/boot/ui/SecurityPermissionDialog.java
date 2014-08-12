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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.security.*;
import java.security.cert.*;
import javax.swing.*;
import javax.swing.border.*;
import org.xito.boot.*;
import org.xito.dialog.*;

/**
 * Dialog used to show Security Permission prompt to users when launching
 * applications etc.
 *
 * @author Deane Richan
 */
public class SecurityPermissionDialog extends JDialog implements ActionListener {
   
   private PolicyStore policyStore;
   private Logger securityLogger;
   private boolean allow;

   DialogTitlePanel titlePanel;
   JPanel bottomPanel;
   JButton allowBtn, denyBtn;
   PermissionPanel mainPanel;
   ProtectionDomain domain;
   ExecutableDesc execDesc;
   Permission permission;
      
   /**
    * Create a SecurityPermission Dialog
    */
   public SecurityPermissionDialog(PolicyStore policyStore, Logger securityLogger) {

      super((Frame)null);
      super.setModal(true);
      this.policyStore = policyStore;      
      this.securityLogger = securityLogger;
      mainPanel = new PermissionPanel();
      setBackground(Color.WHITE);
      
      //Deny is the Default Button
      ButtonType types[] = new ButtonType[]{new ButtonType("Allow", BootPolicy.ALLOW_PERMISSION),new ButtonType("Deny", BootPolicy.DENY_PERMISSION, true)};

      titlePanel = new DialogTitlePanel("Grant Permissions", DialogTitlePanel.class.getResource("org.xito.launcher.images/keychain48.png"));
      getContentPane().add(titlePanel, BorderLayout.NORTH);

      //setup bottom panel
      bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      bottomPanel.setOpaque(false);
      allowBtn = new JButton("Allow");
      allowBtn.addActionListener(this);
      denyBtn = new JButton("Deny");
      denyBtn.addActionListener(this);
      bottomPanel.add(allowBtn);
      bottomPanel.add(denyBtn);
      getContentPane().add(bottomPanel, BorderLayout.SOUTH);

      //Permission panel
      getContentPane().add(mainPanel);

      //Set default button
      denyBtn.setDefaultCapable(true);
      getRootPane().setDefaultButton(denyBtn);

      setSize(480, 400);

   }
   
   /**
    * Set the Security Information to show on this Dialog
    */
   public void setSecurityInfo(ProtectionDomain domain, ExecutableDesc execDesc, Permission permission) {
      
      this.domain = domain;
      this.execDesc = execDesc;
      this.permission = permission;
      this.allow = false;

      //Update Titles
      String title = "Grant "+execDesc.getPermissionDescription()+" for: "+execDesc.getDisplayName();
      String subtitle = "To codesource: "+generateShortCodeSource();
      super.setTitle(title);
      //TODO
      //setTitles(title, subtitle);
      
      //super.dialogResult = BootPolicy.DENY_PERMISSION;
      mainPanel.updatePanel();
      //super.pack();

      DialogManager.centerWindowOnScreen(this);
   }

   /**
    * ActionPerformed for ActionListener
    * @param e
    */
   public void actionPerformed(ActionEvent e) {
      if(e.getSource() == allowBtn) {
         allow = true;
      }
      else if(e.getSource() == denyBtn) {
         allow = false;
      }

      //Hide this org.xito
      this.setVisible(false);
   }

   /**
    * Get the GrantOption the user selected
    */
   public int getGrantOption() {
      if(!allow)
         return BootPolicy.DENY_PERMISSION;
      
      return mainPanel.getOptionSelected();
   }
   
   /**
    * Generate a Short Codesource Description
    */
   private String generateShortCodeSource() {
      
      URL locURL = domain.getCodeSource().getLocation();
      locURL = Boot.getCacheManager().convertFromCachedURL(locURL);
            
      String loc = locURL.toString();
      String p = locURL.getProtocol();
      String host = (locURL.getHost() != null)?locURL.getHost():"";
      String file = locURL.getPath();
      file = (file.lastIndexOf('/')>=0)?"..."+file.substring(file.lastIndexOf('/')):file;
      
      return p+"://"+host+file;
   }
   
   /**
    * Generate the Description to show on the Panel
    */
   private String generateDesc() {

      String permDesc = null;
      String appName = null;
      String type = null;

      if(execDesc != null) {
         permDesc = execDesc.getPermissionDescription();
         appName = execDesc.getDisplayName();
         type = execDesc.getDisplayExecutableType();
      }
      else {
         permDesc = "All Permissions";
         appName = "unknown";
      }
      StringBuffer html = new StringBuffer("<html>");
      html.append("Would you like to Grant: <b>(" + permDesc.toUpperCase() + ")</b> to " + type + ": <br>");
      html.append("&nbsp;&nbsp;&nbsp;&nbsp;<b>" + appName + "</b><br>");
      html.append("&nbsp;&nbsp;&nbsp;&nbsp;<i>" + generateShortCodeSource() + "</i>.");
      html.append("</html>");

      return html.toString();
   }
   
   /*********************************************
    * Permission Panel to show permission options
    *********************************************/
   class PermissionPanel extends JPanel {
       
      JLabel descLbl;
      CertInfoPanel certInfoPanel;
      OptionsPanel optionsPanel;
      
      public PermissionPanel() {
         
         setOpaque(false);
         setLayout(new TableLayout(SecurityPermissionDialog.class.getResource("security_prompt_layout.html")));
         descLbl = new JLabel();
         add("description", descLbl);
         
         certInfoPanel = new CertInfoPanel(policyStore, securityLogger, true);
         add("cert_info", certInfoPanel);
         
         optionsPanel = new OptionsPanel();
         add("options", optionsPanel);
      }
      
      public void updatePanel() {
         descLbl.setText(generateDesc());
         certInfoPanel.updatePanel(domain.getCodeSource().getCertificates());
         optionsPanel.updatePanel();
      }
      
      public int getOptionSelected() {
         return optionsPanel.getOption();
      }
   }
   
   /*********************************************
    * Certificate Info Panel 
    *********************************************/
   
   
   /*********************************************
    * Options Panel 
    *********************************************/
   class OptionsPanel extends RoundRectPanel {

      JComboBox optionCombo;
      
      public OptionsPanel() {

         setOpaque(false);
         setLayout(new TableLayout(SecurityPermissionDialog.class.getResource("options_layout.html")));
                  
         optionCombo = new JComboBox();
         //add a mac style
         optionCombo.putClientProperty("JComboBox.isPopDown", Boolean.TRUE);

         add("label", new JLabel("Choose to Allow:"));
         add("combo", optionCombo);
      }

      protected ComboBoxModel getComboBoxModel(boolean alwaysForCodeSourceOption, String typeName) {

         String[] options = (alwaysForCodeSourceOption) ? new String[4] : new String[3];
         options[0] = "Just this once, for this code source.";
         options[1] = "Just this once, for this " + typeName + "";
         options[2] = "Always for this " + typeName + "";

         if(alwaysForCodeSourceOption) options[3] = "Always for code sources signed with this certificate.";

         return new DefaultComboBoxModel(options);
      }

      /**
       * Update the Info on this Panel
       */
      public void updatePanel() {

         java.security.cert.Certificate certs[] = domain.getCodeSource().getCertificates();

         boolean alwasysForCodeSourceOption = (certs != null && certs.length > 0);

         String type = execDesc.getDisplayExecutableType();
         ComboBoxModel model = getComboBoxModel(alwasysForCodeSourceOption, type);
         optionCombo.setModel(model);
         optionCombo.setSelectedIndex(0);

         /*
         if(certs != null && certs.length > 0) {
            alwaysForSignerRB.setVisible(true);
         }
         else {
             alwaysForSignerRB.setVisible(false);
         }
         
         oneTimeRB.setSelected(true);
         */
      }
      
      /**
       * Get the selected option
       */
      public int getOption() {
          
         int index = optionCombo.getSelectedIndex();
         if(index == 0) {
            return BootPolicy.ONE_TIME_CODESOURCE;
         }
         else if(index == 1) {
            return BootPolicy.ONE_TIME_APP;
         }
         else if(index == 2) {
            return BootPolicy.ALWAYS_APP;
         }
         else if(index == 3) {
            return BootPolicy.ALWAYS_FOR_SIGNER;
         }
         else {
            return BootPolicy.DENY_PERMISSION;
         }
      }
   }
      
}
