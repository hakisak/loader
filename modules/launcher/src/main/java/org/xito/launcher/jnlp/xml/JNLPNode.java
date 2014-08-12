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
import java.net.*;
import java.text.*;
import org.w3c.dom.*;

import org.xito.launcher.Resources;

/**
 *
 * @author DRICHAN
 */
public class JNLPNode extends AbstractNode {
   
   public final static String NAME = "jnlp";
   
   public final static String SPEC_ATTR = "spec";
   public final static String CODEBASE_ATTR = "codebase";
   
   public final static int TYPE_APP = 0;
   public final static int TYPE_APPLET = 1;
   public final static int TYPE_COMP = 2;
   public final static int TYPE_INSTALLER = 3;
   
   private String spec;
   private String codebase;
   private URL codebaseURL;
   
   private ArrayList infoNodes = new ArrayList();
   private ArrayList resources = new ArrayList();
   private AppDescNode appDescNode;
   private AppletDescNode appletDescNode;
   private InstallerDescNode installerDescNode;
   private SecurityNode securityNode;
   private int type;
   
   /** Creates a new instance of JNLPNode */
   public JNLPNode(URL contextURL, Element node) throws InvalidJNLPException {
      if(!node.getNodeName().equals(NAME)) {
         String msg = Resources.jnlpBundle.getString("jnlp.xml.bad.element");
         throw new InvalidJNLPException(MessageFormat.format(msg, NAME));
      }
      
      //get spec
      spec = node.getAttribute(SPEC_ATTR);
      if(spec == null) {
         spec = "1.0+";
      }
      
      //get codeBase
      codebase = node.getAttribute(CODEBASE_ATTR);
      if(codebase == null || codebase.equals("")) {
         String msg = Resources.jnlpBundle.getString("jnlp.codebase.invalid");
         throw new InvalidJNLPException(MessageFormat.format(msg, codebase));
      }
      if(!codebase.endsWith("/")) {
         codebase = codebase + "/";
      }
      try {
         codebaseURL = new URL(codebase);
      }
      catch(MalformedURLException badURL) {
         String msg = Resources.jnlpBundle.getString("jnlp.codebase.invalid");
         throw new InvalidJNLPException(MessageFormat.format(msg, codebase));
      }
      
      processChildren(node);
   }
   
   /**
    * Process the Children Nodes
    */
   private void processChildren(Element node) throws InvalidJNLPException {
      
      //Get the Children
      NodeList nodes = node.getChildNodes();
      for(int i=0;i<nodes.getLength();i++) {
         Node cNode = nodes.item(i);
         String nodeName = cNode.getNodeName();
         
         //skip text nodes
         if(cNode.getNodeType() == Node.TEXT_NODE) continue;
         
         //Information
         if(cNode.getNodeName().equals(InformationNode.NAME)) {
            processInfoNode((Element)cNode);
         }
         
         //Resources
         if(cNode.getNodeName().equals(ResourcesNode.NAME)) {
            processResources((Element)cNode);
         }
         
         //App Desc
         if(cNode.getNodeName().equals(AppDescNode.NAME)) {
            appDescNode = new AppDescNode(codebaseURL, (Element)cNode); 
            type = TYPE_APP;
         }
         
         //Applet Desc
         if(cNode.getNodeName().equals(AppletDescNode.NAME)) {
            appletDescNode = new AppletDescNode(codebaseURL, (Element)cNode); 
            type = TYPE_APPLET;
         }
         
         //Comp Desc
         if(cNode.getNodeName().equals("component-desc")) {
            type = TYPE_COMP;
         }
         
         //Installer Desc
         if(cNode.getNodeName().equals(InstallerDescNode.NAME)) {
            type = TYPE_INSTALLER;
         }
         
         //Security
         if(cNode.getNodeName().equals(SecurityNode.NAME)) {
            securityNode = new SecurityNode(codebaseURL, (Element)cNode);
         }
      }
   }
   
   /**
    * Process an Information Element
    */
   private void processInfoNode(Element node) throws InvalidJNLPException {
      InformationNode infoNode = new InformationNode(codebaseURL, node);
      infoNodes.add(infoNode);
   }
   
   /**
    * Process a Resources Element
    */
   private void processResources(Element node) throws InvalidJNLPException {
      ResourcesNode resourcesNode = new ResourcesNode(codebaseURL, node);
      resources.add(resourcesNode);
   }
   
   /**
    * Get a List of ResourcesNode Objects 
    */
   public List getResources() {
      return resources;
   }
   
   /**
    * Get a List of Resources for this Environment, OS, Arch, Locale etc.
    * This is a List of JarNode, NativeLibNode, and ExtNode Objects
    */
   public List getResourcesForLocalEnv() {
      
      ArrayList list = new ArrayList();
      if(resources == null) return list;
            
      Iterator it = getResources().iterator();
      while(it.hasNext()) {
         ResourcesNode r = (ResourcesNode)it.next();
         if(r.isLocalEnvResource()) {
            list.addAll(r.getAllResources());
         }
      }
      
      return list;    
   }
   
   /**
    * Get a List of Eager Resources for this Environment, OS, Arch, Locale etc.
    * This is a List of JarNode, NativeLibNode, and ExtNode Objects that have been flagged as Eager or not specified
    */
   public List getEagerResourcesForLocalEnv() {
      
      ArrayList list = new ArrayList();
      if(resources == null) return list;
            
      Iterator it = getResources().iterator();
      while(it.hasNext()) {
         ResourcesNode r = (ResourcesNode)it.next();
         if(r.isLocalEnvResource()) {
            list.addAll(r.getEagerResources());
         }
      }
      
      return list;    
   }
      
   /**
    * Get All Resources for a particular Part. 
    * This is a List of JarNode, NativeLibNode, and ExtNode Objects
    */
   public List getResourcesForPart(String part) {
            
      ArrayList list = new ArrayList();
      if(resources == null) return list;
            
      Iterator it = getResources().iterator();
      while(it.hasNext()) {
         ResourcesNode r = (ResourcesNode)it.next();
         list.addAll(r.getResourcesForPart(part));
      }
      
      return list;    
   }
   
   /**
    * Get the CodeBas URL
    */
   public URL getCodeBaseURL() {
      return this.codebaseURL;
   }
   
   /**
    * Get this Apps AppDescNode or null if an Applet or Extension
    */
   public AppDescNode getAppDescNode() {
      return this.appDescNode;
   }
   
   /**
    * Get the Security Node of this Application
    */
   public SecurityNode getSecurityNode() {
      return securityNode;
   }
   
   /**
    * Get the Type of JNLPFile App, Applet or Ext
    */
   public int getType() {
      return type;
   }
   
   /**
    * Get the Title for this JNLP File
    */
   public String getTitle() {
      if(infoNodes == null) return null;
      
      Iterator it = infoNodes.iterator();
      while(it.hasNext()) {
         InformationNode n = (InformationNode)it.next();
         return n.getTitle();
      }
      
      return null;
   }
}


