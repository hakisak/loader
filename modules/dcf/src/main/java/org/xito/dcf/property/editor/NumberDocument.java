package org.xito.dcf.property.editor;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


public class NumberDocument extends PlainDocument
{
  public void insertString(int offs, String str, AttributeSet atts)
  throws BadLocationException
  {
    if (!Character.isDigit(str.charAt(0)))
    {
      return;
    }
    super.insertString(offs, str, atts);
  }
}

