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

package org.xito.dcf;

import java.beans.*;

/**
 *
 * @author  drichan
 * @version
 */
public class DCTileBeanInfo extends SimpleBeanInfo
{
  //Property Descriptors
  private static PropertyDescriptor[] props;
  static {
  
    //Create Property Descriptors
    int i=0;
    props = new PropertyDescriptor[3];
    try {
      props[i] = new PropertyDescriptor("ShowTitle", DCTile.class);
      props[i++].setBound(true);
      props[i] = new PropertyDescriptor("Title", DCTile.class);
      props[i++].setBound(true);
      props[i] = new PropertyDescriptor("Name", DCTile.class);
      props[i++].setBound(true);
    }
    catch(IntrospectionException _exp) {
      _exp.printStackTrace();
    }
  }
  
  //BeanDescriptor
  private static BeanDescriptor beanDesc = new BeanDescriptor(DCTile.class);
  static {
    beanDesc.setDisplayName("Tile");
  }
  
  public PropertyDescriptor[] getPropertyDescriptors() {
    return props;
  }
  
  /**
   * Get the BeanDescriptor for this Bean
   * @return BeanDescriptor
   */
  public BeanDescriptor getBeanDescriptor() {
    return beanDesc;
  }
}
