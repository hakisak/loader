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

package org.xito.reflect;

/**
 * The ReflectException is a wrapper class for InstantiationException, 
 * IllegalAccessException, IllegalArgumentException, and 
 * NoSuchMethodException.
 *
 * This method is thrown by methods in the ReflectionClass and simplifies the 
 * catching of exceptions.
 *
 * The nested exception can be obtained by calling getNestedException.
 *
 *
 * @author  drichan
 */
public class ReflectException extends Exception {
  
  /**
   * Creates a new instance of <code>ReflectException</code> without detail message.
   * @param msg the detail message
   */
  public ReflectException(String msg) {
    super(msg);
  }
  
  /**
   * Constructs an instance of <code>ReflectException</code> with the specified detail message.
   * @param exp Exception that trigged this exception
   */
  public ReflectException(Throwable exp) {
    super(exp.getMessage(), exp);
  }
}
