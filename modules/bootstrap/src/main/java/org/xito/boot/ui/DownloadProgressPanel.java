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

import java.util.*;
import java.text.*;
import java.awt.*;
import java.net.*;
import javax.swing.*;

import org.xito.boot.*;
import org.xito.miglayout.swing.*;

/**
 *
 * @author  Deane
 */
public class DownloadProgressPanel extends JPanel implements CacheListener {
   
   private static final int COMPLETE_DELAY = 10000;
   
   HashMap itemMap = new HashMap();
   
   /** Creates a new instance of DownloadProgressPanel */
   public DownloadProgressPanel() {
      setLayout(new MigLayout("insets 4", "[grow, fill]", ""));
      setBackground(Color.WHITE);
   }
   
   public void completeGettingInfo(final CacheEvent event) {

      final ItemPanel panel = (ItemPanel)itemMap.get(event.getURL());
      if(panel == null) {
        return;
      }

      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            panel.setComplete();
            itemMap.remove(event.getURL());

            DownloadProgressPanel.this.remove(panel);
            DownloadProgressPanel.this.setMinimumSize(getPreferredSize());
            DownloadProgressPanel.this.revalidate();
         }
      });

   }
   
   /**
    * Update the Panel for Download Completion
    */
   public void completeDownload(final CacheEvent event) {
      
      final ItemPanel panel = (ItemPanel)itemMap.get(event.getURL());
      if(panel == null) return;

      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            panel.setComplete();
            itemMap.remove(event.getURL());

            remove(panel);
            setMinimumSize(getPreferredSize());
            revalidate();
            repaint();
         }
      });
   }
   
   /**
    * Update the Panel for a Download Exception
    */
   public void downloadException(final String name, final URL url, final String msg, final Exception exp) {
      final ItemPanel panel = (ItemPanel)itemMap.get(url);
      if(panel == null) return;

      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            panel.setError(msg, exp);

            //Show Error
            String msgText  = MessageFormat.format(Resources.bundle.getString("download.ui.error.msg"), url.toString(), msg);
            Boot.showError(Resources.bundle.getString("download.ui.error.title"), msgText, exp);
            itemMap.remove(url);

            remove(panel);
            setMinimumSize(getPreferredSize());
            revalidate();
            repaint();
         }

      });
   }
   
   /**
    * Getting File Info
    */
   public void gettingInfo(final CacheEvent event) {

      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            ItemPanel p = (ItemPanel)itemMap.get(event.getURL());
            if(p == null) {
               p = new ItemPanel(event);
               itemMap.put(event.getURL(), p);
               add(p, "wrap");
            }

            p.gettingInfo(event);
            setMinimumSize(getPreferredSize());
            revalidate();
         }
      });
   }
   
   public void startDownload(final CacheEvent event) {
   
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            ItemPanel p = (ItemPanel)itemMap.get(event.getURL());
            if(p == null) {
               p = new ItemPanel(event);
               itemMap.put(event.getURL(), p);
               add(p, "wrap");
            }

            p.startDownload(event);
            setMinimumSize(getPreferredSize());
            revalidate();
         }
      });
   }
   
   public synchronized boolean downloadsInProgress() {
      return (itemMap.size()>0);
   }
   
   public void updateDownload(final CacheEvent event) {

      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            ItemPanel p = (ItemPanel)itemMap.get(event.getURL());
            if(p != null) {
               p.updateDownload(event);
            }
         }
      });
   }
   
   /**
    * Get text for Download item Label
    */
   public String getDownloadItemText(String name) {
      String msg = Resources.bundle.getString("download.ui.item.name");      
      return MessageFormat.format(msg, name);
   }
   
   /**
    * Get text for Time Remaining Label
    */
   public String getTimeRemainingText(String time) {
      String msg = Resources.bundle.getString("download.ui.time.remaining");      
      return MessageFormat.format(msg, time);
   }
   
   /**************************************************************
    * Innert Class for each progress Bar
    **************************************************************/
   private class ItemPanel extends JPanel {

      private String name;
      private URL url;
      
      JLabel nameLbl;
      JLabel estimatedTimeLbl;
      JProgressBar progressBar;
      
      /**
       * Create the Iteam Panel
       */
      ItemPanel(CacheEvent event) {
         
         name = event.getResourceName();
         url = event.getURL();

         MigLayout layout = new MigLayout(
            "insets 4",
            "[grow] [grow, align right]",
            "[] [grow, fill]"
         );

         setLayout(layout);
         setOpaque(false);
         
         //Name
         String html = Resources.bundle.getString("download.ui.starting");
         nameLbl = new JLabel(html);
         nameLbl.setFont(nameLbl.getFont().deriveFont(11f));
         add(nameLbl, "wmax 150");
         
         //Time Remaining
         html = "";
         estimatedTimeLbl = new JLabel(html, JLabel.RIGHT);
         estimatedTimeLbl.setFont(nameLbl.getFont().deriveFont(11f));
         add(estimatedTimeLbl, "wrap");
         
         progressBar = new JProgressBar();

         if(event.getTotalSize()>0) {
            progressBar.setMaximum(event.getTotalSize());
         }
         else {
            progressBar.setIndeterminate(true);
         }

         add(progressBar, "span, grow");
      }
      
      public void setComplete() {
         progressBar.setIndeterminate(false);
         progressBar.setValue(progressBar.getMaximum());
      }
      
      public void setError(String msg, Exception exp) {
         
         estimatedTimeLbl.setText(Resources.bundle.getString("download.ui.error"));
      }
      
      /**
       * Start the Download Progress
       */
      public void startDownload(CacheEvent event) {
         nameLbl.setText(getDownloadItemText(event.getResourceGroupName() + " - " + event.getResourceName()));
         estimatedTimeLbl.setText(getTimeRemainingText(getTimeRemaining(event.getEstimateTime())));
      }
      
      /**
       * Getting File Info Status
       */
      public void gettingInfo(CacheEvent event) {
         nameLbl.setText(getDownloadItemText(event.getResourceName()));
         estimatedTimeLbl.setText(getTimeRemainingText(Resources.bundle.getString("download.ui.unknown")));
      }
      
      /**
       * Update the Download Progress
       */
      public void updateDownload(CacheEvent event) {
         
         //Set the Maximum if we know it
         if(event.getTotalSize()>0) {
            progressBar.setIndeterminate(false);
            progressBar.setMaximum(event.getTotalSize());
         }
         else {
            progressBar.setIndeterminate(true);
         }
         
         //Set progress status
         progressBar.setValue(event.getProgressSize());
         
         //Set the Estimated Download time
         if(event.getTotalSize()!= -1 && (event.getTotalSize() <= event.getProgressSize())) {
            estimatedTimeLbl.setText(Resources.bundle.getString("download.ui.complete"));
         }
         else {
            estimatedTimeLbl.setText(getTimeRemainingText(getTimeRemaining(event.getEstimateTime())));
         }
      }
      
      /**
       * Compute the Time Remaining String to be displayed
       */
      private String getTimeRemaining(long time) {
         if(time == -1) return Resources.bundle.getString("download.ui.unknown");
         
         int SEC = 1000;
         int MIN = SEC * 60;
         int HOUR = MIN * 60;
         
         int h=0;
         int m=0;
         int s=0;
         
         h = (int)Math.floor(time/HOUR);
         if(h>0) time=time-(h*HOUR);
         m = (int)Math.floor(time/MIN);
         if(m>0) time=time-(m*MIN);
         s = (int)Math.floor(time/SEC);
         
         return h+":"+ ((m<10)?"0"+m:m+"") + ":" +((s<10)?"0"+s:s +"");
      }
   }
   
  
   
}
