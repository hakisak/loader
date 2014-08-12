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

import java.io.*;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.rmi.RemoteException;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

public class ServiceStub {
   
   public static final String METHOD_PARAM = "method";
   
   protected URI serviceURI;
   protected HttpClient httpClient;
   
   public ServiceStub(URI serviceURI) {
      this.serviceURI = serviceURI;
      httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
   }
   
   /**
    * Get the Service URI
    * @return
    */
   public URI getServiceURI() {
      return serviceURI;
   }
   
   /**
    * Get a Proxy for a Service Interface
    * @param classLoader
    * @param serviceInterface
    * @return
    */
   public Object getProxyService(ClassLoader classLoader, Class serviceInterface) {
      
      return Proxy.newProxyInstance(classLoader, new Class[]{serviceInterface}, new ServiceInvocationHandler(this));
   }

   public void sendData(InputStream inStream) throws IOException {
      
   }
   
   /**
    * Execute a method with no parameters
    * @param methodName
    * @return
    * @throws RemoteException
    */
   public ServiceResponse executeMethod(String methodName) throws RemoteException {
      return executeMethod(methodName, null, null);
   }
   
   /**
    * Excecute a method with parameters
    * @param methodName
    * @param paramTypes
    * @param params
    * @return
    * @throws RemoteException
    */
   public ServiceResponse executeMethod(String methodName, Class[] paramTypes, Object[] params) throws RemoteException {
            
      PostMethod post = new PostMethod(serviceURI.toString());
      
      try {
         
         ByteArrayOutputStream outStream = new ByteArrayOutputStream();
         ObjectOutputStream objStream = new ObjectOutputStream(outStream);
         objStream.writeObject(new ServiceRequest(methodName, paramTypes, params));
         objStream.close();
         outStream.close();
                  
         post.setRequestEntity(new ByteArrayRequestEntity(outStream.toByteArray()));
         int status = httpClient.executeMethod(post);
         
         //throw http exception for bad status
         if(status != HttpStatus.SC_OK) {
            throw new HttpException(status + ":" + HttpStatus.getStatusText(status));
         }
         
         return processResult(post.getResponseBodyAsStream());
      }
      catch(HttpException httpExp) {
         throw new RemoteException(httpExp.getMessage(), httpExp);
      }
      catch(Exception exp) {
         throw new RemoteException(exp.getMessage(), exp);
      }
      finally {
         if(post != null) post.releaseConnection();
      }
   }
   
   /**
    * Process the result and return Response object
    * @param stream
    * @return
    * @throws IOException
    * @throws ClassNotFoundException
    */
   protected ServiceResponse processResult(InputStream stream) throws IOException, ClassNotFoundException {
      
      ObjectInputStream objStream = new ObjectInputStream(stream);
      return (ServiceResponse)objStream.readObject();
   }
   
   //--------------------------------------------------------------------
   
   /**
    * Thread used to Send Request to Server
    */
   public class SendThread extends Thread {
      
      private ServiceRequest request;
      private PostMethod postMethod;
      
      public SendThread(ServiceRequest request, PostMethod postMethod) {
         this.request = request;
         this.postMethod = postMethod;
      }
      
      public void run() {
         
         try {
            PipedInputStream in = new PipedInputStream();
            PipedOutputStream out = new PipedOutputStream(in);
            ObjectOutputStream objStream = new ObjectOutputStream(out);
         
            SequenceInputStream seqIn = new SequenceInputStream(in, request.getParamInputStream());
         
            postMethod.setRequestEntity(new InputStreamRequestEntity(seqIn));
         
            objStream.writeObject(request);
            objStream.close();
            
            
         }
         catch(IOException ioExp) {
            ioExp.printStackTrace();
         }
         
      }
   }

}
