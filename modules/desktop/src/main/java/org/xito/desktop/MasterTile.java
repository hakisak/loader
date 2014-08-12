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

import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.prefs.*;

import org.w3c.dom.*;

import org.xito.dcf.*;
import org.xito.blx.*;

/**
 * MasterTile
 *
 *
 * @author  $Author: drichan $
 * @version $Revision: 1.4 $
 * @since   $Date: 2006/04/08 04:47:33 $
 */
public class MasterTile extends DCComponent implements ActionListener, LaunchListener, ComponentListener {
  
  protected boolean closed_flag = true;
  protected static final int SIDE_WIDTH = 9;
  protected static final Dimension CLOSED_SIZE = new Dimension(SIDE_WIDTH + 48, 48);
  
  protected static final int TOP_LEFT = 1;
  protected static final int BOTTOM_LEFT = 2;
  protected static final int TOP_RIGHT = 3;
  protected static final int BOTTOM_RIGHT = 4;
  
  protected static final String TOP_LEFT_STR = "TOP_LEFT";
  protected static final String BOTTOM_LEFT_STR = "BOTTOM_LEFT";
  protected static final String TOP_RIGHT_STR = "TOP_RIGHT";
  protected static final String BOTTOM_RIGHT_STR = "BOTTOM_RIGHT";
  
  public final static String BG = "org/xito/launcher/images/master_bg.gif";
  public final static String LEFT_SIDE = "org/xito/launcher/images/tileset_left.gif";
  public final static String RIGHT_SIDE = "org/xito/launcher/images/tileset_right.gif";
  
  public final static String ELEMENT_NAME = "mastertile";
  public final static String CLOSED_ATTR = "closed";
  public final static String LOCATION_ATTR = "location";
  
  protected ImageIcon bgImage;
  protected ImageIcon busyIcon;
  protected ImageIcon openBgImage;
  protected ImageIcon leftSideImage;
  protected ImageIcon rightSideImage;
  protected int locGrid = TOP_LEFT;
  
  protected BLXCompFactory componentFactory;
  protected DesktopService desktopService;
  protected Desktop desktop;
  protected LaunchPanel launchPanel;
  protected TileSet masterTileSet;
  
  protected DesktopMenuModel menuModel;
  protected DesktopMenu menu;
  protected JMenuItem newTileSetMI;
  protected JMenuItem newStickyMI;
  
  /**
   * Set to true when we are trying to launch a Command
   */
  protected transient boolean launchInProgress_flag = false;
  
  /**
   * Prefence Object used to store preferences for the master tile
   */
  protected Preferences preference;
  
  //Tile Management
  protected ArrayList dockedTiles = new ArrayList();
  
  /** Create a Master Tile
   */
  public MasterTile() {
    super();
    init();
  }
  
  /**
   * Create the MasterTile
   */
  private void init() {
    
    try {
      //Get Services
      desktopService = DesktopService.getDefaultService();
      desktop = desktopService.getCurrentDesktop();
      
      componentFactory = BLXCompFactory.getInstance();
      
      //Get Images
      bgImage = new ImageIcon(getClass().getResource(BG));
      busyIcon = new ImageIcon(DCTile.class.getResource(DCTile.DEFAULT_BUSY));
      leftSideImage = new ImageIcon(getClass().getResource(LEFT_SIDE));
      rightSideImage = new ImageIcon(getClass().getResource(RIGHT_SIDE));
      
      //Create Master TileSet
      masterTileSet = new TileSet();
      masterTileSet.setDraggable(false);
      masterTileSet.setShowTileSetIcon(false);
      masterTileSet.setShowTileSetIcon(false);
      
      //Setup Launch Panel
      launchPanel = new LaunchPanel();
      launchPanel.addLaunchListener(this);
      launchPanel.addComponentListener(this);
      setLayout(null);
      add(launchPanel);
      setDraggable(true);
      setClosed(true);
      setGridLocation(TOP_LEFT);
    
      //Mouse Support on Master Tile
      addMouseListener(new MyMouseAdapter());
    }
    catch(Throwable t) {t.printStackTrace();}
  }
  
  /**
   * Create the Menus for the master tile
   */
  private void initMenus() {
    
    //Load the Root Model into the Desktop if it isn't there already
    DesktopMenuModel rootModel = DesktopMenuModel.getRootModel();
    DesktopService.getDefaultService().getCurrentDesktop().addDesktopObject(rootModel);
    menu = new DesktopMenu();
    menu.setModel(rootModel);
  }
  
