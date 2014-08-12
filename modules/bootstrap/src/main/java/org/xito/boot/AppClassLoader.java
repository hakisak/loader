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
import java.security.*;
import java.lang.reflect.*;

/**
 * Classloader that loads an application. Sub classes can extend this class loader to specify exactly
 * how this classloader will load classes etc.
 *
 * @author Deane Richan
 */
public class AppClassLoader extends CacheClassLoader {
   
   protected URLClassLoader delegateLoader;
   protected AppInstance application;
           
   public AppClassLoader(AppDesc appDesc, ClassLoader parent ) throws ServiceNotFoundException {
      //Use a CacheClassLoader to load the classes
      super(appDesc, parent);
   }
   
   public AppDesc getAppDesc() {
      return (AppDesc)execDesc;
   }
         
   public AppInstance getAppInstance() {
      return application;
   }
   
   public synchronized void setAppInstance(AppInstance app) {
      if(application == null) application = app;
   }
}


