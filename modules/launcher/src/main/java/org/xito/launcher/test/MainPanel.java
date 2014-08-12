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

package org.xito.launcher.test;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import org.xito.dialog.*;
import org.xito.boot.*;

import org.xito.launcher.*;
import org.xito.launcher.applet.*;
import org.xito.launcher.jnlp.*;
import org.xito.launcher.sys.*;
import org.xito.launcher.web.*;

/**
 *
 * @author Deane Richan
 */
public class MainPanel extends JPanel {
   
   private JList testList;
   
   public static void main(String args[]) {
      
      MainPanel p = new MainPanel();
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setTitle("Xito Launcher Sample");
      desc.setSubtitle("Sample Application to demonstrate Launcher");
      desc.setMessageType(DialogManager.INFO_MSG);
      desc.setButtonTypes(new ButtonType[]{new ButtonType("Close", 1)});
      desc.setCustomPanel(p);
      desc.setWidth(350);
      desc.setHeight(500);
      desc.setResizable(true);

      CustomDialog dialog = new CustomDialog((Frame)null, desc, false);
      dialog.addWindowListener(new WindowAdapter() {
         
         public void windowClosed(WindowEvent e) {
            Boot.endSession(true);
         }
         
      });
      dialog.setVisible(true);
   }
   
   public MainPanel() {
      super();
      init();
   }
   
