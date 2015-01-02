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
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;

import org.xito.dazzle.widget.DefaultStyle;
import org.xito.dcf.dnd.*;
import org.xito.dcf.event.*;
import org.xito.dialog.TableLayout;

//import sun.security.krb5.internal.UDPClient;


/**
 * DCMenu provides a component that can be used in place of
 * a JPopupMenu. This menu provides several features beyond the common JPopupMenu
 *
 * First the DCMenu pops up in different directions based on the popup location on the screen.
 * If the popup is located in the North West corner of the screen then sub menu popups occure in a
 * South East direction. If the popup is located in the North East corner of the screen then sub
 * menu popups occure in a South West direction and so on.
 *
 * DCMenus can be torn off of their orginial menus and copies placed on the destop. Because there can
 * be multiple views of the same menu the DCMenu uses a MVC mechanism. Because of the MVC design, actions
 * must be added to the model not the DCMenu which is just the view.
 *
 * The DCMenuItem is used internall by the DCMenu for display purposes but cannot be added to DCMenu's directly.
 * Actions should be used to add functionality to DCMenus.
 *
 * @author  drichan
 * @version
 */
public class DCMenu extends DCComponent implements AWTEventListener, ActionListener, DCMenuModelListener {
  
  public static final int PARENT = -1;
  public static final int SE = 0;
  public static final int SW = 1;
  public static final int NE = 2;
  public static final int NW = 3;
  
  public static final int MIN_WIDTH = 100;
  public static final int MAX_WIDTH = 250;
  
  /**
   * Variable used for adding seperators to a DCMenu.
   * Use menu.add(DCMenu.SEPERATOR);
   */
  public static final int SEPERATOR = 8;
  private static final ImageIcon closeImage = new ImageIcon(DCComponent.class.getResource("org/xito/dcf/images/close_enabled.gif"));
  private static int tearOff_height = 7;
  private static int title_height = 22;
  private int popupDirection = -1;
  private String text;
  private PopupWindow popupWindow;
    
  //private SubMenuItem myMenuItem;
  private DCMenu parentMenu;
  private DCMenuModel model;
  private TearOffComp tearOffComp;
  private TitleComp titleComp;
  private boolean tearOff = false;
  private String id;
  
  private DCDropTarget dropTarget;
  private boolean paintDragOver;
  private Point dropLocation;
  /**
   * Create a new DCMenu. Required for BLX Support
   */
  public DCMenu() {
    super();
  }
  
  /**
   * Create a new DCMenu with the specified text string as its Name
   * @param text name of this menu. Used for sub-menu text.
   */
  public DCMenu(String text) {
    this(new DCMenuModel(text));
  }
  
  /**
   * Create a new DCMenu with the specified Menu Model
   * @param model to use for this menu
   */
  public DCMenu(DCMenuModel model) {
    
    this(model, false);
  }
  
  /**
   * Create a new DCMenu with the specified Menu Model and Torn Off Setting
   * @param model to use for this menu
   * @param tearOff true if the menu is torn Off
   */
  public DCMenu(DCMenuModel model, boolean tearOff) {
    
    super();
    this.tearOff = tearOff;
    setModel(model);
  }
  
  /**
   * Setup the DCMenu
   */
  private void init() {
    
    this.removeAll();
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            
    //We are not in the Tear Off State then add the Tear Off Comp
    if(tearOff == false) {
      tearOffComp = new TearOffComp();
      tearOffComp.setAlignmentX(0.0f);
      add(tearOffComp);
    }
    //We are torn off so add the Title Component
    else {
      titleComp = new TitleComp();
      titleComp.setAlignmentX(0.0f);
      add(titleComp);
    }
    
    //Drop Target
    dropTarget = new DCDropTarget(this, new MyDropTargetListener());
        
    //Populate contents from current Model
    if(model != null)  {
      for(int i=0;i<model.size();i++) {
        Object item = model.get(i);
        if(item instanceof Action) {
          addAction((Action)model.get(i));
        }
        else if(item instanceof DCMenuModel) {
          addSubMenu((DCMenuModel)item);
        }
      }
    }
    
  }
  
  /**
   * Hides or Shows this Component
   * @param pVisible
   */
  public void setVisible(boolean pVisible) {
    super.setVisible(pVisible);
    
    //Calculate Popup Direction if on Desktop
    if(isTearOff() && pVisible) {
      calculatePopupDirection(getLocationOnScreen());
    }
  }
  
