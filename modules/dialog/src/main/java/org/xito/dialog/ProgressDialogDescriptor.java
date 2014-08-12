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

package org.xito.dialog;

import java.security.*;

/**
 * Descriptor class used to describe how a Progress Dialog Looks. The display
 * is similar to other dialogs except that an Animated Icon is used.
 *
 * @author  Deane Richan
 */
public class ProgressDialogDescriptor extends DialogDescriptor {
   
   private Runnable runnable;
   private boolean disposeOnComplete_flag = true;
   
   public ProgressDialogDescriptor() {
      super();
      init();
   }
   
   /**
    * Initialize this Descriptor
    */
   private void init() {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            //setIcon(new ImageIcon(ProgressDialogDescriptor.class.getResource("org.xito.launcher.images/clock_ani_48.gif")));
            //setIcon(new ImageIcon(ProgressDialogDescriptor.class.getResource("org.xito.launcher.images/tick_clock_ani_48.gif")));
            setIconComp(new ProgressDialog.BusySpinner(ProgressDialog.BusySpinner.dialogTheme));
            return null;
         }
      });
   }
   
   /**
    * Set the Runnable Object that the Progress Dialog will execute while it is displaying
    */
   public void setRunnableTask(Runnable r) {
      runnable = r;
   }
   
   /**
    * Return the Runnable Object
    */
   public Runnable getRunnableTask() {
      return runnable;
   }
   
   /**
    * Set to true if the Dialog should be Disposed when the Runnable Task is Complete.
    * Defaults to true
    */
   public void setDisposeOnComplete(boolean b) {
      disposeOnComplete_flag = b;
   }
   
   /**
    * Return DisposeOnComplete Flag. Defaults to true
    */
   public boolean disposeOnComplete() {
      return disposeOnComplete_flag;
   }
}

