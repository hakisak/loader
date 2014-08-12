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

import org.xito.boot.*;

/**
 *
 * @author  Deane
 */
public class BLXViewer {
   
   /** Creates a new instance of BLXViewer */
   public BLXViewer() {
   }
   
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      BLXViewFrame frame = new BLXViewFrame();
      frame.setSize(500,500);
      frame.setVisible(true);
   }
   
   /**
    * Exit the VM
    */
   public static void exit(int code) {
      
      //End this Session
      //Shell.getShell().endSession(false);
   }
   
}
