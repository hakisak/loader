// Copyright (C) 2002 http://ocd.sourceforge.net
//
// This file is part of OCD.
//
// OCD is free software; you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License (LGPL)
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// OCD is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with OCD.
//
// Information about the GNU LGPL License can be obtained at
// http://www.gnu.org/licenses/

package org.xito.desktop;

import org.xito.dcf.*;

/**
 *
 * @author  drichan
 */
public class DefaultDesktopLayout implements DesktopLayout {
  
  private Desktop desktop;
  
  /** Creates a new instance of DefaultDesktopLayout */
  public DefaultDesktopLayout() {
  }
  
  /** Retrieve the Desktop that this Layout is positioning components
   * for
   * @return deskop
   *
   */
  public Desktop getDesktop() {
    return desktop;
  }
  
  /** After a component is added to the Desktop the Desktop will call its
   * DesktopLayout to position the comp in the correct Location.
   * This implementation doesn't change the location of the component.
   *
   * @param comp DCComponent to Position on the Desktop
   */
  public void positionComp(DCComponent comp) {
    //Do Nothing just let the Component lay where it is now
  }
  
  /** Sets the Desktop that this Layout will be positioning components
   * for
   * @param desktop
   *
   */
  public void setDesktop(Desktop desktop) {
    this.desktop = desktop;
  }
  
}
