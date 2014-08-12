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

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.3 $
 * @since $Date: 2007/11/28 03:52:40 $
 */
public class DCPopupMenu extends JPopupMenu
{

  private static Dimension screenSize;

  /** Creates new PopupMenu */
  public DCPopupMenu()
  {
    super();
  }

  public void show(Component pComp, int pX, int pY)
  {
    Dimension _size = this.getPreferredSize();

    //Get Screen Location
    Point _p = new Point(pX, pY);
    SwingUtilities.convertPointToScreen(_p, pComp);

    //Get Screen Size
    if(screenSize == null)
    {
      Toolkit _kit = Toolkit.getDefaultToolkit();
      screenSize = _kit.getScreenSize();
    }

    if((_p.x + _size.width)>screenSize.width) _p.x = screenSize.width - _size.width;
    if((_p.y + _size.height)>screenSize.height) _p.y = screenSize.height - _size.height;

    SwingUtilities.convertPointFromScreen(_p, pComp);
    super.show(pComp, _p.x, _p.y);
  }

}
