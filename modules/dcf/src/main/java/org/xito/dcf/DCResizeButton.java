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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * << License Info Goes HERE >> All Rights Reserved.
 * CloseButton
 * Description:
 *
 * @author  $Author: drichan $
 * @version $Revision: 1.5 $
 * @since   $Date: 2007/11/28 03:52:40 $
 */
public class DCResizeButton extends JComponent
{
   Component component;
   //ImageIcon resizeImage;
   boolean resizing_flag = false;
   Point startLoc;
   Dimension startSize;

   public DCResizeButton(Component pComponent)
   {
     component = pComponent;

     //resizeImage = new ImageIcon(getClass().getResource("org.xito.launcher.images/resize.gif"));
     Dimension _size = new Dimension(16,16);
     setSize(_size);
     setPreferredSize(_size);
     setMaximumSize(_size);
     setMinimumSize(_size);

     addMouseListener(new MouseAdapter(){
         public void mousePressed(MouseEvent pEvent){
           resizing_flag = true;
           startLoc = pEvent.getPoint();
           SwingUtilities.convertPointToScreen(startLoc, pEvent.getComponent());
           startSize = component.getSize();
         }
         public void mouseReleased(MouseEvent pEvent){
           resizing_flag = false;
           //component.setSize(startSize.width+20, startSize.height+20);
         }
     });

     addMouseMotionListener(new MouseMotionAdapter(){
          public void mouseDragged(MouseEvent pEvent)
          {
            if(resizing_flag == false) return;
            Point _point = pEvent.getPoint();
            SwingUtilities.convertPointToScreen(_point, pEvent.getComponent());
            component.setSize(startSize.width+(_point.x - startLoc.x), startSize.height+(_point.y - startLoc.y));
            component.repaint();
          }
     });
   }

   protected void paintComponent(Graphics g)
   {
      Graphics2D g2 = (Graphics2D)g;
      g2.setColor(Color.WHITE);
      g2.fillRect(0,0, getWidth(), getHeight());
      g2.setColor(Color.GRAY);
      g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      
      for(int i=0;i<=getWidth();i+=4) {
         g2.drawLine(i, getHeight(), getWidth(), i);
      }
      
   }
 }



