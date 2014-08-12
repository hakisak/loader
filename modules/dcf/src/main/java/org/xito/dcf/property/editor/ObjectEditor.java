package org.xito.dcf.property.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

import java.beans.Beans;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

public class ObjectEditor extends EditorSupport
{
  
  private JTextField textfield;
  
  public ObjectEditor()
  {
    textfield = new JTextField();
    
    // NOTE: This should work but there was a regression in JDK 1.3 beta which
    // doesn't fire for text fields.
    // This should be fixed for RC 1.
    textfield.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        // XXX - debug
        System.out.println("SwingObjectEditor.actionPerformed");
        handleAction();
      }
    });
    
    // XXX - Temporary workaround for 1.3 beta
    textfield.addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent evt)
      {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
          handleAction();
        }
      }
    });
    
    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.add(textfield);
  }
  
  public void setValue(Object value)
  {
    super.setValue(value);
    
    if (value != null)
    {
      // Truncate the address from the object reference.
      String text = value.toString();
      
      // XXX javax.swing.AccessibleRelationSet.toString() has a bug in which
      // null is returned. Intecept this and other cases so that the tool
      // doens't get hosed.
      if (text == null) text = "";
      
      int index = text.indexOf('@');
      if (index != -1)
      {
        text = text.substring(0, index);
      }
      textfield.setText(text);
    } else
    {
      textfield.setText("");
    }
  }
  
  /**
   * Callback method which gets handled for actionPerformed.
   */
  private void handleAction()
  {
    String beanText = textfield.getText();
    
    try
    {
      Object obj = Beans.instantiate(this.getClass().getClassLoader(), beanText);
      setValue(obj);
    } catch (Exception ex)
    {
      JOptionPane.showMessageDialog(panel, "Can't find or load\n" + beanText);
    }
  }
  
}
