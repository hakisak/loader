
package org.xito.dialog;

import java.awt.*;

public class DescCustomButtons {
   
   public static void main(String args[]) {
      
      String html = "<html>This is a test of some <b>HTML</b> Message org.xito: <ol><li>test1</li><li>test2<li</ol></html>";
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setWidth(400);
      desc.setHeight(400);
      desc.setWindowTitle("Window Title Goes Here");
      desc.setTitle("Title Goes Here");
      desc.setSubtitle("subtitle goes Here");
      desc.setMessage(html);
      desc.setShowButtonSeparator(true);
      desc.setGradiantColor(new Color(100,100,200,50));
      desc.setGradiantOffsetRatio(0.5f);
      desc.setIcon(DialogManager.getInfoIcon());
      ButtonType types[] = new ButtonType[]{new ButtonType("Option1", 1), new ButtonType("Option2", 2, true)};
      desc.setButtonTypes(types);
      
      int result = DialogManager.showDialog(desc);
      DialogManager.showMessage(null, "Result", "User picked:"+result);
   }
   
}
