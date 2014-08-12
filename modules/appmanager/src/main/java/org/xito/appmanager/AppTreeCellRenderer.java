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

package org.xito.appmanager;

import java.awt.*; 
import javax.swing.*; 
import javax.swing.tree.*;

/**
 *
 * @author DRICHAN
 */
public class AppTreeCellRenderer extends DefaultTreeCellRenderer {

   private ImageIcon openIcon = new ImageIcon(AppTreeCellRenderer.class.getResource("/org/xito/launcher/images/folder_open_maji_16.png"));
   private ImageIcon closedIcon = new ImageIcon(AppTreeCellRenderer.class.getResource("/org/xito/launcher/images/folder_maji_16.png"));
   
   private MyLabel cellLbl;
   
   /** Creates a new instance of AppTreeCellRenderer */
   public AppTreeCellRenderer() {
      cellLbl = new MyLabel();
   }
   
   /**
    * Get Component used to paint cell
    */
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      
      //selected
      if(selected) {
         cellLbl.setForeground(Color.WHITE);
      }
      else {
         cellLbl.setForeground(Color.BLACK);
      }
      
      //Root Node
      if(value == tree.getModel().getRoot()) {
         cellLbl.setText(null);
         cellLbl.setIcon(null);
         
         return cellLbl;
      }
      
      //Group Node
      else if(value instanceof GroupTreeNodeWrapper) {
         cellLbl.setText(value.toString());
         if(expanded) {
            cellLbl.setIcon(this.openIcon);
         }
         else {
            cellLbl.setIcon(this.closedIcon);
         }
      }                                              
      
      //Action Node
      else if(value instanceof ApplicationTreeNodeWrapper) {
         Action action = (Action)((ApplicationTreeNodeWrapper)value).getAction();
         cellLbl.setText(action.toString());
         cellLbl.setIcon((Icon)action.getValue(Action.SMALL_ICON));
      }
                  
      return cellLbl;
   }
   
   private class MyLabel extends JLabel {
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
      }
   }
}
