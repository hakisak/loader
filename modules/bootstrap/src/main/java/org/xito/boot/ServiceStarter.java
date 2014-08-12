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

import java.lang.reflect.*;
import java.net.*;
import java.io.*;
import java.security.AllPermission;
import java.security.Permission;
import java.security.Permissions;
import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * <p>
 * The ServiceStarter is designed to start a set of Services. The ServiceManager will create
 * a new ServiceStarter instance of the boot process and the startup process passing a URL an XML file listing services to
 * start. Each service should point to an XML srv file that describes the Service.
 * See {@link ServiceDesc } for more information.
 *
 * </p>
 *
 *
 * @author Deane Richan
 * @version $Revision: 1.20 $
 * @since $Date: 2008/05/20 01:59:34 $
 */
public class ServiceStarter {
   
   private static Logger logger = Logger.getLogger(ServiceStarter.class.getName());
   private static ArrayList loadedServices = new ArrayList();
   
   /**
    * Get a List of all Loaded Services. Caller must have ALL Permissions
    * @return List of loadedServices as List of ServiceDesc
    */
   public static List getLoadedServices() {
      //Check for all permissions
	   if(System.getSecurityManager() != null) {
	      System.getSecurityManager().checkPermission(new AllPermission());
	   }
             
      return new ArrayList(loadedServices);
   }
      
   /**
    * Start the Services listed in the services file at the given URL.
    * The services file will refer to individual service descriptor files that will
    * be loaded and processed in turn.
    * @param servicesURL for services file
    * @param failOnException will cause this method to return immediately if any service throws an Exception
    * @param cacheServiceCode will load the service code from the cachemanager
    * @return List of StartExceptions if any occured
    */
   public static java.util.List startServices(URL servicesURL, boolean failOnException, boolean cacheServiceCode) {
      
      ArrayList services = new ArrayList();
      ArrayList startExps = new ArrayList();
      
      //Get the XML File that lists the Services to Start
      if(servicesURL == null) {
         startExps.add(new ServiceStartException("A file listing services to start was not specified"));
         return startExps;
      }
      
      //Download servicesURL using CacheManager
      if(servicesURL.getProtocol().equals("http")) {
         try {
            Boot.getCacheManager().downloadResource(servicesURL, null);
            servicesURL = Boot.getCacheManager().getCachedFileForURL(servicesURL).toURL();
         }
         catch(IOException ioExp) {
            startExps.add(new ServiceStartException("An error occured obtaining services descriptor file.", ioExp));
            return startExps;
         }
      }
      
      //parse XML for service data
      try {
         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = builderFactory.newDocumentBuilder();
         Document doc = builder.parse(servicesURL.openStream());
         Element root = doc.getDocumentElement();
         
         NodeList childNodes = root.getElementsByTagName("service");
         for(int i=0;i<childNodes.getLength();i++) {
            Node node = childNodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("service")) {
               ServiceDesc service = new ServiceDesc(servicesURL, (Element)node);
               service.cacheServiceCode(cacheServiceCode);
               services.add(service);
            }
         }
      }
      catch(FileNotFoundException notFound) {
         logger.severe(notFound.getMessage());
         startExps.add(new ServiceStartException("A file listing services to start was not found at: "+servicesURL.toString()));
      }
      catch(org.xml.sax.SAXParseException saxExp) {
          startExps.add(new ServiceStartException("Error Processing boot services:"+servicesURL.toString()+" \n\r" + saxExp.getMessage(), saxExp));
      }
      catch(Throwable t) {
         startExps.add(new ServiceStartException(t.getMessage(), t));
      }
      
      //If we had problems loading services file then return error
      if(!startExps.isEmpty()) return startExps;
      
      //Start Each Service
      Iterator s = services.iterator();
      while(s.hasNext()) {
         try {
            //Start the Service. If we are in Min Mode only start the service if it is a Min Service
            ServiceDesc serv = (ServiceDesc)s.next();
            if(Boot.isMinMode() == false || (Boot.isMinMode() && serv.isMinimumService())) {
               startService(serv);
            }
         }
         catch(ServiceStartException startExp){
            startExps.add(startExp);
            if(failOnException) return startExps;
            else continue;
         }
      }
      
