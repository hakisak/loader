
package org.xito.dialog;

import java.awt.Frame;

public class ConfirmMessageIcon {
   
   public static void main(String args[]) {
      
      int result = DialogManager.showConfirm((Frame) null, DialogManager.getInfoIcon(), "Confirm Title", "This is a Question with Icon", DialogManager.OK_CANCEL);
   }
   
}
