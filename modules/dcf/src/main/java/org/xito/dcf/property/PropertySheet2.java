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
public class PropertySheet2 extends JPanel {
   
   private PropertyDescriptor[] descriptors;
   private BeanDescriptor beanDescriptor;
   private BeanInfo beanInfo;
   private Object bean;
   
   // Cached property editors.
   private HashMap propEditors;
   private HashMap originalValues;
   private HashMap beanProperties;
      
   private static final int ROW_HEIGHT = 20;
   private JScrollPane scrollPane;
   private JPanel panel;
   
   private Dimension buttonSize = new Dimension(70,30);
   private Insets buttonInsets = new Insets(0,0,0,0);
   
   
   /**
    * Constructor
    */
   public PropertySheet2(Object pBean) {
      super(new BorderLayout());
      
      panel = new JPanel(new GridBagLayout());
      scrollPane = new JScrollPane(panel);
      add(scrollPane, BorderLayout.CENTER);
      
      registerPropertyEditors();
      setBean(pBean);
   }
   
   /**
    * Method which registers property editors for types.
    */
   private void registerPropertyEditors() {
      
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
   protected void setBean(Object bean) {
      
      this.bean = bean;
      panel.removeAll();
      propEditors.clear();
      originalValues.clear();
      beanProperties.clear();
      
      setupEditors();
   }
   
   private void setupEditors() {
      
      try {
         beanInfo = Introspector.getBeanInfo(bean.getClass());
         beanDescriptor = beanInfo.getBeanDescriptor();
         descriptors = beanInfo.getPropertyDescriptors();
         for(int i=0;i<descriptors.length;i++) {
            addProperty(i, descriptors[i]);
         }
      }
      catch(IntrospectionException exp) {
         exp.printStackTrace();
      }
   }
   
   private void addProperty(int row, PropertyDescriptor propDesc) {
      
      //originalValues.clear();
      beanProperties.put(propDesc.getName(), propDesc);
      
      
      GridBagConstraints lblcon = new GridBagConstraints();
      lblcon.gridx = 0;
      lblcon.gridy = row;
      lblcon.anchor = lblcon.NORTHWEST;
      //lblcon.gridheight = ROW_HEIGHT;
      
      System.out.println("Adding property:"+propDesc.getDisplayName());
      JLabel lbl = new JLabel(propDesc.getDisplayName(), JLabel.LEFT);
      panel.add(lbl, lblcon);
      
      GridBagConstraints edcon = new GridBagConstraints();
      edcon.gridx = 1;
      edcon.gridy = row;
      edcon.fill  = edcon.BOTH;
      edcon.weightx = 1;
      //edcon.gridheight = ROW_HEIGHT;
      
      PropertyEditor editor = PropertyEditorManager.findEditor(propDesc.getPropertyType());
      propEditors.put(propDesc.getName(), editor);
            
      //panel.add(, edcon);
   }
   
   /**
    * This returns a set of property names that have been modified
    * Use the getOriginalValue method to get the value of the properties
    * before they were modified
    * @return names of properies that have changed
    */
   public Set getModifiedPropNames() {
      //return changedProps;
      return null;
   }
   
   /**
    * Resets the Beans property Values to their Originals
    */
   public void resetOriginalValues() {
      //tableModel.resetOriginalValues(changedProps);
   }
   
   /**
    * A customizer org.xito which takes a Component which implements the
    * customizer interface.
    */
   private class CustomizerDialog extends JDialog implements ActionListener {
      
      public CustomizerDialog(Component comp) {
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
      
      public void actionPerformed(ActionEvent evt) {
         this.dispose();
      }
      
   }
}