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

import java.awt.datatransfer.*;

/**
 * The BLXDataFlavor is used to describe any BLXObject that is maintained by a transferable Object. All
 * DataFlavors of custom BLXObjects should extend this class.
 *
 * @author drichan $
 * @version $Revision: 1.7 $
 * @since $Date: 2007/09/02 00:42:59 $
 */
public class BLXDataFlavor extends DataFlavor {

  public final static String XML_MIME_TYPE = "text/xml";
  public final static String XML_FLAVOR_NAME = "blx.xml";
  public final static BLXDataFlavor XML_FLAVOR = new BLXDataFlavor(XML_MIME_TYPE, XML_FLAVOR_NAME);
  
  public final static String ACTION_FLAVOR_NAME = "blx.action";
  public final static String ACTION_MIME_TYPE = "text/xml";
  public final static BLXDataFlavor ACTION_FLAVOR = new BLXDataFlavor(ACTION_MIME_TYPE, ACTION_FLAVOR_NAME);
  
  /**
   * Create the DataFlavor
   * @param pMimeType
   * @param pName of DataFlavor
   */
  public BLXDataFlavor(String pMimeType, String pName) {
    super(pMimeType, pName);
  }
  
  /**
   * Create the DataFlavor for a local reference class
   * @param pClass
   * @param pName of DataFlavor
   */
  public BLXDataFlavor(Class pClass, String pName) {
    super(pClass, pName);
  }
}

