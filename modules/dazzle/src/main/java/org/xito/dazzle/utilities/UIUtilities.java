// Copyright 2009 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.dazzle.utilities;

import java.awt.*;
import java.awt.peer.*;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class UIUtilities {
   
   /**
    * Return true if the secondary mouse button was pressed
    */
   public static boolean isSecondaryMouseButton(MouseEvent evt) {

      // on mac os if CTRL is pressed with the mouse click then
      // it is a secondary mouse click
      if (System.getProperty("os.name").indexOf("Mac OS X") > -1) {
         if ((evt.getModifiers() & MouseEvent.CTRL_MASK) > 0) {
            return true;
         }
      }

      if (SwingUtilities.isRightMouseButton(evt)) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * Set Alpha Window, only supported on Mac. Will be updated to support Java 6
    * on Mac or windows
    * 
    * @param w
    * @param alpha
    */
   public static void setWindowAlpha(Window w, float alpha) {

      //mac jdk 5 support
      if (MacApplicationUtilities.isAtLeastMacOSVersionX5()) {
         if(w instanceof JWindow) {
            ((JWindow)w).getRootPane().putClientProperty("Window.alpha", new Float(alpha));
         }
         else if(w instanceof JFrame) {
            ((JFrame)w).getRootPane().putClientProperty("Window.alpha", new Float(alpha));
         }
      }
   }
   
}
