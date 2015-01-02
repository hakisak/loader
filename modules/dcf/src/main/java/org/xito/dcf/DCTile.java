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

import java.io.*;
import java.awt.*;
import java.beans.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import org.w3c.dom.*;
import org.xito.dcf.dnd.*;
import org.xito.blx.*;
import org.xito.dcf.property.*;

/**
 *
 *
 * @author  $Author: drichan $
 * @version $Revision: 1.7 $
 * @since   $Date: 2008/03/28 05:25:57 $
 */
public class DCTile extends DCComponent implements Cloneable, Serializable {
   
   public static final String TILE_MIME_TYPE = "text/xml";
   public static final String TILE_FLAVOR_NAME = "tile.xml";
   public static final String TILE_REF_FLAVOR_NAME = "tile.ref";
   public static BLXDataFlavor TILE_FLAVOR = new BLXDataFlavor(TILE_MIME_TYPE, TILE_FLAVOR_NAME);
   public static BLXDataFlavor TILE_REF_FLAVOR = new BLXDataFlavor(DCTile.class, TILE_REF_FLAVOR_NAME);
   
   /**
    */
   public final static Dimension DEFAULT_TILE_SIZE = new Dimension(48,48);
   /**
    */
   public final static Dimension DEFAULT_ICON_SIZE = new Dimension(48,48);
   
   /**
    */
   public final static String DEFAULT_BKG = "/org/xito/dcf/images/tile_background.gif";
   
   /**
    */
   public final static String DEFAULT_ICON = "/org/xito/dcf/images/globe_32x32.png";
   
   /**
    * Resource used to show the Tile is Busy
    */
   public final static String DEFAULT_BUSY = "/org/xito/dcf/images/tile_busy.gif";
   
   /**
    */
   protected transient static ImageIcon defaultIcon = new ImageIcon(DCTile.class.getResource(DEFAULT_ICON));
   
   /**
    * Icon used to show Tile is Busy
    */
   protected transient ImageIcon busyIcon;
   
   /**
    */
   protected final float default_shortTitle_Size = 10f;
   /**
    */
   protected final Font default_shortTitleFont = new Font("SansSerif",Font.PLAIN, 10);
   /**
    */
   public final static String SHOW_TITLE_ATTR = "showtitle";
   /**
    */
   public final static String TITLE_ATTR = "title";
   /**
    */
   public final static String NAME_ATTR = "name";
   /**
    */
   public final static String DESC_ATTR = "desc";
   /**
    */
   public final static String NODE_NAME = "tile";
   
   /**
    */
   protected boolean showTitle_flag = false;
   
   /**
    */
   protected boolean showBkgrd_flag = true;
   
   /**
    */
   protected transient boolean compute_titleFont_flag = true;
   
   /**
    */
   protected transient Image backgroundImage;
   
   /**
    * Set to true when we are trying to perform the associated Action
    */
   protected transient boolean showBusy_flag = false;
   
   /**
    */
   protected Icon icon;
   protected int iconX = 0;
   protected int iconY = 0;
   /**
    */
   protected String shortTitle;
   /**
    */
   protected String title;
   /**
    */
   protected String description;
   /**
    */
   protected transient TitleComponent titleComp;
   /**
    */
   protected Action action;
   /**
    */
   protected transient Font shortTitleFont = default_shortTitleFont;
   /**
    */
   protected transient int shortTitleX = 0;
   
   protected transient DCPopupMenu tileMenu;
   protected transient MyMenuListener menuListener;
   protected transient JMenuItem propertiesMI;
   protected transient JMenuItem deleteMI;
   
   /**
    * Create a Tile
    */
   public DCTile() {
      this((Icon)null, null, null, null);
   }
   
