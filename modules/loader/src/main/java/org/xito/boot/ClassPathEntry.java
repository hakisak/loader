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

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import java.util.logging.*;
import java.security.*;

/**
 * Describes a specific entry for a ClassPath. ClassPath entries can be Jar files Zip files or
 * a URL to a Folder. They can be configured to be downloaded eagerly or lazily, and set to
 * be cached or not cached.
 *
 * @author Deane Richan
 */
public class ClassPathEntry implements Serializable {

   private static Logger logger = Logger.getLogger(ClassPathEntry.class.getName());
   
   public static final int EAGER = 0;
   public static final int LAZY = 1;
   
   private int downloadType = EAGER;
   private String partName = null;
   private CachePolicy cachePolicy = CachePolicy.ALWAYS;
   private boolean mainJar;
   private URL resourceURL = null;
   private String os = null;
   
   transient protected boolean downloaded_flag = false;
   transient private boolean initialized_flag = false;
   transient private JarFile jarFile;
   transient private Hashtable<String, JarEntry> jarEntryNames = new Hashtable<String, JarEntry>();
   transient private Manifest manifest;
   transient private String defaultSpecTitle;
   transient private String defaultSpecVersion;
   transient private String defaultSpecVendor;
   transient private String defaultImplTitle;
   transient private String defaultImplVersion;
   transient private String defaultImplVendor;
   transient private boolean defaultSealed;
 
   /**
    * Create a ClassPath Entry
    */
   public ClassPathEntry(String os, URL url) {
      this.os = os;
      setResourceURL(url);
   }
   
   /**
    * Create a ClassPath Entry
    */
   public ClassPathEntry(URL url) {
      setResourceURL(url);
   }

   /**
    * Make a Copy of this ClassPath Entry
    */
   public ClassPathEntry copy() {
      
      ClassPathEntry c = new ClassPathEntry(resourceURL);
      c.downloadType = this.downloadType;
      c.cachePolicy = this.cachePolicy;
      c.partName = this.partName;
      c.mainJar = this.mainJar;
      
      return c;
   }
   
   /**
    * Return true if the resource has been downloaded
    */
   public boolean isDownloaded() {
      return downloaded_flag;
   }

   /**
    * return true if resource URL points to jar file
    */
   public boolean isJar() {
      if(resourceURL.toString().endsWith(".jar")){
         return true;
      }
      else {
         return false;
      }
   }

   /**
    * return true if resource URL points to zip file
    */
   public boolean isZip() {
      if(resourceURL.toString().endsWith(".zip")){
         return true;
      }
      else {
         return false;
      }
   }

   /**
    * Attempt to close any resources that are open
    */
   public synchronized void close() {
      
      downloaded_flag = false;
      initialized_flag = false;
      if(jarEntryNames != null) {
         jarEntryNames.clear();
      }
            
      if(jarFile != null) {
         try {
            jarFile.close();
         }
         catch(IOException ioExp) {
            //ignore this I don't care
            logger.log(Level.WARNING, ioExp.getMessage(), ioExp);
         }
      }
      
      manifest = null;
      jarFile = null;
   }

   /** 
    * Download the resource and then Initialize it
    */
   protected void downloadResource() {
      if(downloaded_flag) return;

      //if the resource is not a Jar or Zip then just return
      //we can't download it
      if(!isJar() && !isZip()) {
         return;
      }

      CacheManager cm = Boot.getCacheManager();
      try {
         cm.downloadResource(null, resourceURL, cm.getDefaultListener(), cachePolicy);
         downloaded_flag = true;
         initializeJar();
      }
      catch(Exception exp) {
         logger.log(Level.SEVERE, exp.getMessage(), exp);
      }
   }

   protected synchronized boolean isInitialized() {
      return initialized_flag;
   }

   /**
    * Initialize the Jar Archive
    * The resource is assumed to already be downloaded and in the cache before this is called
    */
   protected synchronized void initializeJar() {

      //Check to see if we are already initialized
      if(isInitialized()) return;

      //we can't initialize non-jar non-zip resources
      if(!isJar() && !isZip()) {
         initialized_flag = true;
         return;
      }

      try {
         File f = null;
         if(resourceURL.getProtocol().equals("file")){
            f = new File(resourceURL.getFile());
         }
         else {
            f = Boot.getCacheManager().getCachedFileForURL(resourceURL);
         }
         if(!f.exists()) {
            logger.log(Level.WARNING, "*** Resource file:"+f.toString()+" was not found!");
            return;
         }
         jarFile = new JarFile(f);
         
         jarEntryNames.clear();
         Enumeration<JarEntry> entries = jarFile.entries();
         while(entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            jarEntryNames.put(entry.getName(), entry);
         }

         manifest = jarFile.getManifest();
         if(manifest != null) {
            Attributes mainAttrs = manifest.getMainAttributes();
            if(mainAttrs != null) {
               defaultSpecTitle = mainAttrs.getValue("Specification-Title");
               defaultSpecVersion = mainAttrs.getValue("Specification-Version");
               defaultSpecVendor = mainAttrs.getValue("Specification-Vendor");
               defaultImplTitle = mainAttrs.getValue("Implementation-Title");
               defaultImplVersion = mainAttrs.getValue("Implementation-Version");
               defaultImplVendor = mainAttrs.getValue("Implementation-Vendor");
               defaultSealed = Boolean.valueOf(mainAttrs.getValue("Sealed")).booleanValue();         
            }
         }
      }
      catch(Exception ioExp) {
         logger.log(Level.SEVERE, ioExp.getMessage(), ioExp);
      }
      finally {
         initialized_flag = true;
      }
   }

