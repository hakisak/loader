
package org.xito.dialog;

public class ProgressBasic {
   
   public static void main(String args[]) {
      
      ProgressDialogDescriptor desc = new ProgressDialogDescriptor();
      desc.setTitle("Test Progress");
      desc.setSubtitle("Test of a Simple Progress Dialog");
      desc.setType(DialogManager.CANCEL);
      desc.setShowButtonSeparator(true);
      desc.setMessage("This is a Sample Progress Dialog");
      desc.setWidth(350);
            
      ProgressDialog dialog = new ProgressDialog(null, desc, false);
      dialog.setVisible(true);
   }
   
}
