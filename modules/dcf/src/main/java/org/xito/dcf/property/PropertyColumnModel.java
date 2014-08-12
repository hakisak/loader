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
import java.beans.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class PropertyColumnModel extends DefaultTableColumnModel
{
  
  private final static String COL_LABEL_PROP = "Property";
  private final static String COL_LABEL_DESC = "Description";
  private final static String COL_LABEL_VALUE = "Value";
  
  private static final int minColWidth = 150;
  
  public PropertyColumnModel()
  {
    // Configure the columns and add them to the model
    TableColumn column;
    
    // Property
    column = new TableColumn(0);
    column.setHeaderValue(COL_LABEL_PROP);
    column.setPreferredWidth(minColWidth);
    column.setCellRenderer(new PropertyNameRenderer());
    addColumn(column);
    
    // Value
    column = new TableColumn(1);
    column.setHeaderValue(COL_LABEL_VALUE);
    column.setPreferredWidth(minColWidth * 2);
    column.setCellEditor(new PropertyValueEditor());
    column.setCellRenderer(new PropertyValueRenderer());
    addColumn(column);
  }
  
  /**
   * Renders the name of the property. Sets the short description of the
   * property as the tooltip text.
   */
  class PropertyNameRenderer extends DefaultTableCellRenderer
  {
    
    /**
     * Get UI for current editor, including custom editor button
     * if applicable.
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column)
    {
      PropertyTableModel model = (PropertyTableModel)table.getModel();
      PropertyDescriptor desc = model.getPropertyDescriptor(row);
      
      setToolTipText(desc.getShortDescription());
      setBackground(UIManager.getColor("control"));
      
      return super.getTableCellRendererComponent(table, value,
      isSelected, hasFocus, row, column);
    }
  }
  
  
  /**
   * Renderer for a value with a property editor or installs the default cell rendererer.
   */
  class PropertyValueRenderer implements TableCellRenderer
  {
    
    private DefaultTableCellRenderer renderer;
    private PropertyEditor editor;
    
    private Hashtable editors;
    private Class type;
    
    private Border selectedBorder;
    private Border emptyBorder;
    
    public PropertyValueRenderer()
    {
      renderer = new DefaultTableCellRenderer();
      editors = new Hashtable();
    }
    
    /**
     * Get UI for current editor, including custom editor button
     * if applicable.
     * XXX - yuck! yuck! yuck!!!!
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column)
    {
      
      PropertyTableModel model = (PropertyTableModel)table.getModel();
      type = model.getPropertyType(row);
      
      //Get the Editor
      if (type != null)
      {
        editor = (PropertyEditor)editors.get(type);
        if (editor == null)
        {
          editor = model.getPropertyEditor(row);
          
          if (editor != null) editors.put(type, editor);
        }
      }
      else
      {
        editor = null;
      }
      
      if (editor != null)
      {
        editor.setValue(value);
        
        Component comp = editor.getCustomEditor();
        if (comp != null)
        {
          comp.setEnabled(isSelected);
                    
          return comp;
        }
      }
      
      return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
    
    /**
     * Retrieves the property editor for this value.
     */
    public PropertyEditor getPropertyEditor()
    {
      return editor;
    }
  }
  
}
