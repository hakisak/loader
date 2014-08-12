// Copyright (C) 2002 Xito.org. http://www.xito.org
//
// This file is part of Xito(tm).
//
// This is free software; you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License (LGPL)
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// It is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this application.
//
// Information about the GNU LGPL License can be obtained at
// http://www.gnu.org/licenses/

package org.xito.desktop;

import java.util.*;
import java.util.prefs.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;

import org.w3c.dom.*;

import org.xito.dcf.*;
import org.xito.blx.*;
import org.xito.launcher.LauncherActionFactory;
import org.xito.launcher.LauncherService;

/**
 *
 * @author  drichan
 * @version
 */
public class LaunchPanel extends DCComponent implements ComponentListener {
   
   public static final int CMD_PANEL_WIDTH = 260;
   public final static String ELEMENT_NAME = "launchpanel";
   public final static String DRAG_ICON = "org/xito/launcher/images/master_drag_icon.gif";
   
   protected HashSet launchListeners;
   protected JComboBox cmdList;
   protected JTextField launchField = new JTextField();
   protected TileDragComponent tileDragComponent = new TileDragComponent();
   protected JPanel cmdPanel;
   protected TileSet mySet;
   protected DCTile currentTile;
   protected Preferences prefs;
   
   /**
    * Set to true when we are trying to launch a Command
    */
   protected transient boolean launchInProgress_flag = false;
   
   /**
    * Thread created to launch a Command
    */
   protected transient Thread launchThread;
   
   /**
    * Create the Launch Panel
    */
   public LaunchPanel() {
      super();
      init();
   }
   
   /**
    * Add a Launch Listener to receive launch events
    * @param listener to add
    */
   public void addLaunchListener(LaunchListener listener) {
      if(launchListeners == null) launchListeners = new HashSet();
      launchListeners.add(listener);
   }
   
   /**
    * Remove a Launch Listener to receive launch events
    * @param listener to remove
    */
   public void removeLaunchListener(LaunchListener listener) {
      if(launchListeners == null) return;
      launchListeners.remove(listener);
   }
   
   /**
    * Fire launch stared message to Launch Listeners
    * @param action that is being launched
    */
   /*
   public void fireLaunchStarted(ICmdAction action) {
      Iterator _listeners = launchListeners.iterator();
      while(_listeners.hasNext()) {
         ((ILaunchListener)_listeners.next()).launchStarted(action);
      }
   }
    */
   
   /**
    * Fire launch completed message to Launch Listeners
    * @param action that is finished launching
    */
   /*
   public void fireLaunchCompleted(ICmdAction action) {
      Iterator _listeners = launchListeners.iterator();
      while(_listeners.hasNext()) {
         ((ILaunchListener)_listeners.next()).launchCompleted(action);
      }
   }
    */
   
   
   /**
    * Create the Launch Panel
    */
   private void init() {
      
      //Get Preferences
      prefs = Preferences.userNodeForPackage(LaunchPanel.class);
      
      setLayout(new BorderLayout());
      this.setVisible(false);
      
      //TileSet
      mySet = new TileSet();
      mySet.setDraggable(false);
      mySet.setOrientation(mySet.EAST);
      mySet.setClosed(false);
      mySet.setClosable(false);
      mySet.setShowTileSetIcon(false);
      mySet.setVisible(true);
      mySet.addComponentListener(this);
      add(mySet, BorderLayout.WEST);
      
      //Command Panel
      cmdPanel = new JPanel(null);
      cmdPanel.setBackground(new Color(0xdddefb));
      cmdPanel.setSize(this.CMD_PANEL_WIDTH, 48);
      add(cmdPanel, BorderLayout.CENTER);
      
      //Set size of Launch Panel
      Dimension tsSize = mySet.getPreferredSize();
      setSize(new Dimension(tsSize.width + CMD_PANEL_WIDTH, tsSize.height));
      
      //Command Panel components
      //cmdList = CmdManager.getDefaultManager().getCmdComboBox();
      cmdList = new JComboBox();
      cmdList.setBounds(15, 5, 200, 20);
      cmdList.setBackground(cmdPanel.getBackground());
      
      cmdPanel.add(cmdList);
      
      launchField.setBounds(15, 25, 200, 20);
      launchField.setFont(new JTextField().getFont());
      launchField.setText(prefs.get("launch.cmd", ""));
      cmdPanel.add(launchField);
      
      tileDragComponent.setBounds(225, 25, 20, 20);
      cmdPanel.add(tileDragComponent);
      
      launchField.addKeyListener(new KeyAdapter(){
         public void keyPressed(KeyEvent _evt){
            if(_evt.getKeyCode() == _evt.VK_ENTER) launch();
         }
      });
      
      loadLaunchActionFactories();
   }
   
