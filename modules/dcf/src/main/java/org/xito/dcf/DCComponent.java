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

package org.xito.dcf;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.dnd.*;
import java.util.*;
import java.net.*;
import java.io.*;

import org.w3c.dom.*;

import org.xito.boot.*;
import org.xito.blx.*;
import org.xito.dcf.dnd.*;
import org.xito.dcf.event.*;

/**
 * Component
 *<p>
 * The Component class is the base class for DCF based Components. The Component supports
 * the IXMLPersistable interface.
 *<p>
 * The Component can be placed on the Desktop or reside inside a traditional Swing container
 * When placed on the Desktop the Component is placed in its own delegate Window.
 *<p>
 * The Component also supports the basic DCF drag and drop operations.
 *<p>
 * Sub classes should overridge the setBLXElement and getXMLElement methods to add support
 * for storing their own data in XML.
 *
 * @author  $Author: drichan $
 * @version $Revision: 1.8 $
 * @since   $Date: 2007/11/28 03:52:40 $
 */
public class DCComponent extends JPanel implements BLXObject {
   
   //Flags
   protected boolean onDesktop_flag = false;
   protected boolean visible_flag = true;
   protected boolean draggable_flag = false;
   
   protected Window window;
   protected boolean currentlyDragging_flag = false;
   protected DragSource dragSource;
   protected DragGestureRecognizer dragRecognizer;
   protected DragSourceListener dragSourceListener;
   protected static JFrame desktopFrame;
   protected HashSet dcComponentListeners;
   
   protected Transferable transferObject;
   private String id;
   
   //Persistence Support
   protected boolean dirty_flag = true;
   
   protected BLXExtension extension;
   protected URL relativeURL;
   
   //Data Flavors
   public static final String COMP_MIME_TYPE = "text/xml";
   public static final String COMP_FLAVOR_NAME = "dcf.xml";
   public static final String COMP_REF_FLAVOR_NAME = "dcf.ref";
   public static final DataFlavor DCF_FLAVOR = new BLXDataFlavor(COMP_MIME_TYPE, COMP_FLAVOR_NAME);
   public static final DataFlavor DCF_REF_FLAVOR = new BLXDataFlavor(DCComponent.class,COMP_REF_FLAVOR_NAME); 
   public static Color defaultDragColor = new Color(0.5f, 0.5f, 0.5f, 0.5f);
   
   static {
      desktopFrame = new JFrame();
      desktopFrame.setSize(0,0);
      desktopFrame.setUndecorated(true);
      //hack for mac to make it invisible
      desktopFrame.setBackground(new Color(0,0,0,0));
   }
   
   /**
    *
    *
    */
   public DCComponent() {
      super();
      
      //Setup  extension info
      ClassLoader loader = this.getClass().getClassLoader();
      if(loader instanceof BLXExtClassLoader) {
         extension = ((BLXExtClassLoader)loader).getExtension();
      }
      else if(loader instanceof ServiceClassLoader) {
         try {
            String serviceName = ((ServiceClassLoader)loader).getService().getName();
            extension = BLXExtManager.getInstance().getExtensionFromService(serviceName);
         }
         catch(ExtensionLoadException loadExp) {
            loadExp.printStackTrace();
         }
      }
      else {
         System.err.println("Not using BLXExtClassLoader. BLX Persistance will not work correctly!");
      }
            
      //Setup Default ID
      id = generateID(this);
   }
   
   /**
    * Generate a Unique Component ID for a specified object instance. Calling this method multiple
    * times using the same object may generate the same ID
    * @param object id is to be generated for
    * @return String id
    */
   public static String generateID(Object object) {
      String _clsName = object.getClass().getName();
      int _index = _clsName.lastIndexOf(".");
      _clsName = (_index == -1)?_clsName:_clsName.substring(_index+1);
      String id = _clsName +"_"+System.currentTimeMillis()+ "_" + object.hashCode();
      
      return id;
   }
   
