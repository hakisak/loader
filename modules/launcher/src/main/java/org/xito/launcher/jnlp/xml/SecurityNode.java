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
public class SecurityNode {
   
   public static final String NAME = "security";
   
   public static final int RESTRICTED_PERM = 0;
   public static final int ALL_PERM = 1;
   public static final int J2EE_CLIENT_PERM = 2;
   
   private int permType = RESTRICTED_PERM;
   
   /** Creates a new instance of SecurityNode */
   public SecurityNode(URL codebaseURL, Element node) throws InvalidJNLPException {
      if(!node.getNodeName().equals(NAME)) {
         String msg = Resources.jnlpBundle.getString("jnlp.xml.bad.element");
         throw new InvalidJNLPException(MessageFormat.format(msg, NAME));
      }
      
      //Get the Children
      NodeList nodes = node.getChildNodes();
      for(int i=0;i<nodes.getLength();i++) {
         Node cNode = nodes.item(i);
         String nodeName = cNode.getNodeName();
         
         //skip text nodes
         if(cNode.getNodeType() == Node.TEXT_NODE) continue;
         
         //all-permissions
         if(cNode.getNodeName().equals("all-permissions")) {
            permType = ALL_PERM;
         }  
         
         //j2ee-application-client-permissions
         if(cNode.getNodeName().equals("j2ee-application-client-permissions")) {
            permType = J2EE_CLIENT_PERM;
         }
      }
      
   }
   
   /**
    * Get the Type of Permission the App Wants
    */
   public int getPermissionType() {
      return permType;
   }
   
}
