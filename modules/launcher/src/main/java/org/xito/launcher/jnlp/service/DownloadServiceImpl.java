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

package org.xito.launcher.jnlp.service;

import java.io.*;
import java.util.*;
import java.net.*;
import javax.jnlp.DownloadService;
import javax.jnlp.DownloadServiceListener;

import org.xito.boot.*;
import org.xito.launcher.jnlp.*;
import org.xito.launcher.jnlp.xml.*;

/**
 * Implements the JNLP DownloadService. This implementation provides a Bridge to the Xito BootStrap Cache Manager
 *
 * @author Deane Richan
 */
public class DownloadServiceImpl implements DownloadService {
   
   private JNLPAppDesc jnlpDesc;
   
   /** Creates a new instance of DownloadServiceImpl */
   public DownloadServiceImpl(JNLPAppDesc appDesc) {
      jnlpDesc = appDesc;
   }
   
   /**
    * Remove a Single Part
    */
   public void removePart(String part) throws IOException {
      if(part == null) return;
      
      CacheManager cm = Boot.getCacheManager();
      List resources = jnlpDesc.getJNLPNode().getResourcesForPart(part);
      if(resources == null) return;
      Iterator it = resources.iterator();
      while(it.hasNext()) {
         Object resource = it.next();
         //JarNode, NativeLibNode, and ExtNode
         if(resource instanceof JarNode) {
            cm.clearCache(((JarNode)resource).getURL());
         }
         else if(resource instanceof NativeLibNode) {
            cm.clearCache(((NativeLibNode)resource).getURL());
         }
         else if(resource instanceof ExtNode) {
            //cm.clearCache(((NativeLibNode)resource).getURL());
         }
      }
   }

   /**
    * Remove several Parts
    */
   public void removePart(String[] parts) throws IOException {
      if(parts == null) return;
   
      ArrayList resources = new ArrayList();
      for(int i=0;i<parts.length;i++) {
         List r = jnlpDesc.getJNLPNode().getResourcesForPart(parts[i]);
         resources.addAll(r);
      } 
      
   }
   
   /**
    * Remove a list of Extenion's Parts
    */
   public void removeExtensionPart(URL extURL, String version, String[] parts) throws IOException {
      if(parts == null) return;
      for(int i=0;i<parts.length;i++) {
         removeExtensionPart(extURL, version, parts[i]);
      }
   }

   public void removeExtensionPart(URL extURL, String version, String part) throws IOException {
      
   }
   
   /**
    * Remove a single resource
    */
   public void removeResource(URL url, String version) throws IOException {
      //Version's are not supported
      if(Boot.getCacheManager().clearCache(url)==false) {
         throw new IOException("Resource:"+url.toString()+" could not be removed. It may be in use");
      }
   }
      
   public boolean isPartCached(String part) {
      
      return false;
   }
   
   public boolean isExtensionPartCached(URL url, String str, String[] parts) {
      return false;
   }

   public boolean isExtensionPartCached(URL url, String version, String part) {
      return false;
   }

   public boolean isResourceCached(URL url, String version) {
      return false;
   }

   public boolean isPartCached(String[] parts) {
      return false;
   }

   public void loadPart(String[] parts, DownloadServiceListener downloadServiceListener) throws IOException {
   }
   
   public void loadExtensionPart(URL url, String verstion, String part, DownloadServiceListener downloadServiceListener) throws IOException {
   }

   public void loadExtensionPart(URL url, String version, String[] parts, DownloadServiceListener downloadServiceListener) throws IOException {
   }

   public void loadResource(URL url, String version, DownloadServiceListener downloadServiceListener) throws IOException {
   }

   public void loadPart(String str, DownloadServiceListener downloadServiceListener) throws IOException {
   }

   public DownloadServiceListener getDefaultProgressWindow() {
      return null;
   }
}
