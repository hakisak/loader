package org.xito.dcf.property.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;

import javax.swing.*;


public class IntegerEditor extends EditorSupport {
  
  // Property editor to use if the Integer represents an Enumerated type.
  private EnumEditor enumEditor = new EnumEditor();
  
  private JTextField textfield;
  
  private boolean isEnumeration = false;
  
  public void setValue(Object value) {
    if (isEnumeration) {
      enumEditor.setValue(value);
    } else {
      super.setValue(value);
      
      if (value != null) {
        textfield.setText(value.toString());
      }
    }
  }
  
  public Object getValue() {
    if (isEnumeration) {
      return enumEditor.getValue();
    } else {
      return super.getValue();
    }
  }
  
  /**
   * Must overloade the PropertyChangeListener registration because
   * this class is the only interface to the SwingEnumEditor.
   */
  public void addPropertyChangeListener(PropertyChangeListener l) {
    enumEditor.addPropertyChangeListener(l);
    super.addPropertyChangeListener(l);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener l) {
    enumEditor.removePropertyChangeListener(l);
    super.removePropertyChangeListener(l);
  }
  
  /**
   * Initializes this property editor with the enumerated items.
   */
  public void init(PropertyDescriptor descriptor) {
    
    Enumeration attNames = descriptor.attributeNames();
    ArrayList values = new ArrayList();
    while(attNames.hasMoreElements()) { values.add(attNames.nextElement()); }
    
    if(values.size()>1) {
      // The property descriptor describes an enumerated item.
      isEnumeration = true;
      
      enumEditor.init(descriptor);
    } else {
      // This is an integer item
      isEnumeration = false;
      
      if (textfield == null) {
        textfield = new JTextField();
        textfield.setDocument(new NumberDocument());
        // XXX - Textfield should sent an actionPerformed event.
        // this was broken for 1.3 beta
        textfield.addKeyListener(new KeyAdapter() {
          public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
              setValue(new Integer(textfield.getText()));
            }
          }
        });
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(textfield);
      }
    }
  }
  
  /**
   * Return the custom editor for the enumeration or the integer.
   */
  public Component getCustomEditor() {
    if (isEnumeration) {
      return enumEditor.getCustomEditor();
    } else {
      return super.getCustomEditor();
    }
  }
}
