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

package org.xito.launcher.applet;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.logging.*;
import org.xito.boot.*;
import org.xito.launcher.*;

/**
 *
 * @author Deane
 */
public class AppletDesc extends AppDesc implements Serializable, LaunchDesc {

   private static Logger logger = Logger.getLogger(AppletDesc.class.getName());
   
   private URL codeBaseURL;
   private URL documentBaseURL;
   private URL documentURL;
   private String appletClass;
   private int width;
   private int height;
   private ArrayList archives;
   private Map parameters;
   private boolean useCustomConfig_flag = false;
   private boolean resizable_flag = false;
   private boolean useWebBrowser_flag = false;
   private String externalType = "applet";
   protected String unique_id = null;
   
   /** Creates a new instance of AppletDesc */
   public AppletDesc() {
      setUniqueID(""+hashCode()+"-"+System.currentTimeMillis());
      parameters = new HashMap();
   }
   
   /** Creates a new instance of AppletDesc */
   public AppletDesc(String name, String displayName) {
      super(name, displayName);
      setUniqueID(""+hashCode()+"-"+System.currentTimeMillis());
      parameters = new HashMap();
   }

   @Override
   public String getDisplayExecutableType() {
      return "Applet";
   }

   /**
    * Get the Unique ID of this Desc
    */
   public String getUniqueID() {
      return unique_id;
   }
   
   /**
    * Set the Unique ID of this Desc
    */
   public void setUniqueID(String id) {
      unique_id = id;
   }
   
   /**
    * Get the External Type for this Descriptor
    */
   public String getExternalType() {
      return externalType;
   }
   
   /**
    * Read in File Data and for this Desc. This is overrides the method in org.xito.boot.AppDesc so that
    * External Launch files can be read correctly by this classes class loader
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
    * Set True if this Applet should be resizable. Only works correctly for applets that are 
    * designed to be resizable
    */
   public void setResizable(boolean b) {
      resizable_flag = b;
   }
   
   /**
    * Return true if the Applet should be resizable
    */
   public boolean isResizable() {
      return resizable_flag;
   }
   
   /**
    * Return true if the applet address should be opened in a Browser
    */
   public boolean useWebBrowser() {
      return useWebBrowser_flag;
   }
   
   /**
    * Set to true if the applet should be opened in a browser
    */
   public void setUseWebBrowser(boolean b) {
      useWebBrowser_flag = b;
   }
   
   /**
    * Update this applet info from info supplied by another AppletDesc
    */
   protected void updateInfo(AppletDesc desc) {
      
      this.setCodeBaseURL(desc.getCodeBaseURL());
      this.setAppletClass(desc.getAppletClass());
      this.setWidth(desc.getWidth());
      this.setHeight(desc.getHeight());
      this.setParameters(desc.getParameters());
      this.setArchives(desc.getArchives());
      this.setResizable(desc.isResizable());
   }
   
   public void setName(String name) {
      super.setName(name);
   }
   
   public String getName() {
      return super.name;
   }
      
   public void setTitle(String title) {
      this.setDisplayName(title);
   }
   
   public String getTitle() {
      return this.displayName;
   }
      
   public String toString() {
      if(displayName != null && !displayName.equals("")) 
         return displayName;
      
      return super.name;
   }
   
   /**
    * Return true if the launcher should use a Custom Config to launch the applet 
    * rather then the desc specified in the HTML
    */
   public boolean useCustomConfig() {
      return useCustomConfig_flag;
   }
   
   /**
    * Set to true if the launcher should use a Custom Config to launch the applet 
    * rather then the desc specified in the HTML
    */
   public void setUseCustomConfig(boolean b) {
      useCustomConfig_flag = b;
   }
   
   /**
    * Getter for property height.
    * @return Value of property height.
    */
   public int getHeight() {
      return height;
   }
   
   /**
    * Setter for property height.
    * @param height New value of property height.
    */
   public void setHeight(int height) {
      this.height = height;
   }
   
   /**
    * Getter for property width.
    * @return Value of property width.
    */
   public int getWidth() {
      return width;
   }
   
