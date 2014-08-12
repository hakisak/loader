
package org.xito.launcher.jnlp.service;

import java.security.*;
import org.xito.boot.*;

/**
 *
 * @author Deane Richan
 */
abstract class AbstractServiceImpl {
   
   AppInstance appInstance;
   
   /** Creates a new instance of ClipboardServiceImpl */
   public AbstractServiceImpl(AppInstance appInstance) {
      this.appInstance = appInstance;
   }
   
   /**
    * Check the current Security Manager for a Permission
    */
   protected boolean checkPermission(Permission perm) {
      try {
         if(System.getSecurityManager() == null) return true;
         System.getSecurityManager().checkPermission(perm);
         return true;
      }
      catch(SecurityException se){
         return false;
      }
   }
   
   protected boolean promptForPermission(final String subtitle, final String msg, final Permission perm) {
      Boolean granted = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            if(!(System.getSecurityManager() instanceof BootSecurityManager)) {
               return new Boolean(false);
            }
            
            BootSecurityManager bsm = (BootSecurityManager)System.getSecurityManager();
            boolean granted = bsm.promptForPermission(subtitle, msg, perm, appInstance.getAppDesc());
            if(granted) {
               BootPolicy p = (BootPolicy)Policy.getPolicy();
               p.refresh(appInstance.getClassLoader());
            }
            
            return new Boolean(granted);
         }
      });      
      
      return granted.booleanValue();
   }
   
}
