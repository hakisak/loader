/*
 * TestContainer.java
 *
 * Created on June 18, 2004, 11:44 PM
 */

package test.ext1;

import org.xito.blx.*;

/**
 *
 * @author  Deane
 */
public class TestCollection extends AbstractBLXObject {
   
   BLXCollection collection = new BLXCollection();
   
   /** Creates a new instance of TestContainer */
   public TestCollection() {
   }
   
   public org.w3c.dom.Element getDataElement() {
      return collection.getXMLElement(null);
   }
   
   public boolean isDirty() {
      return false;
   }
   
   public void setBLXElement(BLXElement e) {
      super.setBLXElement(e);
      
      collection.setXMLElement(e.getDataElement(), e.getContextURL());
      
   }
   
}
