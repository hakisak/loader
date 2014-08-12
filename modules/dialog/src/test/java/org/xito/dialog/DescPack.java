
package org.xito.dialog;

import java.awt.*;

public class DescPack {
   
   public static void main(String args[]) {
      
      String html = "<html><p>This is a very long message that will go on and on</p>" +
         "<ul><li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "<li>Testing of text</li>" +
         "</ul></html>";
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setTitle("Info Message");
      desc.setSubtitle("This is a very long subtitle that goes on and on and will cause the org.xito to be wide");
      desc.setMessage(html);
      desc.setGradiantColor(new Color(100,100,100,50));
      desc.setGradiantOffsetRatio(0.5f);
      desc.setType(DialogManager.OK_CANCEL);
      desc.setMessageType(DialogManager.INFO_MSG);
      desc.setPack(true);
            
      AlertDialog dialog = new AlertDialog((Frame)null, desc);
      dialog.show();
   }
   
}
