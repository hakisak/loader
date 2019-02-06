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
 * Used by URLStreamManager. This exception is thrown if a URLStreamHandler is installed for 
 * a protocol that already has a Handler.
 *
 * @author Deane Richan
 */
public class ProtocolSetException extends Exception {

  /**
   * Creates new <code>ProtocolSetException</code> without detail message.
   */
  public ProtocolSetException() {
  }

  /**
   * Constructs an <code>ProtocolSetException</code> with the specified detail message.
   * @param msg the detail message.
   */
  public ProtocolSetException(String msg) {
      super(msg);
  }
}


