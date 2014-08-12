package org.xito.dcf.property.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class BooleanEditor extends EditorSupport
{
  
  private JCheckBox checkbox;
  
  public BooleanEditor()
  {
    checkbox = new JCheckBox();
    checkbox.addItemListener(new ItemListener()
    {
      public void itemStateChanged(ItemEvent evt)
      {
        if (evt.getStateChange() == ItemEvent.SELECTED)
        {
          setValue(Boolean.TRUE);
        } else
        {
          setValue(Boolean.FALSE);
        }
      }
    });
    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.add(checkbox);
  }
  
  public void setValue(Object value )
  {
    super.setValue(value);
    if (value != null)
    {
      try
      {
        checkbox.setText(value.toString());
        if (checkbox.isSelected() != ((Boolean)value).booleanValue())
        {
          // Don't call setSelected unless the state actually changes
          // to avoid a loop.
          checkbox.setSelected(((Boolean)value).booleanValue());
        }
      } catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }
  
  public Object getValue()
  {
    return new Boolean(checkbox.isSelected());
  }
}
