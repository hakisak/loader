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

import java.awt.*;
import java.util.*;
import java.util.logging.*;
import java.io.Serializable;
import java.net.*;
import java.security.*;
import java.security.cert.*;

import javax.swing.*;

import org.xito.boot.ui.*;
import org.xito.dialog.*;

/**
 * Policy for Boot environment.  Will determine the permissions that should be granted
 * to running applications and prompt the user for permission to grant permissions to new
 * applications.
 *
 * @author Deane Richan
 * @version $Revision: 1.36 $
 */
public class BootPolicy extends Policy {
   
   private static Logger logger = Logger.getLogger(BootPolicy.class.getName());
      
   /* Grant Permission Options */
   public static final int DENY_PERMISSION = -1;
   public static final int ALLOW_PERMISSION = 0;
   public static final int ONE_TIME_CODESOURCE = 0;
   public static final int ONE_TIME_APP = 1;
   public static final int ALWAYS_APP = 2;
   public static final int ALWAYS_FOR_SIGNER = 3;
   
   /** classes from this source have all permissions */
   private static CodeSource bootStrapSource;
   private static CodeSource bootDirSource;
   private static ClassLoader bootStrapLoader;
   
   /** classes from this source have all permissions */
   private static CodeSource systemSource;
   
   /** the previous policy */
   private static Policy systemPolicy;
      
   /** this policy session grants **/
   private Hashtable sessionGrants = new Hashtable();
   
   /** Policy Store **/
   private PolicyStore policyStore = new XMLPolicyStore();
   private String trustedCertSerialNum;
   
   /** Thread Group of BootStrap AppContext **/
   //ThreadGroup contextGroup;
      
   private BootSecurityManager securityManager;
   private SimplePermPromptDialog permPromptDialog;
      
   protected BootPolicy(BootSecurityManager securityManager) {
 
      try {
         bootStrapSource = this.getClass().getProtectionDomain().getCodeSource();
         bootStrapLoader = this.getClass().getClassLoader();
         bootDirSource = new CodeSource(new URL(Boot.getBootDir().toURI().toString() + "-"), (java.security.cert.Certificate[])null);
         logger.info("BootDir CodeSource:"+bootDirSource.getLocation());
      }
      catch(MalformedURLException badURL) {
         logger.log(Level.SEVERE, badURL.getMessage(), badURL);
      }
            
      systemSource = Policy.class.getProtectionDomain().getCodeSource();
      systemPolicy = Policy.getPolicy();
      
      if (!Boot.isHeadless()) {
         new JWindow().getOwner();
         initDialogs();
      }
      
      //Get trusted Cert serial number
      trustedCertSerialNum = System.getProperty("trusted.cert.serial");
      
      //trim the serial number
      if(trustedCertSerialNum != null)
         trustedCertSerialNum = trustedCertSerialNum.trim();
      
      logger.info("Trusted Cert:"+trustedCertSerialNum);
   }
   
   /**
    * Build initial Permission Prompt Dialogs
    */
   private void initDialogs() {
      
      permPromptDialog = new SimplePermPromptDialog();
      //securityDialog = new SecurityPermissionDialog(policyStore, securityManager.securityLogger);
   }
   
   /**
    * Prompt the user to grant permission for an action. This can only be called from Trusted code
    * If this method returns true then the user granted permission and the Trusted code can perform 
    * an operation on behalf of untrusted code
    *
    * @param subtitle for org.xito
    * @param message for org.xito
    * @param perm to be granted
    * @param execDesc of the App requesting the Grant
    * @param ProtectionDomain of the App requesting the Grant
    */
   protected boolean promptForPermission(String subtitle, String msg, Permission perm, ExecutableDesc execDesc) {
      
      //The caller must be trusted so display the Permission Prompt
      permPromptDialog.setTitles(null , subtitle);
      permPromptDialog.setMessageText(msg);
      if(EventQueue.isDispatchThread()) {
         permPromptDialog.setVisible(true);
      }
      else {
         Boot.invokeAndWait(new Runnable() {
            public void run() {
               permPromptDialog.setVisible(true);
            }
         });
      }
      
      //Store Option
      int option = permPromptDialog.getSelectedPermOption();
      boolean granted = (permPromptDialog.getResult() == DialogManager.YES);
      permPromptDialog.reset();      
      
      if(granted && option == ALWAYS_APP) {
         try {
            Permissions perms = new Permissions();
            perms.add(perm);
            policyStore.storePermissions(execDesc, perms);
         }
         catch(PolicyStoreException exp) {
            String title = Resources.bundle.getString("policy.store.error.title");
            String error_msg = Resources.bundle.getString("policy.store.error.msg");
            Boot.showError(title, error_msg, exp);
         }
      }
            
      return granted;
   }
   
