package test.xito.dazzle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.xito.dazzle.widget.progress.ProgressSpinner;
import org.xito.dazzle.widget.progress.ProgressSpinnerPanel;
import org.xito.dazzle.widget.progress.ProgressSpinner.SpinnerTheme;

public class BusySpinnerCustomTest {

   public static void main(String args[]) {

      final JFrame f = new JFrame();
      f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      final JPanel contentPanel = new JPanel(null);
      f.getContentPane().add(contentPanel);
      
      JButton startBtn = new JButton("Start");
      startBtn.addActionListener(new ActionListener(){

         public void actionPerformed(ActionEvent e) {
            SpinnerTheme theme = ProgressSpinner.whiteTheme.copy().setSize(36);
            ProgressSpinner spinnerPanel = new ProgressSpinner(theme);

            //spinnerPanel.setText("test");
            //spinnerPanel.setSize(spinnerPanel.getPreferredSize());
            contentPanel.add(spinnerPanel);
            spinnerPanel.start();
         }
         
      });
      
      f.getContentPane().add(startBtn, BorderLayout.NORTH);
      f.setSize(400,400);
      f.setVisible(true);
   }
}
