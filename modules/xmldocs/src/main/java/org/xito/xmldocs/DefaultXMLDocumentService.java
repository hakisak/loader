// Copyright 2007 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.xmldocs;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.security.*;
import javax.xml.parsers.*;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.*;
import org.w3c.dom.*;
import org.xito.boot.*;

/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.9 $
 * @since $Date: 2008/03/28 05:57:07 $
 */
public class DefaultXMLDocumentService implements XMLDocumentService {

   private final Logger logger = Logger.getLogger(DefaultXMLDocumentService.class.getName());
   private static DefaultXMLDocumentService singleton;
   
   private DocumentBuilderFactory buildFactory;
   private File docDir;
   private Handler handler;
   private TransformerFactory transformerFactory;
   private Transformer xformer;
   /**
    * Create the Document Service
    */
   private DefaultXMLDocumentService(File dir){
      
      try {
         buildFactory = DocumentBuilderFactory.newInstance();
         transformerFactory = TransformerFactory.newInstance();
         //URL xslURL = DefaultXMLDocumentService.class.getResource("prettyPrint.xsl");
         //prettyPrintTransformer = transformerFactory.newTransformer(new StreamSource(xslURL.toString()));
         //prettyPrintTransformer = transformerFactory.newTransformer();
         xformer = transformerFactory.newTransformer();
         URLStreamManager _manager = URLStreamManager.getDefaultManager();
         _manager.addProtocolHandler("docservice", new Handler());
      }
      catch(Exception exp) {
         logger.log(Level.SEVERE, exp.toString(), exp);
      }
      
      //Get DocDir
      if(dir == null) {
         
         //Get Env Dir
         File _envDir = Boot.getUserAppDir();
         docDir = new File(_envDir, "docs");
      }
      else {
         docDir = dir;
      }
      
      if(docDir.exists() == false) docDir.mkdir();
   }
   
   /**
    * Get the Default Document Service
    */
   public static XMLDocumentService getDefaultService() {
      if(singleton == null) singleton = new DefaultXMLDocumentService(null);
      return singleton;
   }
   
   /**
    * List Documents in a Path
    * @param pPath parent path you want to retrieve document names from
    * @return Array of Strings
    */
   public String[] listDocuments(String pPath) throws IOException {
      String path = pPath.replace(PATH_SEPERATOR, File.separatorChar);
      File file = new File(docDir, path);
      
      //Return null if Path not a directory
      if(file.isDirectory() == false) return null;
      
      File files[] = file.listFiles();
      ArrayList names = new ArrayList();
      for(int i=0;i<files.length;i++) {
         File f = files[i];
         if(f.isFile() && f.exists() && f.getName() != null) {
            names.add(f.getName());
         }
      }
      
      return (String[])names.toArray(new String[0]);
   }
   
   /**
    * List Sub Directories in a Path
    * @param pPath parent path you want to retrieve sub dirs names from
    * @return Array of Strings
    */
   public String[] listSubDirectories(String pPath) throws IOException {
      
      File dir = new File(docDir, pPath);
      if(dir.exists() == false || dir.isFile()) {
         return new String[0];
      }
      
      ArrayList dirs = new ArrayList();
      File files[] = dir.listFiles();
      for(int i=0;i<files.length;i++) {
         if(files[i].isDirectory() && !files[i].isHidden()) {
            dirs.add(files[i].getName());
         }
      }
      
      return (String[])dirs.toArray(new String[]{});
   }
   
   /**
    * Removes a Document
    * @param pPath file name of document you want to store
    */
   public void removeDocument(String pPath) throws IOException {
      
      String path = pPath.replace(PATH_SEPERATOR, File.separatorChar);
      File file = new File(docDir, path);
      System.out.println("Removing Document:"+file.toString());
      if(file.exists()) file.delete();
   }
   
