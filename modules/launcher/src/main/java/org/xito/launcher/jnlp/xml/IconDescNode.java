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
public class IconDescNode extends AbstractNode {
   
   private static final Logger logger = Logger.getLogger(IconDescNode.class.getName());
   
   public static final String NAME = "icon";
   
   private String href;
   private int width;
   private int height;
   private String depth;
   private String kind;
   private String size;
   
   private URL iconURL;
   
   /** Creates a new instance of IconDescNode */
   public IconDescNode(URL codebaseURL, Element node) throws InvalidJNLPException {
      if(!node.getNodeName().equals(NAME)) {
         String msg = Resources.jnlpBundle.getString("jnlp.xml.bad.element");
         throw new InvalidJNLPException(MessageFormat.format(msg, NAME));
      }
      
      href = node.getAttribute("href");
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
      
      depth = node.getAttribute("depth");
      kind = node.getAttribute("kind");
      size = node.getAttribute("size");
      
      if(kind == null || kind.equals(""))
         kind = "default";
      
      try {
         iconURL = new URL(codebaseURL, href);
      }
      catch(MalformedURLException badURL) {
         logger.log(Level.WARNING, "bad icon url:"+badURL.getMessage());
      }
   }
   
   /**
    * Get kind of Icon or default if not specified
    */
   public String getKind() {
      return kind;
   }
   
   /**
    * Get the Icon's URL
    */
   public URL getIconURL() {
      return iconURL;
   }
   
   public int getWidth() {
      return width;
   }
   
   public int getHeight() {
      return height;
   }
   
}
