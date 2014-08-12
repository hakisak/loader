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

import java.awt.event.*;
import javax.swing.*;

import org.xito.dcf.*;

/**
 *
 * @author  Deane
 */
public class AboutAction extends DefaultAction {
  
  /** Creates a new instance of DesktopMenuModel */
  public AboutAction() {
    super(new ImageIcon(AboutAction.class.getResource("/org/xito/launcher/images/xito32.gif")), "About");
  }
  
  /**
   * The action has been performed
   * @param ActionEvent
   */
  public void actionPerformed(ActionEvent evt) {
    
  }
}



