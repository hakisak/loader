package test;

import java.rmi.Remote;

public interface TestExceptionService extends Remote {

   public void throwRuntimeException();
   
   public void throwCheckException() throws Exception;
   
   public void throwCustomCheckedException() throws CustomException;
}
