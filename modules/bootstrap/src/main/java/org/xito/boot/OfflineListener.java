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

package org.xito.boot;

/**
 * Listener interface used to notify implementations that the BootStrap has changed to
 * offline or online mode.
 *
 * Implementations should register with the BootStrap using Boot.addOfflineListener
 *
 * @author Deane Richan
 */
public interface OfflineListener {
   
   /**
    * Called after the BootStrap has changed to online status
    */
   public void online();
   
   /**
    * Called after the Bootstrap has changed to offline status
    */
   public void offline();
      
}
