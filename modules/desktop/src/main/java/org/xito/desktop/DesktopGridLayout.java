// Copyright (C) 2002 http://ocd.sourceforge.net
//
// This file is part of OCD.
//
// OCD is free software; you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License (LGPL)
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// OCD is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with OCD.
//
// Information about the GNU LGPL License can be obtained at
// http://www.gnu.org/licenses/

package org.xito.desktop;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.xito.dcf.*;

/**
 *
 * @author  Deane
 */
public class DesktopGridLayout extends DefaultDesktopLayout {

  public static final int FLOW_SW = 1;
  public static final int FLOW_SE = 2;
  public static final int FLOW_NW = 3;
  public static final int FLOW_NE = 4;
  
  int cellSize = 72;
  int flowDirection = FLOW_SW;
  boolean autoSnap = false;
  boolean autoPosition = true;
  Dimension desktopSize;
  Rectangle grid[][];
  Dimension gridDim;
    
  /** Creates a new instance of DesktopGridLayout */
  public DesktopGridLayout() {
    desktopSize = DesktopService.getDefaultService().getDesktopSize();
  }
  
  public void setAutoSnap(boolean auto) {
    autoSnap = auto;
  }
  
  public boolean getAutoSnap() {
    return autoSnap;
  }
  
  public void setAutoPosition(boolean auto) {
    autoPosition = auto;
  }
  
  public boolean getAutoPosition() {
    return autoPosition;
  }
  
  public void setFlowDirection(int flow) {
    flowDirection = flow;
    
    if(flowDirection == FLOW_SW) createSWGrid();
    else if(flowDirection == FLOW_SE) createSEGrid();
    else if(flowDirection == FLOW_NW) createNWGrid();
    else if(flowDirection == FLOW_NE) createNEGrid();
  }
  
  public int getFlowDirection() {
    return flowDirection;
  }
  
  public void setDesktop(Desktop desktop) {
    super.setDesktop(desktop);
    desktopSize = DesktopService.getDefaultService().getDesktopSize();
    
    //Reset Flow Direction
    setFlowDirection(flowDirection);
  }
    
  /** After a component is added to the Desktop the Desktop will call its
   * DesktopLayout to position the comp in the correct Location.
   * @param comp DCComponent to Position on the Desktop
   *
   */
  public void positionComp(DCComponent comp) {
    
    //Ignore Master Tiles
    if(comp instanceof MasterTile) return;
    
    Point loc = comp.getLocation();
    if(autoPosition && loc.x == 0 && loc.y == 0) { 
      Rectangle rect = findFirstEmptyCell(comp.getSize());
      comp.setLocation(rect.getLocation());
    }
  }
  
  private void createSWGrid() {
    int gs = cellSize;
    int cols = (int)Math.floor(desktopSize.width / gs);
    int rows = (int)Math.floor(desktopSize.height / gs);
    gridDim = new Dimension(cols, rows);
    
    grid = new Rectangle[cols][rows];
    for(int c=0;c<cols;c++) {
      for(int r=0;r<rows;r++) {
        grid[c][r] = new Rectangle(gs,gs);
        grid[c][r].setLocation(desktopSize.width - ((c+1)*gs), (gs * r)); 
      }
    }
  }

  private void createSEGrid() {
    int gs = cellSize;
    int cols = (int)Math.floor(desktopSize.width / gs);
    int rows = (int)Math.floor(desktopSize.height / gs);
    gridDim = new Dimension(cols, rows);
    
    grid = new Rectangle[cols][rows];
    for(int c=0;c<cols;c++) {
      for(int r=0;r<rows;r++) {
        grid[c][r] = new Rectangle(gs,gs);
        grid[c][r].setLocation((gs * c), (gs * r)); 
      }
    }
  }

  private void createNWGrid() {
    int gs = cellSize;
    int cols = (int)Math.floor(desktopSize.width / gs);
    int rows = (int)Math.floor(desktopSize.height / gs);
    gridDim = new Dimension(cols, rows);
    
    grid = new Rectangle[cols][rows];
    for(int c=0;c<cols;c++) {
      for(int r=0;r<rows;r++) {
        grid[c][r] = new Rectangle(gs,gs);
        grid[c][r].setLocation(desktopSize.width - ((c+1)*gs), desktopSize.height - ((r+1)*gs)); 
      }
    }
  }
  
  private void createNEGrid() {
    int gs = cellSize;
    int cols = (int)Math.floor(desktopSize.width / gs);
    int rows = (int)Math.floor(desktopSize.height / gs);
    gridDim = new Dimension(cols, rows);
    
    grid = new Rectangle[cols][rows];
    for(int c=0;c<cols;c++) {
      for(int r=0;r<rows;r++) {
        grid[c][r] = new Rectangle(gs,gs);
        grid[c][r].setLocation((gs * c), desktopSize.height - ((r+1)*gs)); 
      }
    }
  }
  
  private Rectangle findFirstEmptyCell(Dimension compSize){
        
    //This approach will get slower with more items being added to the desktop
    //A faster algorithym may be possible
    for(int c=0;c<grid.length;c++) {
      for(int r=0;r<grid[c].length;r++) {
        Rectangle rect = grid[c][r];
        DCComponent comps[] = getDesktop().getDesktopComponents(rect);
        if(isCellInCorner(rect)) continue;
        if(willCompFit(rect, compSize) == false) continue;
        if(comps.length == 0) return rect;
      }
    }
    
    return grid[0][0];
  }
  
  private boolean willCompFit(Rectangle rect, Dimension size) {
    if(rect.x + size.width > desktopSize.width) return false;
    if(rect.y + size.height > desktopSize.height) return false;
    
    return true;
  }
  
  private boolean isCellInCorner(Rectangle rect) {
    int widthx = (int)(desktopSize.width / 3);
    int heightx = (int)(desktopSize.height / 3);
    
    if(rect.y < cellSize) {
      if(rect.x < widthx || rect.x > (desktopSize.width - widthx))
        return true;
    }
    
    if(rect.x < cellSize) {
      if(rect.y < heightx || rect.y > (desktopSize.height - heightx))
        return true;
    }
    
    if(rect.x > (desktopSize.width - cellSize + 2)) {
      if(rect.y < heightx || rect.y > (desktopSize.height - heightx)) 
        return true;
    }
        
    if(rect.y > (desktopSize.height - cellSize + 2)) {
      if(rect.x < widthx || rect.x > (desktopSize.width - widthx))
        return true;
    }
        
    return false;
  }
  
  public static void main(String args[]) {
    final DesktopImpl desk = new DesktopImpl();
    DesktopGridLayout layout = new DesktopGridLayout();
    layout.setFlowDirection(layout.FLOW_SE);
    desk.setDesktopLayout(layout);
    DesktopService.getDefaultService().setCurrentDesktop(desk); 
    
    JFrame f= new JFrame("Test");
    JButton btn = new JButton("Create Tile");
    f.setContentPane(btn);
    btn.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt) {
        //DCTile tile = new DCTile();
        //tile.setTitle("This is a Test");
        //desk.addDesktopComponent(tile);
        desk.addDesktopComponent(new StickyNote());
      }
    });
    
    f.pack();
    f.setVisible(true);
  }
}
