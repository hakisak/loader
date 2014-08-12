// Copyright 2007 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.blx;


import java.io.*;
import java.util.logging.*;
import org.xito.boot.*;

/**
 *
 * @author  Deane
 */
public class BLXService {
   
   private static Logger logger = Logger.getLogger(BLXService.class.getName());
   private static ServiceDesc serviceDesc;
      
   /** Creates a new instance of BLXService */
   public BLXService() {
   }
   
   /**
    * Sets this Services ServiceInfo object
    */
   public static void initService(ServiceDesc serv) {
      serviceDesc = serv;
   }
   
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      logger.info("BLX Service Started");
   }
   
}
