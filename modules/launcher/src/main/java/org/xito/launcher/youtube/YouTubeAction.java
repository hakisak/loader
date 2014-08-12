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

package org.xito.launcher.youtube;

import java.awt.*;
import java.net.*;
import java.util.HashMap;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import org.w3c.dom.*;
import org.w3c.tidy.*;
import org.xito.boot.*;
import org.xito.dialog.*;
import org.xito.launcher.*;


/**
 *
 * @author Deane Richan
 */
public class YouTubeAction extends LauncherAction {
  
   /** Creates a new instance of AppletAction */
   public YouTubeAction(YouTubeActionFactory factory) {
      this(factory, new YouTubeDesc());
   }
   
   /** Creates a new instance of AppletAction */
   public YouTubeAction(YouTubeActionFactory factory, YouTubeDesc youTubeDesc) {
      super(factory);
      super.setLaunchDesc(youTubeDesc);
      super.putValue(super.SMALL_ICON, YouTubeActionBeanInfo.icon16);
   }
      
   
   /**
    * Get a DOM Element name that this Action uses for persistence
    */
   public String getElementName() {
      return factory.getElementName();
   }
   
   public void actionPerformed(ActionEvent evt) {
      
      YouTubeDesc youTubeDesc = (YouTubeDesc)getLaunchDesc();
      String videoId = youTubeDesc.getVideoId();
      if(videoId == null) {
         Boot.showError("Video ID Error", "Video ID not specified.", null);
         return;
      }
      
      YouTubeLauncher launcher = new YouTubeLauncher();
      launcher.launch(youTubeDesc);
   }
   
   /**
    * Edit this Action
    */
   public boolean edit(Frame parentFrame) {
      
      YouTubeConfigDialog dialog = new YouTubeConfigDialog(parentFrame, ((YouTubeDesc)getLaunchDesc()));
      dialog.setVisible(true);
      
      if(dialog.getResult() == DialogManager.OK) {
         dirty_flag = true;
         return true;
      }
      else {
         return false;
      }
   }
}
