package org.xito.dazzle.widget.table;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * A Table that uses alternating row colors like on the mac
 * @author deane
 */
public class ScrollTable extends JTable {

    protected TableCellRenderer myDefaultRenderer;
    protected boolean drawSelectedColumn = false;
    protected Color selectedColumnColor;

    public ScrollTable() {
        super();
        init();
    }

    public ScrollTable(Object[][] data, String[] colNames) {
        super(data, colNames);
        init();
    }

    public ScrollTable(TableModel model) {
        super(model);
        init();
    }

    protected void init() {
        myDefaultRenderer = new ScrollTableCellRenderer();
    }

    /**
     * Get PreferredSize
     * @return Dimension
     */
    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();

        if(getParent() instanceof JViewport) {
            int viewPortH = ((JViewport)getParent()).getHeight();
            if(viewPortH > dim.height) {
                dim.height = viewPortH;
            }
        }

        return dim;
    }

    /**
     * Paint the Component
     */
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        //draw fake rows
        TableHelper.drawExtendedBottomRows(this, g);

        //draw selected column
        if(drawSelectedColumn) {
            TableHelper.drawSelectedBottomCol(this, g, selectedColumnColor);
        }

        //draw vertical grid lines
        TableHelper.drawVerticalGrid(this, g);
    }

    public TableCellRenderer getDefaultRenderer(Class columnClass) {
        return myDefaultRenderer;
    }


}
