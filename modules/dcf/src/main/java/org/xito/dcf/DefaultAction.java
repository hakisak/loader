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

package org.xito.dcf;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.xito.blx.*;
import org.w3c.dom.*;

/**
 * Class used as a Basic Action for DCMenu's
 *
 */
public  class DefaultAction extends AbstractAction implements BLXObject {
  
  public static final String LARGE_ICON = "LargeIcon";
  protected static final ImageIcon defaultLargeIcon = new ImageIcon(DCComponent.class.getResource("/org/xito/launcher/images/globe_32x32.png"));
  protected static final ImageIcon defaultSmallIcon = new ImageIcon(DCComponent.class.getResource("/org/xito/launcher/images/globe_16x16.png"));
  protected BLXHelper blxHelper;
  protected boolean dirty_flag = false;
  
  
  /**
   * Create a Default Action
   */
  public DefaultAction() {
    super();
    blxHelper = new BLXHelper(this);
  }
  
  /**
   * Create DefaultAction
   * @param text for action
   */
  public DefaultAction(String text) {
    this(defaultLargeIcon, text);
  }
  
  /**
   * Create DefaultAction
   * @param largeIcon is a 32x32 sized Icon
   * @param text for action
   */
  public DefaultAction(Icon largeIcon, String text) {
    this();
    putValue(Action.NAME, text);
    putValue(LARGE_ICON, largeIcon);
    if(largeIcon.equals(defaultLargeIcon)) {
      putValue(Action.SMALL_ICON, defaultSmallIcon);
    }
    else if(largeIcon instanceof ImageIcon) {
      putValue(Action.SMALL_ICON, new ImageIcon(((ImageIcon)largeIcon).getImage().getScaledInstance(16,16, Image.SCALE_DEFAULT)));
    }
  }
  
  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(ActionEvent e) {
    System.out.println("Action Performed on:"+super.getValue(Action.NAME));
  }
  
  /** Get the BLX Element for this Component or Object
   * @return the BLXElement object that describes this type of Component.
   *
   */
  public BLXElement getBLXElement() {
    
    return blxHelper.getBLXObjectElement();
  }
  
  /** 
   * Get the XML Data associated with this Object. The XML Data should is a single element
   * that this object uses to persist its state
   * @return the XML Data Element for this Component
   */
  public Element getDataElement() {
    return null;
  }
  
  /** Return true if this components state has changed in a way that
   * Requires the objects container to fetch new XML Data for the Object.
   * @return true if component has changed
   *
   */
  public boolean isDirty() {
    return false;
  }
  
  /** 
   * Set the BLX Element for this Component or Object
   * This should only be called when the object is first being created. Which
   * would normally be directly after the default constructor has been called.
   * @param pElement for this
   */
  public void setBLXElement(BLXElement pElement) {
    blxHelper.setBLXElement(pElement);
  }
  
  /** 
   * Get the BLX Object instance ID for this object
   * @return id
   */
  public String getBLXId() {
    return blxHelper.getBLXId();
  }
  
  /** Store the BLX Object. This will store the objects entire child state or
   * its nested children could use the optional IBLXStorageHandler
   * to persist each of its children.
   * @param allChildren true causes this object to call getDataElement on all its children false means
   *   only dirty children
   * @param IBLXStorageHandler child objects can optionally have their state stored in seperate
   *  documents using a Storage handler.
   * @return the XML Data Element for this Component
   *
   */
  public void store(boolean allChildren, BLXStorageHandler storageHandler) throws IOException {
  }  
  
  
}

