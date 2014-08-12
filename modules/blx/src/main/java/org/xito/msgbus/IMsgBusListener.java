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


public interface IMsgBusListener {

  /**
   * This method is called when an MessageBus Listener posts a result
   * back on the MessageBus. When a caller of the MessageBus calls fireMessage
   * it can optionally pass an instance of a IMsgBusListener to be called
   * when the result of that message occurs. The passed message listener will
   * recieve the result message when it is passed back from the receiving listeners.
   * @param topic that the result returned from
   * @param msg result information that returned to that topic
   */
  public void resultPosted(String topic, MsgBusMessage msg);

  /**
   * This method is called by the Message Bus when an topic that this
   * Listener has subscribed to recieves an Event
   * @param topic that the message occured on
   * @param msg information that was passed to that topic
   * @return result of processing this event.
   */
  public MsgBusMessage messagePosted(String topic, MsgBusMessage msg);

}

