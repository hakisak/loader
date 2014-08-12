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

package org.xito.dazzle.widget.button;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Button that is made up of Images
 * 
 * @author Deane Richan
 */
public class ImageButton extends AbstractButton {

   // Serial Version ID
   private static final long serialVersionUID = 1L;

   private ImageIcon icon;

   private ImageIcon iconPressed;

   private ImageIcon iconDisabled;

   private ImageIcon currentIcon;

   private HashSet actionListeners;

   private String actionCommand = "";

   protected ImageButton() {
   }

   public ImageButton(ImageIcon icon, ImageIcon iconPressed, ImageIcon iconDisabled) {
      setIcons(icon, iconPressed, iconDisabled);
      init();
   }

   public ImageButton(ImageIcon icon, ImageIcon iconPressed) {
      setIcons(icon, iconPressed, null);
      init();
   }

   public ImageButton(URL iconResource) {
      this(new ImageIcon(iconResource), null, null);
   }

   public ImageButton(URL iconResource, URL iconPressedResource) {
      this(new ImageIcon(iconResource), new ImageIcon(iconPressedResource), null);
   }

   public ImageButton(URL iconResource, URL iconPressedResource, URL iconDisabledResource) {
      this(new ImageIcon(iconResource), new ImageIcon(iconPressedResource), new ImageIcon(iconDisabledResource));
   }

   /**
    * Initialize this component
    */
   protected void init() {
      setOpaque(false);
      setCursor(Cursor.getDefaultCursor());
      addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent e) {
            if (!ImageButton.this.isEnabled())
               return;

            currentIcon = ImageButton.this.iconPressed;
            invalidate();
            repaint();
         }

         public void mouseReleased(MouseEvent e) {
            if (!ImageButton.this.isEnabled())
               return;

            currentIcon = ImageButton.this.icon;
            invalidate();
            repaint();
         }

         public void mouseClicked(MouseEvent e) {
            fireActionEvent();
         }
      });
   }

 
   /**
    * Fire an ActionEvent to all action listeners
    */
   private void fireActionEvent() {

      ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionCommand);
      fireActionPerformed(evt);
   }

   public void setIcons(ImageIcon icon, ImageIcon iconPressed, ImageIcon iconDisabled) {
      this.icon = icon;
      this.iconPressed = iconPressed;
      this.iconDisabled = iconDisabled;
      this.currentIcon = icon;
      invalidate();
      repaint();
   }

   public void paintComponent(Graphics g) {
      
      Insets insets = new Insets(0,0,0,0);
      if(getBorder() != null) {
         insets = getBorder().getBorderInsets(this);
      }
      
      if (currentIcon != null) {
         currentIcon.paintIcon(this, g, insets.left, insets.top);
      } else {
         icon.paintIcon(this, g, insets.left, insets.top);
      }
      
   }

   public Dimension getPreferredSize() {
      
      Dimension size = new Dimension(icon.getIconWidth(), icon.getIconHeight());
      
      if(getBorder() != null) {
         Insets insets = getBorder().getBorderInsets(this);
         size.width = size.width + insets.left + insets.right;
         size.height = size.height + insets.top + insets.bottom;
      }
      
      return size;
   }

   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      if (enabled) {
         currentIcon = ImageButton.this.icon;
      } else if (ImageButton.this.iconDisabled != null) {
         currentIcon = ImageButton.this.iconDisabled;
      }
      invalidate();
      repaint();
   }
 
}
