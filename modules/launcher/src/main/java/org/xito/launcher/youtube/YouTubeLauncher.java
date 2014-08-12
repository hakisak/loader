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

import java.awt.Component;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.xito.boot.*;
import org.xito.boot.util.RuntimeHelper;
import org.xito.dialog.DialogManager;
import org.xito.launcher.*;
/*import org.jdesktop.jdic.filetypes.*;
import org.jdesktop.jdic.browser.BrowserEngineManager;
import org.jdesktop.jdic.browser.IWebBrowser;
import org.jdesktop.jdic.desktop.*;*/

/**
 *
 * @author DRICHAN
 */
public class YouTubeLauncher {
   /*
   private static BrowserEngineManager bem;
   static {
   
      //load jdic code
      bem = BrowserEngineManager.instance();
   }
   */
   
   /** Creates a new instance of WebLauncher */
   public YouTubeLauncher() {
   }
   
   /**
    * Launches web browser with URL in a new thread
    */
   public void launch(YouTubeDesc youtubeDesc) {
  
      /*if(youtubeDesc == null || youtubeDesc.getVideoId() == null) {
         
         return;
      }
       
      //On App MacOS just call the open command
      try {
         IWebBrowser webBrowser = bem.getActiveEngine().getWebBrowser();
         URL u = new URL("http://xito.sourceforge.net/youtube_template.php?vid_id="+youtubeDesc.getVideoId());
         webBrowser.setURL(u);
         
         JFrame f = new JFrame("YouTube:" + youtubeDesc.getTitle());
         f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
         f.setSize(450, 400);
         f.setResizable(false);
         DialogManager.centerWindowOnScreen(f);
   
         Component webComp = webBrowser.asComponent();
   
         f.add(webComp);
         f.setVisible(true);
      }
      catch(MalformedURLException exp) {
         String msg = "<html>The Web Address is not valid</html>"; 
         DialogManager.showError(null, "Invalid Web Address", msg, null);
      }*/
   }
}
