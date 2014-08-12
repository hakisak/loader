
package org.xito.dialog;

public class ConfirmMessage {
   
   public static void main(String args[]) {
      
      int result = 0;
      result = DialogManager.showConfirm(null, "Confirm Title", "This is an Question", DialogManager.YES_NO_CANCEL);
      
      if(result == DialogManager.YES) 
         DialogManager.showMessage(null, "Confirm Result", "User picked option YES");
      if(result == DialogManager.NO) 
         DialogManager.showMessage(null, "Confirm Result", "User picked option NO");
      if(result == DialogManager.CANCEL) 
         DialogManager.showMessage(null, "Confirm Result", "User picked option CANCEL");
   }
}