  /********************
   * Component Listener used to listen for size changes of the Launch Panel
   ********************/
  /**
   * Invoked when the component has been made visible.
   * MasterTile is listening for changes to launchPanel
   */
  public void componentShown(ComponentEvent e) {
  }
  
  /**
   * Invoked when the component's position changes.
   * MasterTile is listening for changes to launchPanel
   */
  public void componentMoved(ComponentEvent e) {
  }
  
  /**
   * Invoked when the component's size changes.
   * MasterTile is listening for changes to launchPanel
   */
  public void componentResized(ComponentEvent e) {
    
    //Set Closed status again and Force changes to tak effect
    setClosed(isClosed(), true);
  }
  
  /**
   * Invoked when the component has been made invisible.
   * MasterTile is listening for changes to launchPanel
   */
  public void componentHidden(ComponentEvent e) {
  }
  
  /** Is the Master Tile Closed
   */
  public boolean isClosed() {
    return closed_flag;
  }
  
  /**
   * Close or Open the Master Tile
   */
  public void setClosed(boolean pClosed) {
    setClosed(pClosed, false);
  }
  
  /**
   * Close or Open the Master Tile
   * Used to force a Size change in the MasterTile
   */
  protected void setClosed(boolean pClosed, boolean pForce) {
    //Do nothing if it hasn't Change
    if(closed_flag == pClosed && pForce == false) return;
    
    //Update Status
    setIsDirty(true);
    closed_flag = pClosed;
    Dimension openSize = launchPanel.getSize();
    openSize.width = openSize.width + CLOSED_SIZE.width;
    
    if(locGrid == TOP_LEFT || locGrid == BOTTOM_LEFT) {
      if(pClosed) {
        launchPanel.setVisible(false);
        setSize(CLOSED_SIZE);
        Point _loc = this.getLocation();
        _loc.translate((openSize.width - CLOSED_SIZE.width), 0);
        super.setLocation(_loc.x, _loc.y);
      }
      else {
        launchPanel.setVisible(true);
        Dimension oldSize = getSize();
        setSize(openSize);
        Point _loc = this.getLocation();
        _loc.translate(oldSize.width - openSize.width, 0);
        super.setLocation(_loc.x, _loc.y);
      }
    }
    else {
      if(pClosed) {
        launchPanel.setVisible(false);
        setSize(CLOSED_SIZE);
      }
      else {
        launchPanel.setVisible(true);
        setSize(openSize);
      }
    }
  }
  
  /**
   * Get Transferable. Override base method to only
   * support DnDConstants.ACTION_MOVE
   */
  public Transferable getTransferable(int dragAction) {
     //only support move
     return super.getTransferable(DnDConstants.ACTION_MOVE);
  }
  
  /**
   * PaintComponent
   */
  public void paintComponent(Graphics pGraphics) {
    super.paintComponent(pGraphics);
    int width = getSize().width;
    int lpwidth = launchPanel.getPreferredSize().width;
    
    //Left Location
    if(locGrid == TOP_LEFT || locGrid == BOTTOM_LEFT) {
      if(closed_flag) {
        //Side Image
        pGraphics.drawImage(rightSideImage.getImage(), 0, 0, null);
        
        if(launchInProgress_flag) {
          //Show Busy if Firing Action
          busyIcon.paintIcon(this, pGraphics, width - 48, 0);
        }
        else {
          //BG Image
          pGraphics.drawImage(bgImage.getImage(), width - 48, 0, null);
        }
      }
      else {
        //Side Image
        pGraphics.drawImage(leftSideImage.getImage(), 0, 0, null);
        
        if(launchInProgress_flag) {
          //Show Busy if Firing Action
          busyIcon.paintIcon(this, pGraphics, width - 48, 0);
        }
        else {
          //BG Image
          pGraphics.drawImage(bgImage.getImage(), width - 48, 0, null);
        }
      }
    }
    //Right Location
    else {
      if(closed_flag) {
        //Side Image
        pGraphics.drawImage(leftSideImage.getImage(), 48, 0, null);
        
        if(launchInProgress_flag) {
          //Show Busy if Firing Action
          busyIcon.paintIcon(this, pGraphics, 0, 0);
        }
        else {
          //BG Image
          pGraphics.drawImage(bgImage.getImage(), 0, 0, null);
        }
      }
      else {
        //Side Image
        pGraphics.drawImage(rightSideImage.getImage(), width - SIDE_WIDTH, 0, null);
        
        if(launchInProgress_flag) {
          //Show Busy if Firing Action
          busyIcon.paintIcon(this, pGraphics, 0, 0);
        }
        else {
          //BG Image
          pGraphics.drawImage(bgImage.getImage(), 0, 0, null);
        }
      }
    }
  }
  
