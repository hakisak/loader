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

import java.net.*;
import java.io.*;

/**
 *
 * @author  drichan
 * @version
 */
public class Handler extends URLStreamHandler {
  public static final String PROTOCOL = "docservice";
    
  /** Creates new Handler */
  public Handler() {
    
  }
  
  /**
   * opens a Connection to a URL
   */
  protected URLConnection openConnection(URL pURL) throws IOException {
    return new DocServiceURLConnection(pURL);
  }
  
  public class DocServiceURLConnection extends URLConnection {
    public DocServiceURLConnection(URL pURL) {
      super(pURL);
    }
    
    /**
     * connect this URL Connection
     */
    public void connect() throws IOException {
      connected = true;
    }
    
    /**
     * Gets an InputStream to the Document specifed by the URL for
     * This connection
     */
    public InputStream getInputStream() throws IOException {
      return DefaultXMLDocumentService.getDefaultService().getInputStream(url.getPath());
    }
  }
  
}
