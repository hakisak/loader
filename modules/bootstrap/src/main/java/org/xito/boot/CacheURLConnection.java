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

package org.xito.boot;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.security.Permission;

/**
 * Used by CacheManager to download resources to a local cache
 *
 * @author  Deane Richan
 */
public class CacheURLConnection extends URLConnection {
   
   //Logger to log all messages to
   private Logger logger = Logger.getLogger(CacheURLConnection.class.getName());
   
   //Wrapped Connection
   private URLConnection wrappedCon;
   
   /** Creates a new instance of CacheURLConnection */
   public CacheURLConnection(URL url, URLConnection con) {
      super(url);
      wrappedCon = con;
   }
   
   public void connect() throws java.io.IOException {
      wrappedCon.connect();
   }
   
   protected void rebuildWrappedConnection() {
      try {
         wrappedCon = wrappedCon.getURL().openConnection();
      }
      catch(IOException io) {
         //Couldn't reconnect
         logger.log(Level.SEVERE, io.getMessage(), io);
      }
   }
   
   public java.io.OutputStream getOutputStream() throws java.io.IOException {
      OutputStream retValue;
      
      retValue = wrappedCon.getOutputStream();
      return retValue;
   }
   
   public void setDefaultUseCaches(boolean defaultusecaches) {
      super.setDefaultUseCaches(defaultusecaches);
   }
   
   public long getIfModifiedSince() {
      long retValue;
      
      retValue = wrappedCon.getIfModifiedSince();
      return retValue;
   }
   
   public java.security.Permission getPermission() throws java.io.IOException {
      Permission retValue;
      
      retValue = wrappedCon.getPermission();
      return retValue;
   }
   
   public void setUseCaches(boolean usecaches) {
      wrappedCon.setUseCaches(usecaches);
   }
   
   public void addRequestProperty(String key, String value) {
      wrappedCon.addRequestProperty(key, value);
   }
   
   public java.util.Map getRequestProperties() {
      Map retValue;
      
      retValue = wrappedCon.getRequestProperties();
      return retValue;
   }
   
   public boolean getDefaultUseCaches() {
      boolean retValue;
      
      retValue = wrappedCon.getDefaultUseCaches();
      return retValue;
   }
   
   public boolean getDoOutput() {
      boolean retValue;
      
      retValue = wrappedCon.getDoOutput();
      return retValue;
   }
   
   public long getHeaderFieldDate(String name, long Default) {
      long retValue;
      
      retValue = wrappedCon.getHeaderFieldDate(name, Default);
      return retValue;
   }
   
   public long getDate() {
      long retValue;
      
      retValue = wrappedCon.getDate();
      return retValue;
   }
   
   public void setRequestProperty(String key, String value) {
      try {
         wrappedCon.setRequestProperty(key, value);
      }
      catch(IllegalAccessError t) {
         rebuildWrappedConnection();
         wrappedCon.setRequestProperty(key, value);
      }
   }
   
   public void setAllowUserInteraction(boolean allowuserinteraction) {
      wrappedCon.setAllowUserInteraction(allowuserinteraction);
   }
   
   public String getHeaderField(int n) {
      String retValue;
      
      retValue = wrappedCon.getHeaderField(n);
      return retValue;
   }
   
   public String getHeaderField(String name) {
      String retValue;
      
      retValue = wrappedCon.getHeaderField(name);
      return retValue;
   }
   
   public String getHeaderFieldKey(int n) {
      String retValue;
      
      retValue = wrappedCon.getHeaderFieldKey(n);
      return retValue;
   }
   
   public Object getContent(Class[] classes) throws java.io.IOException {
      Object retValue;
      
      retValue = wrappedCon.getContent(classes);
      return retValue;
   }
   
   public long getExpiration() {
      long retValue;
      
      retValue = wrappedCon.getExpiration();
      return retValue;
   }
   
   protected void finalize() throws Throwable {
      super.finalize();
   }
   
   public void setDoInput(boolean doinput) {
      wrappedCon.setDoInput(doinput);
   }
   
   public long getLastModified() {
      long retValue;
      
      retValue = super.getLastModified();
      return retValue;
   }
   
   public boolean getDoInput() {
      boolean retValue;
      
      retValue = wrappedCon.getDoInput();
      return retValue;
   }
   
   public boolean getUseCaches() {
      boolean retValue;
      
      retValue = wrappedCon.getUseCaches();
      return retValue;
   }
   
   public Object getContent() throws java.io.IOException {
      Object retValue;
      
      retValue = wrappedCon.getContent();
      return retValue;
   }
   
   public String getContentType() {
      String retValue;
      
      retValue = wrappedCon.getContentType();
      return retValue;
   }
   
   public java.util.Map getHeaderFields() {
      Map retValue;
      
      retValue = wrappedCon.getHeaderFields();
      return retValue;
   }
   
   public void setDoOutput(boolean dooutput) {
      wrappedCon.setDoOutput(dooutput);
   }
   
   public java.io.InputStream getInputStream() throws java.io.IOException {
      InputStream retValue;
      
      retValue = wrappedCon.getInputStream();
      return retValue;
   }
   
   public String getRequestProperty(String key) {
      String retValue;
      
      retValue = wrappedCon.getRequestProperty(key);
      return retValue;
   }
   
   public int getHeaderFieldInt(String name, int Default) {
      int retValue;
      
      retValue = wrappedCon.getHeaderFieldInt(name, Default);
      return retValue;
   }
   
   public void setIfModifiedSince(long ifmodifiedsince) {
      wrappedCon.setIfModifiedSince(ifmodifiedsince);
   }
   
   public String getContentEncoding() {
      String retValue;
      
      retValue = wrappedCon.getContentEncoding();
      return retValue;
   }
   
   public boolean getAllowUserInteraction() {
      boolean retValue;
      
      retValue = wrappedCon.getAllowUserInteraction();
      return retValue;
   }
   
   public int getContentLength() {
      int retValue;
      
      retValue = wrappedCon.getContentLength();
      return retValue;
   }
   
}
