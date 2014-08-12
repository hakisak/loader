package test;

import java.io.InputStream;
import java.rmi.Remote;

public interface TestStreamService extends Remote {

   /**
    * Test sending data to server over a stream
    */
   public void readData(InputStream inStream);
}
