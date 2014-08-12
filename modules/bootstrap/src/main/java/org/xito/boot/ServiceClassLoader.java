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

import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.security.*;

/**
 * ClassLoader for loading Services. Behaves the same as CacheClassLoader but also has the
 * ability for Services to be dependant on other Services already running. 
 *
 * @author  Deane Richan
 */
public class ServiceClassLoader extends CacheClassLoader {
   
   private static Logger logger = Logger.getLogger(ServiceClassLoader.class.getName());
   
   //List of all Service Loaders
   private static Map<String, ServiceClassLoader> serviceLoaders = new Hashtable<String, ServiceClassLoader>();
   
   //List of only Service Loaders that want to be added to the class path
   public static Vector systemServiceLoaders = new Vector();
   
   private ServiceDesc service;
  
   /** Creates a new instance of ServiceClassLoader */
   public ServiceClassLoader(ServiceDesc service) throws ServiceNotFoundException {
      
      //If This loader should use all Services in its classpath then specify a parent
      super(service, ServiceClassLoader.class.getClassLoader());
      
      this.service = service;
      
      //Add this loader to the list of all Service Loaders
      serviceLoaders.put(service.getName(), this);
      
      //Append this loader to list of globabl service loaders
      //These classes can be used by other applications in a system wide classpath
      if(service.appendToServiceClassPath()) {
         systemServiceLoaders.add(this);
      }
      
      //log info about service
      logger.info(toString());
   }
   
   /**
    * Find a Service Loader for a specified Name
    * @throws ServiceNotFoundException
    */
   public static ServiceClassLoader getServiceLoader(ServiceDescStub serviceRef) throws ServiceNotFoundException {
      
	  if(serviceRef == null || serviceRef.getName() == null)
         throw new ServiceNotFoundException("Service Name is null");
      
      ServiceClassLoader loader = (ServiceClassLoader)serviceLoaders.get(serviceRef.getName());
      if(loader != null) 
    	  return loader;
      
      //No reference to Service URL so throw exception
      if(serviceRef.getHREF() == null) {
    	  
         logger.info("Service Loaders:"+serviceLoaders.entrySet());
         throw new ServiceNotFoundException("Service Classloader not found for service:"+serviceRef.getName());
      }
      
      //Lets try and start the service from the service Ref
      try {
    	  //Starting the Service will cause a new ServiceClassLoader to be created;
    	  ServiceStarter.startService(serviceRef);
    	  
    	  //Look up the loader again since we just started the service
    	  loader = (ServiceClassLoader)serviceLoaders.get(serviceRef.getName());
          
    	  if(loader != null) 
        	  return loader;
    	  else
    		  throw new ServiceNotFoundException("The service:"+serviceRef.getName()+" was just started by the ServiceClassLoader could not be found");
      }
      catch(ServiceStartException startExp) {
    	  throw new ServiceNotFoundException("The service:"+serviceRef.getName()+" is not loaded", startExp);
      }
   }
   
   /**
    * Get All Service Loaders for a specified Name
    */
   public static Collection<ServiceClassLoader> getAllServiceLoaders() {
      
      List<ServiceClassLoader> list = new ArrayList<ServiceClassLoader>(serviceLoaders.size());

      for (ServiceClassLoader serviceClassLoader : serviceLoaders.values()) {
         list.add(serviceClassLoader);
      }
            
      return list;
   }
      
   /**
    * toString
    */
   public String toString() {
      
      String srvRefs = "";
      ServiceDescStub serviceRefs[] = service.getServiceRefs();
      if(serviceRefs != null) {
         for(int i=0;i<serviceRefs.length;i++) {
            srvRefs = srvRefs + serviceRefs[i].getName()+";";
         }
      }
      
      String clsPath = getClassPathString();
            
      return "ServiceClassLoader:"+this.hashCode()+": service:"+service.getName()+": srvRefs:("+srvRefs+") classpath:(" + clsPath +")";
   }
   
   /**
    * Get the Service that this ClassLoader is used for
    */
   public ServiceDesc getService() {
      return service;
   }  
   
   /**
    * Get the ExecutableDesc for this Service which is the ServiceInfo Object
    */
   public ExecutableDesc getExecutableDesc() {
      return getService();
   }
}
