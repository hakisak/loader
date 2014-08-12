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

import java.util.*;
import java.lang.ref.*;

public interface IMessageBus {

  /** Character used to seperate Topic Names */
  public static final char TOPIC_SEP = '.';

  /**
   * Add a Listener to the Bus that listens for Messages
   * delivered on a specified Topic
   * @param topic name of topic
   * @param listener
   */
  public void addListener(String topic, IMsgBusListener listener);

  /**
   * Remove a Listener
   */
  public void removeListener(String topic, IMsgBusListener listener);

  /**
   * Fire a message onto the Bus
   * This call returns quickly the implementation should process the msg
   * in a seperate thread.
   * @param topic name of Topic
   * @param msg message to Send
   */
  public void fireMessage(String topic, MsgBusMessage msg);

  /**
   * Fire a Message and notify the result Listener
   * This call returns quickly the implementation should process the msg
   * in a seperate thread. The MessageBus Listener that acts on this message should send a
   * reply to the Reply Listener.
   * @param topic
   * @param msg to send
   * @param listener that should receive a reply
   */
  public void fireMessage(String topic, MsgBusMessage msg, IMsgBusListener resultListener);

  /**
   * Fire a message onto the bus. For the given Topic. This method does not use the MessageBus Processor
   * and therefore it will block until all listeners have recieved the message.
   * The Result from Each Listener is merged into one result and returned to this methods caller.
   * @param topic to pass the message on
   * @param msg to pass
   * @param resultRequired true if a result should be returned from this method
   * @return result of firing the message
   */
  public MsgBusMessage fireMessage(String topic, MsgBusMessage msg, boolean resultRequired);

  /**
   * Fire a message onto the bus. For the given Topic. This method does not use the MessageBus Processor
   * and therefore it will block until all listeners have recieved the message.
   * The Result from Each Listener is merged into one result and returned to this methods caller
   * If unicast is set to true only the first Listener will receive the Message
   *
   * @param topic to pass the message on
   * @param msg to pass
   * @param resultRequired true if a result should be returned from this method
   * @param unicast true if only first listener should receive the message
   * @return result of firing the message
   */
  public MsgBusMessage fireMessage(String topic, MsgBusMessage msg, boolean resultRequired, boolean unicast);

}

