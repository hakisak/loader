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

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.List;
import java.util.logging.*;

import org.xito.boot.util.*;

/**
 * Represents a Running application. AppInstance maintains the Applications ClassLoader, ThreadGroup
 * and Window List
 *
 * @author  Deane Richan
 */
public class AppInstance {

   private static Logger logger = Logger.getLogger(AppInstance.class.getName());
   
   private AppClassLoader loader = null;
   private ThreadGroup group = null;
   private boolean destoyed = false;
   private WeakVector windows = new WeakVector();
      
   /**
    * Creates new App Instance
    */
   public AppInstance(AppClassLoader loader) {
      this.loader = loader;
      loader.setAppInstance(this);
      
      group = Thread.currentThread().getThreadGroup();
   }
   
   /**
    * Get the ThreadGroup
    *
    * @throws IllegalStateException 
    */
   public ThreadGroup getThreadGroup() throws IllegalStateException {
      if (destoyed) throw new IllegalStateException();
      
      return group;
   }
   
   /**
    * Get the AppClassLoader
    */
   public AppClassLoader getClassLoader() {
      
      return loader;
   }
   
   /**
    * Return true if not stopped
    */
   public boolean isRunning() {
      return !destoyed;
   }
   
   /**
    * Get Application Desc
    */
   public AppDesc getAppDesc() {
      return loader.getAppDesc();
   }
   
   public void addWindow(Window w) {
      windows.add(w);
   }
   
   protected WeakVector getWindows() {
      return windows;
   }
   
   /**
    * Stop the application and destroy its resources.
    */
   public void destroy() {
      if (destoyed) return;
      destoyed = true;
      
      try {
         // destroy resources
         Iterator it = windows.iterator();
         while(it.hasNext()) {
            Window w = (Window)it.next();
            if (w != null) w.dispose();
         }
         
         // interrupt all threads
         Thread threads[] = new Thread[ group.activeCount() * 2 ];
         int nthreads = group.enumerate(threads);
         for (int i=0; i < nthreads; i++) {
            //Skip AWT-EventQueue Threads
            if(threads[i].getName().startsWith("AWT-EventQueue")) {
               continue;
            }
            logger.log(Level.INFO, "Interrupt thread: "+threads[i]);
            threads[i].interrupt();
         }
         
         // then stop
         Thread.yield();
         nthreads = group.enumerate(threads);
         for (int i=0; i < nthreads; i++) {
            //Skip AWT-EventQueue Threads
            if(threads[i].getName().startsWith("AWT-EventQueue")) {
               continue;
            }
            logger.log(Level.INFO, "Stop thread: "+threads[i]);
            //We know this is depracated, but we don't have any good options in this case
            //threads[i].stop();
            logger.log(Level.SEVERE, "Can't Stop thread: "+threads[i]);
         }
         
         //Signal classloader to destroy
         loader.destroy();
     }
     catch(Throwable t) {
        t.printStackTrace();
     }
     finally {
        loader = null;
        group = null;
        System.gc();
        logger.info("Free Memory:"+Runtime.getRuntime().freeMemory());
        logger.info("Total Memory:"+Runtime.getRuntime().totalMemory());
        long usedMemory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        logger.info("Memory in Use:"+(usedMemory/1000000)+" megs");
     }
   }
}
