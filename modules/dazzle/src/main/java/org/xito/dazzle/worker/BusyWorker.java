// Copyright 2007 Xito.org
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

package org.xito.dazzle.worker;

import java.util.*;

import javax.swing.SwingUtilities;

/**
 * BusyWorker provide a mechanism to perform work in a background thread and not 
 * block the EventDispatch Thread. UI should use a BusyWorker to implemnt long running
 * tasks to avoid blocking the UI 
 *  
 * All non swing busy work should occur in the work method. Any code that updates the 
 * UI should be implemented in finished 
 *  
 * @author drichan
 */
public abstract class BusyWorker<T> {
   
   protected LinkedHashSet<BusyWorkerListener> workerListeners = new LinkedHashSet<BusyWorkerListener>();
   protected boolean canceled_flag = false;
   protected Thread workerThread;
   
   /**
    * Add a BusyWorkerListener to listen for worker events
    * @param listener
    */
   public void addBusyWorkerListener(BusyWorkerListener listener) {
      workerListeners.add(listener);
   }
   
   /**
    * Remove a BusyWorkerListener from listening for worker events
    * @param listener
    */
   public void removeBusyWorkerListener(BusyWorkerListener listener) {
      workerListeners.remove(listener);
   }
   
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
   public abstract T work();
   
   /**
    * Place all UI update code in this method
    * @param data return from work method
    */
   public abstract void finished(T data);
   
   /**
    * Executes the code implemented in the work method.
    * Then will execute the code in finished using the Event Dispatch thread 
    */
   public void invokeLater() {
      
      //if we are already doing work just return
      if(workerThread != null && workerThread.isAlive()) {
         return;
      }
      
      //do the work
      canceled_flag = false;
      workerThread = new WorkerThread();
      workerThread.start();
   }
   
   /**
    * Cancel the work being done. This will interrupt the worker thread
    */
   public void cancel() {
      //if the work is already done or interrupted then just return
      if(workerThread == null || !workerThread.isAlive() || workerThread.isInterrupted()) {
         return;
      }
   
      try {
         workerThread.interrupt();
      }
      finally {
         canceled_flag = true;
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               fireWorkerCanceled();
            }
         });
      }
   }
   
   /**
    * Call workComplete on all BusyWorkerListeners
    */
   protected void fireWorkComplete() {
      for(BusyWorkerListener listener : workerListeners) {
          listener.workComplete(this);
      }
   }
   
   /**
    * Call workerFinsished on all BusyWorkerListeners
    */
   protected void fireWorkerFinished() {
      for(BusyWorkerListener listener : workerListeners) {
          listener.workerFinished(this);
      }
   }
   
   /**
    * Call workerCanceled on all BusyWorkerListeners
    */
   protected void fireWorkerCanceled() {
      for(BusyWorkerListener listener : workerListeners) {
          listener.workerCanceled(this);
      }
   }
   
   /**
    * Thread for Work
    */
   private class WorkerThread extends Thread {
      
      public void run() {
         
         preWork();
         T data  = work();
         postWork();
         fireWorkComplete();
                           
         SwingUtilities.invokeLater(new FinishedWorker(data));
      }
   }
   
   /**
    * Runnable for finished block 
    */
   private class FinishedWorker implements Runnable {
      
      private T data;
      
      public FinishedWorker(T data) {
         this.data = data;
      }
      
      public void run() {
         preFinished();
         finished(data);
         postFinished();
         fireWorkerFinished();
      }
   }

}
