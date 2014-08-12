package test.client;

import java.lang.reflect.Constructor;
import java.net.URI;

import org.xito.httpservice.client.ServiceResponse;
import org.xito.httpservice.client.ServiceStub;



public class TestStubClient {

   /**
    * @param args
    */
   public static void main(String[] args) {

      try {
         ServiceStub stub = new ServiceStub(new URI("http://localhost:8080/http_service_test/xito_service/test.TestService"));
         test(stub, "callMethod");
         test(stub, "callMethodReturn_String");
         test(stub, "callMethodReturn_boolean");
         test(stub, "callMethodReturn_Boolean");
         test(stub, "callMethodReturn_int");
         test(stub, "callMethodReturn_Integer");
         test(stub, "callMethodReturn_long");
         test(stub, "callMethodReturn_Long");
         test(stub, "callMethodReturn_float");
         test(stub, "callMethodReturn_Float");
         test(stub, "callMethodReturn_double");
         test(stub, "callMethodReturn_Double");
         test(stub, "callMethodReturn_byte");
         test(stub, "callMethodReturn_Byte");
         test(stub, "callMethodReturn_intArray");
         
         stub = new ServiceStub(new URI("http://localhost:8080/http_service_test/xito_service/test.TestParamsService"));
         test(stub, "echoString", new Class[]{String.class}, new Object[]{"testing from client"});
         test(stub, "concate", new Class[]{String.class, String.class}, new Object[]{"New York", " Rules"});
         test(stub, "add", new Class[]{Integer.TYPE, Integer.TYPE}, new Object[]{new Integer(55), new Integer(45)});
         test(stub, "sum", new Class[]{new int[0].getClass()}, new Object[]{new int[]{1, 5, 14}});
         
         Constructor dtoConstructor = Class.forName("test.TestDTO").getConstructor(new Class[]{String.class, String.class});
         Object dto = dtoConstructor.newInstance(new Object[]{"deane", "richan"});
         test(stub, "process", new Class[]{dto.getClass()}, new Object[]{dto});
         
         stub = new ServiceStub(new URI("http://localhost:8080/http_service_test/xito_service/test.TestExceptionService"));
         test(stub, "throwCheckException");
         test(stub, "throwCustomCheckedException");
         test(stub, "throwRuntimeException");
      }
      catch(Exception exp) {
         exp.printStackTrace();
      }
   }
   
   public static void test(ServiceStub stub, String methodName, Class[] paramTypes, Object[] params) throws Exception {
      
      ServiceResponse resp = stub.executeMethod(methodName, paramTypes, params);
      resp.dumpResponse(System.out, params);
   }
   
   public static void test(ServiceStub stub, String methodName) throws Exception {
      
      ServiceResponse resp = stub.executeMethod(methodName);
      resp.dumpResponse(System.out, null);
   }
   
   /*
   public boolean callMethodReturn_boolean();
   
   public Boolean callMethodReturn_Boolean();
   
   public int callMethodReturn_int();
   
   public Integer callMethodReturn_Integer();
   
   public long callMethodReturn_long();
   
   public Long callMethodReturn_Long();
   
   public float callMethodReturn_float();
   
   public Float callMethodReturn_Float();
   
   public double callMethodReturn_double();
   
   public Double callMethodReturn_Double();
   
   public byte callMethodReturn_byte();
   
   public Byte callMethodReturn_Byte();
   
   public int[] callMethodReturn_intArray();
   */

}
