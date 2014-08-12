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
 * Interface used to confirm if a Custom Panel is valid or not. If a custom panel 
 * is used in an Dialog Descriptor and that Panel implements this method then
 * the org.xito will not close until this method returns true or Cancel is pressed.
 *
 * @author Deane Richan
 */
public interface Validatable {
   
   public boolean hasValidData();
}