  /**
   * return true if this Menu is currently in the TearOff State
   */
  public boolean isTearOff() {
    return tearOff;
  }
  
  /**
   * Sets the Tear Off State of this Menu. Should only be called after creating a
   * new Menu and before calling setModel
   */
  public void setTearOff(boolean to) {
    tearOff = to;
  }
  
  /**
   * Set this DCMenu's Model
   */
  public void setModel(DCMenuModel model) {
    this.model = model;
    this.model.addMenuModelListener(this);
    init();
  }
  
  /**
   * Return this DCMenu's Model
   */
  public DCMenuModel getModel() {
    return model;
  }
  
  /**
   * Get the Menus popup Direction. If this menu has a parent menu then the parent's
   * popup direction will be used
   * @return SE, NE, SW, NW
   */
  protected int getPopupDirection() {
    if(popupDirection == PARENT && parentMenu != null) return parentMenu.getPopupDirection();
    else return popupDirection;
  }
  
  /**
   * Sets this parents menu. Called when this menu is added to another menu
   */
  protected void setParentMenu(DCMenu parent) {
    parentMenu = parent;
  }
  
  /**
   * Get this Menus parent menu
   */
  protected DCMenu getParentMenu() {
    return parentMenu;
  }
  
  /**
   * Return the Menu Title for this menu
   * @return
   */
  public String getMenuTitle() {
     return model.getName();
  }
  
  /**
   * Return the Icon for this Menu
   * @return
   */
  public Icon getIcon() {
     return model.getIcon();
  }
  
  /**
   * The Menu Model has been changed.
   * @param event describing the change that was made to the Menu Model
   */
  public void menuChanged(DCMenuModelEvent event) {
    
    //Add Event
    if(event.getType() == event.ADD_EVENT) {
      //Action Added
      if(event.getItem() instanceof Action) {
        addAction((Action)event.getItem());
      }
      //Sub Menu Added
      else if(event.getItem() instanceof DCMenuModel) {
        addSubMenu((DCMenuModel)event.getItem());
      }
    }
  }
  
  /**
   * Adds a Sub Menus Model this this Menu as a Sub Menu. This is called by the menuChanged message
   * when a Sub Menu Model is added to this Menus Model
   * @param subMenuModel
   */
  protected void addSubMenu(DCMenuModel subMenuModel) {
    
    //Creat the Menu Item for this Action
    DCMenu subMenu = null;
    try {
      subMenu = (DCMenu)DCMenu.this.getClass().newInstance();
    }
    catch(Exception exp) {
      exp.printStackTrace();
      return;
    }
    
    subMenu.setModel(subMenuModel);
    subMenu.setParentMenu(this);
    
    DCMenuItem menuItem = new DCMenuItem(subMenu);
    
    //If we mouse Over a SubMenuItem then we should close all other Sub Menus
    menuItem.addActionListener(this);
    
    //Add the MenuItem to our Display Space
    super.add(menuItem);
    
    updateSizeToPreferredSize();
  }
  
  /**
   * Adds an Action to this Menu. This is called by the menuChanged message
   * when an Action is added to this Menus Model
   * @param action
   */
  protected void addAction(Action action) {
    
    //Creat the Menu Item for this Action
    DCMenuItem menuItem = new DCMenuItem(action);
    menuItem.setParentMenu(this);
    menuItem.addActionListener(this);
    menuItem.setAlignmentX(0.0f);
    
    //Add the MenuItem to our Display Space
    super.add(menuItem);
    
    updateSizeToPreferredSize();
  }
  
  /**
   * Update the current size of the Menu to its preferred Size.
   * based on all children menu items etc.
   */
  public void updateSizeToPreferredSize() {
     
     int height = 0;
     int width = MIN_WIDTH;
     
     for(int i=0;i<getComponentCount();i++) {
        
        Component childComp = getComponent(i);
        Dimension ps = childComp.getPreferredSize();
        height = height + ps.height;
        
        if(ps.width > width) {
           width = ps.width;
        }
     }
     
     //don't go over max
     width = (width > MAX_WIDTH) ? MAX_WIDTH : width;
     
     setSize(width, height);
  }
  
