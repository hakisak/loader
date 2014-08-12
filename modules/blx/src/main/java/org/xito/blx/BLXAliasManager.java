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

/**
 *
 * @author  Deane
 */
public class BLXAliasManager {
   
   private BLXAliasManager parent = null;
   private Hashtable extensions = new Hashtable();
   
   /** Creates a new instance of BLXExtContext */
   public BLXAliasManager(BLXAliasManager parent) {
      this.parent = parent;
   }
   
   public void addExtAlias(BLXExtAliasElement aliasElement) {
      extensions.put(aliasElement.getAliasName(), aliasElement);
   }
   
   public Map getExtAliases() {
      return new HashMap(extensions);
   }
   
   public BLXExtension findExtension(String extAlias) throws ExtensionLoadException {
      BLXExtAliasElement alias = (BLXExtAliasElement)extensions.get(extAlias);
      if(alias == null) return null;
      
      if(alias.getExtURL() != null) {
         return BLXExtManager.getInstance().getExtension(alias.getExtURL());
      }
      else if(alias.getServiceName() != null) {
         return BLXExtManager.getInstance().getExtensionFromService(alias.getServiceName());
      }
      
      throw new ExtensionLoadException("Could not load unknown Extension for ext-alias:"+extAlias);
   }
}
