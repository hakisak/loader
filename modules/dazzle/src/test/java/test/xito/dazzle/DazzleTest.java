package test.xito.dazzle;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.*;

import org.xito.dazzle.widget.panel.GradientPanel;
import org.xito.dazzle.widget.DefaultStyle;
import org.xito.dazzle.widget.border.SingleLineBorder;
import org.xito.dazzle.widget.panel.SplitPane;
import org.xito.dazzle.widget.panel.StackedPanel;
import org.xito.dazzle.widget.panel.StackPanel;
import org.xito.dialog.*;

import org.xito.dialog.CodeViewerDialog;
import org.xito.dialog.DialogTest;

public class DazzleTest {
   
   /** Creates a new instance of TestAlerts */
   public DazzleTest() {
   }
   
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) throws Exception {
  
      //Use native Look and feel
      //UIManager.setLookAndFeel("net.java.plaf.windows.WindowsLookAndFeel");
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      
      Toolkit.getDefaultToolkit().setDynamicLayout(true);
      final JFrame frame = new JFrame("Xito Dazzle Sample");
      
      frame.setSize(700, 500);
      DialogManager.centerWindowOnScreen(frame);
     
      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      frame.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent e) {
            shutdown();
         }
      });
      
      frame.getContentPane().add(new MainPanel());
      frame.setVisible(true);
   }
   
   private static void shutdown() {
      System.out.println("Shutdown Application");
      try {
         Class.forName("org.xito.boot.AppShutdownHelper").newInstance();
      }
      catch(Exception exp) {
        System.exit(0);
      }
   }
   
   /********************************************
    * Main Panel of the Test Application
    *********************************************/   
   public static class MainPanel extends TablePanel implements HyperlinkListener {

      private static final long serialVersionUID = 1L;
      private JSplitPane splitPane;
      private StackPanel stackedPanel;
      private JPanel sampleGroupPanel;
      private JPanel dialogSamplePanel;
      
      public MainPanel() {
         init();
      }
      
      private void init() {
         
         setLayout(new BorderLayout());
         UIDefaults styleMap = DefaultStyle.getDefaults();
         GradientPanel topPanel = new GradientPanel(styleMap.getColor(DefaultStyle.CTRL_TOP_GRADIENT_COLOR_KEY), 
               styleMap.getColor(DefaultStyle.CTRL_BOTTOM_GRADIENT_COLOR_KEY), 0.5f, SwingConstants.SOUTH);
         topPanel.setPreferredSize(new Dimension(100,20));
         topPanel.setBorder(new SingleLineBorder(SingleLineBorder.SOUTH, Color.BLACK, 1));
         add(topPanel, BorderLayout.NORTH);
         
         GradientPanel bottomPanel = new GradientPanel(styleMap.getColor(DefaultStyle.CTRL_TOP_GRADIENT_COLOR_KEY), 
               styleMap.getColor(DefaultStyle.CTRL_BOTTOM_GRADIENT_COLOR_KEY), 0.5f, SwingConstants.SOUTH);
         bottomPanel.setPreferredSize(new Dimension(100,20));
         bottomPanel.setBorder(new SingleLineBorder(SingleLineBorder.NORTH, Color.BLACK, 1));
         add(bottomPanel, BorderLayout.SOUTH);
         
         splitPane = new SplitPane(SplitPane.HORIZONTAL_SPLIT);
         stackedPanel = new StackPanel();
         splitPane.setLeftComponent(stackedPanel);
         
         sampleGroupPanel = new JPanel();
         sampleGroupPanel.setBackground(new Color(209,215,226));
         stackedPanel.addItem("Samples", sampleGroupPanel);
         stackedPanel.addItem("Samples2", new JPanel());
         
         dialogSamplePanel = new JPanel(new TableLayout());
         dialogSamplePanel.setBackground(Color.WHITE);
         JScrollPane sp = new JScrollPane(dialogSamplePanel);
         sp.setBorder(BorderFactory.createEmptyBorder());
         splitPane.setRightComponent(sp);
         
         add(splitPane);
         splitPane.setDividerLocation(200);
         
         initDialogSamples();
      }
      
      /**
       * Add Dialog Samples
       *
       */
      private void initDialogSamples() {
         
         int sampleNum = 0;
         
         for(int i=0;i<10;i++) {
         sampleNum++;
         String sampleID = ""+sampleNum;
         SamplePanel samp = new SamplePanel();
         TableLayout layout = (TableLayout)dialogSamplePanel.getLayout();
         TableLayout.Row row = new TableLayout.Row();
         TableLayout.Column col = new TableLayout.Column(sampleID, 0.999f);
         col.hAlign = TableLayout.FULL;
         col.vAlign = TableLayout.FULL;
         row.addCol(col);
         layout.addRow(row);
         dialogSamplePanel.add(sampleID, samp);
         samp.setTitle("Sample -- " + sampleID);
         }
         
         
      }
      
      /**
       * Listen for HyperLink Clicks
       */
      public void hyperlinkUpdate(HyperlinkEvent e) {
         if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            String desc = e.getDescription();
            if(desc.startsWith("mainclass:")) {
               executeTest(desc.substring(desc.indexOf(':')+1));
            }
            else if(desc.startsWith("source:")) {
               viewSource(desc.substring(desc.indexOf(':')+1));
            }
         }
      }
      
      public void viewSource(String source) {
      
         CodeViewerDialog d = new CodeViewerDialog(null, source);
         d.setVisible(true);
         d.getResult();
      }
      
      public void executeTest(String mainClass) {
         
        if(mainClass == null || "".equals(mainClass)) {
           
           DialogManager.showMessage((Frame)SwingUtilities.getWindowAncestor(this), "Centered Dialog", 
                 "This org.xito should be centered on the parent");
           
           return;
        }
         
         
         try {
            Class mc = DazzleTest.class.getClassLoader().loadClass(mainClass);
            Method mainM = mc.getMethod("main", new Class[]{String[].class});
            mainM.invoke(null, new Object[]{new String[0]});
         }
         catch(Exception e) {
            e.printStackTrace();
            DialogManager.showError(null, "Error Running Test", e.getMessage(), e); 
         }
      }
   }
}
