package org.xito.launcher.applet;

import java.awt.Frame;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.w3c.dom.Element;
import org.xito.launcher.LauncherAction;
import org.xito.launcher.LauncherActionFactory;
import org.xito.launcher.LauncherService;
import org.xito.launcher.Resources;

/**
 * Factory used to create AppletActions
 * 
 * @author deane
 */
public class AppletActionFactory extends LauncherActionFactory {

   public static final String ELEMENT_NAME = "applet-action";
   
   private static final Logger logger = Logger.getLogger(AppletActionFactory.class.getName());
   
   public AppletActionFactory() {
      setName(Resources.appletBundle.getString("action.display.name"));
      setSmallIcon(AppletActionBeanInfo.icon16);
      setLargeIcon(AppletActionBeanInfo.icon32);
   }
      
   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#createActionFromDataElement(org.w3c.dom.Element)
    */
   @Override
   public LauncherAction createActionFromDataElement(Element element) {
      
      if(element == null) return null;
            
      AppletDesc desc = new AppletDesc();
      initNameAndId(desc, element);
      
      //Use Browser
      if(element.getAttribute("use-browser").equals("true")) {
         desc.setUseWebBrowser(true);
      }
      else {
         desc.setUseWebBrowser(false);
      }
      
      //Separate VM
      if(element.getAttribute("separate-vm").equals("true")) {
         desc.setSeperateVM(true);
      }
      else {
         desc.setSeperateVM(false);
      }
      
      //Address
      String address = element.getAttribute("address");
      if(address != null && !address.equals("")) {
         try {
            desc.setDocumentURL(new URL(address));
         } catch(MalformedURLException badURL) {
            logger.log(Level.WARNING, badURL.getMessage(), badURL);
         }
      }
      
      //Custom info
      org.w3c.dom.NodeList children = element.getChildNodes();
      for(int i=0;i<children.getLength();i++) {
         org.w3c.dom.Node n = children.item(i);
         if(n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            org.w3c.dom.Element appletE = (org.w3c.dom.Element)n;
            if(appletE.getTagName().equals("applet")==false)
               continue;
            
            AppletHTMLParser parser = new AppletHTMLParser();
            AppletDesc customDesc = parser.getAppletDesc(appletE, desc.getDocumentURL());
            desc.updateInfo(customDesc);
         }
      }
      
      return new AppletAction(this, desc);
   }

   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#generateDataElement(org.xito.launcher.LauncherAction)
    */
   @Override
   public Element generateDataElement(LauncherAction action) {
      
      if(action == null) return null;
      if(!(action instanceof AppletAction)) return null;
      
      AppletDesc desc = (AppletDesc)action.getLaunchDesc();
      Element e = createInitialElement(desc);
      
      //Address
      URL url = desc.getDocumentURL();
      if(url != null) {
         e.setAttribute("address", url.toString());
      }
      
      //Use Browser
      if(desc.useWebBrowser()) {
         e.setAttribute("use-browser", "true");
      }
      else {
         e.setAttribute("use-browser", "false");
      }
      
      //Use Separate VM
      if(desc.useSeperateVM()) {
         e.setAttribute("separate-vm", "true");
      }
      else {
         e.setAttribute("separate-vm", "false");
      }
      
      //Use Custom Config
      if(desc.useCustomConfig()) {
         e.setAttribute("custom-config", "true");
      }
      else {
         e.setAttribute("custom-config", "false");
      }
      
      AppletHTMLParser parser = new AppletHTMLParser();
      org.w3c.dom.Element appletE = parser.createElementForAppletDesc(desc, desc.getDocumentURL());
      appletE = (org.w3c.dom.Element)e.getOwnerDocument().importNode(appletE, true);
      e.appendChild(appletE);
      
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
      
      return new AppletAction(this);
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
      
      //Look for Applets
      try {
         AppletHTMLParser parser = new AppletHTMLParser();
         AppletDesc applets[] = parser.parseApplets(url);
         //Return null if we dont have applets
         if(applets == null || applets.length ==0) {
            return null;
         }
         
         //We will use the first one we find
         AppletDesc appletDesc = applets[0];
         AppletAction action = new AppletAction(this, appletDesc);
         if(appletDesc.getName()!=null || appletDesc.getTitle()!=null) {
            return action;
         }
         
         //Edit the action because don't know the name or title
         if(action.edit(owner)) {
            return action;
         }
         //They canceled
         else {
            return null;
         }
      }
      catch(IOException ioExp) {
         //if this happens just return null because we can't read applets
         return null;
      }
   }

}
