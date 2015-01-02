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

import java.io.*;
import java.net.*;
import org.w3c.dom.*;

/**
 * The BLXObject interface allows objects or components to be treated as reuseable net based Objects.
 * Objects that implement the BLXObject interface can be downloaded from the net at runtime, instantiated, and bound
 * together with other BLXObjects to form an application or interactive content.
 *
 * All classes that implement this interface should contain a no argument default constructor so that the object can 
 * be instaniated from a factory.
 *
 * @author $Author: drichan $
 * @version $Revision: 1.3 $
 * @since $Date: 2007/09/02 00:42:59 $
 */
public interface BLXObject
{
  public static final String FILE_EXT = ".blx";
  public static final String BLX_NS = "blx";
  public static final String BLX_DOCUMENT_NAME = "document";
  public static final String BLX_OBJ_NODE_NAME = "object";
  public static final String BLX_COMP_NODE_NAME = "component";
  public static final String BLX_FACTORY_NODE_NAME = "factory";
  public static final String BLX_FACTORY_TYPE_ATTR = "factory-type";
  public static final String BLX_EXTENSION_ATTR = "extension";
  public static final String BLX_EXT_HREF_ATTR = "ext-href";
  public static final String BLX_EXT_VERSION_ATTR = "version";
  public static final String BLX_CLASS_NAME_ATTR = "class";
  public static final String BLX_HREF_ATTR = "href";
  public static final String BLX_COMP_X_ATTR = "x";
  public static final String BLX_COMP_Y_ATTR = "y";
  public static final String BLX_COMP_WIDTH_ATTR = "width";
  public static final String BLX_COMP_HEIGHT_ATTR = "height";
  public static final String BLX_COMP_ID = "id";

  public static final int OBJECT_TYPE = 1;
  public static final int COMP_TYPE = 2;
  public static final int FACTORY_TYPE = 3;

  public static final String OBJECT_FACTORY_TYPE_STR = "object";
  public static final String COMPONENT_FACTORY_TYPE_STR = "component";

  public static final int OBJECT_FACTORY_TYPE = 10;
  public static final int COMPONENT_FACTORY_TYPE = 11;

  /**
   * Get the XML Data associated with this Object. The XML Data should be a single element
   * that this object uses to persist its state.  All nested BLXObjects data will also
   * be contained in this Data Element whether they are dirty or not
   * @return the XML Data Element for this Component
   */
  public Element getDataElement();
  
  /**
   * Store the BLX Object. This will store the objects entire child state or 
   * its nested children could use the optional IBLXStorageHandler 
   * to persist each of its children. 
   * @param allChildren true causes this object to call getDataElement on all its children false means 
   *   only dirty children
   * @param storageHandler child objects can optionally have their state stored in seperate
   *  documents using a Storage handler. 
   * @return the XML Data Element for this Component
   */
  public void store(boolean allChildren, BLXStorageHandler storageHandler) throws IOException;

  /**
   * Get the BLX Element for this Component or Object
   * @return the BLXElement object that describes this type of Component.
   */
  public BLXElement getBLXElement();

  /**
   * Set the BLX Element for this Component or Object
   * This should only be called when the object is first being created. Which
   * would normally be directly after the default constructor has been called.
   * @param pElement for this
   */
  public void setBLXElement(BLXElement pElement);

  /**
   * Return true if this components state has changed in a way that
   * Requires the objects container to fetch new XML Data for the Object.
   * @return true if component has changed
   */
  public boolean isDirty();
  
  /**
   * Get the BLX Object instance ID for this object or null if the object does not have
   * an Id.  A BLX ID can be used through the system like a variable name. The id should be unique all though
   * it is possible for a scope to be created where names may be reused.
   * @return id
   */
  public String getBLXId();
    
}

