package test;

import java.rmi.Remote;

public interface TestParamsService extends Remote {
   
   public String echoString(String str);
   
   public String concate(String str1, String str2);
   
   public int add(int i1, int i2);
   
   public int sum(int[] values);
   
   public TestDTO process(TestDTO dto);

}
