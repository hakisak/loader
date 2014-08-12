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

import java.net.*;
import java.util.*;

/**
 * URLStreamManager can be installed into the VM as the main URLStreamHandlerFactory
 * Executing classes can then call 
 *
 * @author  Deane Richan
 * @version $revions$
 */
public class URLStreamManager implements URLStreamHandlerFactory {
   
   private static URLStreamManager singleton;
   private HashMap protocols;
   
   /** Creates new URLStreamManager */
   private URLStreamManager(){
      protocols = new HashMap();
   }
   
   /**
    * Main Entrance for Service
    */
   public static void main(String args[]) {
      singleton = new URLStreamManager();
      
      //Install self as the URLHandlerFactory
      try {
         URL.setURLStreamHandlerFactory(singleton);
      }
      catch(Error _error) {
         _error.printStackTrace();
      }
      catch(SecurityException _exp) {
         _exp.printStackTrace();
      }
      
      //org.xito.shell.Shell.getShell().endSession(true);
   }
   
   /**
    * Get the DefaultStream Manager or null if the service hasn't been started
    */
   public static URLStreamManager getDefaultManager() {
      return singleton;
   }
   
   /**
    * Add a URLStreamHandler for the specifed Protocol.
    * @param protocol to add a handler for
    * @param handler is the Handler that will manage the protocol
    * @throws ProtocolAddException if the protocol already has a handler defined
    */
   public void addProtocolHandler(String protocol, URLStreamHandler handler) throws ProtocolSetException {
      if(protocols.containsKey(protocol)) throw new ProtocolSetException("Protocol Handler for: "+ protocol +" already set");
      
      protocols.put(protocol, handler);
   }
   
   /**
    * Create a URLStreamHandler for the specified protocol
    * @returns the URLStreamHandler for the protocol or null if the Handler could not be created
    */
   public URLStreamHandler createURLStreamHandler(String protocol) {
      return (URLStreamHandler)protocols.get(protocol);
   }
   
}
