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

import java.awt.*;
import java.net.*;
import java.io.*;
import java.beans.*;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.*;
import javax.xml.parsers.*;
import org.xito.boot.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 *
 * @author $Author: drichan $
 * @version $Revision: 1.4 $
 * @since $Date: 2007/09/02 00:43:00 $
 */
public class BLXCompFactory {
   
   private static Logger logger = Logger.getLogger(BLXCompFactory.class.getName());
   private static BLXCompFactory singleton = null;
   private URL defaultCodeBase = null;
      
   /** Creates new CompFactory */
   private BLXCompFactory() {
      //defaultCodeBase = Boot.getBootDir();
   }
   
   /**
    * Get the ComponentFactory
    * @return First Component Factory that Stars
    */
   public static BLXCompFactory getInstance() {
      
      if(singleton == null) singleton = new BLXCompFactory();
      
      return singleton;
   }
   
   /**
    * @param pURL of the BLX Document
    * @throws ClassNotFoundException
    * @throws InstantiationException
    * @throws InvalidBLXXMLException
    * @return
    */
   public Object getObject(URL url) throws ClassNotFoundException, InstantiationException, InvalidBLXXMLException, IOException {
      //Get object using a new AliasManager
      return getObject(url, new BLXAliasManager(null));
   }
   
   /**
    * @param pURL of the BLX Document
    * @throws ClassNotFoundException
    * @throws InstantiationException
    * @throws InvalidBLXXMLException
    * @return
    */
   public Object getObject(URL url, BLXAliasManager aliasManager) throws ClassNotFoundException, InstantiationException, InvalidBLXXMLException, IOException {
      try {
         DocumentBuilder builder = BLXUtility.createDOMParser();
         Document doc = builder.parse(url.toString());
         return getObject(new BLXDocument(doc, url, aliasManager));
      }
      catch(SAXException saxExp) {
         saxExp.printStackTrace();
         throw new IOException(saxExp.getMessage());
      }
   }

   /**
    * Get a Object Instance
    * @param XML Wrapper
    * @return Object
    */
   public Object getObject(BLXDocument blxDoc) throws ClassNotFoundException, InstantiationException {
      try {
         return getObject(blxDoc.getBLXElement(), blxDoc.getAliasManager());
      }
      catch(ClassNotFoundException notFoundExp) {
         StringBuffer msg = new StringBuffer("java.lang.ClassNotFoundException " + notFoundExp.getMessage() + "\n");
         //log all aliases
         Map aliases = blxDoc.getAliasManager().getExtAliases();
         Iterator names = aliases.keySet().iterator();
         while(names.hasNext()) {
            String name = (String)names.next();
            BLXExtAliasElement element = (BLXExtAliasElement)aliases.get(name);
            msg.append(name+":service="+element.getServiceName() + "\n");
            msg.append(name+":ext-url="+element.getExtURL() + "\n");
         }
         
         //log class and alias info
         logger.log(Level.SEVERE, msg.toString(), notFoundExp);
         
         //now throw the exception
         throw notFoundExp;
      }
   }
   
   /**
    * Get a Object Instance
    * @param XML Wrapper
    * @return Object
    */
   public Object getObject(BLXElement blxElement, BLXAliasManager aliasManager) throws ClassNotFoundException, InstantiationException {
      
      //Set the BLXElement on to the BLXObject
      try {
         Object obj = getObject(blxElement.getClassName(), getLoader(blxElement, aliasManager));
         ((BLXObject)obj).setBLXElement(blxElement);
         
         return obj;
      }
      catch(ExtensionLoadException loadExp) {
         throw new ClassNotFoundException("Unable able to load class:"+blxElement.getClassName()+" from Extension.", loadExp);
      }
   }
   
   /**
    * Get a Object Instance
    * @param Class name
    * @param Classloader
    * @return Object
    */
   public Object getObject(String name, ClassLoader loader) throws ClassNotFoundException, InstantiationException {
      
      if(name == null) {
         throw new InstantiationException("Class name not specified.");
      }
      
      //Create the Object
      try {
         Class cls = loader.loadClass(name);
         
         return cls.newInstance();
      }
      catch(ClassNotFoundException notFound) {
         throw notFound;
      }
      catch(Throwable _exp) {
         _exp.printStackTrace();
         throw new InstantiationException(_exp.getMessage());
      }
   }
   
   /**
    * @param pReader input reader of BLX XML Data
    * @throws ClassNotFoundException
    * @throws InstantiationException
    * @throws InvalidBLXXMLException
    * @return  */
   public Object getObject(Reader reader, BLXAliasManager aliasManager) throws ClassNotFoundException, InstantiationException, InvalidBLXXMLException, IOException {
      try {
         DocumentBuilder builder = BLXUtility.createDOMParser();
         Document doc = builder.parse(new InputSource(reader));
         
         return getObject(new BLXDocument(doc, null, aliasManager));
      }
      catch(SAXException saxExp) {
         IOException exp = new IOException();
         exp.initCause(saxExp);
         throw exp;
      }
   }
   
