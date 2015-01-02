// Copyright (C) 2005 Xito.org. http://www.xito.org
//
// This file is part of Xito(tm).
//
// This Software is licensed under the terms of the
// COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0
//
// To view the complete Terms of this license visit:
// http://www.opensource.org/licenses/cddl1.txt
//
// COVERED SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN AS IS BASIS, WITHOUT
// WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, WITHOUT
// LIMITATION, WARRANTIES THAT THE COVERED SOFTWARE IS FREE OF DEFECTS,
// MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR NON-INFRINGING. THE ENTIRE
// RISK AS TO THE QUALITY AND PERFORMANCE OF THE COVERED SOFTWARE IS WITH YOU.
// SHOULD ANY COVERED SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, YOU (NOT THE
// INITIAL DEVELOPER OR ANY OTHER CONTRIBUTOR) ASSUME THE COST OF ANY
// NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
// CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY COVERED
// SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.

package org.xito.launcher.applet;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import org.xito.dialog.*;
import org.xito.boot.Boot;
import org.xito.launcher.*;

/**
 * Dialog used to Configure Applet Descriptions
 *
 * @author Deane Richan
 */
public class AppletConfigDialog extends CustomDialog {
   
   private static final Logger logger = Logger.getLogger(AppletConfigDialog.class.getName());
   
   private AppletDesc appletDesc;
   private MainPanel mainPanel;
   
   /** Creates a new instance of LocalAppConfigDialog */
   public AppletConfigDialog(Frame owner, AppletDesc appletDesc) {
      super(owner);
      if(appletDesc == null)
         this.appletDesc = new AppletDesc();
      else
         this.appletDesc = appletDesc;
      
      descriptor = createDialogDesc();
      super.setModal(true);
      super.setTitle(Resources.appletBundle.getString("config.title"));
      super.init();
   }
   
   /**
    * Create the Dialog Desc for this Dialog
    */
   private DialogDescriptor createDialogDesc() {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setType(DialogManager.OK_CANCEL);
      //desc.setGradiantColor(org.xito.boot.ui.Defaults.DIALOG_GRAD_COLOR);
      //desc.setGradiantOffsetRatio(org.xito.boot.ui.Defaults.DIALOG_GRAD_OFFSET);
      desc.setTitle(Resources.appletBundle.getString("config.title"));
      desc.setSubtitle(Resources.appletBundle.getString("config.subtitle"));
      desc.setIcon(new ImageIcon(LauncherService.class.getResource("/org/xito/launcher/images/java_32.png")));
      desc.setWidth(Resources.getIntForOS(Resources.appletBundle, "config.org.xito.width", Boot.getCurrentOS(), 300));
      desc.setHeight(Resources.getIntForOS(Resources.appletBundle, "config.org.xito.height", Boot.getCurrentOS(), 300));
      
      
      
      desc.setShowButtonSeparator(true);
      desc.setResizable(true);
      
      mainPanel = new MainPanel();
      desc.setCustomPanel(mainPanel);
      
      return desc;
   }
   
   /**
    * Get this Dialogs AppletDesc
    */
   public AppletDesc getAppletDesc() {
      return this.appletDesc;
   }
   
   /**
    * Show the org.xito and update the contents of LocalAppDesc if OK was pressed
    */
   public void show(boolean b) {
      super.show(b);
      
      int result = super.getResult();
      if(result == DialogManager.OK) {
         mainPanel.updateAppletDesc();
      }
      
      return;
   }
   
   /**
    * The Main Panel of the Dialog
    */
   private class MainPanel extends TablePanel implements ActionListener, Validatable {
      
      private JLabel errorLbl;
      private JTextField addressTF;
      private JTextField nameTF;
      private JTextField titleTF;
      private JCheckBox useBrowserCB;
      private JCheckBox seperateVMCB;
      
      private JButton advBtn;
      private AdvancedPanel advancedPanel;
      private DialogDescriptor advancedDesc;
      
      public MainPanel() {
         //super.setPaintBorderLines(true);
         init();
      }
      
      /**
       * Return true if the Contents of this Panel are valid
       */
      public boolean hasValidData() {
         
         String name = nameTF.getText();
         if(name == null || name.equals("")) {
            Toolkit.getDefaultToolkit().beep();
            errorLbl.setText(Resources.appletBundle.getString("config.name.error"));
            errorLbl.setVisible(true);
            return false;
         }
         String address = addressTF.getText();
         if(address != null && !address.equals("")) {
            try {
               new URL(address);
            } catch(MalformedURLException badURL) {
               logger.log(Level.WARNING, badURL.getMessage(), badURL);
               Toolkit.getDefaultToolkit().beep();
               errorLbl.setText(Resources.appletBundle.getString("config.address.error"));
               errorLbl.setVisible(true);
               return false;
            }
         }
         
         errorLbl.setVisible(false);
         return true;
      }
      