  /**
   * Disposes this Menus Popup window then calls parentMenu.disposeParent
   */
  protected void disposeParent() {
    if(popupWindow != null) popupWindow.dispose();
    if(parentMenu != null) parentMenu.disposeParent();
    popupWindow = null;
  }
  
  /**
   * Disposes all Children popups
   */
  protected void disposeAllChildren() {
    //First dispose all children popup Windows
    //Window children[] = popupWindow.getOwnedWindows();
    //for(int i=0;i<children.length;i++) {children[i].dispose();}
    
    for(int i=0;i<getComponentCount();i++) {
      Object item = getComponent(i);
      if(item instanceof DCMenuItem && ((DCMenuItem)item).isSubMenuItem()) {
        DCMenu subMenu = ((DCMenuItem)item).getMenu();
        if(subMenu.popupWindow != null) {
          subMenu.popupWindow.dispose();
          subMenu.popupWindow = null;
          subMenu.disposeAllChildren();
        }
      }
    }
  }
  
  /**
   * Show the DCMenu as a popup at a specified location relative to a specified component
   */
  public void showPopup(Point point, Component comp) {
    
    Toolkit.getDefaultToolkit().removeAWTEventListener(this);
    
    SwingUtilities.convertPointToScreen(point, comp);
    
    //Determine Popup Direction of First Popup
    calculatePopupDirection(point);
    
    //Get the Window the Component is currently placed in
    Window compWindow = SwingUtilities.getWindowAncestor(comp);
    if(popupWindow == null) {
      popupWindow = new PopupWindow(compWindow, this);
    }
    
    //Calculate Point for popup Direction
    int x = 0;
    int y = 0;
    
    //Caculate X Location
    //Goring East
    if((getPopupDirection() == SE) || (getPopupDirection() == NE)) x = point.x;
    //Going West
    else x = point.x - getWidth();
    
    //Calcuate Y Location
    //Going South
    if((getPopupDirection() == SE) || (getPopupDirection() == SW)) y = point.y;
    //Going North
    else y = point.y - getHeight();
    
    popupWindow.setSize(getSize());
    popupWindow.setLocation(new Point(x,y));
    popupWindow.setContentPane(DCMenu.this);
    popupWindow.setVisible(true);
    popupWindow.invalidate();
    popupWindow.repaint();
    
    Toolkit.getDefaultToolkit().addAWTEventListener(DCMenu.this, AWTEvent.MOUSE_EVENT_MASK);
  }
  
  /**
   * For a given Point in Screen space calculate the Direction that this popup
   * and all children should be shown.
   */
  private void calculatePopupDirection(Point point) {
    
    //Determine Popup Direction of First Popup
    popupDirection = PARENT; //Use Parent
    if(parentMenu == null || tearOff == true) {
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      //East
      if(point.x < (screen.width * 0.666)) {
        if(point.y < (screen.height * 0.5)) popupDirection = SE;
        else popupDirection = NE;
      }
      //West
      else {
        if(point.y < (screen.height * 0.5)) popupDirection = SW;
        else popupDirection = NW;
      }
    }
    
    //Hide East pointing Sub Menu Arrow
    int dir = DCMenuItem.EAST;
    if(getPopupDirection() == NW || getPopupDirection() == SW) {
      dir = DCMenuItem.WEST;
    }
    
    //Tell all MenuItems what their direction is
    for(int i=0;i<countComponents();i++) {
      Component comp = this.getComponent(i);
      if(comp instanceof DCMenuItem) {
        ((DCMenuItem)comp).setDirection(dir);
      }
    }
  }
  