   /**
    * Create a Tile
    * @param pIcon Icon to Use for Tile
    * @param pShortTitle short title for Tile
    * @param pTitle Title for Tile
    * @param pDesc Description of Tile
    */
   public DCTile(Icon pIcon, String pShortTitle, String pTitle, String pDesc) {
      //Call Super for Component Stuff
      super();
      
      //Size and Location
      setSize(DEFAULT_TILE_SIZE);
      setLayout(null);
      setLocation(0,0);
      
      //Background
      backgroundImage = (new ImageIcon(DCTile.class.getResource(DEFAULT_BKG))).getImage();
      busyIcon = new ImageIcon(DCTile.class.getResource(DEFAULT_BUSY));
      
      //Title, Icon
      setShortTitle(pShortTitle);
      setTitle(pTitle);
      setDescription(pDesc);
      setIcon(pIcon);
      
      //Setup Draggable
      setDraggable(true);
      setTransferable(new MyTransferable(this));
      
      addMouseListener(new MouseAdapter(){
         public void mouseClicked(MouseEvent pEvent) {
            int _count = pEvent.getClickCount();
            if(SwingUtilities.isLeftMouseButton(pEvent) && _count==2) {
               if(action != null) fireAction();
               return;
            }
            if(SwingUtilities.isRightMouseButton(pEvent) && _count==1) {
               tileMenu.show(pEvent.getComponent(), pEvent.getX(), pEvent.getY());
               return;
            }
         }
      });
      
      //Setup Menu
      tileMenu = new DCPopupMenu();
      menuListener = new MyMenuListener();
      propertiesMI = new JMenuItem("Properties");
      propertiesMI.addActionListener(menuListener);
      deleteMI = new JMenuItem("Delete");
      deleteMI.addActionListener(menuListener);
      tileMenu.add(propertiesMI);
      tileMenu.add(deleteMI);
   }
   