  /**
   * SetOnDesktop Overridden to support proper placement of Master TileSet
   * @pUseDesktop true if MasterTile is on Desktop
   */
  public void setOnDesktop(boolean pUseDesktop) {
    super.setOnDesktop(pUseDesktop);
    masterTileSet.setOnDesktop(pUseDesktop);
  }
  
  /**
   * setVisible Overridden to support proper placement of Master TileSet
   * @pVisible true if MasterTile should be visible
   */
  public void setVisible(boolean pVisible) {
    super.setVisible(pVisible);
    masterTileSet.setVisible(pVisible);
  }
  
  /**
   * setLocation Overridden to place master tile in one of four corners
   * @param x
   * @param y
   */
  public void setLocation(int x, int y) {
    
    //Fix the Location of the MasterTile. The MasterTile
    //Can only be in one of the four corners of its parent
    if(isOnDesktop()) {
      
      Dimension _size = desktopService.getDesktopSize();
            
      if(x>(_size.width/2) && y < (_size.height/2)) setGridLocation(TOP_LEFT);
      if(x>(_size.width/2) && y > (_size.height/2)) setGridLocation(BOTTOM_LEFT);
      if(x<(_size.width/2) && y < (_size.height/2)) setGridLocation(TOP_RIGHT);
      if(x<(_size.width/2) && y > (_size.height/2)) setGridLocation(BOTTOM_RIGHT);
      
      //if(x<(_size.width/2) && y < (_size.height/2)) setGridLocation(TOP_LEFT);
      //if(x<(_size.width/2) && y > (_size.height/2)) setGridLocation(BOTTOM_LEFT);
    }
  }
  
  /**
   * Set the Location of the Master Tile to one of the corners of the desktop
   * @param pLocation TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT
   */
  public void setGridLocation(int pLocation) {
    
    Dimension _deskTopSize = desktopService.getDesktopSize();
    Insets _insets = desktopService.getDesktopInsets();
    
    locGrid = pLocation;
    Dimension lpSize = launchPanel.getSize();
    DesktopLayout deskLayout = null;
    //Get this MasterTile's Desktop
    if(desktopService.getCurrentDesktop() != null && desktopService.getCurrentDesktop().contains(this)) {
      deskLayout = desktopService.getCurrentDesktop().getDesktopLayout();;
    }
    
    //TOP_LEFT
    if(locGrid == TOP_LEFT) {
      launchPanel.setBounds(SIDE_WIDTH, 0, lpSize.width, 48);
      super.setLocation(_deskTopSize.width - getWidth(), _insets.top);
      masterTileSet.setOrientation(TileSet.SOUTH);
      masterTileSet.setLocation(_deskTopSize.width - 48, _insets.top + 48);
      if(deskLayout != null && deskLayout instanceof DesktopGridLayout) {
        ((DesktopGridLayout)deskLayout).setFlowDirection(DesktopGridLayout.FLOW_SW);
      }
    }
    //BOTTOM_LEFT
    else if(locGrid == BOTTOM_LEFT) {
      launchPanel.setBounds(SIDE_WIDTH, 0, lpSize.width, 48);
      super.setLocation(_deskTopSize.width - getWidth(), _deskTopSize.height - getHeight());
      masterTileSet.setOrientation(TileSet.NORTH);
      masterTileSet.setLocation(_deskTopSize.width - 48, _deskTopSize.height - (48 + masterTileSet.getHeight()));
      if(deskLayout != null && deskLayout instanceof DesktopGridLayout) {
        ((DesktopGridLayout)deskLayout).setFlowDirection(DesktopGridLayout.FLOW_NW);
      }
    }
    //TOP_RIGHT
    else if(locGrid == TOP_RIGHT) {
      launchPanel.setBounds(48, 0, lpSize.width, 48);
      super.setLocation(0,_insets.top);
      masterTileSet.setOrientation(TileSet.SOUTH);
      masterTileSet.setLocation(0, _insets.top + 48);
      if(deskLayout != null && deskLayout instanceof DesktopGridLayout) {
        ((DesktopGridLayout)deskLayout).setFlowDirection(DesktopGridLayout.FLOW_SE);
      }
    }
    //BOTTOM_RIGHT
    else if(locGrid == BOTTOM_RIGHT) {
      launchPanel.setBounds(48, 0, lpSize.width, 48);
      super.setLocation(0, _deskTopSize.height - getHeight());
      masterTileSet.setOrientation(TileSet.NORTH);
      masterTileSet.setLocation(0, _deskTopSize.height - (48 + masterTileSet.getHeight()));
      if(deskLayout != null && deskLayout instanceof DesktopGridLayout) {
        ((DesktopGridLayout)deskLayout).setFlowDirection(DesktopGridLayout.FLOW_NE);
      }
    }
    
    invalidate();
    repaint();
  }
  