      /**
       * Update Applet Desc
       */
      private void updateAppletDesc() {
         
         appletDesc.setName(nameTF.getText());
         appletDesc.setTitle(titleTF.getText());
         appletDesc.setUseWebBrowser(useBrowserCB.isSelected());
         appletDesc.setSeperateVM(seperateVMCB.isSelected());
         
         String address = addressTF.getText();
         if(address != null && !address.equals("")) {
            try {
               appletDesc.setDocumentURL(new URL(address));
            } catch(MalformedURLException badURL) {
               logger.log(Level.WARNING, badURL.getMessage(), badURL);
               Boot.showError("Applet Error", Resources.appletBundle.getString("config.address.error"), badURL);
            }
         }
         
         advancedPanel.updateAppletDesc();
      }
      
      /**
       * Build the Panel
       */
      private void init() {
         
         setLayout(new TableLayout(AppletConfigDialog.class.getResource("applet_layout.html")));
                  
         //Description
         JLabel lbl = new JLabel(Resources.appletBundle.getString("config.desc.lbl"));
         add("description", lbl);
         
         //Error Label
         errorLbl = new JLabel();
         errorLbl.setOpaque(true);
         errorLbl.setBorder(new LineBorder(SystemColor.controlShadow));
         errorLbl.setBackground(SystemColor.textHighlight);
         errorLbl.setForeground(SystemColor.textHighlightText);
         errorLbl.setVisible(false);
         add("error_lbl", errorLbl);
         
         //Address
         lbl = new JLabel(Resources.appletBundle.getString("config.address.lbl"));
         add("address_lbl", lbl);
         addressTF = new JTextField();
         add("address", addressTF);
         
         //Name
         lbl = new JLabel(Resources.appletBundle.getString("config.name.lbl"));
         add("name_lbl", lbl);
         nameTF = new JTextField();
         nameTF.setDocument(new ValidatableDocument(BaseLaunchDesc.MAX_NAME_LENGTH));
         add("name", nameTF);
         
         //Title
         lbl = new JLabel(Resources.appletBundle.getString("config.title.lbl"));
         add("title_lbl", lbl);
         titleTF = new JTextField();
         titleTF.setDocument(new ValidatableDocument(BaseLaunchDesc.MAX_DISPLAYNAME_LENGTH));
         add("title", titleTF);
         
         //Launch in Browser
         useBrowserCB = new JCheckBox(Resources.appletBundle.getString("config.usebrowser.lbl"));
         useBrowserCB.setHorizontalTextPosition(SwingConstants.LEFT);
         useBrowserCB.setBorder(null);
         add("browser", useBrowserCB);
         
         //SeperateVM
         seperateVMCB = new JCheckBox(Resources.appletBundle.getString("config.seperatevm.lbl"));
         seperateVMCB.setHorizontalTextPosition(SwingConstants.LEFT);
         seperateVMCB.setBorder(null);
         add("separate_vm", seperateVMCB);
         
         //Advanced Btn
         advBtn = new JButton(Resources.appletBundle.getString("config.advanced.text"));
         advBtn.addActionListener(this);
         add("advanced", advBtn);
            
         //Setup the Dialog Descriptor
         advancedPanel = new AdvancedPanel();
         advancedDesc = new DialogDescriptor();
         advancedDesc.setType(DialogManager.OK_CANCEL);
         //advancedDesc.setGradiantColor(org.xito.boot.ui.Defaults.DIALOG_GRAD_COLOR);
         //advancedDesc.setGradiantOffsetRatio(org.xito.boot.ui.Defaults.DIALOG_GRAD_OFFSET);
         advancedDesc.setTitle(Resources.appletBundle.getString("config.adv.title"));
         advancedDesc.setSubtitle(Resources.appletBundle.getString("config.adv.subtitle"));
         advancedDesc.setIcon(new ImageIcon(this.getClass().getResource("/org/xito/launcher/images/java_32.png")));
         advancedDesc.setWidth(Resources.getIntForOS(Resources.appletBundle, "config.adv.width", Boot.getCurrentOS(), 375));
         advancedDesc.setHeight(Resources.getIntForOS(Resources.appletBundle, "config.adv.height", Boot.getCurrentOS(), 475));
         advancedDesc.setShowButtonSeparator(true);
         advancedDesc.setResizable(true);
         advancedDesc.setCustomPanel(advancedPanel);
         
         //Read Settings from Desc and populate Panel
         readDesc();
      }
      