   /**
    * Removes a Directory
    * @param pPath file name of Directory you want to delete
    */
   public void removeDir(String pPath) throws IOException {
      
      String path = pPath.replace(PATH_SEPERATOR, File.separatorChar);
      File file = new File(docDir, path);
      if(file.exists() == false) return;
      if(file.isFile() || !file.isDirectory()) 
         throw new IOException("Path:"+path+" is not a Directory");
      
      if(deleteTree(file)==false) {
         throw new IOException("Could not delete Directory:"+path);
      }
   }
   
      /**
    * Rename a Directory
    * @param pPath file name of Directory you want to rename
    */
   public void moveDir(String path, String newPath) throws IOException {
      
      String fixed_path = path.replace(PATH_SEPERATOR, File.separatorChar);
      String fixed_newPath = newPath.replace(PATH_SEPERATOR, File.separatorChar);
      File file = new File(docDir, fixed_path);
      if(file.exists() == false) return;
      if(file.isFile() || !file.isDirectory()) 
         throw new IOException("Path:"+path+" is not a Directory");
      
      if(file.renameTo(new File(docDir, fixed_newPath))==false) {
         throw new IOException("Could not rename Directory to:"+newPath);
      }
   }
   
   
   /**
    * Create a Directory
    * @param pPath file name of Directory you want to create
    */
   public void createDir(String pPath) throws IOException {
      String path = pPath.replace(PATH_SEPERATOR, File.separatorChar);
      File file = new File(docDir, path);
      if(file.exists()) {
         throw new IOException("Can't create Directory File already exists");
      }
      
      if(file.mkdirs()==false) {
         throw new IOException("Could not Create Directory:"+path);
      }
   }
   
   /**
    * Return true if the Document Exists
    */
   public boolean documentExists(String pPath) {
      //Setup File Name
      String _path = pPath.replace(PATH_SEPERATOR, File.separatorChar);
      File _file = new File(docDir, _path);
      return _file.exists();
   }
   
   /**
    * Return true if the Directory Exists
    */
   public boolean dirExists(String pPath) {
      //Setup File Name
      String _path = pPath.replace(PATH_SEPERATOR, File.separatorChar);
      File _file = new File(docDir, _path);
      return (_file.exists() && _file.isDirectory());
   }
   
   /**
    * Get a Document from the Specified Path
    */
   public Document getDocument(String pPath) throws XMLDocumentNotFound, IOException {
      //Setup File Name
      String path = pPath.replace(PATH_SEPERATOR, File.separatorChar);
      File file = new File(docDir, path);
      Document doc = null;
      
      //If the File doesn't exist
      if(file.exists()==false) throw new XMLDocumentNotFound("Document: "+pPath+" not Found");
      
      try {
         DocumentBuilder builder = buildFactory.newDocumentBuilder();
         doc = builder.parse(new org.xml.sax.InputSource(new FileInputStream(file)));
      }
      catch(FileNotFoundException notFound) {
         System.err.println("Error processing: "+pPath);
         throw new XMLDocumentNotFound("Document: "+pPath+" not Found");
      }
      catch(ParserConfigurationException configExp) {
         System.err.println("Error processing: "+configExp);
         throw new IOException(configExp.getMessage());
      }
      catch(SAXException sax) {
         System.err.println("Error processing: "+pPath);
         throw new IOException(sax.getMessage());
      }
      
      return doc;
   }
   
   public void storeDocument(final String pPath, final Document pDocument) throws IOException {
      // fix to actually throw IOException and be safe
      final IOException ioExp[] = new IOException[1];
      PrivilegedAction storeDocument = new PrivilegedAction() {
         public Object run() {
            String path = pPath;
            
            //Strip off first /
            if(path.startsWith(""+PATH_SEPERATOR)==false) {
               path = path.substring(0);
            }
            
            createPath(path);
            
            String _path = path.replace(PATH_SEPERATOR, File.separatorChar);
            File _file = new File(docDir, _path);
            System.out.println("Storing Document:"+_file.toString());
            
            try {
               FileOutputStream out = new FileOutputStream(_file);
               writeDocument(pDocument, out);
               out.close();
            }
            catch(IOException _exp) {
               ioExp[0] = _exp;
            }
            
            return null;
         }
      };
      
      //Do the Action
      AccessController.doPrivileged(storeDocument);
      if(ioExp[0] != null) throw ioExp[0];
   }
   
