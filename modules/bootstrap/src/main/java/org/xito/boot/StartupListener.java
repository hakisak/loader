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
 * The StartupListener interface is used to receive notifications during the boot process
 * of the Service Manager. Startup Listeners will be notified when the service manager boots, starts up, and ends.
 *
 * @author  Deane Richan
 * @version $revision$
 */
public interface StartupListener {
   
   /**
    * Called when the Service Manager is about to Start a Service
    */
   public void serviceStarting(ServiceDesc service);
   
   /**
    * Called when the Service Manager has Booted
    * This is fired after all Boot Services have started
    */
   public void sessionBooted();
    
   /**
    * Called when the Service Manager has Started
    * This is fired after all Startup Services have started
    */
   public void sessionStarted();
   
   /**
    * Called when the Service Manager is Ending
    */
   public void sessionEnded();
}