   /**
    * Place the Component on the Desktop or remove it from the Desktop
    * When placed on the Desktop the Component is placed in its own Window
    * When removed from the Desktop the component acts like a standard JComponent
    *<p>
    * When a component is set on the Desktop its visibility is set to false. setVisible(true)
    * must be called after calling setOnDesktop(true)
    *
    * @param pUseDesktop true if the component should be placed on the Desktop
    */
   public void setOnDesktop(boolean pUseDesktop) {
      if(pUseDesktop) {
         placeOnDesktop();
      }
      else {
         removeFromDesktop();
      }
   }
   
   /**
    * Returns true if this component is on the Desktop
    */
   public boolean isOnDesktop() {
      return onDesktop_flag;
   }
   
   public void pack() {
      setSize(getLayout().preferredLayoutSize(this));
   }
   
   public void toFront() {
      if(isOnDesktop()) window.toFront();
   }
   
   /**
    * placed the component on the desktop
    */
   protected void placeOnDesktop() {
      //Already on the Desktop
      if(onDesktop_flag) return;
      
      onDesktop_flag = true;
      
      //Figure out correct Location
      Point loc = super.getLocation();
      try {
         loc = this.getLocationOnScreen();
      }
      catch(Throwable t){
         //Didn't work
      }
      
      //Remove from Parent
      Container parent = this.getParent();
      if(parent != null) {
         parent.remove(this);
         parent.validate();
         parent.repaint();
      }
      
      //First Make a Window
      if(window == null) {
         window = DesktopWindow.createWindow(desktopFrame);
      }
      
      //Set Correct Location
      window.add(this);
      window.setSize(this.getSize());
      setLocation(loc);
      setVisible(false);
      
      //Fire Place on Desktop Event to Listeners
      if(dcComponentListeners == null) return;
      
      Iterator _it = dcComponentListeners.iterator();
      DCComponentEvent _event = new DCComponentEvent(this, DCComponentEvent.COMPONENT_PLACED_ON_DESKTOP);
      while(_it.hasNext()) {
         DCComponentListener _listener = (DCComponentListener)_it.next();
         _listener.componentPlacedOnDesktop(_event);
      }
   }
   
   /**
    * Get OwnerFrame of this Componet
    * This component will return the Frame that owns this Component
    * @return Frame
    */
   public Frame getOwnerFrame() {
      if(isOnDesktop()) return desktopFrame;
      
      return (Frame)SwingUtilities.getAncestorOfClass(Frame.class, this);
   }
   
   /**
    * Gets this components Transferable Object
    * @param dragAction one of DnDConstant Drag Actions
    *        This method by default supports Move and Copy Actions
    *        If you want to support other actions or not support copy etc.
    *        Then this method should be overridden in a subclass.
    */
   public Transferable getTransferable(int dragAction) {
      
      //If we are draggable then return Transferable
      if(dragAction == DnDConstants.ACTION_MOVE)
         return transferObject;
      else if(dragAction == DnDConstants.ACTION_COPY) {
         try {
            DCComponent copy = (DCComponent)BLXCompFactory.getInstance().copy(this);
            return copy.getTransferable(DnDConstants.ACTION_MOVE);
         }
         catch(ClassNotFoundException notFound) {
            notFound.printStackTrace();
         }
         catch(InstantiationException exp) {
            exp.printStackTrace();
         }
      }
      
      return null;
   }
   
   /**
    * Sets this component Transferable Object
    * @param pTransfer Object used for passing data during Drag and Drop
    */
   public void setTransferable(Transferable pTransfer) {
      transferObject = pTransfer;
   }
   
   /**
    * Remove the component from the Desktop
    */
   protected void removeFromDesktop() {
      if(isOnDesktop()) {
         window.setVisible(false);
         this.setSize(window.getSize());
      }
      
      onDesktop_flag = false;
      
      //Fire Place on Desktop Event to Listeners
      if(dcComponentListeners == null) return;
      
      Iterator _it = dcComponentListeners.iterator();
      DCComponentEvent _event = new DCComponentEvent(this, DCComponentEvent.COMPONENT_REMOVED_FROM_DESKTOP);
      while(_it.hasNext()) {
         DCComponentListener _listener = (DCComponentListener)_it.next();
         _listener.componentRemovedFromDesktop(_event);
      }
      
   }
   
