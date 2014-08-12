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

/**
 * IBLXStorageHandler is used to interface to a persistence storage mechanism for BLX Documents
 *
 * @author  drichan
 */
public interface BLXStorageHandler {
    
  public static final String PATH_SEP = "/";
  
  /**
   * Return true if a document exists for the specified name
   * in the current path
   * @return true if it exists false otherwise
   */
  public boolean docExists(String docName);
  
  /**
   * Remove a Document from the implementations storage area
   * @param docName name of Document to remove
   */
  public void removeDoc(String docName) throws IOException;
  
  /**
   * Store a document with the given name in the current path
   * @param docName
   * @return IBLXDocument
   */
  public void storeDoc(String docName, Document doc) throws IOException;
  
  /**
   * Get the Current Path that this Handler will store Documents in
   * @return current path
   */
  public String getCurrentPath();
  
  /**
   * Returns a child Storage Handler for the given path. The path should not start with a / or 
   * end with a slash. Starting and ending /'s should be removed by the implementation
   * @param childPath is a single path element. It should not contain / seperators and should
   *   not start with a / seperator. It represents a single namespace beneath this 
   *   handler's  current path.
   * @return new child handler.
   */
  public BLXStorageHandler getChildHandler(String childPath);
  
}
