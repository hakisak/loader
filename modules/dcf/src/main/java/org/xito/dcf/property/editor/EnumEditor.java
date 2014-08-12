package org.xito.dcf.property.editor;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;

public class EnumEditor extends EditorSupport implements ActionListener {
  
  public JComboBox combobox;
  
  public void setValue(Object value) {
    super.setValue(value);
    
    // Set combo box if it's a new value. We want to reduce number
    // of extraneous events.
    EnumeratedItem item = (EnumeratedItem)combobox.getSelectedItem();
    if (value != null && !value.equals(item.getValue())) {
      for (int i = 0; i < combobox.getItemCount(); ++i ) {
        item = (EnumeratedItem)combobox.getItemAt(i);
        if (item.getValue().equals(value)) {
          // XXX - hack! Combo box shouldn't call action event
          // for setSelectedItem!!
          combobox.removeActionListener(this);
          combobox.setSelectedItem(item);
          combobox.addActionListener(this);
          return;
        }
      }
    }
  }
  
  /**
   * Initializes this property editor with the enumerated items. Instances
   * can be shared but there are issues.
   * <p>
   * This method does a lot of jiggery pokery since enumerated
   * types are unlike any other homogenous types. Enumerated types may not
   * represent the same set of values.
   * <p>
   * One method would be to empty the list of values which would have the side
   * effect of firing notification events. Another method would be to recreate
   * the combobox.
   */
  public void init(PropertyDescriptor descriptor) {
    
    Enumeration attNames = descriptor.attributeNames();
    ArrayList values = new ArrayList();
    while(attNames.hasMoreElements()) { values.add(attNames.nextElement()); }
    
    if (values.size()>1) {
      if (combobox == null) {
        combobox = new JComboBox();
        
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(combobox);
      } else {
        // Remove action listener to reduce extra events.
        combobox.removeActionListener(this);
        combobox.removeAllItems();
      }
      
      for ( int i = 0; i < values.size(); i++) {
        String name = (String)values.get(i);
        combobox.addItem(new EnumeratedItem((Integer)descriptor.getValue(name), name));
      }
      
      combobox.addActionListener(this);
    }
  }
  
  /**
   * Event is set when a combo selection changes.
   */
  public void actionPerformed(ActionEvent evt) {
    EnumeratedItem item = (EnumeratedItem)combobox.getSelectedItem();
    if (item != null && !getValue().equals(item.getValue())) {
      setValue(item.getValue());
    }
  }
  
  /**
   * Object which holds an enumerated item plus its label.
   */
  private class EnumeratedItem {
    private Integer value;
    private String name;
    
    public EnumeratedItem(Integer value, String name) {
      this.value = value;
      this.name = name;
    }
    
    public Integer getValue() {
      return value;
    }
    
    public String toString() {
      return name;
    }
  }
}