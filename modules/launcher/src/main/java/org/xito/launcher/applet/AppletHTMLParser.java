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

package org.xito.launcher.applet;

import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
import javax.xml.parsers.*;

import org.xito.boot.*;
import org.w3c.dom.*;
import org.w3c.tidy.*;

/**
 *
 * @author  Deane
 */
public class AppletHTMLParser {
      
   private static Logger logger = Logger.getLogger(AppletHTMLParser.class.getName());

   private Tidy tidy; 
   private CacheManager cm;
   protected DocumentBuilderFactory builderFactory;
   protected DocumentBuilder builder;
  
   /**
    * Create an AppletHTMLParser
    */
   public AppletHTMLParser() {
      try {
         builderFactory = DocumentBuilderFactory.newInstance();
         builder = builderFactory.newDocumentBuilder();
      } catch(ParserConfigurationException parserExp) {
         throw new RuntimeException("can read service information file, error:"+parserExp.getMessage(), parserExp);
      } catch(DOMException domExp) {
         throw new RuntimeException("can't read services xml error:"+domExp.getMessage(), domExp);
      }
      
      tidy = new Tidy();
      cm = Boot.getCacheManager();
   }
   
   /**
    * Obtain AppletDesc's from a URL
    */
   public AppletDesc[] parseApplets(URL url) throws IOException {
      
      ArrayList applets = new ArrayList();
      
      //First get the HTML page from CacheManager
      InputStream in = null;
      try {
         cm.downloadResource(url, null);
         File f = cm.getCachedFileForURL(url);
         in = new FileInputStream(f);
         
         //Document doc = tidy.parseDOM(in, System.out);
         Document doc = tidy.parseDOM(in, null);
         findApplets(doc.getDocumentElement(), url, applets);
         updateTitles(doc.getDocumentElement(), applets);
      }
      catch(IOException ioExp) {
         logger.log(Level.SEVERE, ioExp.getMessage(), ioExp);
         throw new IOException("Error Reading url: "+url.toString());
      }
      finally {
    	  if(in != null)
    		  try{ in.close(); }catch(IOException ioExp){}
      }
      
      return (AppletDesc[]) applets.toArray(new AppletDesc[0]);
   }
   
   /**
    * Update the Applet Titles based on the Web Page Title
    */
   private void updateTitles(Element e, List appletList) {
      if(e==null || appletList == null) return;
      NodeList children = e.getElementsByTagName("title");
      if(children == null) return;
      
      String title = null;
      for(int i=0;i<children.getLength();i++) {
         try {
            Element childE = (Element)children.item(i);
            if(childE.getTagName().equals("title")) {
               NodeList titleChildren = childE.getChildNodes();
               for(int n=0;n<titleChildren.getLength();n++) {
                  if(titleChildren.item(n).getNodeType() == Element.TEXT_NODE) {
                     title = titleChildren.item(n).getNodeValue();
                  }
               }
            }
         }
         catch(Exception exp) {
            //ignore this
            exp.printStackTrace();
         }
      }
      if(title == null) return;
      
      Iterator it = appletList.iterator();
      while(it.hasNext()) {
         AppletDesc desc = (AppletDesc)it.next();
         if(desc.getName() == null)
            desc.setName(title);
         if(desc.getTitle() == null) 
            desc.setTitle(title);
      }
   }
   
   /**
    * Find applet nodes in this element and its children and add
    * there descriptions to the appletColl
    *
    * @param e to search
    * @param url
    * @param appletList to add applets to
    */
   public void findApplets(Element e, URL url, List appletList) {
   
      if(e == null) return;
      if(e.getNodeName().equals("applet")) {
         AppletDesc appletDesc = getAppletDesc(e, url);
         if(appletDesc != null) {
            appletList.add(appletDesc);
         }
         return;
      }
      
      //Search all Children
      if(e.hasChildNodes()) {
         NodeList children = e.getElementsByTagName("applet");
         if(children == null) return;
         for(int i=0;i<children.getLength();i++) {
            AppletDesc appletDesc = getAppletDesc((Element)children.item(i), url);
            if(appletDesc != null) {
               appletList.add(appletDesc);
            }
         }
      }
   }
   
