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

package org.xito.launcher.web;

import java.net.*;
import java.util.Iterator;
import java.util.List;
import java.io.*;

import org.jdom.filter.ElementFilter;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.*;
import org.w3c.tidy.*;
import org.xito.launcher.*;

/**
 *
 * @author DRICHAN
 */
public class WebDesc extends BaseLaunchDesc {
   
   protected String address;
   protected URL favIconURL;
   protected URL appleTouchIconURL;
   protected boolean new_browser_flag;
   
   public WebDesc() {
      super();
   }
   
   /**
    * @return the appleTouchIconURL
    */
   public URL getAppleTouchIconURL() {
      return appleTouchIconURL;
   }

   /**
    * @param appleTouchIconURL the appleTouchIconURL to set
    */
   public void setAppleTouchIconURL(URL appleTouchIconURL) {
      this.appleTouchIconURL = appleTouchIconURL;
   }

   /**
    * @return the favIconURL
    */
   public URL getFavIconURL() {
      return favIconURL;
   }

   /**
    * @param favIconURL the favIconURL to set
    */
   public void setFavIconURL(URL favIconURL) {
      this.favIconURL = favIconURL;
   }

   public String getAddress() {
      return address;
   }
   
   public void setAddress(String adr) {
      address = adr;
   }
   
   public boolean useNewBrowser() {
      return new_browser_flag;
   }
   
   public void setUseNewBrowser(boolean b) {
      new_browser_flag = b;
   }
   
   public void initializeWebInfo() throws MalformedURLException, IOException, DOMException {
      
      Tidy tidy = new Tidy();
      URL url = new URL(address);
      InputStream in = url.openConnection().getInputStream();
      Document doc = tidy.parseDOM(in, null);
      Element docElement = doc.getDocumentElement();
      
      //look for a title
      String title = processTitleInDocElement(docElement);
      
      //if no title found set the title to the host name of the url
      if(title == null || title.equals("")) {
         setTitle(url.getHost());
      }
      else {
         setTitle(title);
      }
      
      //look for an icon url
      favIconURL = processIconInDocElement(url, docElement);
      appleTouchIconURL = processAppleIconInDocElement(url, docElement);
      
   }

   /**
    * Process a document element looking for an apple touch icon url
    * @param domDocElement
    * @return
    */
   protected URL processAppleIconInDocElement(URL parentURL, Element domDocElement) {
      
      /*
      <link rel="apple-touch-icon" href="/whatever.jpg"/>
      */

      DOMBuilder domBuilder = new DOMBuilder();
      org.jdom.Element docElement = domBuilder.build(domDocElement);
      
      Iterator linkIt = docElement.getDescendants(new ElementFilter("link"));
      String iconHref = null;
      while(linkIt.hasNext()) {
         org.jdom.Element linkE = (org.jdom.Element)linkIt.next();
         if(linkE.getAttributeValue("rel").equals("apple-touch-icon")) {
            if(iconHref == null || linkE.getAttributeValue("type").equals("image/png")) {
               iconHref = linkE.getAttributeValue("href");
            }
         }
      }
      
      try {
         return iconHref != null ? new URL(parentURL, iconHref) : null;
      }
      catch(MalformedURLException e) {
         return null;
      }
   }

   
   /**
    * Process a document element looking for an icon url
    * @param domDocElement
    * @return
    */
   protected URL processIconInDocElement(URL parentURL, Element domDocElement) {
      
      /*
      <link rel="icon" type="image/png" href="http://example.com/image.png">
      <link rel="icon" type="image/gif" href="http://example.com/image.gif">
      */

      DOMBuilder domBuilder = new DOMBuilder();
      org.jdom.Element docElement = domBuilder.build(domDocElement);
      
      Iterator linkIt = docElement.getDescendants(new ElementFilter("link"));
      String iconHref = null;
      while(linkIt.hasNext()) {
         org.jdom.Element linkE = (org.jdom.Element)linkIt.next();
         if(linkE.getAttributeValue("rel").equals("icon")) {
            if(iconHref == null || linkE.getAttributeValue("type").equals("image/png")) {
               iconHref = linkE.getAttributeValue("href");
            }
         }
      }
      
      try {
         return iconHref != null ? new URL(parentURL, iconHref) : null;
      }
      catch(MalformedURLException e) {
         return null;
      }
   }
   
   /**
    * Process a document Element looking for a title
    * @param domDocElement
    */
   protected String processTitleInDocElement(Element domDocElement) {
      
      DOMBuilder domBuilder = new DOMBuilder();
      org.jdom.Element docElement = domBuilder.build(domDocElement);
      
      Iterator titleIt = docElement.getDescendants(new ElementFilter("title"));
      String tempTitle = null;
      while(titleIt.hasNext()) {
         org.jdom.Element titleE = (org.jdom.Element)titleIt.next();
         tempTitle = titleE.getTextTrim();
         if(tempTitle != null) break;
      }
     
      return tempTitle;
   }
}
