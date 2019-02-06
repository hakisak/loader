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

import java.util.*;
import java.net.*;

/**
 * Used to Inform CacheListeners of progress during a download
 *
 * @author  Deane Richan
 */
public class CacheEvent extends EventObject {

   private String resourceGroupName;
   private String resourceName;
   private URL url;
   private int totalSize;
   private int progressSize;
   private long progressTime;
   private long estimateTime;

   /** Creates a new instance of CacheEvent
    * @param source event source
    * @param resourceGroupName name of the group this download is part of
    * @param resourceName name of the resource that is being downloaded
    * @param url of the resource
    * @param totalSize of resource
    * @param progressSize amount downloaded so far
    * @param progressTime amount of time elapsed
    * @param estimateTime estimated time left
    */
   public CacheEvent(Object source, String resourceGroupName, String resourceName, URL url, int totalSize, int progressSize, long progressTime, long estimateTime) {

      super(source);
      setResourceGroupName(resourceGroupName);
      setResourceName(resourceName);
      setURL(url);
      setTotalSize(totalSize);
      setProgressSize(progressSize);
      setProgressTime(progressTime);
      setEstimateTime(estimateTime);
   }

   /**
    * Get the Resource Group Name
    * @return name of resource group name
    */
   public String getResourceGroupName() {
      return resourceGroupName;
   }

   /**
    * Set the Resource Group Name
    * @param resourceGroupName group name
    */
   public void setResourceGroupName(String resourceGroupName) {
      this.resourceGroupName = resourceGroupName;
   }

   /**
    * Getter for property progressSize.
    * @return Value of property progressSize.
    */
   public int getProgressSize() {
      return progressSize;
   }
   
   /**
    * Setter for property progressSize.
    * @param progressSize New value of property progressSize.
    */
   public void setProgressSize(int progressSize) {
      this.progressSize = progressSize;
   }
   
   /**
    * Getter for property resourceName.
    * @return Value of property resourceName.
    */
   public java.lang.String getResourceName() {
      return resourceName;
   }
   
   /**
    * Setter for property resourceName.
    * @param resourceName New value of property resourceName.
    */
   public void setResourceName(java.lang.String resourceName) {
      this.resourceName = resourceName;
   }
   
   /**
    * Getter for property url.
    * @return Value of property url.
    */
   public java.net.URL getURL() {
      return url;
   }
   
   /**
    * Setter for property url.
    * @param url New value of property url.
    */
   public void setURL(java.net.URL url) {
      this.url = url;
   }
   
   /**
    * Getter for property totalSize.
    * @return Value of property totalSize.
    */
   public int getTotalSize() {
      return totalSize;
   }
   
   /**
    * Setter for property totalSize.
    * @param totalSize New value of property totalSize.
    */
   public void setTotalSize(int totalSize) {
      this.totalSize = totalSize;
   }
   
   /**
    * Getter for property totalTime.
    * @return Value of property totalTime.
    */
   public long getProgressTime() {
      return progressTime;
   }
   
   /**
    * Setter for property totalTime.
    * @param progressTime New value of property totalTime.
    */
   public void setProgressTime(long progressTime) {
      this.progressTime = progressTime;
   }
   
   /**
    * Getter for property estimateTime.
    * @return Value of property estimateTime.
    */
   public long getEstimateTime() {
      return estimateTime;
   }
   
   /**
    * Setter for property estimateTime.
    * @param estimateTime New value of property estimateTime.
    */
   public void setEstimateTime(long estimateTime) {
      this.estimateTime = estimateTime;
   }

}
