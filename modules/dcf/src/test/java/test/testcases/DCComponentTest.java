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

package test.testcases;

import java.net.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import org.xito.boot.*;
import org.xito.dcf.*;
import org.xito.dcf.dnd.*;

import junit.framework.*;


/**
 *
 * @author Deane
 */
public class DCComponentTest extends TestCase {
   
   public DCComponentTest(java.lang.String testName) {
      super(testName);
   }
   
   public static Test suite() {
      TestSuite suite = new TestSuite(DCComponentTest.class);
      return suite;
   }
   
   /**
    * 
    */
   public void testDCComponent() {
      
      DCComponent dcomp = new DCComponent();
      dcomp.setLayout(new FlowLayout());
      dcomp.setBackground(Color.WHITE);
      dcomp.add(new JLabel("Test"));
      dcomp.setSize(100,100);
      dcomp.setLocation(400, 400);
      dcomp.setBorder(new LineBorder(Color.BLACK));
      dcomp.setDraggable(true);
      dcomp.setOnDesktop(true);
      dcomp.setVisible(true);
   }
   
   /**
    * 
    */
   public void testDCTitle() {
      
      DCTile tile = new DCTile();
      tile.setDraggable(true);
      tile.setOnDesktop(true);
      tile.setLocation(100, 500);
      tile.setVisible(true);
   }
   
   /**
    * 
    */
   public void testShutdown() {
      
      DCTile tile = new DCTile();
      DefaultAction action = new DefaultAction() {
         public void actionPerformed(ActionEvent e) {
            Boot.endSession(true);
         }
      };
      
      action.putValue(Action.NAME, "Exit");
      action.putValue(Action.SHORT_DESCRIPTION, "Exit");
      tile.setAction(action);
      tile.setDraggable(true);
      tile.setOnDesktop(true);
      tile.setLocation(0,0);
      tile.setVisible(true);
   }
   
   /**
    * 
    */
   public void testTileGroup() {
      
      DCComponent dcomp = new DCComponent();
      dcomp.setLayout(new BorderLayout());
      dcomp.setBackground(Color.WHITE);
      dcomp.setSize(400,100);
      dcomp.setBorder(new LineBorder(Color.BLACK));
      dcomp.setDraggable(true);
      dcomp.setLocation(800, 300);
      dcomp.setOnDesktop(true);
      dcomp.add(new JLabel("Group"), BorderLayout.NORTH);
      
      final JPanel p = new JPanel(new FlowLayout());
      new DCDropTarget(p, new DropTargetAdapter() {
          public void drop(DropTargetDropEvent e){
             try {
                DCComponent comp = (DCComponent)e.getTransferable().getTransferData(DCComponent.DCF_REF_FLAVOR);
                comp.setOnDesktop(false);
                p.add(comp);
                e.dropComplete(true);
             }
             catch(Exception exp) {
                exp.printStackTrace();
                e.rejectDrop();
                e.dropComplete(false);
             }
             
             p.validate();
             p.repaint();
          }
      });
      
      dcomp.add(p);
      for(int i=0;i<5;i++) {
         DCTile tile = new DCTile();
         tile.setTitle(""+i);
         p.add(tile);
      }
            
      dcomp.setVisible(true);
   }
   
   public void testDCMenu() {

      DCMenuModel model1 = new DCMenuModel("Menu1");
      model1.add(new TestAction("Item 1"));
      model1.add(new TestAction("Item 2"));
      model1.add(new TestAction("Item 3"));
      model1.add(new AbstractAction("Exit") {
         public void actionPerformed(ActionEvent evt) {
            System.exit(0);
         }
      });

      DCMenuModel subMenuModel = new DCMenuModel("SubMenu");
      subMenuModel.add(new TestAction("Item 1"));
      subMenuModel.add(new TestAction("Item 2"));
      subMenuModel.add(new TestAction("Item 3"));
      
      model1.add(subMenuModel);
            
      DCMenu menu = new DCMenu(model1, true);
      menu.setOnDesktop(true);
      menu.setVisible(true);
      menu.setLocation(100,300);
   
   }
     
   
   //---------------------------------
   
   public class TestAction extends AbstractAction {

      public TestAction(String name) {
         putValue(Action.NAME, name);
      }
      
      /* (non-Javadoc)
       * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
       */
      public void actionPerformed(ActionEvent arg0) {
         // TODO Auto-generated method stub
         
      }
      
   }
   
}
