package org.xito.dcf.property.editor;

import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InsetsEditor extends EditorSupport
{
  
  private JTextField topTF;
  private JTextField leftTF;
  private JTextField bottomTF;
  private JTextField rightTF;
  
  public InsetsEditor()
  {
    topTF = new JTextField();
    topTF.setDocument(new NumberDocument());
    leftTF = new JTextField();
    leftTF.setDocument(new NumberDocument());
    bottomTF = new JTextField();
    bottomTF.setDocument(new NumberDocument());
    rightTF = new JTextField();
    rightTF.setDocument(new NumberDocument());
    
    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.add(new JLabel("Top: "));
    panel.add(topTF);
    panel.add(new JLabel("Left: "));
    panel.add(leftTF);
    panel.add(new JLabel("Bottom: "));
    panel.add(bottomTF);
    panel.add(new JLabel("Right: "));
    panel.add(rightTF);
  }
  
  public void setValue(Object value)
  {
    super.setValue(value);
    
    Insets insets = (Insets)value;
    
    topTF.setText(Integer.toString(insets.top));
    leftTF.setText(Integer.toString(insets.left));
    bottomTF.setText(Integer.toString(insets.bottom));
    rightTF.setText(Integer.toString(insets.right));
  }
  
  public Object getValue()
  {
    int top = Integer.parseInt(topTF.getText());
    int left = Integer.parseInt(leftTF.getText());
    int bottom = Integer.parseInt(bottomTF.getText());
    int right = Integer.parseInt(rightTF.getText());
    
    return new Insets(top, left, bottom, right);
  }
  
}