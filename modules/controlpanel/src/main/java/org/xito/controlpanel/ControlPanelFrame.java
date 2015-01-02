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

import org.xito.boot.*;
import org.xito.dialog.*;

/**
 *
 * @author Deane Richan
 */
public class ControlPanelFrame extends JFrame {
   
   /** Creates a new instance of ControlPanelFrame */
   public ControlPanelFrame() {
      
      init();
   }
   
   private void init() {
      
      setTitle(Boot.getAppDisplayName()+": "+Resources.bundle.getString("frame.title"));
      
      //Setup the Content Pane Panel
      DialogDescriptor desc = new DialogDescriptor();
      desc.setTitle(Boot.getAppDisplayName()+": "+Resources.bundle.getString("frame.title"));
      desc.setSubtitle(Resources.bundle.getString("frame.subtitle"));
      desc.setIcon(new ImageIcon(ControlPanelFrame.class.getResource("org.xito.launcher.images/control_panel.png")));
      //desc.setGradiantColor(org.xito.boot.ui.Defaults.DIALOG_GRAD_COLOR);
      //desc.setGradiantOffsetRatio(org.xito.boot.ui.Defaults.DIALOG_GRAD_OFFSET);
      desc.setCustomPanel(new MainPanel());
      desc.setButtonTypes(new ButtonType[]{new ButtonType("Close", 99)});
      desc.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ControlPanelFrame.this.setVisible(false);
         }
      });
            
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      setContentPane(new DialogPanel(desc));
      setSize(400,400);
      setLocation((screenSize.width - getWidth())/2, (screenSize.height - getHeight())/2);
      
      setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
   }
   
   /***************************************************
    * Main Panel for Control Panel
    ***************************************************/
   public class MainPanel extends JPanel {
      
      public MainPanel() {
         init();
      }
      
      private void init() {
         
         setLayout(new TableLayout(MainPanel.class.getResource("main_layout.html")));
         JLabel descLbl = new JLabel(Resources.bundle.getString("frame.description"));
         add("description", descLbl);
         add("control_panel", new ControlPanel());
      }
      
   }
}