   /**
    * Get Permissions for a CodeSource this is called everytime time a class is resolved.
    * 
    */
   public PermissionCollection getPermissions(CodeSource codesource) {
      //Get a set of Loader Grants for this ClassLoader
      Hashtable loaderGrants = (Hashtable)sessionGrants.get(""+Thread.currentThread().getContextClassLoader().hashCode());
      if(loaderGrants == null) loaderGrants = new Hashtable();
      
      return (PermissionCollection)loaderGrants.get(codesource.getLocation());
   }
   
   /**
    * Get permissions for a protection domain. This is called everytime a class is resolved.
    */
   public PermissionCollection getPermissions(ProtectionDomain domain) {
      return getPermissions(domain, null);
   }
   
   /**
    * Get permissions for a protection domain. This is called everytime a class is resolved.
    */
   public PermissionCollection getPermissions(ProtectionDomain domain, Permission permission) {
      
      CodeSource codesource = domain.getCodeSource();
      
      //Get a set of Loader Grants for this ClassLoader
      Hashtable loaderGrants = (Hashtable)sessionGrants.get(""+domain.getClassLoader().hashCode());
      if(loaderGrants == null) loaderGrants = new Hashtable();
      
      //Check for codesource specific grants from the loader
      PermissionCollection perms = (PermissionCollection)loaderGrants.get(codesource.getLocation());
      if(perms != null) {
         return perms;
      }
            
      //Check for app specific grant
      ClassLoader loader = domain.getClassLoader();
      if(loader instanceof CacheClassLoader) {
         ExecutableDesc execDesc = ((CacheClassLoader)loader).getExecutableDesc();
         perms = (PermissionCollection)loaderGrants.get(execDesc.getSerialNumber());
         if(perms != null) {
            //cache the permission by location for easier retrieval
            loaderGrants.put(codesource.getLocation(), perms);
            return perms;
         }
      }
      
      //Get the stored Permissions, Prompt the User for Permissions, get them from the System Policy
      perms = (PermissionCollection)AccessController.doPrivileged((PrivilegedAction) new PermissionHandler(domain, permission));

      return perms;
   }
   
