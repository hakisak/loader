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
import java.util.logging.*;
import java.util.jar.*;
import java.util.zip.*;
import java.security.*;

/**
 * Classloader that loads code from a Cache.
 *
 * @author Deane Richan
 */
public class CacheClassLoader extends SecureClassLoader {

   private static Logger logger = Logger.getLogger(CacheClassLoader.class.getName());

   protected ExecutableDesc execDesc;

   //List of Service Loaders that this service loader can delegate to for finding classes
   protected List<ServiceClassLoader> serviceLoaderRefs = new Vector<ServiceClassLoader>();

   protected List<ClassPathEntry> classPath = new Vector<ClassPathEntry>();
   private Map<URL, ClassPathEntry> eagerEntries = new HashMap<URL, ClassPathEntry>();
   private Map<URL, ClassPathEntry> lazyEntries = new HashMap<URL, ClassPathEntry>();
   private Map<String, Set<ClassPathEntry>> packageCache = new HashMap<String, Set<ClassPathEntry>>();
   private Set<String> definedPackages = new HashSet<String>();

   private boolean destroyed_flag = false;
   private boolean natives_unpacked_flag = false;
   protected boolean eager_resources_started_flag = false;

   /**
    * Create a CacheClassLoader
    *
    * @param execDesc Descriptor of Executable
    * @param parent   Class Loader
    * @throws ServiceNotFoundException if a service this service depends on can't be found
    */
   public CacheClassLoader(ExecutableDesc execDesc, ClassLoader parent) throws ServiceNotFoundException {

      super(parent);
      this.execDesc = execDesc;
      initialize(execDesc.getClassPath(), execDesc.getServiceRefs());
   }

   /**
    * Create a CacheClassLoader
    *
    * @param classPath   entries
    * @param serviceRefs services this service is dependent on
    * @param parent      Class Loader
    * @throws ServiceNotFoundException if a service this service depends on can't be found
    */
   public CacheClassLoader(ClassPathEntry[] classPath, ServiceDescStub[] serviceRefs, ClassLoader parent) throws ServiceNotFoundException {

      super(parent);
      initialize(classPath, serviceRefs);
   }

   /**
    * Setup service and classpath refs and download all Eager Resources
    *
    * @param classPath   array of classpath entries
    * @param serviceRefs array of service's this service is dependent on
    * @throws ServiceNotFoundException if a service this service depends on can't be found
    */
   protected void initialize(ClassPathEntry[] classPath, ServiceDescStub[] serviceRefs) throws ServiceNotFoundException {

      //Setup Service Refs
      if (serviceRefs != null) {
         for (ServiceDescStub serviceRef : serviceRefs) {
            addServiceRefLoader(serviceRef);
         }
      }

      //Setup ClassPath
      if (classPath != null) {
         for (ClassPathEntry aClassPath : classPath) {
            this.classPath.add(aClassPath.copy());
         }
      }
   }

   /**
    * Get Main Class from Jar Resources. The first jar that contains a main class name
    * will be used
    *
    * @return the name of the main class from all the jars
    */
   public String getMainClassFromJars() {
      //First look through Eagers
      Iterator it = eagerEntries.values().iterator();
      while (it.hasNext()) {
         ClassPathEntry e = (ClassPathEntry) it.next();
         //First one wins
         String className = e.getMainClassName();
         if (className != null) return className;
      }

      //Now look through Lazy ones
      //if we don't know the main class yet we need to look through
      //the lazy entries also
      it = lazyEntries.values().iterator();
      while (it.hasNext()) {
         ClassPathEntry e = (ClassPathEntry) it.next();
         //First one wins
         String className = e.getMainClassName();
         if (className != null) return className;
      }

      return null;
   }

