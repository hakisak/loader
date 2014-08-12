package test.impl;

import test.CustomException;
import test.TestExceptionService;

public class TestExceptionServiceImpl implements TestExceptionService {

   public void throwCheckException() throws Exception {
      throw new Exception("test exception");
   }

   public void throwCustomCheckedException() throws CustomException {
      throw new CustomException("custom exception");
   }

   public void throwRuntimeException() {
      throw new RuntimeException("runtime exception");
   }
   

}
