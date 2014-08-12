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

public final class MessageBus implements IMessageBus {

  private static MessageBus singleton;

  private HashMap topicIndex;
  private MsgBusProcessor busProcessor;

  public MessageBus() {
    topicIndex = new HashMap();
    busProcessor = new MsgBusProcessor();

  }

  /**
   * Get the Default Message Bus
   */
  public static synchronized IMessageBus getDefaultBus() {
    if(singleton == null) {
      singleton = new MessageBus();
    }

    return singleton;
  }

  /**
   * Remove a Listener
   */
  public void removeListener(String topic, IMsgBusListener listener) {
    //First find Topic
    MsgBusTopic _topic = (MsgBusTopic)topicIndex.get(topic);

    if(_topic != null) {
      //Remove from Parent Topics and Child Topics
      _topic.removeListener(listener);
    }
  }

  /**
   * The Listeners are added in a Soft Reference so that they can come and
   * go with out references being kept on them. This allows the objects to
   * be garbage collected
   *
   */
  public void addListener(String topic, IMsgBusListener listener) {
    //First find Topic
    MsgBusTopic _topic = (MsgBusTopic)topicIndex.get(topic);

    //If exists then add listener
    if(_topic != null && _topic.containsListener(listener) == false) {
      //Remove from Parent Topics and Child Topics
      removeFromRelatives(_topic.getName(), listener);
    }
    else if(_topic == null);
    {
      _topic = new MsgBusTopic(topic);

      //Now add it to the Index
      topicIndex.put(_topic.getName(), _topic);
    }

    _topic.addListener(listener);
  }

  /**
   * Remove listener from all parent topics and all children topics
   * This ensures that listeners won't get notified twice if they are listening
   * on more then one topic.
   */
  private void removeFromRelatives(String topic, IMsgBusListener listener) {
    Collection _topics = topicIndex.values();
    Iterator _iterator = _topics.iterator();

    while(_iterator.hasNext()) {
      MsgBusTopic _topic = (MsgBusTopic)_iterator.next();
      //Same topic so just skip
      if(topic.equals(_topic.getName())) continue;
      //This is a parent so remove the listener
      if(topic.startsWith(_topic.getName())) _topic.removeListener(listener);
      //This is a child so remove the listener
      if(_topic.getName().startsWith(topic)) _topic.removeListener(listener);
    }

  }

  /**
   * Fire a message onto the bus for the given Topic.
   * @param topic topic to pass the message on
   * @param pMsg message to pass
   */
  public void fireMessage(String topic, MsgBusMessage msg) {
    this.fireMessage(topic, msg, null);
  }

  /**
   * Fire a message onto the bus for the given Topic.
   * @param topic to pass the message on
   * @param message to pass
   * @param pResultListener listeners to return a result to
   */
  public void fireMessage(String topic, MsgBusMessage msg, IMsgBusListener resultListener) {
    //First find the Topic
    MsgBusTopic _topic = (MsgBusTopic)topicIndex.get(topic);
    if(_topic == null) {
      return;
    }

    busProcessor.addMessage(_topic, msg, resultListener);
  }

  /**
   * Fire a message onto the bus. For the given Topic. This method does not use the MessageBus Processor
   * and therefore it will block until all listeners have recieved the message.
   * The Result from Each Listener is merged into one result and returned to this methods caller
   * @param topic to pass the message on
   * @param message to pass
   * @param true if a result should be returned from this method
   * @return result of firing the message
   */
  public MsgBusMessage fireMessage(String topic, MsgBusMessage msg, boolean resultRequired) {
    return fireMessage(topic, msg, resultRequired, false);
  }

  /**
   * Fire a message onto the bus. For the given Topic. This method does not use the MessageBus Processor
   * and therefore it will block until all listeners have recieved the message.
   * The Result from Each Listener is merged into one result and returned to this methods caller
   * If pUnicast is set to true only the first Listener will receive the Message
   *
   * @param topic to pass the message on
   * @param msg to pass
   * @param pResultRequired true if a result should be returned from this method
   * @param pUnicast true if only first listener should receive the message
   * @return result of firing the message
   */
  public MsgBusMessage fireMessage(String topic, MsgBusMessage msg, boolean resultRequired, boolean unicast) {
    //First find the Topic
    MsgBusTopic _topic = (MsgBusTopic)topicIndex.get(topic);
    if(_topic == null) return null;

    MsgBusMessage _result = _topic.fire(msg, null, unicast);

    //Return Result if Requested
    if(resultRequired) {
      return _result;
    }
    else {
      return null;
    }
  }

}

