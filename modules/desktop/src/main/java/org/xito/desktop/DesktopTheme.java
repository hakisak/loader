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

import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * This is the Desktop Theme that changes the colors etc. of Metal
 *
 */
public class DesktopTheme extends DefaultMetalTheme{

    public String getName() { return "Desktop"; }

    private ColorUIResource primary1 = new ColorUIResource(66, 33, 66);
    private ColorUIResource primary2 = new ColorUIResource(90, 86, 99);
    private ColorUIResource primary3 = new ColorUIResource(99, 99, 99);

    private ColorUIResource secondary1 = new ColorUIResource(50, 50, 50);
    private ColorUIResource secondary2 = new ColorUIResource(200, 200, 200);
    private ColorUIResource secondary3 = new ColorUIResource(220, 220, 220);

    private ColorUIResource black = new ColorUIResource(0,0,0);
    private ColorUIResource white = new ColorUIResource(255,255,255);

    private FontUIResource menuFont = new FontUIResource("SansSerif", Font.PLAIN, 11);
    private FontUIResource controlFont = new FontUIResource("Dialog", Font.PLAIN, 12);

    //protected ColorUIResource getPrimary1() { return primary1; }
    //protected ColorUIResource getPrimary2() { return primary2; }
    //protected ColorUIResource getPrimary3() { return primary3; }

    protected ColorUIResource getSecondary1() { return secondary1; }
    protected ColorUIResource getSecondary2() { return secondary2; }
    protected ColorUIResource getSecondary3() { return secondary3; }

    protected ColorUIResource getBlack() { return black; }
    protected ColorUIResource getWhite() { return white; }

    public FontUIResource getMenuTextFont() { return menuFont; }
    public FontUIResource getControlTextFont() { return controlFont; }
}
