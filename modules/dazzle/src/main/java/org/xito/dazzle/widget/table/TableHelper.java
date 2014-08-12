package org.xito.dazzle.widget.table;

import org.xito.dazzle.widget.DefaultStyle;
import org.xito.dazzle.*;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * Helper class used to improve the UI of JTables
 * @author deane
 */
public class TableHelper {

    public static int getColumnXLocation(JTable table, int col) {

        //return -1 if the col is greater then the column count
        if(col>table.getColumnCount()) {
            return -1;
        }

        int x = 0;
        for(int i= 0;i<col;i++) {
            x = x + table.getColumnModel().getColumn(i).getWidth();
        }

        return x;
    }

    /**
     * Draw the Bottom Extended Rows of a Table. These are not real rows but
     * just rows that take up space at the bottom
     * @param table that you are drawing bottom rows for
     * @param g graphics that you are drawing to
     */
    public static void drawExtendedBottomRows(JTable table, Graphics g) {

        int preferredH = getTablePreferredHeight(table);
        int h = table.getHeight();
        int w = table.getWidth();
        int rowH = table.getRowHeight();
        int rowCount = table.getRowCount();
        int fakeRowCount = ((h - preferredH)/rowH) + 1; //1 extra fake row

        //nothing to draw
        if(h <= preferredH) return;

        //acts as a color queue for alternating colors
        LinkedList colors = new LinkedList();
        colors.addLast(DefaultStyle.TABLE_EVEN_ROW);
        if((rowCount % 2)==0) {
            colors.addLast(DefaultStyle.TABLE_ODD_ROW);
        }
        else {
            colors.addFirst(DefaultStyle.TABLE_ODD_ROW);
        }

        //Draw Rows
        for(int i=0;i<fakeRowCount;i++) {

            //Get the next color and then place it back in queue
            Color c = (Color)colors.removeFirst();
            g.setColor(c);
            colors.addLast(c);

            int y = (i*rowH) + preferredH;
            g.fillRect(0, y, w, rowH);
        }
    }

    /**
     * drawSelectedBottomCol
     * @param table
     * @param g
     * @param selectColor
     */
    public static void drawSelectedBottomCol(JTable table, Graphics g, Color selectColor) {

        int selectedCol = table.getSelectedColumn();
        if(selectedCol == -1) return;

        //return if the column no longer exists
        if(selectedCol >= table.getColumnModel().getColumnCount()) {
            return;
        }

        int selectedColWidth = table.getColumnModel().getColumn(selectedCol).getWidth();
        int x = 0;
        for(int i=0;i<selectedCol;i++) {
            x = x + table.getColumnModel().getColumn(i).getWidth();
        }

        int y = getTablePreferredHeight(table);
        int w = selectedColWidth;
        int h = table.getHeight();

        if(selectColor == null) {
            selectColor = DefaultStyle.SELECTED_COL_BACKGROUND;
        }

        g.setColor(selectColor);

        g.fillRect(x, y, w, h);
    }

    /**
     * drawExtendedViewportRows
     * @param table
     * @param viewport
     * @param g
     */
    public static void drawExtendedViewportRows(JTable table, JViewport viewport, Graphics g) {

        int preferredH = getTablePreferredHeight(table);
        int h = table.getHeight();
        int x = table.getWidth();
        int w = viewport.getWidth();
        int rowH = table.getRowHeight();
        int rowCount = table.getRowCount();
        int fakeRowCount = ((h - preferredH)/rowH) + 1; //1 extra fake row

        //nothing to draw
        if(x >= w) return;

        //acts as a color queue for alternating colors
        LinkedList colors = new LinkedList();
        colors.addLast(DefaultStyle.TABLE_EVEN_ROW);
        colors.addLast(DefaultStyle.TABLE_ODD_ROW);

        //draw data rows
        //start at location - viewport position
        int y = -1 * (int)viewport.getViewPosition().getY();
        int vph = (int)viewport.getExtentSize().getHeight();

        for(int i=0;i<rowCount;i++) {
            //Get the next color and then place it back in queue
            Color c = (Color)colors.removeFirst();
            g.setColor(c);
            colors.addLast(c);

            rowH = table.getRowHeight(i);
            //if still above viewport then skip
            if(y > -rowH) {
                g.fillRect(x, y, w, rowH);
            }

            y = y + rowH;

            //stop drawing if the bottom part is not visible in viewport
            if(y > vph) break;
        }

        //Draw Bottom Rows
        for(int i=0;i<fakeRowCount;i++) {

            //Get the next color and then place it back in queue
            Color c = (Color)colors.removeFirst();
            g.setColor(c);
            colors.addLast(c);

            g.fillRect(x, y, w, rowH);
            y = y + rowH;
        }

    }

    /**
     * Get the PreferredHeight of a Table based on its Row Heights
     * @param table
     */
    public static int getTablePreferredHeight(JTable table) {

        int rowCount = table.getRowCount();
        int totalH = 0;
        for(int i=0;i<rowCount;i++) {
            totalH = totalH + table.getRowHeight(i);
        }

        return totalH;
    }

    /**
     * Draw the Vertical Grid Lines for a Table
     * @param table
     * @param g
     */
    public static void drawVerticalGrid(JTable table, Graphics g) {

        //draw grid lines
        int totalW = 0;
        g.setColor(DefaultStyle.TABLE_HEADER_BORDER);
        for(int i=0;i<table.getColumnCount();i++) {
            int cw = table.getColumnModel().getColumn(i).getWidth();
            totalW = totalW + cw;
            g.drawLine(totalW-1, 0, totalW-1, table.getHeight());
        }

    }

}
