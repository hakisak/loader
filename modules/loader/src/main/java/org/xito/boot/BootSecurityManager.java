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
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import java.lang.ref.*;
import javax.swing.*;
import java.security.*;

import org.xito.boot.util.*;


/**
 * <p>
 * Security manager for bootstrap environment.  
 * </p>
 * <p>
 * The security manager tracks windows created by an
 * application, allowing those windows to be disposed when the
 * application exits but the JVM does not.  If security is not
 * enabled then the first application to call System.exit will
 * halt the JVM.
 * </p>
 * <p>
 * Some functionality in this Security Manager is derived from the Security Manager
 * found at netx project on SourceForge created by Jon Maxwell
 * </p>
 *
 * @author <a href="mailto:drichan@users.sourceforge.net">Deane Richan</a>
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision: 1.32 $
 */
public class BootSecurityManager extends SecurityManager {
   
   private Class exitClass = null;
   private Vector weakWindows = new Vector(); //have to use real vector because of change from 1.4.2 to 1.4.2_08 and 1.5
   private WeakVector weakApplications = new WeakVector();
      
   /** listener installs the app's classloader on the event dispatch thread */
   private SecurityWindowListener windowListener = new SecurityWindowListener();
   private WeakReference activeApplication;
   
   public static final String XITO_SECURITY_LOG_CAT = "xito.security";
   
   protected static final Logger securityLogger = Logger.getLogger(XITO_SECURITY_LOG_CAT);

   /**
    * Creates a SecurityManager.
    */
   public BootSecurityManager() {
      super();
      // this has the side-effect of creating the Swing shared Frame
      // owner.  Since no application is running at this time, it is
      // not added to any window list when checkTopLevelWindow is
      // called for it (and not disposed).
   }
   
   /**
    * Returns whether the exit class is present on the stack, or
    * true if no exit class is set.
    */
   private boolean isExitClass(Class stack[]) {
      if (exitClass == null)
         return false;
      
      for (int i=0; i < stack.length; i++)
         if (stack[i] == exitClass)
            return true;
      
      return false;
   }
   
   /**
    * Returns true if the Class item is on the Stack
    */
   private boolean isOnStack(Class stack[], Class item) {
      
      for (int i=0; i < stack.length; i++)
         if (stack[i] == item)
            return true;
      
      return false;
   }
   
   /**
    * Returns true if the Class Name item is on the Stack
    * @param
    * @param className is a full class name or can end in * 
    */
   private boolean isOnStack(Class stack[], String className) {
      
      boolean useWildCard = false;
      if(className.endsWith("*")) {
          useWildCard = true;
          className = className.substring(0,className.lastIndexOf('*')-1);
      } 
      
      for (int i=0; i < stack.length; i++) {
         if(useWildCard){ 
             if (stack[i].getName().startsWith(className))
                return true;
         }
         else {
             if (stack[i].getName().equals(className))
                return true;
         }
      }
      
      return false;
   }
   
   /**
    * Set the exit class, which is the only class that can exit the
    * JVM; if not set then any class can exit the JVM.
    *
    * @param exitClass the exit class
    * @throws IllegalStateException if the exit class is already set
    */
   public void setExitClass(Class exitClass) throws IllegalStateException {
      if (this.exitClass != null)
         throw new IllegalStateException("Exit Class already set");
      
      this.exitClass = exitClass;
   }
   
   /**
    * Get the exit class, which is the only class that can exit the
    * JVM; if not set then any class can exit the JVM.
    *
    */
   protected Object getExitClass() {
      return this.exitClass;
   }
   
   /**
    * Return the current Application, or null if none can be
    * determined.
    */
   public AppInstance getApplication() {
      return getApplication(getClassContext());
   }
   
