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

/**
 *
 * @author Deane
 */
public class ButtonType {
   
   public static final int CANCEL = DialogManager.CANCEL;
   public static final int OK = DialogManager.OK;
   public static final int YES = DialogManager.YES;
   public static final int NO = DialogManager.NO;
   
   public String name;
   public int value;
   public boolean defaultButton = false;
   
   /** Creates a new instance of ButtonType */
   public ButtonType() {
   }
   
   public ButtonType(String n, int v) {
      name = n;
      value = v;
   }
   
   public ButtonType(String n, int v, boolean def) {
      name = n;
      value = v;
      defaultButton = def;
   }
   
}
