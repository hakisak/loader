package test.xito.jdic;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;

import javax.swing.*;

import org.jdesktop.jdic.browser.BrowserEngineManager;
import org.jdesktop.jdic.browser.IWebBrowser;
import org.jdesktop.jdic.init.JdicManager;
import org.xito.boot.Boot;
import org.xito.dialog.DialogManager;

/**
 * 
 * @author DRICHAN
 */
public class MainTest {

   public static void main(String[] args) {

      System.out.println("Starting JDIC Main Test");

      try {

         System.out.println("*** browserPath: "
               + org.jdesktop.jdic.browser.internal.WebBrowserUtil.getDefaultBrowserPath());
         BrowserEngineManager bem = BrowserEngineManager.instance();

         final IWebBrowser webBrowser = bem.getActiveEngine().getWebBrowser();
         webBrowser.setURL(new URL("http://google.com"));
         final JFrame f = new JFrame("Test");
         f.setSize(400, 400);

         final Component webComp = webBrowser.asComponent();

         f.add(webComp);
         f.setVisible(true);
         f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

         f.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent arg0) {
               webBrowser.stop();
               f.getContentPane().remove(webComp);
               f.dispose();
               Boot.endSession(true);
            }
         });
      } catch (Exception e) {
         DialogManager.showError(null, "Error", e.getMessage(), e);
      }
      
      youTube();
   }

   public static void youTube() {

      String VID_ID = "@@vid_id@@";
      String WWORLD = "fBQnqf4ejQM";

      try {
         // Generate HTML
         //StringBuffer htmlTemplate = getTemplate();
         
         //int s = htmlTemplate.indexOf(VID_ID);
         //htmlTemplate.replace(s, s+VID_ID.length(), WWORLD);
         
         //File tempFile = writeTempFile(htmlTemplate.toString());
         
         System.out.println("*** browserPath: "
               + org.jdesktop.jdic.browser.internal.WebBrowserUtil.getDefaultBrowserPath());
         BrowserEngineManager bem = BrowserEngineManager.instance();

         final IWebBrowser webBrowser = bem.getActiveEngine().getWebBrowser();
         URL u = new URL("http://xito.sourceforge.net/youtube_template.php?vid_id="+WWORLD);
         webBrowser.setURL(u);
         final JFrame f = new JFrame("YouTube");
         f.setSize(450, 400);
         f.setResizable(false);

         final Component webComp = webBrowser.asComponent();

         f.add(webComp);
         f.setVisible(true);
         f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

         f.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent arg0) {
               webBrowser.stop();
               f.getContentPane().remove(webComp);
               f.dispose();
               Boot.endSession(true);
            }
         });
      } catch (Exception e) {
         DialogManager.showError(null, "Error", e.getMessage(), e);
      }
   }
   
   private static StringBuffer getTemplate() throws Exception {
      
      InputStream in = MainTest.class.getResourceAsStream("youtube_template.html");
            
      StringBuffer fileData = new StringBuffer(1000);
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      char[] buf = new char[1024];
      int numRead=0;
      while((numRead=reader.read(buf)) != -1){
          String readData = String.valueOf(buf, 0, numRead);
          fileData.append(readData);
          buf = new char[1024];
      }
      reader.close();
      return fileData;
   }
   
   private static File writeTempFile(String contents) throws Exception {
      
      System.out.println("OUTPUT =======================");
      System.out.println(contents);
      
      File f = File.createTempFile("ytv", "vid");
      FileWriter fw = new FileWriter(f);
      fw.write(contents);
      fw.close();
            
      System.out.println("File:" + f);
      
      return f;
   }
}
