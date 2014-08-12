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

package org.xito.launcher.jnlp;

import java.security.*;
import java.io.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import org.xito.dialog.*;
import org.xito.boot.Boot;
import org.xito.boot.NativeLibDesc;
import org.xito.launcher.*;

/**
 *
 * @author Deane Richan
 */
public class JavaConfigDialog extends CustomDialog {
   
   private static final Logger logger = Logger.getLogger(JavaConfigDialog.class.getName());
   
   private JavaAppDesc appDesc;
   private MainPanel mainPanel;
   
   /** Creates a new instance of LocalAppConfigDialog */
   public JavaConfigDialog(Frame owner, JavaAppDesc appDesc) {
      super(owner);
      if(appDesc == null) 
         this.appDesc = new JavaAppDesc();
      else
         this.appDesc = appDesc;
            
      super.descriptor = createDialogDesc();
      super.setModal(true);
      super.setTitle(Resources.javaBundle.getString("config.title"));
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
      desc.setTitle(Resources.javaBundle.getString("config.title"));
      desc.setSubtitle(Resources.javaBundle.getString("config.subtitle"));
      desc.setIcon(new ImageIcon(this.getClass().getResource("/org/xito/launcher/images/java_32.png")));
      desc.setShowButtonSeparator(true);
      desc.setResizable(true);
      desc.setWidth(Resources.getIntForOS(Resources.javaBundle, "config.org.xito.width", Boot.getCurrentOS(), 420));
      desc.setHeight(Resources.getIntForOS(Resources.javaBundle, "config.org.xito.height", Boot.getCurrentOS(), 500));
                        
      mainPanel = new MainPanel();
      desc.setCustomPanel(mainPanel);
      
      return desc;
   }
   
   /**
    * Get this Dialogs AppDesc
    */
   public JavaAppDesc getAppDesc() {
      return this.appDesc;
   }
   
   /**
    * Show the org.xito and update the contents of LocalAppDesc if OK was pressed
    */
   public void show(boolean b) {
      super.show(b);

      int result = super.getResult();
      if(result == DialogManager.OK) {
         mainPanel.updateAppDesc();
      }
      
      return;
   }
   
   /*******************************************
    * The Main Panel of the Dialog
    *******************************************/
   private class MainPanel extends JPanel {
      
      private JLabel errorLbl;
      private JTextField mainClassTF;
      private JTextField nameTF;
      private JTextField titleTF;
      private JTextField argsTF;
      
      private JComboBox permissionsCombo;
      private JCheckBox useWebStartCB;
      private JCheckBox seperateVMCB;
      private JCheckBox sharedClassLoaderCB;
      
      private JTabbedPane tabs;
      private ArchivesPanel javaArchivesPanel;
      private ArchivesPanel nativeArchivesPanel;
                  
      public MainPanel() {
         init();
      }
      
      /**
       * Update App Desc
       */
      private void updateAppDesc() {
         
         appDesc.setMainClass(mainClassTF.getText());
         appDesc.setName(nameTF.getText());
         appDesc.setTitle(titleTF.getText());
         
         //args
         String args = argsTF.getText();
         if(args != null && !args.equals("")) {
            appDesc.setMainArgs(args.split(" "));
         }
         else {
            appDesc.setMainArgs(null);
         }
         
         appDesc.setUseWebStart(useWebStartCB.isSelected());
         appDesc.setSeperateVM(seperateVMCB.isSelected());
         appDesc.setUseSharedClassLoader(sharedClassLoaderCB.isSelected());
                           
         //Archives
         appDesc.setJavaArchives(javaArchivesPanel.getArchives());
                  
         //Native Archives
         appDesc.setNativeArchives(nativeArchivesPanel.getArchives());
         
         //Permissions
         if(permissionsCombo.getSelectedIndex()==0) {
            appDesc.setPermissions(null);
         }
         else {
            Permissions perms = new Permissions();
            perms.add(new AllPermission());
            appDesc.setPermissions(perms);
         }
         
      }
      
