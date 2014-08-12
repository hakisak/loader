package org.xito.launcher.jnlp;

import java.awt.Frame;
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

public class JNLPActionFactory extends LauncherActionFactory {

   private static final Logger logger = Logger.getLogger(JNLPActionFactory.class.getName());
   public static final String ELEMENT_NAME = "jnlp-action";
   
   public JNLPActionFactory() {
      setName(Resources.jnlpBundle.getString("action.display.name"));
      setSmallIcon(JNLPActionBeanInfo.icon16);
      setLargeIcon(JNLPActionBeanInfo.icon32);
   }
   
   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#createActionFromDataElement(org.w3c.dom.Element)
    */
   @Override
   public LauncherAction createActionFromDataElement(Element element) {
            
      JNLPAppDesc desc = new JNLPAppDesc();
      initNameAndId(desc, element);
            
      String address = element.getAttribute("address");
      
      
      //Use webstart
      if(element.getAttribute("use-webstart").equals("true")) {
         desc.setUseWebStart(true);
      }
      else {
         desc.setUseWebStart(false);
      }
      
      //Separate VM
      if(element.getAttribute("separate-vm").equals("true")) {
         desc.setSeperateVM(true);
      }
      else {
         desc.setSeperateVM(false);
      }
      
      //address
      if(address != null && !address.equals("")) {
         try {
            desc.setJNLPAddress(new URL(address));
         } catch(MalformedURLException badURL) {
            logger.log(Level.WARNING, badURL.getMessage(), badURL);
         }
      }
      
      return new JNLPAction(this, desc);
   }

   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#generateDataElement(org.xito.launcher.LauncherAction)
    */
   @Override
   public Element generateDataElement(LauncherAction action) {
      
      JNLPAppDesc desc = (JNLPAppDesc)action.getLaunchDesc();
      org.w3c.dom.Element e = createInitialElement(desc);
            
      //Address
      URL url = desc.getJNLPAddress();
      if(url != null) {
         e.setAttribute("address", url.toString());
      }
      
      //Use WebStart
      if(desc.useWebStart()) {
         e.setAttribute("use-webstart", "true");
      }
      else {
         e.setAttribute("use-webstart", "false");
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
      
      return new JNLPAction(this);
   }
   
   @Override
   public LauncherAction createAction(Frame owner, Object obj) {
      //Can't be null
      if(obj == null)
         return null;
      
      //It needs to be a URL object
      if(!(obj instanceof URL)) 
         return null;
      
      //Must be a url then ends in .jnlp
      //TODO need to support none .jnlp files that are the correct MIME Type
      URL url = (URL)obj;
      if(url.toString().endsWith(".jnlp") == false) {
         return null;
      }
      
      JNLPAction action = new JNLPAction(this);
      ((JNLPAppDesc)action.getLaunchDesc()).setJNLPAddress(url);
      try {
         ((JNLPAppDesc)action.getLaunchDesc()).initializeJNLPInfo();
         return action;
      }
      catch(Exception exp) {
         exp.printStackTrace();
         if(action.edit(owner)) {
            return action;
         }
         else {
            return null;
         }
      }
   }

}
