/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.xito.dazzle;

import javax.swing.*;
import org.xito.dazzle.widget.table.ScrollTable;

/**
 *
 * @author deane
 */
public class TableTest {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Table Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        String[] colnames = new String[]{"Name", "Track #", "Time", "Artist"};
        String[][] data = new String[][]{
            new String[]{"Where The Streets Have No Name", "1", "5:37", "U2"},
            new String[]{"I Still Haven't Found What I'm Looking For", "2", "4:29", "U2"},
            new String[]{"With or Without You", "3", "4:56", "U2"},
            new String[]{"Bullet The Blue Sky", "4", "4:32", "U2"},
            new String[]{"Running To Stand Still", "5", "4:19", "U2"},
            new String[]{"Red Hill Mining Town", "6", "4:54", "U2"}
        };

        ScrollTable table = new ScrollTable(data, colnames);
        //TableScrollPane scrollPane = new TableScrollPane(table);

        //JTable table = new JTable(data, colnames);
        JScrollPane scrollPane = new JScrollPane(table);

        frame.getContentPane().add(scrollPane);

        frame.setSize(600,600);
        frame.setVisible(true);

    }

}
