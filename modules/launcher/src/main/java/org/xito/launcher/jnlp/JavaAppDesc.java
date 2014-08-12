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

import java.io.*;
import java.net.*;
import java.util.*;

import org.xito.boot.*;
import org.xito.launcher.*;
import org.xito.launcher.jnlp.xml.*;

/**
 *
 * @author  Deane Richan
 */
public class JavaAppDesc extends AppDesc implements LaunchDesc {

   private boolean useWebStart_flag = false;
      
   private String externalType = "java";
   protected String unique_id = null;
   private ArrayList javaArchives = new ArrayList();
   private ArrayList nativeArchives = new ArrayList();
   
   /**
    * Creates a new instance of JNLPAppDesc
    */
   public JavaAppDesc() { 
      setUniqueID(""+hashCode()+"-"+System.currentTimeMillis());
   }
   
   /**
    * Get the Unique ID of this Desc
    */
   public String getUniqueID() {
      return unique_id;
   }
   
   /**
    * Set the Unique ID of this Desc
    */
   public void setUniqueID(String id) {
      unique_id = id;
   }
   
   public void setName(String name) {
      super.setName(name);
   }
   
   public String getName() {
      return super.name;
   }
      
   public void setTitle(String title) {
      this.setDisplayName(title);
   }
   
   public String getTitle() {
      return this.displayName;
   }
      
   /**
    * Set the Java Archives. This is a list of URLs for Jar Resources
    */
   public void setJavaArchives(List list) {
      javaArchives.clear();
      javaArchives.addAll(list);
      
      clearClassPath();
      Iterator it = javaArchives.iterator();
      while(it.hasNext()) {
         URL url = (URL)it.next();
         ClassPathEntry entry = new ClassPathEntry(url);
                    
         addClassPathEntry(entry);
      }
   }
   
   /**
    * Get Java Archives
    */
   public List getJavaArchives() {
      return (List)new ArrayList(javaArchives);
   }
   
   /**
    * Set the Native Archives. This is a list of NativeLibDesc
    */
   public void setNativeArchives(List list) {
      nativeArchives.clear();
      nativeArchives.addAll(list);
      
      super.clearNativeResources();
      Iterator it = nativeArchives.iterator();
      while(it.hasNext()) {
         super.addNativeLib((NativeLibDesc)it.next());
      }
   }
   
   /**
    * Get Native Archives
    */
   public List getNativeArchives() {
      return (List)new ArrayList(nativeArchives);
   }
   
   /**
    * Get the External Type for this Descriptor
    */
   public String getExternalType() {
      return this.externalType;
   }
   
   /**
    * Read in File Data and for this Desc. This is overrides the method in org.xito.boot.AppDesc so that
    * External Launch files can be read correctly by this classes class loader
    */
   public static AppDesc readFileData(byte[] data) throws IOException, ClassNotFoundException {
   
      ByteArrayInputStream in = new ByteArrayInputStream(data);
      ObjectInputStream objIn = new ObjectInputStream(in);
      Object obj = objIn.readObject();
      
      try {
         return (AppDesc)obj;
      }
      catch(ClassCastException badCast) {
         throw new IOException(badCast.getMessage());
      }
   }
   
   /**
    * Return true if the App should be launched by WebStart
    */
   public boolean useWebStart() {
      return useWebStart_flag;
   }
   
   /**
    * Set to true if the JNLP app should be launched by WebStart in a new VM
    */
   public void setUseWebStart(boolean b) {
      useWebStart_flag = b;
   }
}
