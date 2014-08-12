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
 * @author Deane Richan
 */
public class AppletDescNode extends AbstractNode {
   
   public static final String NAME = "applet-desc";
   
   private int width;
   private int height;
   private String mainClass;
   private String documentbase;
   private URL docBaseURL;
   private String name;
   private HashMap params = new HashMap();
   
   /** Creates a new instance of AppDescNode */
   public AppletDescNode(URL codebaseURL, Element node) throws InvalidJNLPException {
      if(!node.getNodeName().equals(NAME)) {
         String msg = Resources.jnlpBundle.getString("jnlp.xml.bad.element");
         throw new InvalidJNLPException(MessageFormat.format(msg, NAME));
      }
      
      mainClass = node.getAttribute("main-class");
      documentbase = node.getAttribute("documentbase");
      name = node.getAttribute("name");
      
      try {
         String w = node.getAttribute("width");
         if(w != null || !w.equals(""))
            width = Integer.parseInt(w);
      }
      catch(NumberFormatException badNum) {
         width = -1;
      }
      
      try {
         String h = node.getAttribute("height");
         if(h != null || !h.equals(""))
            height = Integer.parseInt(h);
      }
      catch(NumberFormatException badNum) {
         height = -1;
      }
      
      try {
         docBaseURL = new URL(codebaseURL, documentbase);
      }
      catch(MalformedURLException badURL) {
         String msg = Resources.jnlpBundle.getString("jnlp.applet.docbase.invalid");
         throw new InvalidJNLPException(MessageFormat.format(msg, documentbase));
      }
      
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
         
         //param
         if(cNode.getNodeName().equals("param")) {
            String pName = ((Element)cNode).getAttribute("name");
            String pValue = ((Element)cNode).getAttribute("value");
            if(pName != null && pValue != null)
               params.put(pName, pValue);
         }
      }
   }
}
