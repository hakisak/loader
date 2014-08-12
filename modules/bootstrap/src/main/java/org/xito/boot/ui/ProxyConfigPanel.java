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
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import org.xito.boot.*;
import org.xito.dialog.*;

/**
 *
 * @author DRICHAN
 */
public class ProxyConfigPanel extends TablePanel implements ActionListener {
   
   private ProxyConfig proxyConfig;
   private JCheckBox noProxyCB;
   private JLabel noProxyDescLbl;
      
   private JLabel socksServerLbl;
   private JTextField socksServerTF;
   private JLabel socksPortLbl;
   private JSpinner socksPortSpinner;
   private JButton socksAdvBtn; 
   
   private JLabel httpServerLbl;
   private JTextField httpServerTF;
   private JLabel httpPortLbl;
   private JSpinner httpPortSpinner;
   private JButton httpAdvBtn; 
   
   private SocksAdvancedPanel socksAdvPanel = new SocksAdvancedPanel();
   private HTTPAdvancedPanel httpAdvPanel = new HTTPAdvancedPanel();
   
   /** Creates a new instance of ProxyConfigPanel */
   public ProxyConfigPanel(ProxyConfig config) {
      super();
      proxyConfig = config;
      //super.setPaintBorderLines(true);
      init();
   }
   
   /**
    * Setup this Panel
    */
   private void init() {
      
      setLayout(new TableLayout(ProxyConfigPanel.class.getResource("proxy_layout.html")));
                  
      SpinnerNumberModel socksPortModel = new SpinnerNumberModel(0, 0, 65535, 1);
      SpinnerNumberModel httpPortModel = new SpinnerNumberModel(0, 0, 65535, 1);
                  
      //No Proxy CheckBox
      String html = Resources.bundle.getString("proxy.ui.no_proxy");
      noProxyCB = new JCheckBox(html);
      noProxyCB.addActionListener(this);
      add("no_proxy_cb", noProxyCB);
            
      //No Proxy Description
      html = Resources.bundle.getString("proxy.ui.no_proxy.desc");
      noProxyDescLbl = new JLabel(html);
      add("no_proxy_desc", noProxyDescLbl);
      
      //Socks Server
      socksServerLbl = new JLabel("Socks Server:", JLabel.RIGHT);
      add("socks_server_lbl", socksServerLbl);
      
      socksServerTF = new JTextField();
      add("socks_server", socksServerTF);
      
      //Socks Port
      socksPortLbl = new JLabel("Port:", JLabel.RIGHT);
      add("socks_port_lbl", socksPortLbl);
      
      JPanel socksPortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      socksPortPanel.setOpaque(false);
      socksPortSpinner = new JSpinner(socksPortModel);
      socksPortPanel.add(socksPortSpinner);
      add("socks_port", socksPortPanel);
      
      socksAdvBtn = new JButton("Advanced");
      socksAdvBtn.addActionListener(this);
      add("socks_advanced", socksAdvBtn);
            
      //HTTP Proxy Server
      httpServerLbl = new JLabel("HTTP Proxy Server:", JLabel.RIGHT);
      add("http_server_lbl", httpServerLbl);
      
      httpServerTF = new JTextField();
      add("http_server", httpServerTF);
      
      //HTTP Proxy Port
      httpPortLbl = new JLabel("Port:", JLabel.RIGHT);
      add("http_port_lbl", httpPortLbl);
      
      JPanel httpPortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      httpPortPanel.setOpaque(false);
      httpPortSpinner = new JSpinner(httpPortModel);
      httpPortPanel.add(httpPortSpinner);
      add("http_port", httpPortPanel);
            
      //Advanced Button
      httpAdvBtn = new JButton("Advanced");
      httpAdvBtn.addActionListener(this);
      add("http_advanced", httpAdvBtn);
      add("separator", new JSeparator(JSeparator.HORIZONTAL));
      loadConfig();
   }
   
   private void loadConfig() {
      
      boolean proxySet = false;
      noProxyCB.setSelected(!proxyConfig.useProxy());
            
      //Socks
      if(proxyConfig.getSocksProxyHost()!= null) {
         socksServerTF.setText(proxyConfig.getSocksProxyHost());
         proxySet = true;
      }
      if(proxyConfig.getSocksProxyPort()!= -1) {
         socksPortSpinner.setValue(new Integer(proxyConfig.getSocksProxyPort()));
      }
      
      //Http
      if(proxyConfig.getHttpProxyHost()!= null) {
         httpServerTF.setText(proxyConfig.getHttpProxyHost());
         proxySet = true;
      }
      if(proxyConfig.getHttpProxyPort()!= -1) {
         httpPortSpinner.setValue(new Integer(proxyConfig.getHttpProxyPort()));
      }
      
      if(proxySet == false) {
         noProxyCB.setSelected(true);
      }
      
      checkNoProxy();
   }
   
   /**
    * Action Performed
    */
   public void actionPerformed(ActionEvent evt) {
    
      if(evt.getSource() == noProxyCB) {
         checkNoProxy();
      }
      else if(evt.getSource() == socksAdvBtn) {
         showSocksAdvanced();
      }
      else if(evt.getSource() == httpAdvBtn) {
         showHttpAdvanced();
      }
   }
   
   private void showSocksAdvanced() {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setWindowTitle(Resources.bundle.getString("proxy.ui.socks.title"));
      desc.setTitle(Resources.bundle.getString("proxy.ui.socks.title"));
      desc.setSubtitle(Resources.bundle.getString("proxy.ui.socks.subtitle"));
      desc.setCustomPanel(socksAdvPanel);
      desc.setType(DialogManager.OK);
      
      DialogManager.showDialog((Dialog)SwingUtilities.getWindowAncestor(this), desc);
   }
   