   /**
    * Get Permissions for a CodeSource
    */
   protected PermissionCollection getPermissions(CodeSource cs) {

      //Gets basic permissions
      PermissionCollection perms = super.getPermissions(cs);

      //Permission to listen for connections from local host
      perms.add(new java.net.SocketPermission("localhost:1024-", "listen"));
      //Permission to stop ones own thread
      perms.add(new RuntimePermission("stopThread"));

      //Permission to read properties
      perms.add(new PropertyPermission("java.version", "read"));
      perms.add(new PropertyPermission("java.vendor", "read"));
      perms.add(new PropertyPermission("java.vendor.url", "read"));
      perms.add(new PropertyPermission("java.class.version", "read"));
      perms.add(new PropertyPermission("os.name", "read"));
      perms.add(new PropertyPermission("os.version", "read"));
      perms.add(new PropertyPermission("os.acrch", "read"));
      perms.add(new PropertyPermission("file.separator", "read"));
      perms.add(new PropertyPermission("path.separator", "read"));
      perms.add(new PropertyPermission("line.separator", "read"));
      perms.add(new PropertyPermission("java.specification.version", "read"));
      perms.add(new PropertyPermission("java.specification.vendor", "read"));
      perms.add(new PropertyPermission("java.specification.name", "read"));
      perms.add(new PropertyPermission("java.vm.specification.version", "read"));
      perms.add(new PropertyPermission("java.vm.specification.vendor", "read"));
      perms.add(new PropertyPermission("java.vm.specification.name", "read"));
      perms.add(new PropertyPermission("java.vm.version", "read"));
      perms.add(new PropertyPermission("java.vm.vendor", "read"));
      perms.add(new PropertyPermission("java.vm.name", "read"));

      //Setup Permissions for the Real CodeSource location
      URL url = Boot.getCacheManager().convertFromCachedURL(cs.getLocation());

      try {
         //If a local file
         if (url.getProtocol().equals("file")) {
            String path = new File(url.getFile()).getAbsolutePath();
            if (path.endsWith(File.separator)) {
               path = path + "-";
            }
            perms.add(new FilePermission(path, "read"));

            //Now add Cached File to Permissions
            File cacheF = Boot.getCacheManager().getCachedFileForURL(url);
            path = cacheF.getAbsolutePath();
            if (path.endsWith(File.separator)) {
               path = path + "-";
            }
            perms.add(new FilePermission(path, "read"));
         } else {
            String host = url.getHost();
            if (host == null) {
               host = "localhost";
            }

            File cacheF = Boot.getCacheManager().getCachedFileForURL(url);
            String path = cacheF.getAbsolutePath();
            //remove _root_
            if (path.endsWith("_root_")) {
               path = path.substring(0, path.lastIndexOf(File.separator) + 1);
            }

            if (path.endsWith(File.separator)) {
               path = path + "-";
            }

            perms.add(new FilePermission(path, "read, write, delete"));

            //host url
            StringBuffer hostURL = new StringBuffer();
            hostURL.append(url.getProtocol());
            hostURL.append("://");
            hostURL.append(host);
            if (url.getPort() > -1) {
               hostURL.append(":");
               hostURL.append(url.getPort());
            }

            File hostCacheFile = Boot.getCacheManager().getCachedFileForURL(new URL(hostURL.toString()));
            path = hostCacheFile.getAbsolutePath();

            //remove _root_
            if (path.endsWith("_root_")) {
               path = path.substring(0, path.lastIndexOf(File.separator) + 1);
            }

            //permission granted to see anywhere under the host directory
            if (path.endsWith(File.separator)) {
               path = path + "-";
            }

            perms.add(new FilePermission(path, "read, write, delete"));

            //add permission for code to access codesource host
            perms.add(new SocketPermission(host, "connect,accept"));
         }
      }
      catch (Exception badURL) {
         logger.log(Level.SEVERE, badURL.getMessage(), badURL);
      }

      return perms;
   }

   /**
    * Get the ExecutableDesc used for this ClassLoader
    *
    * @return ExecutableDesc used to define this class loader
    */
   public ExecutableDesc getExecutableDesc() {
      return execDesc;
   }

   /**
    * Add a ServiceLoader to this services classpath
    *
    * @param serviceRef to add to a classloader for the specified service
    * @throws ServiceNotFoundException if the service can not be found
    */
   protected void addServiceRefLoader(ServiceDescStub serviceRef) throws ServiceNotFoundException {

      if (serviceRef.getName() == null) return;
      if (serviceRef.getName().equals("*")) {
         serviceLoaderRefs.addAll(ServiceClassLoader.getAllServiceLoaders());
      } else {
         serviceLoaderRefs.add(ServiceClassLoader.getServiceLoader(serviceRef));
      }
   }

