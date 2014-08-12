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

package org.xito.launcher;

import java.util.*;

/**
 * Used to parse arguments in Command Lines
 *
 * @author Deane Richan
 */
public class CmdLineParser {
   
   /**
    * Parse a Command line into a String Array
    */
   public static String[] parse(String cmdline) {
      if(cmdline == null) {
         return new String[0];
      }
      
      char[] chars = cmdline.toCharArray();
      
      boolean inquote = false;
      
      ArrayList args = new ArrayList();
      StringBuffer buf = new StringBuffer();
      for(int i=0;i<chars.length;i++) {
         char c = chars[i];
         //quote
         if(c == '\"') {
            if(!inquote) {
               inquote = true;
               continue;
            }
            else {
               args.add(buf.toString());
               buf = new StringBuffer();
               inquote = false;
               continue;
            }
         }
         
         //space
         if(c == ' ' && !inquote) {
            if(buf.length()==0)
               continue;
            else {
               args.add(buf.toString());
               buf = new StringBuffer();
               continue;
            }
         }
         
         //append the char
         buf.append(c);
      }
      
      //append last arg
      if(buf.length()!=0) {
         args.add(buf.toString());
      }
      
      return (String[])args.toArray(new String[args.size()]);
   }
   
}