   /**
    * Return the application the opened the specified window (only
    * call from event dispatch thread).
    */
   protected AppInstance getApplication(Window window) {
      
      Iterator it = weakApplications.iterator();
      while(it.hasNext()) {
         AppInstance app = (AppInstance)it.next();
         if(app == null) {
             it.remove();
             continue;
         }
         
         WeakVector windows = app.getWindows();
         for(int i=0;i<windows.size();i++) {
            Object w = windows.get(i);
            if(w != null && w == window) return app;
         }
      }
      
      return null;
   }
   
   /**
    * Return the current Application, or null.
    */
   protected AppInstance getApplication(final Class stack[]) {
      
      AppInstance app = (AppInstance)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            // this needs to be tightened up
            for (int i=0; i < stack.length; i++) {
               ClassLoader loader = stack[i].getClassLoader();
               if (loader instanceof AppClassLoader) {
                  AppClassLoader apploader = (AppClassLoader)loader;
                  if (apploader.getAppInstance() != null) {
                     return apploader.getAppInstance();
                  }
               }
            }
            
            return null;
         }
      });
      
      return app;
   }
   
   /**
    * Returns the application's thread group if the application can
    * be determined; otherwise returns super.getThreadGroup()
    */
   public ThreadGroup getThreadGroup() {
      AppInstance app = getApplication();
      if (app == null)
         return super.getThreadGroup();
      
      return app.getThreadGroup();
   }
   
   /**
    * Throws a SecurityException if the permission is denied,
    * otherwise return normally.  This method always denies
    * permission to change the security manager or policy.
    */
   public void checkPermission(final Permission perm) {
      
      //First ask the Super if this permission is allowed
      SecurityException exp = null; 
      try {
         super.checkPermission(perm);
      }
      catch(SecurityException secExp) {
         exp = secExp;
      }
      
      //Check to see if they are trying to Change Security Manager
      if(perm.getName().equals("setSecurityManager") && Boot.isLaunchingExternal()==false) {
         
         showSetSecurityManagerWarning();
         
         //Throw an exception
         throw new SecurityException("Application not allowed to change Security Manager");
      }
            
      //Throw the original Expception
      if(exp != null) {
         throw exp;
      }
   }
   
   /**
    * Prompt the user to grant permission for an action. This can only be called from Trusted code
    * If this method returns true then the user granted permission and the Trusted code can perform 
    * an operation on behalf of untrusted code
    *
    * @param subtitle for org.xito
    * @param msg for org.xito
    * @param perm to be granted
    * @param execDesc of the App requesting the Grant
    */
   public boolean promptForPermission(String subtitle, String msg, Permission perm, ExecutableDesc execDesc) {
      try {
         super.checkPermission(new AllPermission());
      }
      catch(SecurityException secExp) {
         //this is ok it means the caller is not trusted so just return false
         return false;
      }
      
      //Must be using Boot Policy
      if(!(Policy.getPolicy() instanceof BootPolicy)) {
         return false;
      }
      
      return ((BootPolicy)Policy.getPolicy()).promptForPermission(subtitle, msg, perm, execDesc);
   }
   
   /**
    * Show a Warning about setting a Security Manager in the Shared VM
    */
   private void showSetSecurityManagerWarning() {
      if(Boot.isHeadless() == true) 
         return;
      
      String title = Resources.bundle.getString("boot.security.warning.title");
      title = java.text.MessageFormat.format(title, Boot.getAppDisplayName());
      String subtitle = Resources.bundle.getString("boot.security.manager.changed.subtitle");
      String msg = Resources.bundle.getString("boot.security.manager.changed.msg");
      msg = java.text.MessageFormat.format(msg, Boot.getAppDisplayName());

      Boot.showError(title,subtitle,msg, null);
   }
   
   /**
    * Show a warning message about the security violation
    */
   private void showSecurityViolation(final Permission permission) {
      
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            String permissionName = null;
            if(permission != null) permissionName = permission.toString();
            JOptionPane.showMessageDialog(null, "Security Violation: "+ permissionName +" caused by: unknown", "Security Violation", JOptionPane.OK_OPTION);
            
            return null;
         }
      });
      
      return;
   }
   
   /**
    * Checks whether the window can be displayed without an
    * warning banner, and adds the window to the list of windows to
    * be disposed when the calling application exits.
    */
   public boolean checkTopLevelWindow(Object window) {
      
      //If prompting user then pause until finished
      /*
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            Policy policy = Policy.getPolicy();
            if(policy instanceof BootPolicy) {
              Thread promptThread = ((BootPolicy)policy).getPromptThread();
              if(promptThread != null && promptThread.isAlive() && promptThread != Thread.currentThread()) {
                 try {
                    promptThread.join();
                 }
                 catch(InterruptedException exp) {
                    securityLogger.log(Level.SEVERE, exp.getMessage(), exp);
                 }
              }
            }
            
            return null;
         }
      });
       */
      
      weakWindows.add(window); 
      AppInstance app = getApplication();
      // remember window -> application mapping for focus, close on exit
      if (app != null && window instanceof Window) {
         Window w = (Window) window;
         weakApplications.add(app);
         
         w.addWindowListener(windowListener); // for dynamic context classloader
         app.addWindow(w);
      }
      
      // change coffee cup to app default icon
      ImageIcon icon = Boot.getAppIcon();
      if ((window instanceof Window) && (icon != null)) {
         Window w = (Window)window;
         while(w != null) {
            if (w instanceof Frame) {
               w.setIconImage(icon.getImage());
            }
            w = w.getOwner();
         }
      }
      
      return super.checkTopLevelWindow(window);
   }
   
   /**
    * Checks to see if a Window has been created since the Security Manager was Started
    * If there is not a visible window then the BootStrap may use this information to
    * end the Session
    */
   protected boolean checkWindowVisible() {
      
	  //Check All Windows
      for(int i=0;i<weakWindows.size();i++) {
         Window w = (Window)weakWindows.get(i);
         
         if(w != null && w.isVisible()) {
            return true;
         }
      }
           
      //Check Boot Context Frames
      Frame frames[] = Frame.getFrames();
      for(int i=0;i<frames.length;i++) {
    	  
         if(frames[i].isVisible()) 
            return true;
      }
      
      //If we make it down here then there are no visible windows
      return false;
   }
   
   
   /**
    * Checks whether the caller can exit the VM. In this implementation only the ExitClass or the Boot class
    * have permission to Exit the VM. If another application attempts to exit the VM the Application will be 
    * Identified by searching the classloaders of the call Stack and then the App will be destroyed. 
    * If the call to checkExit is not done via the Runtime class then checkExit will behave the same as the default
    * security manager
    */
   public void checkExit(int status) {
      
      //Check to see if we are in Launch External mode if so then Anybody can exit the VM so just return
      if(Boot.isLaunchingExternal()) {
         return;
      }
      
      //Check to see if Runtime is actually trying to exit the VM or is
      //Somebody just calling SecurityManager.checkVM like JFrame does when setDefaultCloseOperation is called
      Class stack[] = getClassContext();
      boolean realCall = false;
      if(isOnStack(stack, Runtime.class)) {
         realCall = true;
      }
      
      //If not a real call just let Super implementation handle it
      if(realCall == false) {
         //Check for JFrame or Frame on the Stack
         if(isOnStack(stack, java.awt.Frame.class)) {
            return;
         }
         else if(isOnStack(stack, javax.swing.JFrame.class)) {
            return;
         }
         else {
            super.checkExit(status);
            return;
         }
      }
      
      //First see if the Exit Class is on the Stack if so then its ok to exit
      if(isExitClass(stack)) {
         return; 
      }
      
      //Check to see if Boot is on the Stack if so then it is ok to exit
      if(isOnStack(stack, Boot.class)) {
         return;
      }
      
      //Check to see if Apple Application on Stack
      //if exit called from com.apple.eawt.Application then user clicked quit from App Menu
      if(isOnStack(stack, "com.apple.eawt.Application*")) return;
            
      //Now check to see if we should destroy a running App
      // but when they really call, stop only the app instead of the JVM
      AppInstance app = getApplication(stack);
      
      //At this point we can't tell what to do. This could happen if the App is using 
      //setDefaultCloseOperation on Frame or JFrame set to Exit VM
      //In such a case we can't tell which app is trying to Exit
      //So we assume the Active Application based on last active Window is the App
      if (app == null) {
         app = windowListener.getActiveApplication();
      }
      
      //Now attempt to Destory the Application
      if (app != null) {
         app.destroy();
         throw new SecurityException("Exit VM not allowed by this application");
      }
      
      if(checkWindowVisible()) {  
         securityLogger.info("Some Visible Apps are still active. Not shutting down VM");
         throw new SecurityException("Exit VM not allowed by this application");
      }
      else if(Boot.isHeadless()==false) {
         //shutdown because all visible Windows have been disposed
         securityLogger.info("Shutting Down because all Visible UI has been disposed!");
         return;
      }
            
      //Finally Check permissions by calling super
      super.checkExit(status);
   }

   /**
    * Throws a <code>SecurityException</code> if the 
    * calling thread is not allowed to dynamic link the library code 
    * specified by the string argument file. The argument is either a 
    * simple library name or a complete filename. 
    * <p>
    * This method is invoked for the current security manager by 
    * methods <code>load</code> and <code>loadLibrary</code> of class 
    * <code>Runtime</code>. 
    * <p>
    * This method calls <code>checkPermission</code> with the
    * <code>RuntimePermission("loadLibrary."+lib)</code> permission.
    * <p>
    * If you override this method, then you should make a call to 
    * <code>super.checkLink</code>
    * at the point the overridden method would normally throw an
    * exception.
    * 
    * @param      lib   the name of the library.
    * @exception  SecurityException if the calling thread does not have
    *             permission to dynamically link the library.
    * @exception  NullPointerException if the <code>lib</code> argument is
    *             <code>null</code>.
    * @see        java.lang.Runtime#load(java.lang.String)
    * @see        java.lang.Runtime#loadLibrary(java.lang.String)
    * @see        #checkPermission(java.security.Permission) checkPermission
    */
   public void checkLink(String lib) {
      Class[] execStack = getClassContext();
      
      for(int i=0;i<execStack.length;i++) {
         
         //Skip these classes on the Stack
         if(execStack[i] == this.getClass())
            continue;
         if(execStack[i] == Runtime.class)
            continue;
         if(execStack[i] == System.class)
            continue;
         
         //See if a System class is trying to load the library
         if(execStack[i].getClassLoader() == ClassLoader.getSystemClassLoader()) {
            super.checkLink(lib);
            return;
         }
      }
      
      //Thread.dumpStack();
      super.checkLink(lib);
   }

   /*****************************************************
    * Listener for Windows
    *****************************************************/
   private class SecurityWindowListener extends WindowAdapter {
      private WeakReference activeApplication;
      
      /**
       * Get the currently know application based on last window Active
       */
      public AppInstance getActiveApplication() {
         if(activeApplication != null)
            return (AppInstance)activeApplication.get();
         else 
            return null;
      }
      
      public void windowActivated(WindowEvent e) {
         AppInstance app = getApplication(e.getWindow());
         if(app != null) {
            activeApplication = new WeakReference(app);
         }
         else {
            activeApplication = null;
         }
      }
      
      public void windowDeactivated(WindowEvent e) {
         activeApplication = null;
      }
            
      public void windowClosed(WindowEvent e) {
         
         AppInstance closingApp = getApplication(e.getWindow());
         
         if(closingApp == null) return;
        
         WeakVector windows = closingApp.getWindows();
         Iterator it = windows.iterator();
         boolean openWindows = false;
         while(it.hasNext()) {
            Window w = (Window)it.next();
            if(w != null && w.isDisplayable()) openWindows = true;
         }
         
         //If no open windows check active threads and exit VM if we should
         if(openWindows == false) {
            if(Boot.isHeadless() == false && Boot.isLaunchingExternal()) {
               Boot.endSession(true);
               return;
            }
         }
      }
   };
}


