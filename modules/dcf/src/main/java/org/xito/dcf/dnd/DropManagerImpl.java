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

package org.xito.dcf.dnd;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.lang.ref.*;
import java.util.*;
import java.io.*;

import org.xito.dcf.*;

/**
 * << License Info Goes HERE >> All Rights Reserved.
 * DragListenerAdapter
 * Description:
 *
 * @author  $Author: drichan $
 * @version $Revision: 1.2 $
 * @since   $Date: 2007/11/28 03:52:39 $
 */
public class DropManagerImpl implements DropManager, AWTEventListener {
  private static DropManagerImpl singleton = new DropManagerImpl();
  private ArrayList windows = new ArrayList();
  
  //The Drop Manager uses an internal Desktop Target it non is set
  private DropTarget defaultDropTarget = new DropTarget(null, new MyDesktopDropTargetListener());
  
  //Static because you can only be in one DnD operation at a time.
  
  /** Component that started the Drag Operation */
  static Component currentDragComponent;
  
  /** Component that the Dnd Operation Mouse Point is Currently Over */
  static Component currentlyOverComp;
  
  /** Window containing the current Drag Image */
  static DragWindow currentDragWindow;
  
  /** Image to show during Dnd Operation */
  static Image currentDragImage;
  
  /** Location where the Dnd Operation originated */
  static Point currentStartLoc;
  
  /** Current Location of the mouse durring a Dnd Operation */
  static Point currentDragPoint;
  
  /** OffSet to show DragWindow with Image from the Mouse Location during DnD Operation */
  static Point currentDragOffSet;
  
  /** The current Drop Target that the mouse is Over or null if not over a drop Target */
  static DropTarget currentDropTarget;
  
  /** The Drag Gesture Event that initiated the current operation */
  static DragGestureEvent currentGestureEvent;
  
  /** Current Drag Source Object */
  static DragSource currentDragSource;
  
  /** Current DragSource Context */
  static DragSourceContext currentDragContext;
  
  /** Current DropTarget Context */
  static DropTargetContext currentDropContext;
  
  /** current Transferable object for the Operation */
  static Transferable currentTransferable;
  
  boolean dragStarted = false;
  
  DragMouseMotionAdapter motionAdapter = new DragMouseMotionAdapter();
  DragMouseAdapter mouseAdapter = new DragMouseAdapter();
  
  /**
   * Get the Drop Manager Instance
   */
  public static DropManager getDropManager() {
    return singleton;
  }
  