   /**
    * Set the Size of this Component
    * @param width
    * @param height
    */
   public void setSize(int width, int height) {
      super.setSize(width, height);
      this.validate();
      
      if(isOnDesktop()) {
         window.setSize(width, height);
      }
      
      setIsDirty(true);
   }
   
   /**
    * Dispose of this Component. This method disposes of the component's Window if
    * The component is on the Desktop. If the component is not on the desktop then
    * it is removed from its parent.
    *<p>
    * The component should not be used after calling dispose.
    */
   public synchronized void dispose() {
      if(isOnDesktop()) {
         removeFromDesktop();
         window.dispose();
      }
      else {
         if(getParent()!=null) getParent().remove(this);
      }
      
      //Fire Dispose Events to Listeners
      if(dcComponentListeners == null) return;
      
      Iterator _it = dcComponentListeners.iterator();
      DCComponentEvent _event = new DCComponentEvent(this, DCComponentEvent.COMPONENT_DISPOSED);
      while(_it.hasNext()) {
         DCComponentListener _listener = (DCComponentListener)_it.next();
         _listener.componentDisposed(_event);
      }
   }
   
   /**
    * Add A DCComponentListener. All DCComponentListeners are also added as ComponentListeners
    * @param pListener
    */
   public void addDCComponentListener(DCComponentListener pListener) {
      
      //Create Array List
      if(dcComponentListeners == null) dcComponentListeners= new HashSet();
      
      //Add The Listener
      dcComponentListeners.add(pListener);
      super.addComponentListener(pListener);
   }
   
   /**
    * Remove a Disposed Listener
    * @param pListener to remove
    */
   public void removeDCComponentListener(DCComponentListener pListener) {
      
      //Remove from Component Listeners also
      if(dcComponentListeners != null) {
         dcComponentListeners.remove(pListener);
      }
      
      super.removeComponentListener(pListener);
   }
   
   /**
    * Get the Location of this Component Relative to its Parent. If the
    * Component is on the Desktop then location is relative to the Screen
    * @return Point
    */
   public Point getLocation() {
      if(isOnDesktop()) {
         return window.getLocation();
      }
      else {
         return super.getLocation();
      }
   }
   
   /**
    * Sets the Location of the Component in its parent or on the Screen
    * @param pX
    * @param pY
    */
   public void setLocation(int pX, int pY) {
      
      if(isOnDesktop()) {
         window.setLocation(pX, pY);
      }
      else {
         super.setLocation(pX, pY);
      }
      
      setIsDirty(true);
   }
   
   /**
    * Hides or Shows this Component
    * @param pVisible
    */
   public void setVisible(boolean pVisible) {
      visible_flag = pVisible;
      super.setVisible(pVisible);
      
      if(isOnDesktop()) {
         window.setVisible(pVisible);
      }
      
   }
   
   /**
    * Sets the Drag Source Listener of this Component
    * @param pListener
    */
   protected void setDragSourceListener(DragSourceListener pListener) {
      dragSourceListener = pListener;
   }
   
   /**
    * True if This Component can be dragged. False otherwise
    * @param pDraggable
    */
   public void setDraggable(boolean pDraggable) {
      draggable_flag = pDraggable;
      
      //Setup DragSource and DragRecognizer
      dragSource = (DragSource)DCDragSource.getDefaultDragSource();
      dragRecognizer = dragSource.createDefaultDragGestureRecognizer(this,DnDConstants.ACTION_COPY_OR_MOVE, new DragListenerAdapter(this, dragSourceListener));
      
      //Setup the Default Transfer Object
      transferObject = new DCTransferObject(this);
   }
   
   /**
    * Returns the Image that will be used while this Component is being Dragged
    * @return Image
    */
   public Image getComponentDragImage() {
      
      BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = (Graphics2D)image.getGraphics();
      
      //remeber currently dragged so that we don't wash out the drag image
      boolean state = currentlyDragging_flag;
      currentlyDragging_flag = false;
      this.paint(g);
      currentlyDragging_flag = state;
            
      return image;
   }
   
