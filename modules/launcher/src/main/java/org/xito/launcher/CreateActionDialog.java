// Copyright 2007 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.launcher;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;
import javax.swing.*;

import org.xito.dialog.*;

/**
 *
 * @author Deane Richan
 */
public class CreateActionDialog extends CustomDialog {
   
   private static final Logger logger = Logger.getLogger(CreateActionDialog.class.getName());
   private MainPanel mainPanel;
   private JList actionList;
   
   /** 
    * CreateActionDialog
    */
   public CreateActionDialog(Frame owner) {
      this(owner, null);
   }
   
   /** 
    * CreateActionDialog
    */
   public CreateActionDialog(Frame owner, java.util.List actions) {
      super(owner);
   
      if(actions == null) {
         this.actionList = new JList(LauncherService.getActionFactories());
      }
      else {
         this.actionList = new JList(new java.util.Vector(actions));
      }
      
      super.descriptor = createDialogDesc();
      super.setModal(true);
      super.setTitle(Resources.bundle.getString("create.action.title"));
      super.init();
   }
   
   /**
    * Create the Dialog Desc for this Dialog
    */
   private DialogDescriptor createDialogDesc() {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setType(DialogManager.OK_CANCEL);
      desc.setGradiantColor(org.xito.boot.ui.Defaults.DIALOG_GRAD_COLOR);
      desc.setGradiantOffsetRatio(org.xito.boot.ui.Defaults.DIALOG_GRAD_OFFSET);
      desc.setTitle(Resources.bundle.getString("create.action.title"));
      desc.setSubtitle(Resources.bundle.getString("create.action.subtitle"));
      desc.setIcon(new ImageIcon(CreateActionDialog.class.getResource("/org/xito/launcher/images/new_app_32.png")));
      desc.setWidth(400);
      desc.setHeight(350);
      desc.setResizable(true);
      
      mainPanel = new MainPanel();
      desc.setCustomPanel(mainPanel);
      
      return desc;
   }
   
   /**
    * Get the Action Class That was Selected
    */
   public LauncherActionFactory getActionFactory() {
      super.setVisible(true);
      int result = super.getResult();
      if(result == DialogManager.OK) {
         return mainPanel.getSelectedActionFactory();
      }
            
      return null;
   }
   
   /**
    * The Main Panel of the Dialog
    */
   private class MainPanel extends JPanel {
      
      public MainPanel() {
         init();
      }
      
      /**
       * Build the Panel
       */
      private void init() {
   
         setLayout(new TableLayout(CreateActionDialog.class.getResource("create_action_layout.html")));
                  
         //Description
         JLabel lbl = new JLabel(Resources.bundle.getString("create.action.desc"));
         add("description", lbl);
                  
         actionList.setCellRenderer(new MyCellRenderer());
         actionList.setSelectedIndex(0);
         actionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         actionList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
               if(evt.getClickCount()<2) return;
               CreateActionDialog.this.dialogResult = DialogManager.OK;
               CreateActionDialog.this.setVisible(false);
            }
         });
         
         add("actions", new JScrollPane(actionList));
      }
      
      private LauncherActionFactory getSelectedActionFactory() {
         return (LauncherActionFactory)actionList.getSelectedValue();
      }
   }
   
   /**
    * Cell Renderer for List of Action
    */
   private class MyCellRenderer extends DefaultListCellRenderer { 
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
         JLabel lbl = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
         if(!(value instanceof Class)) {
            lbl.setText("unknown");
         }
         
         LauncherActionFactory factory = (LauncherActionFactory)value;
         lbl.setIcon(factory.getSmallIcon());
         lbl.setText(factory.getName());
                           
         return lbl;
      }
   }
}
