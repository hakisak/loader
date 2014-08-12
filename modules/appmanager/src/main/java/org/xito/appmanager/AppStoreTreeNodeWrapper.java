package org.xito.appmanager;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import org.xito.appmanager.store.AppStoreNode;
import org.xito.appmanager.store.GroupNode;

public class AppStoreTreeNodeWrapper extends DefaultMutableTreeNode {
   
   public GroupTreeNodeWrapper getParentGroupTreeNodeWrapper() {
      return (GroupTreeNodeWrapper)getParent();
   }
   
   public GroupNode getParentGroupNode() {
      if(parent != null) {
         return (GroupNode)((GroupTreeNodeWrapper)parent).getUserObject();
      }
      else {
         return null;
      }
   }
   
   public AppStoreNode getAppStoreNode() {
      return (AppStoreNode)getUserObject();
   }
   
   public String toString() {
      return getAppStoreNode().getName();
   }
   
public void setParent(MutableTreeNode newParent) {
      
      //get existing group node parent
      GroupNode existingGroupNode = getParentGroupNode();
      GroupNode newGroupNode = null;
      
      if(newParent != null) newGroupNode = ((GroupTreeNodeWrapper)newParent).getGroupNode();
            
      //remove from existing parent
      if(existingGroupNode != null) {
         existingGroupNode.remove(getAppStoreNode());
         existingGroupNode.setDirty(true);
      }
   
      //add to new group
      if(newGroupNode != null) {
         if(getAppStoreNode().getParent() != newGroupNode) {
         
            newGroupNode.add(getAppStoreNode());
            newGroupNode.setDirty(true);
         }
      }
      
      //call original treeNode setParent
      super.setParent(newParent);
   }
   

}
