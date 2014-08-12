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

package org.xito.launcher.jnlp.xml;

import java.util.*;
import java.text.*;
import java.net.*;
import org.w3c.dom.*;
import org.xito.launcher.Resources;

/**
 *
 * @author Deane Richan
 */
public class ResourcesNode extends AbstractNode {
   
   public static final String NAME = "resources";
   
   private String os;
   private String arch;
   private String locale;
   
   private Properties props = new Properties();
   private ArrayList jars = new ArrayList();
   private ArrayList nativeLibs = new ArrayList();
   private ArrayList extensions = new ArrayList();
   
   /** Creates a new instance of InformationNode */
   public ResourcesNode(URL codebaseURL, Element node) throws InvalidJNLPException {
      if(!node.getNodeName().equals(NAME)) {
         String msg = Resources.jnlpBundle.getString("jnlp.xml.bad.element");
         throw new InvalidJNLPException(MessageFormat.format(msg, NAME));
      }
      
      os = node.getAttribute("os");
      arch = node.getAttribute("arch");
      locale = node.getAttribute("locale");
    
      processChildren(codebaseURL, node);
   }
   
   public String getOS() {
      return os;
   }
   
   public String getArch() {
      return arch;
   }
   
   public String getLocale() {
      return locale;
   }
   
   /**
    * Return true if this resource's os, arch, and local match this local machine. If os, arch
    * and locale aren't specified in the jnlp then they are assumed to be required
    */
   public boolean isLocalEnvResource() {
            
      //Check OS
      //TODO add support for multiple os names in OS JNLP field as per JNLP Spec
      String osName = System.getProperty("os.name");
      if(os != null  && !osName.startsWith(os)) {
         return false;
      }
      
      //Check Arch
      String osArch = System.getProperty("os.arch");
      if(arch != null  && !osArch.startsWith(arch)) {
         return false;
      }
      
      //Check Locale
      //TODO add support for locale resources
      //skipped for now
      
      return true;
   }
   
   /**
    * Process the Children Nodes
    */
   private void processChildren(URL codebaseURL, Element node) throws InvalidJNLPException {
      
      //Get the Children
      NodeList nodes = node.getChildNodes();
      for(int i=0;i<nodes.getLength();i++) {
         Node cNode = nodes.item(i);
         String nodeName = cNode.getNodeName();
         
         //skip text nodes
         if(cNode.getNodeType() == Node.TEXT_NODE) continue;
         
         //property
         if(cNode.getNodeName().equals("property")) {
            String pName = ((Element)cNode).getAttribute("name");
            String pValue = ((Element)cNode).getAttribute("value");
            if(pName != null && pValue != null)
               props.put(pName, pValue);
         }
         
         //jar
         if(cNode.getNodeName().equals(JarNode.NAME)) {
            processJar(codebaseURL, (Element)cNode);
         }
         
         //nativelib
         if(cNode.getNodeName().equals(NativeLibNode.NAME)) {
            processNativeLib(codebaseURL, (Element)cNode);
         }
         
         //extension
         if(cNode.getNodeName().equals(ExtNode.NAME)) {
            processExt(codebaseURL, (Element)cNode);
         }
      }
   }
   
   /**
    * Process an Jar Element
    */
   private void processJar(URL codebaseURL, Element node) throws InvalidJNLPException {
      
      JarNode jarNode = new JarNode(codebaseURL, node);
      jars.add(jarNode);
   }
   
   /**
    * Process a Native Lib Element
    */
   private void processNativeLib(URL codebaseURL, Element node) throws InvalidJNLPException  {
      
      NativeLibNode libNode = new NativeLibNode(codebaseURL, node);
      nativeLibs.add(libNode);
   }
   
   /**
    * Process a Ext Element
    */
   private void processExt(URL codebaseURL, Element node) throws InvalidJNLPException {
      
      ExtNode extNode = new ExtNode(codebaseURL, node);
      extensions.add(extNode);
   }
   
   public List getJars() {
      return jars;
   }
   
   public List getNativeLibs() {
      return nativeLibs;
   }
   
   public List getExtensions() {
      return extensions;
   }
   
   /**
    * Get a List of Resources for this Local Environment OS, Arch, Local etc
    * This is a List of JarNode, NativeLibNode, and ExtNode Objects
    */
   public List getEagerResources() {
      ArrayList all = new ArrayList();
      
      //Jars
      Iterator it = jars.iterator();
      while(it.hasNext()) {
         JarNode jar = (JarNode)it.next();
         if(jar.isLazyDownload() == false) all.add(jar);
      }
      
      //NativeLib
      it = nativeLibs.iterator();
      while(it.hasNext()) {
         NativeLibNode lib = (NativeLibNode)it.next();
         if(lib.isLazyDownload() == false) all.add(lib);
      }     
      
      //Extension
      it = extensions.iterator();
      while(it.hasNext()) {
         ExtNode ext = (ExtNode)it.next();
         if(ext.isLazyDownload() == false) all.add(ext);
      }     
            
      return all;
   }
   
   /**
    * Get a List of Resources for a specific Part. 
    * This is a List of JarNode, NativeLibNode, and ExtNode Objects
    */
   public List getResourcesForPart(String partName) {
      ArrayList all = new ArrayList();
            
      //Jars
      Iterator it = jars.iterator();
      while(it.hasNext()) {
         JarNode jar = (JarNode)it.next();
         if(jar.getPart().equals(partName))
            all.add(jar);
      }
      
      //NativeLib
      it = nativeLibs.iterator();
      while(it.hasNext()) {
         NativeLibNode lib = (NativeLibNode)it.next();
         if(lib.getPart().equals(partName))
            all.add(lib);
      }     
      
      //Extension
      it = extensions.iterator();
      while(it.hasNext()) {
         ExtNode ext = (ExtNode)it.next();
         if(ext.getPart().equals(partName))
            all.add(ext);
      }     
            
      return all;
   }
   
   /**
    * Get a List of all Resources this is a List of JarNode, NativeLibNode, and ExtNode Objects
    */
   public List getAllResources() {
      ArrayList all = new ArrayList();
      all.addAll(jars);
      all.addAll(nativeLibs);
      all.addAll(extensions);
      
      return all;
   }
}
