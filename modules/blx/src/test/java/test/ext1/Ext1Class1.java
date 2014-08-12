/*
 * Ext1Class1.java
 *
 * Created on June 17, 2004, 9:44 PM
 */

package test.ext1;

import org.xito.blx.*;

/**
 *
 * @author  Deane
 */
public class Ext1Class1 implements BLXObject {
   
   String id = "" + System.currentTimeMillis();
   
   /** Creates a new instance of Ext1Class1 */
   public Ext1Class1() {
   }
   
   public BLXElement getBLXElement() {
      return new BLXElement(OBJECT_TYPE, ((BLXExtClassLoader)this.getClass().getClassLoader()).getExtension(), this.getClass().getName(), id);
   }
   
   public String getBLXId() {
      return id;
   }
   
   public org.w3c.dom.Element getDataElement() {
      return null;
   }
   
   public boolean isDirty() {
      return true;
   }
   
   public void setBLXElement(BLXElement pElement) {
      
   }
   
   public void store(boolean allChildren, BLXStorageHandler storageHandler) throws java.io.IOException {
      
   }
   
}
