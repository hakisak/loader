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

//Java Imports
import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.net.*;
import java.security.*;

/**
 * <p>
 * The Default Application started from Boot. By launching the ServiceManager
 * through the main method the environment is initialized by starting a set of specified Services. 
 * </p><p><b>Boot Process</b></p><p>
 * When the service manager is started it first loads services found in boot_services.xml. Each service is loaded in turn.
 * </p><p>
 * After boot_services are loaded then startup_services are loaded. The only difference between boot services and
 * startup services is that boot services must execute without failure, in otherwords they are required for the environment
 * to boot up. Startup sevices on the other hand can fail and the environment will continue to run.
 * </p><p>
 * Once the startup has completed the BootStrap will check to see if there are any visible UI Windows. If no User Interface windows can
 * be found then the BootStrap will terminate unless the -nogui option was set on the command line or in boot.properties. 
 * </p><p><b>Services</b></p><p>
 * The services that are started using the Service Starter are simple java applications. More information about Services can
 * be found by reading the {@link ServiceStarter ServiceStarter} documentation.
 * </p><p><b>Argument Summary</b></p><p>
 *
 * @author Deane Richan
 * @version $revision$
 */
public final class ServiceManager implements Serializable {
   
   public static final String BOOT_SERVICES_URL = "boot.services.url";
   public static final String STARTUP_SERVICES_URL = "startup.services.url";
   
   /** Listeners notified of Service Manager boot, startup, and endSession Events */
   private HashSet startupListeners = new HashSet();
   private HashSet serviceListeners;
   private Properties startupArgs = new Properties();
         
   private boolean sessionStarted_flag = false;
   private boolean sessionBooted_flag = false;
            
   //List of Exceptions that occured during Boot
   private java.util.List bootServiceExps;
      
   //List of Exceptions that occured during Startup
   private java.util.List startupServiceExps;
   
   //Logger to log all messages to
   private Logger logger = Logger.getLogger(ServiceManager.class.getName());
   
   /**
    * Create a ServiceManager Instance
    * protected so only the Boot can create a ServiceManager
    */
   protected ServiceManager() {
   }
      
   /**
    * Main Method of the Service Manager
    * Should only be called once. This method creates a Service Manager singleton. Calling main more
    * then once from the same classloader will have no effect.
    */
   protected void startAllServices() {
      
      if(Boot.isMinMode()) {
         logger.info("*** BootStrap Started in MINMODE ***");
      }
      
      //All Boot Session if it hasn't been booted already
      if(!hasBooted()) {
         bootServices();
      }
            
      //Only start Startup Services if we aren't in min mode
      //And they have been started already
      if(Boot.isMinMode() == false && !hasStarted()) {
         startServices();
      }
   }
   
   /**
    * Get a list of Loaded Services
    * @return List
    */
   public java.util.List getLoadedServices() {
	   return ServiceStarter.getLoadedServices();
   }
   
   /**
    * Return true if this ServiceManager Session has been Booted. The session is 
    * booted if all the Boot Services have started without error
    */
   public boolean hasBooted() {
      return this.sessionBooted_flag;
   }
   
   /**
    * Return true if this Service Manager Session has been Started. The session is
    * started if all the Startup Services have had a chance to start.
    */
   public boolean hasStarted() {
      return this.sessionStarted_flag;
   }
   
   /**
    * Add a Listener to be notified when the Session has booted, started, or ended.
    * @param listener to add
    */
   public synchronized void addStartupListener(StartupListener listener) {
            
      if(startupListeners.contains(listener)) return;
      else startupListeners.add(listener);
      
   }
   
   /**
    * Remove a StartupListener Listener
    * @param listener to remove
    */
   public void removeStartupListener(StartupListener listener) {
      if(startupListeners == null) return;
      
      startupListeners.remove(listener);
   }
   