   public void dragStopped() {
      currentlyDragging_flag = false;
      invalidate();
      repaint();
   }
   
   public void dragStarted() {
      currentlyDragging_flag = true;
      invalidate();
      repaint();
   }
   
   /**
    * Return true if this Component is Draggable
    * @return boolean
    */
   public boolean isDraggable() {
      return draggable_flag;
   }
   
   public void paint(Graphics g) {
      super.paint(g);
      if(currentlyDragging_flag == true) {
         Graphics2D g2 = (Graphics2D)g;
         //Paint wash = new GradientPaint(0, 0, defaultDragColor, getWidth(), getHeight(), new Color(0,0,0,0));
         g2.setPaint(new Color(0,0,0,50));
         g2.fillRect(0, 0, getWidth(), getHeight());
      }
   }
   
   /**
    * Helper Method Used by sub-classes to easily create a
    * DOM Document using the xmlParser
    */
   protected org.w3c.dom.Document createDOMDocument() {
      
      return BLXUtility.createDOMDocument();
   }
   
   /**
    * Get the BLX Element for this Component or Object
    * @return the BLXElement object that describes this type of Component.
    */
   public BLXElement getBLXElement() {
      
      BLXElement blxElement = new BLXElement(BLXElement.COMP_TYPE, extension, this.getClass().getName(), getBLXId(), getLocation().x, getLocation().y, getSize().width, getSize().height);
      blxElement.setContextURL(relativeURL);
      return blxElement;
   }
   
   /**
    * Set the xmlChanged Flag
    * Only sub classes can call this
    * @param Changed
    */
   public void setIsDirty(boolean pChanged) {
      dirty_flag = pChanged;
   }
   
   /**
    * Add a Child Node to a Parent Node for the Given Document
    * @param doc Document of Nodes
    * @param parent Node child should be added to. This node will be imported into doc if
    *        it hasn't already
    * @param child Node that will be added to parent node
    * @return new parent node
    */
   protected Node appendChild(Document doc, Node parent, Node child) throws DOMException {
      
      //Return null for Parent
      if(parent == null) return null;
      
      //Import parent into Doc
      if(parent.getOwnerDocument() != doc) {
         parent = doc.importNode(parent, true);
      }
      
      //Return Parent for null just return parent
      if(child == null) return parent;
      
      //Import child into Doc
      if(child.getOwnerDocument() != doc) {
         child = doc.importNode(child, true);
      }
      
      //Append Child to Parent
      child = parent.appendChild(child);
      
      return parent;
   }
   
   /**
    * Get an Attribute Int Value from an Element
    */
   protected int getIntAttribute(Element pElement, String pAttName, int pDefaultValue) {
      
      try {
         return Integer.parseInt(pElement.getAttribute(pAttName));
      }
      catch(NumberFormatException _exp) {
         return pDefaultValue;
      }
   }
   
   /**
    * Get the Desktop Frame that owns all components on the Desktop
    */
   public static Frame getDesktopFrame() {
      return desktopFrame;
   }
   
   /**
    * Add a stage listener to the Main Desktop Frame
    */
   public static void addDesktopFrameListener(WindowListener listener) {
      desktopFrame.addWindowListener(listener);
   }
   
   /**
    * Add a stage listener to the Main Desktop Frame
    */
   public static void removeDesktopFrameListener(WindowListener listener) {
      desktopFrame.removeWindowListener(listener);
   }
   
   /**
    * Set the Image used for the DCComponents Desktop Frame
    * This Frame is the owner of all DCComponents when they are on the Desktop
    */
   public static void setDesktopFrameIconImage(Image pImage) {
      desktopFrame.setIconImage(pImage);
   }
   
   /**
    * Set the Title used for the DCComponents Desktop Frame
    * This Frame is the owner of all DCComponents when they are on the Desktop
    */
   public static void setDesktopFrameTitle(String pTitle) {
      desktopFrame.setTitle(pTitle);
   }
   
