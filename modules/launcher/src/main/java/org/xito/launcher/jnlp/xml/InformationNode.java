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
 * @author DRICHAN
 */
public class InformationNode extends AbstractNode {
   
   public static final String NAME = "information";
   
   private String os;
   private String arch;
   private String platform;
   private String locale;
   
   private String title;
   private String vendor;
   private String homepage;
   private HashMap descriptions = new HashMap();
   private HashMap icons = new HashMap();
   
   /** Creates a new instance of InformationNode */
   public InformationNode(URL codebaseURL, Element node) throws InvalidJNLPException {
      if(!node.getNodeName().equals(NAME)) {
         String msg = Resources.jnlpBundle.getString("jnlp.xml.bad.element");
         throw new InvalidJNLPException(MessageFormat.format(msg, NAME));
      }
      
      os = node.getAttribute("os");
      arch = node.getAttribute("arch");
      platform = node.getAttribute("platform");
      locale = node.getAttribute("locale");
    
      processChildren(codebaseURL, node);
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
         
         //title
         if(cNode.getNodeName().equals("title")) {
            title = getText(cNode);
         }
         
         //vendor
         if(cNode.getNodeName().equals("vendor")) {
            vendor = getText(cNode);
         }
         
         //homepage
         if(cNode.getNodeName().equals("homepage")) {
            homepage = getText(cNode);
         }
         
         //description
         if(cNode.getNodeName().equals("description")) {
            processDesc((Element)cNode);
         }
         
         //icon
         if(cNode.getNodeName().equals(IconDescNode.NAME)) {
            processIcon(codebaseURL, (Element)cNode);
         }
      }
   }
   
   /**
    * Process a Desc Element
    */
   private void processDesc(Element node) {
      
      String kind = node.getAttribute("kind");
      String desc = getText(node);
      
      if(kind == null || kind.equals(""))
         kind = "default";
      
      descriptions.put(kind, desc);
   }
   
   /**
    * Process an Icon Element
    */
   private void processIcon(URL codebaseURL, Element node) throws InvalidJNLPException {
      
      IconDescNode iconNode = new IconDescNode(codebaseURL, node);
      icons.put(iconNode.getKind(), iconNode);
   }
   
   /**
    * Get the Title of this Application
    */
   public String getTitle() {
      return title;
   }
}