   /**
    * Log a security log message using a AccessController.doPrivileged
    */
   protected void logSecurity(final Level level, final String message) {

      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            BootSecurityManager.securityLogger.log(level, message);
            return null;
         }
      });
   }
   
   /**
    * Refresh. Clears Session grants making the Application obtain permissions from the stored policy
    */
   public synchronized void refresh() {
      //sessionGrants.clear();
   }
   
   /**
    * Refresh A ClassLoaders grants
    */
   public synchronized void refresh(ClassLoader loader) {
      if(loader == null) return;
      
      sessionGrants.remove(loader.hashCode()+"");
   }
   
   /**
    * Return an all-permissions collection.
    */
   public Permissions getAllPermissions() {
      Permissions result = new Permissions();
      
      result.add( new AllPermission() );
      return result;
   }
   
   /**
    * Prompt for the user to grant permission for this action
    */
   private synchronized int promptForPermission(final ProtectionDomain domain, final ExecutableDesc execDesc, final Permission permission) {
      
      final SecurityPermissionDialog securityDialog = new SecurityPermissionDialog(policyStore, securityManager.securityLogger);
       
      //The caller must be trusted to display the Permission Prompt
      Integer result = (Integer)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            securityDialog.setSecurityInfo(domain, execDesc, permission); 
            if(EventQueue.isDispatchThread()) {
               securityDialog.setVisible(true);
            }
            else {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                      public void run() {
                         Toolkit.getDefaultToolkit().beep(); 
                         securityDialog.setVisible(true);
                      }
                   });
                }
                catch(Exception exp) {
                    logger.log(Level.SEVERE, exp.getMessage(), exp);
                }
            }
            
            return new Integer(securityDialog.getGrantOption());
         }
      });
      
      return result.intValue();
            
      //spawn a Thread under the context group to show org.xito in context of BootStrap
      /*
      promptThread = new PromptThread(domain, execDesc, permission);

      promptThread.start();
      try {
         promptThread.join();
      }
      catch(InterruptedException exp) {
         logger.log(Level.SEVERE, exp.getMessage(), exp);
         return DENY_PERMISSION;
      }
       
      
      if(promptThread.result == DENY_PERMISSION)
         return DENY_PERMISSION;
      else {
         return promptThread.grantOption;
      }
       */
   }
   
   /**
    * Check for security implications on domain for permission
    */
   public boolean implies(ProtectionDomain domain, Permission permission) {
      
      //BootStrap codesource always implies true
      if(domain.getCodeSource().equals(bootStrapSource)) {
         return true;
      }

      //BootStrap ClassLoader always implies true
      if(domain.getClassLoader() == bootStrapLoader) {
          return true;
      }
      
      //Do normal permission check
      PermissionCollection permissions = getPermissions(domain, permission);
      if(permissions.implies(permission)) {

         BootSecurityManager.securityLogger.fine("Granting "+permission.getName()+" to codesource:"+domain.getCodeSource());
         return true;
      }
      else {
         BootSecurityManager.securityLogger.fine("NOT Granting "+permission.getName()+" to codesource:"+domain.getCodeSource());
         return false;
      }
   }
   
   /**
    * Store the requested permission 
    */
   protected void storePermission(int mode, ExecutableDesc execDesc, ProtectionDomain domain, PermissionCollection perms) {
   
      //Get a set of Loader Grants for this ClassLoader
      Hashtable loaderGrants = (Hashtable)sessionGrants.get(""+domain.getClassLoader().hashCode());
      if(loaderGrants == null) {
         loaderGrants = new Hashtable();
         sessionGrants.put(""+domain.getClassLoader().hashCode(), loaderGrants);
      }
      
      //We store Denies just for this session only in the ClassLoader
      if(mode == DENY_PERMISSION) {
         loaderGrants.put(domain.getCodeSource().getLocation(), perms);
         return;
      }
      
      //one time codesource grant
      //We don't store the permission but just remember it for this session in the ClassLoader
      if(mode == ONE_TIME_CODESOURCE) {
         logSecurity(Level.INFO, "Storing One Time Codesource Permission for:"+domain.getCodeSource().getLocation());
         loaderGrants.put(domain.getCodeSource().getLocation(), perms);
         return;
      }
      
      //one time app grant
      //We don't store the permission but we remember the whole application for this session
      if(mode == ONE_TIME_APP) {
         logSecurity(Level.INFO, "Storing One Time App Permission for:"+execDesc.getDisplayName());
         loaderGrants.put(execDesc.getSerialNumber(), perms);
         return;
      }
      
      try {
         //Always App grant
         //We remember the application and we store it in the Policy Store
         if(mode == ALWAYS_APP) {
            logSecurity(Level.INFO, "Storing Always App Permission for:"+execDesc.getDisplayName());
            loaderGrants.put(execDesc.getSerialNumber(), perms);
            policyStore.storePermissions(execDesc, perms);
         }
         
         //Always for the certificate
         if(mode == ALWAYS_FOR_SIGNER) {
            java.security.cert.Certificate[] certs = domain.getCodeSource().getCertificates();
            if(certs.length>0 && certs[0]!=null)  {
               X509Certificate x509 = (X509Certificate)certs[0];
               
               logSecurity(Level.INFO, "Storing Always for Signer: "+x509.getSubjectDN().toString());
               loaderGrants.put(execDesc.getSerialNumber(), perms);
               policyStore.storePermissions(x509, perms);
            }
            
         }
      }
      catch(PolicyStoreException storeExp) {
         logger.log(Level.SEVERE, storeExp.getMessage(), storeExp);
         Boot.showError("Security Manager Error","<html>There was an error trying to store the permission:"+storeExp.getMessage()+"</html>", storeExp);
      }
      catch(ClassCastException badCast) {
         logger.log(Level.SEVERE, badCast.getMessage(), badCast);
          Boot.showError("Security Manager Error","<html>There was an error trying to store the permission. The certificate is not an X509Certificate</html>", badCast);
      }
   }
   
   /**
    * Add Permissions from one Collection to another
    */
   protected static void addPermissions(PermissionCollection source, PermissionCollection target) {
      if(source == null || target == null) return;
      
      Enumeration perms = source.elements();
      while(perms.hasMoreElements()) {
         Permission perm = (Permission)perms.nextElement();
         if(target.implies(perm)) continue;
         else {
            target.add(perm);
         }
      }
   }
      
   /**************************************************************************
    * Class that deals with figuring out permissions
    * It must be called in a priviledged Action 
    **************************************************************************/
   private final class PermissionHandler implements PrivilegedAction {
      
      private ProtectionDomain domain;
      private Permission permission;
      
      public PermissionHandler(ProtectionDomain d, Permission p) {
         domain = d;
         permission = p;
      }
      
      /**
       * Main method of Permission Handler
       */
      public Object run() {
        
         //Get a set of Loader Grants for this ClassLoader
         Hashtable loaderGrants = (Hashtable)sessionGrants.get(""+domain.getClassLoader().hashCode());
         if(loaderGrants == null) {
            loaderGrants = new Hashtable();
            sessionGrants.put(""+domain.getClassLoader().hashCode(), loaderGrants);
         }
         
         ExecutableDesc execDesc = null;
         PermissionCollection perms = null;
         PermissionCollection storedPerms = null;
         ClassLoader loader = domain.getClassLoader();
         if(loader instanceof CacheClassLoader) {
            execDesc = ((CacheClassLoader)loader).getExecutableDesc();
         }
         
         //All code from the bootstrap jar is automatically granted all permissions
         if(bootStrapSource.implies(domain.getCodeSource())) {
            loaderGrants.put(domain.getCodeSource().getLocation(), getAllPermissions());
            BootSecurityManager.securityLogger.fine("Granting <All Permissions> to codesource:"+domain.getCodeSource());
            return getAllPermissions();
         }
         
         //There is a bug for some CodeSources with Spaces. This means even though the codesource is
         //in the boot dir it fails the implies check above. This checks for that
         //by default we trust all code in the boot dir. However this can be turned off with a property
         String trustBootDir = Boot.getBootProperty(Boot.TRUST_BOOT_DIR, "true");
         if(Boolean.parseBoolean(trustBootDir) && codeSourceCheckImplies(domain.getCodeSource())) {
            loaderGrants.put(domain.getCodeSource().getLocation(), getAllPermissions());
            BootSecurityManager.securityLogger.fine("Granting <All Permissions> to codesource:"+domain.getCodeSource());
            return getAllPermissions();
         }
         
                     
         //Check to see if the domain code is signed by a trusted cert serial num
         java.security.cert.Certificate domainCerts[] = domain.getCodeSource().getCertificates();
         if(domainCerts != null && domainCerts.length >0 && domainCerts[0] instanceof X509Certificate) {
            String serialNum = ((X509Certificate)domainCerts[0]).getSerialNumber().toString(16);
            if(serialNum != null && serialNum.equals(trustedCertSerialNum)) {
               return getAllPermissions();
            }
         }
                  
         //Attempt to get permissions from Policy Store for the execDesc
         if(execDesc != null) {
            perms = policyStore.getPermissions(execDesc);
            if(perms == null) {
               perms = new Permissions();
               addPermissions(domain.getPermissions(), perms);
            }
         }
         
         //If the Executable doesn't want all Permissions then just give them Restricted
         //Or if the perms stored grants all permission then give them that
         if(execDesc != null && (execDesc.getPermissions() == null || perms.implies(new AllPermission()))) {
            loaderGrants.put(domain.getCodeSource().getLocation(), perms);
            perms.setReadOnly();
            return perms;
         }
                  
         //Attempt to get Permissions for the Cert
         if(domainCerts != null && domainCerts.length>0 && domainCerts[0]!=null)  {
            X509Certificate x509 = (X509Certificate)domainCerts[0];
            perms = policyStore.getPermissions(x509);
            if(perms != null) {
               loaderGrants.put(domain.getCodeSource().getLocation(), perms);
               perms.setReadOnly();
               return perms;
            }
         }
                  
         //At this point the code wants All Permissions but we don't have them stored
         //So we need to prompt the user
         if(execDesc != null) {
            int grantOption = promptForPermission(domain, execDesc,  permission);
            
            if(grantOption == DENY_PERMISSION) { 
               perms = domain.getPermissions();
            }
            else {   
               perms = getAllPermissions();
            }
            storePermission(grantOption, execDesc, domain, perms);
            perms.setReadOnly();
            return perms;
         }
                   
         //Default to Original System Policy
         //Attempt to get permissions for java system policy
         return getSystemPermissions();
      }
      
      /**
       * Checks to see if code source is in the boot dir to work around a bug with spaces in the
       * codesource name
       */
      private boolean codeSourceCheckImplies(CodeSource cs) {
         
         try {
            String bootdirStr = Boot.getBootDir().toURL().toString();
            String csStr = cs.getLocation().toString();
            
            if(csStr.startsWith(bootdirStr)) {
               return true;
            }
         }
         catch(MalformedURLException badBootDir) {
            logger.log(Level.WARNING, badBootDir.getMessage(), badBootDir);
         }

         return false;
      }
      
      /**
       * Get the System Permissions installed in the VM for this domain
       */
      private PermissionCollection getSystemPermissions() {
         
         PermissionCollection perms = systemPolicy.getPermissions(domain);
         final PermissionCollection perms1 = perms;
         if(perms != null) {
            sessionGrants.put(domain.getCodeSource().getLocation(), perms);
         }

         //Log a message
         if(BootSecurityManager.securityLogger.isLoggable(Level.FINE)) {

            if(perms1 != null) {
               StringBuffer msg = new StringBuffer();
               msg.append("Granting Permissions:\n");
               Enumeration permElements = perms1.elements();
               while(permElements.hasMoreElements()) {
                  Permission p =(Permission)permElements.nextElement();
                  msg.append("\t"+p.getName()+"\n");
               }
               msg.append("to codesource:"+domain.getCodeSource());
               BootSecurityManager.securityLogger.fine(msg.toString());
            }
         }
         
         return perms;
      }
   }
}
