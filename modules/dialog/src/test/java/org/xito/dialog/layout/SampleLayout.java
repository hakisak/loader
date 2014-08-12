/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xito.dialog.layout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import org.xito.dialog.TableLayout;

/**
 *
 * @author deane
 */
public class SampleLayout {

    public static void main(String[] args) {

        JFrame f = new JFrame("Sample");
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //Create the Panel using a TableLayout
        //Note: The URL can be a resource in your application such as
        //MyClass.getResource("layout.html")
        JPanel panel = new JPanel(new TableLayout(SampleLayout.class.getResource("sample.html")));

        panel.add("firstname_lbl", new JLabel("First Name:"));

        JTextField firstNameTF = new JTextField(15);
        panel.add("firstname_fld", firstNameTF);

        panel.add("lastname_lbl", new JLabel("Last Name:"));

        JTextField lastNameTF = new JTextField(15);
        panel.add("lastname_fld", lastNameTF);
        
        f.getContentPane().add(panel);
        f.pack();

        f.setVisible(true);


    }
}