   private void init() {
      
     setLayout(new TableLayout(MainPanel.class.getResource("main_layout.html")));
           
      JLabel lbl = new JLabel();
      String html = "<html><p>This is a sample application that demonstrates " +
         "applications running using the Xito Launcher Service.<br></p><p>The Launcher Service provides support for: " +
         "<b>Applets, Java Applications, JNLP Applications, Web Apps,</b> and <b>Local System Applications</b>.<br></p>" +
         "<p>Double-Click a Test application below to launch it.</p>" +
         "</html>";
      
      lbl.setText(html);
      add("description", lbl);      
      
      JButton addBtn = new JButton("Add Action");
      addBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            addAction();
         }
      });
      add("add_button", addBtn);
      
      //Edit Popup Menu
      final JPopupMenu editMnu = new JPopupMenu();
      JMenuItem editMI = new JMenuItem("Edit");
      editMnu.add(editMI);
      editMI.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            editSelected();
         }
      });
      
      testList = new JList(new DefaultListModel());
      
      Iterator it = getActionList().iterator();
      while(it.hasNext()) {
         ((DefaultListModel)testList.getModel()).addElement(it.next());
      }
      
      testList.addMouseListener(new MouseAdapter(){
         public void mouseClicked(MouseEvent evt) {
            Action a = (Action)testList.getSelectedValue();
            if(evt.getClickCount()==2)
               a.actionPerformed(new ActionEvent(testList, -1, ""));
            if(evt.getClickCount()==1 && (evt.isPopupTrigger() || evt.getButton() == evt.BUTTON3)){
               testList.setSelectedIndex(testList.locationToIndex(evt.getPoint()));
               editMnu.show(testList, evt.getX(), evt.getY());
            }
               
         }
      });
      testList.setCellRenderer(new MyCellRenderer());
      testList.setSelectedIndex(0);
      testList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      
      add("actions", new JScrollPane(testList));      
   }
   
   public void addAction() {
      
      LauncherService.createAction(null, new LauncherActionCreatedListener() {
         
         public void launcherActionCreated(LauncherAction action) {
            if(action != null) {
               ((DefaultListModel)testList.getModel()).addElement(action);
            }
         }
      });
   }
   
   private Vector getActionList() {
      Vector items = new Vector();
            
      AppletActionFactory appletFactory = new AppletActionFactory();
      JNLPActionFactory jnlpFactory = new JNLPActionFactory();
      LocalAppActionFactory localFactory = new LocalAppActionFactory();
      WebActionFactory webFactory = new WebActionFactory();
      
      //HTML Editor
      /*
      try {
         AppletDesc appletDesc = new AppletDesc();
         appletDesc.setResourceName("editor-applet");
         appletDesc.setTitle("HTML Editor Applet");
         appletDesc.setDocumentURL(new URL("http://www.hexidec.com/ekitdemo.php"));
         items.add(new AppletAction(appletDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }
       */
      
      //JRisk
      try {
         AppletDesc appletDesc = new AppletDesc();
         appletDesc.setName("JRisk");
         appletDesc.setTitle("JRisk");
         appletDesc.setDocumentURL(new URL("http://jrisk.sourceforge.net/swinggui.shtml"));
         items.add(new AppletAction(appletFactory, appletDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }
      
      //Freecell
      try {
         AppletDesc appletDesc = new AppletDesc();
         appletDesc.setName("FreeCell");
         appletDesc.setTitle("Free Cell Solitaire");
         appletDesc.setDocumentURL(new URL("http://www.idiotsdelight.net/v109/freecell.html"));
         items.add(new AppletAction(appletFactory, appletDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }
      
      //Mario Brothers 3
      try {
         AppletDesc appletDesc = new AppletDesc();
         appletDesc.setName("MarioBro3");
         appletDesc.setTitle("Super Mario Brothers 3");
         appletDesc.setDocumentURL(new URL("http://xito.sourceforge.net/apps/games/nes/super_mario_bros_3.html"));
         items.add(new AppletAction(appletFactory, appletDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }
      
      //Mario Brothers
      try {
         AppletDesc appletDesc = new AppletDesc();
         appletDesc.setName("MarioBro");
         appletDesc.setTitle("Mario Brothers");
         appletDesc.setDocumentURL(new URL("http://xito.sourceforge.net/apps/games/nes/super_mario_bros.html"));
         items.add(new AppletAction(appletFactory, appletDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }

       //DK
       try {
           AppletDesc appletDesc = new AppletDesc();
           appletDesc.setName("DK");
           appletDesc.setTitle("DK");
           appletDesc.setDocumentURL(new URL("http://www.virtualnes.com/play/NES-DK"));
           items.add(new AppletAction(appletFactory, appletDesc));
       }
       catch(MalformedURLException badURL) {
           badURL.printStackTrace();
       }



      //Adventrure
      try {
         AppletDesc appletDesc = new AppletDesc();
         appletDesc.setName("Adventure");
         appletDesc.setTitle("Adventure");
         appletDesc.setDocumentURL(new URL("http://xito.sourceforge.net/apps/games/atari/adventure.html"));
         items.add(new AppletAction(appletFactory, appletDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }

      //Donkey Kong
      try {
         AppletDesc appletDesc = new AppletDesc();
         appletDesc.setName("DonkeyKong");
         appletDesc.setTitle("Donkey Kong");
         appletDesc.setDocumentURL(new URL("http://www.old-games.nu/game271.php"));
         items.add(new AppletAction(appletFactory, appletDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }   
      
      //Jaxito-Roids
      try {
         JNLPAppDesc jnlpDesc = new JNLPAppDesc();
         jnlpDesc.setJNLPAddress(new URL("http://jaxito.com/roids/roids.jnlp"));
         jnlpDesc.setName("Roids");
         jnlpDesc.setTitle("Jaxito-Roids");
         //jnlpDesc.setSeperateVM(true);
         items.add(new JNLPAction(jnlpFactory, jnlpDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }  
      
      //Democrat Roids
      try {
         JNLPAppDesc jnlpDesc = new JNLPAppDesc();
         jnlpDesc.setJNLPAddress(new URL("http://jaxito.com/democrat_roids.jnlp"));
         jnlpDesc.setName("DemRoids");
         jnlpDesc.setTitle("Democrat-Roids");
         //jnlpDesc.setSeperateVM(true);
         items.add(new JNLPAction(jnlpFactory, jnlpDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      } 

      
      //Clock
      try {
         AppletDesc appletDesc = new AppletDesc();
         appletDesc.setName("clock");
         appletDesc.setTitle("Clock Applet");
         appletDesc.setDocumentURL(new URL("http://www.adcsoft.com/javaclock.html"));
         items.add(new AppletAction(appletFactory, appletDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }
          
      //JTrack
      try {
         AppletDesc appletDesc = new AppletDesc();
         appletDesc.setName("JTrack");
         appletDesc.setTitle("JTrack Applet");
         appletDesc.setDocumentURL(new URL("http://science.nasa.gov/RealTime/JTrack/Spacecraft.html"));
         items.add(new AppletAction(appletFactory, appletDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }
            
      //JTrack 3D
      /*
      try {
         AppletDesc appletDesc = new AppletDesc();
         appletDesc.setResourceName("JTrack3D");
         appletDesc.setTitle("JTrack 3D Applet");
         appletDesc.setDocumentURL(new URL("http://science.nasa.gov/Realtime/JTrack/3D/AppletFrame.html"));
         items.add(new AppletAction(appletDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }
       */
      
      //Solar System Viewer
      try {
         AppletDesc appletDesc = new AppletDesc();
         appletDesc.setSeperateVM(true);
         appletDesc.setName("SolarSystem");
         appletDesc.setTitle("Solar System Viewer Applet");
         appletDesc.setDocumentURL(new URL("http://janus.astro.umd.edu/javadir/orbits/ssv.html"));
         items.add(new AppletAction(appletFactory, appletDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }
      
      //Moon Orbits
      try {
         AppletDesc appletDesc = new AppletDesc();
         appletDesc.setSeperateVM(true);
         appletDesc.setName("moons");
         appletDesc.setTitle("Moon Orbits Applet");
         appletDesc.setDocumentURL(new URL("http://janus.astro.umd.edu/javadir/orbits/moons.html"));
         items.add(new AppletAction(appletFactory, appletDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }
            
      //Power of Ten Applet
      try {
         AppletDesc appletDesc = new AppletDesc();
         appletDesc.setName("poften1");
         appletDesc.setTitle("Power of Ten Applet");
         appletDesc.setDocumentURL(new URL("http://micro.magnet.fsu.edu/primer/java/scienceopticsu/powersof10/index.html"));
         items.add(new AppletAction(appletFactory, appletDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }
      
      //Caffeine Molecule Viewer
      try {
         AppletDesc appletDesc = new AppletDesc();
         appletDesc.setName("moleviewer");
         appletDesc.setTitle("Caffeine Molecular Viewer");
         appletDesc.setDocumentURL(new URL("http://jmol.sourceforge.net/demo/jssample0/"));
         items.add(new AppletAction(appletFactory, appletDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }
      
      //Local Calc App
      LocalAppDesc appDesc = new LocalAppDesc();
      appDesc.setName("calc");
      appDesc.setTitle("Calculator (win32)");
      appDesc.setExecutableCmd("calc.exe");
      items.add(new LocalAppAction(localFactory, appDesc));
      
      //Local Notepad App
      appDesc = new LocalAppDesc();
      appDesc.setName("notepad");
      appDesc.setTitle("Notepad (win32)");
      appDesc.setExecutableCmd("notepad.exe");
      items.add(new LocalAppAction(localFactory, appDesc));
      
      //Xito Web Site
      WebDesc webDesc = new WebDesc();
      webDesc.setName("Xito WebSite");
      webDesc.setAddress("http://xito.sourceforge.net");
      items.add(new WebAction(webFactory, webDesc));
      
      //Google Web Site
      webDesc = new WebDesc();
      webDesc.setName("Google");
      webDesc.setAddress("www.google.com");
      items.add(new WebAction(webFactory, webDesc));
      
      //SwingSet JNLP
      try {
         JNLPAppDesc jnlpDesc = new JNLPAppDesc();
         jnlpDesc.setJNLPAddress(new URL("http://java.sun.com/products/javawebstart/apps/swingset2.jnlp"));
         jnlpDesc.setName("swingset");
         jnlpDesc.setTitle("SwingSet (WebStart)");
         jnlpDesc.setUseWebStart(true);
         items.add(new JNLPAction(jnlpFactory, jnlpDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }
      
      //SwingSet JNLP
      try {
         JNLPAppDesc jnlpDesc = new JNLPAppDesc();
         jnlpDesc.setJNLPAddress(new URL("http://java.sun.com/products/javawebstart/apps/swingset2.jnlp"));
         jnlpDesc.setName("swingset");
         jnlpDesc.setTitle("SwingSet (JNLP - Internal)");
         items.add(new JNLPAction(jnlpFactory, jnlpDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }
      
      //Asteroids JNLP
      try {
         JNLPAppDesc jnlpDesc = new JNLPAppDesc();
         jnlpDesc.setJNLPAddress(new URL("http://xito.sourceforge.net/apps/games/asteroids/asteroids.jnlp"));
         jnlpDesc.setName("asteroids");
         jnlpDesc.setTitle("Asteroids (JNLP - Internal)");
         items.add(new JNLPAction(jnlpFactory, jnlpDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }   
      
      //Editor
      try {
         JNLPAppDesc jnlpDesc = new JNLPAppDesc();
         jnlpDesc.setJNLPAddress(new URL("http://xito.sourceforge.net/apps/tools/editor/editor.jnlp"));
         jnlpDesc.setName("editor");
         jnlpDesc.setTitle("Editor (JNLP - Internal)");
         items.add(new JNLPAction(jnlpFactory, jnlpDesc));
      }
      catch(MalformedURLException badURL) {
         badURL.printStackTrace();
      }      
      
      return items;
   }
   
   private void editSelected() {
      LauncherAction a = (LauncherAction)testList.getSelectedValue();
      a.edit(null);
   }
   
   /**
    * Cell Renderer for List of Action
    */
   private class MyCellRenderer extends DefaultListCellRenderer { 
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
         JLabel lbl = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
         if(!(value instanceof Action)) {
            lbl.setText("unknown");
         }
         
         Action a = (Action)value;
         
         lbl.setIcon((Icon)a.getValue(Action.SMALL_ICON));
         lbl.setText(a.toString());
                           
         return lbl;
      }
   }
}
