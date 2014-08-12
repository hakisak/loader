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

public class MsgBusProcessor {
  protected Vector msgPool;
  protected ProcessorThread thread;

  public MsgBusProcessor() {
    msgPool = new Vector();
    thread = new ProcessorThread();
    //Start Processing
    thread.start();
  }

  /**
   * Add Message to the Pool to be delivered
   */
  public synchronized void addMessage(MsgBusTopic topic, MsgBusMessage msg, IMsgBusListener resultListener) {
    //Add the message and topic to the Message Pool
    msgPool.addElement(new Bundle(topic, msg, resultListener));

    //Notify any blocked threads that there is a Message Now
    notify();
  }

  /**
   * Returns the next Bundle to Deliver
   * This method blocks until a Bundle is ready
   * @return bundle
   */
  protected synchronized Bundle getNextBundle() {
    //If we don't have any messages then cause the caller to wait
    try {
      if(msgPool.size() == 0) wait();
    }
    catch(InterruptedException _exp) {
      _exp.printStackTrace();
    }

    //Get the next bundle to process
    Bundle _next = (Bundle)msgPool.firstElement();

    //Remove the first Element
    msgPool.removeElementAt(0);

    return _next;
  }

  /**
   * Inner class used to bundle a Topic and a Msg together
   */
  private class Bundle {
    MsgBusMessage message = null;
    MsgBusTopic topic = null;
    IMsgBusListener resultListener = null;

    public Bundle(MsgBusTopic topic, MsgBusMessage msg, IMsgBusListener resultListener) {
      message = msg;
      this.topic = topic;
      this.resultListener = resultListener;
    }
  }

  /**
   * Inner Class used as the Thread to process the msg Pool
   *
   */
  private class ProcessorThread extends Thread {
    /**
     * The run method of this thread processes all messages waiting to be delivered
     */
    public void run() {
      //Just loop getting messages to deliver
      while(true) {
        try {
          //Get the next bundle to process
          //This method blocks until a Bundle is ready
          Bundle _next = getNextBundle();

          //Fire message
          _next.topic.fire(_next.message, _next.resultListener, false);
        }
        catch(Throwable _exp) {
          //Required so that Null Pointers and such don't kill Thread
          _exp.printStackTrace();
        }

      }
    }
  }

}


