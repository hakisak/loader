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

import java.net.*;
import java.util.logging.*;

import org.xito.boot.ServiceClassLoader;

/**
 * The BLXHelper class can be used by BLXObject implementations to simplify the creation of
 * BLXElements and Extension management. The Helper class can also generate simple
 * IDs used by the BLX environment.
 *
 * @author $Author: drichan $
 * @version $Revision: 1.5 $
 * @since $Date: 2007/09/02 00:42:59 $
 */
public class BLXHelper {
   
   private Logger logger = Logger.getLogger(BLXHelper.class.getName());
   
   protected String serviceName;
   protected BLXExtension extension;
   protected String id;
   protected URL contextURL;
   protected String objClassName;
   
   /**
    * Create a new BLX Helper for a BLXObject with no ID
    * @param blx Object that this helper is used for
    */
   public BLXHelper(BLXObject blxObject) {
      this(blxObject, null);
   }
   
   /**
    * Create a new BLX Helper for a BLXObject with an ID
    * @param blx Object that this helper is used for
    * @param id to use for this object
    */
   public BLXHelper(BLXObject blxObject, String id) {
      
      //Setup  extension info
      objClassName = blxObject.getClass().getName();

      ClassLoader loader = blxObject.getClass().getClassLoader();
      if(loader instanceof BLXExtClassLoader) {
         extension = ((BLXExtClassLoader)loader).getExtension();
         String extName = ((BLXExtClassLoader)blxObject.getClass().getClassLoader()).getExtension().getName();
         String extVersion = ((BLXExtClassLoader)blxObject.getClass().getClassLoader()).getExtension().getVersion();
         String extHREF = ((BLXExtClassLoader)blxObject.getClass().getClassLoader()).getExtension().getHREF();
      }
      else if(loader instanceof ServiceClassLoader) {
         serviceName = ((ServiceClassLoader)loader).getService().getName();
      }
      else {
         logger.log(Level.WARNING, "Not using BLXExtClassLoader or ServiceClassLoader. BLX Persistance will not work correctly!");
      }
      
      //Use Generated ID
      if(id == null || id.length() == 0) {
         this.id =  generateID(blxObject);
      }
      else {
         this.id = id;
      }
   }
   
   /**
    * Get an ID for a BLX Object
    * @return id
    */
   public String getBLXId() {
      
      return id;
   }
   
   /**
    * Get the BLX Element for this Helper's BLX Object
    * @return BLXElement
    */
   public BLXElement getBLXObjectElement() {
      
      BLXElement blxElement = null;
      if(extension != null) {
         blxElement = new BLXElement(BLXElement.OBJECT_TYPE, extension, objClassName, id);
      }
      else {
         blxElement = new BLXElement(BLXElement.OBJECT_TYPE, serviceName, objClassName, id);
      }
      
      blxElement.setContextURL(contextURL);
      
      return blxElement;
   }
   
   /**
    * Get the BLX Element for this Helper's BLX Component
    * @param x location of Component
    * @param y location of Component
    * @param width of Component
    * @param height of Component
    * @return BLXElement
    */
   public BLXElement getBLXCompElement(int x, int y, int width, int height) {
      
      BLXElement blxElement = null;
      if(extension != null) {
         blxElement = new BLXElement(BLXElement.COMP_TYPE, extension, objClassName, id);
      }
      else {
         blxElement = new BLXElement(BLXElement.COMP_TYPE, serviceName, objClassName, id);
      }
      
      blxElement.setContextURL(contextURL);
      blxElement.setX(x);
      blxElement.setY(y);
      blxElement.setWidth(width);
      blxElement.setHeight(height);
      
      return blxElement;
   }
   
   /**
    * Set the BLXElement for the object that this helper is managing
    *
    */
   public void setBLXElement(BLXElement blxElement) {
      
      contextURL = blxElement.getContextURL();
      
      //Set extension Info
      extension = blxElement.getExtension();
      String _id = blxElement.getID();
      if((_id != null) && (_id.length()>0)) this.id = _id;
   }
   
   /**
    * Get the ContextURL the BLX Object was loaded using
    */
   public URL getContextURL() {
      return contextURL;
   }
   
   /**
    * Generate a Unique Component ID for a specified object instance. Calling this method multiple
    * times using the same object may generate the same ID
    * @param object id is to be generated for
    * @return String id
    */
   public static String generateID(Object object) {
      String _clsName = object.getClass().getName();
      int _index = _clsName.lastIndexOf(".");
      _clsName = (_index == -1)?_clsName:_clsName.substring(_index+1);
      String id = _clsName +"_"+System.currentTimeMillis()+ "_" + object.hashCode();
      
      return id;
   }
   
}


