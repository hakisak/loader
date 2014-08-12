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

package org.xito.httpservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xito.httpservice.client.ServiceRequest;
import org.xito.httpservice.client.ServiceResponse;



public class HttpServiceServlet extends HttpServlet {

   public static final String INTERFACE_NAME = "interface_name";
   public static final String IMPL_NAME = "impl_name";
   
   protected Class interfaceCls;
   protected Class implCls;
   
   protected Object implInstance;
   
   public void init(ServletConfig config) throws ServletException {
      
      String servletName = config.getServletName();
      String interfaceName = config.getInitParameter(INTERFACE_NAME);
      String implName = config.getInitParameter(IMPL_NAME);
      
      try {
         System.out.println(servletName + ": obtaining interface:" + interfaceName);
         System.out.println(servletName + ": obtaining impl:" + implName);
         
         interfaceCls = Class.forName(interfaceName);
         implCls = Class.forName(implName);
         
         implInstance = implCls.newInstance();
      }
      catch(Exception exp) {
         exp.printStackTrace();
      }
      
   }
   
   protected void doGet(HttpServletRequest reg, HttpServletResponse resp) throws ServletException, IOException {
      if(reg.getParameterMap().containsKey("info")) {
         resp.setContentType("text/html");
         
         PrintWriter out = resp.getWriter();
         out.println("<html><body>");
         
         out.println("<h2>" + interfaceCls.getName() + "</h2>");
         out.println("impl: " + implCls.getName() +"<br>");
         out.println("<hr>");
        
         
         writeMethodInfo(out);
         
         out.println("</body></html");
      }
   }

   protected void writeMethodInfo(PrintWriter out) {
      
      out.println("<pre>");
      
      Method[] methods = interfaceCls.getMethods();
      for(int i=0;i<methods.length;i++) {
         out.print("public ");
         Class returnType = methods[i].getReturnType();
         if(returnType.isArray()) {
            out.print(returnType.getComponentType().getName() + "[]");
         }
         else {
            out.print(returnType.getName());
         }
         
         out.print(" " + methods[i].getName());
         out.print("(");

         Class[] paramTypes = methods[i].getParameterTypes();
         for(int t=0;t<paramTypes.length;t++) {
            if(paramTypes[t].isArray()) {
               out.print(paramTypes[t].getComponentType().getName() + "[]");
            }
            else {
               out.print(paramTypes[t].getName());
            }
            if(t<paramTypes.length-1) {
               out.print(", ");
            }
         }
         out.print(")");
         
         Class[] exceptions = methods[i].getExceptionTypes();
         if(exceptions.length > 0) {
            out.print(" throws ");
            for(int e=0;e<exceptions.length;e++) {
               out.print(exceptions[e].getName());
               if(e<exceptions.length-1) {
                  out.print(", ");
               }
            }
         }
         
         out.println("; \n");
      }
      
      out.println("</pre>");
      
   }
   
   protected void doPost(HttpServletRequest reg, HttpServletResponse resp) throws ServletException, IOException {
      
      try {
         ServiceRequest serviceRequest = unpackServiceRequest(reg.getInputStream());
         
         System.out.println("executing: "+interfaceCls.getName() +":" + serviceRequest.getMethodName());
         ServiceResponse serviceResponse = executeMethod(serviceRequest.getMethodName(), 
               serviceRequest.getParamTypes(), 
               serviceRequest.getParams());
         
         sendResponse(resp, serviceResponse);
      }
      catch(Throwable exp) {
         exp.printStackTrace();
      }
   }
   
   public void sendResponse(HttpServletResponse resp, ServiceResponse serviceResp) throws IOException {
      
      ObjectOutputStream stream = new ObjectOutputStream(resp.getOutputStream());
      stream.writeObject(serviceResp);
      stream.close();
   }
   
   protected Class[] getParamTypes(Object[] params) {
      
      if(params == null || params.length==0) {
         return null;
      }
      
      Class[] paramTypes = new Class[params.length];
      for(int i=0;i<params.length;i++) {
         paramTypes[i] = params[i].getClass();
      }
      
      return paramTypes;
   }
   
   protected ServiceResponse executeMethod(String methodName, Class[] paramTypes, Object[] params) throws Throwable {
      
      ServiceResponse response = new ServiceResponse(interfaceCls.getName(), methodName);
      
      try {
         Method method = implCls.getMethod(methodName, paramTypes);
         Object result = method.invoke(implInstance, params);
         
         response.setResult((Serializable)result, method.getReturnType());
      }
      catch(InvocationTargetException targetExp) {
         targetExp.getTargetException().printStackTrace();
         response.setException(targetExp.getTargetException());
      }
      catch(Throwable exp) {
         exp.printStackTrace();
         response.setException(exp);
      }
      
      return response;
   }
   
   protected ServiceRequest unpackServiceRequest(InputStream inStream) throws IOException, ClassNotFoundException {
      
      ObjectInputStream objStream = new ObjectInputStream(inStream);
      ServiceRequest request = (ServiceRequest)objStream.readObject();
            objStream.close();
      inStream.close();
      
      return request;
   }
 
}
