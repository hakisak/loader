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
import java.io.*;
import javax.swing.*;

import org.w3c.dom.*;

import org.xito.dialog.*;
import org.xito.dcf.*;
import org.xito.blx.*;
import org.xito.dcf.dnd.*;
import org.xito.dcf.property.*;

/**
 *
 * @author  $Author: drichan $
 * @version $Revision: 1.5 $
 * @since   $Date: 2006/04/08 04:47:33 $
 */
public class TileSet extends DCComponent implements ContainerListener, ActionListener {
   
   public static final String NODE_NAME = "tileset";
   public static final String NODE_ORIENTATION_ATTR = "orientation";
   public static final String NODE_SHOW_TILE_ATTR = "showtile";
   public static final String NODE_CLOSABLE_ATTR = "closeable";
   public static final String NODE_CLOSED_ATTR = "closed";
   public static final String NODE_NAME_ATTR = "name";
   public static final String NODE_DESCRIPTION_ATTR = "description";
   
   public static final int NORTH = 0;
   public static final int EAST = 1;
   public static final int SOUTH = 2;
   public static final int WEST = 3;
   
   protected boolean closed_flag = false;
   protected boolean showTileIcon_flag = false;
   protected boolean closeable_flag = true;
   protected int orientation = EAST;
   protected static final int SIDE_WIDTH = 9;
   
   public final static String OPEN_LEFT_IMG = "org/xito/launcher/images/tileset_left.gif";
   public final static String OPEN_RIGHT_IMG = "org/xito/launcher/images/tileset_right.gif";
   public final static String OPEN_UP_IMG = "org/xito/launcher/images/tileset_up.gif";
   public final static String OPEN_DOWN_IMG = "org/xito/launcher/images/tileset_down.gif";
   public final static String TILESET_ICON_IMG = "org/xito/launcher/images/xito32.gif";
   public final static String NO_TILE_IMG = "org/xito/launcher/images/tileset_no_tile.gif";
   
   protected ImageIcon openLeftImage;
   protected ImageIcon openRightImage;
   protected ImageIcon openUpImage;
   protected ImageIcon openDownImage;
   protected ImageIcon tileSetIconImage;
   protected ImageIcon noTileImage;
   
   protected DCTile myTile;
   protected TilePanel tilePanel;
   protected BLXCollection compCollection = new BLXCollection(BLXCollection.LIST);
   protected DCDropTarget dropTarget;
   
   protected BLXCompFactory componentFactory;
   protected DCPopupMenu popupMenu;
   protected JMenuItem propertiesMI;
   protected JMenuItem deleteMI;
   
   /** Create a TileSet
    */
   public TileSet() {
      super();
      init();
   }
   
