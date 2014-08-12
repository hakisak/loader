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
 * BLXActionDataFlavor represents a DataFlavor for a BLXObject that is a javax.swing.Action. 
 * The object managed by this data flavor should implement the BLXObject and Action interfaces
 *
 * @author drichan
 * @version $Revision: 1.5 $
 * @since $Date: 2007/09/02 00:43:00 $
 */
public class BLXActionDataFlavor extends BLXDataFlavor {

  public final static String OBJECT_FLAVOR_NAME = "blx.action";
  public final static String OBJECT_MIME_TYPE = "application/x-blx-action-object; class=javax.swing.Action";
  public final static BLXActionDataFlavor OBJECT_FLAVOR = new BLXActionDataFlavor(OBJECT_MIME_TYPE, OBJECT_FLAVOR_NAME);

  /**
   * Create the DataFlavor
   * @param pMimeType
   * @param pName of DataFlavor
   */
  public BLXActionDataFlavor(String pMimeType, String pName) {
    super(pMimeType, pName);
  }
}

