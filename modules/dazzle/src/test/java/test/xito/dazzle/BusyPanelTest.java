package test.xito.dazzle;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.xito.dazzle.widget.progress.ProgressSpinnerPanel;
import org.xito.dazzle.widget.progress.ProgressSpinner;
import org.xito.dialog.TableLayout;

public class BusyPanelTest {
   
   public static void main(String args[]) {

      JFrame f = new JFrame("Test");
      f.getContentPane().setLayout(new BorderLayout());

      JPanel glassPane = (JPanel) f.getGlassPane();
      glassPane.setVisible(true);

      TableLayout layout = new TableLayout("<html><table>"
            + "<tr height=\"50%\"><td width=\"50%\"></td><td></td><td width=\"50%\"></td></tr>"
            + "<tr><td></td><td align=\"center\">panel</td><td></td></tr>" + "<tr height=\"50%\"></tr>"
            + "</table></html>");

      glassPane.setLayout(layout);
      final ProgressSpinnerPanel bp = new ProgressSpinnerPanel(ProgressSpinner.blackTheme);
      bp.setText("Please Wait for Test...");
      bp.setCancelEnabled(true);
            
      glassPane.add("panel", bp);

      f.setSize(500, 300);
      f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      JPanel top = new JPanel(new FlowLayout());
      JButton startBtn = new JButton("Start");
      startBtn.addActionListener(new ActionListener() {

         public void actionPerformed(ActionEvent e) {
            bp.start();
         }
      });

      JButton stopBtn = new JButton("Stop");
      stopBtn.addActionListener(new ActionListener() {

         public void actionPerformed(ActionEvent e) {
            bp.stop();
         }
      });

      top.add(startBtn);
      top.add(stopBtn);
      f.getContentPane().add(top, BorderLayout.NORTH);

      JTextArea ta = new JTextArea();
      f.getContentPane().add(new JScrollPane(ta));

      f.setVisible(true);

   }

}
