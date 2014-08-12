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

package org.xito.launcher.sys;

import java.io.*;
import org.xito.boot.*;
import org.xito.launcher.*;

/**
 *
 * @author Deane Richan
 */
public class LocalAppDesc extends BaseLaunchDesc {
   
   private String arguments;
   private File startInDir;
   private String execCmd;
   
   public LocalAppDesc() {
      super();
   }
   
   public LocalAppDesc(String cmd) {
      setExecutableCmd(cmd);
   }
   
   public void setExecutableCmd(String cmd) {
      execCmd = cmd;
   }
   
   public String getExecutableCmd() {
      return execCmd;
   }
   
   public String getArgs() {
      return arguments;
   }
   
   public void setArgs(String args) {
      this.arguments = args;
   }
   
   public void setStartInDir(File dir) {
      startInDir = dir;
   }
   
   public File getStartInDir() {
      return startInDir;
   }
}