   /**
    * Create a DOM Element for an Applet Desc
    */
   public Element createElementForAppletDesc(AppletDesc desc, URL url) {
      Document doc = builder.newDocument();
      Element e = doc.createElement("applet");
      if(desc == null) return e;
      
      //Name
      e.setAttribute("name", desc.getName());
            
      //Alt
      e.setAttribute("alt", desc.getTitle());
            
      //Code
      e.setAttribute("code", desc.getAppletClass());
                  
      //CodeBase
      String codebase = desc.getCodeBaseURL().toString();
      e.setAttribute("codebase", codebase);
                        
      //Archive
      StringBuffer archive = new StringBuffer();
      Iterator it = desc.getArchives().iterator();
      while(it.hasNext()) {
         archive.append((String)it.next());
         archive.append(",");
      }
      e.setAttribute("archive", archive.toString());
            
      //Width 
      e.setAttribute("width", ""+desc.getWidth());
            
      //Height
      e.setAttribute("height", ""+desc.getHeight());
                        
      //parameters
      Iterator keys = desc.getParameters().keySet().iterator();
      while(keys.hasNext()) {
         String name = (String)keys.next();
         String value = (String)desc.getParameters().get(name);
         Element paramE = doc.createElement("param");
         paramE.setAttribute("name", name);
         paramE.setAttribute("value", value);
         e.appendChild(paramE);
      }
      
      return e;
   }
   
   /**
    * Get an AppletDesc from an Element
    * @param e that contains applet desc
    * @param url
    */
   public AppletDesc getAppletDesc(Element e, URL url) {
      
      if(!e.getNodeName().equals("applet")) return null;
      
      //Name
      String name = e.getAttribute("name");
      if(name != null && name.equals("")){
         name = null;
      }
                  
      //Alt
      String alt = e.getAttribute("alt");
      if(alt != null && alt.equals("")) {
         alt = null;
      }
      
      AppletDesc appletDesc = new AppletDesc(name, alt);
      
      //Code
      String code = e.getAttribute("code");
      if(code != null) {
         if(code.endsWith(".class")) code = code.substring(0, code.length()-6);
         code = code.replace('/','.');
         appletDesc.setAppletClass(code);
      }
      logger.info("code:"+code);
      
      //Document URL
      appletDesc.setDocumentURL(url);
      logger.info("Document URL:"+url);
      
      //CodeBase
      String codeBase = e.getAttribute("codebase");
      logger.info("codebase:"+codeBase);
      if(codeBase != null && !codeBase.equals("")) {
         try {
            URL codeBaseURL = new URL(url, codeBase);
            logger.info("codeBaseURL:"+codeBaseURL);
            appletDesc.setCodeBaseURL(codeBaseURL);
         }
         catch(MalformedURLException badURL) {
            logger.log(Level.WARNING, badURL.getMessage(), badURL);
         }
      }
                  
      //Archive
      String archive = e.getAttribute("archive");
      logger.info("archive:"+archive);
      ArrayList archiveList = new ArrayList();
      if(archive != null && !archive.equals("")) {
         String[] archives = archive.split(",");
         for(int i=0;i<archives.length;i++) {
            String arch = archives[i];
            if(arch == null || arch.equals("") || arch.equals(","))
               continue;
            archiveList.add(arch.trim());
         }
      }
      appletDesc.setArchives(archiveList);
      
      //Width 
      String width = e.getAttribute("width");
      logger.info("width:"+width);
      int w = 640;
      try {
         w = Integer.parseInt(width);
      }
      catch(NumberFormatException badNum) {
      }
      appletDesc.setWidth(w);
      
      //Height
      String height = e.getAttribute("height");
      logger.info("height:"+height);
      int h = 480;
      try {
         h = Integer.parseInt(height);
      }
      catch(NumberFormatException badNum) {
      }
      appletDesc.setHeight(h);
                  
      //process parameters
      NodeList paramNodes = e.getElementsByTagName("param");
      HashMap params = new HashMap();
      for(int i=0;i<paramNodes.getLength();i++) {
         Element param = (Element)paramNodes.item(i);
         String n = param.getAttribute("name");
         String v = param.getAttribute("value");
         params.put(n.toLowerCase(), v);
      }
      appletDesc.setParameters((Map)params);
      
      return appletDesc;
   }
   
}
