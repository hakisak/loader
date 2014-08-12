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

/**
 * MsgBusTopic
 * Description:
 *
 */
public class MsgBusTopic
{
  private String topicName;
  private HashSet listeners;

  /**
   *
   *
   */
  public MsgBusTopic(String pFullName)
  {
    topicName = pFullName;
    listeners = new HashSet();
  }

  /**
   *
   *
   */
  public MsgBusTopic(String pParentName, String pName)
  {
    this(pParentName + '.' + pName);
  }

  /**
   *
   *
   */
  public synchronized void addListener(IMsgBusListener pListener)
  {
    //If it already exists don't add 1it but move it to First Position
    if(listeners.contains(pListener) == false)
    {
      listeners.add(pListener);
    }
  }

  /**
   *
   */
  public synchronized void removeListener(IMsgBusListener pListener)
  {
    listeners.remove(pListener);
  }

  /**
   * Return true if this Topic contains the specified Listener
   */
  public boolean containsListener(IMsgBusListener pListener)
  {
    return listeners.contains(pListener);
  }

  /**
   * Returns a copy of the Listeners for this Topic
   * @return vector of listeners
   */
  public synchronized IMsgBusListener[] getListeners()
  {
    return (IMsgBusListener[])listeners.toArray(new IMsgBusListener[0]);
  }

  /**
   *
   *
   */
  public String getName()
  {
    return topicName;
  }

  /**
   * Fires a Message to all of this Topics Listeners
   * If the pResultListener is not null then it will have a result posted to it
   * The source of the message will also have the result posted to it.
   * @param Message to fire to listeners
   * @param Listener to recieve the result from any listeners
   * @param pUniCast is true if only first Listener should be notified.
   * @return result for any calling methods that want a synchronize return.
   */
  public MsgBusMessage fire(MsgBusMessage pEvent, IMsgBusListener pResultListener, boolean pUniCast)
  {
    MsgBusMessage _result = null;
    Hashtable _mergedResult = null;

    //Walk through all Listeners
    Iterator _items = listeners.iterator();
    while(_items.hasNext())
    {
      IMsgBusListener _listener = (IMsgBusListener)_items.next();
      if(_listener != null)
      {
        _result = _listener.messagePosted(getName(), pEvent);
      }

      //Merge result with others
      if(_result != null)
      {
        if(_mergedResult == null) _mergedResult = new Hashtable();

        _mergedResult.putAll(_result.getParameters());
      }

      //return result to designated listener
      if(pResultListener != null && _result != null)
      {
        pResultListener.resultPosted(getName(), _result);
      }

      //return result to Event source
      try
      {
        Object _src = pEvent.getSource();
        if(_result != null && pEvent.resultWasRequested())
          ((IMsgBusListener)_src).resultPosted(getName(), _result);
      }
      catch(Throwable _exp)
      {
        //This would happen if _src was null or it was not a Listener
      }

      //If we are firing unicast message then break after first Listener
      if(pUniCast) break;
    }//End Loop

    //Return result
    if(_mergedResult == null)
    {
      return null;
    }
    else
    {
      return new MsgBusMessage(null,_mergedResult);
    }
  }

}
