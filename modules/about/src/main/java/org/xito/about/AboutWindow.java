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

package org.xito.about;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.xito.boot.*;

/**
 *
 * @author Deane Richan
 */
public class AboutWindow extends JDialog {
   
   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   JTabbedPane tabs;
   
   /** Creates a new instance of AboutWindow */
   public AboutWindow(Frame owner, JPanel infoPanel) {
      super(owner, "About: "+Boot.getAppDisplayName());
      super.setModal(true);
      super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      
      init(infoPanel);
   }
   
   private void init(JPanel infoPanel) {
      getContentPane().setLayout(new BorderLayout());
      
      JButton close = new JButton("Close");
      close.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            AboutWindow.this.dispose();
         }
      });
      
      tabs = new JTabbedPane();
      tabs.addTab("Information", infoPanel);
      
      JEditorPane editorPane = new JEditorPane();
      editorPane.setContentType("text/html");
      editorPane.setText(AboutService.getAllServiceInfo());
      editorPane.setEditable(false);
      
            
      JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      bottom.add(close);
      getContentPane().add(bottom, BorderLayout.SOUTH);
      getContentPane().add(tabs);
      
      pack();
            
      tabs.addTab("Details", new JScrollPane(editorPane));
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      setLocation((screenSize.width - this.getWidth()) / 2, (screenSize.height - this.getHeight())/2);
   }
         
}
