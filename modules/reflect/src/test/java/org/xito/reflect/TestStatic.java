package org.xito.reflect;

/*
 * Test.java
 *
 * Created on January 11, 2003, 1:50 PM
 */

import junit.awtui.TestRunner;
import org.xito.reflect.*;

/**
 *
 * @author  Deane
 */
public class TestStatic extends junit.framework.TestCase {
  
  public static final String SUCCESS = "success";
    
  public TestStatic(String name) {
    super(name);
  }
    
  // Static Method Tests
  public static String testStaticMethod(Object o1) {
    return SUCCESS;
  }
  
  public static String testStaticMethod(Object o1, Object o2) {
    return SUCCESS;
  }
  
  public static String testStaticMethod() {
    return SUCCESS;
  }
  
  public static String testStaticMethod(boolean value) {
    return SUCCESS;
  }
  
  public static String testStaticMethod(int value) {
    return SUCCESS;
  }
  
  public static String testStaticMethod(String value) {
    return SUCCESS;
  }
      
  public void testAll() {
    Reflection kit = Reflection.getToolKit();
    
    try {
      Class cls = kit.findClass("org.xito.reflect.TestStatic");
      this.assertEquals(SUCCESS, kit.callStatic(cls, "testStaticMethod", "argValue"));
      this.assertEquals(SUCCESS, kit.callStatic(cls, "testStaticMethod", true));
      this.assertEquals(SUCCESS, kit.callStatic(cls, "testStaticMethod", false));
    }
    catch(Throwable t) {
      fail(t.getMessage());
    }
  }
  
  public static void main(String args[]) {
      
      TestRunner.run(TestStatic.class);
  }
}
