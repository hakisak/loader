package test;

import java.rmi.Remote;

public interface TestService extends Remote {
   
   public void callMethod();
   
   public String callMethodReturn_String();
   
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

}
