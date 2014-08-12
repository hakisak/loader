package org.xito.dialog.layout;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.xito.dialog.TableLayout;

import org.xito.dialog.DialogTest;

public class PreferredSizeLayout {

    static int currentColorNum = 50;
    static int colorStep = 10;

    public static void main(String args[]) {

        JFrame f = new JFrame("Preferred Size Layout");
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        f.setSize(400, 400);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        JPanel fieldPanel = new JPanel(new TableLayout(PreferredSizeLayout.class.getResource("preferred_size_test_layout.html")));
        //fieldPanel.setPaintBorderLines(true);

        f.setContentPane(contentPane);

        fieldPanel.setOpaque(true);
        fieldPanel.setBackground(Color.WHITE);
        JScrollPane fieldScroll = new JScrollPane(fieldPanel);
        fieldScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPane.add(fieldScroll, BorderLayout.CENTER);

        Font txt_font = new JLabel().getFont();
        Font lbl_font = new JLabel().getFont().deriveFont(Font.BOLD);

        //large icon
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        iconPanel.setOpaque(false);
        ImageIcon globeIcon = new ImageIcon(DialogTest.class.getResource("org.xito.launcher.images/Glow|be_128x128.png"));
        iconPanel.add(new JLabel(globeIcon), BorderLayout.NORTH);
        contentPane.add(iconPanel, BorderLayout.WEST);

        //1
        JTextField comp1TF = new JTextField();
        comp1TF.setFont(txt_font);
        JLabel comp1Lbl = new JLabel("test1:");
        comp1Lbl.setFont(lbl_font);
        comp1Lbl.setLabelFor(comp1TF);
        fieldPanel.add("1_lbl", comp1Lbl);
        fieldPanel.add("1_field", comp1TF);

        //2
        JTextField comp2TF = new JTextField();
        comp2TF.setFont(txt_font);
        JLabel comp2Lbl = new JLabel("test2:");
        comp2Lbl.setFont(lbl_font);
        fieldPanel.add("2_lbl", comp2Lbl);
        fieldPanel.add("2_field", comp2TF);

        //3
        JComboBox comp3CB = new JComboBox(
                new String[]{"test1", "test2", "test3"});

        comp3CB.setFont(txt_font);
        comp3CB.setOpaque(false);
        JLabel comp3Lbl = new JLabel("test3:");
        comp3Lbl.setFont(lbl_font);
        fieldPanel.add("3_lbl", comp3Lbl);
        fieldPanel.add("3_field", comp3CB);

        //4
        JComboBox comp4CB = new JComboBox(
                new String[]{"test1", "test2", "test3"});

        comp4CB.setFont(txt_font);
        comp4CB.setOpaque(false);
        JLabel comp4Lbl = new JLabel("test4:");
        comp4Lbl.setFont(lbl_font);
        fieldPanel.add("4_lbl", comp4Lbl);
        fieldPanel.add("4_field", comp4CB);

        /*
        //Points Possible
        //Create a layout for points possible and extra credit
        TableLayout ppLayout = new TableLayout();
        ppLayout.setWidth(0.99f);
        {
            TableLayout.Row row = new TableLayout.Row();
            ppLayout.addRow(row);
            TableLayout.Column col1 = new TableLayout.Column("pp_field");
            row.addCol(col1);
            row.addCol(new TableLayout.Column(8));
            TableLayout.Column col2 = new TableLayout.Column("extra_lbl");
            row.addCol(col2);
            row.addCol(new TableLayout.Column(8));
            TableLayout.Column col3 = new TableLayout.Column("extra_field");
            row.addCol(col3);
            row.addCol(new TableLayout.Column(8));
            TableLayout.Column col4 = new TableLayout.Column("max_field");
            col4.width = 0.99f;
            col4.hAlign = TableLayout.RIGHT;
            row.addCol(col4);
        }

        ppPanel = new JPanel(ppLayout);
        ppPanel.setOpaque(false);
        pointsPossibleTF = new BeanNumField(asmtBH, Assignment.POINTS_POSSIBLE);
        pointsPossibleTF.addDirtyComponentListener(dirtyCompListener);
        pointsPossibleTF.setFont(txt_font);
        pointsPossibleTF.setColumns(4);
        ppPanel.add("pp_field", pointsPossibleTF);
        BeanPropLabel ppLbl = new BeanPropLabel(AssignmentMsg.getAsmtDetailPointsPossibleField(), asmtBH, Assignment.POINTS_POSSIBLE);
        ppLbl.setFont(lbl_font);
        fieldPanel.add("points_possible_lbl", ppLbl);
        fieldPanel.add("points_possible_field", ppPanel);
        */

        //5
        JTextField comp5TF = new JTextField();
        comp5TF.setFont(txt_font);
        comp5TF.setColumns(4);
        JLabel comp5Lbl = new JLabel("test5:");
        comp5Lbl.setFont(lbl_font);
        fieldPanel.add("5_lbl", comp5Lbl);
        fieldPanel.add("5_field", comp5TF);

        //6
        JTextField comp6TF = new JTextField();
        comp6TF.setFont(txt_font);
        comp6TF.setColumns(4);
        JLabel comp6Lbl = new JLabel("test6:");
        comp6Lbl.setFont(lbl_font);
        fieldPanel.add("6_lbl", comp6Lbl);
        fieldPanel.add("6_field", comp6TF);

        //7
        JTextField comp7TF = new JTextField();
        comp7TF.setFont(txt_font);
        comp7TF.setColumns(4);
        JLabel comp7Lbl = new JLabel("test7:");
        comp7Lbl.setFont(lbl_font);
        fieldPanel.add("7_lbl", comp7Lbl);
        fieldPanel.add("7_field", comp7TF);

        //8
        JCheckBox comp8CB = new JCheckBox();
        comp8CB.setSelected(true);
        comp8CB.setOpaque(false);
        JLabel comp8Lbl = new JLabel("testing for 8:");
        comp8Lbl.setFont(lbl_font);
        fieldPanel.add("8_lbl", comp8Lbl);
        fieldPanel.add("8_field", comp8CB);

        //9
        JTextArea comp9TA = new JTextArea();
        comp9TA.setFont(txt_font);
        JLabel comp9Lbl = new JLabel("test9:");
        comp9Lbl.setFont(lbl_font);
        fieldPanel.add("9_lbl", comp9Lbl);
        fieldPanel.add("9_field", new JScrollPane(comp9TA));


        f.pack();
        f.setVisible(true);
    }

    public static Color nextColor() {
        currentColorNum += colorStep;
        return new Color(currentColorNum, currentColorNum, currentColorNum);
    }

    public static class CellPanel extends JPanel {

        public CellPanel(String name, Color bgColor) {
            setLayout(new BorderLayout());
            setName(name);
            add(new JLabel(name, JLabel.CENTER), BorderLayout.CENTER);
            setOpaque(true);
            setBackground(bgColor);
        }
    }
}