   /**
    * Fire the Action Associated with this Tile
    */
   private synchronized void fireAction() {
      
      if(action == null || showBusy_flag) return;
      
      //Start a Thread for this Action
      Thread _t = new Thread(new Runnable(){
         public void run() {
            showBusy_flag = true;
            //Repaint the Tile
            invalidate();
            repaint();
            
            action.actionPerformed(new ActionEvent(this, 0, ""));
            showBusy_flag = false;
            
            //Repaint the Tile
            invalidate();
            repaint();
         }
      });
      
      try {
         _t.start();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }
   
   /**
    * Clone this Tile
    * For the tile to be cloned the Action must also be able to clone
    */
   public Object clone() throws CloneNotSupportedException {
      DCTile copy = new DCTile(icon, shortTitle, title, description);
      return copy;
   }
   
   /**
    * Set the Icon for the Tile
    * @param pIcon to use
    */
   public void setIcon(Icon pIcon) {
      //Setup Icon
      icon = (pIcon == null)?defaultIcon:pIcon;
      //icon.setSize(DEFAULT_ICON_SIZE);
      iconX = (DEFAULT_TILE_SIZE.width/2) - (icon.getIconWidth()/2);
      iconY = 4;
      
      //Update Action
      if(this.action != null) {
         this.action.putValue("LargeIcon", icon);
      }
   }
   
   /**
    * Set the Action for the Tile
    * @param pAction
    */
   public void setAction(Action pAction) {
      action = pAction;
      
      //Setup Properties
      if(action != null) {
         
         //Setup Icon
         if(icon == null || icon == defaultIcon) {
            setIcon((Icon)action.getValue("LargeIcon"));
         }
         
         //Setup Titles
         setShortTitle((String)action.getValue(Action.NAME));
         setTitle((String)action.getValue(Action.SHORT_DESCRIPTION));
         
         action.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
               String propName = evt.getPropertyName();
               if(propName.equals(Action.NAME)) setShortTitle((String)evt.getNewValue());
               if(propName.equals(Action.SHORT_DESCRIPTION)) setTitle((String)evt.getNewValue());
               if(propName.equals(Action.LONG_DESCRIPTION)) setDescription((String)evt.getNewValue());
               
               //Tile is Dirty
               setIsDirty(true);
               
               //Repaint the Tile
               revalidate();
               repaint();
            }
         });
      }
      
      //Update Transferable
      MyTransferable transferObj = new MyTransferable(this);
      setTransferable(transferObj);
   }
   
   /**
    * Get the Action of the Tile
    * @return Action
    */
   public Action getAction() {
      return action;
   }
   
   /**
    * Set the Location of the Tile
    * @param x location
    * @param y location
    */
   public void setLocation(int x, int y) {
      super.setLocation(x, y);
      
      //Show Title
      if(getShowTitle() && title != null) updateTitleLocation();
   }
   
   /**
    * Update the Location of the Title based on the Tile Location
    */
   protected void updateTitleLocation() {
      
      System.out.println("Update Title Location:"+title);
      if(title != null || shortTitle != null) {
         Point _loc = this.getLocation();
         _loc.x = _loc.x - ((titleComp.getWidth() - this.getWidth())/2);
         _loc.x = (_loc.x>0)?_loc.x:0;
         titleComp.setLocation(_loc.x, _loc.y + getHeight());
         titleComp.setVisible(showTitle_flag);
      }
      else if(title == null && shortTitle == null) {
         titleComp.setVisible(false);
      }
      
      revalidate();
      repaint();
   }
   
   /**
    * Return the Preferred Size of this Tile
    */
   public Dimension getPreferredSize() {
      return DEFAULT_TILE_SIZE;
   }
   
   /**
    * Show or Hide the Tile
    * @param pVisible
    */
   public void setVisible(boolean pVisible) {
      super.setVisible(pVisible);
      
      //Show the Title
      
      if(showTitle_flag && title != null) {
         updateTitleLocation();
         titleComp.setVisible(pVisible);
      }
   }
   
   /**
    * Set Tile on Desktop
    * @param pBool
    */
   public void setOnDesktop(boolean pBool) {
      super.setOnDesktop(pBool);
      setShowTitle(pBool);
      titleComp.setOnDesktop(pBool);
   }
   
   /**
    * Show the Title
    * @param pShowTitle
    */
   public void setShowTitle(boolean pShowTitle) {
      showTitle_flag = pShowTitle;
      if(title != null) updateTitleLocation();
   }
   
   /**
    * Show the Tile BackGround
    */
   public void setShowBackground(boolean b) {
      showBkgrd_flag = b;
      setOpaque(showBkgrd_flag);
      revalidate();
      repaint();
   }
   
   /**
    * Do we show the Background
    */
   public boolean getShowBackground() {
      return showBkgrd_flag;
   }
   
   /**
    * Show the Title
    */
   public boolean getShowTitle() {
      return showTitle_flag;
   }
   
   /**
    * Set the Short Title. The Short Title is displayed on the Tile itself
    * @param @pTitle
    */
   public void setShortTitle(String pTitle) {
      
      if(pTitle == null || pTitle.length()==0) {
         shortTitle = null;
      }
      else {
         shortTitle = pTitle;
      }
      
      compute_titleFont_flag = true;
      
      //Use the Short Title for the Title if not already Set
      if(title == null && titleComp == null) {
         titleComp = new TitleComponent(shortTitle);
      }
      else if(title==null) {
         titleComp.setText(shortTitle);
      }
      
      if(title != null || shortTitle != null) updateTitleLocation();
      
      revalidate();
      repaint();
   }
   
   /**
    * Get the Short Title
    * @return Short Title
    */
   public String getShortTitle() {
      return shortTitle;
   }
   
   /**
    * Set the Title of the Tile
    * The Title displays in a seperate Window below the Tile
    * @param pTitle
    */
   public void setTitle(String pTitle) {
      
      if(pTitle != null) pTitle = pTitle.trim();
      
      //Can't be empty string
      if(pTitle == null || pTitle.length()==0){
         title = null;
      }
      else {
         title = pTitle;
      }
      
      //Setup Tooltip
      setToolTipText(title);
      
      if(titleComp == null) titleComp = new TitleComponent(title);
      
      titleComp.setText(title == null?shortTitle:title);
      if(title != null || shortTitle != null) updateTitleLocation();
   }
   
   /**
    * Get the Title
    * @return title
    */
   public String getTitle() {
      return title;
   }
   
   /**
    * Set the Description of the Tile.
    * The Description is used for the ToolTip
    * @param pDesc
    */
   public void setDescription(String pDesc) {
      description = pDesc;
   }
   
   /**
    * Get Description
    * @return Description
    */
   public String getDescription() {
      return description;
   }
   
   /**
    * Show the Busy Icon
    */
   public void showBusy() {
      showBusy_flag = true;
   }
   
   /**
    * Hide the Busy Icon
    */
   public void hideBusy() {
      showBusy_flag = false;
   }
   
   /**
    * Paint the Tile Component
    * This method first paints the Background Image then the Icon
    * and then the Title
    */
   public void paintComponent(Graphics pGraphics) {
      super.paintComponent(pGraphics);
      
      //Anti-Alias Fonts
      //((Graphics2D)pGraphics).addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
      
      //Show Busy if Firing Action
      if(showBusy_flag) {
         busyIcon.paintIcon(this, pGraphics, 0, 0);
         return;
      }
      
      //Paint BackGround
      if(showBkgrd_flag || this.isOnDesktop()) pGraphics.drawImage(backgroundImage,0,0,null);
      
      //Paint the Icon
      icon.paintIcon(this, pGraphics, iconX , iconY);
      
      //Pick a Title to Use.
      String _title;
      if(getShortTitle() == null || getShortTitle().length()==0) {
         _title = getTitle();
      }
      else {
         _title = getShortTitle();
      }
      
      //Figure out the best font if haven't already
      if(compute_titleFont_flag && _title != null) {
         FontMetrics _fm = pGraphics.getFontMetrics(default_shortTitleFont);
         int _w = SwingUtilities.computeStringWidth(_fm, _title);
         
         //Get the right Font
         int max_w = getWidth()-5;
         if(_w > max_w) {
            float _dif = (_w - max_w) > 0 ?_w - max_w :0;
            float _ratio = 1 - (_dif/max_w);
            
            shortTitleFont = default_shortTitleFont.deriveFont(default_shortTitle_Size * _ratio);
            _fm = pGraphics.getFontMetrics(shortTitleFont);
            _w = SwingUtilities.computeStringWidth(_fm, _title);
         }
         else {
            shortTitleFont = default_shortTitleFont;
         }
         
         shortTitleX = (getWidth()/2) - (_w/2);
         compute_titleFont_flag = false;
      }
      
      //We only paint the Short Title when we are not displaying the Regular Title
      if(showTitle_flag == false && _title != null) {
         pGraphics.setFont(shortTitleFont);
         pGraphics.drawString(_title, shortTitleX, 43);
      }
   }
   
   /**
    * Dispose of this Tile
    */
   public void dispose() {
      //Dispose Title Also
      if(titleComp != null) {
         titleComp.dispose();
      }
      
      super.dispose();
   }
   
   /**
    *
    */
   public void setBLXElement(BLXElement blxElement) {
      //First set the basic Component Stuff
      super.setBLXElement(blxElement);
      
      //Now get the Tile Stuff.
      Element _tileElement = blxElement.getDataElement();
      //If null then xml data is bad
      if(_tileElement == null) {
         this.dirty_flag = true;
         return;
      }
      
      //Process Each Child element of the tile Element
      NodeList _children = _tileElement.getChildNodes();
      for(int i=0;i<_children.getLength();i++) {
         Node _child = _children.item(i);
         if(_child.getNodeType() == Node.ELEMENT_NODE) {
            Element _element = (Element)_child;
            processElement(_element);
         }
      }
   }
   
   /**
    * Process an Embedded Element
    */
   protected void processElement(Element pElement) {
      try {
         //Check for Properties Element
         if(pElement.getNodeName().equals(BLXProperties.PROPERTIES_NODE_NAME)) {
            BLXProperties _properties = new BLXProperties();
            _properties.setXMLElement(pElement);
            processProperties(_properties);
            return;
         }
         
         //Get the Object from the Component Factory
         BLXCompFactory _compFactory = BLXCompFactory.getInstance();
         Object _obj = _compFactory.getObject(new BLXElement(pElement, relativeURL), null);
         
         //See if Object is an Action
         if(_obj instanceof Action) {
            this.setAction((Action)_obj);
         }
      }
      catch(ClassNotFoundException _exp) {
         _exp.printStackTrace();
      }
      catch(InstantiationException _exp) {
         _exp.printStackTrace();
      }
      catch(InvalidBLXXMLException _exp) {
         System.err.println("Error: Tile Contains invalid BLX Element");
      }
   }
   
   /**
    * Process Properties from XML
    * @param pProperties Properties to Process
    */
   protected void processProperties(BLXProperties pProperties) {
      //Get Show Title Property
      this.setShowTitle(pProperties.getPropertyBoolean(SHOW_TITLE_ATTR));
      this.setTitle(pProperties.getPropertyString(TITLE_ATTR));
      this.setShortTitle(pProperties.getPropertyString(NAME_ATTR));
      this.setDescription(pProperties.getPropertyString(DESC_ATTR));
   }
   
   /**
    * Gets the XML Data for this Tile
    * @return xml Element
    */
   public Element getDataElement() {
      try {
         //Create Tile Element
         Document _doc = createDOMDocument();
         Element _tileElement = _doc.createElement(NODE_NAME);
         
         //Add Properties
         BLXProperties _properties = new BLXProperties();
         _properties.setPropertyBoolean(this.SHOW_TITLE_ATTR, this.showTitle_flag);
         _properties.setPropertyString(this.TITLE_ATTR, getTitle());
         _properties.setPropertyString(this.NAME_ATTR, getShortTitle());
         _properties.setPropertyString(this.DESC_ATTR, getDescription());
         _tileElement = (Element)appendChild(_doc, _tileElement, _properties.getXMLElement());
         
         //Add Action
         if(action != null) {
            if(Beans.isInstanceOf(action, BLXObject.class)) {
               Element _actionE = ((BLXObject)action).getBLXElement().getDOMElement();
               _actionE = (Element)appendChild(_doc, _actionE, ((BLXObject)action).getDataElement());
               _tileElement = (Element)appendChild(_doc, _tileElement, _actionE);
            }
            else if(Beans.isInstanceOf(action, Serializable.class)) {
               _tileElement = (Element)appendChild(_doc, _tileElement, new SerializeObjectAdapter((Serializable)action).getBLXElement().getDOMElement());
            }
         }
         
         return _tileElement;
      }
      catch(ClassCastException _exp) {
         return null;
      }
   }
   
   /************************************************************
    * This Class is the listener for the Popup Menu
    ************************************************************/
   class MyMenuListener implements ActionListener {
      
      public MyMenuListener() {
         
      }
      
      public void actionPerformed(ActionEvent pEvent) {
         
         //Delete
         if(pEvent.getSource() == deleteMI) {
            //Need a Confirm Here
            dispose();
            return;
         }
         
         //Properties
         if(pEvent.getSource() == propertiesMI) {
            PropertySheetDialog _dialog = null;
            if(action == null) {
               _dialog = new PropertySheetDialog(DCTile.this, "Tile Properties");
            }
            else {
               _dialog = new PropertySheetDialog(action, "Tile Properties");
            }
            _dialog.show();
         }
      }
   }
   
   /************************************************************
    * This Class is used to display the Title of the Tile
    ************************************************************/
   class TitleComponent extends DCComponent {
      JLabel label;
      FontMetrics metrics;
      
      TitleComponent(String text) {
         super();
         label = new JLabel();
         label.setForeground(Color.WHITE);
         label.setHorizontalAlignment(JLabel.CENTER);
         label.setFont(label.getFont().deriveFont(Font.PLAIN, 10));
         metrics = Toolkit.getDefaultToolkit().getFontMetrics(label.getFont());
         setLayout(new BorderLayout());
         add(label);
         
         this.setBackground(Color.BLACK);
         this.setBorder(LineBorder.createBlackLineBorder());
         setText(text);
      }
      
      void setText(String pText) {
         label.setText(pText);
         
         if(pText != null) {
            int _w = SwingUtilities.computeStringWidth(metrics, pText);
            setSize(_w+10, 20);
         }
         else {
            setSize(0,0);
         }
      }
   }
   
   /************************************************************
    * This Class is the Transferable Object for the Tile
    ************************************************************/
   class MyTransferable extends DCTransferObject {
      //Create the Transfer Object
      protected MyTransferable(DCTile pTile) {
         super(pTile);
         
         //Tile DataFlavor
         dataFlavors.add(DCTile.TILE_FLAVOR);
         
         //Tile Ref DataFlavor
         dataFlavors.add(DCTile.TILE_REF_FLAVOR);
         
         //Action Data Flavor
         Action action = pTile.getAction();
         if(action != null) {
            dataFlavors.add(BLXDataFlavor.ACTION_FLAVOR);
         }
         
      }
      
      /**
       * Get TransferData
       */
      public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
         
         //Return Tile Reference
         if(flavor.equals(DCTile.TILE_REF_FLAVOR)) {
            return dataObj;
         }
         
         //Return XML for Tile
         else if(flavor.equals(DCTile.TILE_FLAVOR)) {
            return super.getTransferData(BLXDataFlavor.XML_FLAVOR);
         }
         
         return super.getTransferData(flavor);
      }
   }
}