   /**
    * Load the various command actions
    */
   private void loadLaunchActionFactories() {
      LauncherActionFactory[] factories = LauncherService.getActionFactories();
   }
   
   /**
    * create a Task for the current Command
    */
   /*
   public ICmdAction createCmdAction() {
      
      ICmdFactory factory = null;
      String cmd = getCommand();
      //Get CmdACtion from Selected Factory
      
      try {
         factory = (ICmdFactory)cmdList.getSelectedItem();
         return factory.getCmdAction(cmd);
      }
      catch(Exception exp) {
         exp.printStackTrace();
         String factoryName = factory.getResourceName();
         //String title = MessageFormat.format(CmdManager.resources.getString("unknown.error.title"),new String[]{factoryName});
         String title = DesktopService.getResources().getString("command.error.title");
         title = MessageFormat.format(title, new String[]{factoryName});
         AlertManager.getDefaultAlertManager().showError(null, null, title, exp.getLocalizedMessage(), exp);
      }
      
      return null;
   }
    */
   
   /**
    * Get this Component's Data Element
    */
   public Element getDataElement() {
      
      Document _doc = createDOMDocument();
      Element _element = _doc.createElement(ELEMENT_NAME);
      
      //TileSet
      Element tsElement = mySet.getBLXElement().getDOMElement();
      tsElement = (Element)appendChild(_doc, tsElement, mySet.getDataElement());
      _element = (Element)appendChild(_doc, _element, tsElement);
      
      return _element;
   }
   
   /**
    * Set the Node on this Component
    * @param pElement Node that contains this components settings
    * @param pRelativeURL the URL that all HREFs would be relative to
    */
   public void setBLXElement(BLXElement blxElement) {
      
      super.setBLXElement(blxElement);
      Element myElement = blxElement.getDataElement();
      
      //LaunchPanel not found just return
      if(myElement == null) return;
      
      //Embedded Components
      //Only a TileSet can be embedded into the Launch Panel
      //All other BLX Components will be ignored.
      NodeList nodes = myElement.getChildNodes();
      for(int i=0;i<nodes.getLength();i++) {
         
         //Only process Elements
         if(nodes.item(i).getNodeType()!= Node.ELEMENT_NODE) continue;
         
         Element tsElement = (Element)nodes.item(i);
         
         //Create Tile Set Component
         try {
            
            Component comp = BLXCompFactory.getInstance().getComponent(new BLXElement(tsElement, blxElement.getContextURL()), null);
            
            //TileSet
            if(comp instanceof TileSet) {
               mySet.removeComponentListener(this);
               remove(mySet);
               mySet = (TileSet)comp;
               mySet.setDraggable(false);
               mySet.setOrientation(mySet.EAST);
               mySet.setClosed(false);
               mySet.setClosable(false);
               mySet.setShowTileSetIcon(false);
               mySet.setVisible(true);
               mySet.addComponentListener(this);
               add(mySet, BorderLayout.WEST);
               break;
            }
         }
         catch(Exception exp) {
            //Could be ClassNotFound or InstantiationException
            //Ignore error the Component will just be skipped.
            exp.printStackTrace();
         }
      }//End For Loop
   }
   
   /**
   * Return true if this components state has changed in a way that
   * Requires a new XML Node to be fetched
   * @return true if component has changed
   */
  public boolean isDirty() {
    
    //Has the launchPanel changed
    if (super.isDirty()) return true;
    
    //Has the TileSet changed
    if(mySet.isDirty()) {
       return true;
    }
        
    //Just return false
    return false;
  }
   
   /**
    * Launch the Current Command
    */
   public void launch() {
      
      //Set Flag
      launchInProgress_flag = true;
      
      launchThread = new Thread(new Runnable() {
         public void run() {
            /*
            //Notify listeners that we are starting the Launch
            launchField.setEditable(false);
            fireLaunchStarted(null);
            ICmdAction _cmdAction = createCmdAction();
            fireLaunchStarted(_cmdAction);
            
            //Command Not Created Show Error
            if(_cmdAction == null) {
               fireLaunchCompleted(_cmdAction);
               launchField.setEditable(true);
               return;
            }
            
            _cmdAction.actionPerformed(new ActionEvent(this, 0 ,  ""));
            fireLaunchCompleted(_cmdAction);
            launchField.setEditable(true);
             */
         }
      });
      
      //Store the command in recent commands prefs
      //prefs.put("launch.cmd", launchField.getText());
      //prefs.put("launch.cmd.factory", ((ICmdFactory)cmdList.getSelectedItem()).getResourceName());
      
      //Start the Thread
      launchThread.start();
      
   }
   