      /**
       * Build the Panel
       */
      private void init() {
         
         setLayout(new TableLayout(JNLPConfigDialog.class.getResource("java_layout.html")));
         
         //Description
         JLabel lbl = new JLabel(Resources.javaBundle.getString("config.desc.lbl"));
         add("description", lbl);
         
         //Error Label
         errorLbl = new JLabel();
         errorLbl.setOpaque(true);
         errorLbl.setBorder(new LineBorder(SystemColor.controlShadow));
         errorLbl.setBackground(SystemColor.textHighlight);
         errorLbl.setForeground(SystemColor.textHighlightText);
         errorLbl.setVisible(false);
         add("error_lbl", errorLbl);
         
         //Main Class
         lbl = new JLabel(Resources.javaBundle.getString("config.mainclass.lbl"));
         add("main_cls_lbl", lbl);
         mainClassTF = new JTextField();
         add("main_cls", mainClassTF);
         
         //Name
         lbl = new JLabel(Resources.javaBundle.getString("config.name.lbl"));
         add("name_lbl", lbl);
         nameTF = new JTextField();
         nameTF.setDocument(new ValidatableDocument(BaseLaunchDesc.MAX_NAME_LENGTH));
         add("name", nameTF);
         
         //Title
         lbl = new JLabel(Resources.javaBundle.getString("config.title.lbl"));
         add("title_lbl", lbl);
         titleTF = new JTextField();
         titleTF.setDocument(new ValidatableDocument(BaseLaunchDesc.MAX_DISPLAYNAME_LENGTH));
         add("title", titleTF);
         
         //Arguments
         lbl = new JLabel(Resources.javaBundle.getString("config.args.lbl"));
         add("args_lbl", lbl);
         argsTF = new JTextField();
         add("args", argsTF);
         
         add("separator_1", new JSeparator(JSeparator.HORIZONTAL));
         
         lbl = new JLabel(Resources.javaBundle.getString("config.permissions.lbl"));
         add("permissions_lbl", lbl);
         String restricted = Resources.javaBundle.getString("config.restricted.perms");
         String allPerms = Resources.javaBundle.getString("config.all.perms");
         permissionsCombo = new JComboBox(new String[]{restricted, allPerms});
         permissionsCombo.setSelectedIndex(0);
         add("permissions", permissionsCombo);
                  
         //Launch in WebStart
         useWebStartCB = new JCheckBox(Resources.javaBundle.getString("config.usewebstart.lbl"));
         useWebStartCB.setHorizontalTextPosition(SwingConstants.LEFT);
         useWebStartCB.setBorder(null);
         useWebStartCB.setEnabled(false);
         add("web_start", useWebStartCB);
         
         //SeperateVM
         seperateVMCB = new JCheckBox(Resources.javaBundle.getString("config.seperatevm.lbl"));
         seperateVMCB.setHorizontalTextPosition(SwingConstants.LEFT);
         seperateVMCB.setBorder(null);
         add("separate_vm", seperateVMCB);
         
         //Shared ClassLoader
         sharedClassLoaderCB = new JCheckBox(Resources.javaBundle.getString("config.sharedclassloader.lbl"));
         sharedClassLoaderCB.setHorizontalTextPosition(SwingConstants.LEFT);
         sharedClassLoaderCB.setBorder(null);
         add("shared_classloader", sharedClassLoaderCB);
         
         add("separator_2", new JSeparator(JSeparator.HORIZONTAL));
         
         //Tabs
         tabs = new JTabbedPane();
         add("tabs", tabs);
         
         javaArchivesPanel = new ArchivesPanel(ArchivesPanel.JAVA_ARCHIVES);
         tabs.addTab(Resources.javaBundle.getString("config.java.archives"), javaArchivesPanel);
         nativeArchivesPanel = new ArchivesPanel(ArchivesPanel.NATIVE_ARCHIVES);
         tabs.addTab(Resources.javaBundle.getString("config.native.archives"), nativeArchivesPanel);
         
         readDesc();
      }
      
