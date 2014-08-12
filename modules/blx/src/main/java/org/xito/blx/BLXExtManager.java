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

package org.xito.blx;

import java.net.*;
import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xito.boot.*;

/**
 * The BLXExtensionManager manages a list of currently known Extensions. It also maintains references
 * to cached extensions that can be loaded from the local machine.
 *
 * @author  drichan
 */
public class BLXExtManager {
   
   private static BLXExtManager singleton; 
   
   private Hashtable serviceExtensions = new Hashtable();
   private Hashtable extensions = new Hashtable();
   private Hashtable extHREFs = new Hashtable();
   
   /** Creates a new instance of BLXExtensionManager */
   private BLXExtManager() {
   }
   
   /**
    * Get instance of BLXExtensionManager
    */
   public static BLXExtManager getInstance() {
      
      if(singleton == null) singleton = new BLXExtManager();
      
      return singleton;
   }
   
   /**
    * Get an Extension from a Service
    */
   public synchronized BLXExtension getExtensionFromService(String serviceName) throws ExtensionLoadException {
      
      synchronized(serviceExtensions) {
         try {
            BLXExtension ext = (BLXExtension)serviceExtensions.get(serviceName);
            if(ext != null)
               return ext;

            ext = new BLXExtension(serviceName);
            serviceExtensions.put(serviceName, ext);
            return ext;
         }
         catch(ServiceNotFoundException notFound) {
            throw new ExtensionLoadException(notFound.getMessage(), notFound);
         }
      }
   }
   
   /**
    * Get an Extension requested by a BLXElement. 
    * @param blxE the BLXElement
    * @param aliasManager a manager of current Extension aliases for the context of the BLXElement
    * @throws ExtensionLoadException if the Extension can not be loaded
    */
   public BLXExtension getExtension(BLXElement blxE, BLXAliasManager aliasManager) throws ExtensionLoadException {
      BLXExtension ext = null;
      
      //First check AliasManager
      if(aliasManager != null && blxE.getExtAliasName()!=null) {
         
         ext = aliasManager.findExtension(blxE.getExtAliasName());
         if(ext != null) 
            return ext;
      }
      
      //Next check by name and version
      ext = getExtension(blxE.getExtName(), blxE.getExtVersion());
      if(ext != null) 
         return ext;
         
      //Next check by HREF
      URL extURL = null; 
      try {
         extURL = new URL(blxE.getContextURL(), blxE.getExtHREF());
         ext = getExtension(extURL);
      }
      catch(MalformedURLException exp) {
         //Ok we can't find it so throw the exception
         throw new ExtensionLoadException("Cannot load extension:"+blxE.getExtName()+" from location:"+blxE.getContextURL()+"/"+blxE.getExtHREF(), exp);
      }
      catch(ExtensionLoadException loadExp) {
         //Ok we can't find it so throw the exception
         throw new ExtensionLoadException("Cannot load extension:"+blxE.getExtName()+" from location:"+extURL.toString(), loadExp);
      }
      
      return ext;
   }
   
   /**
    * Get an Extension specified by the the URL to a BLX Extension descriptor file or blxe file.
    * @param url of blxe file
    * @throws ExtensionLoadException if the Extension cannot be loaded
    */
   public BLXExtension getExtension(URL extURL) throws ExtensionLoadException {
      
      BLXExtension ext = (BLXExtension)extHREFs.get(extURL.toString());
      if(ext != null) return ext;
       
      return loadExtension(extURL);
   }
   
   /**
    * Get a currently loaded or cached extension by name and version
    * @param name distinct name of extension
    * @param version of extension
    * @throws ExtensionLoadException if the extension cannot be loaded
    */
   public BLXExtension getExtension(String name, String version) {
      
      return null;
   }
   
   /**
    * Loads the Extension located at the provided URL and creates its ClassLoader etc.
    * @param url of Extension
    * @throws ExtensionLoadException if extension cannot be loaded
    */
   private synchronized BLXExtension loadExtension(URL extURL) throws ExtensionLoadException {
    
      BLXExtension ext = null;
      try {
         DocumentBuilder builder = BLXUtility.createDOMParser();
         Document doc = builder.parse(extURL.toString());
         ext = new BLXExtension(doc, extURL);
         extHREFs.put(extURL.toString(), ext);
      }
      catch(IOException ioExp) {
         throw new ExtensionLoadException(ioExp.getMessage(), ioExp);
      }
      catch(SAXException saxExp) {
         throw new ExtensionLoadException(saxExp.getMessage(), saxExp);
      }
      catch(ServiceNotFoundException serviceNotFound) {
         throw new ExtensionLoadException(serviceNotFound.getMessage(), serviceNotFound);
      }
      catch(InvalidBLXXMLException xmlExp) {
         throw new ExtensionLoadException(xmlExp.getMessage(), xmlExp);
      }
      
      return ext;
   }
}
