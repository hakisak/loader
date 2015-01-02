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
import java.awt.event.*;
import java.text.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.security.*;
import java.security.cert.*;
import javax.swing.*;
import org.xito.boot.*;
import org.xito.dialog.*;

/**
 * Dialog used to show Detailed information about a Certificate
 *
 * @author Deane Richan
 */
public class CertViewDialog extends CustomDialog {
      
   private PolicyStore policyStore;
   private Logger securityLogger;
   CertPanel mainPanel;
   java.security.cert.Certificate certs[];
   
   /**
    * Create a SecurityPermission Dialog
    */
   public CertViewDialog(PolicyStore policyStore, Logger securityLogger) {
      super((Frame)null);
      super.setModal(true);
      this.policyStore = policyStore;
      this.securityLogger = securityLogger;
      mainPanel = new CertPanel();
      
      //Deny is the Default Button
      ButtonType types[] = new ButtonType[]{new ButtonType("Close", 99)};
      
      String title = "Certificate Viewer";
      String subtitle = "View the Certificates for this Application";
      descriptor = new DialogDescriptor();
      setTitle(title);
      descriptor.setTitle(title);
      descriptor.setSubtitle(subtitle);
      descriptor.setCustomPanel(mainPanel);
      descriptor.setButtonTypes(types);
      descriptor.setGradiantColor(Defaults.DIALOG_GRAD_COLOR);
      descriptor.setGradiantOffsetRatio(Defaults.DIALOG_GRAD_OFFSET);
      //descriptor.setWidth(500);
      //descriptor.setHeight(500);
      //descriptor.setResizable(true);
      //descriptor.setIcon(new ImageIcon(this.getClass().getResource("/org/xito/launcher.images/encrypted32.png")));
      
      super.init();
   }
   
   /**
    * Set the Security Information to show on this Dialog
    */
   public void setCerts(java.security.cert.Certificate certs[]) {
      this.certs = certs;
      mainPanel.updatePanel();
   }
   
   /*********************************************
    * Permission Panel to show permission options
    *********************************************/
   class CertPanel extends JPanel implements ItemListener {
      
      CertInfoPanel certInfoPanel;
      CertDetailPanel certDetailPanel;
      JComboBox certList;
      DefaultComboBoxModel listModel; 
            
      public CertPanel() {
         setLayout(new TableLayout(SecurityPermissionDialog.class.getResource("cert_view_layout.html")));
         
         certInfoPanel = new CertInfoPanel(policyStore, securityLogger, false);
         add("cert_info", certInfoPanel);
         
         listModel = new DefaultComboBoxModel();
         certList = new JComboBox(listModel);
         certList.addItemListener(this);
         add("cert_list", certList);
         
         certDetailPanel = new CertDetailPanel();
         add("cert_detail", certDetailPanel);
      }
      
      public void updatePanel() {
         certInfoPanel.updatePanel(certs);
         listModel.removeAllElements();
         if(certs != null) {
            for(int i=0;i<certs.length;i++) {
               java.security.cert.X509Certificate x509 = (java.security.cert.X509Certificate)certs[i];
               listModel.addElement(x509.getSubjectDN().getName());
            }
            
            certDetailPanel.setCert(certs[0]);
         }
         
         pack();
      }
      
      /**
       * The Cert List selection changed
       */
      public void itemStateChanged(ItemEvent e) {
         if(certs != null && certs.length >0 && certList.getSelectedIndex()>=0){
            certDetailPanel.setCert(certs[certList.getSelectedIndex()]);
         }
      }
   }
   
   /**********************************************************
    * Panel that displays specific info about each Cert
    **********************************************************/
   private class CertDetailPanel extends JPanel {
      
      private static final String SUBJECT = "<html><b>Subject:</b></html>";
      private static final String ISSUER = "<html><b>Issuer:</b></html>";
      private static final String VALIDITY = "<html><b>Validity:</b></html>";
      private static final String SERIAL_NUM = "<html><b>Serial Number:</b></html>";
               
      private JLabel subjLbl;
      private JLabel issuerLbl;
      private JLabel validityLbl;
      private JLabel serialLbl;
      private JTextArea sigArea;
      private JScrollPane sigScroll;
      private SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM d, yyyy");
      
      public CertDetailPanel() {
         super();
               
         setLayout(new TableLayout(CertViewDialog.class.getResource("cert_detail_layout.html")));
                  
         subjLbl = new JLabel("Unknown");
         add("subject_lbl", new JLabel(SUBJECT));
         add("subject", subjLbl);
         
         issuerLbl = new JLabel("Unknown");
         add("issuer_lbl", new JLabel(ISSUER));
         add("issuer", issuerLbl);
         
         validityLbl = new JLabel("Unknown");
         add("validity_lbl", new JLabel(VALIDITY));
         add("validity", validityLbl);
         
         serialLbl = new JLabel("Unknown");
         add("serialnum_lbl", new JLabel(SERIAL_NUM));
         add("serialnum", serialLbl);
         
         sigArea = new JTextArea();
         sigArea.setWrapStyleWord(true);
         sigArea.setLineWrap(true);
         sigArea.setEditable(false);
         sigScroll = new JScrollPane(sigArea);
         add("serial_data", sigScroll);
      }
      
      /**
       * Set the Certificate to Display
       */
      public void setCert(java.security.cert.Certificate cert) {
         
         try {
            java.security.cert.X509Certificate x509 = (java.security.cert.X509Certificate)cert;
            subjLbl.setText("<html>"+x509.getSubjectDN().toString()+"</html>");
            issuerLbl.setText("<html>"+x509.getIssuerDN().toString()+"</html>");
            validityLbl.setText("<html>"+dateFormatter.format(x509.getNotBefore()) + " - " + dateFormatter.format(x509.getNotAfter())+"</html>");
            serialLbl.setText("<html>"+x509.getSerialNumber().toString(16)+"</html>");
            byte[] sig = x509.getSignature();
            StringBuffer buf = new StringBuffer();
            for(int i=0;i<sig.length;i++) {
               buf.append((Integer.toString( (sig[i] & 0xff ) + 0x100, 16 /* radix */ ).substring(1)+" ").toUpperCase());
            }
            sigArea.setText(buf.toString());
            sigScroll.getViewport().setViewPosition(new Point(0,0));
         }
         catch(ClassCastException badCast) {
            subjLbl.setText("Unknown");
            issuerLbl.setText("Unknown");
            validityLbl.setText("Unknown");
            serialLbl.setText("Unknown");
            sigArea.setText("");
         }
      }
   }
}