      return startExps;
   }
   
   /**
    * Start a single Service given a ServiceDescStub.
    * This is used when the Service is declared through a ServiceRef not from a boot file.
    * The service will be loaded and started
    */
   public static ServiceDesc startService(ServiceDescStub serviceRef) throws ServiceStartException {
	   
	   if(serviceRef == null)
		   throw new ServiceStartException("No service specified");
	   
	   //Check to see if the Service is already loaded
	   ServiceDesc service = getLoadedService(serviceRef.getName());
	   if(service != null)
		   throw new ServiceStartException("Service:"+serviceRef.getName()+" is already loaded");
	   	   
	   //Get the URL for the Service
	   URL serviceURL = null;
	   try {
		   serviceURL = new URL(serviceRef.getContextURL(), serviceRef.getHREF());
		   //Download serviceURL using CacheManager
		   if(serviceURL.getProtocol().equals("http")) {
			   Boot.getCacheManager().downloadResource(serviceURL, null);
		       serviceURL = Boot.getCacheManager().getCachedFileForURL(serviceURL).toURL();
		   }
	   }
	   catch(MalformedURLException badURL) {
		   throw new ServiceStartException("Invalid service URL:"+serviceRef.getHREF());
	   }
	   catch(IOException ioExp) {
           throw new ServiceStartException("An error occured obtaining services descriptor file.", ioExp);
       }
	   
	   //parse XML for service data
	   service = new ServiceDesc(serviceRef);
	   service.cacheServiceCode(true);
	   service.loadXMLData();           
                    
       //Now Start the Service
       startService(service);
      
       //Return the found service
	   return service;
   }
   
   /**
    * Find a loaded service by name
    * @param name
    * @return
    */
   private static ServiceDesc getLoadedService(String name) {
	   
	   Iterator it = loadedServices.iterator();
	   while(it.hasNext()) {
		   ServiceDesc service = (ServiceDesc)it.next();
		   if(service.getName().equals(name))
			   return service;
	   }
	   
	   return null;
   }
   
   /**
    * Start a single Service app given a Class Name and Arguments
    */
   private static void startService(ServiceDesc service) throws ServiceStartException {
      
      //Load Service info. This causes a full parse
      service.loadXMLData();
      
      //Inform listeners
      Boot.getServiceManager().fireServiceStarting(service);
      
      if(service.getName() == null) {
         throw new ServiceStartException("Service Name not specified in service descriptor");
      }
      logger.log(Level.INFO, "Starting Service:"+service.getName());
      
      createServiceLoaderAndStart(service);
   }

   /**
    * Create the service class loader and start the service
    * @param service
    * @throws ServiceStartException
    */
   private static void createServiceLoaderAndStart(final ServiceDesc service) throws ServiceStartException {

      //Create the Class Loader and start the Service if necessary
      try {
         ClassLoader serviceLoader = new ServiceClassLoader(service);

         //Prompt for Permission
         //Prompt the user to grant this Service All Permissions
         //if(!promptForPermission(service)) {
         //   return;
         //}

         //Service Class name
         if(service.getServiceClassName() == null) {
            logger.log(Level.INFO, "No class for Service:"+service.getName()+" specified.");
            return;
         }

         //Service Class
         Class serviceCls = serviceLoader.loadClass(service.getServiceClassName());

         try{
            //Set ServiceInfo
            Method setServiceInfo = serviceCls.getMethod("initService", new Class[]{ServiceDesc.class});
            setServiceInfo.invoke(null, new Object[]{service});
         }
         catch(NoSuchMethodException noMethod){
            //If init method not found this is ok because it is optional
            //All Other exceptions will pass through os if a runtime happens in the init method
            //the service will not start
         }

         //Execute Main Method
         Method mainM = serviceCls.getMethod("main", new Class[]{String[].class});
         mainM.invoke(null, new Object[]{new String[]{}});

         loadedServices.add(service);
      }
      catch(Throwable t) {
         throw new ServiceStartException(service, "<html>" + t.getClass().getName() + " thrown while starting service:"+service.getName()+".<br><b>Error: "+getCauseMessage(t)+"</b></html>", t);
      }
   }
   
   private static String getCauseMessage(Throwable t) {
      if(t == null) return null;
      
      if(t.getMessage() == null)
         return getCauseMessage(t.getCause());
      else
         return t.getMessage();
   }
   
   /**
    * Get a URL from a uri using the CodeBase if required
    */
   private static URL getURL(String uri) {
      URL url = null;
      try {
         url = new URL(uri);
      }
      catch(MalformedURLException exp) {
         //if argument is not a valid URL then get URL from codebase + arg
         try {
            url = new URL(Boot.getBootDir().toURL(), uri);
         }
         catch(Exception e) {
            e.printStackTrace();
         }
      }
      
      return url;
   }
}
