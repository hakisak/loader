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

import java.util.*;
import java.util.logging.*;
import java.net.*;
import org.xito.boot.*;

/**
 *
 * @author  Deane Richan
 */
public class BLXExtClassLoader extends CacheClassLoader {
   
   public static Logger logger = Logger.getLogger(BLXExtClassLoader.class.getName());
     
   private Vector extensionLoaders;
   
   /** Creates a new instance of BLXExtClassLoader */
   public BLXExtClassLoader(BLXExtension ext) throws ServiceNotFoundException, ExtensionLoadException {
      super(ext, BLXExtClassLoader.class.getClassLoader());
   }
   
   /** Creates a new instance of BLXExtClassLoader */
   public BLXExtClassLoader(BLXExtension ext, ServiceClassLoader serviceLoader) throws ServiceNotFoundException, ExtensionLoadException {
      super(ext, serviceLoader);
   }
   
   protected void addExtensionRefLoader(String extRef) throws ExtensionLoadException {
      if(extensionLoaders == null) extensionLoaders = new Vector();
      
      try {
         URL extURL = new URL(getExtension().getContextURL(), extRef);
         extensionLoaders.add(BLXExtManager.getInstance().getExtension(extURL).getExtClassLoader());
      }
      catch(MalformedURLException badURL) {
         throw new ExtensionLoadException(badURL.getMessage(), badURL);
      }
   }
   
   public BLXExtension getExtension() {
      return (BLXExtension)super.execDesc;
   }
   
   /**
    * Load a Class from this ClassLoader
    */
   public Class findClass(String name) throws ClassNotFoundException {
      
      if(isDestroyed()) {
         throw new IllegalStateException("Classloader for:"+execDesc.getName()+" has been destroyed");
      }
      
      if(name == null || name.equals("")) throw new ClassNotFoundException(null);
      
      //First look in our classes and System Classes
      try {
         return findFromSystemServiceLoaders(name);
      }
      catch(ClassNotFoundException notFound) {
         //Ok because we try the Referenced Service ClassesGlobalServiceClassLoader
      }
      
      //next look in our service Loaders
      try {
         return findFromServiceLoaders(name);
      }
      catch(ClassNotFoundException notFound) {
         //Ok now try the System class path
      }
      
      //look in our extension references for the class
      try {
         return findFromExtLoaders(name);
      }
      catch(ClassNotFoundException notFound) {
         //This is Ok because we will try the GlobalServiceClassLoader
      }
      
      //Now look in our classpath
      return findClassDirectly(name);
     
   }
   
   /**
    * Load a class from all the extension-ref loaders
    */
   protected Class findFromExtLoaders(String name) throws ClassNotFoundException {
      
      if(extensionLoaders != null) {
         Iterator it = extensionLoaders.iterator();
         while(it.hasNext()) {
            try {
               BLXExtClassLoader loader = (BLXExtClassLoader)it.next();
               if(loader != this)
                  return loader.findClass(name);
            }
            catch(ClassNotFoundException notFound) {
               //This is ok just keep trying
            }
         }
      }
      
      throw new ClassNotFoundException(name);
   }
}