  /**
   * Add the DropManager as an AWT Listener
   */
  private DropManagerImpl() {
    Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.WINDOW_EVENT_MASK);
  }
  
  /**
   * A DnD Session has been started, using a DragImage OffSet from the Cursor by pImageOffset
   * @param pContext of the Drag Source
   * @param pDragImage Image to display during DnD operation
   * @param pImageOffset offSet of image location from Cursor or null to use Drag Component's Location
   */
  public void dragStarted(DragSourceContext pContext, Image pDragImage, Point pOffSet) {
    
    //Setup Drag Components
    currentDragComponent = pContext.getComponent();
    if(currentDragWindow == null) currentDragWindow = new DragWindow();
    
    currentGestureEvent = pContext.getTrigger();
    currentDragPoint = currentGestureEvent.getDragOrigin();
    currentDragOffSet = pOffSet;
    
    currentDragImage = pDragImage;
    currentDragWindow.setSize(currentDragImage.getWidth(null), currentDragImage.getHeight(null));
    currentDragWindow.setDragImage(currentDragImage);
    
    //Don't use OffSet
    if(currentDragOffSet == null || (currentDragOffSet.x == 0 && currentDragOffSet.y == 0)) {
      currentStartLoc = currentDragPoint.getLocation();
    }
    //If currentDragOffSet Specifed then apply it
    else {
      currentStartLoc = currentDragPoint.getLocation();
      currentStartLoc.translate(currentDragOffSet.x, currentDragOffSet.y);
    }
        
    currentDragContext = pContext;
    currentDragSource = pContext.getDragSource();
    currentTransferable = pContext.getTransferable();
    
    Point _dragLoc = currentStartLoc.getLocation();
    SwingUtilities.convertPointToScreen(_dragLoc, currentDragComponent);
        
    currentDragWindow.setLocation(_dragLoc);
    
    //Hide the window for now
    currentDragWindow.setVisible(false);
        
    //Add Listeners
    currentDragComponent.addMouseListener(mouseAdapter);
    currentDragComponent.addMouseMotionListener(motionAdapter);
    
    //notify component that the drag has started
    if(currentDragComponent instanceof DCComponent) {
       ((DCComponent)currentDragComponent).dragStarted();
    }
  }
  
  /**
   * The Dragging Operation has stopped
   * Try and do a Drop if located over a DropTarget
   * @param MouseEvent where the mouse was released
   */
  public void dragStopped(MouseEvent pEvent) {
    
    dragStarted = false;
    
    //remove Listeners
    currentDragComponent.removeMouseListener(mouseAdapter);
    currentDragComponent.removeMouseMotionListener(motionAdapter);
    
    //Hide Drag Window
    currentDragWindow.setVisible(false);
    
    //Convert Point to context of DragSource Component
    Point _point = pEvent.getPoint();
    SwingUtilities.convertPointToScreen(_point, pEvent.getComponent());
        
    //Get a DropTarget at the current location
    currentDropTarget = getDropTarget(_point);
    
    //If we were just dropped over ourselves and we are in a move action then just do a Component Move
    int _action = currentDragContext.getSourceActions();
    if((_action & DnDConstants.ACTION_MOVE)>0 
        && currentlyOverComp != null 
        && SwingUtilities.isDescendingFrom(currentlyOverComp, currentDragComponent)) {

      int xdif = pEvent.getPoint().x - currentDragPoint.x;
      int ydif = pEvent.getPoint().y - currentDragPoint.y;

      Point loc = currentDragComponent.getLocation();
      loc.translate(xdif, ydif);
      currentDragComponent.setLocation(loc);
      clearCurrentOperation();
    }
    //Found something to Drop on
    else if(currentDropTarget != null) {
      
      //Get the Component that we are dropping on
      Component _dropComponent = currentDropTarget.getComponent();
      //Convert point to context of Component
      //_point = currentDragWindow.getLocation();
      if(_dropComponent != null) {
        SwingUtilities.convertPointFromScreen(_point, _dropComponent);
      }

      //Create the DropEvent
      DropTargetDropEvent _dropEvent = null;
      _dropEvent = new DCDropTargetDropEvent(currentDragSource, currentDropTarget, _point, 0, 0, true, currentTransferable, currentGestureEvent);
      currentDropTarget.drop(_dropEvent);
      
      clearCurrentOperation();
    }
    //If No Drop Target then just send no-success message to DragSource
    else {
      
      DragSourceDropEvent _dragEvent = new DragSourceDropEvent(currentDragContext, currentDragContext.getSourceActions(), false);
      currentDragContext.dragDropEnd(_dragEvent);
      clearCurrentOperation();
    }
    
    return;
  }

  /** 
   * Clear the Current Dnd Operation Variables
   */
  private synchronized void clearCurrentOperation() {

    //notify component that the drag has stopped
    if(currentDragComponent instanceof DCComponent) {
       ((DCComponent)currentDragComponent).dragStopped();
    }
     
    //Clear current Drag variables
    dragStarted = false;
    currentlyOverComp = null;
    currentDragComponent = null;
    currentGestureEvent = null;
    currentDragImage = null;
    currentDragPoint = null;
    currentDragOffSet = null;
    currentStartLoc = null;
    currentDragSource = null;
    currentDragContext = null;
    currentDropTarget = null;
    currentTransferable = null;
  }
  
  /**
   * Get the Transferable for the current drag operation
   * @return Transferable
   */
  public Transferable getCurrentTransferable() {
    return currentTransferable;
  }
  
  /**
   * Get the DragSourceContext for the current drag operation
   * @return DragSourceContext
   */
  public DragSourceContext getCurrentDragSourceContext() {
    return currentDragContext;
  }
  
  /**
   * Get an Image for a Component
   * @return Image
   */
  public static Image getComponentImage(Component pComp) {
    BufferedImage _image = new BufferedImage(pComp.getWidth(), pComp.getHeight(), BufferedImage.TYPE_INT_ARGB);
    pComp.paintAll(_image.getGraphics());
    return _image;
  }
  
  /**
   * Add a Window that will be checked for targets during Drag Operations
   * @param pWindow window that will be checked
   */
  protected void addWindow(Window pWindow) {
    
    if(windows.contains(pWindow)) return;
    
    //Add the Window
    windows.add(0, pWindow);
  }
  
  protected void removeWindow(Window pWindow) {
    windows.remove(pWindow);
  }
  
  /**
   * Moves the Window to the top of the window list
   * @param pWin
   */
  protected void moveWindowToTop(Window pWin) {
    //Remove from current spot
    removeWindow(pWin);
    //Insert it at the Top
    addWindow(pWin);
  }
  
  /**
   * Sets the default drop target if a drag occurs over any item that is not a Drop
   * Target. This should usually be the desktop of the system.
   * @param default Drop Target
   */
  public void setDefaultDropTarget(DropTarget pDropTarget) {
    defaultDropTarget = pDropTarget;
  }
  
  /**
   *  Return a Drop Component for the component at the Current Location
   *  This method should search down the component hierarchy until it finds a Drop Target
   * @param Location look for the Drop Target in Screen Corrdinates
   */
  public DropTarget getDropTarget(Point pLocation) {
    
    //Convert the point to be relative to the window
    Point _point = pLocation.getLocation();
    
    //Go through the registered windows and get its components
    Iterator _it = windows.iterator();
    while(_it.hasNext()) {
      Window _win = (Window)_it.next();
      if(_win == currentDragWindow) continue;
      
      //If the WIndow has been disposed
      if(_win == null) {
        _it.remove();
        continue;
      }
      
      //Window must be Visible
      if(_win.isVisible()==false) continue;
                        
      //Point must be in visible window area
      Dimension _size = _win.getSize();
      Point _loc = _win.getLocation();
      Rectangle _bounds = new Rectangle(_loc, _size);
      
      if(_bounds.contains(_point)==false) continue;
            
      //At this point we know that the point is contained in the window boundaries
      //and that the window is Visible
            
      //Get the Component at that location
      SwingUtilities.convertPointFromScreen(_point, _win);
      currentlyOverComp = _win.getComponentAt(_point);
      if(currentlyOverComp != null)
        currentlyOverComp = SwingUtilities.getDeepestComponentAt(currentlyOverComp, _point.x, _point.y);
      else
        currentlyOverComp = _win;
                  
      //Get the Drop Target
      if(currentlyOverComp == null) return defaultDropTarget;
            
      DropTarget _dropTarget = currentlyOverComp.getDropTarget();
      if(_dropTarget != null && _dropTarget.getComponent() != null) return _dropTarget;
      
      //Seach up components for Drop Targets
      return getParentDropTarget(currentlyOverComp);
    }
    
    //Clear currect over Comp
    currentlyOverComp = null;
    
    return defaultDropTarget;
  }
  
  /**
   * Gets the parent's drop target
   *
   */
  private DropTarget getParentDropTarget(Component pComp) {
    Component parent = pComp.getParent();
    if(parent == null) return null;
    
    DropTarget dt = parent.getDropTarget();
    if(dt != null) return dt;
    else return getParentDropTarget(parent);
  }
  
  
  /**
   * Invoked when an event is dispatched in the AWT.
   */
  public void eventDispatched(AWTEvent event) {
    //We only should be getting Window Events
    WindowEvent winEvent = (WindowEvent)event;
    
    //Window Closed
    if(winEvent.getID() == WindowEvent.WINDOW_CLOSED) {
      //remove window
      removeWindow(winEvent.getWindow());
    }    
    //Window Activated move to Top of List
    else if(winEvent.getID() == (WindowEvent.WINDOW_ACTIVATED & WindowEvent.WINDOW_OPENED)) {
      if(winEvent.getWindow().getClass() == DragWindow.class) return;
      moveWindowToTop(winEvent.getWindow());
    }
  }
  
  /**********************************************************
   * DragMouseMotionAdapter
   **********************************************************/
  class DragMouseMotionAdapter extends MouseMotionAdapter {
     
    public void mouseDragged(MouseEvent pEvent) {
      //Only One drag can occur at a time
      if(currentDragComponent != pEvent.getComponent() || currentDragComponent==null) return;
      
      //This info is all in the context of the Current Drag Component
      Point _point = pEvent.getPoint();
      Point _newLoc = null;
      if(currentDragOffSet == null) {
        _newLoc = new Point(_point.x - currentDragPoint.x, _point.y - currentDragPoint.y);
      }
      else {
        _newLoc = new Point(_point.x - currentDragPoint.x, _point.y - currentDragPoint.y);
        _newLoc.translate(currentDragOffSet.x, currentDragOffSet.y);
      }
            
      //Update Drag Window Location
      SwingUtilities.convertPointToScreen(_newLoc, currentDragComponent);
      currentDragWindow.setLocation(_newLoc);
      if(dragStarted == false) {
        dragStarted = true;
        currentDragWindow.toFront();
        currentDragWindow.setVisible(true);
      }
      
      //Get Drop Target
      SwingUtilities.convertPointToScreen(_point, currentDragComponent);
      DropTarget _dropTarget = getDropTarget(_point);
      
      //No drop target found just return;
      if(_dropTarget == null) return;
            
      //Convert Point to context of Drop Component
      Component _dropComp = _dropTarget.getComponent();
      //SwingUtilities.convertPointToScreen(_point, currentDragComponent);
      if(_dropComp != null) {
        SwingUtilities.convertPointFromScreen(_point, _dropComp);
      }
      
      //Notify Drop Target and Source
      if((_dropTarget instanceof DCDropTarget) == false) return;
      DropTargetDragEvent _targetDragEvent = null;
      if(currentDropTarget != _dropTarget) {
        //Send Enter to new Drop Target
        currentDropContext = ((DCDropTarget)_dropTarget).createDropTargetContext();
        _targetDragEvent = new DropTargetDragEvent(currentDropContext, _point, DnDConstants.ACTION_MOVE, DnDConstants.ACTION_COPY_OR_MOVE);
        _dropTarget.dragEnter(_targetDragEvent);
                
        //Send Exit to old Drop Target
        if(currentDropTarget != null) {
           currentDropTarget.dragExit(new DropTargetEvent(currentDropContext));
        }
        
        //Update the current target
        currentDropTarget = _dropTarget;
      }
      else {
        _targetDragEvent = new DropTargetDragEvent(currentDropContext, _point, DnDConstants.ACTION_MOVE, DnDConstants.ACTION_COPY_OR_MOVE);
      }
      
      currentDropTarget.dragOver(_targetDragEvent);
    }
  }
  
  /**********************************************************
   * Drag Mouse Adapter
   **********************************************************/
  class DragMouseAdapter extends MouseAdapter {
    public void mouseReleased(MouseEvent pEvent) {
      //Only One drag can occur at a time
      if(currentDragComponent != pEvent.getComponent()) return;
      
      //stopped Dragging
      dragStopped(pEvent);
    }
    
  }
  
  /**********************************************************
   * Default Simple Desktop Drop Target Listener
   **********************************************************/
  static class MyDesktopDropTargetListener implements DropTargetListener {
    
    public void dragOver(DropTargetDragEvent pDragEvent) {
    }
    
    public void dropActionChanged(DropTargetDragEvent pEvent) {
    }
    
    public void dragExit(DropTargetEvent pEvent) {
    }
    
    public void dragEnter(DropTargetDragEvent pEvent) {
    }
    
    public void drop(DropTargetDropEvent pEvent) {
      //Get Location of Drop
      Point _point = pEvent.getLocation();
      
      //Check to see if desktop component is support
      if(DCTransferObject.isDCF_FlavorSupported(pEvent)==false) {
        System.out.println("Data Flavor not Supported");
        //Reject the Drop
        pEvent.dropComplete(false);
        return;
      }
      
      try {
        //Must be Supported
        DCDropTargetDropEvent _event = (DCDropTargetDropEvent)pEvent;
        Point _offset = _event.getTrigger().getDragOrigin();
        
        Transferable _transfer = pEvent.getTransferable();
        DCComponent _comp = (DCComponent)_transfer.getTransferData(DCComponent.DCF_REF_FLAVOR);
        
        if(_comp.isOnDesktop() == false) _comp.setOnDesktop(true);
        
        _comp.setLocation(_point.x - _offset.x, _point.y - _offset.y);
        if(_comp instanceof DCTile) {
          ((DCTile)_comp).setShowTitle(true);
        }
        _comp.setVisible(true);
        
        //Drop is Complete
        pEvent.dropComplete(true);
      }
      catch(UnsupportedFlavorException _exp) {
        _exp.printStackTrace();
      }
      catch(IOException _ioExp) {
        _ioExp.printStackTrace();
      }
    }
    
  }
  
  /**********************************************************
   * Window used to display component during drag operation
   **********************************************************/
  class DragWindow extends JWindow {
    DCImagePanel panel = new DCImagePanel();
    Color c1 = new Color(100,100,100,75);
    Color c2 = new Color(100,100,100,75);
    
    public DragWindow() {
      super();
      setContentPane(panel);
    }
    
    public void setSize(Dimension pDim) {
      super.setSize(pDim);
      panel.setFilterPaint(new GradientPaint(new Point(0,0), c1, new Point(pDim.width, pDim.height), c2));
    }
    
    public void setDragImage(Image pImage) {
      panel.setImage(pImage);
    }
    
  }
  
}
