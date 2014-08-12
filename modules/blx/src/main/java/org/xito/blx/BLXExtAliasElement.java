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

import java.net.*;
import org.w3c.dom.*;

/**
 *
 * @author  Deane
 */
public class BLXExtAliasElement {
   
   public final static String BLX_ALIAS_NODE_NAME = "alias";
   public final static String BLX_ALIAS_NAME_ATTR = "name";
   public final static String BLX_ALIAS_EXT_HREF_ATTR = "ext-href";
   public final static String BLX_ALIAS_SERVICE_ATTR = "service";
   
   private String name;
   private String extHREF;
   private String extName;
   private String extVersion;
   private String serviceName;
   
   private URL contextURL;
   private URL extURL;
   
   /** Creates a new instance of BLXExtAliasElement */
   public BLXExtAliasElement(Element e, URL contextURL) throws DOMException, InvalidBLXXMLException {
      this.contextURL = contextURL;
      
      processNode(e);
   }
   
   private void processNode(Element e) throws DOMException, InvalidBLXXMLException {
      String nodeName = BLXUtility.getLocalNodeName(e);
      if(nodeName.equals(BLX_ALIAS_NODE_NAME) == false) {
         throw new InvalidBLXXMLException("Alias Element needs to be named: "+BLX_ALIAS_NODE_NAME);
      }
      
      //Alias Name
      name = e.getAttribute(BLX_ALIAS_NAME_ATTR);
      if("".equals(name)) {
         name = null;
         throw new InvalidBLXXMLException("Alias name not specified.");
      }
           
      //Extension HREF
      try {
         extHREF = e.getAttribute(BLX_ALIAS_EXT_HREF_ATTR);
         serviceName = e.getAttribute(BLX_ALIAS_SERVICE_ATTR);
         if(extHREF != null && "".equals(extHREF) == false) {
            extURL = new URL(contextURL, extHREF);
         }
         else if(serviceName == null || "".equals(serviceName)) {
            throw new InvalidBLXXMLException("Alias ext-href or service not specified.");
         }
      }
      catch(MalformedURLException badURL) {
         throw new InvalidBLXXMLException("ext-href:"+extHREF+" is not valid.", badURL);
      }
   }
   
   /**
    * Get the Alias name
    */
   public String getAliasName() {
      return name;
   }
   
   /**
    * Get the ServiceName
    */
   public String getServiceName() {
      return serviceName;
   }
   
   /**
    * Get the Extension's URL
    */
   public URL getExtURL() {
      return extURL;
   }
}
