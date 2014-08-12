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

import java.io.*;
import java.util.logging.*;
import java.security.*;
import javax.swing.*;
import javax.jnlp.*;

import org.xito.boot.*;
import org.xito.launcher.*;
import org.xito.launcher.jnlp.*;


/**
 *
 * @author Deane Richan
 */
public class FileOpenServiceImpl implements FileOpenService {

   private static final Logger logger = Logger.getLogger(FileOpenServiceImpl.class.getName());
 
   /** Creates a new instance of JNLPBasicService */
   public FileOpenServiceImpl() {

   }

   /**
    * Open a File
    */
   public FileContents openFileDialog(final String pathHint, final String[] exts) throws IOException {
      
      FileContents contents = (FileContents)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            
            JFileChooser chooser = new JFileChooser(pathHint);
            int returnVal = chooser.showOpenDialog(null);
            if(returnVal != JFileChooser.APPROVE_OPTION) {
               return null;
            }

            String fileName = chooser.getSelectedFile().getAbsolutePath();
            FileContents contents = new FileContentsImpl(new File(fileName));

            return contents;
         }
         
      });
      
      return contents;
   }
   
   public FileContents[] openMultiFileDialog(String pathHint, String[] exts) throws IOException {
      return null;
   }

}
