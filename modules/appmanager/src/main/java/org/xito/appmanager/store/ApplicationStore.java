package org.xito.appmanager.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.tree.TreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xito.appmanager.ApplicationTreeNodeWrapper;
import org.xito.appmanager.GroupTreeNodeWrapper;
import org.xito.appmanager.AppTreeModel;
import org.xito.boot.Boot;
import org.xito.boot.CacheManager;
import org.xito.xmldocs.DefaultXMLDocumentService;
import org.xito.xmldocs.XMLDocumentNotFound;
import org.xito.xmldocs.XMLDocumentService;
import org.xito.launcher.LauncherAction;
import org.xito.launcher.LauncherService;

public class ApplicationStore {

   private final static Logger logger = Logger.getLogger(ApplicationStore.class.getName());

   private final static ApplicationStore singleton = new ApplicationStore();

   private final static String ROOT_ID = "root";
   
   private XMLDocumentService docService;
   
   protected static final String APPMANAGER_DIR = "/appmanager";
   
   /**
    * Private Constructor for Singleton
    * 
    */
   private ApplicationStore() {
      init();
   }

   /**
    * Init
    */
   private void init() {
      // Unzip Default Apps
      docService = DefaultXMLDocumentService.getDefaultService();
      if (docService.dirExists(APPMANAGER_DIR) == false) {
         unzipDefaultApps();
      }
   }

   /**
    * Get Instance
    * 
    * @return
    */
   public static ApplicationStore getInstance() {
      
      return singleton;
   }

   /**
    * Store an Application
    * 
    * @param node
    * @throws StoreException
    */
   public void storeApplication(ApplicationNode node) throws StoreException {

      try {
         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = builderFactory.newDocumentBuilder();
         Document doc = builder.newDocument();
         LauncherAction action = node.getAction();
         Element e = action.getFactory().generateDataElement(action);
         if (e == null)
            return;

         doc.appendChild(doc.importNode(e, true));

         XMLDocumentService docService = DefaultXMLDocumentService.getDefaultService();
         docService.storeDocument(getFullPath(node.getUniqueID()), doc);
         node.setDirty(false);

      } catch (Exception parserExp) {
         throw new StoreException("Error storing Application", parserExp);
      }
   }

   /**
    * Traverses all Children and stores and dirty nodes
    * @param node
    */
   public void storeAllDirty(GroupNode node) throws StoreException {
      
      if(node == null) return;
      
      //Store this node
      if(node.isDirty()) {
         storeGroup(node);
      }
      
      //store any dirty children
      AppStoreNode[] children = node.getNodes();
      for(int i=0;i<children.length;i++) {
         
         //store dirty children
         if(children[i].isDirty()) {
            
               if(children[i] instanceof GroupNode) {
                  System.out.println("Storing group node:" + children[i].toString());
                  storeGroup((GroupNode)children[i]);
               }
               else if(children[i] instanceof ApplicationNode){
                  System.out.println("Storing application node:" + children[i].toString());
                  storeApplication((ApplicationNode)children[i]);
               }
         }
         
         //now store all children
         if(children[i] instanceof GroupNode) {
            storeAllDirty((GroupNode)children[i]);
         }
         
      }
   }
   
   /**
    * Store a Group
    * @param node
    * @throws StoreException
    */
   public void storeGroup(GroupNode node) throws StoreException {
      
      if(node == null) throw new StoreException("null GroupNode");
      
      try {
         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = builderFactory.newDocumentBuilder();
         Document doc = builder.newDocument();
         Element e = doc.createElement("group");
         doc.appendChild(e);
         
         e.setAttribute("name", node.getName());
         e.setAttribute("id", node.getUniqueID());
         
         //Store each Child Item
         AppStoreNode apps[] = node.getNodes();
         for(int i=0;i<apps.length;i++) {
         
            Element item = doc.createElement("item");
            item.setAttribute("id", apps[i].getUniqueID());
            item.setAttribute("type", apps[i].getType());
            e.appendChild(item);
         }
         
         XMLDocumentService docService = DefaultXMLDocumentService.getDefaultService();
         docService.storeDocument(getFullPath(node.getUniqueID()), doc);
         node.setDirty(false);
      } 
      catch(Exception exp) {
         throw new StoreException("Error storing root groups", exp);
      }
   }

   /**
    * Get all Groups and their associated Applications
    * 
    * @return
    */
   public GroupNode getRootNode() throws StoreException {
      
      return loadGroup(ROOT_ID);
   }
  