   /**
    * Unpack Native Libraries from their Jar Containers
    */
   protected synchronized void unPackNatives() {

      //first check to see if we have already unpack any native libs
      if (natives_unpacked_flag) return;
      if (execDesc == null) {
         return;
      }

      NativeLibDesc nativeLibs[] = execDesc.getNativeLibs();

      //download all native libs
      boolean foundLib = false;
      List<URL> libsForOS = new ArrayList<URL>();
      for (NativeLibDesc nativeLib : nativeLibs) {
         if (nativeLib.getOS() != null && nativeLib.getOS().equals(NativeLibDesc.currentOS())) {
            libsForOS.add(nativeLib.getPath());
            foundLib = true;
         }
      }

      //if no libs found for this OS then just return
      if (!foundLib) return;

      List<CacheResource> resources = new ArrayList<CacheResource>();
      for (URL url : libsForOS) {
         resources.add(new CacheResource(url));
      }

      //download all the jars for this OS
      CacheManager cm = Boot.getCacheManager();
      cm.downloadResources(this.getExecutableDesc(), resources, cm.getDefaultListener(), true);

      //Unpack each jar
      for (CacheResource resource : resources) {
         unpackNativeJar(resource.getUrl());
      }

      natives_unpacked_flag = true;
   }

   /**
    * Unpack a native jar into the cache
    *
    * @param jarURL url of the jar
    */
   protected void unpackNativeJar(URL jarURL) {

      logger.info("unpacking native lib jar: " + jarURL);
      CacheManager cm = Boot.getCacheManager();
      try {
         File f = cm.getCachedFileForURL(jarURL);
         File dir = new File(f.getParentFile(), f.getName() + ".native");
         logger.fine("using native dir: " + dir.getAbsolutePath());
         if (!dir.exists()) {
            boolean success = dir.mkdir();
            if (!success) throw new RuntimeException("can't create directory:" + dir.getAbsolutePath());
         }

         JarFile jarFile = new JarFile(f);
         Enumeration entries = jarFile.entries();
         while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            File unpackedFile = new File(dir, entry.getName());

            //make parent directory if necessary
            if (!unpackedFile.getParentFile().exists()) {
               boolean success = unpackedFile.getParentFile().mkdirs();
               if (!success)
                  throw new RuntimeException("can't create directory:" + unpackedFile.getParentFile().getAbsolutePath());
            }

            //if not a directory then write out the file
            if (!entry.isDirectory()) {
               logger.log(Level.FINER, "unpacking file:" + unpackedFile.getCanonicalPath());
               //skip files that are up to date
               if (unpackedFile.length() == entry.getSize() && unpackedFile.lastModified() == entry.getTime())
                  continue;

               FileOutputStream out = null;
               InputStream in = null;
               try {
                  out = new FileOutputStream(unpackedFile);
                  in = jarFile.getInputStream(entry);
                  byte[] buf = new byte[1024];
                  int c = in.read(buf);
                  while (c != -1) {
                     out.write(buf, 0, c);
                     c = in.read(buf);
                  }
               }
               catch (IOException ioExp) {
                  logger.log(Level.SEVERE, ioExp.getMessage(), ioExp);
               }
               finally {
                  if (out != null) try {
                     out.close();
                  } catch (IOException ioExp) {
                     ioExp.printStackTrace();
                  }
                  if (in != null) try {
                     in.close();
                  } catch (IOException ioExp) {
                     ioExp.printStackTrace();
                  }
               }

               //Update modified time to that of entry
               boolean success = unpackedFile.setLastModified(entry.getTime());
               if (!success)
                  throw new RuntimeException("could not set last modified time of:" + unpackedFile.getAbsolutePath());
            }
         }
      }
      catch (MalformedURLException badURL) {
         logger.log(Level.SEVERE, badURL.getMessage(), badURL);
      }
      catch (IOException ioExp) {
         logger.log(Level.SEVERE, ioExp.getMessage(), ioExp);
      }

   }

   /**
    * Find Library. Returns the absolute path name of a native library. This will
    * cause Native Libraries to be downloaded and unpacked if necessary
    */
   protected String findLibrary(String libname) {

      if (isDestroyed()) {
         throw new IllegalStateException("Classloader for:" + execDesc.getName() + " has been destroyed");
      }

      logger.fine("looking for native library: " + libname);

      if (execDesc == null) {
         return super.findLibrary(libname);
      }

      //Look for libs in any of the native dirs in the cache for this resource
      NativeLibDesc nativeLibs[] = execDesc.getNativeLibs();

      //download all native libs
      try {
         CacheManager cm = Boot.getCacheManager();
         for (NativeLibDesc nativeLib : nativeLibs) {
            File jarFile = cm.getCachedFileForURL(nativeLib.getPath());
            File dir = new File(jarFile.getParentFile(), jarFile.getName() + ".native");
            File libFile = new File(dir, System.mapLibraryName(libname));
            if (libFile.exists()) {
               logger.info("Found Native Lib:" + libFile.toString());
               return libFile.getAbsolutePath();
            }
         }
      }
      catch (MalformedURLException badURL) {
         logger.log(Level.SEVERE, badURL.getMessage(), badURL);
      }

      //Couldn't find library
      logger.warning("Couldn't find native lib:" + libname + " for Application:" + execDesc.getName());
      return null;
   }

   /**
    * Download all Eager Resources
    */
   protected void downloadEagerResources() {

      synchronized (this) {
         if (eager_resources_started_flag) return;
      }

      //update the flag
      synchronized (this) {
         eager_resources_started_flag = true;
      }

      List<CacheResource> eagerResources = new ArrayList<CacheResource>();
      for (ClassPathEntry entry : classPath) {

         //check os of classpath Entry
         //skip this jar if it is not for this OS
         if (entry.getOs() != null && !entry.getOs().equals(NativeLibDesc.currentOS())) {
            logger.info("skipping non-os classpath entry: " + entry.getResourceURL());
            continue;
         }

         if (entry.getDownloadType() == ClassPathEntry.EAGER && (entry.isJar() || entry.isZip())) {
            eagerEntries.put(entry.getResourceURL(), entry);
            eagerResources.add(new CacheResource(entry.getResourceURL(), entry.getCachePolicy()));
         } else {
            lazyEntries.put(entry.getResourceURL(), entry);
         }
      }

      //Download the resources
      CacheManager cm = Boot.getCacheManager();
      cm.downloadResources(this.getExecutableDesc(), eagerResources, cm.getDefaultListener(), true);

      //Update downloaded status and initialize the jars
      Collection<ClassPathEntry> entries = eagerEntries.values();
      for (ClassPathEntry entry : entries) {
         entry.downloaded_flag = true;
         entry.initializeJar();
      }

      unPackNatives();
   }

   /**
    * Get a package name from a class name or null if no package
    */
   private static String getPackageName(String name) {
      if (name == null) return null;
      int i = name.lastIndexOf("");
      if (i <= 0) return null;
      else {
         return name.substring(0, i);
      }
   }

   /**
    * Get a package name from a resource name or null if no package
    */
   private static String getResourcePackageName(String name) {

      if (name == null) return null;
      int i = name.lastIndexOf("/");
      if (i <= 0) return null;
      else {
         return name.substring(0, i - 1);
      }
   }

   /**
    * Get Class File Name
    */
   protected static String getClassFileName(String name) {

      return name.replace('.', '/') + ".class";
   }

   /**
    * Load a Class from this ClassLoader by looking at each Resource and loading the class data
    * Eager Resources are checked first and then Lazy ones
    */
   protected Class findClassDirectly(String name) throws ClassNotFoundException {

      //First look at jars that have had Classes from the Same Package
      String packName = getPackageName(name);
      HashSet set = (HashSet) packageCache.get(packName);
      if (set == null) set = new HashSet();

      if (!set.isEmpty()) {
         try {
            return findClassInSet(name, set, false);
         }
         catch (ClassNotFoundException notFound) {
            //Just ignore class must not be in here
         }
      }

      //make sure eager resources are downloaded
      downloadEagerResources();

      //Next look in all Eager Resources minus the ones we just tried
      HashSet eagerSet = new HashSet();
      eagerSet.addAll(eagerEntries.values());
      if (!set.isEmpty()) {
         eagerSet.removeAll(set);
      }

      if (!eagerSet.isEmpty()) {
         try {
            return findClassInSet(name, eagerSet, true);
         }
         catch (ClassNotFoundException notFound) {
            //Just ignore class must not be in here
         }
      }

      //Now Finally Look in Lazy Resources
      HashSet lazySet = new HashSet();
      lazySet.addAll(lazyEntries.values());
      if (!set.isEmpty()) {
         lazySet.removeAll(set);
      }

      if (!lazySet.isEmpty()) {
         try {
            return findClassInSet(name, lazySet, true);
         }
         catch (ClassNotFoundException notFound) {
            //Just ignore class must not be in here
         }
      }

      //Ok we made it down here with out finding anything so throw an Exception
      throw new ClassNotFoundException(name);

   }

   /**
    * Find a Class in a ClassPath Entry Set
    *
    * @param name              of Class
    * @param entries           set of classpath entries
    * @param addToPackageCache true if package Cache should be updated
    */
   private Class findClassInSet(String name, Set<ClassPathEntry> entries, boolean addToPackageCache) throws ClassNotFoundException {

      //First look at jars that have had Classes from the Same Package
      String packName = getPackageName(name);
      Set<ClassPathEntry> set = packageCache.get(packName);
      if (set == null) set = new HashSet<ClassPathEntry>();

      for (ClassPathEntry entry : entries) {

         //Check to make sure the resource is downloaded
         entry.downloadResource();

         try {
            Class cls = entry.findClass(name, this);

            //we found a class so update package cache 
            set.add(entry);
            if (addToPackageCache) {
               packageCache.put(packName, set);
            }
            //define package if not defined yet
            if (!definedPackages.contains(packName)) {
               entry.definePackage(packName, this);
               definedPackages.add(packName);
            }
            return cls;
         }
         catch (ClassNotFoundException notFound) {
            //Just ignore class must not be in here
         }
      }

      //Couldn't find it so throw an Exception
      throw new ClassNotFoundException(name);

   }

   /**
    * Find a URL in a ClassPath Entry Set
    *
    * @param name              of resource
    * @param entries           set of ClassPathEntries
    * @param addToPackageCache true if package Cache should be updated
    */
   private URL findResourceInSet(String name, Set<ClassPathEntry> entries, boolean addToPackageCache) {

      //First look at jars that have had Classes from the Same Package
      String packName = getResourcePackageName(name);
      Set<ClassPathEntry> set = packageCache.get(packName);
      if (set == null) set = new HashSet<ClassPathEntry>();

      for (ClassPathEntry entry : entries) {

         //Check to make sure the resource is downloaded
         if (!entry.isDownloaded()) {
            entry.downloadResource();
         }

         URL url = entry.findResource(name);
         if (url != null) {
            //we found a class to update package cache 
            set.add(entry);
            if (addToPackageCache) {
               packageCache.put(packName, set);
            }
            return url;
         }
      }

      //Couldn't find it so return null
      return null;
   }

   /**
    * Find a Class in a Specific 
    * @param name of class
    * @param jarFile to search
    * @param originalURL of Jar File
    */
   /*
   private Class findClassInJar(String name, JarFile jarFile) throws ClassNotFoundException {
      
      String classFileName = getClassFileName(name);
      Enumeration entries = jarFile.entries();
      ZipEntry entry = null;
      
      //Look for entry in this Jar
      System.out.println("Reading Jar:");
      while(entries.hasMoreElements()) {
         entry = (ZipEntry)entries.nextElement();
         System.out.println(entry.getResourceName());
         if(entry.getResourceName().equals(classFileName)) {
            break;
         }
         
         entry = null;
      }
      
      //If no entry in jar for this name then throw exception
      if(entry == null) {
         throw new ClassNotFoundException(name);
      }
      
      //Extract the Class from the Jar
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
         Class cls = super.defineClass(name, data, 0, data.length);
         return cls;
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
    */

   /**
    * Load a Resource from this ClassLoader
    */
   public URL findResource(String name) {

      if (isDestroyed()) {
         throw new IllegalStateException("Classloader for:" + execDesc.getName() + " has been destroyed");
      }

      URL url;

      //First look at jars that have had Classes from the Same Package
      String packName = getResourcePackageName(name);
      HashSet set = (HashSet) packageCache.get(packName);
      if (set == null) set = new HashSet();

      if (!set.isEmpty()) {
         url = findResourceInSet(name, set, false);
         if (url != null) {
            return url;
         }
      }

      //Next look in all Eager Resources minus the ones we just tried
      HashSet eagerSet = new HashSet();
      eagerSet.addAll(eagerEntries.values());
      if (!set.isEmpty()) {
         eagerSet.removeAll(set);
      }

      if (!eagerSet.isEmpty()) {
         url = findResourceInSet(name, eagerSet, true);
         if (url != null) {
            return url;
         }
      }

      //Now Finally Look in Lazy Resources
      HashSet lazySet = new HashSet();
      lazySet.addAll(lazyEntries.values());
      if (!set.isEmpty()) {
         lazySet.removeAll(set);
      }

      if (!lazySet.isEmpty()) {
         url = findResourceInSet(name, lazySet, true);
         if (url != null) {
            return url;
         }
      }

      //Ok we made it down here with out finding anything so return null
      return null;
   }

   /**
    * Load a Class from this ClassLoader
    */
   public Class findClass(final String name) throws ClassNotFoundException {

      Class cls = null;

      //Class loader is destroyed
      if (isDestroyed()) {
         throw new IllegalStateException("Classloader for:" + execDesc.getName() + " has been destroyed");
      }

      if (name == null || name.equals("")) throw new ClassNotFoundException(null);

      //First look in our classes and System Classes
      try {
         cls = findFromSystemServiceLoaders(name);
      }
      catch (ClassNotFoundException notFound) {
         //Ok because we try the Referenced Service ClassesGlobalServiceClassLoader
      }

      //next look in our service Loaders
      if(cls == null) {
         try {
            cls = findFromServiceLoaders(name);
         }
         catch (ClassNotFoundException notFound) {
            //Ok now try the System class path
         }
      }

      //Now look in services appended to the classpath
      if(cls == null) {
         //Now look in our classpath
         Object result = AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               try {
                  return findClassDirectly(name);
               }
               catch (ClassNotFoundException t) {
                  return t;
               }
            }
         });

         if (result instanceof Class) {
            cls = (Class) result;
         } else if (result instanceof ClassNotFoundException) {
            throw (ClassNotFoundException) result;
         } else {
            throw new ClassNotFoundException(name);
         }
      }

      return cls;
   }

   /**
    * Load a class from all the service loaders
    */
   protected Class findFromServiceLoaders(String name) throws ClassNotFoundException {

      Iterator it = serviceLoaderRefs.iterator();
      while (it.hasNext()) {
         try {
            ServiceClassLoader loader = (ServiceClassLoader) it.next();
            String serviceName = loader.getService().getName();
            if (loader != this)
               return loader.loadClass(name);
         }
         catch (ClassNotFoundException notFound) {
            //This is ok just keep trying
         }
      }

      throw new ClassNotFoundException(name);
   }

   /**
    * Load from system service loaders
    */
   protected Class findFromSystemServiceLoaders(String name) throws ClassNotFoundException {

      Iterator it = ServiceClassLoader.systemServiceLoaders.iterator();
      while(it.hasNext()) {
         try {
            ServiceClassLoader loader = (ServiceClassLoader)it.next();
            
            if(loader != this)
               return loader.findClassDirectly(name);
         }
         catch(ClassNotFoundException notFound) {
            //This is ok just keep trying
         }
      }

      throw new ClassNotFoundException(name);
   }

   /**
    * Return true if this ClassLoader has been destroyed. Destroyed Class loaders
    * should no longer load classes
    */
   public synchronized boolean isDestroyed() {
      return destroyed_flag;
   }

   /**
    * Attempt to release any resources this Class Loader has Open
    */
   public synchronized void destroy() {

      logger.info("Destroying classloader for:" + toString());

      //Close all ClassPath Entries
      Iterator it = classPath.iterator();
      while (it.hasNext()) {
         ClassPathEntry entry = (ClassPathEntry) it.next();
         entry.close();
      }

      serviceLoaderRefs.clear();
      classPath.clear();
      eagerEntries.clear();
      lazyEntries.clear();
      packageCache.clear();

      destroyed_flag = true;
   }

   /**
    * Return a ClassPath String. Used for Logging etc
    */
   public String getClassPathString() {
      Iterator it = classPath.iterator();
      StringBuffer cp = new StringBuffer();
      while (it.hasNext()) {
         ClassPathEntry entry = (ClassPathEntry) it.next();
         cp.append(entry.getResourceURL().toString());
         cp.append("\n");
      }

      return cp.toString();
   }

   protected Class defineClassInternal(String name, byte[] b, int off, int len, CodeSource cs) {
      return super.defineClass(name, b, off, len, cs);
   }

   protected Package definePackage(String name, String defaultSpecTitle, String defaultSpecVersion, String defaultSpecVendor, String defaultImplTitle, String defaultImplVersion, String defaultImplVendor, URL sealBase) {
      return super.definePackage(name, defaultSpecTitle, defaultSpecVersion, defaultSpecVendor, defaultImplTitle, defaultImplVersion, defaultImplVendor, sealBase);
   }
}