   /**
    * Setter for property width.
    * @param width New value of property width.
    */
   public void setWidth(int width) {
      this.width = width;
   }
   
   /**
    * Getter for property appletClass.
    * @return Value of property appletClass.
    */
   public java.lang.String getAppletClass() {
      return appletClass;
   }
   
   /**
    * Setter for property appletClass.
    * @param appletClass New value of property appletClass.
    */
   public void setAppletClass(java.lang.String appletClass) {
          
      if(appletClass.endsWith(".class")) appletClass = appletClass.substring(0, appletClass.length()-6);
      
      this.appletClass = appletClass.replace('/','.');
   }
   
   /**
    * Set the Archives
    */
   public void setArchives(List archives) {
      this.archives = new ArrayList(archives);
   }
   
   /**
    * Get the Archives
    */
   public List getArchives() {
      return this.archives;
   }
   
   /**
    * Get ClassPath URLs
    */
   public ClassPathEntry[] getClassPath() {
      classpath.clear();
      addClassPathEntry(new ClassPathEntry(getCodeBaseURL()));
      
      //Add each Archive
      if(archives == null) {
         return (ClassPathEntry[])classpath.toArray(new ClassPathEntry[1]);
      }
                  
      Iterator it = archives.iterator();
      while(it.hasNext()) {
         String archive = (String)it.next();
         try {
            addClassPathEntry(new ClassPathEntry(new URL(getCodeBaseURL(), archive)));
         }
         catch(MalformedURLException badURL) {
            logger.log(Level.WARNING, "Invalid Archive URL:"+archive+" "+badURL.getMessage(), badURL);
         }
      }
      
      return (ClassPathEntry[])classpath.toArray(new ClassPathEntry[classpath.size()]);
   }
   
   /**
    * Set Parameters
    */
   public void setParameters(Map params) {
      parameters = params;
   }
   
   /**
    * Get the Applet Parameters
    */
   public Map getParameters() {
      return (Map)parameters;
   }
   
   /**
    * Getter for property documentURL.
    * @return Value of property documentURL.
    */
   public java.net.URL getDocumentURL() {
      return documentURL;
   }
   
   /**
    * Setter for property documentURL.
    * @param documentURL New value of property documentURL.
    */
   public void setDocumentURL(java.net.URL documentURL) {
      this.documentURL = documentURL;
      String url = documentURL.toString();
      try {
         this.documentBaseURL = new URL(url.substring(0,url.lastIndexOf('/')+1));
      }
      catch(MalformedURLException badURL) {
         //Can ignore this
         badURL.printStackTrace();
      }
   }
   
   /**
    * Get the DocumentBase URL
    */
   public URL getDocumentBaseURL() {
      return documentBaseURL;
   }
   
   /**
    * Get the DocumentBase URL
    */
   public URL getCodeBaseURL() {
      if(codeBaseURL == null)
         return documentBaseURL;
      else
         return codeBaseURL;
   }
   
   /**
    * Set the code base URL
    */
   public void setCodeBaseURL(URL codeBase) {
      
      if(codeBase == null)
         this.codeBaseURL = null;
      
      if(codeBase.toString().endsWith("/"))
         this.codeBaseURL = codeBase;
      else {
         try {
            String cbStr = codeBase.toString();
            this.codeBaseURL = new URL(cbStr + "/");
         }
         catch(MalformedURLException badURL) {
            //shouldn't happen but will just use passed url if it does
            badURL.printStackTrace();
            this.codeBaseURL = codeBase;
         }
      }
   }
   
   /**
    * Set the First ClassLoader created with this Description
    */
   public synchronized AppletClassLoader getNewAppletClassLoader(ClassLoader parent) {
      try {
         if(shareClassLoader) {
            if(firstAppClassLoader == null)
               firstAppClassLoader = new AppletClassLoader(this, parent);

            return (AppletClassLoader)firstAppClassLoader;
         }
         else {
            return new AppletClassLoader(this, parent);
         }
      }
      catch(ServiceNotFoundException serviceNotFound) {
         logger.log(Level.SEVERE, serviceNotFound.getMessage(), serviceNotFound);
      }
      
      return null;
   }
   
}
