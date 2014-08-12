/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xito.dialog.layout;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.xito.dialog.TableLayout;

/**
 *
 * @author deane
 */
public class SampleBorderLayout {

    public static void main(String[] args) {

        JFrame f = new JFrame("Sample Border Layout");
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //Create the Panel using a TableLayout
        JPanel panel = new JPanel(TableLayout.createLayout(TableLayout.BORDER_LAYOUT));

        JLabel northLbl = new JLabel("NORTH", JLabel.CENTER);
        northLbl.setOpaque(true);
        northLbl.setBackground(Color.BLUE);
        panel.add("north", northLbl);
        
        JLabel southLbl = new JLabel("SOUTH", JLabel.CENTER);
        southLbl.setOpaque(true);
        southLbl.setBackground(Color.RED);
        panel.add("south", southLbl);
        
        JLabel westLbl = new JLabel("WEST", JLabel.CENTER);
        westLbl.setOpaque(true);
        westLbl.setBackground(Color.YELLOW);
        panel.add("west", westLbl);
        
        JLabel eastLbl = new JLabel("EAST", JLabel.CENTER);
        eastLbl.setOpaque(true);
        eastLbl.setBackground(Color.GREEN);
        panel.add("east", eastLbl);
        
        JLabel centerLbl = new JLabel("CENTER", JLabel.CENTER);
        centerLbl.setOpaque(true);
        centerLbl.setBackground(Color.BLACK);
        centerLbl.setForeground(Color.WHITE);
        panel.add("center", centerLbl);
        
        f.getContentPane().add(panel);
        f.pack();

        f.setVisible(true);


    }
}