   /**
    * Set Visiblity of the Desktop Frame
    * This Frame is the owner of all DCComponents when they are on the Desktop
    * If the frame is Visible then it will be in an Iconified state
    */
   public static void setDesktopFrameVisible(boolean pVisible) {
      if(pVisible) {
         //hack to move desktop Frame off screen
         desktopFrame.setLocation(2000,2000);
      }
      desktopFrame.setVisible(pVisible);
   }
   
   /**
    * Get the XML Data associated with this Object. The XML Data should be a single element
    * that this object uses to persist its entire state.  All nested IBLXObjects data will also
    * be contained in this Data Element whether they are dirty or not
    * @return the XML Data Element for this Component
    */
   public Element getDataElement() {
      return null;
   }
   
   /**
    * Return true if this components state has changed in a way that
    * Requires the objects container to fetch new XML Data for the Object.
    * @return true if component has changed
    */
   public boolean isDirty() {
      return dirty_flag;
   }
   
   /**
    * Set the BLX Element for this Component or Object
    * This should only be called when the object is first being created. Which
    * would normally be directly after the default constructor has been called.
    * @param pElement for this
    */
   public void setBLXElement(BLXElement blxElement) {
      
      relativeURL = blxElement.getContextURL();
      
      //Set extension Info
      if(blxElement.getExtension() != null) {
         extension = blxElement.getExtension();
      }
      
      String _id = blxElement.getID();
      if(_id.length()>0) this.id = _id;
      
      //Setup Location and Size for Components
      int x = blxElement.getX();
      int y = blxElement.getY();
      int w = blxElement.getWidth();
      int h = blxElement.getHeight();
      
      //Set Locations and Size
      setLocation(x,y);
      setSize(w,h);
      
      setIsDirty(false);
   }
   
   /**
    * Get the BLX Object instance ID for this object
    * @return id
    */
   public String getBLXId() {
      return id;
   }
   
   /** Store the BLX Object. This will store the objects entire child state or
    * its nested children could use the optional IBLXStorageHandler
    * to persist each of its children.
    * @param allChildren true causes this object to call getDataElement on all its children false means
    *   only dirty children
    * @param IBLXStorageHandler child objects can optionally have their state stored in seperate
    *  documents using a Storage handler.
    * @return the XML Data Element for this Component
    *
    */
   public void store(boolean allChildren, BLXStorageHandler storageHandler) throws IOException {
      
      /**
       * The Default implementation calls CompFactory.getBLXDocument(this) and
       * stores a document that represents this objects entire child data state
       */
      
      Element blxElement = this.getBLXElement().getDOMElement();
      Document doc = blxElement.getOwnerDocument();
      System.out.println("****************** Getting Data Element for:"+this.getClass().toString()+":"+this.toString());
      blxElement.appendChild(doc.importNode(this.getDataElement(), true));
      
      doc = BLXCompFactory.getInstance().getBLXDocument(this);
      String name = this.getBLXId() + BLXElement.FILE_EXT;
      storageHandler.storeDoc(name, doc);
      
      setIsDirty(false);
   }
   
   /*****************************************************************
    * Used to fix a problem with Window Focus see JDK Bug: 4186928
    ******************************************************************/
   public static class DesktopWindow implements FocusListener {
      
      static DesktopWindow listener = new DesktopWindow();
      
      public static Window createWindow(Frame parent) {
         String version = System.getProperty("java.specification.version");
         
         //As of JDk 1.4 release I don't think we need this code any more
      /*
      if ("1.4".equals(version)) {
        Frame f = new Frame("DCComponent");
       
        // set no decoration with reflection so it links in 1.3 jre
        try {
          Method m = Frame.class.getDeclaredMethod("setUndecorated", new Class[] { Boolean.TYPE });
          m.invoke(f, new Object[] { Boolean.TRUE });
        }
        catch (Exception ex) {
        }
       
        f.addFocusListener(listener);
       
        return f;
      }
       */
         //else {
         Window w = new Window(parent);
         
         return w;
         //}
      }
      
      public void focusLost(FocusEvent e) {
      }
      
      public void focusGained(FocusEvent e) {
         Component c = e.getComponent();
         if (!(c instanceof Window))
            return;
         
         Window w = (Window)c;
         w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_ACTIVATED));
      }
   }
   
}
