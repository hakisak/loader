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
package org.xito.dcf.property.editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class NumberEditor extends EditorSupport {
  
  private JTextField textfield;
  
  public NumberEditor() {
    textfield = new JTextField();
    textfield.setDocument(new NumberDocument());
    textfield.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        setValue(new Float(textfield.getText()));
      }
    });
    
    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.add(textfield);
  }
  
  /**
   * Get Value from the Editor
   */
  public Object getValue() {
    if(super.getValue() == null || super.getValue().equals(textfield.getText()))
      return super.getValue();
    else
      return textfield.getText();
  }
    
  /**
   * Set the Value into the Editor
   */
  public void setValue(Object value) {
    super.setValue(value);
    if (value != null) {
      textfield.setText(value.toString());
    }
  }
  
}