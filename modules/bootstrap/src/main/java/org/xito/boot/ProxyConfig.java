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

package org.xito.boot;

import java.awt.Frame;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;

import org.xito.dialog.*;
import org.xito.boot.ui.*;

/**
 * This class maintains Java HTTP and Socks proxy settings. It wraps the standard
 * System properties used by Java to configure proxy settings.
 *
 * If System properties are detected this class will use them. Otherwise it will 
 * load proxy settings from a properties file in the {app.dir}/proxy.properies. 
 * (See boot for more information on what an AppDir is).
 *
 * By default the user will not be prompted for proxy information, unless the
 * boot.proxy.prompt=true setting is set in the boot.properties file. If this value
 * is true and if a proxy.properties file is not found and the BootStrap is not
 * Headless the user will be prompted for Their proxy settings the first
 * time they start this Application.
 *
 * For Information on Proxy Setting Options supported by Java See:
 *      http://java.sun.com/j2se/1.4.2/docs/guide/net/properties.html
 *
 * @author Deane Richan
 */
public class ProxyConfig {
   
   private static final Logger logger = Logger.getLogger(ProxyConfig.class.getName());
   
   public static final String BOOT_PROXY_PROMPT = "boot.proxy.prompt";
   
   public static final String USE_PROXY = "use.proxy";
   public static final String HTTP_PROXY_HOST = "http.proxyHost";
   public static final String HTTP_PROXY_PORT = "http.proxyPort";
   public static final String HTTP_NON_PROXY_HOSTS = "http.nonProxyHosts";
   
   public static final String SOCKS_PROXY_HOST = "socksProxyHost";
   public static final String SOCKS_PROXY_PORT = "socksProxyPort";
   public static final String SOCKS_USERNAME = "java.net.socks.username";
   public static final String SOCKS_PASSWORD = "java.net.socks.password";
   
   private static final String PROXY_FILENAME = "proxy.properties";
   
   private static ProxyConfig singleton;
   
   private String httpProxyHost;
   private int httpProxyPort = -1;
   private ArrayList httpNonProxyHosts = new ArrayList();
   
   private String socksProxyHost;
   private int socksProxyPort = -1;
   private String socksProxyUsername;
   private String socksProxyPassword;
   
   private boolean proxyIsSet = false;
   private boolean useProxy_flag = false;
   
   /** Creates a new instance of ProxyConfig */
   private ProxyConfig() {
      
      //Check Java Properties
      //Java System Properties always override these settings
      proxyIsSet = readProperties(System.getProperties());
      if(proxyIsSet) return;
      
      //Attempt to Read proxy properties for this app
      try {
         File f = new File(Boot.getUserAppDir(), PROXY_FILENAME);
         String showProxyPrompt = System.getProperty(BOOT_PROXY_PROMPT, "false");
         //If the file doesn't exist and we support UI then prompt the user
         if(!f.exists() && !Boot.isHeadless()) {
            if(showProxyPrompt == null || showProxyPrompt.equals("true")) {
               showProxyDialog();
            }
         }
         else if(f.exists()) {
             Properties props = new Properties();
             props.load(new FileInputStream(f));
             readProperties(props);
             if(useProxy()) {
                 storeProperties(System.getProperties());
             }
         }
      }
      catch(IOException exp) {
         logger.log(Level.SEVERE, exp.getMessage(), exp);
         if(!Boot.isHeadless()) {
            String title = Resources.bundle.getString("boot.error.title");
            String message = Resources.bundle.getString("proxy.read.error ");
            message = MessageFormat.format(message, exp.getMessage());
            DialogManager.showError((Frame)null, title, message, DialogManager.ERROR_MSG, exp);
         }
      }
   }
   
   /**
    * Use Proxy
    */
   public boolean useProxy() {
      return this.useProxy_flag;
   }
   
   /**
    * Set Use Proxy
    */
   public void setUseProxy(boolean b) {
      this.useProxy_flag = b;
   }
   
   /**
    * Set the HTTP Proxy No Hosts
    */
   public void setHTTPNoProxyHosts(java.util.List noHosts) {
      this.httpNonProxyHosts = new ArrayList(noHosts);
   }
   
   /**
    * Get the HTTP Proxy No Hosts
    */
   public java.util.List getHTTPNoProxyHosts() {
      return httpNonProxyHosts;
   }
   
   /**
    * Get HTTP Proxy Host
    */
   public String getHttpProxyHost() {
      return httpProxyHost;
   }
   
   /**
    * Set HTTP Proxy Host
    */
   public void setHttpProxyHost(String host) {
      httpProxyHost = host;
   }
   
   /**
    * Get HTTP Proxy Port
    */
   public int getHttpProxyPort() {
      return httpProxyPort;
   }
   
   /**
    * Set HTTP Proxy Port
    */
   public void setHttpProxyPort(int port) {
      httpProxyPort = port;
   }
   
   /**
    * Get SOCKS Proxy Host
    */
   public String getSocksProxyHost() {
      return socksProxyHost;
   }
   
   /**
    * Set SOCKS Proxy Host
    */
   public void setSocksProxyHost(String host) {
      socksProxyHost = host;
   }
   
   /**
    * Get Socks Proxy Port
    */
   public int getSocksProxyPort() {
      return socksProxyPort;
   }
   
   /**
    * Set Socks Proxy Port
    */
   public void setSocksProxyPort(int port) {
      socksProxyPort = port;
   }
   
   /**
    * Get SOCKS Username
    */
   public String getSocksUsername() {
      return socksProxyUsername;
   }
   
   /**
    * Set SOCKS Username
    */
   public void setSocksUsername(String user) {
      socksProxyUsername = user;
   }
      
