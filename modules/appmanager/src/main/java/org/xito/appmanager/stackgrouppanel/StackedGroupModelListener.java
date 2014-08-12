package org.xito.appmanager.stackgrouppanel;

public interface StackedGroupModelListener {

   public void groupAdded(StackedGroupModel model, int index, StackedGroup group);
   
   public void groupDeleted(StackedGroupModel model, int index);
   
   public void groupMoved(StackedGroupModel model, StackedGroup group, int index);
   
   public void itemAdded(StackedGroupModel model, StackedGroup parent, int index, StackedGroupItem item);
   
   public void itemDeleted(StackedGroupModel model, StackedGroup parent, int index);
   
   public void itemMoved(StackedGroupModel model, StackedGroup parent, StackedGroupItem item, int index);
   
   
}
