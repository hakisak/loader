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

package org.xito.launcher.jnlp;

import java.util.logging.*;
import org.xito.boot.*;
import org.xito.launcher.jnlp.xml.*;

/**
 *
 * @author  Deane
 */
public class JNLPClassLoader extends AppClassLoader {
   
   private static final Logger logger = Logger.getLogger(JNLPClassLoader.class.getName());
   private JNLPNode jnlpNode;
   
   public JNLPClassLoader(JNLPAppDesc appDesc, JNLPNode node, ClassLoader parent) throws ServiceNotFoundException {
      
      super(appDesc, parent);
      jnlpNode = node;
   }
   
   /**
    * Get the JNLPNode that Describes this JNLP App's XML
    */
   public JNLPNode getJNLPNode() {
      return getJNLPAppDesc().getJNLPNode();
   }
   
   /**
    * Get the JNLP App Descriptor used by this ClassLoader
    */
   public JNLPAppDesc getJNLPAppDesc() {
      return (JNLPAppDesc)super.execDesc;
   }
   
   /**
    * Load a Class from this ClassLoader. This overrides the super findClass so that
    * the JNLP App classes will be searched before the services etc.
    */
   
   /*
   public Class findClass(String name) throws ClassNotFoundException {
      
      if(isDestroyed()) {
         throw new IllegalStateException("Classloader for:"+execDesc.getResourceName()+" has been destroyed");
      }
      
      if(name == null || name.equals("")) throw new ClassNotFoundException(null);
      
      //First look in our classes
      try {
         return findClassDirectly(name);
      }
      catch(ClassNotFoundException notFound) {
         //Ok now try the System class path
      }
      
      //Next look in our classes and System Classes
      try {
         return findFromSystemServiceLoaders(name);
      }
      catch(ClassNotFoundException notFound) {
         //Ok because we try the Referenced Service ClassesGlobalServiceClassLoader
      }
      
      //finally look in our service Loaders
      return findFromServiceLoaders(name);
   }
    
    */
}
