package test.client;

import java.io.PrintStream;
import java.net.URI;

import org.xito.httpservice.client.ServiceResponse;
import org.xito.httpservice.client.ServiceStub;


import test.CustomException;
import test.TestExceptionService;
import test.TestParamsService;
import test.TestService;
import test.TestDTO;

public class TestProxyClient {

   /**
    * @param args
    */
   public static void main(String[] args) {

      try {
         ServiceStub stub = new ServiceStub(new URI("http://localhost:8080/http_service_test/xito_service/test.TestService"));
         
         TestService srv = (TestService)stub.getProxyService(TestService.class.getClassLoader(), TestService.class);
         
         PrintStream out = System.out;
         
         srv.callMethod();
         dumpResult(out, stub, "callMethod", Void.TYPE);
         dumpResult(out, stub, "callMethod", srv.callMethodReturn_String());
         dumpResult(out, stub, "callMethod", new Boolean(srv.callMethodReturn_boolean()));
         dumpResult(out, stub, "callMethod", srv.callMethodReturn_Boolean());
         dumpResult(out, stub, "callMethod", new Integer(srv.callMethodReturn_int()));
         dumpResult(out, stub, "callMethod", srv.callMethodReturn_Integer());
         dumpResult(out, stub, "callMethod", new Long(srv.callMethodReturn_long()));
         dumpResult(out, stub, "callMethod", srv.callMethodReturn_Long());
         dumpResult(out, stub, "callMethod", new Float(srv.callMethodReturn_float()));
         dumpResult(out, stub, "callMethod", srv.callMethodReturn_Float());
         dumpResult(out, stub, "callMethod", new Double(srv.callMethodReturn_double()));
         dumpResult(out, stub, "callMethod", srv.callMethodReturn_Double());
         dumpResult(out, stub, "callMethod", new Byte(srv.callMethodReturn_byte()));
         dumpResult(out, stub, "callMethod", srv.callMethodReturn_Byte());
         dumpResult(out, stub, "callMethod", srv.callMethodReturn_intArray());
         
         stub = new ServiceStub(new URI("http://localhost:8080/http_service_test/xito_service/test.TestParamsService"));
         
         TestParamsService srv2 = (TestParamsService)stub.getProxyService(TestParamsService.class.getClassLoader(), TestParamsService.class);
         dumpResult(out, stub, "echoString", srv2.echoString("this is a test"));
         dumpResult(out, stub, "concate", srv2.concate("Happy", " Birthday"));
         dumpResult(out, stub, "add", new Integer(srv2.add(50, 250)));
         dumpResult(out, stub, "sum", new Integer(srv2.sum(new int[]{1,2,3,4,5})));
         dumpResult(out, stub, "process", srv2.process(new TestDTO("deane", "richan")));
         
         stub = new ServiceStub(new URI("http://localhost:8080/http_service_test/xito_service/test.TestExceptionService"));
         TestExceptionService srv3 = (TestExceptionService)stub.getProxyService(TestExceptionService.class.getClassLoader(), TestExceptionService.class);
         try {
            srv3.throwCheckException();
         }
         catch(Exception exp) {
            dumpException(out, stub, "throwCheckException", exp);
         }
         
         try {
            srv3.throwCustomCheckedException();
         }
         catch(CustomException exp) {
            dumpException(out, stub, "throwCustomCheckedException", exp);
         }
         
         try {
            srv3.throwRuntimeException();
         }
         catch(RuntimeException exp) {
            dumpException(out, stub, "throwRuntimeException", exp);
         }
         
      }
      catch(Exception exp) {
         exp.printStackTrace();
      }
   }
   
   public static void dumpResult(PrintStream out, ServiceStub stub, String methodName, Object result) {
      
      out.println("service:"+stub.getServiceURI()+" - method:"+ methodName);
      out.println("----------------------------------------------");
      out.println("result:"+result);
      out.println("\n");
   }
   
   public static void dumpException(PrintStream out, ServiceStub stub, String methodName, Throwable exp) {
      
      out.println("service:"+stub.getServiceURI()+" - method:"+ methodName);
      out.println("----------------------------------------------");
      out.println("exp:"+exp.toString());
      out.println("\n");
   }
}
