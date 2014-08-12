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
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;
import org.xito.dcf.property.editor.*;

/**
 *
 */
public class PropertyValueEditor extends AbstractCellEditor implements TableCellEditor, PropertyChangeListener {
   
   private PropertyEditor editor;
   private DefaultCellEditor cellEditor;
   private Class type;
   
   private Border selectedBorder;
   private Border emptyBorder;
   
   private HashMap editors;
   
   //
   public PropertyValueEditor() {
      editors = new HashMap();
      cellEditor = new DefaultCellEditor(new JTextField());
   }
   
   /**
    * Gets the Editor Component for the specified cell
    */
   public Component getTableCellEditorComponent(JTable table, Object value,
   boolean isSelected, int row, int column) {
      
      PropertyTableModel model = (PropertyTableModel)table.getModel();
      type = model.getPropertyType(row);
      
      if (type != null) {
         editor = (PropertyEditor)editors.get(type);
         if (editor == null) {
            PropertyEditor ed = model.getPropertyEditor(row);
            
            // Make a copy of this prop editor and register this as a
            // prop change listener.
            // We have to do this since we want a unique PropertyEditor
            // instance to be used for an editor vs. a renderer.
            if (ed != null) {
               Class editorClass = ed.getClass();
               try {
                  editor = (PropertyEditor)editorClass.newInstance();
                  editor.addPropertyChangeListener(this);
                  editors.put(type, editor);
               }
               catch (Exception ex) {
                  ex.printStackTrace();
               }
            }
         }
      } else {
         editor = null;
      }
      
      if (editor != null) {
         // Special case for the enumerated properties. Must reinitialize
         // to reset the combo box values.
         if (editor instanceof EditorSupport) {
            ((EditorSupport)editor).init(model.getPropertyDescriptor(row));
         }
         
         editor.setValue(value);
         
         Component comp = editor.getCustomEditor();
         if (comp != null) {
            comp.setEnabled(isSelected);
            
            if (comp instanceof JComponent) {
               if (isSelected) {
                  if (selectedBorder == null)
                     selectedBorder = BorderFactory.createLineBorder(table.getSelectionBackground(), 1);
                  
                  ((JComponent)comp).setBorder(selectedBorder);
               }
            }
            return comp;
         }
      }
      
      return cellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
   }
   
   /**
    * Get cellEditorValue for current editor
    */
   public Object getCellEditorValue() {
     Object obj = null;
      
     if (editor != null) {
         
         obj = editor.getValue();
      } else {
         obj = cellEditor.getCellEditorValue();
      }
      
      if (type != null && obj != null &&
      !type.isPrimitive() && !type.isAssignableFrom(obj.getClass())) {
         
         try {
            obj = type.newInstance();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      return obj;
   }
   
   /**
    * Property Change handler.
    *
    */
   public void propertyChange(PropertyChangeEvent evt) {
      stopCellEditing();
   }
   
}
