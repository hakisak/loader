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

package org.xito.launcher.youtube;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import org.xito.boot.Boot;
import org.xito.dialog.*;
import org.xito.launcher.*;

/**
 *
 * @author Deane Richan
 */
public class YouTubeConfigDialog extends CustomDialog {
   
   private YouTubeDesc youTubeDesc;
   private MainPanel mainPanel;
   
   /** Creates a new instance of LocalAppConfigDialog */
   public YouTubeConfigDialog(Frame owner, YouTubeDesc youTubeDesc) {
      super(owner);
      if(youTubeDesc == null) 
         this.youTubeDesc = new YouTubeDesc();
      else
         this.youTubeDesc = youTubeDesc;
      
      descriptor = createDialogDesc();
      super.setModal(true);
      super.setTitle(Resources.youtubeBundle.getString("config.title"));
      super.init();
   }
   
   /**
    * Create the Dialog Desc for this Dialog
    */
   private DialogDescriptor createDialogDesc() {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setType(DialogManager.OK_CANCEL);
      //desc.setGradiantColor(org.xito.boot.ui.Defaults.DIALOG_GRAD_COLOR);
      //desc.setGradiantOffsetRatio(org.xito.boot.ui.Defaults.DIALOG_GRAD_OFFSET);
      desc.setTitle(Resources.youtubeBundle.getString("config.title"));
      desc.setSubtitle(Resources.youtubeBundle.getString("config.subtitle"));
      desc.setIcon(new ImageIcon(this.getClass().getResource("/org/xito/launcher/images/youtube.png")));
      desc.setShowButtonSeparator(true);
      desc.setResizable(true);
      desc.setWidth(Resources.getIntForOS(Resources.youtubeBundle, "config.org.xito.width", Boot.getCurrentOS(), 300));
      desc.setHeight(Resources.getIntForOS(Resources.youtubeBundle, "config.org.xito.height", Boot.getCurrentOS(), 300));
      
      
      mainPanel = new MainPanel();
      desc.setCustomPanel(mainPanel);
      
      return desc;
   }
   
   /**
    * Get this Dialogs WebDesc
    */
   public YouTubeDesc getYouTubeDesc() {
      return this.youTubeDesc;
   }
   
   /**
    * Show the org.xito and update the contents of LocalAppDesc if OK was pressed
    */
   public void show(boolean b) {
      super.show(b);

      int result = super.getResult();
      if(result == DialogManager.OK) {
         mainPanel.updateWebDesc();
      }
      
      return;
   }
   
   /**************************************************
    * The Main Panel of the Dialog
    **************************************************/
   private class MainPanel extends JPanel {
      
      private JLabel errorLbl;
      private JTextField videoIdTF;
      private JTextField nameTF;
      private JTextField titleTF;
     
      public MainPanel() {
         init();
      }
      
      private void updateWebDesc() {
         
         youTubeDesc.setVideioId(videoIdTF.getText());
         youTubeDesc.setName(nameTF.getText());
         youTubeDesc.setTitle(titleTF.getText());
      }
      
      /**
       * Build the Panel
       */
      private void init() {
         
         setLayout(new TableLayout(YouTubeConfigDialog.class.getResource("layout.html")));
                  
         //Description
         JLabel lbl = new JLabel(Resources.youtubeBundle.getString("config.desc.lbl"));
         add("description", lbl);
         
         //Error Label
         errorLbl = new JLabel();
         errorLbl.setOpaque(true);
         errorLbl.setBorder(new LineBorder(SystemColor.controlShadow));
         errorLbl.setBackground(SystemColor.textHighlight);
         errorLbl.setForeground(SystemColor.textHighlightText);
         errorLbl.setVisible(false);
         add("error_lbl", errorLbl);
         
         //Address
         lbl = new JLabel(Resources.youtubeBundle.getString("config.video.lbl"));
         add("id_lbl", lbl);
         videoIdTF = new JTextField();
         add("id", videoIdTF);
         
         //Name
         lbl = new JLabel(Resources.youtubeBundle.getString("config.name.lbl"));
         add("name_lbl", lbl);
         nameTF = new JTextField();
         nameTF.setDocument(new ValidatableDocument(BaseLaunchDesc.MAX_NAME_LENGTH));
         add("name", nameTF);
         
         //Title
         lbl = new JLabel(Resources.youtubeBundle.getString("config.title.lbl"));
         add("title_lbl", lbl);
         titleTF = new JTextField();
         titleTF.setDocument(new ValidatableDocument(BaseLaunchDesc.MAX_DISPLAYNAME_LENGTH));
         add("title", titleTF);
         
         //Read Settings from Desc and populate Panel
         readDesc();
      }
      
      /**
       * Read the Settings from the Desc and populate the Panels
       */
      private void readDesc() {
         videoIdTF.setText(youTubeDesc.getVideoId());
         nameTF.setText(youTubeDesc.getName());
         titleTF.setText(youTubeDesc.getTitle());
      }
   }
}