  /**
   * Invoked when an action occurs on one of this Menu's MenuItems.
   */
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    
    //Dispose ourself because a MenuItem was clicked
    if(src instanceof DCMenuItem && !((DCMenuItem)src).isSubMenuItem())
      disposeParent();
  }
  
  /**
   * Displays a SubMenu for a menus MenuItem
   */
  protected void showSubMenu(DCMenuItem menuItem) {
    
    DCMenu subMenu = menuItem.getMenu();
    if(subMenu == null) throw new RuntimeException("Cannot show subMenu for:"+menuItem);
    
    //Dispose all Children
    disposeAllChildren();
    
    //Calculate Point for popup Direction
    int x = 0;
    int y = -tearOff_height;
    
    //Caculate X Location
    //Goring East
    if((getPopupDirection() == SE) || (getPopupDirection() == NE)) x = menuItem.getWidth();
    //Going West
    //else x = -1 * menuItem.getMenu().getWidth();
    
    //Calcuate Y Location
    //Going South
    if((getPopupDirection() == SE) || (getPopupDirection() == SW)) y = -tearOff_height;
    //Going North
    else y = menuItem.getHeight();
    
    subMenu.showPopup(new Point(x,y), menuItem);
  }
  
  /**
   * Invoked when an event is dispatched in the AWT.
   * This is used to determine when a Menus popup should be disposed
   *  If we detect a Mouse Click or Press in the AWT system that is not on our
   * Popup Window this menus popup will be disposed
   */
  public void eventDispatched(AWTEvent event) {
    int id = event.getID();
    if(id == MouseEvent.MOUSE_CLICKED || id == MouseEvent.MOUSE_PRESSED) {
      MouseEvent mouseEvent = (MouseEvent)event;
      Component compSrc = (Component)event.getSource();
      //If the Event Source is the PopupWindow then just return
      if(compSrc == popupWindow) return;
      
      //Get the Components Window and see if it is owned by us
      Window evtWindow = SwingUtilities.getWindowAncestor(compSrc);
      //Return if the window were the event occured is the PopupWindow
      if(evtWindow == popupWindow) return;
      
      Window ownerWindow = evtWindow.getOwner();
      while(ownerWindow != null) {
        //If the window is Owned by the PopupWindow then Return
        if(ownerWindow == popupWindow) return;
        ownerWindow = ownerWindow.getOwner();
      }
      
      //Dipose of the Popup Window
      if(popupWindow != null) {
        popupWindow.dispose();
        popupWindow = null;
      }
      
      Toolkit.getDefaultToolkit().removeAWTEventListener(this);
    }
  }
  
  public void paint(Graphics g) {
     super.paint(g);
     
     if(paintDragOver && dropLocation != null) {
        g.setColor(Color.red);
        g.drawLine(0, dropLocation.y, getWidth(), dropLocation.y);
     }
  };
  
  /**
   * Popup Window used to display the DCMenu in a Popup
   */
  private class PopupWindow extends JWindow {
    DCMenu menu;
    
    /**
     *
     */
    PopupWindow(Window owner, DCMenu menu) {
      super(owner);
      this.menu = menu;
    }
  }
  
  /**
   * This is the Component that is used to display the Tear Off point in the Menu
   * This component will also handle the Dragging operation of the Tear Off Menu.
   * The Drag Image and Transfer Object will be the DCMenu not the Tear Off Component
   */
  private class TearOffComp extends DCComponent implements DragSourceListener {
    
    private DCMenu dragMenu = null;
    private Color tearOffBG;
    private Color tearOffFG;
    
    /**
     * Constructor for TearOffComp
     */
    public TearOffComp() {
      super.setDragSourceListener(this);
      setDraggable(true);
      
      tearOffBG = DefaultStyle.getDefaults().getColor(DefaultStyle.CTRL_TOP_GRADIENT_COLOR_KEY);
      tearOffFG = Color.BLACK;
      
    }
        
    public Dimension getPreferredSize() {
       return new Dimension(getParent().getWidth(), tearOff_height); 
    }
        
    /**
     * DragDrop of TearOff Menu Component End
     *
     */
    public synchronized void dragDropEnd(DragSourceDropEvent evt) {
      //Dipose of the Popup Menu after the Drop of the TearOff menu
      if(DCMenu.this.popupWindow != null) DCMenu.this.disposeParent();
      if(evt.getDropSuccess()) {
        dragMenu.tearOff = true;
        dragMenu.calculatePopupDirection(dragMenu.getLocationOnScreen());
      }
    }
    
    public void dragEnter(DragSourceDragEvent evt) {}
    public void dragExit(DragSourceEvent evt) {}
    public void dragOver(DragSourceDragEvent evt) {}
    public void dropActionChanged(DragSourceDragEvent evt) {}
    
    /**
     * Returns the Image that will be used while this Component is being Dragged
     * @return Image
     */
    public Image getComponentDragImage() {
      return DCMenu.this.getComponentDragImage();
    }
    
    /**
     * Gets this components Transferable Object
     * This will be data that represents the DCMenu on the TearOff Comp
     */
    public synchronized Transferable getTransferable(int dragAction) {
      //We don't care what the action is for now just return a copy
      //If we are draggable to Tear Off a Menu then return a Copy
      if(DCMenu.this.popupWindow != null) {
        try {
          dragMenu = (DCMenu)DCMenu.this.getClass().newInstance();
          dragMenu.setTearOff(true);
          dragMenu.setModel(DCMenu.this.getModel());
          return new DCTransferObject(dragMenu);
        }
        catch(Exception exp) {
          exp.printStackTrace();
          return null;
        }
      }
      //Else we are Moving a current Tear off so use this Menu as the Transfer Object
      else {
        return new DCTransferObject(DCMenu.this);
      }
    }
    
    /**
     * Paint the TearOff Component should be a - - - - - -
     */
    public void paintComponent(Graphics g) {
       
       Graphics2D g2 = (Graphics2D)g;
       
       g2.setColor(tearOffBG);
       g2.fillRect(0,0,getWidth(), getHeight());
       
       g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, 
             new float[]{4, 4}, 0));
       
       g2.setColor(tearOffFG);
      
       int w = getWidth();
       int x = 0;
       int y = tearOff_height/2;
       
       g2.drawLine(x, y, w, y);
      
       /*
      while(x < w) {
        g.drawLine(x,y, x+4, y);
        x+=8;
      }
      */
    }
  }
  
  /**
   * This class will listen for Drop Target Events for items dropped on the DCMenu
   */
  private class MyDropTargetListener implements DropTargetListener {
     
     private JPanel glassPane = new JPanel(null);
     public void dragEnter(DropTargetDragEvent dtde) {
        paintDragOver = true;
        dropLocation = dtde.getLocation();
        invalidate();
        repaint();
     }
     
     public void dragExit(DropTargetEvent dte) {
        paintDragOver = false;
        dropLocation = null;
        System.out.println("dragExit");
        invalidate();
        repaint();
     }
     
     public void dragOver(DropTargetDragEvent dtde) {
        dropLocation = dtde.getLocation();
        invalidate();
        repaint();
     }
     
     public void drop(DropTargetDropEvent dtde) {
        paintDragOver = false;
        System.out.println("drop");
        invalidate();
        repaint();
     }
     
     public void dropActionChanged(DropTargetDragEvent dtde) {
     }
     
  }
  
  /**
   * This is the Component that is used to display the Title of the Menu when it
   * is in a Torn off State. This component will also handle the Dragging operation of the Tear Off Menu.
   */
  private class TitleComp extends DCComponent implements DragSourceListener {
    
    private Font titleFont; 
     
    /**
     * Create a new Title Component
     */
    public TitleComp() {
      this.setPreferredSize(new Dimension(20, title_height));
      super.setDragSourceListener(this);
      setDraggable(true);
      init();
    }
    
    /**
     * Layout the Title Component
     */
    private void init() {
      TableLayout layout = new TableLayout();
      TableLayout.Row row = new TableLayout.Row();
      
      TableLayout.Column col = new TableLayout.Column("icon");
      col.vAlign = TableLayout.CENTER;
      col.hAlign = TableLayout.CENTER;
      row.addCol(col);
      
      col = new TableLayout.Column("text", 0.9999f);
      col.vAlign = TableLayout.CENTER;
      col.hAlign = TableLayout.LEFT;
      row.addCol(col);
      
      col = new TableLayout.Column("close");
      col.vAlign = TableLayout.CENTER;
      col.hAlign = TableLayout.CENTER;
      row.addCol(col);
      
      layout.addRow(row);
      
      setLayout(layout);
      
      //Icon
      Icon icon = DCMenu.this.getModel().getIcon();
      JLabel iconLabel = new JLabel(icon);
      iconLabel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
      add("icon", iconLabel);
      
      //Text
      JLabel textLabel = new JLabel(DCMenu.this.getModel().getName());
      textLabel.setBorder(BorderFactory.createEmptyBorder(0,0,2,0));
      add("text", textLabel);
      
      //Close
      DCCloseButton closeBtn = new DCCloseButton(DCMenu.this);
      closeBtn.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
      add("close", closeBtn);
      
      //Setup Colors
      titleFont = DefaultStyle.getDefaults().getFont(DefaultStyle.LABEL_FONT_KEY);
      //titleColor = Color.
      
      
      TitleComp.this.setBackground(UIManager.getDefaults().getColor("InternalFrame.activeTitleBackground"));
      textLabel.setForeground(UIManager.getDefaults().getColor("InternalFrame.activeTitleForeground"));
      textLabel.setFont(titleFont);
      
    }
    
    public Dimension getPreferredSize() {
       Dimension ps = super.getPreferredSize();
       ps.width = getParent().getWidth();
       return ps;
    }
    
    public Dimension getMaximumSize() {
       return getPreferredSize();
    }
    
    /**
     * Returns the Image that will be used while this Component is being Dragged
     * @return Image
     */
    public Image getComponentDragImage() {
      return DCMenu.this.getComponentDragImage();
    }
    
    /**
     * Gets this components Transferable Object
     * This will be data that represents the DCMenu on the TearOff Comp
     */
    public Transferable getTransferable(int dragAction) {
       //Ignore the Action
      return new DCTransferObject(DCMenu.this);
    }
    
    /**
     * Called when the Dragging of the Title Component is ended.
     * @param dsde the <code>DragSourceDropEvent</code>
     */
    public void dragDropEnd(DragSourceDropEvent dsde) {
      DCMenu.this.calculatePopupDirection(DCMenu.this.getLocationOnScreen());
    }
    
    public void dragEnter(DragSourceDragEvent dsde) {}
    public void dragExit(DragSourceEvent dse) {}
    public void dragOver(DragSourceDragEvent dsde) {}
    public void dropActionChanged(DragSourceDragEvent dsde) {}
    
  }
  
  /**
   * Testing the DCMenu
   */
  public static void main(String args[]) throws Throwable {
    //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    
    //Enumeration keys = UIManager.getDefaults().keys();
    //while (keys.hasMoreElements())    System.out.println(keys.nextElement());
    
    DCMenuModel menuModel = new DCMenuModel("Main Menu");
    menuModel.add(new DefaultAction("Item 1"));
    menuModel.add(new DefaultAction("Item 2"));
    
    Action action3 = new DefaultAction("Item 3 xxxxxxxxxxxxx");
    action3.putValue(Action.SMALL_ICON, null);
    menuModel.add(action3);
    
    menuModel.add(new DefaultAction("Item 4"));
    
    DCMenuModel subMenu1 = new DCMenuModel("Sub Menu1");
    subMenu1.add(new DefaultAction("1 SubItem 1"));
    subMenu1.add(new DefaultAction("1 SubItem 2"));
    subMenu1.add(new DefaultAction("1 SubItem 3"));
    subMenu1.add(new DefaultAction("1 SubItem 4"));
    
    menuModel.add(subMenu1);
    
    DCMenuModel subMenu2 = new DCMenuModel("Sub Menu2");
    subMenu2.add(new DefaultAction("2 SubItem 1"));
    subMenu2.add(new DefaultAction("2 SubItem 2"));
    subMenu2.add(new DefaultAction("2 SubItem 3"));
    subMenu2.add(new DefaultAction("2 SubItem 4"));
    
    menuModel.add(subMenu2);
    
    DCMenuModel subMenu3 = new DCMenuModel("Sub Menu3");
    subMenu3.setIcon(new ImageIcon(DCMenu.class.getResource("/org/xito/launcher/images/globe_16x16.png")));
    subMenu3.add(new DefaultAction("3 SubItem 1"));
    subMenu3.add(new DefaultAction("3 SubItem 2"));
    subMenu3.add(new DefaultAction("3 SubItem 3"));
    subMenu3.add(new DefaultAction("3 SubItem 4"));
    subMenu2.add(subMenu3);
    
    final DCMenu menu = new DCMenu(menuModel);
    
    
    JFrame f1 = new JFrame("f1");
    System.out.println("f1:"+f1.hashCode());
    f1.setSize(200,200);
    f1.getContentPane().addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        if(SwingUtilities.isRightMouseButton(evt)) {
          menu.showPopup(evt.getPoint(), evt.getComponent());
        }
      }
    });
    
    f1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    f1.setVisible(true);
  }
  
}
