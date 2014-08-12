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

package org.xito.controlpanel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author Deane Richan
 */
public class ControlPanel extends JPanel {
   
   private JTable table;
   
   /** Creates a new instance of ControlPanel */
   public ControlPanel() {
      setLayout(new BorderLayout());
      
      table = new JTable(ControlPanelService.getModel(), ControlPanelService.getModel().getColModel());
      table.setShowGrid(false);
      table.getTableHeader().setPreferredSize(new JLabel("dummy").getPreferredSize());
      TableColumn col = table.getColumnModel().getColumn(0); 
      col.setWidth(20);
      col.setCellRenderer(new DefaultTableCellRenderer() {
         public Component getTableCellRendererComponent(JTable table,
                                               Object value,
                                               boolean isSelected,
                                               boolean hasFocus,
                                               int row,
                                               int column) {
            
            JLabel comp = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus,row, column);
            Action action = (Action)value;
            comp.setIcon((Icon)action.getValue(Action.SMALL_ICON));
            comp.setText((String)action.getValue(Action.NAME));
            //comp.setPreferredSize(new Dimension(20,20));
            return comp;
         }
      });
      
      table.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent evt) {
            if(evt.getClickCount()==2) {
               Action a = (Action)table.getValueAt(table.getSelectedRow(), 0);
               a.actionPerformed(new ActionEvent(table, 0, null));
            }
         }
      });
      
            
      JScrollPane sp = new JScrollPane(table);
      sp.getViewport().setBackground(new JTextField().getBackground());
      add(sp);
      
   }
   
}
