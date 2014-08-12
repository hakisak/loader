package test.impl;

import java.io.IOException;
import java.io.InputStream;

import test.TestStreamService;

public class TestStreamServiceImpl implements TestStreamService {

   /**
    * Test sending data to server over a stream
    */
   public void readData(InputStream inStream) {
      try {
         byte[] buf = new byte[2048];
         int count = inStream.read(buf);
         int total = 0;
         while(count>0) {
            total += count;
            count = inStream.read(buf);
            System.out.println("read:"+total);
         }
      }
      catch(IOException exp) {
         exp.printStackTrace();
      }
   }
}