      /**
       * Read the Settings from the Desc and populate the Panels
       */
      private void readDesc() {
         if(appletDesc.getDocumentURL() != null)
            addressTF.setText(appletDesc.getDocumentURL().toString());
         
         nameTF.setText(appletDesc.getName());
         titleTF.setText(appletDesc.getTitle());
         useBrowserCB.setSelected(appletDesc.useWebBrowser());
         seperateVMCB.setSelected(appletDesc.useSeperateVM());
      }
      
      /**
       * Action Performed
       */
      public void actionPerformed(ActionEvent evt) {
         
         if(evt.getSource() == advBtn) {
            showAdvanced();
         }
      }
      
      private void showAdvanced() {
         DialogManager.showDialog((Frame)AppletConfigDialog.this.getOwner(), advancedDesc);
      }
   }
   
   /***************************************************************************
    * The Advanced Panel of the Dialog
    ***************************************************************************/
   private class AdvancedPanel extends JPanel implements ActionListener {
      
      private ButtonGroup configGroup;
      private JRadioButton defaultConfigRB;
      private JRadioButton customConfigRB;
      private JTextField codebaseTF;
      private JTextField appletClassTF;
      private JTextField widthTF;
      private JTextField heightTF;
      private JCheckBox resizableCB;
      
      private JTabbedPane tabs;
      private ArchivesPanel archivesPanel;
      private ParamtersPanel paramsPanel;
      
      public AdvancedPanel() {
         init();
      }
      
      private void enableControls(boolean enable) {
         
         Component c[] = this.getComponents();
         for(int i=0;i<c.length;i++) {
            if(c[i] == defaultConfigRB || c[i] == customConfigRB)
               continue;
            
            ((JComponent)c[i]).setEnabled(enable);
         }
         archivesPanel.setEnabled(enable);
         paramsPanel.setEnabled(enable);
      }
      
      /**
       * Update Applet Desc
       */
      private void updateAppletDesc() {
         
         appletDesc.setUseCustomConfig(customConfigRB.isSelected());
         try {
            if(codebaseTF.getText()!= null && !codebaseTF.getText().equals(""))
               appletDesc.setCodeBaseURL(new URL(codebaseTF.getText()));
         } catch(MalformedURLException badURL) {
            badURL.printStackTrace();
         }
         appletDesc.setAppletClass(appletClassTF.getText());
         appletDesc.setWidth(Integer.parseInt(widthTF.getText()));
         appletDesc.setHeight(Integer.parseInt(heightTF.getText()));
         appletDesc.setResizable(resizableCB.isSelected());
         
         //Archives
         appletDesc.setArchives(archivesPanel.getArchives());
         
         //Paramters
         appletDesc.setParameters(paramsPanel.getParameters());
      }
      
      /**
       * Build the Panel
       */
      private void init() {
         
         setLayout(new TableLayout(AppletConfigDialog.class.getResource("adv_layout.html")));
                           
         //Default Config
         configGroup = new ButtonGroup();
         defaultConfigRB = new JRadioButton(Resources.appletBundle.getString("config.adv.default.config"));
         defaultConfigRB.addActionListener(this);
         configGroup.add(defaultConfigRB);
         add("default_config", defaultConfigRB);
                  
         //Custom Config
         customConfigRB = new JRadioButton(Resources.appletBundle.getString("config.adv.custom.config"));
         customConfigRB.addActionListener(this);
         configGroup.add(customConfigRB);
         add("custom_config", customConfigRB);
         add("separator_1", new JSeparator(JSeparator.HORIZONTAL));
                  
         //CodeBase
         JLabel lbl = new JLabel(Resources.appletBundle.getString("config.adv.codebase"));
         add("codebase_lbl", lbl);
         codebaseTF = new JTextField();
         add("codebase", codebaseTF);
         
         //Applet Class
         lbl = new JLabel(Resources.appletBundle.getString("config.adv.applet.class"));
         add("applet_lbl", lbl);
         appletClassTF = new JTextField();
         add("applet", appletClassTF);
                  
         //Width
         lbl = new JLabel(Resources.appletBundle.getString("config.adv.width"));
         add("width_lbl", lbl);
         widthTF = new JTextField();
         add("width", widthTF);
         
         //Resizable
         lbl = new JLabel(Resources.appletBundle.getString("config.adv.resizable"));
         add("resizable_lbl", lbl);
         resizableCB = new JCheckBox();
         resizableCB.setBorder(null);
         add("resizable", resizableCB);
                  
         //Height
         lbl = new JLabel(Resources.appletBundle.getString("config.adv.height"));
         add("height_lbl", lbl);
         heightTF = new JTextField();
         add("height", heightTF);
         add("separator_2", new JSeparator(JSeparator.HORIZONTAL));
         
         //Tabs
         tabs = new JTabbedPane();
         add("tabs", tabs);
         
         archivesPanel = new ArchivesPanel();
         tabs.addTab(Resources.appletBundle.getString("config.adv.archives"), archivesPanel);
         paramsPanel = new ParamtersPanel();
         tabs.addTab(Resources.appletBundle.getString("config.adv.params"), paramsPanel);
           
         //Read the Desc and populate the Panel
         readDesc();
      }
      