   /**
    * @param pURL of the BLX Document
    * @throws ClassNotFoundException
    * @throws InstantiationException
    * @throws InvalidBLXXMLException
    * @return  */
   public Component getComponent(URL url) throws ClassNotFoundException, InstantiationException, InvalidBLXXMLException, IOException {
      return getComponent(url, new BLXAliasManager(null));
   }
   
   /**
    * @param pURL of the BLX Document
    * @throws ClassNotFoundException
    * @throws InstantiationException
    * @throws InvalidBLXXMLException
    * @return  */
   public Component getComponent(URL url, BLXAliasManager aliasManager) throws ClassNotFoundException, InstantiationException, InvalidBLXXMLException, IOException {
      try {
         Component comp = (Component)getObject(url, aliasManager);
         return comp;
      }
      catch(ClassCastException exp) {
         throw new InstantiationException(exp.getMessage());
      }
   }
   
   /**
    * @param pWrapper
    * @throws ClassNotFoundException
    * @throws InstantiationException
    * @return   */
   public Component getComponent(BLXElement blxElement, BLXAliasManager aliasManager) throws ClassNotFoundException, InstantiationException {
      
      try {
         Component comp = (Component)getObject(blxElement, aliasManager);
         return comp;
      }
      catch(ClassCastException exp) {
         throw new InstantiationException(exp.getMessage());
      }
   }
   
   /**
    * Get a Component Instance
    * @param Class Name
    * @param ClassLoader to Use
    * @return Component
    */
   public Component getComponent(String pName, ClassLoader pLoader) throws ClassNotFoundException, InstantiationException {
      try {
         Component _cmp = (Component)getObject(pName, pLoader);
         return _cmp;
      }
      catch(ClassCastException _exp) {
         throw new InstantiationException(_exp.getMessage());
      }
   }
   
   /**
    * Return a Class Loader described by the BLXElement
    * @param XML OBject Wrapper that describes which Component to create
    * @return Class Loader
    */
   private ClassLoader getLoader(BLXElement blxElement, BLXAliasManager aliasManager) throws ExtensionLoadException {
      
      ClassLoader loader = null;
      
      //First check to see if this object should be loaded from a service
      String serviceName = blxElement.getServiceName();
      if(serviceName != null) {
         try {
            return ServiceClassLoader.getServiceLoader(new ServiceDescStub(serviceName, null, null));
         }
         catch(ServiceNotFoundException noService) {
            throw new ExtensionLoadException(noService.getMessage(), noService);
         }
      }
                 
      //Now get the Extension Specified for this object.
      //Ext Manager will throw Extension Load exception if the extension cannot be found etc.
      BLXExtension ext = BLXExtManager.getInstance().getExtension(blxElement, aliasManager);
      loader = ext.getExtClassLoader();
      
      if(loader == null) {
         logger.warning("ClassLoader not defined in Extension Definition:"+ext.getName());
         loader = this.getClass().getClassLoader();
      }
      
      return loader;
   }
   
   /**
    * Get the URL of the BLX Extension
    * @param blxElement
    * @return URL
    */
   /*
   private URL getExtensionURL(BLXElement blxElement) throws MalformedURLException {
      
      //Get the HREF of the Extension
      String extHREF = blxElement.getExtHREF();
      
      if(extHREF == null || extHREF.length() == 0) {
         return null;
      }

      //Make the URL relative to the CodeBase
      URL codeBase = blxElement.getContextURL();
      if(codeBase != null) {
         return new URL(codeBase, extHREF);
      }

      return new URL(defaultCodeBase, extHREF);
   }
    **/
   
   /**
    * Get a BLX DOM Document from a BLXObject.
    *
    * This method will call the BLXObject's getBLXElement and getXMLData methods
    * and wrap the return elements in a BLX style Document object.
    * @param pBLXObject blx Object to obtain the Document for
    * @return dom Document
    */
   public Document getBLXDocument(BLXObject pBLXObject) {
      
      Document document = BLXUtility.createBLXDocument();
      
      Element blx = (Element)document.importNode(pBLXObject.getBLXElement().getDOMElement(), true);
      Element data = pBLXObject.getDataElement();
      if(data != null) {
         data = (Element)document.importNode(data, true);
         blx.appendChild(data);
      }
      
      document.getDocumentElement().appendChild(blx);
      
      return document;
   }
   
   /**
    * Copy a BLX Object
    */
   public BLXObject copy(BLXObject source) throws ClassNotFoundException, InstantiationException {
      
      BLXElement blxE = source.getBLXElement();
      Element data = source.getDataElement();
      blxE.setDataElement(data);
      BLXObject dest = (BLXObject)getObject(blxE, null);
      dest.setBLXElement(blxE);
      
      return dest;
   }
}
