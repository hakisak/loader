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

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author Deane Richan
 */
public class ControlPanelModel extends AbstractTableModel {
   
   private ArrayList actions = new ArrayList();
   
   public static int NAME_COL = 0;
   public static int DESC_COL = 1;
   
   public static int COL_COUNT = 2;
   
   private DefaultTableColumnModel colModel;
   
   /** Creates a new instance of ControlPanelModel */
   public ControlPanelModel() {
      
      colModel = new DefaultTableColumnModel();
                  
      TableColumn c1 = new TableColumn(0, 50);
      c1.setHeaderValue("Name");
      c1.setResizable(true);
            
      TableColumn c2 = new TableColumn(1, 120);
      c2.setHeaderValue("Description");
      c2.setResizable(true);
            
      colModel.addColumn(c1);
      colModel.addColumn(c2);
   }

   public Object getValueAt(int row, int col) {
      Action action = (Action)actions.get(row);
      Object value = null;
      
      if(col == NAME_COL) {
         value = action;
      }
      else if(col == DESC_COL) {
         value = action.getValue(Action.SHORT_DESCRIPTION);
      }
      
      return value;
   }
   
   public String getColumnName(int column) {
      return getColModel().getColumn(column).getHeaderValue().toString();
   }

   public int getRowCount() {
      return actions.size();
   }

   public int getColumnCount() {
      return COL_COUNT;
   }
   
   public void addItem(Action action) {
      actions.add(action);
   }
   
   public Action getItem(int index) {
      return (Action)actions.get(index);
   }
   
   public TableColumnModel getColModel() {
            
      return colModel;
   }
   
}