      /**
       * Read the Settings from the Desc and populate the Panels
       */
      private void readDesc() {
         
         mainClassTF.setText(appDesc.getMainClass());
         nameTF.setText(appDesc.getName());
         titleTF.setText(appDesc.getTitle());
         useWebStartCB.setSelected(appDesc.useWebStart());
         seperateVMCB.setSelected(appDesc.useSeperateVM());
         sharedClassLoaderCB.setSelected(appDesc.useSharedClassLoader());
         
         //Permissions
         if(appDesc.getPermissions() != null) {
            this.permissionsCombo.setSelectedIndex(1);
         }
         else {
            this.permissionsCombo.setSelectedIndex(0);
         }
         
         //Arguments
         String[] args = appDesc.getMainArgs();
         StringBuffer argLine = new StringBuffer();
         if(args != null) {
            for(int i=0;i<args.length;i++) {
               argLine.append(args[i] + " ");
            }
            argsTF.setText(argLine.toString());
         }
         
         javaArchivesPanel.setArchives(appDesc.getJavaArchives());
         nativeArchivesPanel.setArchives(appDesc.getNativeArchives());
      }
   }
   
   /***************************************************
    * Archives Panel
    ***************************************************/
   private class ArchivesPanel extends JPanel implements ActionListener {
      
      public final static int JAVA_ARCHIVES = 0;
      public final static int NATIVE_ARCHIVES = 1;
      
      private DefaultListModel listModel;
      private JList archiveList;
      private JButton addBtn;
      private JButton addFileBtn;
      private JButton delBtn;
      private int type = JAVA_ARCHIVES;
      
      ArchivesPanel(int type) {
         this.type = type;
         init();
      }
      
      public void setArchives(java.util.List list) {
         if(list != null) {
            Iterator it = list.iterator();
            while(it.hasNext()) {
               listModel.addElement(it.next());
            }
         }
      }
      
      public java.util.List getArchives() {
         return Arrays.asList(listModel.toArray());
      }
      
      public void setEnabled(boolean enable) {
         super.setEnabled(enable);
         archiveList.setEnabled(enable);
         addBtn.setEnabled(enable);
         delBtn.setEnabled(enable);
      }
      
      private void init() {
         
         setOpaque(false);
         setLayout(new TableLayout(JavaConfigDialog.class.getResource("archives_layout.html")));
                  
         listModel = new DefaultListModel();
         archiveList = new JList(listModel);
         add("archives", new JScrollPane(archiveList));
   
         addBtn = new JButton(Resources.javaBundle.getString("config.add"));
         addBtn.addActionListener(this);
         addBtn.setOpaque(false); 
         add("add_btn", addBtn);
         
         addFileBtn = new JButton(Resources.javaBundle.getString("config.add.file"));
         addFileBtn.addActionListener(this);
         addFileBtn.setOpaque(false);
         add("add_file_btn", addFileBtn);
            
         delBtn = new JButton(Resources.javaBundle.getString("config.delete"));
         delBtn.addActionListener(this);
         delBtn.setOpaque(false);
         add("del_btn", delBtn);
         
         setupDND();
      }
      
      private void setupDND() {
         
         new DropTarget(archiveList, new DropTargetAdapter() {
             
             public void drop(DropTargetDropEvent evt) {
               System.out.println("Drop");
               evt.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
               
               try {
                  DataFlavor fileFlavor = new DataFlavor("application/x-java-file-list; class=java.util.List");
                  java.util.List files = (java.util.List)evt.getTransferable().getTransferData(fileFlavor);
                  Iterator it = files.iterator();
                  while(it.hasNext()) {
                     File f = (File)it.next();
                     listModel.addElement(f.toURL());
                  }
                  evt.dropComplete(true);
                  return;
               }
               catch(Exception exp) {
                  exp.printStackTrace();
               }
                              
               evt.dropComplete(false);
             }
         });
      }
      
