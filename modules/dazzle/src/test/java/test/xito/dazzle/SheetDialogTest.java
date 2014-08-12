/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.xito.dazzle;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.xito.dazzle.dialog.SheetDialog;
import org.xito.dialog.DialogDescriptor;
import org.xito.dialog.DialogManager;

/**
 *
 * @author deane
 */
public class SheetDialogTest {

    public static void main(String args[]) {

        JFrame frame = new JFrame("Test Sheet");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(createButtonPanel(frame, false), BorderLayout.NORTH);

        frame.setSize(600,600);
        DialogManager.centerWindowOnScreen(frame);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setVisible(true);
        
        JFrame heavyFrame = new JFrame("Test Heavy Sheet");
        heavyFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        heavyFrame.getContentPane().add(createButtonPanel(heavyFrame, true), BorderLayout.NORTH);

        heavyFrame.setSize(600,600);
        DialogManager.centerWindowOnScreen(heavyFrame);
        heavyFrame.getContentPane().setBackground(Color.WHITE);
        heavyFrame.getContentPane().add(new Canvas());
        heavyFrame.setVisible(true);

    }

    public static JPanel createButtonPanel(final JFrame frame, final boolean useHeavy) {
       JButton openBtn = new JButton("Open");
       openBtn.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
             basicTest(frame, useHeavy);
          }
       });


       JButton descBtn = new JButton("Desc");
       descBtn.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
             descTest(frame, useHeavy);
          }
       });
       
       JButton desc2Btn = new JButton("Desc with Overlay");
       desc2Btn.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
             descTestWithOverlay(frame, useHeavy);
          }
       });

       JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
       buttonPanel.add(openBtn);
       buttonPanel.add(descBtn);
       buttonPanel.add(desc2Btn);
       
       return buttonPanel;
    }
    
    public static void descTest(JFrame frame, boolean useHeavy) {
       
       DialogDescriptor desc = new DialogDescriptor();
       desc.setTitle("Sheet Title");
       desc.setSubtitle("This is an example Sheet");
       desc.setMessageType(DialogManager.INFO_MSG);
       desc.setType(DialogManager.OK_CANCEL);
       desc.setMessage("This is an example Sheet");
       desc.setWidth(300);
       desc.setHeight(200);
       SheetDialog sheetPanel = new SheetDialog(desc);
       sheetPanel.setUseHeavyWeightSupport(useHeavy);
       sheetPanel.revealSheet(frame, null);
    }
    
    public static void descTestWithOverlay(JFrame frame, boolean useHeavy) {
       
       DialogDescriptor desc = new DialogDescriptor();
       desc.setTitle("Sheet Title");
       desc.setSubtitle("This is an example Sheet");
       desc.setMessageType(DialogManager.INFO_MSG);
       desc.setType(DialogManager.OK_CANCEL);
       desc.setMessage("This is an example Sheet");
       desc.setWidth(300);
       desc.setHeight(200);
       SheetDialog sheetPanel = new SheetDialog(desc);
       sheetPanel.setUseHeavyWeightSupport(useHeavy);
       sheetPanel.setFadeGlassPane(true);
       //sheetPanel.setOverlayPaint(Color.BLACK);
       sheetPanel.setOverlayPaint(new GradientPaint(0,0,Color.BLACK,0,frame.getHeight(),Color.WHITE));
       sheetPanel.setFadeGlassPanePercent(0.3f);
       
       sheetPanel.revealSheet(frame, null);
    }
    
    public static void basicTest(JFrame frame, boolean useHeavy) {
       
       JPanel customPanel = new JPanel(new BorderLayout());
       customPanel.setOpaque(true);
       customPanel.setBackground(Color.WHITE);
       customPanel.setPreferredSize(new Dimension(300,300));

       final SheetDialog sheetPanel = new SheetDialog(customPanel);
       sheetPanel.setUseHeavyWeightSupport(useHeavy);
       JButton okBtn = new JButton("OK");
       okBtn.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent evt) {
               sheetPanel.hideSheet();
            }
       });
       
       JPanel compPanel = new JPanel();
       compPanel.setOpaque(false);
       JComboBox combo = new JComboBox(new String[]{"aaa", "bbb", "ccc"});
       compPanel.add(combo);
       customPanel.add(compPanel);
       
       JPanel panel = new JPanel();
       panel.setOpaque(false);
       panel.add(okBtn);
       customPanel.add(panel, BorderLayout.SOUTH);

       sheetPanel.revealSheet(frame, new SheetDialog.SheetPaneListener() {

             public void sheetPaneDisposed(SheetDialog pane) {
                 System.out.println("Made it Here");
             }
         });
    }

}