   private void init() {
      
      //Get Services
      componentFactory = BLXCompFactory.getInstance();
      
      //Get Images
      openLeftImage = new ImageIcon(getClass().getResource(OPEN_LEFT_IMG));
      openRightImage = new ImageIcon(getClass().getResource(OPEN_RIGHT_IMG));
      openUpImage = new ImageIcon(getClass().getResource(OPEN_UP_IMG));
      openDownImage = new ImageIcon(getClass().getResource(OPEN_DOWN_IMG));
      tileSetIconImage = new ImageIcon(getClass().getResource(TILESET_ICON_IMG));
      noTileImage = new ImageIcon(getClass().getResource(NO_TILE_IMG));
      
      //Setup Tile
      setLayout(null);
      myTile = new DCTile();
      myTile.setLocation(0,0);
      myTile.setIcon(tileSetIconImage);
      
      //Setup Tile Panel
      tilePanel = new TilePanel();
      tilePanel.setBackground(new Color(0xf0e0c1));
      tilePanel.addContainerListener(this);
      add(tilePanel);
      setDraggable(true);
      setOrientation(EAST);
      setClosed(true);
      setShowTileSetIcon(true);
      setVisible(false);
      updateSize();
      
      //Drop Support
      dropTarget = new DCDropTarget(this, new MyDropTargetListener());
      
      //Popup Menu
      initMenus();
      
      //Mouse Support
      addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent pEvent){
            int x = pEvent.getX();
            int y = pEvent.getY();
            
            if(orientation == WEST && x < SIDE_WIDTH) {
               if(isClosed()) setClosed(false);
               else setClosed(true);
            }
            else if(orientation == EAST && x > getWidth() - SIDE_WIDTH) {
               if(isClosed()) setClosed(false);
               else setClosed(true);
            }
            else if(orientation == SOUTH && y > getSize().height - SIDE_WIDTH) {
               if(isClosed()) setClosed(false);
               else setClosed(true);
            }
            else if(orientation == NORTH && y < SIDE_WIDTH) {
               if(isClosed()) setClosed(false);
               else setClosed(true);
            }
         }
         public void mousePressed(MouseEvent pEvent) {
            if(SwingUtilities.isRightMouseButton(pEvent)) {
               popupMenu.show(pEvent.getComponent(), pEvent.getX(), pEvent.getY());
            }
         }
      });
      
   }
   
   /**
    * Create the Menus for the master tile
    */
   private void initMenus() {
      //Setup Menu
      popupMenu = new DCPopupMenu();
      propertiesMI = new JMenuItem("Properties");
      propertiesMI.addActionListener(this);
      deleteMI = new JMenuItem("Delete");
      deleteMI.addActionListener(this);
      popupMenu.add(propertiesMI);
      popupMenu.add(deleteMI);
   }
   
   /**
    * Actions for Menu
    */
   public void actionPerformed(ActionEvent evt) {
      
      //Properties
      if(evt.getSource() == propertiesMI) {
         PropertySheetDialog _dialog = new PropertySheetDialog(this, "TileSet Properties");
         _dialog.show();
      }
      
      //Delete
      else if(evt.getSource() == deleteMI) {
         //need prompt here
         dispose();
      }
   }
   
   /**
    * Set the TileSet to be Closable
    * If true then the TileSet will have a control that
    * The user can use to close and Open the TileSet
    * @param boolean true to Close/Open the TileSet false if the Set is always Open
    */
   public void setClosable(boolean c) {
      if(closeable_flag == c) return;
      //Make it Open
      if(c == false) {
         setClosed(false);
      }
      setIsDirty(true);
      closeable_flag = c;
      updateSize();
      
      invalidate();
      repaint();
   }
   
   /**
    * Returns true if the TileSet can be closed false otherwise
    * @return boolean
    */
   public boolean isClosable() {
      return closeable_flag;
   }
   
   /**
    * Set the Name of the TileSet
    */
   public void setName(String title) {
      
      if(myTile.getShortTitle() != null && myTile.getShortTitle().equals(title)) return;
      
      setIsDirty(true);
      myTile.setShortTitle(title);
      invalidate();
      repaint();
   }
   
   /**
    * Set the Description of the TileSet
    */
   public void setDescription(String desc) {
      
      if(myTile.getDescription()!=null && myTile.getDescription().equals(desc)) return;
      
      setIsDirty(true);
      myTile.setDescription(desc);
   }
   
   /**
    * Get the Description
    */
   public String getDescription() {
      return myTile.getDescription();
   }
   
   /**
    * Get the Name of the Tile Set
    */
   public String getName() {
      return myTile.getShortTitle();
   }
   
   public void setShowTileSetIcon(boolean pShow) {
      
      myTile.setVisible(pShow);
      if(showTileIcon_flag == pShow) return;
      
      showTileIcon_flag = pShow;
      
      setIsDirty(true);
      updateSize();
   }
   
   public boolean getShowTileSetIcon() {
      return showTileIcon_flag;
   }
   
   /**
    * Get the Preferred Size of the TileSet
    * @return Dimension
    */
   public Dimension getPreferredSize() {
      return getSize();
   }
   
   /**
    * Update the Size of the TileSet
    */
   private void updateSize() {
      
      int _sideWidth = SIDE_WIDTH;
      if(closeable_flag == false) _sideWidth = 0;
      
      int th = 48;
      int tw = 48;
      
      //Closed
      if(isClosed()) {
         //If we are suppose to Show the Tile
         if(showTileIcon_flag) {
            if(orientation == SOUTH || orientation == NORTH) setSize(tw, th + _sideWidth);
            else setSize(tw + _sideWidth, th);
         }
         else {
            if(orientation == SOUTH || orientation == NORTH) setSize(tw, _sideWidth);
            else setSize(_sideWidth, th);
         }
      }
      //Open
      else {
         Dimension _dim = tilePanel.getPreferredSize();
         tilePanel.setSize(_dim);
         
         //SOUTH or NORTH
         if(orientation == SOUTH || orientation == NORTH) {
            if(showTileIcon_flag) {
               setSize(tw, th + _sideWidth + _dim.height);
               if(orientation == SOUTH) tilePanel.setLocation(0, th);
               else tilePanel.setLocation(0,_sideWidth);
            }
            else {
               setSize(tw, _sideWidth + _dim.height);
               if(orientation == SOUTH) tilePanel.setLocation(0, 0);
               else tilePanel.setLocation(0,_sideWidth);
            }
         }
         //WEST or EAST
         else {
            if(showTileIcon_flag) {
               setSize(tw + _sideWidth + _dim.width, th);
               if(orientation == EAST) tilePanel.setLocation(tw, 0);
               else tilePanel.setLocation(_sideWidth,0);
            }
            else {
               setSize(_sideWidth + _dim.width, th);
               if(orientation == EAST) tilePanel.setLocation(0, 0);
               else tilePanel.setLocation(_sideWidth,0);
            }
         }
      }
      
      invalidate();
      repaint();
   }
   
   /**
    * Set the Orientation
    * @param pOrientation where NORTH = Up, SOUTH = Down, EAST = Opens to Right, WEST = Open to Left
    */
   public void setOrientation(int pOrientation) {
      if(this.orientation == pOrientation) return;
      
      setIsDirty(true);
      orientation = pOrientation;
      tilePanel.rebuild();
   }
   
   /**
    * Get the Orientation of the TileSet
    * NORTH, SOUTH, EAST, WEST
    */
   public int getOrientation() {
      return orientation;
   }
   
   /**
    * Get the Orientation of the TileSet as a String
    * NORTH_STR, SOUTH_STR, EAST_STR, WEST_STR
    */
   protected String getOrientationAsString() {
      
      switch(orientation) {
         case NORTH: return BorderLayout.NORTH;
         case SOUTH: return BorderLayout.SOUTH;
         case WEST:  return BorderLayout.WEST;
         case EAST:  return BorderLayout.EAST;
      }
      
      return BorderLayout.EAST;
   }
   
   /**
    * Get the Orientation of the TileSet as a String
    * NORTH_STR, SOUTH_STR, EAST_STR, WEST_STR
    */
   protected void setOrientationAsString(String pStr) {
      
      if(pStr.equals(BorderLayout.NORTH)) setOrientation(NORTH);
      else if(pStr.equals(BorderLayout.SOUTH)) setOrientation(SOUTH);
      else if(pStr.equals(BorderLayout.WEST)) setOrientation(WEST);
      else setOrientation(EAST);
   }
   
   /** Is the TileSet Closed
    */
   public boolean isClosed() {
      return closed_flag;
   }
   
   /** Close or Open the TileSet
    */
   public void setClosed(boolean pClosed) {
      
      //Do nothing if we can
      if(closed_flag == pClosed) return;
      
      boolean _wasVisible = this.isVisible();
      if((orientation == WEST || orientation == EAST) && _wasVisible) setVisible(false);
      
      //Update Status
      closed_flag = pClosed;
      Dimension _oldSize = getSize();
      updateSize();
      Dimension _newSize = getSize();
      
      //Hide or Show the TilePanel
      if(pClosed) tilePanel.setVisible(false);
      else tilePanel.setVisible(true);
      
      //Set new Location for Horizontal
      if((orientation == WEST || orientation == NORTH)) {
         if(orientation == WEST){
            Point _loc = this.getLocation();
            _loc.translate(_oldSize.width - _newSize.width, 0);
            setLocation(_loc);
         }
         if(orientation == NORTH){
            Point _loc = this.getLocation();
            _loc.translate(0, _oldSize.height - _newSize.height);
            setLocation(_loc);
         }
      }
      
      if(_wasVisible) setVisible(true);
      
      setIsDirty(true);
      invalidate();
      repaint();
   }
   
   /** Paint
    */
   public void paintComponent(Graphics pGraphics) {
      super.paintComponent(pGraphics);
      final JPanel tPanel = new JPanel();
      Dimension size = getSize();
      //VERTICAL NORTH or SOUTH
      if((orientation == NORTH || orientation == SOUTH)) {
         //CLOSED
         if(closed_flag) {
            //SOUTH
            if(orientation == SOUTH){
               pGraphics.drawImage(openDownImage.getImage(), 0, size.height-SIDE_WIDTH, null);
               if(showTileIcon_flag) {
                  SwingUtilities.paintComponent(pGraphics, myTile, tPanel, 0, 0, 48, 48);
               }
            }
            //NORTH
            else{
               pGraphics.drawImage(openUpImage.getImage(), 0, 0, null);
               if(showTileIcon_flag) {
                  SwingUtilities.paintComponent(pGraphics, myTile, tPanel, 0, SIDE_WIDTH, 48, 48);
               }
            }
         }
         //OPEN
         else {
            //SOUTH
            if(orientation == SOUTH){
               pGraphics.drawImage(openUpImage.getImage(), 0, size.height-SIDE_WIDTH, null);
               if(showTileIcon_flag) {
                  SwingUtilities.paintComponent(pGraphics, myTile, tPanel, 0, 0, 48, 48);
               }
            }
            //NORTH
            else{
               pGraphics.drawImage(openDownImage.getImage(), 0, 0, null);
               if(showTileIcon_flag) {
                  SwingUtilities.paintComponent(pGraphics, myTile, tPanel, 0, size.height - 48, 48, 48);
               }
            }
         }
      }
      //HORIZONTAL WEST or EAST
      else if(orientation == WEST || orientation == EAST) {
         //CLOSED
         if(closed_flag) {
            //WEST
            if(orientation == WEST){
               pGraphics.drawImage(openRightImage.getImage(), 0, 0, null);
               if(showTileIcon_flag) {
                  SwingUtilities.paintComponent(pGraphics, myTile, tPanel, SIDE_WIDTH, 0, 48, 48);
               }
            }
            //EAST
            else{
               pGraphics.drawImage(openLeftImage.getImage(), size.width-SIDE_WIDTH, 0, null);
               if(showTileIcon_flag) {
                  SwingUtilities.paintComponent(pGraphics, myTile, tPanel, 0, 0, 48, 48);
               }
            }
         }
         //OPEN
         else {
            //WEST
            if(orientation == WEST){
               pGraphics.drawImage(openLeftImage.getImage(), 0, 0, null);
               if(showTileIcon_flag) {
                  SwingUtilities.paintComponent(pGraphics, myTile, tPanel, size.width - 48, 0, 48, 48);
               }
            }
            //EAST
            else{
               pGraphics.drawImage(openRightImage.getImage(), size.width-SIDE_WIDTH, 0, null);
               if(showTileIcon_flag) {
                  SwingUtilities.paintComponent(pGraphics, myTile, tPanel, 0, 0, 48, 48);
               }
            }
         }
      }
   }
   
   /**
    * Add a Tile to this TileSet
    * @param pTile to Add
    * @param pIndex where to add it in the List
    */
   public void addDockedTile(DCTile pTile, int pIndex) {
      
      //See if we contain the Tile already
      if(compCollection.contains(pTile)) {
         int _oldIndex = compCollection.getManagedObjectIndex(pTile);
         compCollection.removeManagedObject(pTile);
         tilePanel.remove(pTile);
         if(pIndex > _oldIndex) pIndex--;
      }
      
      setIsDirty(true);
      compCollection.addManagedObject(pIndex, pTile);
      pTile.setOnDesktop(false);
      pTile.setShowTitle(false);
      pTile.setVisible(true);
      tilePanel.add(pTile);
      tilePanel.rebuild();
   }
   
   /**
    * Remove a Tile from this TileSet
    * @param pTile to remove
    */
   public void removeDockedTile(DCTile pTile) {
      if(compCollection.contains(pTile)) {
         setIsDirty(true);
         compCollection.removeManagedObject(pTile);
         tilePanel.remove(pTile);
         tilePanel.rebuild();
      }
   }
   
   /**
    * Set the Node on this Component
    * @param pElement Node that contains this components settings
    * @param pRelativeURL the URL that all HREFs would be relative to
    */
   public void setBLXElement(BLXElement blxElement) {
      super.setBLXElement(blxElement);
      
      //Walk through All Children and look for tileset element
      Element _tileSetElement = blxElement.getDataElement();
      
      //Guess we have no Data
      if(_tileSetElement == null) return;
      
      //Set attributes on TileSet
      setName(_tileSetElement.getAttribute(NODE_NAME_ATTR));
      setDescription(_tileSetElement.getAttribute(NODE_DESCRIPTION_ATTR));
      String s = _tileSetElement.getAttribute(NODE_CLOSABLE_ATTR);
      setClosable((s != null && s.equals("true"))?true:false);
      s = _tileSetElement.getAttribute(NODE_CLOSED_ATTR);
      setClosed((s != null && s.equals("true"))?true:false);
      s = _tileSetElement.getAttribute(NODE_SHOW_TILE_ATTR);
      setShowTileSetIcon((s != null && s.equals("true"))?true:false);
      setOrientationAsString(_tileSetElement.getAttribute(NODE_ORIENTATION_ATTR));
      
      //Now look for Container Element
      Element _containerElement = null;
      NodeList _list = _tileSetElement.getChildNodes();
      for(int i=0;i<_list.getLength();i++) {
         
         Node _node = _list.item(i);
         //Only Process Elements
         if(_node.getNodeType() != Node.ELEMENT_NODE) continue;
         //Check for Container Element
         if(_node.getNodeName().equals(compCollection.CONTAINER_NODE_NAME)) {
            _containerElement = (Element)_node;
         }
      }
      //Contains no Tiles
      if(_containerElement == null) return;
      
      //Load the Container
      boolean successful = compCollection.setXMLElement(_containerElement, relativeURL);
      
      if(successful) {
         //Place all Tiles into the TileSet
         BLXObject _objs[] = compCollection.getManagedObjects();
         for(int i=0;i<_objs.length;i++) {
            try {
               DCTile _tile = (DCTile)_objs[i];
               _tile.setOnDesktop(false);
               _tile.setVisible(true);
               tilePanel.add(_tile);
            }
            catch(ClassCastException _exp) {
               System.err.println("Invalid object in TileSet:"+_objs.getClass());
               compCollection.removeManagedObject(_objs[i]);
            }
         }
         tilePanel.rebuild();
         setIsDirty(false);
      }
      else {
         setIsDirty(true);
      }
   }
   
   /**
    * This is the Data of the TileSet.
    */
   public Element getDataElement() {
      
      Document _doc = super.createDOMDocument();
      Element _element = _doc.createElement(NODE_NAME);
      
      //Get the Containers XML
      _element.appendChild(_doc.importNode(compCollection.getXMLElement(null), true));
      
      //Setup TileSet Attributes
      String name = getName();
      if(name != null)
         _element.setAttribute(NODE_NAME_ATTR, name);
      
      _element.setAttribute(NODE_DESCRIPTION_ATTR, getDescription());
      _element.setAttribute(NODE_CLOSABLE_ATTR, (isClosable()?"true":"false"));
      _element.setAttribute(NODE_CLOSED_ATTR, (isClosed()?"true":"false"));
      _element.setAttribute(NODE_SHOW_TILE_ATTR, (getShowTileSetIcon()?"true":"false"));
      _element.setAttribute(NODE_ORIENTATION_ATTR, getOrientationAsString());
      
      return _element;
   }
   
   /**
    * Invoked when a component has been added to the compCollection.
    */
   public void componentAdded(ContainerEvent e) {
      //Not Used right now
   }
   
   /**
    * Invoked when a component has been removed from the compCollection.
    */
   public void componentRemoved(ContainerEvent e) {
      
      if((e.getContainer() == tilePanel) && (e.getChild() instanceof DCTile)) {
         removeDockedTile((DCTile)e.getChild());
      }
   }
   
   /**
    * Panel that holds docked Tiles
    */
   class TilePanel extends JPanel {
      public TilePanel() {
         super(null);
         setVisible(true);
      }
      
      public Dimension getPreferredSize() {
         Dimension _size = null;
         
         int _count = compCollection.size();
         if(_count == 0) _count = 1;
         
         if(orientation == NORTH || orientation == SOUTH) {
            _size =  new Dimension(DCTile.DEFAULT_TILE_SIZE.width, DCTile.DEFAULT_TILE_SIZE.height * _count);
         }
         else {
            _size = new Dimension(DCTile.DEFAULT_TILE_SIZE.width * _count, DCTile.DEFAULT_TILE_SIZE.height);
         }
         
         return _size;
      }
      
      /**
       * Rebuild the locations of the Tiles in this Panel
       */
      protected void rebuild() {
         
         for(int i=0;i<compCollection.size();i++) {
            DCTile _tile = (DCTile)compCollection.getManagedObject(i);
            if(orientation == NORTH || orientation == SOUTH) {
               _tile.setLocation(0, DCTile.DEFAULT_TILE_SIZE.height * i);
            }
            else {
               _tile.setLocation(DCTile.DEFAULT_TILE_SIZE.width * i, 0);
            }
         }
         
         this.invalidate();
         this.repaint();
         
         TileSet.this.updateSize();
      }
      
      public void paintComponent(Graphics g) {
         g.drawImage(noTileImage.getImage(), 0, 0, null);
      }
   }
   
   /***************************************
    * Drop Target Listener
    ***************************************/
   class MyDropTargetListener implements DropTargetListener {
      
      /**
       * The drag operation has departed
       * the <code>DropTarget</code> without dropping.
       * <P>
       * @param dte the <code>DropTargetEvent</code>
       */
      public void dragExit(DropTargetEvent dte) {
      }
      
      /**
       * Called when a drag operation has
       * encountered the <code>DropTarget</code>.
       * <P>
       * @param dtde the <code>DropTargetDragEvent</code>
       */
      public void dragEnter(DropTargetDragEvent dtde) {
      }
      
      /**
       * The drag operation has terminated
       * with a drop on this <code>DropTarget</code>.
       * This method is responsible for undertaking
       * the transfer of the data associated with the
       * gesture. The <code>DropTargetDropEvent</code>
       * provides a means to obtain a <code>Transferable</code>
       * object that represents the data object(s) to
       * be transfered.<P>
       * From this method, the <code>DropTargetListener</code>
       * shall accept or reject the drop via the
       * acceptDrop(int dropAction) or rejectDrop() methods of the
       * <code>DropTargetDropEvent</code> parameter.
       * <P>
       * Subsequent to acceptDrop(), but not before,
       * <code>DropTargetDropEvent</code>'s getTransferable()
       * method may be invoked, and data transfer may be
       * performed via the returned <code>Transferable</code>'s
       * getTransferData() method.
       * <P>
       * At the completion of a drop, an implementation
       * of this method is required to signal the success/failure
       * of the drop by passing an appropriate
       * <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
       * dropComplete(boolean success) method.
       * <P>
       * Note: The actual processing of the data transfer is not
       * required to finish before this method returns. It may be
       * deferred until later.
       * <P>
       * @param dtde the <code>DropTargetDropEvent</code>
       */
      public void drop(DropTargetDropEvent dtde) {
         
         //If this supports Tile REF DataFlavor
         if(dtde.isDataFlavorSupported(DCTile.TILE_REF_FLAVOR)==false) {
            dtde.dropComplete(false);
            String title = DesktopService.resources.getString("drop.error.title");
            String msg = DesktopService.resources.getString("drop.dataflavor.notsupported");
            DialogManager.showError(null, title, msg, null);
            return;
         }
         
         //Get the Tile
         try {
            DCTile tile = (DCTile)dtde.getTransferable().getTransferData(DCTile.TILE_REF_FLAVOR);
            TileSet.this.addDockedTile(tile, 0);
            
            //Drop is Complete
            dtde.dropComplete(true);
         }
         catch(IOException ioExp) {
            ioExp.printStackTrace();
            dtde.dropComplete(false);
            String title = DesktopService.resources.getString("drop.error.title");
            String msg = DesktopService.resources.getString("drop.ioexpception");
            DialogManager.showError(null, title, msg, ioExp);
         }
         catch(UnsupportedFlavorException exp) {
            exp.printStackTrace();
            dtde.dropComplete(false);
            String title = DesktopService.resources.getString("drop.error.title");
            String msg = DesktopService.resources.getString("drop.dataflavor.notsupported");
            DialogManager.showError(null, title, msg, null);
         }
         
      }
      
      /**
       * Called when a drag operation is ongoing
       * on the <code>DropTarget</code>.
       * <P>
       * @param dtde the <code>DropTargetDragEvent</code>
       */
      public void dragOver(DropTargetDragEvent dtde) {
         //if(dtde.getDropTargetContext().isDataFlavorSupported()) {
      }
      
      /**
       * Called if the user has modified
       * the current drop gesture.
       * <P>
       * @param dtde the <code>DropTargetDragEvent</code>
       */
      public void dropActionChanged(DropTargetDragEvent dtde) {
      }
   }
}
