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
import javax.swing.*;
import java.net.*;

import org.xito.dcf.*;
import org.xito.blx.*;
import org.xito.launcher.*;

/**
 *
 * @author  Deane
 */
public class CreateStickyNoteAction extends DefaultAction {
  
  
  /** Creates a new instance of a Create Tile Task */
  public CreateStickyNoteAction() {
    super("New StickyNote");
  }
  
  /**
   * The action has been performed
   * @param ActionEvent
   */
  public void actionPerformed(ActionEvent evt) {
    
    StickyNote note = new StickyNote();
    DesktopService.getDefaultService().getCurrentDesktop().addDesktopComponent(note);
  }
}



