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

import javax.swing.text.*;

/**
 * Document to use in JTextField components to assist with validating
 *
 * @author Deane Richan
 */
public class ValidatableDocument extends PlainDocument {
   
   int maxlength=0;
   
   /** Creates a new instance of ValidatableDocument */
   public ValidatableDocument(int maxlength) {
      this.maxlength = maxlength;
   }
   
   public void insertString(int offset, String  str, AttributeSet attr) throws BadLocationException {
      if (str == null) return;

      if(maxlength<=0) {
         super.insertString(offset, str, attr);
      }
      else if ((getLength() + str.length()) <= maxlength) {
         super.insertString(offset, str, attr);
      }
   }
}