  /**
   * Gets the Location of the Master Tile on the Destop
   * @return TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT
   */
  public int getGridLocation() {
    return locGrid;
  }
  
  /**
   * Converts the grid location to a String
   * @return String of TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT
   */
  protected String convertGridLocToString(int pLocation) {
    switch(pLocation) {
      case TOP_LEFT:     return TOP_LEFT_STR;
      case BOTTOM_LEFT:  return BOTTOM_LEFT_STR;
      case TOP_RIGHT:    return TOP_RIGHT_STR;
      case BOTTOM_RIGHT: return BOTTOM_RIGHT_STR;
    }
    
    return TOP_LEFT_STR;
  }
  
  /**
   * Converts the grid location from a String
   * @return String of TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT
   */
  protected int convertGridLocFromString(String pLocation) {
    if(pLocation.equals(TOP_LEFT_STR))    return TOP_LEFT;
    else if(pLocation.equals(BOTTOM_LEFT_STR)) return BOTTOM_LEFT;
    else if(pLocation.equals(TOP_RIGHT_STR))   return TOP_RIGHT;
    else if(pLocation.equals(BOTTOM_RIGHT_STR))return BOTTOM_RIGHT;
    
    return TOP_LEFT;
  }
  
  /** Action Performed
   */
  public void actionPerformed(ActionEvent pEvent) {
    
    //New TileSet
    if(pEvent.getSource()==newTileSetMI) {
      TileSet tileSet = new TileSet();
      tileSet.setOnDesktop(true);
      tileSet.setVisible(true);
      desktop.addDesktopComponent(tileSet);
    }
    
    //New Sticky
    if(pEvent.getSource()==newStickyMI) {
      StickyNote note = new StickyNote();
      note.setOnDesktop(true);
      note.setVisible(true);
      desktop.addDesktopComponent(note);
    }
  }
  
  /**
   * Return true if this components state has changed in a way that
   * Requires a new XML Node to be fetched
   * @return true if component has changed
   */
  public boolean isDirty() {
    
    //Has the Master Tile Changedsetsdfsd
    if (dirty_flag) return true;
    
    //Has the TileSet changed
    if(masterTileSet.isDirty()) {
       return true;
    }
    
    //Has the LaunchPanel changed
    if(launchPanel.isDirty()) {
       return true;
    }
    
    //Just return false
    return false;
  }
  
  /**
   * Set the Node on this Component
   * @param pElement Node that contains this components settings
   * @param pRelativeURL the URL that all HREFs would be relative to
   */
  public void setBLXElement(BLXElement blxElement) {
    super.setBLXElement(blxElement);
    Element _myElement = blxElement.getDataElement();
    
    //MasterTile Element not found set Defaults
    if(_myElement == null) {
      setGridLocation(TOP_LEFT);
      setClosed(false);
      setIsDirty(true);
      return;
    }
    
    //Embedded Components
    //Only a TileSet and a Launch Panel can be embedded into the Master Tile
    //All other BLX Components will be ignored.
    boolean successful = true;
    int count = 0;
    NodeList _nodes = _myElement.getChildNodes();
    for(int i=0;i<_nodes.getLength();i++) {
      
      //Only process Elements
      if(_nodes.item(i).getNodeType()!=Node.ELEMENT_NODE) continue;
      
      Element _element = (Element)_nodes.item(i);
      //BLX Components
      try {
        
        Component _comp = componentFactory.getComponent(new BLXElement(_element, blxElement.getContextURL()), null);
        
        //Master TileSet
        if(_comp instanceof TileSet) {
          masterTileSet = (TileSet)_comp;
          count++;
        }
        
        //Launch Panel
        if(_comp instanceof LaunchPanel) {
          //Remove current LaunchPanel
          launchPanel.removeComponentListener(this);
          remove(launchPanel);
          _comp.setLocation(launchPanel.getLocation());
          launchPanel = (LaunchPanel)_comp;
          launchPanel.addComponentListener(this);
          launchPanel.addLaunchListener(this);
          add(launchPanel);
          count++;
        }
        
        //If we loaded both the Launch panel and the TitleSet then we can Break.
        if(count == 2) break;
      }
      catch(Exception _exp) {
        //Could be ClassNotFound or InstantiationException
        //Ignore error the Component will just be skipped.
         successful = false;
        _exp.printStackTrace();
      }
    }//End For Loop
    
    //Setup MasterTile Specific Stuff
    //Location Attribute
    setGridLocation(convertGridLocFromString(_myElement.getAttribute(LOCATION_ATTR)));
    
    //Closed Attribute
    if(_myElement.getAttribute(CLOSED_ATTR).equals("true")) 
       setClosed(true);
    else 
       setClosed(false);
    
    if(successful) setIsDirty(false);
  }
  
