
package org.xito.dialog;

public class DescLongMessage {
   
   public static void main(String args[]) {
      
      StringBuffer html = new StringBuffer();
      html.append("<html>This is a test of some <b>HTML</b> Message org.xito: ");
      html.append("<ol><li>test1</li><li>test2<li</ol>");
      html.append("<p>This is a very long message. This is a very long message. This is a very long message. This is a very long message. </p></html>");
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setTitle("Bad Error");
      desc.setSubtitle("subtitle goes Here");
      desc.setMessage(html.toString());
      desc.setShowButtonSeparator(true);
      desc.setType(DialogManager.OK_CANCEL);
      desc.setWidth(300);
      desc.setHeight(300);
      
      
      DialogManager.showDialog(desc);
   }
   
}
