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

package org.xito.dcf.property;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.util.*;
import org.xito.dcf.property.editor.*;

/**
 *
 * @author  drichan
 * @version
 */
public class PropertySheet extends JPanel implements TableModelListener
{
  private Object bean;        // Current Bean.
  private Stack beanStack;    // Stack of beans for walking bean hierarchy.
  
  private JTable table;
  private PropertyColumnModel columnModel;
  private PropertyTableModel tableModel;
  private HashSet changedProps = new HashSet();
  private HashMap originalValues = new HashMap();
  
  // UI for the property control panel.
  private JLabel nameLabel;
  private JComboBox viewCombo;
  private JButton up;
  private JButton down;
  private JButton add;
  private JButton cust;
  
  private static final int ROW_HEIGHT = 20;
  
  // View options.
  
  private Dimension buttonSize = new Dimension(70,30);
  private Insets buttonInsets = new Insets(0,0,0,0);
  
  /**
   * Constructor
   */
  public PropertySheet(Object pBean)
  {
    super(new BorderLayout());
    bean = pBean;
    registerPropertyEditors();
    
    //Create Models
    tableModel = new PropertyTableModel(bean);
    tableModel.addTableModelListener(this);
    columnModel = new PropertyColumnModel();
    
    //Create Table
    table = new JTable(tableModel, columnModel);
    table.setRowHeight(ROW_HEIGHT);
    table.setAutoResizeMode(table.AUTO_RESIZE_LAST_COLUMN);
    
    add(new JScrollPane(table), BorderLayout.CENTER);
  }
  
  public boolean isTableEditing() {
     if(table != null) {
        return table.isEditing();
     }
     else {
        return false;
     }
  }
  
  public void stopEditing() {
     if(table != null) {
        TableCellEditor editor = table.getCellEditor();
        if ( null != editor ) editor.stopCellEditing();
     }
  }
  
  /**
   * Handle the add gesture. Informs prop change listener to add the selected
   * current property sheet component.
   */
  public void handleAddAction()
  {
    int index = table.getSelectedRow();
    if (index != -1)
    {
      Object obj = tableModel.getValueAt(index, PropertyTableModel.COL_VALUE);
      
      if (obj != null && !(obj instanceof Component))
      {
        String message = obj.getClass().getName();
        message += " sent to design panel";
      }
    }
  }
  
  /**
   * Handle the customizer action. Will display a customizer in a org.xito
   */
  public void handleCustomizerAction()
  {
    Component comp = tableModel.getCustomizer();
    
    if (comp != null)
    {
      CustomizerDialog dlg = new CustomizerDialog(comp);
      dlg.setVisible(true);
    }
  }
  
  /**
   * A customizer org.xito which takes a Component which implements the
   * customizer interface.
   */
  private class CustomizerDialog extends JDialog implements ActionListener
  {
    
    public CustomizerDialog(Component comp)
    {
      super(new JFrame(), "Customizer Dialog");
      
      Customizer customizer = (Customizer)comp;
      customizer.setObject(bean);
      
      JPanel okpanel = new JPanel();
      //okpanel.add(CommonUI.createButton(CommonUI.BUTTONTEXT_OK, this, CommonUI.MNEMONIC_OK));
      Container pane = getContentPane();
      pane.add(comp, BorderLayout.CENTER);
      pane.add(okpanel, BorderLayout.SOUTH);
      pack();
      
      //CommonUI.centerComponent(this, PropertyPane.this);
    }
    
    public void actionPerformed(ActionEvent evt)
    {
      this.dispose();
    }
    
  }
  
  /**
   * Method which registers property editors for types.
   */
  private void registerPropertyEditors()
  {
    //Color, Font, Border
    PropertyEditorManager.registerEditor(Color.class, ColorEditor.class);
    PropertyEditorManager.registerEditor(Font.class, FontEditor.class);
    PropertyEditorManager.registerEditor(Border.class, BorderEditor.class);
    
    //Booleans
    PropertyEditorManager.registerEditor(Boolean.class, BooleanEditor.class);
    PropertyEditorManager.registerEditor(boolean.class, BooleanEditor.class);
    
    //Integers
    PropertyEditorManager.registerEditor(Integer.class, IntegerEditor.class);
    PropertyEditorManager.registerEditor(int.class, IntegerEditor.class);
    
    //Floats
    PropertyEditorManager.registerEditor(Float.class, NumberEditor.class);
    PropertyEditorManager.registerEditor(float.class, NumberEditor.class);
    
    //Dimension, Rectangle
    PropertyEditorManager.registerEditor(java.awt.Dimension.class, DimensionEditor.class);
    PropertyEditorManager.registerEditor(java.awt.Rectangle.class, RectangleEditor.class);
    PropertyEditorManager.registerEditor(java.awt.Insets.class, InsetsEditor.class);
    
    //String or Object
    PropertyEditorManager.registerEditor(String.class, StringEditor.class);
    PropertyEditorManager.registerEditor(Object.class, ObjectEditor.class);
  }
  
  
  /**
   * Sets the PropertyPane to show the properties of the named bean.
   */
  protected void setBean(Object bean)
  {
    this.bean = bean;
    
    if (bean != null)
    {
      tableModel.setObject(bean);
      tableModel.fireTableDataChanged();
    }
  }
  
  /**
   * This returns a set of property names that have been modified
   * Use the getOriginalValue method to get the value of the properties
   * before they were modified
   * @return names of properies that have changed
   */
  public Set getModifiedPropNames()
  {
    return changedProps;
  }
  
  /**
   * Resets the Beans property Values to their Originals
   */
  public void resetOriginalValues()
  {
    tableModel.resetOriginalValues(changedProps);
  }
  
  /**
   * Table Model Listener methods
   */
  public void tableChanged(TableModelEvent evt)
  {
    PropertyDescriptor _desc = tableModel.getPropertyDescriptor(evt.getFirstRow());
    changedProps.add(_desc.getName());
    
    /*
    // Adjust the preferred height of the row to the the same as
    // the property editor.
    table.setRowHeight(ROW_HEIGHT);
     
    PropertyEditor editor;
    Component comp;
    Dimension prefSize;
     
    for (int i = 0; i < table.getRowCount(); i++)
    {
      editor = tableModel.getPropertyEditor(i);
      if (editor != null)
      {
        comp = editor.getCustomEditor();
        if (comp != null)
        {
          prefSize = comp.getPreferredSize();
          if (prefSize.height > ROW_HEIGHT)
          {
            table.setRowHeight(i, prefSize.height);
          }
        }
      }
    }
     */
  }
}