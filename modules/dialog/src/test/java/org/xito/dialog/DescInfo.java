
package org.xito.dialog;

import java.awt.*;

public class DescInfo {
   
   public static void main(String args[]) {
      
      String html = "<html>This is a test of some <b>HTML</b> Message org.xito: <ol><li>test1</li><li>test2<li</ol></html>";
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setTitle("Info Message");
      desc.setSubtitle("subtitle goes Here");
      desc.setMessage(html);
      desc.setGradiantColor(new Color(100,100,100,50));
      desc.setGradiantOffsetRatio(0.5f);
      desc.setType(DialogManager.OK_CANCEL);
      desc.setMessageType(DialogManager.INFO_MSG);
      
      AlertDialog dialog = new AlertDialog((Frame)null, desc);
      dialog.show();
   }
   
}
