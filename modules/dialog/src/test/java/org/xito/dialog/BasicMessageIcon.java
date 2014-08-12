
package org.xito.dialog;

import java.awt.Frame;

public class BasicMessageIcon {
   
   public static void main(String args[]) {
      
      DialogManager.showMessage((Frame) null, DialogManager.getInfoIcon(), "Title", "This is a Test of a Message with Icon");
   }
   
}
