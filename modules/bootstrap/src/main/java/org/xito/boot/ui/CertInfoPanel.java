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

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.logging.*;
import java.security.*;
import java.security.cert.*;
import javax.swing.*;
import javax.swing.border.*;
import org.xito.boot.*;
import org.xito.dialog.*;

/**
 *
 * @author Deane Richan
 */
public class CertInfoPanel extends RoundRectPanel {
   
   private PolicyStore policyStore;
   private Logger securityLogger;
   
   JLabel certIconLbl;
   JLabel certLbl;
   JLabel viewCertLink;
   
   Icon invalidIcon = DialogManager.getWarningIcon();
   Icon validIcon = DialogManager.getInfoIcon();
   CertViewDialog viewDialog;
   java.security.cert.Certificate certs[];
   boolean allowDetailView = false;
   
   public CertInfoPanel(PolicyStore policyStore, Logger securityLogger, boolean allowDetailView) {
     
      this.policyStore = policyStore;
      this.securityLogger = securityLogger;
      this.allowDetailView = allowDetailView;
      setLayout(new TableLayout(CertInfoPanel.class.getResource("cert_info_layout.html")));
      
      certIconLbl = new JLabel();
      add("cert_icon", certIconLbl);
      
      certLbl = new JLabel();
      add("cert_label", certLbl);
      
      viewCertLink = new JLabel("view certificate");
      viewCertLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      viewCertLink.setForeground(Color.BLUE);
      Font f = viewCertLink.getFont();
      viewCertLink.setFont( f.deriveFont(f.getSize() * .80f) );
                  
      if(allowDetailView) {
         viewDialog = new CertViewDialog(policyStore, securityLogger);
         viewCertLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               viewDialog.setCerts(certs);
               viewDialog.setVisible(true);
            }
         });
         add("view_cert", viewCertLink);
      }
      
      setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
   }

   /**
    * Update the Info on this Panel
    */
   public void updatePanel(java.security.cert.Certificate certs[]) {
      this.certs = certs;
      
      //Hide or show the view cert button
      if(certs == null || certs.length == 0) {
         viewCertLink.setVisible(false);
      }
      else {
         viewCertLink.setVisible(true);
      }
      
      //Check the CertPath and generate a Message
      CertPathValidatorException certExp = null;
      try {
         checkCertPath(certs);
         certIconLbl.setIcon(validIcon);
      } catch(CertPathValidatorException e) {
         certExp = e;
         certIconLbl.setIcon(invalidIcon);
      }
      
      certLbl.setText(generateCertDesc(certs, certExp));
   }
   
   /**
    * Parse the Common Name from a DN
    */
   protected Properties getNames(Principal dn) {
      
      Properties nameProps = new Properties();
      if(dn == null) return nameProps;
      
      String name = dn.getName();
      String names[] = name.split(", ");
      StringBuffer propsBuf = new StringBuffer();
      for(int i=0;i<names.length;i++)
         propsBuf.append(names[i]+"\n");
      
      try {
         ByteArrayInputStream stream = new ByteArrayInputStream(propsBuf.toString().getBytes());
         nameProps.load(stream);
         nameProps.list(System.out);
      }
      catch(IOException ioExp) {
         securityLogger.log(Level.INFO, "Error parsing principal name", ioExp);
      }
      
      return nameProps;
   }
   
   /**
    * Generate a Description of the Certificate
    */
   private String generateCertDesc(java.security.cert.Certificate[] certs, CertPathValidatorException certExp) {
   
      if(certs == null || certs.length==0) {
         return "<html>The code is <b>not signed</b> and the authenticity of the publisher cannot be verified.</html>";
      }
      
      StringBuffer html = new StringBuffer("<html><b>Signed by:</b><br>");
      String htmlError = null;
      
      for(int i=0;i<certs.length;i++) {
         if(certs[i] instanceof java.security.cert.X509Certificate) {

            java.security.cert.X509Certificate x509 = (java.security.cert.X509Certificate)certs[i];
            Properties names = getNames(x509.getSubjectDN());
            String name = names.getProperty("CN");
            if(name == null || name.equals("Unknown")) name = names.getProperty("O");
            if(name == null) name = "Unknown";
            html.append("&nbsp;&nbsp;"+name);
            if(certExp == null) {
               html.append(" <b>(valid)</b>");
            }
            else if(certExp.getIndex() == i) {
               if(certExp.getCause() instanceof CertificateExpiredException) {
                  html.append(" <b>(expired)</b>");
                  htmlError = "The Certificate has Expired!";
               }
            }
         }
         else {
            html.append("<b>Unknown Certificate</b>");
         }

         //New Line after each cert common name
         html.append("<br>");
      }
      
      //If we don't know the error yet but there is a problem
      if(certExp != null && htmlError == null) {
         if(certExp.getCause() == null) {
            htmlError = "The Certificate is not Valid or has not been <br> signed by a trusted Certificate Authority.";
         }
         else {
            htmlError = certExp.getMessage();
         }
      }
         
      if(htmlError != null) {
         html.append("<br>"+htmlError);
      }
            
      html.append("</html>");
      return html.toString();
   }
   
   /**
    * Build the Panel that holds the Certificate Information
    */
   private void checkCertPath(java.security.cert.Certificate[] certs) throws CertPathValidatorException {
      
      if(certs == null) throw new CertPathValidatorException("No Certificates Found");
      
      CertPath certPath = null;
      try {
         KeyStore keyStore = policyStore.getKeyStore();
                  
         PKIXParameters params = new PKIXParameters(keyStore); 
         params.setRevocationEnabled(false);
         CertificateFactory factory = CertificateFactory.getInstance("X509");
         certPath = factory.generateCertPath(Arrays.asList(certs));
         
         CertPathValidator pathValidator = CertPathValidator.getInstance("PKIX");
         pathValidator.validate(certPath, params);
      }
      catch(KeyStoreException storeExp) {
         securityLogger.log(Level.SEVERE, storeExp.getMessage(), storeExp);
         throw new CertPathValidatorException("Trusted Certificates could not be verified.");
      }
      catch(CertificateException certExp) {
         securityLogger.log(Level.WARNING, certExp.getMessage(), certExp);
         throw new CertPathValidatorException("Certificates could not be validated.");
      }
      catch(NoSuchAlgorithmException noAlgExp) {
         securityLogger.log(Level.WARNING, noAlgExp.getMessage(), noAlgExp);
         throw new CertPathValidatorException("Problem with Certificate Algorithm");
      }
      catch(CertPathValidatorException validateExp) {
         securityLogger.log(Level.FINE, validateExp.getMessage(), validateExp);
         throw validateExp;
      }
      catch(InvalidAlgorithmParameterException paramExp) {
         securityLogger.log(Level.WARNING, paramExp.getMessage(), paramExp);
         throw new CertPathValidatorException("Problem with Certificate Algorithm");
      }
   }
   
   
}

