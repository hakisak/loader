package test.impl;

import test.TestService;

public class TestServiceImpl implements TestService {

   public void callMethod() {
      System.out.println("callMethod Executed");
   }
   
   public String callMethodReturn_String() {
      return "This is a test String";
   }

   public boolean callMethodReturn_boolean() {
      return false;
   }

   public Boolean callMethodReturn_Boolean() {
      return new Boolean(true);
   }

   public byte callMethodReturn_byte() {
      return (byte)0xF;
   }

   public Byte callMethodReturn_Byte() {
      return new Byte((byte)0xF);
   }

   public double callMethodReturn_double() {
      return Double.MAX_VALUE;
   }

   public Double callMethodReturn_Double() {
      return new Double(Double.MAX_VALUE);
   }

   public float callMethodReturn_float() {
      return Float.MAX_VALUE;
   }

   public Float callMethodReturn_Float() {
      return new Float(Float.MAX_VALUE);
   }

   public int callMethodReturn_int() {
      return Integer.MAX_VALUE;
   }

   public int[] callMethodReturn_intArray() {
      
      return new int[]{0, 1, 2, 3, 4, 5};
   }

   public Integer callMethodReturn_Integer() {
      return new Integer(Integer.MAX_VALUE);
   }

   public long callMethodReturn_long() {
      return Long.MAX_VALUE;
   }

   public Long callMethodReturn_Long() {
      return new Long(Long.MAX_VALUE);
   }
   
}
