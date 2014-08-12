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
import java.lang.reflect.*;
import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;

import org.xito.dcf.property.editor.*;

public class PropertyTableModel extends AbstractTableModel {
   
   private PropertyDescriptor[] descriptors;
   private BeanDescriptor beanDescriptor;
   private BeanInfo info;
   private Object bean;
   
   // Cached property editors.
   private HashMap propEditors;
   private HashMap originalValues;
   private HashMap beanProperties;
   
   private static final int NUM_COLUMNS = 2;
   
   public static final int COL_NAME = 0;
   public static final int COL_VALUE = 1;
   
   // View options
   public static final int VIEW_ALL = 0;
   
   /**
    * Create the Model for Editing a Beans Properties
    * Call setObject to set the object to modify
    */
   public PropertyTableModel() {
      propEditors = new HashMap();
   }
   
   /**
    * Create the Model for Editing a Beans Properties
    * @param bean to modify
    */
   public PropertyTableModel(Object bean) {
      this();
      setObject(bean);
   }
   
   /**
    * Set the table model to represents the properties of the object.
    */
   public void setObject(Object bean) {
      this.bean = bean;
      
      //Get this Beans Descriptors
      try {
         if(bean instanceof javax.swing.JComponent) {
            info = Introspector.getBeanInfo(bean.getClass(), javax.swing.JComponent.class);
         }
         else if(bean instanceof java.awt.Component) {
            info = Introspector.getBeanInfo(bean.getClass(), java.awt.Component.class);
         }
         else {
            info = Introspector.getBeanInfo(bean.getClass(), Object.class);
         }
         
         if (info != null) {
            //Get Descriptors
            descriptors = info.getPropertyDescriptors();
            beanDescriptor = info.getBeanDescriptor();
            
            //Store Original Values
            storeOriginals();
         }
      }
      catch(Exception _exp) {
         _exp.printStackTrace();
      }
   }
   
   /**
    * Store the Original Values for the Bean
    */
   private void storeOriginals() {
      originalValues = new HashMap();
      beanProperties = new HashMap();
      for(int i=0;i<descriptors.length;i++) {
         String _name = descriptors[i].getName();
         beanProperties.put(_name, descriptors[i]);
         Method _getter = descriptors[i].getReadMethod();
         Object _value = null;
         try {
            _value = _getter.invoke(bean, null);
         }
         catch(Exception _exp) {
            //can't get value set to null
            _value = null;
            _exp.printStackTrace();
         }
         
         //Place in Map
         originalValues.put(_name, _value);
      }
   }
   
   /**
    * Reset the Values of the Bean to their originals
    * @param Set of property Names to revert
    */
   public void resetOriginalValues(Set propNames) {
      //If null use all Names
      if(propNames == null) {
         propNames = new HashSet();
         for(int i=0;i<descriptors.length;i++) {
            propNames.add(descriptors[i].getName());
         }
      }
      
      //For Each Property Reset its Value
      Iterator _names = propNames.iterator();
      while(_names.hasNext()) {
         String _name = (String)_names.next();
         Object _value = originalValues.get(_name);
         Method _setter = ((PropertyDescriptor)beanProperties.get(_name)).getWriteMethod();
         try {
            _setter.invoke(bean, new Object[]
            { _value });
         }
         catch (Exception _exp) {
            _exp.printStackTrace();
         }
      }
   }
   
   /**
    * Return the current object that is represented by this model.
    */
   public Object getObject() {
      return bean;
   }
   
   /**
    * Get row count (total number of properties shown)
    */
   public int getRowCount() {
      if (descriptors == null) {
         return 0;
      }
      
      return descriptors.length;
   }
   
   /**
    * Get column count (2: name, value)
    */
   public int getColumnCount() {
      return NUM_COLUMNS;
   }
   
   /**
    * Check if given cell is editable
    * @param row table row
    * @param col table column
    */
   public boolean isCellEditable(int row, int col) {
      if (col == COL_VALUE && descriptors != null) {
         return (descriptors[row].getWriteMethod() == null) ? false : true;
      } else {
         return false;
      }
   }
   
