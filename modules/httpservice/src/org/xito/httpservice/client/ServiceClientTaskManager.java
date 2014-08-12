package org.xito.httpservice.client;

import java.util.LinkedList;

import javax.swing.SwingUtilities;

public class ServiceClientTaskManager {

   private LinkedList taskList;
   private Thread taskThread;
   private boolean stop_flag = false;
   
   public ServiceClientTaskManager() {
      taskList = new LinkedList();
   }
   
   public synchronized void addTask(ServiceClientTask task) {
      if(task != null) {
         taskList.addLast(task);
         notify();
      }
   }
   
   public void start() {
      stop_flag = false;
      
      taskThread = new TaskThread();
      taskThread.start();
   }
   
   public void stop() {
      stop_flag = true;
      taskList.clear();
   }
   
   private synchronized ServiceClientTask getNextTask() {
      
      while(!stop_flag) {
         if(taskList.size() == 0) {
            try {
               wait();
            }
            catch(InterruptedException exp) {
               exp.printStackTrace();
            }
         }
         
         return (ServiceClientTask)taskList.removeFirst();
      }
       
      return null;
   }
      
   //--------------------------------------------
   
   private class TaskThread extends Thread {
      
      private ServiceClientTask task;
      
      public void run() {
         while(!stop_flag) {
                        
            task = getNextTask();
            if(task != null) {
            
               try {
                  //call construct
                  task.init_construct();
               }
               finally {
                  
                  //call finished
                  SwingUtilities.invokeLater(new Runnable() {
                     public void run() {
                        task.init_finished();
                     }
                  });
               }
            }
            
            
            
         }
      }
   }
   
}
