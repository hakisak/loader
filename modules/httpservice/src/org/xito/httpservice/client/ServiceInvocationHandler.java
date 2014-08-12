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

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.RemoteException;

public class ServiceInvocationHandler implements InvocationHandler {

   private ServiceStub stub;
   
   public ServiceInvocationHandler(ServiceStub stub) {
      this.stub = stub;
   }
   
   public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
      
      try {
         ServiceResponse response = stub.executeMethod(method.getName(), method.getParameterTypes(), params);
         if(response.hasException()) {
            throw response.getException();
         }
         
         return response.getResult();
      }
      catch(RemoteException exp) {
         throw exp;
      }
   }
   

}
