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

package org.xito.appmanager;

import org.xito.dazzle.widget.DefaultStyle;
import org.xito.dazzle.widget.button.ImageButton;
import org.xito.dazzle.widget.panel.GradientPanel;
import org.xito.dazzle.utilities.UIUtilities;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;

import org.xito.dazzle.*;
import org.xito.dazzle.worker.BusyWorker;
import org.xito.dazzle.worker.BusyWorkerAdapter;
import org.xito.dialog.*;
import org.xito.launcher.*;
import org.xito.appmanager.store.ApplicationNode;
import org.xito.appmanager.store.GroupNode;
import org.xito.boot.Boot;
import org.xito.boot.OfflineListener;

/**
 *
 * @author Deane Richan
 */
public class MainPanel extends GradientPanel {
   
   //private static Color topGradColor = new Color(100,100,100); 
   //private static Color bottomGradColor = new Color(75,75,75);
   private static Color topGradColor = new Color(125,125,125);
   private static Color bottomGradColor = new Color(125,125,125);
   
   private static Color hoverColor = new Color(0xafbfed);
   
   private ImageButton addBtn;
   private ImageButton removeBtn;
   private JTextField commandTF;
   private JLabel statusLbl;
   private JPanel topPanel;
   private JPanel bottomPanel;
   private AppTree tree;
   private AppTreeModel model;
   private JPopupMenu addPopupMnu;
   private TableLayout layout;
   
   private String onlineText = Resources.bundle.getString("status.online");
   private String offlineText = Resources.bundle.getString("status.offline");
   
   /** Creates a new instance of MainPanel */
   public MainPanel() {
      super(topGradColor, bottomGradColor, 1.0f, SwingConstants.SOUTH);
      init();
   }
   
   private void init() {
      
      layout = new TableLayout(MainPanel.class.getResource("main_layout.html"));
      setLayout(layout);
            
      //Top Panel
      initTopPanel();
            
      //Bottom Panel
      initBottomPanel();
      
      //Tree
      buildTree();
   }

