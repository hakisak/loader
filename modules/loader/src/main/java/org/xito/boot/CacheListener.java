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

import java.net.*;

/**
 * CacheListeners are notified about downloading events occuring on the cache. A implementation
 * could be used to display a progress bar for downloads to a user.
 *
 * @author  Deane Richan
 */
public interface CacheListener {
   
   public void gettingInfo(CacheEvent event);
   
   public void startDownload(CacheEvent event);
   
   public void updateDownload(CacheEvent event);
   
   public void completeDownload(CacheEvent event);
   
   public void completeGettingInfo(CacheEvent event);
   
   public void downloadException(String name, URL url, String msg, Exception exp);
   
}