      public void actionPerformed(ActionEvent evt) {
         if(evt.getSource() == delBtn) {
            int index[] = archiveList.getSelectedIndices();
            if(index != null && index.length>0) {
               for(int i=index.length-1;i>=0;i--)
                  listModel.remove(index[i]);
            }
         }
         else if(evt.getSource() == addBtn) {
            addArchive();
         }
         if(evt.getSource() == addFileBtn) {
            addFileArchive();
         }
      }
      
      /**
       * Add File Archive
       */
      private void addFileArchive() {
         
         String btnText = Resources.javaBundle.getString("config.browse.org.xito.btn.text");
         
         JFileChooser fd = new JFileChooser();
         fd.setDialogTitle(Resources.javaBundle.getString("config.file.browse.title"));
         fd.setFileSelectionMode(fd.FILES_ONLY);
         fd.setLocation(0,0);
         fd.showDialog(JavaConfigDialog.this.getOwner(), btnText);
         File file = fd.getSelectedFile();
         if(file == null || file.isFile()==false)
            return;
         
         try {
            if(type == JAVA_ARCHIVES) {
               listModel.addElement(file.toURL());
            }
            else if(type == NATIVE_ARCHIVES) {
               listModel.addElement(new NativeLibDesc(NativeLibDesc.currentOS(), file.toURL()));
            }
         }
         catch(MalformedURLException badURL) {
            logger.log(Level.SEVERE, badURL.getMessage(), badURL);
         }
      }
      
      /**
       * Add an Archive
       */
      private void addArchive() {
         
         DialogDescriptor addArchiveDesc = new DialogDescriptor();
         addArchiveDesc.setType(DialogManager.OK_CANCEL);
         addArchiveDesc.setGradiantColor(org.xito.boot.ui.Defaults.DIALOG_GRAD_COLOR);
         addArchiveDesc.setGradiantOffsetRatio(org.xito.boot.ui.Defaults.DIALOG_GRAD_OFFSET);
         addArchiveDesc.setTitle(Resources.javaBundle.getString("config.add.archive.title"));
         addArchiveDesc.setSubtitle(Resources.javaBundle.getString("config.add.archive.subtitle"));
         addArchiveDesc.setWidth(350);
         addArchiveDesc.setHeight(175);
         
         AddArchivePanel addPanel = new AddArchivePanel((Frame)JavaConfigDialog.this.getOwner());
         addArchiveDesc.setCustomPanel(addPanel);
         
         int result = DialogManager.showDialog((Frame)JavaConfigDialog.this.getOwner(), addArchiveDesc);
         if(result == DialogManager.OK) {
            URL archive = addPanel.getURL();
            if(archive != null && type == JAVA_ARCHIVES) {
               listModel.addElement(archive);
            }
            else if(archive != null && type == NATIVE_ARCHIVES) {
               listModel.addElement(new NativeLibDesc(NativeLibDesc.currentOS(), archive));
            }
            
         }
      }
      
      /***********************************
       * Add Archive Panel
       ***********************************/
      class AddArchivePanel extends JPanel implements Validatable {
         
         URL url;
         Frame parent;
         JTextField archiveNameTF = new JTextField();
         
         public AddArchivePanel(Frame parent) {
            this.parent = parent;
            setLayout(new TableLayout(JavaConfigDialog.class.getResource("add_archive.html")));
            add("label", new JLabel(Resources.javaBundle.getString("config.add.archive.lbl")));
            add("field", archiveNameTF);
         }
         
         /**
          * Check to see if the Archive specified is Valid
          */
         public boolean hasValidData() {
            String name = archiveNameTF.getText();
            if(name == null || name.equals("")) {
               url = null;
               return true;
            }
            
            try {
               
               //Check to see if it is a File
               File f = new File(archiveNameTF.getText());
               if(f.exists()) {
                  url = f.toURL();
               }
               else {
                  url = new URL(archiveNameTF.getText());
               }
               return true;
            }
            catch(Exception e) {
               DialogManager.showError(parent, "Invalid Archive", "The Archive is not a valid Address", null);
            }
            return false;
         }
         
         public URL getURL() {
            return url;
         }
      }
   }
   
   
}