   /**
    * Initialize the Top panel
    */
   private void initTopPanel() {
      topPanel = new JPanel(new BorderLayout());
      topPanel.setOpaque(false);
      commandTF = new JTextField();
      commandTF.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            LauncherService.launch(commandTF.getText());
         }
         
      });
      topPanel.add(commandTF);
      add("top", topPanel);
   }

   private void initBottomPanel() {
      bottomPanel = new JPanel(new BorderLayout());
      bottomPanel.setOpaque(false);
      
      //Buttons
      initPopupMenu();
      JPanel buttonPanel = new JPanel(new FlowLayout(0,0, FlowLayout.LEFT));
      buttonPanel.setOpaque(false);
      addBtn = new ImageButton(
            new ImageIcon(ImageManager.getImageByName("plus.png")),
            new ImageIcon(ImageManager.getImageByName("plus_pressed.png")),
            new ImageIcon(ImageManager.getImageByName("plus_off.png")));
      addBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            int h=addPopupMnu.getHeight();
            System.out.println(h);
            addPopupMnu.show(addBtn, 0, -1 * h);
         }
      });
      
      buttonPanel.add(addBtn);
      
      removeBtn = new ImageButton(new ImageIcon(ImageManager.getImageByName("minus.png")),
            new ImageIcon(ImageManager.getImageByName("minus_pressed.png")),
            new ImageIcon(ImageManager.getImageByName("minus_off.png")));
      removeBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
            deleteNode(node);
         }
         
      });
      buttonPanel.add(removeBtn);
      bottomPanel.add(buttonPanel, BorderLayout.WEST);
      
      //status
      String status = (Boot.isOffline()?offlineText:onlineText);
      Boot.addOfflineListener(new OfflineListener() {
         public void offline() {
            statusLbl.setText(offlineText);
         }
         public void online() {
            statusLbl.setText(onlineText);
         }
      });
      
      statusLbl = new JLabel(status);
      statusLbl.setFont(DefaultStyle.getDefaults().getFont(DefaultStyle.LABEL_FONT_KEY));
      statusLbl.setForeground(Color.WHITE);
      statusLbl.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));
      bottomPanel.add(statusLbl, BorderLayout.EAST);
      add("bottom", bottomPanel);
   }
   
   private void initPopupMenu() {
      addPopupMnu = new JPopupMenu();
      JMenuItem addAliasMI = new JMenuItem(new AbstractAction() {
         {
            putValue(Action.NAME, Resources.bundle.getString("add.app.menu"));
         }
         public void actionPerformed(ActionEvent e) {
            addApplication();
         }
      });
      JMenuItem addGroupMI = new JMenuItem(new AbstractAction() {
         {
            putValue(Action.NAME, Resources.bundle.getString("add.group.menu"));
         }
         public void actionPerformed(ActionEvent e) {
            addGroup();
         }
      });
      addPopupMnu.add(addAliasMI);
      addPopupMnu.add(addGroupMI);
   }
   
   /**
    * Build the Tree
    *
    */
   private void buildTree() {
      
      UIManager.put("Tree.hash", UIManager.getColor("Tree.background"));
      tree = new AppTree("main.tree");
      tree.setRootVisible(false);
      tree.setCellRenderer(new AppTreeCellRenderer());
      tree.addMouseListener(new MyTreeMouseAdapter());
      tree.setBorder(new EmptyBorder(3,3,3,3));
      tree.setDragEnabled(true);
      
      JScrollPane sp = new JScrollPane(tree);
      sp.setBorder(null);
      //sp.getViewport().setBorder(null);
      
      //sp.setBorder(null);
      add("tree", sp);

      //new TreeDNDHandler(tree, this);
      
      //int h = Toolkit.getDefaultToolkit().getFontMetrics(UIManager.getFont("Label.font")).getHeight();
      //tree.setRowHeight(h + 2);
      
      //start a background thread to load the AppTreeModel
      model = new AppTreeModel();
      tree.setModel(model);
   }
   
   /**
    * Initialize the applications in the Tree
    * @param blockingWindow
    */
   protected void initTree(final Window blockingWindow, final boolean launchStartupApps) {
      AppStoreFetchWorker worker = new AppStoreFetchWorker(blockingWindow, tree);
      worker.addBusyWorkerListener(new BusyWorkerAdapter() {

         @Override
         public void workerFinished(BusyWorker worker) {
            if(launchStartupApps) launchStartup();
         }
         
      });
      worker.invokeLater();
   }
   
   /**
    * launch Startup Apps
    */
   private void launchStartup() {
      
      GroupTreeNodeWrapper root = (GroupTreeNodeWrapper)model.getRoot();
      Enumeration children = root.children();
      
      while(children.hasMoreElements()) {
         Object node = children.nextElement();
         if(node instanceof GroupTreeNodeWrapper) {
            if(node.toString().equalsIgnoreCase("startup")) {
               Enumeration startupNodes = ((GroupTreeNodeWrapper)node).children();
               while(startupNodes.hasMoreElements()) {
                  Object startupNode = startupNodes.nextElement();
                  if(startupNode instanceof ApplicationTreeNodeWrapper) {
                     try {
                        ((ApplicationTreeNodeWrapper)startupNode).getAction().actionPerformed(null);
                     }
                     catch(Throwable t) {
                        t.printStackTrace();
                     }
                  }
               }
               break;
            }
         }
      }
   }
   
   /**
    * Get the TreeModel
    */
   public AppTreeModel getTreeModel() {
      return model;
   }
   
   /**
    * Add a Group to this Manager
    */
   protected void addGroup() {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setTitle(Resources.bundle.getString("add.group.title"));
      desc.setSubtitle(Resources.bundle.getString("add.group.subtitle"));
      //desc.setGradiantColor(org.xito.boot.ui.Defaults.DIALOG_GRAD_COLOR);
      //desc.setGradiantOffsetRatio(org.xito.boot.ui.Defaults.DIALOG_GRAD_OFFSET);
      desc.setType(DialogManager.OK_CANCEL);
      desc.setIcon(new ImageIcon(MainPanel.class.getResource("/org/xito/launcher/images/folder_maji_32.png")));
      desc.setWidth(350);
      
      JPanel p = new GroupNamePanel();
      desc.setCustomPanel(p);
      
      int result = DialogManager.showDialog((Frame)null, desc);
      if(result == DialogManager.OK) {
         String name = p.getName();
         GroupNode node = new GroupNode(null, name);
         TreePath parentPath = tree.getSelectionPath();
         model.addNode(parentPath, node);
      }
   }
   
   private void editAction(ApplicationTreeNodeWrapper node) {
      LauncherAction action = (LauncherAction)((ApplicationTreeNodeWrapper)node).getAction();
      boolean success = action.edit(null);
      if(success) {
         model.nodeChanged(node);
      }
   }
   
   private void editGroup(GroupTreeNodeWrapper treeNode) {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setTitle(Resources.bundle.getString("edit.group.title"));
      desc.setSubtitle(Resources.bundle.getString("edit.group.subtitle"));
      desc.setType(DialogManager.OK_CANCEL);
      desc.setIcon(new ImageIcon(MainPanel.class.getResource("/org/xito/launcher/images/folder_32.png")));
      desc.setWidth(300);
      
      JPanel p = new GroupNamePanel();
      p.setName(treeNode.getGroupNode().getName());
      desc.setCustomPanel(p);
      
      int result = DialogManager.showDialog((Frame)null, desc);
      if(result == DialogManager.OK) {
         String name = p.getName();
         treeNode.getGroupNode().setName(name);
         model.nodeChanged(treeNode);
      }
   }
   
   /**
    * Add an Application to this Manager
    */
   protected void addApplication() {
      
      LauncherService.createAction((Frame)SwingUtilities.getWindowAncestor(this), new LauncherActionCreatedListener() {
         
         public void launcherActionCreated(LauncherAction action) {
            if(action != null) {
               TreePath parentPath = tree.getSelectionPath();
               TreePath childPath = model.addNode(parentPath, new ApplicationNode(action, true));
               tree.scrollPathToVisible(childPath);
            }
         }
      });
      
   }
   
   /**
    * Edit Node on App Tree
    */
   public void editNode(TreeNode node) {
      if(node instanceof ApplicationTreeNodeWrapper) {
         editAction((ApplicationTreeNodeWrapper)node);
      }
      else if(node instanceof GroupTreeNodeWrapper) {
         editGroup((GroupTreeNodeWrapper)node);
      }
   }
   
   /**
    * Delete a Node from the AppTree
    */
   public void deleteNode(DefaultMutableTreeNode node) {
  
      Frame frame = (Frame)SwingUtilities.getWindowAncestor(MainPanel.this);
      
      String title = null;
      String subtitle = null;
      String msg = null;
            
      if(node instanceof ApplicationTreeNodeWrapper) {
         title = Resources.bundle.getString("delete.app.title");
         subtitle = Resources.bundle.getString("delete.app.subtitle");
         msg = Resources.bundle.getString("delete.app.message");
         msg = MessageFormat.format(msg, node.toString());
      }
      else if(node instanceof GroupTreeNodeWrapper) {
         title = Resources.bundle.getString("delete.group.title");
         subtitle = Resources.bundle.getString("delete.group.subtitle");
         msg = Resources.bundle.getString("delete.group.message");
         msg = MessageFormat.format(msg, node.toString());
      }
      else {
         //shouldn't happen
         return;
      }
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setTitle(title);
      desc.setSubtitle(subtitle);
      desc.setMessage(msg);
      desc.setType(DialogManager.YES_NO);
      desc.setMessageType(DialogManager.WARNING_MSG);
      desc.setShowButtonSeparator(true);
      
      int result = DialogManager.showDialog((Frame)null, desc);
      if(result == DialogManager.YES) {
         model.removeNode(node);
      }
   }
   
   /**
    * Move a Tree Node to a new Path
    */
   public void moveNode(MutableTreeNode node, TreePath destPath, boolean above) {
      
      //Default parent to Root Node
      MutableTreeNode newParentNode = null;
      MutableTreeNode oldParentNode = (MutableTreeNode)node.getParent();
      
      //First remove from the Parent
      model.removeNodeFromParent(node);
      int index = 0;
            
      //If no path then use Root
      if(destPath == null) {
         newParentNode = (MutableTreeNode)model.getRoot();
         index = newParentNode.getChildCount();
      }
      //Find Parent node using Path
      else {
         MutableTreeNode selectedNode = (MutableTreeNode)destPath.getLastPathComponent();
         if(selectedNode.getAllowsChildren() == false) {
            newParentNode = (MutableTreeNode)selectedNode.getParent();
            if(newParentNode == null) {
               newParentNode = (MutableTreeNode)model.getRoot();
            }
            index = newParentNode.getIndex(selectedNode);
         }
         else {
            newParentNode = selectedNode;
         }
      }
      
      //Fix index if above
      if(above && index>0) {
         index--;
      }
      
      System.out.println("moving to:"+newParentNode.toString()+" index:"+index);
      model.insertNodeInto(node, newParentNode, index);
   }
   
   public void storeTreeNodes() {
      model.storeDirtyNodes();
   }
      
   /**
    * Tree Mouse Adapter
    */
   public class MyTreeMouseAdapter extends MouseAdapter {

      @Override
      public void mouseClicked(MouseEvent evt) {
         
         TreePath path = tree.getPathForLocation(evt.getX(), evt.getY());
         tree.setSelectionPath(path);
         
         //Get Selected Node
         path = tree.getSelectionPath();
         if (path == null) {
            return;
         }
         
         final DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
         if(node instanceof ApplicationTreeNodeWrapper) {
            //If double click then perform Action
            if(evt.getClickCount() == 2 && !UIUtilities.isSecondaryMouseButton(evt)) {
               ActionEvent ae = new ActionEvent(tree, -1, null);
               ((ApplicationTreeNodeWrapper)node).getAction().actionPerformed(ae);
               return;
            }
         }
         
         if(UIUtilities.isSecondaryMouseButton(evt)) {
            MyPopupMenu m = new MyPopupMenu(node);
            m.show(tree, evt.getX(), evt.getY());
         }
      }
   }
   
   /**
    * Popup Menu for Tree Nodes
    */
   public class MyPopupMenu extends JPopupMenu {
      
      private DefaultMutableTreeNode node;
      private JMenuItem deleteMI;
      private JMenuItem editMI;
      
      public MyPopupMenu(DefaultMutableTreeNode node) {
         this.node = node;
         init();
      }
      
      private void init() {
         deleteMI = new JMenuItem("Delete");
         deleteMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               deleteNode(node);
            }
         });
         editMI = new JMenuItem("Properties");
         editMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               editNode(node);
            }
         });
         
         add(deleteMI);
         add(new JPopupMenu.Separator());
         add(editMI);
      }
   }
   
   /**
    * Edit Group Panel
    */
   public class GroupNamePanel extends JPanel {
      
      private JTextField nameTF;
      
      public GroupNamePanel() {
         init();
      }
      
      private void init() {
         
         setLayout(new TableLayout(MainPanel.class.getResource("add_group_layout.html")));
                           
         JLabel lbl = new JLabel(Resources.bundle.getString("add.group.name"));
         add("label", lbl);
         
         nameTF = new JTextField();
         add("field", nameTF);
      }

      @Override
      public String getName() {
         return nameTF.getText();
      }

      @Override
      public void setName(String name) {
         nameTF.setText(name);
      }
   }
}
