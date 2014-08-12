package org.xito.appmanager.store;

import javax.swing.Action;

import org.xito.launcher.LauncherAction;

public class ApplicationNode extends AppStoreNode {

   public static final String TYPE = "app";
   
   private LauncherAction action;
   
   public ApplicationNode(LauncherAction action) {
      this.action = action;
   }
   
   public ApplicationNode(LauncherAction action, boolean dirty) {
      this.action = action;
      setDirty(dirty);
   }
   
   public String getType() {
      return TYPE;
   }
   
   public LauncherAction getAction() {
      return action;
   }
   
   public String getUniqueID() {
      return action.getLaunchDesc().getUniqueID();
   }
   
   public String getName() {
      return (String)action.toString();
   }
}
