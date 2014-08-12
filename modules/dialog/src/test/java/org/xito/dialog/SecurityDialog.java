package org.xito.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateExpiredException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;


/**
 * Dialog used to show Security Permission prompt to users when launching
 * applications etc.
 *
 * @author Deane Richan
 */
public class SecurityDialog extends CustomDialog {
   
   private Logger securityLogger;
   
   PermissionPanel mainPanel;
   
   /**
    * Create a SecurityPermission Dialog
    */
   public SecurityDialog(Frame owner) {
      super(owner);
      
      mainPanel = new PermissionPanel();
      
      //Deny is the Default Button
      ButtonType types[] = new ButtonType[]{new ButtonType("Allow", 1),new ButtonType("Deny", 2, true)};
      
      String title = "Title";
      String subtitle = "Subtitle";
      super.descriptor = new DialogDescriptor();
      descriptor.setTitle(title);
      descriptor.setSubtitle(subtitle);
      descriptor.setCustomPanel(mainPanel);
      descriptor.setButtonTypes(types);
      //descriptor.setGradiantColor(Defaults.DIALOG_GRAD_COLOR);
      //descriptor.setGradiantOffsetRatio(Defaults.DIALOG_GRAD_OFFSET);
      descriptor.setWidth(400);
      //descriptor.setPack(true);
      //descriptor.setShowButtonSeparator(true);
      descriptor.setHeight(430);
      //descriptor.setResizable(true);
      //descriptor.setIcon(new ImageIcon(this.getClass().getResource("org.xito.launcher.images/encrypted32.png")));
      
      super.init();
   }
   
   public static void main(String args[]) {
      
      JFrame f = new JFrame("Testing");
      f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      f.setSize(200,200);
      f.setVisible(true);
      
      new SecurityDialog(f).setVisible(true);
   }
  
   
   /**
    * Generate a Short Codesource Description
    */
   private String generateShortCodeSource() {
 
      
      return "://This is a test of a short code/source";
   }
   
   /**
    * Generate the Description to show on the Panel
    */
   private String generateDesc() {

      StringBuffer html = new StringBuffer("<html>");
      html.append("Would you like to Grant: <b>(something)</b> to codesource: <br>");
      html.append("<b>"+generateShortCodeSource()+"</b><br> for: \"app test\".");
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
         setLayout(new TableLayout(SecurityDialog.class.getResource("security_prompt_layout.html")));
         descLbl = new JLabel(generateDesc());
         add("description", descLbl);
         
         certInfoPanel = new CertInfoPanel(securityLogger, true);
         certInfoPanel.setBorder(new TitledBorder("Certificate"));
         add("cert_info", certInfoPanel);
         
         optionsPanel = new OptionsPanel();
         add("options", optionsPanel);
      }
      
      
      public int getOptionSelected() {
         return optionsPanel.getOption();
      }
   }
   
   /*********************************************
    * Certificate Info Panel 
    *********************************************/
   public class CertInfoPanel extends JPanel {
      
      private Logger securityLogger;
      
      JLabel certIconLbl;
      JLabel certLbl;
      JButton viewCertBtn;
      
      Icon invalidIcon = new ImageIcon(this.getClass().getResource("org.xito.launcher.images/invalid_cert.png"));
      Icon validIcon = new ImageIcon(this.getClass().getResource("org.xito.launcher.images/valid_cert.png"));

      java.security.cert.Certificate certs[];
      boolean allowDetailView = false;
      
      public CertInfoPanel(Logger securityLogger, boolean allowDetailView) {
         
         this.securityLogger = securityLogger;
         this.allowDetailView = allowDetailView;
         setLayout(new TableLayout(CertInfoPanel.class.getResource("cert_info_layout.html")));
         
         certIconLbl = new JLabel(validIcon);
         add("cert_icon", certIconLbl);
         
         certLbl = new JLabel();
         add("cert_label", certLbl);
         
         viewCertBtn = new JButton("View Certificate");
                     
         if(allowDetailView) {
            
         }
         
         updatePanel(null);
         setMaximumSize(new Dimension(500,500));
      }
      
      public void paintComponent(Graphics g) {
         ((TableLayout)getLayout()).paintTableLines(this, g);
      }
      
      /**
       * Update the Info on this Panel
       */
      public void updatePanel(java.security.cert.Certificate certs[]) {
         this.certs = certs;
         
         //Hide or show the view cert button
         if(certs == null || certs.length == 0) {
            viewCertBtn.setVisible(false);
         }
         else {
            viewCertBtn.setVisible(true);
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
               htmlError = "The Certificate is not Valid or has not been signed by a trusted Certificate Authority.";
            }
            else {
               htmlError = certExp.getMessage();
            }
         }
            
         if(htmlError != null) {
            html.append("<br>Error: "+htmlError);
         }
               
         html.append("</html>");
         return html.toString();
      }
      
      /**
       * Build the Panel that holds the Certificate Information
       */
      private void checkCertPath(java.security.cert.Certificate[] certs) throws CertPathValidatorException {
         
         
      }
      
      
   }


   
   /*********************************************
    * Options Panel 
    *********************************************/
   class OptionsPanel extends JPanel {
      
      JComboBox optionCombo;
      DefaultComboBoxModel optionModel;
      
      JRadioButton oneTimeRB;
      JRadioButton oneTimeAppRB;
      JRadioButton alwaysRB;
      JRadioButton alwaysForSignerRB;
      ButtonGroup optionGroup;
      
      public OptionsPanel() {
      
         setLayout(new TableLayout(SecurityDialog.class.getResource("options_layout.html")));
         setBorder(new javax.swing.border.TitledBorder("Grant Option:"));
         
         optionModel = new DefaultComboBoxModel();
         optionModel.addElement("Just this once, for this codesource.");
         optionModel.addElement("Just this once, for this application.");
         optionModel.addElement("Always for this Application");
         optionModel.addElement("Always for codesources signed with this certificate.");         
         
         optionCombo = new JComboBox(optionModel);
         add("label", new JLabel("Grant Option:"));
         add("combo", optionCombo);
         
         /*
         optionGroup = new ButtonGroup();
         
         oneTimeRB = new JRadioButton("Just this once, for this codesource.");
         oneTimeRB.setSelected(true);
         optionGroup.add(oneTimeRB);
         add("just_once_cs", oneTimeRB);
      
         oneTimeAppRB = new JRadioButton("Just this once, for this application.");
         optionGroup.add(oneTimeAppRB);
         add("just_once_app", oneTimeAppRB);
      
         alwaysRB = new JRadioButton("Always for this Application");
         optionGroup.add(alwaysRB);
         add("always_app", alwaysRB);
      
         alwaysForSignerRB = new JRadioButton("Always for codesources signed with this certificate.");         
         optionGroup.add(alwaysForSignerRB);
         add("always_cert", alwaysForSignerRB);
          */
      }
      
      /**
       * Update the Info on this Panel
       */
      public void updatePanel() {
         
         optionCombo.setSelectedIndex(0);
      }
      
      /**
       * Get the selected option
       */
      public int getOption() {
         
         int index = optionCombo.getSelectedIndex();
         return index;
      }
   }
      
}
