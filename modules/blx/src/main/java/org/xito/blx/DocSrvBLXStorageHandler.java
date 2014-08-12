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

package org.xito.blx;

import java.io.*;
import org.w3c.dom.*;
import org.xito.xmldocs.*;

/**
 * Implements a BLXStorageHandler that uses the XMLDocument Service to access 
 * documents.
 *
 * @author  drichan
 */
public class DocSrvBLXStorageHandler implements BLXStorageHandler {
  
  private XMLDocumentService docService;
  private String currentPath;
  
  /**
   * Create a new BLXDocument Factory that uses the XMLDocument Service as its 
   * backend
   */
  private DocSrvBLXStorageHandler(String path, XMLDocumentService docService) {
    this.currentPath = path;
    this.docService = docService;
  }
  
  /**
   * Get a BLXDocument Factory for the Root Path of the default XML Document Service
   * @return XMLBLXDocFactory
   */
  public static DocSrvBLXStorageHandler getRootDocHandler() {
    return new DocSrvBLXStorageHandler(null, DefaultXMLDocumentService.getDefaultService());
  }
     
  /**
   * Get the Current Path that this Document Factory will create new Documents in
   * @return current path
   */
  public String getCurrentPath() {
    return currentPath;
  }
  
  /**
   * Return true if a document exists for the specified name
   * in the current path
   * @return true if it exists false otherwise
   */
  public boolean docExists(String docName) {
      
     if(docName.startsWith(PATH_SEP)) {
       return docService.documentExists(docName);
     }
     else {
       return docService.documentExists(getCurrentPath() + PATH_SEP + docName);
     }
  }
  
  /**
   * Remove a Document from the implementations storage area
   * @param docName name of Document to remove
   */
  public void removeDoc(String docName) throws IOException {
     
    docService.removeDocument(getCurrentPath() + PATH_SEP + docName);
  }
  
  /** Returns a child Storage Handler for the given path. The path should not start with a / or
   * end with a slash. Starting and ending /'s should be removed by the implementation
   * @param childPath is a single path element. It should not contain / seperators and should
   *   not start with a / seperator. It represents a single namespace beneath this
   *   handler's  current path.
   * @return new child handler.
   *
   */
  public BLXStorageHandler getChildHandler(String childPath) {
    
    //fix initial sep character. ChildPaths should not start with a /
    if(childPath.startsWith(PATH_SEP))
      childPath = childPath.substring(1);
    
    //fix last sep character. ChildPaths should not end with a /
    if(childPath.endsWith(PATH_SEP))
      childPath = childPath.substring(0, childPath.length()-1);
    
    if(getCurrentPath() == null)
      return new DocSrvBLXStorageHandler(PATH_SEP+childPath, docService);
    else
      return new DocSrvBLXStorageHandler(getCurrentPath()+PATH_SEP+childPath, docService);
  }
  
  /** Store a document with the given name in the current path
   * @param docName
   * @return IBLXDocument
   *
   */
  public void storeDoc(String docName, Document doc) throws IOException {
    String name = null;
    if(getCurrentPath() == null)
      name = PATH_SEP + docName;
    else
      name = getCurrentPath() + PATH_SEP + docName;
    
   docService.storeDocument(name, doc); 
  }
  
}