  /**
   * Set the xmlChanged Flag
   * Only sub classes can call this
   * @param Changed
   */
  public void setIsDirty(boolean pChanged) {
    super.setIsDirty(pChanged);
    
    //Set MasterTileSet dirty if required
    if(masterTileSet.isDirty()) masterTileSet.setIsDirty(pChanged);
    
    //Set LaunchPanel dirty if required
    if(launchPanel.isDirty()) launchPanel.setIsDirty(pChanged);
    
  }
  
  /**
   * Get the XML Data Element for the Master Tile
   * @return Element
   */
  public Element getDataElement() {
    
    Document _doc = createDOMDocument();
    Element _element = _doc.createElement(ELEMENT_NAME);
    
    //Closed
    if(isClosed()) {
      _element.setAttribute(CLOSED_ATTR, "true");
    }
    else {
      _element.setAttribute(CLOSED_ATTR, "false");
    }
    
    //Location
    _element.setAttribute(LOCATION_ATTR, convertGridLocToString(this.locGrid));
    
    //TileSet
    Element tsElement = masterTileSet.getBLXElement().getDOMElement();
    tsElement = (Element)appendChild(_doc, tsElement, masterTileSet.getDataElement());
    _element = (Element)appendChild(_doc, _element, tsElement);
    
    //LaunchPanel
    Element lpElement = launchPanel.getBLXElement().getDOMElement();
    lpElement.setAttribute(BLXElement.BLX_COMP_WIDTH_ATTR, ""+launchPanel.getWidth());
    lpElement.setAttribute(BLXElement.BLX_COMP_HEIGHT_ATTR, ""+launchPanel.getHeight());
    lpElement = (Element)appendChild(_doc, lpElement, launchPanel.getDataElement());
    _element = (Element)appendChild(_doc, _element, lpElement);
    
    return _element;
  }
     
  /**
   * Launch Panel as finished launching an Action
   * @param action that has finished
   */
  /*
  public void launchCompleted(ICmdAction action) {
    
    launchInProgress_flag = false;
    
    //Repaint the Tile
    invalidate();
    repaint();
  }
   */
  
  /**
   * Launch Panel as triggered an ICmdAction
   * @param action that has been Launched
   */
  /*
  public void launchStarted(ICmdAction action) {
    
    launchInProgress_flag = true;
    
    //Repaint the Tile
    invalidate();
    repaint();
  }
   */
  
  /**
   * Class used to process Mouse Events on the Master Tile
   */
  private class MyMouseAdapter extends MouseAdapter {
    
    /**
     * Process the Mouse Clicked Event
     */
    public void mouseClicked(MouseEvent pEvent){
      boolean side_clicked = false;
      boolean min_clicked = false;
      Point _loc = pEvent.getPoint();
      Dimension _size = getSize();
      
      //check for side clicked
      if((locGrid == TOP_LEFT || locGrid == BOTTOM_LEFT) && (_loc.x<SIDE_WIDTH))
        side_clicked = true;
      else if((locGrid == TOP_RIGHT || locGrid == BOTTOM_RIGHT) && (_loc.x>(_size.width - SIDE_WIDTH)))
        side_clicked = true;
      
      //check for min clicked
      if((locGrid == TOP_RIGHT || locGrid == BOTTOM_RIGHT) && (_loc.x < 48 && (_loc.x > (48 - 15)) && _loc.y < 15))
        min_clicked = true;
      else if((locGrid == TOP_LEFT || locGrid == BOTTOM_LEFT) && (_loc.x > (_size.width - 15)) && _loc.y < 15)
        min_clicked = true;
      
      //Close or unClose
      if(side_clicked) {
        if(isClosed()) setClosed(false);
        else setClosed(true);
      }
      else if(min_clicked) {
        DCComponent.getDesktopFrame().setState(Frame.ICONIFIED);
      }
    }
    
    /**
     * Process the Mouse Pressed Event
     */
    public void mousePressed(MouseEvent pEvent) {
      if(SwingUtilities.isRightMouseButton(pEvent)) {
        if(menu == null) initMenus();
        
        menu.showPopup(pEvent.getPoint(), pEvent.getComponent());
      }
      else {
        //popupMenu.setVisible(false);
      }
    }
  }
  
}
