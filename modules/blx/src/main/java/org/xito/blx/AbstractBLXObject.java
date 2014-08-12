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

import java.net.URL;

/**
 * Abstract BLX Object can be used to easily create a subclass that implement BLXObject.
 *
 * This class has a no-op implementation of store that will do nothing. If your object wishes to store
 * its state subclasses should override the store method.
 *
 * @author  Deane
 */
public abstract class AbstractBLXObject implements BLXObject {
   
   protected BLXHelper blxHelper;
   
   /** 
    * Create Abstract BLX Object with default ID
    */
   public AbstractBLXObject() {
      blxHelper = new BLXHelper(this);
   }
   
   /** 
    * Create Abstract BLX Object with specified ID or null for default
    * @param id of blx object
    */
   public AbstractBLXObject(String id) {
      blxHelper = new BLXHelper(this, id);
   }
   
   public BLXElement getBLXElement() {
      return blxHelper.getBLXObjectElement();
   }
   
   public String getBLXId() {
      return blxHelper.getBLXId();
   }
   
   public abstract org.w3c.dom.Element getDataElement();
   
   public abstract boolean isDirty();
   
   public void setBLXElement(BLXElement blxElement) {
      blxHelper.setBLXElement(blxElement);
   }
   
   public void store(boolean allChildren, BLXStorageHandler storageHandler) throws java.io.IOException {
   }
   
   protected URL getContextURL() {
      return blxHelper.getContextURL();
   }
}
