package org.xito.launcher.jnlp;

import java.awt.Frame;
import java.net.URL;
import java.security.AllPermission;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.xito.boot.NativeLibDesc;
import org.xito.launcher.LauncherAction;
import org.xito.launcher.LauncherActionFactory;
import org.xito.launcher.Resources;
import org.xito.launcher.applet.AppletAction;

public class JavaActionFactory extends LauncherActionFactory {

   private static final Logger logger = Logger.getLogger(JavaActionFactory.class.getName());
   
   public static final String ELEMENT_NAME = "java-action";
   
   public JavaActionFactory() {
      setName(Resources.javaBundle.getString("action.display.name"));
      setSmallIcon(JavaActionBeanInfo.icon16);
      setLargeIcon(JavaActionBeanInfo.icon32);
   }
   
   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#createAction()
    */
   @Override
   public LauncherAction createAction() {
      
      return new JavaAction(this);
   }
   
   @Override
   public LauncherAction createAction(Frame owner, Object obj) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public LauncherAction createActionFromDataElement(Element element) {
      
      
      JavaAppDesc desc = new JavaAppDesc();
      
      initNameAndId(desc, element);
      
      //Main Class
      String mainClass = element.getAttribute("main-class");
      if(mainClass != null && !mainClass.equals("")) {
         desc.setMainClass(mainClass);
      }
      
      //Arguments
      String args = element.getAttribute("args");
      if(args != null && !args.equals("")) {
         desc.setMainArgs(args.split(" "));
      }
      else {
         desc.setMainArgs(null);
      }
            
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
      
      //Shared ClassLoader
      if(element.getAttribute("shared-classloader").equals("true")) {
         desc.setUseSharedClassLoader(true);
      }
      else {
         desc.setUseSharedClassLoader(false);
      }
      
      //Process Resources
      org.w3c.dom.NodeList children = element.getChildNodes();
      org.w3c.dom.Element resources = null;
      for(int i=0;i<children.getLength();i++) {
         org.w3c.dom.Node n = children.item(i);
         if(n.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
            continue;
         }
         if(n.getNodeName().equals("resources")) {
            resources = (org.w3c.dom.Element)n;
            break;
         }
      }
      
      if(resources == null) {
         return new JavaAction(this, desc);
      }
      
      ArrayList javaArchives = new ArrayList();
      ArrayList nativeArchives = new ArrayList();
      children = resources.getChildNodes();
      for(int i=0;i<children.getLength();i++) {
         org.w3c.dom.Node n = children.item(i);
         if(n.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
            continue;
         }
         if(n.getNodeName().equals("jar")) {
            String jarURL = (String)((org.w3c.dom.Element)n).getAttribute("href");
            try {
               javaArchives.add(new URL(jarURL));
            }
            catch(Exception e) {
               logger.warning("Invalid Resource URL:"+jarURL);
            }
         }
         else if(n.getNodeName().equals("nativelib")) {
            String libURL = (String)((org.w3c.dom.Element)n).getAttribute("href");
            String os = (String)((org.w3c.dom.Element)n).getAttribute("os");
            try {
               nativeArchives.add(new NativeLibDesc(os, new URL(libURL)));
            }
            catch(Exception e) {
               logger.warning("Invalid Resource URL:"+libURL);
            }
         }
      }
      
      desc.setJavaArchives(javaArchives);
      desc.setNativeArchives(nativeArchives);
      
      //Permissions
      String perms = element.getAttribute("permissions");
      if(perms != null && perms.equals("all-permissions")) {
         Permissions p = new Permissions();
         p.add(new AllPermission());
         desc.setPermissions(p);
      }
      else {
         desc.setPermissions(null);
      }
      
      return new JavaAction(this, desc);
   }

   @Override
   public Element generateDataElement(LauncherAction action) {
      
      JavaAppDesc desc = (JavaAppDesc)action.getLaunchDesc();
      org.w3c.dom.Element e = createInitialElement(desc);
      
      //Main Class
      e.setAttribute("main-class", desc.getMainClass());
      
      //Arguments
      String[] args = desc.getMainArgs();
      StringBuffer argLine = new StringBuffer();
      if(args != null) {
         for(int i=0;i<args.length;i++) {
            argLine.append(args[i] + " ");
         }
      }
      e.setAttribute("args", argLine.toString().trim());
      
      //Permissions
      if(desc.getPermissions() != null) {
         e.setAttribute("permissions", "all-permissions");
      }
      else {
         e.setAttribute("permissions", "restricted-permissions");
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
      
      //Use Separate VM
      if(desc.useSharedClassLoader()) {
         e.setAttribute("shared-classloader", "true");
      }
      else {
         e.setAttribute("shared-classloader", "false");
      }
      
      //Java Archives
      org.w3c.dom.Element resources = e.getOwnerDocument().createElement("resources");
      e.appendChild(resources);
      Iterator it = desc.getJavaArchives().iterator();
      while(it.hasNext()) {
         URL javaResource = (URL)it.next();
         org.w3c.dom.Element jarElement = e.getOwnerDocument().createElement("jar");
         jarElement.setAttribute("href", javaResource.toString());
         resources.appendChild(jarElement);
      }
      
      //Native Archives
      it = desc.getNativeArchives().iterator();
      while(it.hasNext()) {
         NativeLibDesc nativeLib = (NativeLibDesc)it.next();
         org.w3c.dom.Element nativeElement = e.getOwnerDocument().createElement("nativelib");
         nativeElement.setAttribute("href", nativeLib.getPath().toString());
         nativeElement.setAttribute("os", nativeLib.getOS());
         resources.appendChild(nativeElement);
      }
      
      return e;
   }

   @Override
   public String getElementName() {
      return ELEMENT_NAME;
   }

}
