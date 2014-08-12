// Copyright (C) 2005 Xito.org. http://www.xito.org
//
// This file is part of Xito(tm).
//
// This Software is licensed under the terms of the
// COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0
//
// To view the complete Terms of this license visit:
// http://www.opensource.org/licenses/cddl1.txt
//
// COVERED SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN AS IS BASIS, WITHOUT
// WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, WITHOUT
// LIMITATION, WARRANTIES THAT THE COVERED SOFTWARE IS FREE OF DEFECTS,
// MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR NON-INFRINGING. THE ENTIRE
// RISK AS TO THE QUALITY AND PERFORMANCE OF THE COVERED SOFTWARE IS WITH YOU.
// SHOULD ANY COVERED SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, YOU (NOT THE
// INITIAL DEVELOPER OR ANY OTHER CONTRIBUTOR) ASSUME THE COST OF ANY
// NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
// CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY COVERED
// SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.

package org.xito.launcher;

import java.beans.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * Serves as a Simple Action that can be subclassed by specific actions to implement basic XML Persistence
 *
 * @author Deane Richan
 */
public abstract class LauncherAction implements Action {
   
   protected PropertyChangeSupport propertyChangeSupport;
   protected HashMap values = new HashMap();
   
   protected boolean enabled_flag = true;
   protected boolean dirty_flag = false;
   
   protected LaunchDesc launch_desc;
   protected LauncherActionFactory factory;
   
   public LauncherAction(LauncherActionFactory factory) {
      this.factory = factory;
   }
   
   /**
    * @return the factory
    */
   public LauncherActionFactory getFactory() {
      return factory;
   }

   /**
    * Get the LaunchDesc
    */
   public LaunchDesc getLaunchDesc() {
      
      return launch_desc;
   }
   
   /**
    * Set the LaunchDesc
    */
   public void setLaunchDesc(LaunchDesc desc) {
      
      launch_desc = desc;
   }
   
   /**
    * Get the Name of this Action
    */
   public String getName() {
      return (String)getValue(Action.NAME);
   }
   
   /**
    * Get a DOM Element name that this Action uses for persistence
    */
   public String getElementName() {
      return factory.getElementName();
   }
   
   /**
    * Edit this Action. Display a GUI to edit this Action. Return true if the edit succeeded. Return
    * false if the edit failed or was canceled
    * @param parentFrame the edit Dialog should be added to
    */
   public abstract boolean edit(Frame parentFrame);
   
   /**
    * Get supported DataFlavors of this Action or return null if no DataFlavors are supported
    */
   public Collection getDataFlavors() {
      return null;
   }
   
   /**
    * Get TransferData for a specific flavor
    */
   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
      throw new UnsupportedFlavorException(flavor);     
   } 
   
   /**
    * Get Value
    */
   public Object getValue(String key) {
      return values.get(key);
   }
   
   /**
    * Put Value
    */
   public void putValue(String key, Object value) {
      
      Object old = values.get(key);
      if(propertyChangeSupport != null)
         propertyChangeSupport.firePropertyChange(key, old, value);
      
      values.put(key, value);
      
   }
   
   /**
    * Remove a Property Change Listener
    */
   public void removePropertyChangeListener(PropertyChangeListener listener) {
      if(propertyChangeSupport == null) return;
      
      propertyChangeSupport.removePropertyChangeListener(listener);
   }
   
   /**
    * Add a Property Change Listener
    */
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      if(propertyChangeSupport == null)
         propertyChangeSupport = new PropertyChangeSupport(this);
      
      propertyChangeSupport.addPropertyChangeListener(listener);
   }
   
   /**
    * Set Enabled
    */
   public void setEnabled(boolean b) {
      enabled_flag = b;
   }
   
   /**
    * Is Enabled
    */
   public boolean isEnabled() {
      return enabled_flag;
   }
   
   /**
    * Return true if the settings of this Action have changed since setDataElement was called
    */
   public boolean isDirty() {
      return dirty_flag;
   }
   
   public String toString() {
      if(getLaunchDesc().getTitle() != null && getLaunchDesc().getTitle().equals("")==false) {
         return getLaunchDesc().getTitle();
      } else {
         return getLaunchDesc().getName();
      }
   }
   
}
