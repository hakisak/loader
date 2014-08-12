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
import org.w3c.dom.Document;

/**
 *
 * @author Deane Richan
 * @since $Date: 2007/11/13 05:51:10 $
 */
public interface XMLDocumentService {
  
  public static final String PROTOCOL = "docservice";
  public static final char PATH_SEPERATOR = '/';
  
  /**
   * Gets an OutputStream to a document
   * @param fullPath file name of document you want an OutputStream to
   * @return OutputStream
   */
  public OutputStream getOutputStream(String fullPath) throws IOException;
  
  /**
   * Gets an InputStream to a document
   * @param fullPath file name of document you want an inputstream to
   * @return InputStream
   */
  public InputStream getInputStream(String fullPath) throws IOException;
  
  /**
   * Return true if the Document Exists
   */
  public boolean documentExists(String fullPath);
  
  /**
    * Return true if the Directory Exists
    */
   public boolean dirExists(String fullPath);
  
  /**
   * Gets a Document
   * @param fullPath file name of document you want to get
   * @return DOM Document
   */
  public Document getDocument(String fullPath) throws XMLDocumentNotFound, IOException;
  
  /**
   * Stores a Document
   * @param fullPath file name of document you want to store
   * @param pDocument DOM Document object for the Document
   */
  public void storeDocument(String fullPath, Document pDocument) throws IOException;
  
  /**
   * Removes a Document
   * @param fullPath file name of document you want to store
   */
  public void removeDocument(String fullPath) throws IOException;
  
  /**
    * Removes a Directory
    * @param fullPath file name of Directory you want to delete
    */
   public void removeDir(String fullPath) throws IOException;
   
   /**
    * Rename/Move a Directory
    * @param file name of Directory you want to rename
    ** @param new file name of Directory 
    */
   public void moveDir(String path, String newPath) throws IOException;
   
   /**
    * Create a Directory
    * @param fullPath file name of Directory you want to create
    */
   public void createDir(String fullPath) throws IOException;
  
  /**
   * List Documents in an Path
   * @param fullPath parent path you want to retrieve document names from
   * @return Array of Strings
   */
  public String[] listDocuments(String fullPath) throws IOException;
  
  /**
   * List Sub Directories in a Path
   * @param fullPath parent path you want to retrieve sub dirs names from
   * @return Array of Strings
   */
  public String[] listSubDirectories(String fullPath) throws IOException;
  
  /**
   * Get the URLStreamHandler for the DocService
   * @return handler for this Service
   */
  public URLStreamHandler getHandler();
  
}

