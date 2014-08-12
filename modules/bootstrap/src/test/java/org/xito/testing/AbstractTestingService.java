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

package org.xito.testing;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.xito.boot.*;
import junit.textui.*;

/**
 *
 * @author  Deane
 */
public abstract class AbstractTestingService {
   
   private static Logger logger = Logger.getLogger(AbstractTestingService.class.getName());
   private static ServiceDesc service;
   
   /** Creates a new instance of TestingService */
   public AbstractTestingService() {
   }
   
   public static void initService(ServiceDesc serv) {
      service = serv;
   }
   
   /**
    * Process all test cases found in testcases.properties in the boot dir
    * @param class loader to use for loading tests
    */
   public static void processTestCases(ClassLoader classLoader) {
      
      try {
         System.out.println("Processing TestCases Properties File");
         System.out.println("=======================================");
         File f = new File(Boot.getBootDir(), "testcases.properties");
         Properties props = new Properties();
         props.load(new FileInputStream(f));
         
         String suites = props.getProperty("testsuites");
         System.out.println("Running Tests:");
         System.out.println(suites);
         if(suites == null) {
            logger.warning("No Test Suites listed in testcases.properties");
            return;
         }
         
         StringTokenizer tokens = new StringTokenizer(suites, ", ");
         while(tokens.hasMoreTokens()) {
            String testSuite = tokens.nextToken();
            runTestSuite(classLoader, testSuite);
         }
      }
      catch(IOException ioExp) {
         logger.log(Level.SEVERE, "No testcases.properties file found in boot dir", ioExp);
      }
   }
   
   private static void runTestSuite(ClassLoader classLoader, String suiteName) {
      
      try {
         Class testSuiteClass = classLoader.loadClass(suiteName);
         TestRunner.run(testSuiteClass);
      }
      catch(ClassNotFoundException notFound) {
         logger.severe("Test Suite Class:"+suiteName+ " not found: "+notFound.getMessage());
      }
   }
   
}
