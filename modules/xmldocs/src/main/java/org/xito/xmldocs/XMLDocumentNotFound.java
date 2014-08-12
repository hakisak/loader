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

/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.3 $
 * @since $Date: 2007/09/02 00:46:37 $
 */
public class XMLDocumentNotFound extends java.lang.Exception
{

    /**
     * Creates new <code>OCXMLDocumentNotFound</code> without detail message.
     */
    public XMLDocumentNotFound() {
    }


    /**
     * Constructs an <code>OCXMLDocumentNotFound</code> with the specified detail message.
     * @param msg the detail message.
     */
    public XMLDocumentNotFound(String msg) {
        super(msg);
    }
}


