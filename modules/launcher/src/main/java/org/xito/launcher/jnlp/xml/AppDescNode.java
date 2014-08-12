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

import java.text.*;
import java.util.*;
import java.net.*;
import org.w3c.dom.*;
import org.xito.launcher.Resources;

/**
 *
 * @author DRICHAN
 */
public class AppDescNode extends AbstractNode {
   
   public static final String NAME = "application-desc";
   
   private String mainClass;
   private ArrayList args = new ArrayList();
   
   /** Creates a new instance of AppDescNode */
   public AppDescNode(URL codebaseURL, Element node) throws InvalidJNLPException {
      if(!node.getNodeName().equals(NAME)) {
         String msg = Resources.jnlpBundle.getString("jnlp.xml.bad.element");
         throw new InvalidJNLPException(MessageFormat.format(msg, NAME));
      }
      
      mainClass = node.getAttribute("main-class");
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
         
         //argument
         if(cNode.getNodeName().equals("argument")) {
            String arg = getText(cNode);
            if(arg != null) args.add(arg);
         }
      }
   }
   
   /**
    * Get Arguments
    */
   public String[] getArgs() {
      return (String[]) args.toArray(new String[args.size()]);
   }
   
   /**
    * Get the Applications main Class
    */
   public String getMainClass() {
      return mainClass;
   }
}
