/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.xito.dazzle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.xito.dazzle.ImageManager;
import org.xito.dazzle.dialog.SheetDialog;
import org.xito.dazzle.widget.toolbar.Toolbar;
import org.xito.dazzle.widget.toolbar.ToolbarFrame;
import org.xito.dazzle.widget.toolbar.ToolbarItem;
import org.xito.dialog.DialogDescriptor;
import org.xito.dialog.DialogManager;

/**
 *
 * @author deane
 */
public class ToolBarTest {

    private static ToolbarFrame frame;

    public static void main(String[] args) {

        //final JFrame frame = new JFrame("Test ToolBar");
        frame = Toolbar.createToolbarFrame();
        frame.setTitle("Test Toolbar");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        
        Toolbar toolbar = frame.getToolbar();
        ToolbarItem item = null;

        item = ToolbarItem.createItem("colors", "Colors", new ColorActionListener(), new ImageIcon(ImageManager.getImageByName("colors.png")), null);
        toolbar.addItem(item);
        
        item = ToolbarItem.createItem("fonts", "Fonts", null, null, null );//new ImageIcon(ImageManager.getImageByName("colors.png")));
        toolbar.addItem(item);
        
        toolbar.addItemSeparator();
        
        item = ToolbarItem.createItem("print", "Print", null, new ImageIcon(ImageManager.getImageByName("print.png")), null);
        toolbar.addItem(item);
        
        toolbar.addItemFlexibleSpacer();
        
        item = ToolbarItem.createItem("customize", "Customize", null, new ImageIcon(ImageManager.getImageByName("customize.png")), null);
        toolbar.addItem(item);

        item = ToolbarItem.createItem(new JButton("Send Message"));
        toolbar.addItem(item);
        
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        frame.getContentPane().add(p, BorderLayout.CENTER);
        frame.setSize(600,600);

        frame.setVisible(true);
    }

    public static class ColorActionListener implements ActionListener {

       public void actionPerformed(ActionEvent e) {
          DialogDescriptor desc = new DialogDescriptor();
          desc.setTitle("Sheet Title");
          desc.setSubtitle("This is an example Sheet");
          desc.setMessageType(DialogManager.INFO_MSG);
          desc.setType(DialogManager.OK_CANCEL);
          desc.setMessage("This is an example Sheet");
          desc.setWidth(300);
          desc.setHeight(200);
          SheetDialog sheetPanel = new SheetDialog(desc);
          sheetPanel.getDialogPanel().setBackground(new Color(240,240,240));
          sheetPanel.revealSheet(frame, null);
       }
    }

}
