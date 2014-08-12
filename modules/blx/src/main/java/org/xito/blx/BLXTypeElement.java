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

import java.net.*;
import org.w3c.dom.*;

/**
 * The BLXTypeElement is used to describe BLX Types that may be used over and over again in a BLX Document.
 * By definging a BLXType, BLXElements can refer to that type by name
 *
 * Sample:
 *
 * <blx:type name="Tile" extension="DCF" ext-href="http://.../dcf.jnlp" class="org.ocd.dcf.DCTile">
 *
 * Now a Tile can be defined as follows:
 *
 * <blx:component type="Tile" id="123456" x="0" y="0" width="48" height="48">
 *
 * @author $Author: drichan $
 * @version $Revision: 1.4 $
 * @since $Date: 2007/09/02 00:42:59 $
 */
public class BLXTypeElement {

  private String name;
  private BLXExtension extension;
  private String className;

  public BLXTypeElement(org.w3c.dom.Element element, URL mainURL) {

  }

  public BLXTypeElement(String name, BLXExtension extension, String className, URL mainURL) {

  }

  public BLXExtension getExtension() {
    return extension;
  }

  public String getName() {
    return name;
  }

  public String getClassName() {
    return className;
  }
}
