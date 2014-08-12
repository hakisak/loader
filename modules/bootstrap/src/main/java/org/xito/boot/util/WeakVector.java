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

package org.xito.boot.util;

import java.util.*;
import java.lang.ref.*;

/**
 * Vector used to contain Weak References to objects
 *
 * @author Deane Richan
 */
public class WeakVector extends AbstractList {
   
   private Vector list;
   
   /** Creates a new instance of WeakVector */
   public WeakVector() {
      list = new Vector();
   }
   
   /**
    * Get an Object at a specified Index
    */
   public Object get(int index) {
      WeakReference ref = (WeakReference)list.get(index);
      
      if(ref !=  null) {
         if(ref.get() == null) {
            list.remove(ref);
         }
         
         return ref.get();
      }
      else {
         return null;   
      }
   }
   
   /**
    * Add an element at a specific index
    */
   public void add(int index, Object obj) {
      list.add(index, new WeakReference(obj));
      
   }
   
   /**
    * Get size of this Vector
    */
   public int size() {
      return list.size();
   }
   
   /**
    * Remove an Object
    */
   public boolean removeObject(Object obj) {
      int i = indexOfObject(obj);
      if(i>=0) {
         WeakReference ref = (WeakReference)super.remove(i);
         if(ref != null) {
            return true;
         }
      }
      
      return false;
   }
   
   public synchronized int indexOfObject(Object obj) {
      for(int i=0;i<size();i++) {
         Object obj2 = get(i);
         if(obj == obj2) {
            return i;
         }
      }
      
      return -1;
   }
   
   public synchronized boolean containsObject(Object obj) {
      int i = indexOfObject(obj);
      return (i>=0);
   }
}