      /**
       * Read the Desc and Populate the Panel
       */
      private void readDesc() {
         
         defaultConfigRB.setSelected(!appletDesc.useCustomConfig());
         customConfigRB.setSelected(appletDesc.useCustomConfig());
         if(appletDesc.getCodeBaseURL()!=null)
            codebaseTF.setText(appletDesc.getCodeBaseURL().toString());
         
         appletClassTF.setText(appletDesc.getAppletClass());
         widthTF.setText(appletDesc.getWidth()+"");
         heightTF.setText(appletDesc.getHeight()+"");
         resizableCB.setSelected(appletDesc.isResizable());
         
         //Archives
         archivesPanel.setArchives(appletDesc.getArchives());
         
         //Parameters
         paramsPanel.setParameters(appletDesc.getParameters());
         
         enableControls(customConfigRB.isSelected());
      }
      
      /**
       * Action Performed
       */
      public void actionPerformed(ActionEvent evt) {
         if(evt.getSource() == customConfigRB) {
            enableControls(true);
         } else if(evt.getSource() == defaultConfigRB) {
            enableControls(false);
         }
      }
   }
   
   
   /***************************************************
    * Archives Panel
    ***************************************************/
   private class ArchivesPanel extends TablePanel implements ActionListener {
      
      private DefaultListModel listModel;
      private JList archiveList;
      private JButton addBtn;
      private JButton delBtn;
      
      ArchivesPanel() {
         //super.setPaintBorderLines(true);         
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
         setLayout(new TableLayout(AppletConfigDialog.class.getResource("archives_layout.html")));
                  
         listModel = new DefaultListModel();
         archiveList = new JList(listModel);
         add("archives", new JScrollPane(archiveList));
   
         addBtn = new JButton(Resources.appletBundle.getString("config.adv.add"));
         addBtn.addActionListener(this);
         addBtn.setOpaque(false);
         add("add_btn", addBtn);
            
         delBtn = new JButton(Resources.appletBundle.getString("config.adv.delete"));
         delBtn.addActionListener(this);
         delBtn.setOpaque(false);
         add("del_btn", delBtn);
      }
      
      public void actionPerformed(ActionEvent evt) {
         if(evt.getSource() == delBtn) {
            if(archiveList.getSelectedIndex()>=0)
               listModel.remove(archiveList.getSelectedIndex());
         } else if(evt.getSource() == addBtn) {
            addArchive();
         }
      }
      
      private void addArchive() {
         
         DialogDescriptor addArchiveDesc = new DialogDescriptor();
         addArchiveDesc.setType(DialogManager.OK_CANCEL);
         //addArchiveDesc.setGradiantColor(org.xito.boot.ui.Defaults.DIALOG_GRAD_COLOR);
         //addArchiveDesc.setGradiantOffsetRatio(org.xito.boot.ui.Defaults.DIALOG_GRAD_OFFSET);
         addArchiveDesc.setTitle(Resources.appletBundle.getString("config.adv.add.archive.title"));
         addArchiveDesc.setSubtitle(Resources.appletBundle.getString("config.adv.add.archive.subtitle"));
         addArchiveDesc.setWidth(300);
         addArchiveDesc.setHeight(175);
         
         final JTextField archiveNameTF = new JTextField();
         addArchiveDesc.setCustomPanel(new JPanel() {
            {
               setLayout(new TableLayout(AppletConfigDialog.class.getResource("add_archive.html")));
               
               add("label", new JLabel(Resources.appletBundle.getString("config.adv.add.archive.lbl")));
               add("field", archiveNameTF);
            }
         });
         
         int result = DialogManager.showDialog((Frame)AppletConfigDialog.this.getOwner(), addArchiveDesc);
         if(result == DialogManager.OK) {
            String archive = archiveNameTF.getText();
            if(archive != null && !archive.equals(""))
               listModel.addElement(archive);
         }
         
      }
   }
   
