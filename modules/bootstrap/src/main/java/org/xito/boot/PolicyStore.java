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

import java.util.*;
import java.util.logging.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;

/**
 * Interface for any PolicyStore implementation
 *
 * @author Deane Richan
 */
public interface PolicyStore {
   
   /**
    * Store a collection of Permissions for a specific Executable Descriptor
    */
   public void storePermissions(ExecutableDesc execDesc, PermissionCollection perms) throws PolicyStoreException;
   
   /**
    * Store a collection of Permissions for the Certification Path
    */
   public void storePermissions(X509Certificate cert, PermissionCollection perms) throws PolicyStoreException;
   
   /**
    * Get a set of permissions for a ExecutableDesc
    */
   public PermissionCollection getPermissions(ExecutableDesc execDesc);
   
   /**
    * Get a set of Permissions for the Certification Path
    */
   public PermissionCollection getPermissions(X509Certificate cert);
      
   /**
    * Get the KeyStore associated with this PolicyStore
    */
   public KeyStore getKeyStore()  throws KeyStoreException;
}