   /**
    * process information from the Manifest
    */
   protected void definePackage(String name, CacheClassLoader loader) {

      if(name == null) return;
      //Get attributes for this package
      if(manifest != null) {
         Attributes attrs = manifest.getAttributes(name.replace('.', '/')+'/');
         if(attrs != null) {
            String specTitle = defaultSpecTitle != null ? defaultSpecTitle : attrs.getValue("Specification-Title");
            String specVersion = defaultSpecVersion != null ? defaultSpecVersion : attrs.getValue("Specification-Version");
            String specVendor = defaultSpecVendor != null ? defaultSpecVendor : attrs.getValue("Specification-Vendor");
            String implTitle = defaultImplTitle != null ? defaultImplTitle : attrs.getValue("Implementation-Title");
            String implVersion = defaultImplVersion != null ? defaultImplVersion : attrs.getValue("Implementation-Version");
            String implVendor = defaultImplVendor != null ? defaultImplVendor : attrs.getValue("Implementation-Vendor");
            String sealedStr = attrs.getValue("Sealed");
            boolean sealed = defaultSealed;
            if(sealedStr != null) {
               sealed = Boolean.valueOf(attrs.getValue("Sealed")).booleanValue();         
            }
            loader.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, (sealed)?resourceURL:null);
            return;
         }
      }