   /**
    * Write DOM Document to Output Stream
    * @param stream to write Output to.
    */
   private synchronized void writeDocument(Document pDoc, OutputStream pStream) throws IOException {
      
      try {
         //Prepare the DOM document for writing
         Source source = new DOMSource(pDoc);

         // Prepare the output file
         Result result = new StreamResult(pStream);

         // Write the DOM document to the file
         xformer.transform(source, result);
         pStream.flush();
         
         //prettyPrintTransformer.transform(new DOMSource(pDoc.getDocumentElement()), new StreamResult(pStream));
         //pStream.flush();
      }
      catch(TransformerException exp) {
         logger.log(Level.SEVERE, exp.toString(), exp);
         throw new IOException(exp.toString());
      }

      
      //XMLSerializer _s = new org.apache.xml.serialize.XMLSerializer(pStream, new OutputFormat(pDoc, "UTF-8", true));
      //_s.serialize(pDoc);
   }
   
   private void createPath(String pPath) {
      StringTokenizer _tokens = new StringTokenizer(pPath, ""+PATH_SEPERATOR);
      if(_tokens.countTokens()==1) return;
      int count = _tokens.countTokens()-1;
      File _dir = docDir;
      for(int i=0;i<count;i++) {
         String _dirStr = _tokens.nextToken();
         _dir = new File(_dir, _dirStr);
         _dir.mkdir();
      }
   }
   
   private boolean deleteTree(File dir) {
      
      if(dir.isFile()) {
         return dir.delete();
      }
      
      File contents[] = dir.listFiles();
      for(int i=0;i<contents.length;i++) {
         if(contents[i].isFile()) {
            contents[i].delete();
         }
         else if(contents[i].isDirectory()) {
            deleteTree(contents[i]);
         }
      }
      
      //Now delete the dir
      return dir.delete();
   }
   
   /**
   * Gets an OutputStream to a document
   * @param pPath file name of document you want an OutputStream to
   * @return OutputStream
   */
  public OutputStream getOutputStream(final String pPath) throws IOException {
     // fix to actually throw IOException and be safe
      final IOException ioExp[] = new IOException[1];
      PrivilegedAction storeDocument = new PrivilegedAction() {
         public Object run() {
            String path = pPath;
            
            //Strip off first /
            if(path.startsWith(""+PATH_SEPERATOR)==false) {
               path = path.substring(0);
            }
            
            createPath(path);
            
            String _path = path.replace(PATH_SEPERATOR, File.separatorChar);
            File _file = new File(docDir, _path);
            System.out.println("Getting OutputStream to:"+_file.toString());
            
            try {
               return new FileOutputStream(_file, false);
            }
            catch(IOException _exp) {
               ioExp[0] = _exp;
            }
            
            return null;
         }
      };
      
      //Do the Action
      OutputStream out = (OutputStream)AccessController.doPrivileged(storeDocument);
      if(ioExp[0] != null) throw ioExp[0];
      
      return out;
  }
   
   /**
    * Gets an InputStream to a document
    * @param pPath file name of document you want an inputstream to
    * @return InputStream
    */
   public InputStream getInputStream(String pPath) throws IOException {
      
      String _path = pPath.replace(PATH_SEPERATOR, File.separatorChar);
      File _file = new File(docDir, _path);
            
      //If its a directory then return null
      if(_file.isDirectory()) return null;
      
      //If the File doesn't exist
      if(_file.exists()==false) throw new IOException("Document: "+pPath+" not Found");
      
      return new FileInputStream(_file);
   }
   
   /**
    * Get the URLStreamHandler for the DocSerice
    * @return handler for this Service
    */
   public URLStreamHandler getHandler() {
      if(this.handler == null) handler = new Handler();
      
      return this.handler;
   }
}
