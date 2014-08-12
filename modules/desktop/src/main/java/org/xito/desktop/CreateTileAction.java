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
import java.awt.event.*;
import javax.swing.*;
import java.net.*;

import org.xito.dcf.*;
import org.xito.blx.*;

/**
 *
 * @author  Deane
 */
public class CreateTileAction extends DefaultAction {
  
  
  /** Creates a new instance of a Create Tile Task */
  public CreateTileAction() {
    super("New Tile");
  }
  
  /**
   * The action has been performed
   * @param ActionEvent
   */
  public void actionPerformed(ActionEvent evt) {
    
    JDialog dialog = DesktopService.getDefaultService().getDesktopDialog("Create Tile", true);
    ContentPanel contentPanel = new ContentPanel(dialog);
    dialog.setContentPane(contentPanel);
    dialog.pack();
    DesktopService.getDefaultService().centerWindow(dialog);
    dialog.setVisible(true);
    
    //place tile on Desktop
    DCTile tile = contentPanel.getTile();
    DesktopService.getDefaultService().getCurrentDesktop().addDesktopComponent(tile);
  }
  
  public static class ContentPanel extends JPanel implements ActionListener {
    
    private JComboBox cmdList;
    private JTextField nameTF;
    private JTextField titleTF;
    private JButton okBtn;
    private JButton cancelBtn;
    private JDialog owner;
    
    private DCTile tile;
    
    public ContentPanel(JDialog owner) {
      super(new BorderLayout());
      this.owner = owner;
      init();
    }
    
    private void init() {
      
      JPanel centerPanel = new JPanel();
      GridBagLayout layout = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      centerPanel.setLayout(layout);
      
      int lbl_x = 0;
      int comp_x = 1;
      int y = 0;
      JLabel typeLbl = new JLabel("Type:", JLabel.RIGHT);
      c.gridx = lbl_x;
      c.gridy = y++;
      c.fill = c.HORIZONTAL;
      c.insets = new Insets(2,2,2,2);
      centerPanel.add(typeLbl, c);
      //cmdList = CmdManager.getDefaultManager().getCmdComboBox();
      cmdList = new JComboBox();
      c.gridx = comp_x;
      centerPanel.add(cmdList, c);
      
      JLabel nameLbl = new JLabel("Name:", JLabel.RIGHT);
      c.gridx = lbl_x;
      c.gridy = y++;
      centerPanel.add(nameLbl, c);
      nameTF = new JTextField();
      c.gridx = comp_x;
      centerPanel.add(nameTF, c);
      
      JLabel titleLbl = new JLabel("Title:", JLabel.RIGHT);
      c.gridx = lbl_x;
      c.gridy = y++;
      centerPanel.add(titleLbl, c);
      titleTF = new JTextField();
      c.gridx = comp_x;
      centerPanel.add(titleTF, c);
      
      okBtn = new JButton("Ok");
      okBtn.addActionListener(this);
      cancelBtn = new JButton("Cancel");
      cancelBtn.addActionListener(this);
      
      JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      southPanel.add(okBtn);
      southPanel.add(cancelBtn);
      
      add(centerPanel, BorderLayout.CENTER);
      add(southPanel, BorderLayout.SOUTH);
    }
    
    /** Invoked when an action occurs.
     *
     */
    public void actionPerformed(ActionEvent e) {
      if(e.getSource() == cancelBtn) {
        tile = null;
        owner.dispose();
      }
      else if(e.getSource() == okBtn) {
        buildTile();
        owner.dispose();
      }
    }
    
    /**
     * Build the Tile
     */
    private void buildTile() {
       
      try {
        tile = new DCTile();
        /*
        ICmdFactory factory = (ICmdFactory)cmdList.getSelectedItem();
        ICmdAction action = factory.getCmdAction(null);
        if(nameTF.getText().length() != 0) {
          tile.setShortTitle(nameTF.getText());
          action.putValue(ICmdAction.NAME, nameTF.getText());
        }
        if(titleTF.getText().length() != 0) {
          tile.setTitle(titleTF.getText());
          action.putValue(ICmdAction.SHORT_DESCRIPTION, titleTF.getText());
        }
        
        tile.setAction(action);
         */
      }
      catch(Exception exp) {
        exp.printStackTrace();
      }
    }
    
    /**
     * Get the Tile created from this Dialog
     *
     */
    public DCTile getTile() {
      return tile;
    }
    
  }
}



