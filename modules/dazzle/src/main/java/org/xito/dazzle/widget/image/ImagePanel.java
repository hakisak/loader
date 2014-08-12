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

package org.xito.dazzle.widget.image;

import java.awt.Graphics;
import java.awt.Image;
import java.net.*;
import javax.swing.*;

/**
 * This Panel will display an Image. The size of the panel can be defined or 
 * it can be sized to the default size of the image
 * @author deane
 *
 */
public class ImagePanel extends JPanel {

   //Serial Version ID
   private static final long serialVersionUID = 1L;
   private URI imageURI;
   private Image image;
   
   
   public ImagePanel() {
      
   }
   
   public void setImageURI(URI uri) {
      imageURI = uri;
      loadImage();
   }
   
   private void loadImage() {
      
   }
   
   private class ImageWrapperPanel {
      
      public void paintComponent(Graphics g) {
         
      }
   }
}
