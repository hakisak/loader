package org.xito.launcher.sys;

import java.awt.Frame;
import java.io.File;

import org.w3c.dom.Element;
import org.xito.launcher.LauncherAction;
import org.xito.launcher.LauncherActionFactory;
import org.xito.launcher.Resources;
import org.xito.launcher.jnlp.JavaAppAction;

public class LocalAppActionFactory extends LauncherActionFactory {

   public static final String ELEMENT_NAME = "sys-action";
   
   public LocalAppActionFactory() {
      setName(Resources.sysBundle.getString("action.display.name"));
      setSmallIcon(LocalAppActionBeanInfo.icon16);
      setLargeIcon(LocalAppActionBeanInfo.icon32);
   }
   
   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#createActionFromDataElement(org.w3c.dom.Element)
    */
   @Override
   public LauncherAction createActionFromDataElement(Element element) {
      
      LocalAppDesc desc = new LocalAppDesc();
      
      initNameAndId(desc, element);
      
      desc.setExecutableCmd(element.getAttribute("exec"));
      
      String args = element.getAttribute("args"); 
      if(args != null && !args.equals("")) {
         desc.setArgs(args);
      }
      
      String dir = element.getAttribute("start-in");
      if(dir != null && !dir.equals("")) {
         desc.setStartInDir(new File(dir));
      }
      
      return new LocalAppAction(this, desc);
   }

   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#generateDataElement(org.xito.launcher.LauncherAction)
    */
   @Override
   public Element generateDataElement(LauncherAction action) {
      
      LocalAppDesc desc = (LocalAppDesc)action.getLaunchDesc();
      org.w3c.dom.Element e = createInitialElement(desc);
            
      e.setAttribute("exec", desc.getExecutableCmd());
      e.setAttribute("args", desc.getArgs());
      if(desc.getStartInDir()!= null) {
         e.setAttribute("start-in", desc.getStartInDir().toString());
      }
      
      return e;
   }

   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#getElementName()
    */
   @Override
   public String getElementName() {
     return ELEMENT_NAME;
   }

   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#createAction()
    */
   @Override
   public LauncherAction createAction() {
      
      return new LocalAppAction(this);
   }
   
   @Override
   public LauncherAction createAction(Frame owner, Object obj) {
      // TODO Auto-generated method stub
      return null;
   }

}
