// Copyright (C) 2002 Xito.org. http://www.xito.org
//
// This file is part of Xito(tm). 
//
// This is free software; you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License (LGPL)
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// It is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this application.
//
// Information about the GNU LGPL License can be obtained at
// http://www.gnu.org/licenses/

package org.xito.desktop;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.prefs.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;

import org.w3c.dom.*;

import org.xito.dcf.*;
import org.xito.blx.*;


/**
 * StickyNote
 *
 * @author  $Author: drichan $
 * @version $Revision: 1.3 $
 * @since   $Date: 2005/06/18 01:13:06 $
 */
public class StickyNote extends DCComponent {

  public static final String NODE_NAME = "note";
  protected Color bgColor = new Color(0xfffbab);
  protected JTextArea textArea = new JTextArea();
  protected DCCloseButton closeBtn;
  protected DCResizeButton resizeBtn;
  protected JLabel titleLabel;
  protected Date createdDate;

  public StickyNote() {
    init();
  }

  private void init() {
    
    setSize(200,250);
    setDraggable(true);

    setLayout(new BorderLayout());
    setBorder(LineBorder.createBlackLineBorder());

    //Top
    closeBtn = new DCCloseButton(this);
    closeBtn.setBounds(0,0,16,16);
    //JPanel _top = new JPanel(null);
    JPanel _top = new JPanel(new BorderLayout());
    _top.setOpaque(false);
    _top.add(closeBtn, BorderLayout.WEST);
    _top.setPreferredSize(new Dimension(16,16));
    this.add(_top, BorderLayout.NORTH);

    createdDate = new Date();
    titleLabel = new JLabel(createdDate.toString());
    _top.add(titleLabel);

    //Text Area
    textArea.setOpaque(false);
    textArea.setBorder(new EmptyBorder(6,6,6,6));
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setFont(titleLabel.getFont());

    JScrollPane _pane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    _pane.getViewport().setOpaque(false);
    _pane.setOpaque(false);
    _pane.setBorder(null);
    this.add(_pane);

    //Bottom
    resizeBtn = new DCResizeButton(this);
    JPanel _south = new JPanel(new BorderLayout());
    _south.setOpaque(false);
    _south.add(resizeBtn, BorderLayout.EAST);
    _south.setPreferredSize(new Dimension(16,16));
    this.add(_south, BorderLayout.SOUTH);

    //Setup textArea Listener
    textArea.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent evt) {
          setIsDirty(true);
        }
      });

  }

  protected void paintComponent(Graphics pGraphics) {
    pGraphics.setColor(bgColor);
    Dimension _size = getSize();
    pGraphics.fillRect(0, 0, _size.width, _size.height);

  }

  /**
   * Set the Node on this Component
   * @param pElement Node that contains this components settings
   * @param pRelativeURL the URL that all HREFs would be relative to
   */
  public void setBLXElement(BLXElement blxElement) {
    super.setBLXElement(blxElement);

    Element noteNode = blxElement.getDataElement();
    if(noteNode != null) {
      Node _text = noteNode.getFirstChild();
      if(_text != null) {
        textArea.setText(_text.getNodeValue());
      }
    }
    
    //Clear the Dirty Flag
    setIsDirty(false);
  }

  /**
   * Get the Data Node for this Component
   * @return the Node that contains this Components Settings
   */
  public Element getDataElement() {
    Document doc = this.createDOMDocument();
    Element e = doc.createElement(NODE_NAME);
    Text text = doc.createTextNode(textArea.getText());
    e.appendChild(text);

    return e;
  }
}

