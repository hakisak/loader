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

package org.xito.launcher.jnlp;

import java.beans.*;
import javax.swing.*;
import org.xito.launcher.*;

/**
 *
 * @author DRICHAN
 */
public class JavaAppActionBeanInfo extends SimpleBeanInfo {
   
   protected static ImageIcon icon16 = new ImageIcon(LauncherService.class.getResource("/org/xito/launcher/images/java_16.png"));
   protected static ImageIcon icon32 = new ImageIcon(LauncherService.class.getResource("/org/xito/launcher/images/java_32.png"));
   
   private BeanDescriptor desc;
   
   /** Creates a new instance of JavaAppActionBeanInfo */
   public JavaAppActionBeanInfo() {
      desc = new BeanDescriptor(JavaAppAction.class, JavaConfigDialog.class);
      desc.setDisplayName(Resources.javaBundle.getString("action.display.name"));
   }

   /**
    * Get the Icon
    */
   public java.awt.Image getIcon(int iconKind) {

      if(iconKind == ICON_COLOR_16x16 || iconKind == ICON_MONO_16x16)
         return icon16.getImage();
      
      if(iconKind == ICON_COLOR_32x32 || iconKind == ICON_MONO_32x32)
         return icon32.getImage();
      
      return null;
   }

   /**
    * Get the Bean Descriptor
    */
   public BeanDescriptor getBeanDescriptor() {

      return desc;
   }
   
   
   
}
