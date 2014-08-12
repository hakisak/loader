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

package org.xito.launcher.jnlp.service;

import java.net.*;
import java.util.logging.*;
import javax.jnlp.BasicService;

import org.xito.boot.*;
import org.xito.launcher.*;
import org.xito.launcher.jnlp.*;
import org.xito.launcher.web.*;

/**
 *
 * @author Deane Richan
 */
public class BasicServiceImpl implements BasicService {

   private static final Logger logger = Logger.getLogger(BasicServiceImpl.class.getName());
   private URL codebase;
   private Process browserProcess;
   private WebLauncher webLauncher;
      
   /** Creates a new instance of JNLPBasicService */
   public BasicServiceImpl(JNLPAppDesc appDesc) {
      codebase = appDesc.getJNLPNode().getCodeBaseURL();
   }

   /**
    * Show a Document in the Default Web Browser
    */
   public boolean showDocument(URL url) {
      if(url == null) {
         return false;
      }
      
      if(webLauncher == null) {
         webLauncher = new WebLauncher();
      }
      
      try {
         int exitValue = browserProcess.exitValue();
         webLauncher.launch(url);
         return true;
      }
      catch(Exception exp) {
         //Ignore this
      }
      
      webLauncher.launch(url);
      return true;
   }

   /**
    * Get the Apps CodeBase
    */
   public URL getCodeBase() {
      return codebase;
   }

   /**
    * Return true if the client is Offline
    */
   public boolean isOffline() {
      return false;
   }

   /**
    * Return true if a WebBrowser is support
    */
   public boolean isWebBrowserSupported() {
      return true;
   }
   
}
