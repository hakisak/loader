package org.xito.httpservice.client;

public abstract class ServiceClientTask {
   
   private Object value;
   
   public abstract Object construct();
   
   public abstract void finished();
   
   protected void init_construct() {
      value = construct();
   }
   
   protected void init_finished() {
      finished();
   }
   
   public void invokeLater() {
      
   }

}
