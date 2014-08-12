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

package org.xito.msgbus;

import java.lang.ref.SoftReference;
import java.util.Hashtable;


public class MsgBusMessage {
  SoftReference sourceRef;
  Hashtable parameters;
  boolean resultRequested = false;

  /**
   * Creates a new MsgBusMessage
   * @param a Soft Reference is created for the Source object.
   * @param pParams contains the parameters that should be passed to a listener
   */
  public MsgBusMessage(Object source, Hashtable params) {
    sourceRef = new SoftReference(source);
    parameters = params;
  }

  /**
   * Creates a new MsgBusMessage
   * @param an Object that is the source of this Message
   */
  public MsgBusMessage(Object source) {
    sourceRef = new SoftReference(source);

  }

  /**
   * Creates a new MsgBusMessage
   * @param a Soft Reference is created for the Source object.
   * @param pParams contains the parameters that should be passed to a listener
   * @param true if the source is requesting a result
   */
  public MsgBusMessage(Object source, Hashtable params, boolean resultRequested) {
    this(source, params);
    this.resultRequested = resultRequested;
  }

  /**
   * returns the source of this event
   * @return soruce
   */
  public Object getSource() {
    return sourceRef.get();
  }

  /**
   * Get the Parameters passed with this Event
   * @return parameters
   */
  public Hashtable getParameters() {
    return parameters;
  }

  /**
   * Puts a value into the parameters object
   * @param name of value
   * @param value of parameter
   */
  public void put(String name, Object value) {
    if(parameters == null) parameters = new Hashtable();
    parameters.put(name, value);

  }

  /**
   * Gets a value from the parameters object
   * @param name of value
   * @return value of parameter
   */
  public Object get(String name) {
    if(parameters == null) return null;
    return parameters.get(name);

  }

  /**
   * @return true if the source of this event has requested a result.
   */
  public boolean resultWasRequested() {
    return resultRequested;
  }


}
