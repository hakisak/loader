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

import java.io.InputStream;
import java.io.Serializable;

public class ServiceRequest implements Serializable {
   
   private Class[] paramTypes;
   private Object[] params;
   private String methodName;
   
   public ServiceRequest(String methodName, Class paramTypes[], Object[] params) {
      this.methodName = methodName;
      this.paramTypes = paramTypes;
      this.params = params;
   }
   
   public Class[] getParamTypes() {
      return paramTypes;
   }
   
   public Object[] getParams() {
      return params;
   }
   
   public String getMethodName() {
      return methodName;
   }
   
   public InputStream getParamInputStream() {
      if(paramTypes == null) return null;
      
      for(int i=0;i<paramTypes.length;i++) {
         if(InputStream.class.isAssignableFrom(paramTypes[i])) {
            return (InputStream)params[i];
         }
      }
      
      return null;
   }

}
