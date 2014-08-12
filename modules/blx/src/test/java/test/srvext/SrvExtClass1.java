/*
 * Ext2Class1.java
 *
 * Created on June 17, 2004, 10:10 PM
 */

package test.srvext;

import org.xito.blx.*;

/**
 *
 * @author  Deane
 */
public class SrvExtClass1 extends AbstractBLXObject {
   
   /** Creates a new instance of SrvExtClass1 */
   public SrvExtClass1() {
   }
   
   public org.w3c.dom.Element getDataElement() {
      return null;
   }
   
   public boolean isDirty() {
      return false;
   }
   
   public static void main(String args[]) {
      System.out.println("********************* Started SrvExtClass1");
   }
   
}
