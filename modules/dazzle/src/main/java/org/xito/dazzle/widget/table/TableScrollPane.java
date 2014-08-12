package org.xito.dazzle.widget.table;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Provides a Scroll Pane for a Table. and displays alternating
 * row colors for the ViewPort area not covered by the Table
 */
public class TableScrollPane extends JScrollPane {

    private JTable table;

    public TableScrollPane(final JTable table) {
        super(table);
        this.table = table;

        final JViewport vp = new MyViewport();
        vp.setView(table);
        setViewport(vp);

        //every time the vertical scroll position changes we have to 
        //trigger a repaint of the viewport
        this.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
            public void adjustmentValueChanged(AdjustmentEvent e) {

                JViewport vp = TableScrollPane.this.getViewport();
                vp.repaint(vp.getViewRect());
            }
        });

        //when the table's rowheight changes we want to repaint the view port
        table.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals("rowHeight")) {
                    vp.repaint();
                }
            }
        });
        
    }

    public class MyViewport extends JViewport {

        public void paintComponent(Graphics g) {

            TableHelper.drawExtendedViewportRows(table, this, g);
        }


    }


}
