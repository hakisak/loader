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
 * This Exception is thrown when a Specified Service can not found during Service ClassLoading
 *
 * @author  Deane Richan
 */
public class ServiceNotFoundException extends java.lang.Exception {
   
   /**
    * Creates a new instance of <code>ServiceNotFoundException</code> without detail message.
    */
   public ServiceNotFoundException() {
   }
   
   
   /**
    * Constructs an instance of <code>ServiceNotFoundException</code> with the specified detail message.
    * @param msg the detail message.
    */
   public ServiceNotFoundException(String msg) {
      super(msg);
   }
   
   /**
    * Constructs an instance of <code>ServiceNotFoundException</code> with the specified detail message.
    * @param msg the detail message.
    */
   public ServiceNotFoundException(String msg, Throwable cause) {
      super(msg, cause);
   }
}
