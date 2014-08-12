
package org.xito.dialog;

public class ErrorMessageException {
   
   public static void main(String args[]) {
      
      //first create some nested exceptions
      Throwable exp1 = buildDeepStack(null, 5);
      Throwable exp2 = buildDeepStack(exp1, 5);
      
      //Now show exp2
      DialogManager.showError(null, "Error Title", "This is a Error Message", exp2);
   }
   
    public static Throwable buildDeepStack(Throwable cause, int levels) {
      if(levels <=0) return new Exception("Sample Error", cause);
      
      return buildDeepStack(cause, levels-1);
   }
   
}