   private void showHttpAdvanced() {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setWindowTitle(Resources.bundle.getString("proxy.ui.http.title"));
      desc.setTitle(Resources.bundle.getString("proxy.ui.http.title"));
      desc.setSubtitle(Resources.bundle.getString("proxy.ui.http.subtitle"));
      desc.setWidth(330);
      desc.setHeight(330);
      desc.setResizable(true);
      desc.setCustomPanel(httpAdvPanel);
      desc.setType(DialogManager.OK);
      
      DialogManager.showDialog((Dialog)SwingUtilities.getWindowAncestor(this), desc);
   }
   
   /**
    * If no Proxy then disable all controls otherwise enable them
    */
   private void checkNoProxy() {
      
      socksServerLbl.setEnabled(!noProxyCB.isSelected());
      socksServerTF.setEnabled(!noProxyCB.isSelected());
      socksPortLbl.setEnabled(!noProxyCB.isSelected());
      socksPortSpinner.setEnabled(!noProxyCB.isSelected());
      socksAdvBtn.setEnabled(!noProxyCB.isSelected());
      
      httpServerLbl.setEnabled(!noProxyCB.isSelected());
      httpServerTF.setEnabled(!noProxyCB.isSelected());
      httpPortLbl.setEnabled(!noProxyCB.isSelected());
      httpPortSpinner.setEnabled(!noProxyCB.isSelected());
      httpAdvBtn.setEnabled(!noProxyCB.isSelected());
   }
   
   /**
    * Update the ProxyConfig with the settings from this UI Panel
    */
   public void updateConfig() {
      
      proxyConfig.setUseProxy(!noProxyCB.isSelected());
      proxyConfig.setHttpProxyHost(httpServerTF.getText());
      proxyConfig.setHttpProxyPort(((Integer)httpPortSpinner.getValue()).intValue());
      
      proxyConfig.setSocksProxyHost(socksServerTF.getText());
      proxyConfig.setSocksProxyPort(((Integer)socksPortSpinner.getValue()).intValue());
      proxyConfig.setSocksUsername(socksAdvPanel.usernameTF.getText());
      proxyConfig.setSocksPassword(new String(socksAdvPanel.passwordTF.getPassword()));
            
      //Need to do Http Non-Proxy Hosts list
      String hosts = httpAdvPanel.hostsTextArea.getText();
      StringTokenizer parser = new StringTokenizer(hosts, ";");
      ArrayList hostList = new ArrayList();
      while(parser.hasMoreTokens()) {
         hostList.add(parser.nextToken());
      }
      proxyConfig.setHTTPNoProxyHosts(hostList);
   }
   
   /**
    * Panel for Socks Advanced Settings
    */
   public class SocksAdvancedPanel extends JPanel implements Validatable {
      
      JLabel infoLbl = new JLabel(Resources.bundle.getString("proxy.ui.socks.info"));
      JTextField usernameTF = new JTextField();
      JPasswordField passwordTF = new JPasswordField();
      JPasswordField passwordConfirmTF = new JPasswordField();
      
      public SocksAdvancedPanel() {
         
         setLayout(new TableLayout(ProxyConfigPanel.class.getResource("socks_layout.html")));
         
         
         String text = Resources.bundle.getString("proxy.ui.socks.info");
         infoLbl = new JLabel(text);
         add("info_lbl", infoLbl);
         
         text = Resources.bundle.getString("proxy.ui.socks.username");
         JLabel usernameLbl = new JLabel(text);
         add("socks_username_lbl", usernameLbl);
         add("socks_username", usernameTF);
         
         text = Resources.bundle.getString("proxy.ui.socks.password");
         JLabel passwordLbl = new JLabel(text);
         add("socks_password_lbl", passwordLbl);
         add("socks_password", passwordTF);
         
         text = Resources.bundle.getString("proxy.ui.socks.confirmPassword");
         JLabel passwordConfirmLbl = new JLabel(text);
         add("socks_password_confirm_lbl", passwordConfirmLbl);
         add("socks_password_confirm", passwordConfirmTF);
         //add("separator", new JSeparator(JSeparator.HORIZONTAL));
      }
      
      /**
       * Check to see if the settings are valid
       */
      public boolean hasValidData() {
         String pass = new String(passwordTF.getPassword());
         String confirmPass = new String(passwordConfirmTF.getPassword());
         
         if(pass.equals(confirmPass) == false) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            infoLbl.setText(Resources.bundle.getString("proxy.ui.socks.pwd.error"));
            return false;
         }
         
         if(pass.length()>0 && usernameTF.getText().length()==0) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            infoLbl.setText(Resources.bundle.getString("proxy.ui.socks.nouser.error"));
            return false;
         }
         
         infoLbl.setText(Resources.bundle.getString("proxy.ui.socks.info"));
         return true;
      }
   }
   
   /**
    * Panel for HTTP Proxy Advanced Settings
    */
   public class HTTPAdvancedPanel extends JPanel {
      
      JLabel infoLbl = new JLabel(Resources.bundle.getString("proxy.ui.http.info"));
      JTextArea hostsTextArea = new JTextArea();
      
      public HTTPAdvancedPanel() {
         
         setLayout(new TableLayout(ProxyConfigPanel.class.getResource("http_layout.html")));
         
         add("info_lbl", infoLbl);
         
         String text = Resources.bundle.getString("proxy.ui.http.hosts");
         JLabel hostsLbl = new JLabel(text);
         add("hosts_lbl", hostsLbl);

         hostsTextArea.setLineWrap(true);
         hostsTextArea.setWrapStyleWord(true);
         add("hosts", new JScrollPane(hostsTextArea));
         //add("separator", new JSeparator(JSeparator.HORIZONTAL));
         
      }
      
   }
   
}
