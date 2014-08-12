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

package org.xito.desktop.menu_designer;

import java.awt.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

import org.xito.desktop.*;

/**
 *
 * @author  Deane
 */
public class DesktopMenuDesigner {
  
  private static DesktopMenuDesigner singleton;
  private static final Dimension DEFAULT_SIZE = new Dimension(400,400);
  
  private JFrame frame;
  private JTree tree;
  private MenuTreeModel model;
  
  /**
   * DesktopMenuDesigner Constructor
   *
   */
  private DesktopMenuDesigner() {
    init();
  }
  
  /**
   * Build the Menu Designer 
   */
  private void init() {
    
    //Setup Frame
    frame = new JFrame("Desktop Menu Designer");
    frame.setSize(DEFAULT_SIZE);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    
    //Center Frame
    DesktopService.getDefaultService().centerWindow(frame);
    
    tree = new JTree();
    tree.setRootVisible(true);
    frame.setContentPane(tree);
  }
  
  /**
   * Create the Model based on the RootModel
   *
   */
  private void loadCurrentRoot() {
     
    model = new MenuTreeModel(DesktopMenuModel.getRootModel());
    tree.setModel(model);
    model.refresh();
  }
  
  /**
   * Show the Designer
   */
  public void show() {
    frame.setVisible(true);
  }
  
  /**
   * Get the Instance of the Menu Designer
   * There should only be one instance
   */
  public static DesktopMenuDesigner getInstance() {
    if(singleton == null) {
      singleton = new DesktopMenuDesigner();
      org.xito.desktop.Desktop desktop = DesktopService.getDefaultService().getCurrentDesktop();
      
      //If no Desktop is Loaded then Show Wizard
      if(desktop == null) {
        //Goto Wizard open or New
      }
      //Get the Current Default Root
      else {
        singleton.loadCurrentRoot();
      }
    }
    
    return singleton;
  }
  
  /**
   * Main method to start the Menu Designer
   *
   */
  public static void main(String args[]) {
    
    DesktopMenuDesigner designer = getInstance();
    designer.show();
  }
}
