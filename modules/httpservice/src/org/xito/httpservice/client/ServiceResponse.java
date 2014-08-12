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

package org.xito.httpservice.client;

import java.io.PrintStream;
import java.io.Serializable;

public class ServiceResponse implements Serializable {
   
   private String serviceName;
   private String methodName;
   private Class resultClass;
   private Serializable result;
   private Throwable exception;
   
   public ServiceResponse(String serviceName, String methodName) {
      this.serviceName = serviceName;
      this.methodName = methodName;
   }
   
   public void setResult(Serializable result, Class resultClass) {
      this.result = result;
      this.resultClass = resultClass;
   }
   
   public void setException(Throwable exp) {
      this.exception = exp;
   }
   
   public boolean hasException() {
      return (exception != null);
   }
   
   public Serializable getResult() {
      return result;
   }
   
   public Throwable getException() {
      return exception;
   }
   
   public Class getResultClass() {
      return resultClass;
   }
   
   public void dumpResponse(PrintStream out, Object[] params) {
      
      out.println("service:"+serviceName+" - method:"+ methodName);
      out.println("----------------------------------------------");
      if(params != null) {
         out.println("params:");
         
         for(int i=0;i<params.length;i++) {
            System.out.println(i+":"+params[i]);
         }
      }
      if(hasException()) {
         out.println("exception:"+exception.getMessage());
         exception.printStackTrace(out);
      }
      else {
         out.println("resultType:"+resultClass.getName());
         out.println("result:"+result);
      }
      
      
      out.println("\n");
   }

}
