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

package org.xito.blx.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author  Deane
 */
public class BLXViewFrame extends JFrame {
   
   /** Creates a new instance of BLXViewFrame */
   public BLXViewFrame() {
      this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      this.addWindowListener(new MyWindowListener());
      this.getContentPane().setBackground(Color.white);
   }
   
   /**
    * Add a Window Listener this works the same as a normal frame except untrusted code
    * is not able to add window listeners to this frame
    */
   public void addWindowListener(WindowListener listener) {
      super.addWindowListener(listener);
   }
   
   /**
    * Window Listener for this Frame
    *
    */
   public class MyWindowListener implements WindowListener {
      
      public void windowActivated(WindowEvent e) {
      }
      
      public void windowClosed(WindowEvent e) {
         BLXViewer.exit(0);
      }
      
      public void windowClosing(WindowEvent e) {
      }
      
      public void windowDeactivated(WindowEvent e) {
      }
      
      public void windowDeiconified(WindowEvent e) {
      }
      
      public void windowIconified(WindowEvent e) {
      }
      
      public void windowOpened(WindowEvent e) {
      }
      
   }
   
}
