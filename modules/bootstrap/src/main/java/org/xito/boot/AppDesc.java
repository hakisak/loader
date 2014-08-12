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
import java.text.MessageFormat;
import java.util.*;
import java.io.*;
import java.security.*;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Describes an Application Executable
 *
 * @author  Deane Richan
 */
public class AppDesc extends ExecutableDesc implements Serializable {
   
   protected boolean seperateVM = false;
   protected boolean shareClassLoader = false;
   private String[] mainArgs;
   private boolean appContext = true;
   protected AppClassLoader firstAppClassLoader;
   private String externalType = "app";
   private URL appDescURL;
 
   //For use by Subclasses
   protected AppDesc() {}
   
  /** 
    * Creates a new instance of AppDesc 
    * @param name of application
    * @param displayName name of application
    */
   public AppDesc(String name, String displayName) {
      
      this(name, displayName, true);
   }   
      
   /** 
    * Creates a new instance of AppDesc 
    * @param name of application
    * @param displayName name of application
    * @param useCache
    */
   public AppDesc(String name, String displayName, boolean useCache) {
      
      this.name = name;
      this.displayName = displayName;
      setUseCache(useCache);      
      permissions = null;
   }
   
   /**
    * Create a new instance of AppDesc
    * @param appDescURL URL of AppDesc XML file
    */
   public AppDesc(URL appDescURL) {
      setAppDescURL(appDescURL);
   }

   @Override
   public String getDisplayExecutableType() {
      return "Application";
   }

   /**
    * Set the URL this AppDesc should use to obtain an app desc xml file.
    * This xml file will define the properties and settings to launch an application
    * 
    * @param appDescURL
    */
   public void setAppDescURL(URL appDescURL) {
      this.appDescURL = appDescURL;
   }
   
   protected void processAppDescURL() {
      
      //if no desc URL then just return
      if(appDescURL == null)
         return;
      
      //Parse the file and setup the AppDesc
      try {     
         initParser();
         Document doc = builder.parse(appDescURL.openStream());
         Element mainElement = doc.getDocumentElement();
         if(mainElement.getNodeName().equals("application")) {
            processMainNode(appDescURL, doc.getDocumentElement());
         }
         else {
            throw new RuntimeException("URL does not describe an Application Resource");
         }
      }
      catch(SAXException exp) {
         String msg = MessageFormat.format(Resources.bundle.getString("appdesc.invalid.error"), exp.getMessage());
         throw new RuntimeException(msg, exp);
      }
      catch(IOException exp) {
         String msg = MessageFormat.format(Resources.bundle.getString("appdesc.invalid.error"), exp.getMessage());
         throw new RuntimeException(msg, exp);
      }
   }
   
   /**
    * Process a custom desc xml element. Subclasses should override
    * @param element
    */
   protected void processCustomElement(Element element) throws DOMException {
      
      //main-cls
      if(element.getNodeName().equals("main-cls")) {
         if(element.getFirstChild()!=null)
            this.mainClass = element.getFirstChild().getNodeValue();
      }
      //newAppContext
      else if(element.getNodeName().equals("newAppContext")) {
         if(element.getFirstChild()!=null) {
            if(element.getFirstChild().getNodeValue() != null && element.getFirstChild().getNodeValue().equals("false")) {
               setNewAppContext(false);
            }
            else {
               setNewAppContext(true);
            }
         }
      }
      //seperateVM
      else if(element.getNodeName().equals("seperateVM")) {
         if(element.getFirstChild()!=null) {
            if(element.getFirstChild().getNodeValue() != null && element.getFirstChild().getNodeValue().equals("true")) {
               setSeperateVM(true);
            }
            else {
               setSeperateVM(false);
            }
         }
      }
      //security
      else if(element.getNodeName().equals("security")) {
         if(element.getFirstChild()!=null) {
            if(element.getFirstChild().getNodeValue() != null && element.getFirstChild().getNodeValue().equals("all-permission")) {
               this.setPermissions(getAllPermissions());
            }
            else {
               this.setPermissions(null);
            }
         }
      }
   }
   
   /**
    * Get the URL used for this AppDesc or null if the AppDesc was not specified by
    * an XML file from a URL
    * @return
    */
   public URL getAppDescURL() {
      return appDescURL;
   }
   
   /**
    * Read in File Data and return an AppDesc of this type. Implementations can use
    * Java serialization or another format they prefer. 
    * This implementation uses Java Serialization
    */
   public static AppDesc readFileData(byte[] data) throws IOException, ClassNotFoundException {
   
      ByteArrayInputStream in = new ByteArrayInputStream(data);
      ObjectInputStream objIn = new ObjectInputStream(in);
      Object obj = objIn.readObject();
      
      try {
         return (AppDesc)obj;
      }
      catch(ClassCastException badCast) {
         throw new IOException(badCast.getMessage());
      }
   }
   
   /**
    * Write out File Data. Implementations can use
    * Java serialization or another format they prefer. 
    * This implementation uses Java Serialization
    */
   public byte[] getFileData() throws IOException {
      
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream objOut = new ObjectOutputStream(out);
      objOut.writeObject(this);
      out.flush();
      
      byte[] data  = out.toByteArray();
      out.close();
      
      return data;
   }
   
   /**
    * Get the External Type for this Descriptor
    */
   public String getExternalType() {
      return externalType;
   }
   
   /**
    * Set the First ClassLoader created with this Description
    */
   public AppClassLoader getNewAppClassLoader(ClassLoader parent) throws ServiceNotFoundException {
      
      if(parent == null) parent = this.getClass().getClassLoader();
      
      if(shareClassLoader) {
         if(firstAppClassLoader == null) {
            firstAppClassLoader = new AppClassLoader(this, parent);
         }

         return firstAppClassLoader;
      }
      else {
         return new AppClassLoader(this, parent);
      }
   }
   
   /**
    * Set the Args that should be passed to the application
    */
   public void setMainArgs(String[] args) {
      mainArgs = args;
   }
   
   /**
    * Get the Args that should be passed to the application
    */
   public String[] getMainArgs() {
      return mainArgs;
   }
   
   /**
    * Set the Main class of the application
    */
   public void setMainClass(String mainClass) {
      this.mainClass = mainClass;
   }
   
   /**
    * Get the Main class of the application
    */
   public String getMainClass() {
      return mainClass;
   }
   
   /**
    * Set this to true if the application should use a new
    * AppContext as part of Sun's Event Queue. This defaults to true. and cannot be
    * set to false unless the app has All Permissions
    */
   public void setNewAppContext(boolean newContext) {
   
      if(getPermissions() != null && getPermissions().implies(new AllPermission())) {
         appContext = newContext;
      }
   }
   
   /**
    * Return true if the app should use a new Context. 
    */
   public boolean useNewAppContext() {
      return appContext;
   }
   
   /**
    * Set to true if this App Desc should use the same AppClassLoader for
    * all app Instances. Defaults to false
    */
   public void setUseSharedClassLoader(boolean shared) {
      shareClassLoader = shared;
   }
   
   /**
    * Return true if this Application should always use the
    * same classloader for each app instance
    */
   public boolean useSharedClassLoader() {
      return shareClassLoader;
   }
   
   /**
    * Set to true if this App should be launched in a SeperateVM
    */
   public void setSeperateVM(boolean newVM) {
      seperateVM = newVM;
   }
   
   /**
    * Return true if this application should be launched in a seperate VM
    */
   public boolean useSeperateVM() {
      return seperateVM;
   }
   
}
