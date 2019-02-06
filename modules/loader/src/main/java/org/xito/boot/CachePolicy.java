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
 * CachePolicy used with Cache Manager to determine when new updates should be downloaded
 *
 * ALWAYS - The resource uptodate check should occur every time the resource is requested
 * DAILY - The resource uptodate check should occur at least once a day
 * WEEKLY - The resource uptodate check should occur at least once a week
 * MONTHLY - The resource uptodate check should occur at least once a month
 * NEVER - The resource uptodate check should never occur after the initial download
 *
 * @author Deane Richan
 */
public class CachePolicy implements java.io.Serializable {
   
   private static final String ALWAYS_STR = "ALWAYS";
   private static final String DAILY_STR = "DAILY";
   private static final String WEEKLY_STR = "WEEKLY";
   private static final String MONTHLY_STR = "MONTHLY";
   private static final String NEVER_STR = "NEVER";
   
   private String policy;
   
   public static final CachePolicy ALWAYS = new CachePolicy(ALWAYS_STR);
   public static final CachePolicy DAILY = new CachePolicy(DAILY_STR);
   public static final CachePolicy WEEKLY = new CachePolicy(WEEKLY_STR);
   public static final CachePolicy MONTHLY = new CachePolicy(MONTHLY_STR);
   public static final CachePolicy NEVER = new CachePolicy(NEVER_STR);
   
   /** Creates a new instance of CachePolicy */
   private CachePolicy(String p) {
      policy = p;
   }

   /**
    * Get a policy for a String description
    */
   public static CachePolicy getPolicy(String s) {
      if(ALWAYS_STR.equals(s)) {
         return ALWAYS;
      }
      else if(DAILY_STR.equals(s)) {
         return DAILY;
      }
      else if(WEEKLY_STR.equals(s)) {
         return WEEKLY;
      }
      else if(MONTHLY_STR.equals(s)) {
         return MONTHLY;
      }
      else {
         return ALWAYS;
      }
   }
   
   /**
    * Return the String version of this policy
    */
   public String toString() {
      return policy;
   }
}