   /**
    * Get SOCKS Password
    */
   public String getSocksPassword() {
      return socksProxyPassword;
   }
   
   /**
    * Set SOCKS Password
    */
   public void setSocksPassword(String pw) {
      socksProxyPassword = pw;
   }
   
   /**
    * Show a Proxy Dialog to update the Settings
    */
   public void showProxyDialog() {
      
      ProxyConfigPanel configPanel = new ProxyConfigPanel(this);
      DialogDescriptor desc = new DialogDescriptor();
      
      //desc.setWidth(375);
      //desc.setHeight(425);
      desc.setWindowTitle("Proxy Configuration");
      desc.setTitle("Proxy Configuration");
      desc.setSubtitle("Configure a Proxy Server for this application");
      desc.setCustomPanel(configPanel);
      desc.setType(DialogManager.OK_CANCEL);
      desc.setGradiantColor(Defaults.DIALOG_GRAD_COLOR);
      desc.setGradiantOffsetRatio(Defaults.DIALOG_GRAD_OFFSET);
      desc.setIcon(new ImageIcon(this.getClass().getResource("ui/org.xito.launcher.images/proxy32.gif")));
      desc.setPack(true);
      //desc.setResizable(true);
      
      int result = DialogManager.showDialog(desc);
      
      //If cancel we don't save the settings meaning they will
      //be prompted again
      if(result == DialogManager.CANCEL) {
         return;
      }
      //If Ok then we save and use those settings
      else if(result == DialogManager.OK) {
         configPanel.updateConfig();
         if(useProxy()) {
            storeProperties(System.getProperties());
         }
         
         storeSettings();
      }
   }
   
   /**
    * Store the current Settings to the proxy properties file
    */
   private void storeSettings() {
      Properties props = new Properties();
      storeProperties(props);
      
      try {
         File f = new File(Boot.getUserAppDir(), PROXY_FILENAME);
         FileOutputStream out = new FileOutputStream(f);
         props.store(out, null);
      }
      catch(IOException ioExp) {
         Boot.showError(Resources.bundle.getString("proxy.store.error.title"), Resources.bundle.getString("proxy.store.error.msg"), ioExp);
      }
   }
   
   /**
    * Read Proxy settings from Properties 
    */
   private boolean readProperties(Properties props) {
      
      //Use Proxy
      if("true".equals(props.getProperty(USE_PROXY))) {
         useProxy_flag = true;
      }
      else {
         useProxy_flag = false;
      }
            
      //HTTP Settings
      httpProxyHost = props.getProperty(HTTP_PROXY_HOST);
      String p = props.getProperty(HTTP_PROXY_PORT);
      if(p != null) {
         httpProxyPort = Integer.parseInt(p);
      }
      else httpProxyPort = -1;
      
      String nonProxyHosts = props.getProperty(HTTP_NON_PROXY_HOSTS);
      if(nonProxyHosts != null && "".equals(nonProxyHosts)) {
         StringTokenizer parser = new StringTokenizer(nonProxyHosts, "|");
         while(parser.hasMoreTokens()) {
            httpNonProxyHosts.add(parser.nextToken());
         }
      }
      
      //Socks Settings
      socksProxyHost = props.getProperty(SOCKS_PROXY_HOST);
      p = props.getProperty(SOCKS_PROXY_PORT);
      if(p != null) {
         socksProxyPort = Integer.parseInt(p);
      }
      else socksProxyPort = -1;
      
      socksProxyUsername = props.getProperty(SOCKS_USERNAME);
      socksProxyPassword = props.getProperty(SOCKS_PASSWORD);
      
      //If there are settings for proxy in these properties return true
      if(httpProxyHost != null || socksProxyHost != null) {
         return true;
      }
      else {
         return false;
      }
   }
   
   /**
    * Store current settings into the properties object
    */
   private void storeProperties(Properties props) {
      
      if(useProxy_flag)
         props.setProperty(USE_PROXY, "true");
      else
         props.setProperty(USE_PROXY, "false");
      
      if(httpProxyHost != null)
         props.setProperty(HTTP_PROXY_HOST, httpProxyHost);
      if(httpProxyPort != -1) 
         props.setProperty(HTTP_PROXY_PORT, ""+httpProxyPort);
      
      if(!httpNonProxyHosts.isEmpty()) {
         Iterator it = httpNonProxyHosts.iterator();
         StringBuffer buf = new StringBuffer();
         while(it.hasNext()) {
            buf.append((String)it.next());
            buf.append("|");
         }
      }
      
      if(socksProxyHost != null) 
         props.setProperty(SOCKS_PROXY_HOST, socksProxyHost);
      if(socksProxyPort != -1) 
         props.setProperty(SOCKS_PROXY_PORT, ""+socksProxyPort);
      if(socksProxyUsername != null) 
         props.setProperty(SOCKS_USERNAME, socksProxyUsername);
      if(socksProxyPassword != null) 
         props.setProperty(SOCKS_PASSWORD, socksProxyPassword);
      
   }
   
   /**
    * Refresh this Proxy Config from the Current settings stored in System.properties
    */
   public void refresh() {
      storeProperties(System.getProperties());
   }
   
   /**
    * Initialize Proxy Settings
    */
   public static synchronized void initialize() {
      //if we are running under WebStart then it handles Proxy
      if(Boot.isQuickLaunch()) return;
      
      //If we are already initialized then return
      if(singleton != null) return;
      
      singleton = new ProxyConfig();
   }
   
   /**
    * Get the Proxy Config
    */
   public static ProxyConfig getConfig() {
      initialize();
      singleton.refresh();
      
      return singleton;
   }
      
}
