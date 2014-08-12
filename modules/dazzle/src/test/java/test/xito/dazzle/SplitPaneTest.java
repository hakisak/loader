/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.xito.dazzle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.xito.dazzle.widget.panel.SplitPane;

/**
 *
 * @author deane
 */
public class SplitPaneTest {

    public static void main(String[] args) {

        JFrame frame = new JFrame("SplitPane Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        SplitPane splitPane = new SplitPane(SplitPane.HORIZONTAL_SPLIT);

        splitPane.setLeftComponent(new JPanel());
        splitPane.setRightComponent(new JPanel());

        frame.setContentPane(splitPane);
        frame.setSize(600,600);
        frame.setVisible(true);

    }

}
