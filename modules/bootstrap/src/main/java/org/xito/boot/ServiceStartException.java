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

/**
 * This exception is thrown when a Service application fails to Startup without throwing a Runtime Exception or if 
 * The applications main method throws an Exception.
 *
 * @author  Deane Richan
 * @version $revision$
 */
public class ServiceStartException extends java.lang.Exception {
   
   private ServiceDesc service;
   
   /**
    * Creates a new instance of <code>ServiceStartException</code> with detail message and root cause.
    */
   public ServiceStartException(ServiceDesc service, String msg, Throwable exp) {
      super(msg, exp);
      this.service = service;
   }
   
   /**
    * Creates a new instance of <code>ServiceStartException</code> with detail message and root cause.
    */
   public ServiceStartException(String msg, Throwable exp) {
      super(msg, exp);
   }
   
   /**
    * Creates a new instance of <code>ServiceStartException</code> with detail message.
    */
   public ServiceStartException(String msg) {
      super(msg);
   }
   
   /**
    * Get the Service that was starting when this exception occured
    */
   public ServiceDesc getService() {
      return service;
   }
   
}
