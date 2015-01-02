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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.prefs.*;
import java.util.logging.*;
import javax.swing.*;
import org.xito.dialog.*;
import org.xito.boot.*;

/**
 *
 * @author Deane Richan
 */
public class MainFrame extends JFrame implements ActionListener {
   
   private static final Logger logger = Logger.getLogger(MainFrame.class.getName());
   
   private static final int DEFAULT_WIDTH = 300;
   private static final int DEFAULT_HEIGHT = 400;

   private JMenuItem newAppMI;
   private JMenuItem newGroupMI;
   private JCheckBoxMenuItem offlineMI;
   private JMenuItem hideAppManagerMI;
   private JMenuItem exitMI;
   private JMenuItem settingsMI;
   private JMenuItem aboutMI;
   
   private JPanel contentPane;
   private MainPanel mainPanel;
   private boolean exitOnClose_flag;
   
   /** Creates a new instance of MainFrame */
   public MainFrame(boolean exitOnClose) {
      super();
      exitOnClose_flag = exitOnClose;
      init();
   }
   
   /**
    * Initialize the Frame
    */
   private void init() {
      setTitle(Resources.bundle.getString("frame.title"));
      setBackground(Color.WHITE);
      this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      
      Preferences prefs = Preferences.userNodeForPackage(MainFrame.class);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      
      int x = prefs.getInt("mainframe.x", screenSize.width - DEFAULT_WIDTH);
      int y = prefs.getInt("mainframe.y", screenSize.height - DEFAULT_HEIGHT - 40);
      int w = prefs.getInt("mainframe.width", DEFAULT_WIDTH);
      int h = prefs.getInt("mainframe.height", DEFAULT_HEIGHT);
      
      setLocation(x,y);
      setSize(w,h);
      this.setMaximizedBounds(new Rectangle(screenSize.width-DEFAULT_WIDTH, 0, DEFAULT_WIDTH, screenSize.height-50));
      
      buildMenu();
      buildContentPane();
      
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            if(exitOnClose_flag) {
               int result = showExitPrompt();
               if(result == DialogManager.YES) {
                  shutdown();
               }
            }
            else {
               MainFrame.this.setVisible(false);
            }
         }
      });
   }
   
   protected void loadApplications(boolean launchStartupApps) {
      mainPanel.initTree(this, launchStartupApps);
   }
   
   private void shutdown() {
      MainFrame.this.dispose();
      mainPanel.storeTreeNodes();
      storePrefs();
      Boot.endSession(true);
   }
   
   private void storePrefs() {
      
      Preferences prefs = Preferences.userNodeForPackage(MainFrame.class);
      prefs.putInt("mainframe.x", this.getX());
      prefs.putInt("mainframe.y", this.getY());
      prefs.putInt("mainframe.width", this.getWidth());
      prefs.putInt("mainframe.height", this.getHeight());
            
      try {
         logger.info("storing mainframe prefs");
         prefs.flush();
         logger.info("done storing mainframe prefs");
      }
      catch(BackingStoreException exp) {
         logger.log(Level.WARNING, exp.getMessage(), exp);
      }
   }
   
   
   /**
    * Build the Menu
    */
   private void buildMenu() {
      JMenuBar menuBar = new JMenuBar();
      setJMenuBar(menuBar);
      
      //File Menu
      JMenu fileMnu = new JMenu(Resources.bundle.getString("menu.file"));
      fileMnu.setMnemonic(Resources.bundle.getString("menu.file.mnemonic.char").charAt(0));
      menuBar.add(fileMnu);
      
      //New Menu
      JMenu newMnu = new JMenu(Resources.bundle.getString("menu.new"));
      newMnu.setMnemonic(Resources.bundle.getString("menu.new.mnemonic.char").charAt(0));
      fileMnu.add(newMnu);
      newAppMI = buildMenuItem("menu.item.new_app");
      newMnu.add(newAppMI);
      
      newGroupMI = buildMenuItem("menu.item.new_group");
      newMnu.add(newGroupMI);
      fileMnu.addSeparator();
      
      //Offline
      offlineMI = new JCheckBoxMenuItem(Resources.bundle.getString("menu.item.offline"));
      offlineMI.setSelected(Boot.isOffline());
      offlineMI.addActionListener(this);
      fileMnu.add(offlineMI);
      Boot.addOfflineListener(new OfflineListener(){
         public void offline() {
            offlineMI.setSelected(true);
         }
         public void online() {
            offlineMI.setSelected(false);
         }
      });
      
      //Hide
      if(exitOnClose_flag == false) {
        hideAppManagerMI = buildMenuItem("menu.item.hide");
        fileMnu.add(hideAppManagerMI);
      }
            
      //Exit only for Windows. on Apple we use the Quit Menu
      String os = System.getProperty("os.name");
      if(os.startsWith("Mac OS")==false) {
          fileMnu.addSeparator();
          exitMI = buildMenuItem("menu.item.exit");
          fileMnu.add(exitMI);
      }
      
      //Edit Menu
      JMenu editMnu = new JMenu(Resources.bundle.getString("menu.edit"));
      editMnu.setMnemonic(Resources.bundle.getString("menu.edit.mnemonic.char").charAt(0));
      menuBar.add(editMnu);
      settingsMI = buildMenuItem("menu.item.settings");
      editMnu.add(settingsMI);
      
      //Help Menu
      org.xito.about.AboutService.setDefaultInfoPanel(new AboutPanel());
      JMenu helpMnu = new JMenu(Resources.bundle.getString("menu.help"));
      helpMnu.setMnemonic(Resources.bundle.getString("menu.help.mnemonic.char").charAt(0));
      menuBar.add(helpMnu);
      aboutMI = buildMenuItem("menu.item.about");
      helpMnu.add(aboutMI);
   }
   
   /**
    * Build a Menu Item from Resources using Key
    */
   private JMenuItem buildMenuItem(String key) {
      JMenuItem mi = new JMenuItem(Resources.bundle.getString(key));
      try {
         mi.setMnemonic(Resources.bundle.getString(key+".mnemonic.char").charAt(0));
         mi.setAccelerator(KeyStroke.getKeyStroke(Resources.bundle.getString(key+".accelerator")));
      }
      catch(MissingResourceException exp) {
         logger.warning("No Resource for:"+exp.getKey());
      }
      
      mi.addActionListener(this);
      return mi;
   }
   
   public void actionPerformed(ActionEvent evt) {
      //Offline
      if(evt.getSource() == offlineMI) {
         Boot.setOffline(!Boot.isOffline());
      }      
      
      //Hide
      else if(evt.getSource() == hideAppManagerMI) {
         this.setVisible(false);
      }      
      
      //Exit
      else if(evt.getSource() == exitMI) {
         promptForExit();
      }
      
      //New App 
      else if(evt.getSource() == newAppMI) {
         mainPanel.addApplication();
      }
      
      //New Group
      else if(evt.getSource() == newGroupMI) {
         mainPanel.addGroup();
      }
      
      //Settings
      else if(evt.getSource() == settingsMI) {
         org.xito.controlpanel.ControlPanelService.getFrame().show();
      }
      
      //About
      else if(evt.getSource() == aboutMI) {
         org.xito.about.AboutService.showAboutWindow(this); 
      }
   }
   
   /**
    * Prompt for Exit
    */
   protected void promptForExit() {
      int result = showExitPrompt();
      if(result == DialogManager.YES) {
         shutdown();
      }
   }
   
   /**
    * show the Exit Prompt
    */
   private int showExitPrompt() {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setWindowTitle(Boot.getAppDisplayName());
      desc.setTitle(Boot.getAppDisplayName());
      
      String subtitle = null;
      String msg = null;
      String quitBtnTitle = null;
      String cancelBtnTitle = Resources.bundle.getString("quit.xito.cancel.button");
      
      if(Boot.getCurrentOS() == Boot.MAC_OS) {
         subtitle = Resources.bundle.getString("quit.xito.subtitle.mac");
         msg = Resources.bundle.getString("quit.xito.message.mac");
         quitBtnTitle = Resources.bundle.getString("quit.xito.button.mac");
      }
      else {
         subtitle = Resources.bundle.getString("quit.xito.subtitle");
         msg = Resources.bundle.getString("quit.xito.message");
         quitBtnTitle = Resources.bundle.getString("quit.xito.button");
      }
      
      desc.setSubtitle(subtitle);
      desc.setMessage(msg);
      desc.setIcon(new ImageIcon(MainFrame.class.getResource("/org/xito/launcher/images/xito_64.png")));
      //desc.setGradiantColor(org.xito.boot.ui.Defaults.DIALOG_GRAD_COLOR);
      //desc.setGradiantOffsetRatio(org.xito.boot.ui.Defaults.DIALOG_GRAD_OFFSET);
      desc.setButtonTypes(new ButtonType[]{new ButtonType(cancelBtnTitle, ButtonType.NO), new ButtonType(quitBtnTitle, ButtonType.YES)});
      desc.setShowButtonSeparator(false);
      desc.setType(DialogManager.YES_NO);
      desc.setWidth(375);
      desc.setHeight(210);
      
      return DialogManager.showDialog((Frame)null, desc);
   }
   
   private void buildContentPane() {
      contentPane = new JPanel(new BorderLayout());
      setContentPane(contentPane);
            
      buildMainPanel();
   }
   
   private void buildMainPanel() {
      mainPanel = new MainPanel();
      contentPane.add(mainPanel, BorderLayout.CENTER);
   }
   
}