   /*****************************************************
    * Parameters Panel
    *****************************************************/
   private class ParamtersPanel extends JPanel implements ActionListener {
      
      private DefaultTableModel tableModel;
      private JTable parameterTable;
      private JButton addBtn;
      private JButton delBtn;
      
      ParamtersPanel() {
         //super();
         init();
      }
      
      public void setEnabled(boolean enable) {
         super.setEnabled(enable);
         parameterTable.setEnabled(enable);
         addBtn.setEnabled(enable);
         delBtn.setEnabled(enable);
      }
      
      public void setParameters(Map map) {
         if(map == null) return;
         
         Iterator keys = map.keySet().iterator();
         while(keys.hasNext()) {
            Object name = keys.next();
            tableModel.addRow(new Object[]{name, map.get(name)});
         }
      }
      
      public Map getParameters() {
         HashMap map = new HashMap();
         Vector data = tableModel.getDataVector();
         if(data == null) return map;
         
         Iterator rows = data.iterator();
         while(rows.hasNext()) {
            Vector r = (Vector)rows.next();
            Object n = r.get(0);
            Object v = r.get(1);
            if(n != null && !n.equals("") && v != null && !v.equals("")) map.put(n,v);
         }
         
         return map;
      }
      
      private void init() {
         setOpaque(false);
         setLayout(new TableLayout(AppletConfigDialog.class.getResource("parameters_layout.html")));
                  
         tableModel = new DefaultTableModel() {
            public int 	getColumnCount() {return 2;}
            public String getColumnName(int c) {String names[] = new String[]{"Name", "Value"};return names[c];}
         };
         parameterTable = new JTable(tableModel);
         add("parameters", new JScrollPane(parameterTable));
         
         addBtn = new JButton(Resources.appletBundle.getString("config.adv.add"));
         addBtn.addActionListener(this);
         addBtn.setOpaque(false);
         add("add_btn", addBtn);
                  
         delBtn = new JButton(Resources.appletBundle.getString("config.adv.delete"));
         delBtn.addActionListener(this);
         delBtn.setOpaque(false);
         add("del_btn", delBtn);

      }
      
      public void actionPerformed(ActionEvent evt) {
         if(evt.getSource() == delBtn) {
            int row = parameterTable.getSelectedRow();
            if(row>-1) tableModel.removeRow(row);
         } else if(evt.getSource() == addBtn) {
            addParameter();
         }
      }
      
      private void addParameter() {
         
         DialogDescriptor addParamterDesc = new DialogDescriptor();
         addParamterDesc.setType(DialogManager.OK_CANCEL);
         //addParamterDesc.setGradiantColor(org.xito.boot.ui.Defaults.DIALOG_GRAD_COLOR);
         //addParamterDesc.setGradiantOffsetRatio(org.xito.boot.ui.Defaults.DIALOG_GRAD_OFFSET);
         addParamterDesc.setTitle(Resources.appletBundle.getString("config.adv.add.parameter.title"));
         addParamterDesc.setSubtitle(Resources.appletBundle.getString("config.adv.add.parameter.subtitle"));
         addParamterDesc.setWidth(300);
         addParamterDesc.setHeight(175);
         
         final JTextField nameTF = new JTextField();
         final JTextField valueTF = new JTextField();
         addParamterDesc.setCustomPanel(new JPanel() {
            {
               setLayout(new TableLayout(AppletConfigDialog.class.getResource("add_parameter.html")));
                              
               add("name_lbl", new JLabel(Resources.appletBundle.getString("config.adv.name.lbl")));
               add("name", nameTF);
               
               add("value_lbl", new JLabel(Resources.appletBundle.getString("config.adv.value.lbl")));
               add("value", valueTF);
            }
         });
         
         int result = DialogManager.showDialog((Frame)AppletConfigDialog.this.getOwner(), addParamterDesc);
         if(result == DialogManager.OK) {
            String name = nameTF.getText();
            String value = valueTF.getText();
            if(name!=null && !name.equals("") && value!=null && !value.equals("")) {
               tableModel.addRow(new String[]{name, value});
            }
         }
         
      }
   }
}
