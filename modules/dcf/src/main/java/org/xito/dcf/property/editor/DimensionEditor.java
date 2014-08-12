package org.xito.dcf.property.editor;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DimensionEditor extends EditorSupport
{
  
  private JTextField widthTF;
  private JTextField heightTF;
  
  public DimensionEditor()
  {
    widthTF = new JTextField();
    widthTF.setDocument(new NumberDocument());
    heightTF = new JTextField();
    heightTF.setDocument(new NumberDocument());
    
    JLabel wlabel = new JLabel("Width: ");
    JLabel hlabel = new JLabel("Height: ");
    
    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.add(wlabel);
    panel.add(widthTF);
    panel.add(hlabel);
    panel.add(heightTF);
  }
  
  public void setValue(Object value)
  {
    super.setValue(value);
    
    Dimension dim = (Dimension)value;
    
    widthTF.setText(Integer.toString(dim.width));
    heightTF.setText(Integer.toString(dim.height));
  }
  
  public Object getValue()
  {
    int width = Integer.parseInt(widthTF.getText());
    int height = Integer.parseInt(heightTF.getText());
    
    return new Dimension(width, height);
  }
}