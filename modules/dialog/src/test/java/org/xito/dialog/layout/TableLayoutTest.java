/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dialog.layout;

import java.awt.Dimension;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.swing.*;
import org.xito.dialog.TableLayout;
import org.xito.dialog.TableLayout.Row;
import org.xito.dialog.TablePanel;

/**
 *
 * @author deane
 */
public class TableLayoutTest {
    
    ArrayList<LayoutTest> tests = new ArrayList();
    
    
    public static void main(String args[]) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch(Exception exp) {
            exp.printStackTrace();
        }
        int lblHeight = new JLabel("test").getPreferredSize().height;
        
        TableLayoutTest test = new TableLayoutTest();
                
        test.addTest("layout/fixed_layout1.html", 60, 60);
        test.addTest("layout/fixed_layout2.html", 180, (lblHeight * 3) + 100);
        //test.addTest("layout/fixed_layout3.html", 60, 60);
        
        //test.testAll();
        
        PropsLayout.main(args);
        
    }
    
    public void addTest(String resourceName, int preferredWidth, int preferredHeight) {
       
        LayoutTest layoutTest = new LayoutTest();
        layoutTest.resourceName = resourceName;
        layoutTest.preferredHeight = preferredHeight;
        layoutTest.preferredWidth = preferredWidth;
        tests.add(layoutTest);
    }
    
    public void testAll() {
        
        int passTotal = 0;
        int failTotal = 0;
        for(LayoutTest layoutTest : tests) {
            try {
                test(layoutTest);
                passTotal++;
            }
            catch(AssertionError error) {
                error.printStackTrace();
                failTotal++;
            }
        }
        
        System.out.println("==========================================");
        System.out.println("Tests Complete Pass:" + passTotal + "  Fail:" + failTotal);
        System.out.println("==========================================");
        
    }
    
    public void test(LayoutTest layoutTest) {
        
        TablePanel p = new TablePanel();
        p.setPaintBorderLines(true);
        URL url = TableLayoutTest.class.getResource(layoutTest.resourceName);
        System.out.println("Testing Layout:"+url);
        
        TableLayout layout = new TableLayout(url);
        
        p.setLayout(layout);
    
        for(int r = 0; r < layout.getRowCount();r++) {
            Row row = layout.getRow(r);
            for(int c = 0; c < row.getColumnCount(); c++) {
                TableLayout.Column col = row.getColumn(c);
                JLabel comp = new JLabel(col.name);
                p.add(col.name, comp);
            }
        }
        
        Dimension ps = p.getPreferredSize();
        
        assert ps.width == layoutTest.preferredWidth : 
            MessageFormat.format("Expected Width {0} got {1}", layoutTest.preferredWidth, ps.width);
        
        assert ps.height == layoutTest.preferredHeight :
            MessageFormat.format("Expected Height {0} got {1}", layoutTest.preferredHeight, ps.height);
    }
    
    public static class LayoutTest {
        String resourceName;
        int preferredWidth;
        int preferredHeight;
    }

}