   /**
    * Get the Current Command String
    */
   public String getCommand() {
      if(launchField.getText().equals("")) 
         return null;
      else
         return launchField.getText();
   }
   
   /**
    * Set the Current Command String
    */
   public void setCommand(String pCmd) {
      launchField.setText(pCmd);
   }
   
   /*************
    * Component Listener used to listen for size changes of the Embedded TileSet
    ********************/
   /**
    * Invoked when the component has been made visible.
    * Launch Panel is listening for changes to mySet
    */
   public void componentShown(ComponentEvent e) {
   }
   
   /**
    * Invoked when the component's position changes.
    *Launch Panel is listening for changes to mySet
    */
   public void componentMoved(ComponentEvent e) {
   }
   
   /**
    * Invoked when the component's size changes.
    * Launch Panel is listening for changes to internal TileSet
    */
   public void componentResized(ComponentEvent e) {
      
      Dimension tsSize = mySet.getPreferredSize();
      setSize(new Dimension(tsSize.width + CMD_PANEL_WIDTH, tsSize.height));
   }
   
   /**
    * Invoked when the component has been made invisible.
    * Launch Panel is listening for changes to mySet
    */
   public void componentHidden(ComponentEvent e) {
   }
   
   /**
    * This is the component that can be dragged off the LaunchPanel to create Tiles
    * Based on the current Command
    */
   class TileDragComponent extends DCComponent {
      
      DCTile tile = new DCTile();
      ImageIcon image;
      
      /**
       * Component use to drag a new tile from the Master Tile
       */
      public TileDragComponent() {
         super();
         setDragSourceListener(new MyDragSourceListener());
         setDraggable(true);
         this.setSize(20,20);
         this.setVisible(true);
         setToolTipText("Drag Command to Desktop");
         image = new ImageIcon(getClass().getResource(DRAG_ICON));
         
         //Mouse Click Listener. Launch the Current Command
         addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent pEvent){
               LaunchPanel.this.launch();
            }
         });
      }
      
      public void paintComponent(Graphics pGraphics) {
         pGraphics.drawImage(image.getImage(), 0, 0, null);
      }
      
      /** The Dnd Transferable Object for this Component
       */
      public Transferable getTransferable(int dndAction) {
         currentTile = new DCTile((Icon)null, null, null, null);
         
         return currentTile.getTransferable(DnDConstants.ACTION_MOVE);
      }
      
      /** Get the Image for the Tile Drag
       */
      public Image getComponentDragImage() {
         return tile.getComponentDragImage();
      }
   }
   
   /**
    * Class used to Render Command List
    */
   class MyListCellRenderer extends DefaultListCellRenderer {
      
      /**
       * Get the Component to render this list
       */
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
         Component _comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
         /*
         try {
            ICmdFactory _factory = (ICmdFactory)value;
            ((JLabel)_comp).setText(_factory.getResourceName());
            Icon _icon = _factory.getSmallIcon();
            if(_icon != null) ((JLabel)_comp).setIcon(_icon);
         }
         catch(ClassCastException _exp) {
            //Ignore
         }
         catch(Exception _exp) {
            //Just ignore problem with Factory
            _exp.printStackTrace();
         }
         */
         return _comp;
      }
   }
   
   /**
    * DragSource Listener that is notified about Drag Operations of the DragComponent
    */
   class MyDragSourceListener implements DragSourceListener {
      public void dragOver(java.awt.dnd.DragSourceDragEvent dragSourceDragEvent) {
      }
      
      public void dragExit(java.awt.dnd.DragSourceEvent dragSourceEvent) {
      }
      
      public void dropActionChanged(java.awt.dnd.DragSourceDragEvent dragSourceDragEvent) {
      }
      
      public void dragEnter(java.awt.dnd.DragSourceDragEvent dragSourceDragEvent) {
      }
      
      /**
       * The DragDropEnd creats an Action for a Tile when the Tile has been dropped
       */
      public void dragDropEnd(java.awt.dnd.DragSourceDropEvent pEvent) {
         //Place action into Dropped Tile
         if(pEvent.getDropSuccess()==true) {
            
            //Create the Dropped Task in a Seperate Thread
            Thread dropThread = new Thread(new Runnable() {
               public void run() {
                  /*
                  currentTile.showBusy();
                  //Create the Task from the Command Line
                  ICmdAction _action = createCmdAction();
                  
                  //Setup Action for Tile
                  if(_action != null) {
                     currentTile.setAction(_action);
                  }
                  
                  currentTile.hideBusy();
                   */
               }
            });
            
            //Start CmdTask Creation
            dropThread.start();
         }
      }
   }
}
