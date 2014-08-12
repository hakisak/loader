package org.xito.launcher.web;

import java.awt.Frame;
import java.net.URL;

import org.w3c.dom.Element;
import org.xito.launcher.LauncherAction;
import org.xito.launcher.LauncherActionFactory;
import org.xito.launcher.Resources;
import org.xito.launcher.jnlp.JavaAppAction;

public class WebActionFactory extends LauncherActionFactory {

   public static final String ELEMENT_NAME = "web-action";

   public WebActionFactory() {
      setName(Resources.webBundle.getString("action.display.name"));
      setSmallIcon(WebActionBeanInfo.icon16);
      setLargeIcon(WebActionBeanInfo.icon32);
   }
   
   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#createActionFromDataElement(org.w3c.dom.Element)
    */
   @Override
   public LauncherAction createActionFromDataElement(Element element) {
      
      WebDesc desc = new WebDesc();
      initNameAndId(desc, element);
      
      desc.setAddress(element.getAttribute("address"));
      String newB = element.getAttribute("new-browser");
      if(newB != null && newB.equals("true")) {
         desc.setUseNewBrowser(true);
      }
      else {
         desc.setUseNewBrowser(false);
      }
      
      return new WebAction(this, desc);
   }

   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#generateDataElement(org.xito.launcher.LauncherAction)
    */
   @Override
   public Element generateDataElement(LauncherAction action) {
      WebDesc desc = (WebDesc)action.getLaunchDesc();
      
      org.w3c.dom.Element e = createInitialElement(desc);
      e.setAttribute("address", desc.getAddress());
      if(desc.useNewBrowser()) {
         e.setAttribute("new-browser", "true");
      }
      else {
         e.setAttribute("new-browser", "false");
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
      
      return new WebAction(this);
   }
   
   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#createAction(java.awt.Frame, java.lang.Object)
    */
   @Override
   public LauncherAction createAction(Frame owner, Object obj) {
      
      //Can't be null
      if(obj == null)
         return null;
      
      //It needs to be a URL object
      if(!(obj instanceof URL)) 
         return null;
      
      URL url = (URL)obj;
      WebAction action = new WebAction(this);
      WebDesc webDesc = (WebDesc)action.getLaunchDesc();
      webDesc.setAddress(url.toString());
      
      //Get the Title using a b
      try {
         webDesc.initializeWebInfo();
         action.updateIconsFromWebDesc();
         return action;
      }
      catch(Exception exp) {
         //exp.printStackTrace();
         if(action.edit(owner)) {
            return action;
         }
         else {
            return null;
         }
      }
   }
   
   

}
