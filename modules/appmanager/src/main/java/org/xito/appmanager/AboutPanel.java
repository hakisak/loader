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

/**
 *
 * @author Deane Richan
 */
public class AboutPanel extends JPanel {
    
   Image aboutImg;
   Dimension preferredSize;
   String version;
   
   /** Creates a new instance of AboutPanel */
   public AboutPanel() {
      init();
   }
   
   private void init() {

      setBackground(Color.WHITE);
      setLayout(new BorderLayout());
      aboutImg = new ImageIcon(AboutPanel.class.getResource("/org/xito/launcher/images/about.jpg")).getImage();
      preferredSize = new Dimension(aboutImg.getWidth(null), aboutImg.getHeight(null));
      version = AboutPanel.class.getPackage().getImplementationVersion();
   }
   
   public void paint(Graphics g) {
      super.paint(g);
      
      Dimension s = getSize(); 
      int x = (s.width - preferredSize.width)/2;
      int y = (s.height - preferredSize.height)/2;
      g.drawImage(aboutImg, x, y, null);
      g.drawString("Version: "+version, x + 20, y + preferredSize.height - 50);
   }
   
   public Dimension getPreferredSize() {
      return preferredSize;
   }
   
   public Dimension getMinimumSize() {
      return preferredSize;
   }
}
