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

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.xito.boot.util.RuntimeHelper;
import org.xito.dialog.*;

/**
 * Quick Launch will download the necessary files when the BootStrap is launched
 * view WebStart or some other JNLP mechanism.
 *
 * @author Deane Richan
 */
public class QuickLaunch {
   private static final Logger logger = Logger.getLogger(QuickLaunch.class.getName());
   private static final String QUICK_LAUNCH_FILELIST = "quick_launch_file_list.txt";
   private static URL codeBase;
   private static File installDir;
   private static Properties bootProps;
   
   /**
    * Initialize the QuickLaunch 
    */
   protected static void init(URL codebase) {
      
      //check codebase
      QuickLaunch.codeBase = codebase;
      if(codeBase == null) {
         Boot.showError(null, Resources.bundle.getString("quicklaunch.nocodebase"), null);
         Boot.endSession(true);
      }
      
      //download quicklaunch list and files
      List<String> fileList = null;
      try {
         URL fileListURL = new URL(codeBase, QUICK_LAUNCH_FILELIST);
         URL localFileListURL = Boot.getCacheManager().getResource(fileListURL, null, CachePolicy.ALWAYS);
         fileList = processFileList(localFileListURL);
      }
      catch(Exception exp) {
         Boot.showError(null, Resources.bundle.getString("quicklaunch.filelist.error"), exp);
         Boot.endSession(true);
      }
            
      //copy the files to the install dir
      try {
         copyFilesToInstallDir(fileList);
      }
      catch(Exception exp) {
         Boot.showError(null, Resources.bundle.getString("quicklaunch.copy.error"), exp);
         Boot.endSession(true);
      }
      
      //launch BootStrap app
      try {
         launchInstalledBootStrap();
      }
      catch(Exception exp) {
         Boot.showError(null, Resources.bundle.getString("quicklaunch.launch.error"), exp);
         Boot.endSession(true);
      }
       
      Boot.endSession(true);
   }
   
   /**
    * Launch the installed BootStrap that was copied from the FileList
    * This will either launch a native process or a Java process
    */
   private static void launchInstalledBootStrap() throws IOException {
      
      //store current system properties
      File tmpFile = File.createTempFile("ql-xito-props", null);;
      
      //launch new BootStrap
      String javaCmd = AppLauncher.getJavaCommand();  
      String classPath = new File(installDir, "boot.jar").toString();
      
      String cmds[] = null;

      //mac os
      if(Boot.getCurrentOS() == Boot.MAC_OS) {
         String appDockName = Boot.getAppDisplayName();
         cmds = new String[]{javaCmd, "-Xdock:name="+appDockName, "-cp", classPath, "org.xito.boot.Boot",
         "-"+Boot.BOOTDIR_ARG, installDir.toString(), "-"+Boot.PROPS_FILE_ARG, tmpFile.toString()};      
      }
      //other os
      else {
         cmds = new String[]{javaCmd, "-cp", classPath, "org.xito.boot.Boot",
               "-"+Boot.BOOTDIR_ARG, installDir.toString(), "-"+Boot.PROPS_FILE_ARG, tmpFile.toString()};
      }
      //just for logging
      StringBuffer cmdLine = new StringBuffer();
      for(int i=0;i<cmds.length;i++) {
         cmdLine.append(cmds[i]+" ");
      }
      logger.info("Launching VM:"+cmdLine.toString());
      
      //store the temp properties
      //place cmdline in properties
      System.setProperty("quicklaunch_cmdline", cmdLine.toString());
      System.getProperties().store(new FileOutputStream(tmpFile), null);
      
      String env[] = null;
      OutputStream outStream = null;
      OutputStream errStream = null;
      InputStream inStream = null;
      
      //DialogManager.showMessage(null, "QuickLaunch", cmdLine.toString());
      RuntimeHelper.exec(cmds, env, Boot.getBootDir(),  outStream, errStream, inStream);      
   }
   
   /**
    * Copy the downloaded files to the install dir.
    * The install directory is updated from the boot.properties listed
    * in the fileList.
    *
    * @return properties read from installed boot.properties
    */
   private static void copyFilesToInstallDir(List<String> fileList) throws IOException {
      
      //First read the boot.properties for the quick.launch.install.dir
      URL bootPropsURL = new URL(codeBase, "boot.properties");
      File bootPropsFile = Boot.getCacheManager().getCachedFileForURL(bootPropsURL);
      bootProps = new Properties();
      bootProps.load(new FileInputStream(bootPropsFile));
      String quickLaunchInstallDir = bootProps.getProperty("quicklaunch.install.dir");
      if(quickLaunchInstallDir == null) throw new IOException("quicklaunch.install.dir not specified in boot.properties");

      //Get Actual Install Dir for Platform
      if(NativeLibDesc.currentOS() == NativeLibDesc.WINDOWS_OS) {
         installDir = new File("c:/Program Files/"+quickLaunchInstallDir);
      }
      else if(NativeLibDesc.currentOS() == NativeLibDesc.MAC_OS){
         installDir = new File("/Applications/"+quickLaunchInstallDir);
      }
      else {
         installDir = new File("/opt/"+quickLaunchInstallDir);
      }
      if(installDir.exists()==false) {
         if(installDir.mkdirs()==false) {
            throw new IOException("Could not create install directory:"+installDir.toString());
         }
      }
      
      //Copy each File to the install dir
      Iterator fileListIterator = fileList.iterator();
      while(fileListIterator.hasNext()) {
         String fileName = (String)fileListIterator.next();
         URL u = new URL(codeBase, fileName);
         File cachedFile = Boot.getCacheManager().getCachedFileForURL(u);
         copyFile(cachedFile, new File(installDir, fileName));
      }
   }
   
   /**
    * Copy a single file
    * @param fromFile
    * @param toFile
    */
   private static void copyFile(File fromFile, File toFile) throws IOException {
      FileInputStream in = new FileInputStream(fromFile);
      FileOutputStream out = new FileOutputStream(toFile);
      
      //copy file
      byte[] buf = new byte[1024];
      int count = in.read(buf);
      while(count>0) {
         out.write(buf, 0, count);
         count = in.read(buf);
      }
   }
   
   /**
    * Process the File List
    */
   private static List<String> processFileList(URL fileListURL) throws IOException {
      InputStream in = (InputStream)fileListURL.getContent();
      InputStreamReader reader = new InputStreamReader(in);
      LineNumberReader lineReader = new LineNumberReader(reader);
      
      //Get list of File URLS
      List<String> fileList = new ArrayList<String>();
      String line = lineReader.readLine();
      while(line != null) {
         //skip lines that start with # these are comments
         if(!line.startsWith("#") && !line.equals("")) {
            fileList.add(line);
         }
         line = lineReader.readLine();
      }
      
      //Check that at least boot.jar and boot.properties are in the filelist
      if(!fileList.contains("boot.jar") || !fileList.contains("boot.properties")) {
         throw new IOException("quick_launch_file.lst does not contain boot.jar and boot.properties and it should");
      }
      
      //download all files in groups of 4
      List<CacheResource> downloads = new ArrayList<CacheResource>();
      int i = 0;
      for(String fname : fileList) {
         downloads.add(new CacheResource(new URL(codeBase, fname)));
      }

      Boot.getCacheManager().downloadResources("Boot Resources", downloads, null, true);
      return fileList;
   }
   
}