   /**
    * Delete a Node with the following ID
    * @param id
    * @throws StoreException
    */
   public void delete(String id) throws StoreException {
      String fullPath = getFullPath(id);
      try {
         docService.removeDocument(fullPath);
      } catch (IOException ioExp) {
         throw new StoreException("An Error Occurred Deleting id:" + id, ioExp);
      }
   }
   
   public void deleteTree(AppStoreNode node) throws StoreException {
      
      //delete any children
      if(node instanceof GroupNode && ((GroupNode)node).size()>0) {
         AppStoreNode children[] = ((GroupNode)node).getNodes();
         for(int i=0;i<children.length;i++) {
            deleteTree(children[i]);
         }
      }
      
      //delete the node itself
      if(node.getParent() != null) node.getParent().remove(node);
      delete(node.getUniqueID());
   }

   private String getFullPath(String id) {

      return APPMANAGER_DIR + XMLDocumentService.PATH_SEPERATOR + id;
   }

   /**
    * Load a Group from its ID
    * @param id
    * @return Array of GroupNodes, because we use to support subgroups
    */
   private GroupNode loadGroup(String id) {

      // Ensure your have no children first
      docService = DefaultXMLDocumentService.getDefaultService();
      Document d = null;
      Element e = null;
      // if new Group create the Group Document and be done
      try {
         d = docService.getDocument(getFullPath(id));
         e = d.getDocumentElement();
         if (e.getTagName().equals("group") == false) {
            return null;
         }
      } catch (Exception exp) {
         logger.log(Level.SEVERE, exp.getMessage(), exp);
         return null;
      }

      // Group Name
      GroupNode group = new GroupNode(id, e.getAttribute("name"));

      // Load Children
      NodeList children = e.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
         if (children.item(i).getNodeType() != Node.ELEMENT_NODE)
            continue;

         Element child = (Element) children.item(i);
         if (child.getTagName().equals("item") == false)
            continue;

         String childID = child.getAttribute("id");
         String type = child.getAttribute("type");
         Document childDoc = null;
         try {
            childDoc = docService.getDocument(APPMANAGER_DIR + XMLDocumentService.PATH_SEPERATOR + childID);
         } catch (Exception exp) {
            logger.log(Level.SEVERE, exp.getMessage(), exp);
         }

         // Child is App
         if (type.equals("app")) {
            try {
               LauncherAction action = LauncherService.createActionForElement(childDoc.getDocumentElement());
               if (action != null) {
                  group.add(new ApplicationNode(action));
               }
            } catch (Exception exp) {
               logger.log(Level.SEVERE, exp.getMessage(), exp);
            }
         }
         // Child is Group.
         else if (type.equals("group")) {
            group.add(loadGroup(childID));
         }
      }
      
      return group;
   }

   /**
    * Unzip the contents of the default_apps.zip
    */
   private void unzipDefaultApps() {

      logger.info("Unzipping Default Apps...");
      File file = null;
      ZipFile zipFile = null;
      try {
         URL cs = ApplicationStore.class.getProtectionDomain().getCodeSource().getLocation();
         URL defaultAppsURL = new URL(Boot.getCacheManager().convertFromCachedURL(cs), "default_apps.zip");
         if (!defaultAppsURL.getProtocol().equals("file")) {
            CacheManager cm = Boot.getCacheManager();
            cm.downloadResource(defaultAppsURL, null);
            file = cm.getCachedFileForURL(defaultAppsURL);
         } else {
            file = new File(defaultAppsURL.getFile());
         }

         // Check to see if the file exists
         logger.info("default apps file:" + file.toString());
         if (file.exists() == false) {
            logger.warning("default apps file was not found");
            return;
         }

         zipFile = new ZipFile(file);
      } catch (Exception exp) {
         exp.printStackTrace();
         return;
      }

      XMLDocumentService docService = DefaultXMLDocumentService.getDefaultService();
      Enumeration entries = zipFile.entries();
      while (entries.hasMoreElements()) {
         ZipEntry entry = (ZipEntry) entries.nextElement();
         try {
            InputStream in = zipFile.getInputStream(entry);
            OutputStream out = docService.getOutputStream(APPMANAGER_DIR + XMLDocumentService.PATH_SEPERATOR
                  + entry.getName());
            byte buf[] = new byte[1024];
            int c = in.read(buf);
            while (c != -1) {
               out.write(buf, 0, c);
               c = in.read(buf);
            }
            out.close();
            in.close();
         } catch (IOException ioExp) {
            ioExp.printStackTrace();
         }
      }
   }
   
   /**
    * Class used to contain all groups loaded along with 
    * a flag indicating groups incorrectly contained other groups
    * 
    */
   private class GroupSet {
      public GroupNode[] groups;
      public boolean groupMismatch = false;
   }
}
