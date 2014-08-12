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
import java.net.*;
import java.util.logging.*;
import org.w3c.dom.*;

import org.xito.launcher.Resources;

/**
 *
 * @author DRICHAN
 */
public abstract class AbstractResourceNode extends AbstractNode {
   
   private static final Logger logger = Logger.getLogger(AbstractResourceNode.class.getName());
   
   private String href;
   
   private URL url;
   private String version;
   protected String download;
   private String part;
      
   /** Creates a new instance of IconDescNode */
   protected void processNode(URL codebaseURL, Element node) throws InvalidJNLPException {
      
      download = node.getAttribute("download");
      part = node.getAttribute("part");
      version = node.getAttribute("version");
      
      href = node.getAttribute("href");
      try {
         url = new URL(codebaseURL, href);
      }
      catch(MalformedURLException badURL) {
         logger.log(Level.WARNING, "bad resource url:"+badURL.getMessage());
      }
   }
   
   /**
    * Get the Jar's URL
    */
   public URL getURL() {
      return url;
   }
   
   /**
    * Get the Part Name this resource is part of
    */
   public String getPart() {
      return part;
   }
   
   /**
    * Return true if this resource should be downloaded lazily
    */
   public boolean isLazyDownload() {
      if(download != null && download.equals("lazy")) {
         return true;
      }
      
      return false;
   }
   
}
