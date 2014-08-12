package org.xito.launcher.jnlp;

import java.awt.Frame;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.xito.launcher.LauncherAction;
import org.xito.launcher.LauncherActionFactory;
import org.xito.launcher.Resources;

public class JavaRemoteAppActionFactory extends LauncherActionFactory {

   private static final Logger logger = Logger.getLogger(JavaRemoteAppActionFactory.class.getName());
   public static final String ELEMENT_NAME = "java-remote-action";
    
   public JavaRemoteAppActionFactory() {
      setName(Resources.javaBundle.getString("remote.action.display.name"));
      setSmallIcon(JavaRemoteAppActionBeanInfo.icon16);
      setLargeIcon(JavaRemoteAppActionBeanInfo.icon32);
   }
   
   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#createActionFromDataElement(org.w3c.dom.Element)
    */
   @Override
   public LauncherAction createActionFromDataElement(Element element) {
      
      JavaAppDesc desc = new JavaAppDesc();
      initNameAndId(desc, element);
      
      //AppDescURL
      String appDescHref = element.getAttribute("app-href");
      if(appDescHref != null && !appDescHref.equals("")) {
         try {
            desc.setAppDescURL(new URL(appDescHref));
         }
         catch(MalformedURLException exp) {
            logger.warning("Invalid App Desc URL:"+appDescHref);
         }
      }
      
      //Separate VM
      if(element.getAttribute("separate-vm").equals("true")) {
         desc.setSeperateVM(true);
      }
      else {
         desc.setSeperateVM(false);
      }
      
      return new JavaRemoteAppAction(this, desc);
   }

   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#generateDataElement(org.xito.launcher.LauncherAction)
    */
   @Override
   public Element generateDataElement(LauncherAction action) {
      
      JavaAppDesc desc = (JavaAppDesc)action.getLaunchDesc();
      
      org.w3c.dom.Element e = createInitialElement(desc);
      
      //check to see if we used an AppDescURL
      if(desc.getAppDescURL() != null) {
         e.setAttribute("app-href", desc.getAppDescURL().toString());
      }
      
      //Use Separate VM
      if(desc.useSeperateVM()) {
         e.setAttribute("separate-vm", "true");
      }
      else {
         e.setAttribute("separate-vm", "false");
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
      
      return new JavaRemoteAppAction(this);
   }
   
   @Override
   public LauncherAction createAction(Frame owner, Object obj) {
      // TODO Auto-generated method stub
      return null;
   }

}
