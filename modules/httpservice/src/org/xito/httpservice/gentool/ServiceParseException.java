package org.xito.httpservice.gentool;

public class ServiceParseException extends Exception {
   
   public ServiceParseException(String msg) {
      super(msg);
   }
   
   public ServiceParseException(String msg, Throwable exp) {
      super(msg, exp);
   }

}
