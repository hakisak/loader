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

import java.awt.*;


/**
 * Implements an Alert Dialog for the Alert Manager. The Dialog can have the following optional layout
 * <pre>
 * ==============================================
 * window title
 * ==============================================
 * |  T I T L E                            icon |
 * |    subtitle                                |
 * ==============================================
 * |                                            |
 * |   Basic Message Text or HTML Message Text  |
 * |                                            |
 * |                                            |
 * |--------------------------------------------|
 * |                             <ok> <cancel>  |
 * ==============================================
 * </pre>
 * The Details section will display a Details Panel. The ok, cancel buttons can be changed
 * to Yes No etc.
 *
 * Also if the Title is not specified Then the Title Bar will not be displayed
 * Note: A Window title can also be specified.  See AlertDescriptor for more information
 *
 * @author  Deane Richan
 */
public class AlertDialog extends CustomDialog {
   
   /**
    * Used by Sub-classes
    */
   protected AlertDialog(Frame owner) {
      super(owner);
   }
   
   /**
    * Used by Sub-classes
    */
   protected AlertDialog(Dialog owner) {
      super(owner);
   }
   
   /** Creates a new instance of AlertDialog */
   public AlertDialog(Frame owner, DialogDescriptor descriptor) {
      super(owner, descriptor, true);
   }
   
   /** Creates a new instance of AlertDialog */
   public AlertDialog(Frame owner, DialogDescriptor descriptor, boolean modal) {
      super(owner, descriptor, modal);
   }
   
   /** Creates a new instance of AlertDialog */
   public AlertDialog(Dialog owner, DialogDescriptor descriptor) {
      super(owner, descriptor, true);
   }
   
   /** Creates a new instance of AlertDialog */
   public AlertDialog(Dialog owner, DialogDescriptor descriptor, boolean modal) {
      super(owner, descriptor, modal);
   }
}
