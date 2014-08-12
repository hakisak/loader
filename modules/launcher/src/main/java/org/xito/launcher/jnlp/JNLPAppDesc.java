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

package org.xito.launcher.jnlp;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

import org.xito.boot.*;
import org.xito.launcher.*;
import org.xito.launcher.jnlp.xml.*;

/**
 *
 * @author  Deane Richan
 */
public class JNLPAppDesc extends AppDesc implements LaunchDesc {

   private static Logger logger = Logger.getLogger(JNLPAppDesc.class.getName());
   
   private URL jnlpAddress;
   private boolean useWebStart_flag = false;
   
   private String externalType = "jnlp";
   private transient JNLPNode jnlpNode;
   protected String unique_id = null;
   
   /**
    * Creates a new instance of JNLPAppDesc
    */
   public JNLPAppDesc() { 
      setUniqueID(""+hashCode()+"-"+System.currentTimeMillis());
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
   
   public void initializeJNLPInfo() throws IOException, InvalidJNLPException {
      if(this.getJNLPAddress() == null)
         return;
      
      CachePolicy cachePolicy = CachePolicy.ALWAYS;
      CacheManager cm = Boot.getCacheManager();
      URL cachedJNLPURL = cm.getResource(this.getJNLPAddress(), cm.getDefaultListener(), cachePolicy);
      JNLPParser parser = new JNLPParser();
      JNLPNode jnlpNode = parser.parse(cachedJNLPURL);
      setName(jnlpNode.getTitle());
      setTitle(jnlpNode.getTitle());
      
      //Now process Icons
      
   }
   
   /**
    * Get the Jar Resources from this list of Resources
    * This could cause a recursive call if the resource is an Ext Node.
    * The List returned contains ClassPathEntry objects for all Jar Resources
    */
   protected List getJarResources(List resourceNodes) {
      
      ArrayList jarResources = new ArrayList();
      
      //Setup
      Iterator resources = resourceNodes.iterator();
      while(resources.hasNext()) {
         AbstractResourceNode r = (AbstractResourceNode)resources.next();
         if(r instanceof ExtNode) {
            JNLPNode extJNLPNode = ((ExtNode)r).getExtJNLPNode();
            if(extJNLPNode == null) logger.warning("Can't get resources for JNLP Extension:"+r.getURL());
            
            jarResources.addAll(getJarResources(extJNLPNode.getResourcesForLocalEnv()));
         }
         else if (r instanceof JarNode){
            JarNode jarResource = (JarNode)r;
            ClassPathEntry e = new ClassPathEntry(jarResource.getURL());
            if(jarResource.isLazyDownload()) {
               e.setDownloadType(ClassPathEntry.LAZY);
            }
            if(jarResource.isMainJar()) {
               e.setMainJar(true);
            }
            
            e.setPart(jarResource.getPart());
            jarResources.add(e);
         }
      }
      
      return jarResources;
   }

   /**
    * Get the ClassPath for this Application
    */
   public ClassPathEntry[] getClassPath() {

      List jarResources = getJarResources(jnlpNode.getResourcesForLocalEnv());
      return (ClassPathEntry[])jarResources.toArray(new ClassPathEntry[jarResources.size()]);
   }
   
   /**
    * Set the JNLPNode Object for this Descrition this is set by the launcher after parsing
    * a JNLPFile. A JNLPNode contains all the info from the JNLP File
    */
   protected void setJNLPNode(JNLPNode node) {
      jnlpNode = node;
      URLClassLoader loader;
      //Setup Security
      SecurityNode sn = jnlpNode.getSecurityNode();
      if(sn != null) {
         if(sn.getPermissionType() == SecurityNode.ALL_PERM) {
            super.setPermissions(super.getAllPermissions());
         }
         //TODO figure out what J2EE Client Permissions are
         else if(sn.getPermissionType() == SecurityNode.J2EE_CLIENT_PERM) {
            super.setPermissions(super.getAllPermissions());
         }
         else if(sn.getPermissionType() == SecurityNode.RESTRICTED_PERM) {
            super.setPermissions(null);
         }
      }
   }
   
   /**
    * Get the DisplayName of this Application
    */
   public String getDisplayName() {
      if(jnlpNode == null) {
         return getName();
      }
      
      return jnlpNode.getTitle();
   }
   
   /**
    * Get this AppDesc's JNLPNode object or null of the JNLPFile for this App hasn't been parsed yet
    */
   public JNLPNode getJNLPNode() {
      return jnlpNode;
   }
   
   /**
    * Return the main class for the JNLP App as described in the JNLP File
    */
   public String getMainClass() {
      if(jnlpNode == null) return null;
      if(jnlpNode.getType() == JNLPNode.TYPE_APP) {
         String mainClass = jnlpNode.getAppDescNode().getMainClass();
         if(mainClass != null && !mainClass.equals("")) {
            return mainClass;
         }
      }
      
      //not an App
      return null;
   }
   
   /**
    * Get Main Arguments from JNLP File
    */
   public String[] getMainArgs() {
      if(jnlpNode == null) return null;
      if(jnlpNode.getType() == JNLPNode.TYPE_APP) {
         return jnlpNode.getAppDescNode().getArgs();
      }
      else {
         return new String[0];
      }
      
   }
   
   /**
    * Get a Classloader for this AppDesc
    */
   public AppClassLoader getNewAppClassLoader(ClassLoader parent) {
      
      try {
         if(parent == null) parent = JNLPClassLoader.class.getClassLoader();

         if(shareClassLoader) {
            if(firstAppClassLoader == null) {
               firstAppClassLoader = new JNLPClassLoader(this, jnlpNode, parent);
            }

            return firstAppClassLoader;
         }
         else {
            return new JNLPClassLoader(this, jnlpNode, parent);
         }
      }
      catch(ServiceNotFoundException serviceNotFound) {
         logger.log(Level.SEVERE, serviceNotFound.getMessage(), serviceNotFound);
      }
      
      return null;
   }
   
   /**
    * Set the Address of the JNLP File
    */
   public void setJNLPAddress(URL u) {
      jnlpAddress = u;
   }
   
   /**
    * Get the Address of the JNLP File
    */
   public URL getJNLPAddress() {
      return jnlpAddress;
   }
      
   /**
    * Get the External Type for this Descriptor
    */
   public String getExternalType() {
      return this.externalType;
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
   
   /**
    * Return true if the App should be launched by WebStart
    */
   public boolean useWebStart() {
      return useWebStart_flag;
   }
   
   /**
    * Set to true if the JNLP app should be launched by WebStart in a new VM
    */
   public void setUseWebStart(boolean b) {
      useWebStart_flag = b;
   }
   
}
