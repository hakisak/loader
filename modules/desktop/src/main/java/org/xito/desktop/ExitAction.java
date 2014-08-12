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

import java.text.*;
import java.awt.event.*;
import java.beans.*;

import org.xito.dcf.*;
import org.xito.blx.*;
import org.xito.boot.*;
import org.xito.dialog.*;

/**
 *
 * @author  Deane
 */
public class ExitAction extends DefaultAction {
   
   /** Creates a new instance of DesktopMenuModel */
   public ExitAction() {
      super("Exit");
   }
   
   /**
    * The action has been performed
    * @param ActionEvent
    */
   public void actionPerformed(ActionEvent evt) {
      Boot.endSession(true);
      /*
      String title = DesktopService.resources.getString("exit.org.xito.title");
      title = MessageFormat.format(title, new String[]{Shell.getShell().getAppName()});
      
      String msg = DesktopService.resources.getString("exit.org.xito.msg");
      msg = MessageFormat.format(msg, new String[]{Shell.getShell().getAppName()});
      int result = AlertManager.getDefaultAlertManager().showConfirm(null, null, title, msg, AlertManager.YES_NO);
      
      if(result == AlertManager.YES)
         Shell.getShell().endSession(false);
       */
   }
}
