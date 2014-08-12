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

import java.util.*;
import java.util.logging.*;
import java.net.*;
import java.security.*;
import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

/**
 * <p>
 * Service's are defined by using a <i>servicename</i>.srv XML file. The format of
 * the Service descriptor file is:
 * <p>
 * To view a sample service xml file see the resources/sample.srv file for more information
 * </p>
 * @author Deane Ricna
 * @version $revision$
 */
public class ServiceDesc extends ExecutableDesc {
   
   //Logger to log all messages to
   private Logger logger = Logger.getLogger(ServiceDesc.class.getName());
    
   private String href;
   private boolean minimumService;
   
   private boolean appendClassPath;
   private Properties properties = new Properties();
   
   private boolean xmlLoaded = false;
      
   /** 
    * Creates a new instance of ServiceDesc from a Stub.
    * The ServiceDescStub contains the URL to a Service Desc and the
    * name of the Service. The full XML won't be parsed until you
    * call loadXMLData
    * @param serviceRef
    */
   public ServiceDesc(ServiceDescStub serviceRef) {
	   
	   this.contextURL = serviceRef.getContextURL();
	   this.href = serviceRef.getHREF();
	   this.name = serviceRef.getName();
	   
	   initParser();
	   initSecurity();
   }
   
   /** 
    * Creates a new instance of ServiceDesc 
    * @param contextURL the URL the XML Element was loaded from
    * @param node the <service> element in a startup xml that describes this service
    */
   public ServiceDesc(URL contextURL, Element node) {
      
      if(contextURL == null) throw new RuntimeException("codebase not specified");
      this.contextURL = contextURL;
            
      if(node == null) throw new RuntimeException("Service node not specified");

      initParser();
      processServiceNode(node, false);
      
      //href would have been read
      if(href == null) throw new RuntimeException("service href not specified");
            
      initSecurity();
   }

   @Override
   public String getDisplayExecutableType() {
      return "Library";
   }

   private void initSecurity() {
	  permissions = getAllPermissions();
      setPermissionDescription("All Permissions");
      restrictedPerms_flag = false;
   }
   
   /**
    * Get the Name of the Service
    */
   public String toString() {
      return name;
   }
   
   /**
    * Get Service Class Name
    */
   public String getServiceClassName() {
      return mainClass;
   }
   
   /**
    * Get Service Descriptor location
    */
   public String getHREF() {
      return href;
   }
   
   /**
    * Return true if this Service is a Minimum Service
    */
   public boolean isMinimumService() {
      return minimumService;
   }
   
   /**
    * Load the data from the HREF into this Service Info
    */
   public synchronized void loadXMLData() throws ServiceStartException {
      
      if(href == null) throw new ServiceStartException("service href not specified");
      
      //parse XML for service data
      try {
         //setup the URL to the actual SRV file
         CacheManager cm = Boot.getCacheManager();
         URL serviceURL = new URL(cm.convertFromCachedURL(contextURL), href);
         //The service file becomes our context URL for all references from here on out
         contextURL = serviceURL;
         cm.downloadResource(serviceURL, null);
         File serviceFile = cm.getCachedFileForURL(serviceURL);
         logger.info("Service Context URL:"+contextURL.toString());
         boolean e = serviceFile.exists();
         long size = serviceFile.length();

         Document doc = builder.parse(serviceFile);
         Element root = doc.getDocumentElement();
         processServiceNode(root, true);
      }
      catch(MalformedURLException urlExp) {
         throw new ServiceStartException("Invalid service href specifed:("+href+") using contextURL:("+contextURL.toString()+")", urlExp);
      }
      catch(IOException ioExp) {
         throw new ServiceStartException("Error reading service descriptor file: "+ioExp.getMessage(), ioExp);
      }
      catch(SAXException saxExp) {
         throw new ServiceStartException("can't read services xml error:"+saxExp.getMessage(), saxExp);
      }
      catch(DOMException domExp) {
         throw new ServiceStartException("can't read services xml error:"+domExp.getMessage(), domExp);
      }
   }
   
   /**
    * Return true if the XML was loaded
    */
   public boolean isXMLLoaded() {
      
      return xmlLoaded;
   }
   
   /**
    * Return true if this Service should append its class path on the end
    * of the current Service ClassPath
    */
   public boolean appendToServiceClassPath() {
      return appendClassPath;
   }
   
   /**
    * Process the <service> node of the XML
    * @param node to process
    * @param fullParse true if the full Service Element should be parsed
    */
   private void processServiceNode(Element node, boolean fullParse) throws DOMException {
      
      NodeList nodes = node.getChildNodes();
      for(int i=0;i<nodes.getLength();i++) {
         Node cNode = nodes.item(i);
         if(cNode.getNodeType() == Node.TEXT_NODE) continue;
         
         //Name
         if(cNode.getNodeName().equals("name"))
            this.name = cNode.getFirstChild().getNodeValue();
         //Display Name
         else if(cNode.getNodeName().equals("display-name"))
            this.displayName = cNode.getFirstChild().getNodeValue();
         //Href
         else if(cNode.getNodeName().equals("href"))
            this.href = cNode.getFirstChild().getNodeValue();
         //minimum-srv
         else if(cNode.getNodeName().equals("minimum-srv")) {
            if(cNode.getFirstChild()!=null) {
               String s = cNode.getFirstChild().getNodeValue();
               if(s != null && s.equals("true")) minimumService = true;
            }
         }
                  
         //Check for fullParse Flag
         if(fullParse == false) {
            continue;
         }
         
         //These items are only parsed during a full Parse
         //desc
         if(cNode.getNodeName().equals("desc")) {
            if(cNode.getFirstChild()!=null)
               this.description = cNode.getFirstChild().getNodeValue();
         }
         //version
         else if(cNode.getNodeName().equals("version")) {
            if(cNode.getFirstChild()!=null)
               this.version = cNode.getFirstChild().getNodeValue();
         }
         //service-cls
         else if(cNode.getNodeName().equals("service-cls")) {
            if(cNode.getFirstChild()!=null)
               this.mainClass = cNode.getFirstChild().getNodeValue();
         }
         //append-to-classpath
         else if(cNode.getNodeName().equals("append-to-classpath") && fullParse) {
            this.appendClassPath = Boolean.valueOf(cNode.getFirstChild().getNodeValue()).booleanValue();
         }
         //ClassPath
         else if(cNode.getNodeName().equals("classpath")) {
            processClassPathNode(contextURL, (Element)cNode);
         }
         //Native Libs
         else if(cNode.getNodeName().equals("native-libs")) {
            processNativeLibsNode(contextURL, (Element)cNode);
         }
         //Service Properties
         else if(cNode.getNodeName().equals("properties")) {
            processPropertiesNode(contextURL, (Element)cNode);
         }
      }
      
      if(fullParse) {
         xmlLoaded = true;
      }
   }
   
   /**
    * Get Properties associated with this Service
    */
   public Properties getProperties() {
      return properties;
   }
         
   /**
    * Set to true if the Service Class loader should cache the code
    */
   public void cacheServiceCode(boolean useCache) {
      useCache_flag = useCache;
   }
}