      //Just use default settings
      loader.definePackage(name, defaultSpecTitle, defaultSpecVersion, defaultSpecVendor, defaultImplTitle, defaultImplVersion, defaultImplVendor, (defaultSealed)?resourceURL:null);

   }

   /**
    * Get the OS this ClassPathEntry should be used for
    * @return
    */
   public String getOs() {
      return os;
   }

   /**
    * Set the OS this ClassPathEntry should be used for
    * @param os
    */
   public void setOs(String os) {
      this.os = os;
   }

   /**
    * Set the Resource URL for this ClassPath Entry
    */
   public void setResourceURL(URL url) {

      resourceURL = url;
   }
   
   /**
    * Get the URL of the Resource
    */
   public URL getResourceURL() {
      return resourceURL;
   }

   /**
    * Set Part Name
    */
   public void setPart(String part) {
      partName = part;
   }

   /**
    * Get the Part Name
    */
   public String getPart() {
      return partName;
   }
   
   /**
    * Set the Cache Policy of this Resource. Defaults to ALWAYS if policy is null
    */
   public void setCachePolicy(CachePolicy policy) {
      if(policy == null) {
         cachePolicy = CachePolicy.ALWAYS;
         return;
      }
      
      cachePolicy = policy;
   }
   
   /**
    * Get the Cache Policy of this Resource
    */
   public CachePolicy getCachePolicy() {
      return cachePolicy;
   }

   /**
    * Set Main Jar Flag
    */
   public void setMainJar(boolean b) {
      mainJar = b;
   }

   /**
    * Return true if this resource is flagged as a Main jar
    */
   public boolean isMainJar() {
      return mainJar;
   }

   /**
    * Get the Main Class from this Resource Manifest
    */
   public String getMainClassName() {

      downloadResource();
      initializeJar();

      if(manifest == null) return null;
      Attributes attrs = manifest.getMainAttributes();
      if(attrs == null) return null;

      return attrs.getValue("Main-Class");
   }

   /**
    * Set the Download Type either EAGER or LAZY
    */
   public void setDownloadType(int type) {
      if(downloadType == LAZY) {
         downloadType = LAZY;
      }
      else {
         downloadType = EAGER;
      }
   }

   /**
    * Get the Download Type either EAGER or LAZY
    */
   public int getDownloadType() {
      return downloadType;
   }

   /**
    * Find a Class in this ClassPath Entry
    */
   protected Class findClass(String name, CacheClassLoader loader) throws ClassNotFoundException {

      if(!isInitialized()) {
         initializeJar();
      }

      //If this resource is not a jar or zip then attempt to just download the file
      if(!isJar() && !isZip()) {
         initialized_flag = true;
         return findClassFromURL(name, loader);
      }

      //Check to see if this Jar has this class
      String classFileName = CacheClassLoader.getClassFileName(name);

      if(!jarEntryNames.containsKey(classFileName)) {
         throw new ClassNotFoundException(name);
      }

      synchronized (this) {
         JarEntry entry = jarFile.getJarEntry(classFileName);

         //If no entry in jar for this name then throw exception
         if(entry == null) {
            throw new ClassNotFoundException(name);
         }

         //Extract the Class Data from the Jar
         InputStream in = null;
         try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            in = jarFile.getInputStream(entry);
            byte buf[] = new byte[1024];
            int c = in.read(buf);
            while(c != -1) {
               out.write(buf, 0, c);
               c = in.read(buf);
            }

            byte data[] = out.toByteArray();
            URL u = resourceURL;
            if(u != null && !u.getProtocol().equals("file")) {
               u = Boot.getCacheManager().convertToCachedURL(u);
            }
            CodeSource cs = new CodeSource(u, entry.getCertificates());
            return loader.defineClassInternal(name, data, 0, data.length, cs);
         }
         catch(IOException ioExp) {
            logger.log(Level.SEVERE, ioExp.getMessage(), ioExp);
            throw new ClassNotFoundException(name);
         }
         finally {
            if(in != null) {
               try {in.close();}catch(IOException ioExp){ioExp.printStackTrace();}
            }
         }
      }
   }

   /**
    * Find a class using the URL the URL is assumed to be a base
    * URL. The class name is appended to it to download the file
    */
   protected Class findClassFromURL(String name, CacheClassLoader loader) throws ClassNotFoundException {

      URL url = null;
      InputStream in = null;
      try {
         if(resourceURL.toString().endsWith("/")) {
            url = new URL(resourceURL.toString() + CacheClassLoader.getClassFileName(name));
         }
         else {
            url = new URL(resourceURL.toString() + "/" + CacheClassLoader.getClassFileName(name));
         }

         //Now download the URL
         Boot.getCacheManager().downloadResource(null, url, null, cachePolicy);
         File f = Boot.getCacheManager().getCachedFileForURL(url);
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         in = new FileInputStream(f);
         byte buf[] = new byte[1024];
         int c = in.read(buf);
         while(c != -1) {
            out.write(buf, 0, c);
            c = in.read(buf);
         }

         byte data[] = out.toByteArray();
         URL u = resourceURL;
         if(u != null && !u.getProtocol().equals("file")) {
            u = Boot.getCacheManager().convertToCachedURL(u);
         }
         CodeSource cs = new CodeSource(u, (java.security.cert.Certificate[])null);
         Class cls = loader.defineClassInternal(name, data, 0, data.length, cs);
         return cls;
      }
      catch(Exception exp) {
         logger.log(Level.SEVERE, exp.getMessage(), exp);
         throw new ClassNotFoundException(name);
      }
      finally {
         if(in != null) {
            try {in.close();}catch(IOException ioExp){ioExp.printStackTrace();}
         }
      }

   }

   /**
    * Find a Resource in this ClassPath Entry
    */
   protected URL findResource(String name) {

      //First initialize jar
      if(!isInitialized()) {
         initializeJar();
      }

      //If this resource is not a jar or zip then attempt to find the Resource from a URL
      if(!isJar() && !isZip()) {
         initialized_flag = true;
         return findResourceFromURL(name);
      }

      //Check to see if this Jar has this resource
      synchronized (this) {

         JarEntry entry = jarFile.getJarEntry(name);

         //If no entry in jar for this name then return null
         if(entry == null) {
            return null;
         }


         //Return a URL to this Resource
         File f = null;
         try {
            if(resourceURL.getProtocol().equals("file")){
               f = new File(resourceURL.getFile());
            }
            else {
               f = Boot.getCacheManager().getCachedFileForURL(resourceURL);
            }

            URL url = new URL("jar:"+f.toURI().toURL()+"!/"+name);
            return url;
         }
         catch(Exception badURL) {
            logger.log(Level.SEVERE, badURL.getMessage(), badURL);
         }

         return null;
      }
   }

   /**
    * Find a resource using the URL the URL is assumed to be a base
    * URL. The resource name is appended to it to download the file
    */
   protected URL findResourceFromURL(String name) {

      URL url = null;
      try {
         if(resourceURL.toString().endsWith("/")) {
            url = new URL(resourceURL.toString() + name);
         }
         else {
            url = new URL(resourceURL.toString() + "/" + name);
         }

         //Now download the URL
         Boot.getCacheManager().downloadResource(null, url, null, cachePolicy);
         File f = Boot.getCacheManager().getCachedFileForURL(url);
         return f.toURI().toURL();
      }
      catch(Exception exp) {
         logger.log(Level.SEVERE, exp.getMessage(), exp);
      }

      return null;

   }
}
   
   
