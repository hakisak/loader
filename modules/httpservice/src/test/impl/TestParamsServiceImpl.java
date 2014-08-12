package test.impl;

import test.TestDTO;
import test.TestParamsService;

public class TestParamsServiceImpl implements TestParamsService {

   public String echoString(String str) {
      return str + ": with Server Response";
   }

   public String concate(String str1, String str2) {
      return str1 + str2; 
   }
   
   public int add(int i1, int i2) {
     return i1 + i2;
   }
   
   public int sum(int[] values) {
      
      int sum = 0;
      
      if(values != null) {      
         for(int i=0;i<values.length;i++) {
            sum += values[i];
         }
      }
      
      return sum;
   }

   public TestDTO process(TestDTO dto) {
      
      dto.setFirstName(dto.getFirstName() + "RETURN");
      dto.setLastName(dto.getLastName() + "RETURN");
      
      return dto;
   }
   
}
