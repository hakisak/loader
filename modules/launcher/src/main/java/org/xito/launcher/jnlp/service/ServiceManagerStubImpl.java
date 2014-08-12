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

package org.xito.launcher.jnlp.service;

import java.util.logging.*;
import javax.jnlp.*;
import org.xito.boot.*;
import org.xito.launcher.*;
import org.xito.launcher.jnlp.*;

/**
 *
 * @author Deane Richan
 */
public class ServiceManagerStubImpl implements ServiceManagerStub {
   
   public static final Logger logger = Logger.getLogger(ServiceManagerStubImpl.class.getName());
   
   /** Creates a new instance of ServiceManagerStubImpl */
   public ServiceManagerStubImpl() {
   }

   /**
    * Lookup a Service
    */
   public Object lookup(String serviceName) throws UnavailableServiceException {
      
      logger.info("Looking up JNLP Service:"+serviceName);
      
      //Can't be null Service Name
      if(serviceName == null) {
         throw new UnavailableServiceException("serviceName is null");
      }
      
      //First determine which app is calling this
      SecurityManager sm = System.getSecurityManager();
      if((sm instanceof BootSecurityManager) == false) {
         throw new UnavailableServiceException("Can't obtain Service when not using BootSecurityManager");
      }
      
      //return their impl of the Service
      AppInstance app = ((BootSecurityManager)sm).getApplication();
      if((app.getAppDesc() instanceof JNLPAppDesc) == false) {
         throw new UnavailableServiceException("Calling application is not a JNLP Application");
      }
      
      //Get BasicService
      if(serviceName.equals("javax.jnlp.BasicService")) {
         return new BasicServiceImpl((JNLPAppDesc)app.getAppDesc());
      }
      
      //Get FileOpenService
      if(serviceName.equals("javax.jnlp.FileOpenService")) {
         return (FileOpenService)new FileOpenServiceImpl();
      }
      
      //Get FileSaveService
      if(serviceName.equals("javax.jnlp.FileSaveService")) {
         return new FileSaveServiceImpl();
      }
      
      //Get ClipBoard Service
      if(serviceName.equals("javax.jnlp.ClipboardService")) {
         return new ClipboardServiceImpl(app);
      }
      
       //Get Print Service
      if(serviceName.equals("javax.jnlp.PrintService")) {
         return new PrintServiceImpl(app);
      }
      
      throw new UnavailableServiceException(serviceName);
   }

   public String[] getServiceNames() {
      //return a list of services that are installed
      String[] names = new String[]{
         "javax.jnlp.BasicService", 
         "javax.jnlp.ClipboardService",
         "javax.jnlp.PrintService", 
         "javax.jnlp.FileOpenService",
         "javax.jnlp.FileSaveService"   
         }; 
         
      return names;
   }
}
