package org.xito.boot.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

/**
 * DialogTitlePanel
 *
 * @author: drichan
 */
public class DialogTitlePanel extends JComponent {

    protected JLabel titleLbl;
    protected JLabel iconLbl;

    public DialogTitlePanel(String title, URL iconURL) {

        setLayout(new BorderLayout());
        titleLbl = new JLabel(title);
        Font f = titleLbl.getFont().deriveFont(24f);
        titleLbl.setFont(f);
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLbl);

        ImageIcon icon = new ImageIcon(iconURL);
        iconLbl = new JLabel(icon);
        iconLbl.setBorder(new EmptyBorder(8,8,8,8));
        add(iconLbl, BorderLayout.EAST);

        add(Box.createRigidArea(iconLbl.getPreferredSize()), BorderLayout.WEST);
    }

    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D)g;

        Color top = new Color(240,240,240);
        Color bottom = new Color(230,230,230);
        
        GradientPaint gp = new GradientPaint(0, 0, bottom, 0, getHeight()-1, top);

        g2.setColor(top);
        g2.fillRect(0, 0, getWidth()-1, getHeight()-1);

        g2.setPaint(gp);
        g2.fillRect(0,getHeight()/2,getWidth()-1, getHeight()-1);

        g2.setPaint(new Color(150,150,150));
        g2.drawLine(0,getHeight()-1, getWidth()-1, getHeight()-1);
    }


    public static void main(String args[]) {
        JFrame w = new JFrame("Test");
        w.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        w.setResizable(true);
        DialogTitlePanel titlePanel = new DialogTitlePanel("Grant Permission", DialogTitlePanel.class.getResource("org.xito.launcher.images/keychain48.png"));
        w.getContentPane().add(titlePanel, BorderLayout.NORTH);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        w.getContentPane().add(p);

        w.setSize(500,400);
        w.setVisible(true);
    }

}
