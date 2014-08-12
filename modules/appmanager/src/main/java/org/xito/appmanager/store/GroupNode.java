package org.xito.appmanager.store;

import java.util.ArrayList;
import java.util.logging.Logger;

public class GroupNode extends AppStoreNode {
   
   public static final String TYPE = "group";
   
   private static final Logger logger = Logger.getLogger(GroupNode.class.getName());
   
   private ArrayList children = new ArrayList();
   
   public GroupNode(String id, String name) {

      this.name = name;
      if(id == null) {
         this.id = ""+System.currentTimeMillis();
      }
      else {
         this.id = id;
      }
   }
   
   public String getType() {
      return TYPE;
   }
   
   public GroupNode(String id, String name, boolean dirty) {
      this(id, name);
      setDirty(dirty);
   }
   
   public void setName(String name) {
      super.name = name;
   }
   
   public void remove(AppStoreNode node) {
      logger.fine("remove node: " + node.toString() + " from: " + this.toString());
      node.setParent(null);
      children.remove(node);
   }
   
   public void add(AppStoreNode node) {
      logger.fine("adding node: " + node.toString() + " to: " + this.toString());
      node.setParent(this);
      children.add(node);
   }
   
   public AppStoreNode[] getNodes() {
      return (AppStoreNode[])children.toArray(new AppStoreNode[0]);
   }
   
   public int size() {
      return children.size();
   }
   
   public AppStoreNode get(int i) {
      return (AppStoreNode)children.get(i);
   }
}
