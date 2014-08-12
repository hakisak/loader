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

package org.xito.boot.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;
import javax.swing.*;

import org.xito.boot.*;
import org.xito.dialog.*;
import org.xito.miglayout.swing.MigLayout;

/**
 *
 * @author  Deane
 */
public class DownloadProgressFrame extends JFrame implements CacheListener {
   
   private static final String DOWNLOAD_FRAME_H = "download.frame.h";
   private static final String DOWNLOAD_FRAME_W = "download.frame.w";
   private static final String DOWNLOAD_FRAME_Y = "download.frame.y";
   private static final String DOWNLOAD_FRAME_X = "download.frame.x";
   private static Logger logger = Logger.getLogger(DownloadProgressFrame.class.getName()); 
   private final static int COMPLETE_DELAY = 2000;
      
   private DownloadProgressPanel panel;
   private JButton hideBtn = new JButton("Hide");
   private CacheManager cacheManager = null;
   private JPanel content;
      
   public DownloadProgressFrame(CacheManager cm) {
      this(null, cm);
   }
  
   /** Creates a new instance of DownloadProgressFrame */
   public DownloadProgressFrame(String title, CacheManager cm) {
      
      cacheManager = cm;
      if(title != null) {
         setTitle(title);
      }
      else {
         setTitle(Boot.getAppDisplayName()+" Download Manager");
      }
      
      setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      
      //JPanel content = new JPanel(new TableLayout(DownloadProgressFrame.class.getResource("download_frame_layout.html")));
      content = new JPanel(new MigLayout("", "[grow, fill]", "[grow, fill] []"));
      setContentPane(content);
      panel = new DownloadProgressPanel();
      content.add(new JScrollPane(panel), "wrap");
      content.add(hideBtn, "grow 0 0, align right");
      hideBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            DownloadProgressFrame.this.setVisible(false);
         }});
      
      this.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent e) {
            cacheManager.getCacheProperties().put(DOWNLOAD_FRAME_W, Integer.toString(getWidth()));
            cacheManager.getCacheProperties().put(DOWNLOAD_FRAME_H, Integer.toString(getHeight()));
            cacheManager.storeCacheProperties();
         }
         public void componentMoved(ComponentEvent e) {
            cacheManager.getCacheProperties().put(DOWNLOAD_FRAME_X, Integer.toString(getX()));
            cacheManager.getCacheProperties().put(DOWNLOAD_FRAME_Y, Integer.toString(getY()));
            cacheManager.storeCacheProperties();
         }
      });
      
      
      
   }
   
   private void resizeForItems() {

      pack();

      Dimension min = new Dimension(300, 200);
      Dimension max = new Dimension(320, 600);

      Dimension s = getSize();

      s.width = Math.min(s.width, max.width);
      s.width = Math.max(s.width, min.width);

      s.height = Math.min(s.height, max.height);
      s.height = Math.max(s.height, min.height);

      setSize(s);
   }
   
   /**
    * Show the window in the last position that it was shown or center it by default
    */
   public void setPreferredLocationAndSize() {
      
      //Size
      int w = 300;
      int h = 150;
      String wStr = cacheManager.getCacheProperties().getProperty("download.frame.w", ""+w);
      String hStr = cacheManager.getCacheProperties().getProperty("download.frame.h", ""+h);
      
      w = Integer.valueOf(wStr).intValue();
      h = Integer.valueOf(hStr).intValue();
      this.setSize(w, h);
      
      //Location
      String xStr = cacheManager.getCacheProperties().getProperty(DOWNLOAD_FRAME_X, null);
      String yStr = cacheManager.getCacheProperties().getProperty(DOWNLOAD_FRAME_Y, null);
      
      //center window by default
      if(xStr == null || yStr == null) {
         DialogManager.centerWindowOnScreen(this);
      }
      else {
         int x = Integer.valueOf(xStr).intValue();
         int y = Integer.valueOf(yStr).intValue();
         this.setLocation(x,y);
      }
   }
   
   public void completeDownload(final CacheEvent event) {
      logger.info("Recieved completeDownload Event for:"+event.getResourceName());
      
      //Execute in Boot Thread so we get Boots L&F rather then launching app L&F
      Boot.invokeAndWait(new Runnable() {
         public void run() {
            panel.completeDownload(event);
            resizeForItems();

            //Check to see if we should hide the Frame
            if(panel.downloadsInProgress() == false) {
               DownloadProgressFrame.this.setVisible(false);
            }
         }
      });
   }
   
   public void completeGettingInfo(final CacheEvent event) {
      
      logger.info("Recieved completeGettingInfo Event for:"+event.getResourceName());
      
      //Execute in Boot Thread so we get Boots L&F rather then launching app L&F
      Boot.invokeAndWait(new Runnable() {
         public void run() {
                        
            panel.completeGettingInfo(event);
            //resizeForItems();

            //Check to see if we should hide the Frame
            if(panel.downloadsInProgress() == false) {
               DownloadProgressFrame.this.setVisible(false);
            }
         }
      });
      
   }
   
   /**
    * Return true if This Progress Window is still tracking some downloads
    */
   public boolean downloadsInProgress() {
      return panel.downloadsInProgress();
   }
   
   public void downloadException(final String name, final java.net.URL url, final String msg, final Exception exp) {
      logger.info("Recieved downloadException Event for:"+name);
      
      //Execute in Boot Thread so we get Boots L&F rather then launching app L&F
      Boot.invokeAndWait(new Runnable() {
         public void run() {
            panel.downloadException(name, url, msg, exp);
            resizeForItems();
            
            if(panel.downloadsInProgress() == false) {
               DownloadProgressFrame.this.setVisible(false);
            }
         }
      });
   }
   
   /**
    * Get File Info
    */
   public void gettingInfo(final CacheEvent event) {
      logger.info("Recieved gettingInfo Event for:"+event.getResourceName());
            
      //Execute in Boot Thread so we get Boots L&F rather then launching app L&F
      Boot.invokeLater(new Runnable() {
         public void run() {
            panel.gettingInfo(event);
            resizeForItems();
            
            if(DownloadProgressFrame.this.isVisible()) {
              DownloadProgressFrame.this.toFront();
              return;
            }
            
            //Wait for a little bit to see if we are already complete
            //before we show the Frame
            try{Thread.currentThread().sleep(COMPLETE_DELAY);}
            catch(InterruptedException e){e.printStackTrace();}

            if(panel.downloadsInProgress() == true) {
               DownloadProgressFrame.this.setVisible(true);
               DownloadProgressFrame.this.toFront();
            }
         }
      });
   }
   
   /**
    * A Download has started
    * We will display the download frame
    */
   public void startDownload(final CacheEvent event) {
      
      logger.info("Recieved startDownload Event for:"+event.getResourceName());
      
      //Execute in Boot Thread so we get Boots L&F rather then launching app L&F
      Boot.invokeLater(new Runnable() {
         public void run() {
            panel.startDownload(event);
            resizeForItems();
            
            if(DownloadProgressFrame.this.isVisible()) {
              DownloadProgressFrame.this.toFront();
              return;
            }

            //Wait for a little bit to see if we are already complete
            //before we show the Frame
            try{Thread.currentThread().sleep(COMPLETE_DELAY);}
            catch(InterruptedException e){e.printStackTrace();}

            if(panel.downloadsInProgress()) {
               DownloadProgressFrame.this.setVisible(true);
               DownloadProgressFrame.this.toFront();
            }
         }
      });
   }
   
   public void updateDownload(final CacheEvent event) {
      //Execute in Boot Thread so we get Boots L&F rather then launching app L&F
      Boot.invokeAndWait(new Runnable() {
         public void run() {
            panel.updateDownload(event);
         }
      });
   }
   
}
