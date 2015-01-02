/*
 * BasicExtTestCase.java
 * JUnit based test
 *
 * Created on June 17, 2004, 10:53 PM
 */

package test.testcases;

import java.net.*;
import org.xito.boot.*;
import org.xito.blx.*;

import static junit.framework.TestCase.*;

/**
 *
 * @author Deane
 */
public class BasicExtTestCase {
   
   /**
    * Test to make sure correct Exceptions are thrown for bad BLX Files
    */
   public void testBadDocument() {
      
      //This file doesn't have a blx document root element
      try {
         URL url = new URL(Boot.getBootDir().toURL(), "ext/badDoc.blx");
         BLXCompFactory.getInstance().getObject(url);
      }
      catch(InvalidBLXXMLException badXML) {
         //this is ok
         return;
      }
      catch(Exception exp) {
         exp.printStackTrace();
         fail(exp.getMessage());
      }
   }
   
   /**
    * Test to make sure correct Exceptions are thrown for bad BLX Files
    */
   public void testMissingBLXObjext() {
      
      //This file doesn't have a blx object element
      try {
         URL url = new URL(Boot.getBootDir().toURL(), "ext/missingBLXObject.blx");
         BLXCompFactory.getInstance().getObject(url);
      }
      catch(InvalidBLXXMLException badXML) {
         //this is ok
      }
      catch(Exception exp) {
         exp.printStackTrace();
         fail(exp.getMessage());
      }
   }
   
   /**
    * Load up a BLX file with an extension
    */
   public void testBasicExtLoad() {
      try {
         URL url = new URL(Boot.getBootDir().toURL(), "ext/test1.blx");
         Object obj = BLXCompFactory.getInstance().getObject(url);
      }
      catch(Exception exp) {
         exp.printStackTrace();
         fail(exp.getMessage());
      }
   }
   
   /**
    * Load up a BLX file with an extension
    */
   public void testBasicExtAliasLoad() {
      try {
         URL url = new URL(Boot.getBootDir().toURL(), "ext/test2.blx");
         System.out.println(url);
         Object obj = BLXCompFactory.getInstance().getObject(url);
      }
      catch(Exception exp) {
         exp.printStackTrace();
         fail(exp.getMessage());
      }
   }
   
   /**
    * Load up a BLX file with a Collection
    */
   public void testCollection() {
      try {
         URL url = new URL(Boot.getBootDir().toURL(), "ext/testCollection.blx");
         Object obj = BLXCompFactory.getInstance().getObject(url);
      }
      catch(Exception exp) {
         exp.printStackTrace();
         fail(exp.getMessage());
      }
   }
   
   /**
    * Load up a BLX file with a blx object that was loaded by a Service rather then extension
    */
   public void testServiceExtension() {
      try {
         URL url = new URL(Boot.getBootDir().toURL(), "ext/testServiceExt.blx");
         Object obj = BLXCompFactory.getInstance().getObject(url);
      }
      catch(Exception exp) {
         exp.printStackTrace();
         fail(exp.getMessage());
      }
   }
}
