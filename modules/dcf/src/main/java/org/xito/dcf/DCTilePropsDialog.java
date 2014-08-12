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
import java.awt.event.*;
import javax.swing.*;

import org.xito.dcf.property.*;
/**
 *
 * @author  drichan
 */
public class DCTilePropsDialog extends JDialog implements ActionListener
{
  
  private DCTile tile;
  private JPanel topPanel;
  private JPanel bottomPanel;
  private JButton okBtn;
  private JButton cancelBtn;
  private JTabbedPane tabs;
  private PropertySheet tilePropsPanel;
  private JPanel actionPropsPanel;
  
  private int w = 300;
  private int h = 400;
  
  /** Creates new form DCTilePropsDialog */
  public DCTilePropsDialog(DCTile pTile)
  {
    super(pTile.getOwnerFrame(), true);
    tile = pTile;
    init();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   */
  private void init()
  {
    setTitle("Tile Properties");
    setModal(true);
    //setResizable(false);
    setSize(w,h);
    Dimension _size = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation((_size.width/2)-(w/2), (_size.height/2)-(h/2));
    
    topPanel = new TilePanel();
    bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    okBtn = new JButton("Ok");
    cancelBtn = new JButton("Cancel");
    cancelBtn.addActionListener(this);
    tabs = new JTabbedPane();
    tilePropsPanel = new PropertySheet(tile);
    actionPropsPanel = new JPanel();
    
    getContentPane().add(topPanel, BorderLayout.NORTH);
    
    addWindowListener(new java.awt.event.WindowAdapter()
    {
      public void windowClosing(java.awt.event.WindowEvent evt)
      {
        closeDialog(evt);
      }
    });
        
    bottomPanel.add(okBtn);
    bottomPanel.add(cancelBtn);
    
    getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    
    tabs.addTab("Tile", tilePropsPanel);
    
    tabs.addTab("Action", actionPropsPanel);
    
    getContentPane().add(tabs, BorderLayout.CENTER);
  }
  
  /** Closes the org.xito */
  private void closeDialog(java.awt.event.WindowEvent evt)
  {
    setVisible(false);
    dispose();
  }
  
  /**
   * ActionPerformed
   */
  public void actionPerformed(ActionEvent pEvent)
  {
    //Cancel
    if(pEvent.getSource() == cancelBtn) this.dispose();
  }
  
  protected class TilePanel extends JPanel
  {
    public TilePanel()
    {
      super(null);
    }
    
    public void paintComponent(Graphics g)
    {      
      //.drawImage(tile.getComponentDragImage(),5,5,null)
      tile.paint(g);
    }
    
    public Dimension getPreferredSize()
    {
      Dimension _dim = tile.getSize();
      _dim.height = _dim.height + 10;
      return _dim;
    }
  }
}
