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
import java.util.*;
import org.xito.launcher.*;
import org.xito.boot.util.RuntimeHelper;

/**
 *
 * @author Deane
 */
public class LocalAppLauncher {
   
   /** Creates a new instance of LocalAppLauncher */
   public LocalAppLauncher() {
   }
   
   /**
    * Launch a Local Application
    */
   public void launch(LocalAppDesc app) throws IOException {
      
      if(app.getExecutableCmd() == null) {
         throw new IOException("Command not specified");
      }
      
      String cmd = app.getExecutableCmd();
      String argsLine = app.getArgs();
      File curDir = app.getStartInDir();
      
      String args[] = null;
      if(argsLine != null) {
         args = CmdLineParser.parse(argsLine);
      }
      
      ArrayList cmds = new ArrayList();
      cmds.add(cmd);
      if(args != null) {
         cmds.addAll(Arrays.asList(args));
      }
      
      RuntimeHelper.exec((String[])cmds.toArray(new String[cmds.size()]), null, curDir);
   }
   
}
