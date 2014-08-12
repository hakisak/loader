package org.xito.appmanager.store;

public abstract class AppStoreNode {

   protected String name;
   protected String id;
   protected boolean dirty;
   protected GroupNode parentNode;
   
   public abstract String getType();
   
   public String getName() {
      return name;
   }
   
   public String getUniqueID() {
      return id;
   }

   public boolean isDirty() {
      return dirty;
   }

   public void setDirty(boolean dirty) {
      this.dirty = dirty;
   }
   
   protected void setParent(GroupNode newParentNode) {
      parentNode = newParentNode;
   }
   
   public GroupNode getParent() {
      return parentNode;
   }
   
   public String toString() {
      return getName();
   }

}
