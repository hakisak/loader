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

package org.xito.dialog;

import java.awt.*;
import java.net.URL;
import java.util.*;
import javax.swing.*;

/**
 * a JPanel implementation that uses a TableLayout by default. Also Users can easily turn on borderlines to 
 * assist in Developement of laying out components.
 *
 * @author Deane Richan
 */
public class TablePanel extends JPanel {
   
   private boolean paintBorderLines_flag = false;
   private boolean antialias_flag = false;
   
   /** Creates a new instance of TablePanel */
   public TablePanel() {
      super.setOpaque(false);
   }
   
   /** Creates a new instance of TablePanel */
   public TablePanel(ArrayList rows) {
      setLayout(new TableLayout(rows));
      super.setOpaque(false);
   }
   
   public TablePanel(TableLayout layout) {
      super(layout);
      super.setOpaque(false);
   }
   
   public TablePanel(URL htmlURL) {
      super(new TableLayout(htmlURL));
      super.setOpaque(false);
   }
   
   public void setAntiAlias(boolean b) {
      antialias_flag = b;
   }
   
   public boolean getAntiAlias() {
      return antialias_flag;
   }
   
   public void setPaintBorderLines(boolean b) {
      paintBorderLines_flag = b;
   }
   
   public boolean getPaintBorderLines() {
      return paintBorderLines_flag;
   }
   
   public void paint(Graphics g) {
      
      super.paint(g); 
       
      if(antialias_flag) {
         Graphics2D g2 = (Graphics2D)g;
         g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)); 
      }
      
      if(paintBorderLines_flag)
         getTableLayout().paintTableLines(this, g);
      
      
   }
   
   public TableLayout getTableLayout() {
      return (TableLayout)getLayout();
   }
}
