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
import java.net.*;
import java.security.*;
import java.util.logging.*;
import javax.jnlp.*;
import javax.swing.*;

import org.xito.boot.*;
import org.xito.launcher.*;
import org.xito.launcher.jnlp.*;


/**
 *
 * @author Deane Richan
 */
public class FileSaveServiceImpl implements FileSaveService {

   private static final Logger logger = Logger.getLogger(FileSaveServiceImpl.class.getName());
         
   /** Creates a new instance of JNLPBasicService */
   public FileSaveServiceImpl() {
   
   }

   /**
    * Show Save As Dialog
    */
   public FileContents saveAsFileDialog(final String pathHint, final String[] exts, FileContents fileContents) throws IOException {
      
      FileContents contents = (FileContents)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            
            JFileChooser chooser = new JFileChooser(pathHint);
            int returnVal = chooser.showSaveDialog(null);
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

   /**
    * Show Save Dialog and Save the File
    */
   public FileContents saveFileDialog(final String pathHint, final String[] exts, final InputStream in, final String name) throws IOException {
      Object obj = (FileContents)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            
            JFileChooser chooser = new JFileChooser(pathHint);
            int returnVal = chooser.showSaveDialog(null);
            if(returnVal != JFileChooser.APPROVE_OPTION) {
               return null;
            }

            String fileName = chooser.getSelectedFile().getAbsolutePath();
            FileContents contents = new FileContentsImpl(new File(fileName));
            try {
               OutputStream out = contents.getOutputStream(true);
               byte[] buf = new byte[1024];
               int c = in.read(buf);
               while(c != -1) {
                  out.write(buf,0,c);
                  c = in.read(buf);
               }
               
               out.close();
            }
            catch(IOException ioExp) {
               return ioExp;
            }

            return contents;
         }
         
      });
      
      //Return the contents or throw the exception
      if(obj == null) {
         return null;
      }
      else if(obj instanceof IOException) {
         throw (IOException)obj;
      }
      else if(obj instanceof FileContents) {
         return (FileContents)obj;
      }
      
      return null;
   }
}
