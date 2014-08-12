//Copyright 2007 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.boot.ui;

import javax.swing.SwingUtilities;

/**
 * UIWorker provide a mechanism to perform work in a background thread and not 
 * block the EventDispatch Thread. UI should use a UIWorker to implemnt long running
 * tasks to avoid blocking the UI 
 *  
 * All non swing busy work should occur in the work method. Any code that updates the 
 * UI should be implemented in finished 
 *  
 * @author drichan
 */
public abstract class UIWorker {
   
   /**
    * Application logic to do before work is done in the busy worker.
    * Subclasses of BusyWorker can use this to do setup worker prior to the work method 
    * being called
    */
   protected void preWork(){}
   
   /**
    * Application logic to be done after "work" is done but still part of the Busy Worker Thread
    */
   protected void postWork(){}
   
   /**
    * Application work to be done before the "finished" block but still in the EventDispatch Thread
    * Subclasses can use this method to implement setup prior to calling finished
    */
   protected void preFinished(){}
   
   /**
    * Application work to be done after the "finished" block but still int the EventDispatch Thread
    */
   protected void postFinished(){}
   
   /**
    * Place busy work in this method. Do not make any UI updates in this method
    * @return any data 
    */
   public abstract Object work();
   
   /**
    * Place all UI update code in this method
    * @param data return from work method
    */
   public abstract void finished(Object data);
   
   /**
    * Executes the code implemented in the work method.
    * Then will execute the code in finished using the Event Dispatch thread 
    */
   public void invokeLater() {
      new WorkerThread().start();
   }
      
   /**
    * Thread for Work
    */
   private class WorkerThread extends Thread {
      
      public void run() {
         
         preWork();
         Object data  = work();
         postWork();
                                    
         SwingUtilities.invokeLater(new FinishedWorker(data));
      }
   }
   
   /**
    * Runnable for finished block 
    */
   private class FinishedWorker implements Runnable {
      
      private Object data;
      
      public FinishedWorker(Object data) {
         this.data = data;
      }
      
      public void run() {
         preFinished();
         finished(data);
         postFinished();
      }
   }

}
