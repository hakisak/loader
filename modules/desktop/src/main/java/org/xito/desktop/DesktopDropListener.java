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

import org.xito.dcf.*;
/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.1.1.1 $
 * @since $Date: 2003/09/13 04:39:26 $
 */
public interface DesktopDropListener
{
  /**
   * Item was Dropped onto the Desktop
   * This event is only fired after the Desktop has accepted the dropped component
   * @param Component that was dropped on the desktop or the Component that the Desktop Created.
   *
   */
  public void drop(DCComponent pComp);
}

