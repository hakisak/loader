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
import org.xito.dcf.*;
import org.xito.blx.*;

/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.2 $
 * @since $Date: 2005/06/15 23:19:18 $
 */
public interface Desktop extends BLXObject
{
  public static final String XML_NODE_NAME = "desktop";
  public static final String XML_DOC_DIR = "/desktop";
  /**
   * Add a Desktop Drop Listener
   * @param Listener
   */
  public void addDesktopDropListener(DesktopDropListener pListener);

  /**
   * Remove a Desktop Drop Listener
   * @param Listener
   */
  public void removeDesktopDropListener(DesktopDropListener pListener);

  /**
   * This Method is called by the Desktop Service
   * When this Desktop has been made the current Desktop
   */
  public void desktopInstalled();

  /**
   * This Method is called by the Desktop Service when
   * This Desktop has been removed as the current Desktop
   */
  public void desktopUninstalled();

  /**
   * Returns True if this Desktop is Currently
   * Installed as the Current Desktop
   * @return installed status
   */
  public boolean isInstalled();

  /**
   * Loads this Desktops Default Configuration
   */
  public void loadDefault();

  /**
   * Add a Object to the Desktop
   * @param pComp to add
   */
  public void addDesktopObject(BLXObject pObject);
  
  /**
   * Add a Component to the Desktop
   * @param pComp to add
   */
  public void addDesktopComponent(DCComponent pComp);

  /**
   * Remove a Component from the Desktop
   * @param pComp to remove
   */
  public void removeDesktopComponent(DCComponent pComp);
  
  /** 
   * Get all Desktop Components that are located between two points
   * @param rect area to get components for or null for all Components
   * @return array of DCComponents
   */
  public DCComponent[] getDesktopComponents(Rectangle rect);
  
  /**
   * Sets the Layout for the Desktop
   * @param DesktopLayout or null for no layout
   */
  public void setDesktopLayout(DesktopLayout layout);
  
  /**
   * Get the current Desktop Layout
   * @return IDesktopLayout
   */
  public DesktopLayout getDesktopLayout();
  
  /**
   * Return true if the Desktop contains the Specified Object
   * @param blxObject
   * @return true if this Desktop contains the blxObject
   */
  public boolean contains(BLXObject obj);
}