   /**
    * Get text value for cell of table
    * @param row table row
    * @param col table column
    */
   public Object getValueAt(int row, int col) {
      
      Object value = null;
      
      if (col == COL_NAME) {
         value = descriptors[row].getDisplayName();
      } else {
         // COL_VALUE is handled
         Method getter = descriptors[row].getReadMethod();
         
         if (getter != null) {
            Class[] paramTypes = getter.getParameterTypes();
            Object[] args = new Object[paramTypes.length];
            
            try {
               value = getter.invoke(bean, args);
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         }
         
      }
      return value;
   }
   
   /**
    * Set the value of the Values column.
    */
   public void setValueAt(Object value, int row, int column) {
      
      if (column != COL_VALUE || descriptors == null
      || row > descriptors.length) {
         return;
      }
      
      //Compare with old value
      Object _oldValue = getValueAt(row, column);
      if(value == _oldValue) return;
      if(value != null && _oldValue !=null && value.equals(_oldValue)) return;
      
      Method setter = descriptors[row].getWriteMethod();
      if (setter != null) {
         try {
            setter.invoke(bean, new Object[]
            { value });
            fireTableChanged(new TableModelEvent(this, row));
         } catch (IllegalArgumentException ex) {
            // XXX - handle better
            System.out.println("Setter: " + setter + "\nArgument: " + value.getClass().toString());
            System.out.println("Row: " + row + " Column: " + column);
            ex.printStackTrace();
            System.out.println("\n");
         } catch (IllegalAccessException ex2) {
            // XXX - handle better
            System.out.println("Setter: " + setter + "\nArgument: " + value.getClass().toString());
            System.out.println("Row: " + row + " Column: " + column);
            ex2.printStackTrace();
            System.out.println("\n");
         } catch (InvocationTargetException ex3) {
            // XXX - handle better
            System.out.println("Setter: " + setter + "\nArgument: " + value.getClass().toString());
            System.out.println("Row: " + row + " Column: " + column);
            ex3.printStackTrace();
            System.out.println("\n");
         }
      }
   }
   
   /**
    * Returns the Java type info for the property at the given row.
    */
   public Class getPropertyType(int row) {
      return descriptors[row].getPropertyType();
   }
   
   /**
    * Returns the PropertyDescriptor for the row.
    */
   public PropertyDescriptor getPropertyDescriptor(int row) {
      return descriptors[row];
   }
   
   /**
    * Returns a new instance of the property editor for a given class. If an
    * editor is not specified in the property descriptor then it is looked up
    * in the PropertyEditorManager.
    */
   public PropertyEditor getPropertyEditor(int row) {
      Class cls = descriptors[row].getPropertyEditorClass();
      
      PropertyEditor editor = null;
      
      if (cls != null) {
         try {
            editor = (PropertyEditor)cls.newInstance();
         } catch (Exception ex) {
            // XXX - debug
            System.out.println("PropertyTableModel: Instantiation exception creating PropertyEditor");
         }
      } else {
         // Look for a registered editor for this type.
         Class type = getPropertyType(row);
         if (type != null) {
            editor = (PropertyEditor)propEditors.get(type);
            
            if (editor == null) {
               // Load a shared instance of the property editor.
               editor = PropertyEditorManager.findEditor(type);
               if (editor != null)
                  propEditors.put(type, editor);
            }
            
            if (editor == null) {
               // Use the editor for Object.class
               editor = (PropertyEditor)propEditors.get(Object.class);
               if (editor == null) {
                  editor = PropertyEditorManager.findEditor(Object.class);
                  if (editor != null)
                     propEditors.put(Object.class, editor);
               }
               
            }
         }
      }
      
      //Initialize our Editors
      if(editor instanceof EditorSupport) {
         ((EditorSupport)editor).init(descriptors[row]);
      }
      
      return editor;
   }
   
   /**
    * Returns a flag indicating if the encapsulated object has a customizer.
    */
   public boolean hasCustomizer() {
      if (beanDescriptor != null) {
         Class cls = beanDescriptor.getCustomizerClass();
         return (cls != null);
      }
      
      return false;
   }
   
   /**
    * Gets the customizer for the current object.
    * @return New instance of the customizer or null if there isn't a customizer.
    */
   public Component getCustomizer() {
      Component customizer = null;
      
      if (beanDescriptor != null) {
         Class cls = beanDescriptor.getCustomizerClass();
         
         if (cls != null) {
            try {
               customizer = (Component)cls.newInstance();
            } catch (Exception ex) {
               // XXX - debug
               System.out.println("PropertyTableModel: Instantiation exception creating Customizer");
            }
         }
      }
      
      return customizer;
   }
}