   /**
    * Boot this Session
    *<p>
    * The boot_services.xml located in the boot dir of this App will be executed.
    *</p>
    */
   private synchronized void bootServices() {
      
      //Check to see if we already started
      if(sessionBooted_flag) return;
      
      logger.log(Level.INFO, "BOOTING Services");
      sessionBooted_flag = true;
            
      //Boot the services listed in boot_services.xml
      try {
         URL url = null;
         String bootURL = System.getProperty(BOOT_SERVICES_URL);
         if(bootURL != null) {
            url = new URL(bootURL);
         }
         else {
            File bootServicesFile = new File(Boot.getBootDir(), "boot_services.xml");
            if(bootServicesFile.exists()) {
                url = bootServicesFile.toURI().toURL();
                logger.log(Level.INFO, "Boot Services URL:"+url.toString());
            }
         }

         //Start the Boot Services, fail if any don't start
         bootServiceExps = ServiceStarter.startServices(url, true, false);
         
         ServiceStartException exp = null; 
         if(!bootServiceExps.isEmpty()) {
            Iterator it = bootServiceExps.iterator();
            while(it.hasNext()) {
               exp = (ServiceStartException)it.next();
               if(exp.getService()!=null)
                  logger.log(Level.SEVERE, "An Error occured starting boot service:"+exp.getService().getName(), exp);
               else
                  logger.log(Level.SEVERE, "An Error occured starting boot services:"+exp.getMessage(), exp);
            }
            
            //Should show an Error Dialog Here
            Boot.showError("Error Booting:"+Boot.getAppName(), "<html>There was a problem booting the Application Environment:<br><br>"+exp.getMessage()+"</html>", exp);
            Boot.endSession(true);
         }
      }
      catch(Throwable exp) {
         //This is Bad
         logger.log(Level.SEVERE, "Error Starting Boot Services!", exp);
         //Should show an Error Dialog Here
         Boot.showError("Error Booting:"+Boot.getAppName(), "<html>There was a problem booting the Application Environment:<br><br>"+exp.getMessage()+"</html>", exp);
         endSession();
      }
      
      fireSessionBooted();
   }
   
   /**
    * Fire the Service Starting message to all Startup Listeners
    */
   protected void fireServiceStarting(ServiceDesc serviceDesc) {
      
      //Fire Session Booted Messages
      if(startupListeners != null && !startupListeners.isEmpty()) {
         Iterator i = startupListeners.iterator();
         while(i.hasNext()) {
            try {
               ((StartupListener)i.next()).serviceStarting(serviceDesc);
            }
            catch (Throwable t) {
               logger.log(Level.SEVERE, t.getMessage(), t);
            }
         }
      }
   }
   
   /**
    * Fire the Session Booted message to all Startup Listeners
    */
   private void fireSessionBooted() {
      
      //Fire Session Booted Messages
      if(startupListeners != null && !startupListeners.isEmpty()) {
         Iterator i = startupListeners.iterator();
         while(i.hasNext()) {
            try{
               ((StartupListener)i.next()).sessionBooted();
            }
            catch (Throwable t) {
               logger.log(Level.SEVERE, t.getMessage(), t);
            }
         }
      }
   }
      
   /**
    * Start this Session
    *<p>
    * The start_services.xml located in the boot dir of this App will be executed.
    *</p>
    */
   private synchronized void startServices() {
      
      //Check to see if we already started
      if(sessionStarted_flag) return;
      
      logger.log(Level.INFO, "STARTING Session");
      sessionStarted_flag = true;
      
      //Start the services listed in start_services.xml
      try {
         URL url = null;
         String startupURL = System.getProperty(STARTUP_SERVICES_URL);
         if(startupURL != null) {
            url = new URL(startupURL);
         }
         else {
             File startupServicesFile = new File(Boot.getBootDir(), "start_services.xml");
             if(startupServicesFile.exists()) {
                 url = startupServicesFile.toURI().toURL();
                 logger.log(Level.INFO, "Start Services URL:"+url.toString());
             }
         }

         //Start the Startup Services
         startupServiceExps = ServiceStarter.startServices(url, false, true);
         
         if(!startupServiceExps.isEmpty()) {
            Iterator it = startupServiceExps.iterator();
            while(it.hasNext()) {
               ServiceStartException exp = (ServiceStartException)it.next();
               if(exp.getService()!=null)
                  logger.log(Level.SEVERE, "An Error occured starting startup service:"+exp.getService().getName(), exp);
               else
                  logger.log(Level.SEVERE, "An Error occured starting startup services:"+exp.getMessage(), exp);
            }
         }
      }
      catch(Throwable exp) {
         //This is Bad but we don't have to show an error to the user
         logger.log(Level.SEVERE, "Error Starting Starup Services!", exp);
      }
      
      fireSessionStarted();
   }
   
   /**
    * Fire Session Started Messages
    */
   private void fireSessionStarted() {
      //Fire Session Started Messages
      if(startupListeners != null && !startupListeners.isEmpty()) {
         Iterator i = startupListeners.iterator();
         while(i.hasNext()) {
            try {
               ((StartupListener)i.next()).sessionStarted();
            }
            catch (Throwable t) {
               logger.log(Level.SEVERE, t.getMessage(), t);
            }
         }
      }
   }
   
   /**
    * End The Service Manager Session
    */
   public void endSession() {
      
      logger.log(Level.INFO, "ENDING Session");
      
      //Fire Session Ended Messages
      if(startupListeners != null && !startupListeners.isEmpty()) {
         Iterator i = startupListeners.iterator();
         while(i.hasNext()) {
            ((StartupListener)i.next()).sessionEnded();
         }
      }
      
      logger.log(Level.INFO, "Session Ended!");
   }
}
