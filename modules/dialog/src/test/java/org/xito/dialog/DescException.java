
package org.xito.dialog;

import java.awt.*;

public class DescException {
   
   public static void main(String args[]) {
      
      String html = "<html>This is a test of some <b>HTML</b> Message org.xito: <ol><li>test1</li><li>test2<li</ol></html>";
      Throwable exp1 = buildDeepStack(null, 5);
      Throwable exp2 = buildDeepStack(exp1, 5);
            
      DialogDescriptor desc = new DialogDescriptor();
      desc.setTitle("Bad Error");
      desc.setSubtitle("subtitle goes Here");
      desc.setMessage(html);
      desc.setGradiantColor(new Color(200,100,100,50));
      desc.setGradiantOffsetRatio(0.5f);
      desc.setType(DialogManager.OK_CANCEL);
      desc.setMessageType(DialogManager.ERROR_MSG);
      desc.setException(exp2);
            
      AlertDialog dialog = new AlertDialog((Frame)null, desc);
      dialog.show();
   }
   
   public static Throwable buildDeepStack(Throwable cause, int levels) {
      if(levels <=0) return new Exception("Sample Error", cause);
      
      return buildDeepStack(cause, levels-1);
   }
   
}
