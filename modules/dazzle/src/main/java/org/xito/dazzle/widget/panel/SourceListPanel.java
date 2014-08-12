package org.xito.dazzle.widget.panel;

import org.xito.dazzle.utilities.DrawUtilities;
import org.xito.dazzle.widget.DefaultStyle;
import org.xito.dialog.TableLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * SourceListPanel
 *
 * @author drichan
 */
public class SourceListPanel extends JComponent {

   private TableLayout layout = new TableLayout();
   private SourceListTreeBadgeProvider badgeProvider;


   public SourceListPanel() {
      init();
   }

   /**
    * Create a source list panel with 1 section
    * @param title
    * @param treeModel
    */
   public SourceListPanel(String title, TreeModel treeModel, TreeSelectionListener selectionListener) {
      init();
      addSection(title, treeModel, selectionListener);
   }

   private void init() {

      setLayout(layout);
   }

   /**
    * Set the BadgeProvider for this source list
    * @param badgeProvider
    */
   public void setSourceListTreeBadgeProvider(SourceListTreeBadgeProvider badgeProvider) {
      this.badgeProvider = badgeProvider;
   }

   /**
    * Paint Component
    * @param g
    */
   public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;

      //mac blue
      g2.setColor(DefaultStyle.SOURCELIST_BACKGROUND);

      g2.fillRect(0,0,getWidth(), getHeight());
   }

   /**
    * add a section to the sourcelist panel
    * @param title
    * @param model
    * @param selectionListener
    * @return
    */
   public SectionPanel addSection(String title, TreeModel model, TreeSelectionListener selectionListener) {

      SectionPanel sectionPanel = new SectionPanel(title, model, selectionListener);

      TableLayout.Row row = new TableLayout.Row();
      TableLayout.Column col = new TableLayout.Column(title, TableLayout.PERCENT_100);
      col.hAlign = TableLayout.FULL;
      row.addCol(col);
      layout.addRow(row);

      add(title, sectionPanel);

      return sectionPanel;
   }

   public SectionPanel[] getSectionPanels() {

      ArrayList<SectionPanel> sections = new ArrayList<SectionPanel>();
      for(Component c : getComponents()) {
         if(c instanceof SectionPanel) {
            sections.add((SectionPanel)c);
         }
      }

      return sections.toArray(new SectionPanel[sections.size()]);
   }

   public void removeSelectionFromSections(SectionPanel currentSelectedSectionPanel) {
      for(Component c : getComponents()) {
         if(c == currentSelectedSectionPanel) {
            continue;
         }
         else if(c instanceof SectionPanel) {
            ((SectionPanel)c).getTree().setSelectionRow(-1);   
         }
      }
   }


   /**
    * Section Panel of a Source List
    */
   public class SectionPanel extends JComponent {

      private MyTree tree;

      public SectionPanel(String title, TreeModel model, TreeSelectionListener selectionListener) {
         tree = new MyTree(model);
         if(selectionListener != null) {
            tree.addTreeSelectionListener(selectionListener);
         }

         setLayout(new BorderLayout());
         add(new HeaderPanel(title), BorderLayout.NORTH);
         add(tree);
      }

      public JTree getTree() {
         return tree;
      }

      public void addTreeSelectionListener(TreeSelectionListener listener) {
         getTree().addTreeSelectionListener(listener);
      }
   }

   /**
    *  HeaderPanel for SourceListPanel
    */
   private class HeaderPanel extends JComponent {

      private String title;
      private Font titleFont;

      public HeaderPanel() {
         this(null);
      }

      public HeaderPanel(String title) {
         setTitle(title);
         titleFont = new JLabel().getFont().deriveFont(Font.BOLD, 11f);
         Rectangle2D bounds = DrawUtilities.getStringBounds(titleFont, title);
         setPreferredSize(new Dimension((int)bounds.getWidth(), (int)bounds.getHeight() + 6));
      }

      public String getTitle() {
         return title;
      }

      public void setTitle(String title) {
         this.title = title;
      }

      public void paintComponent(Graphics g) {

         Graphics2D g2 = DrawUtilities.getGraphics2DWithAntiAliasing(g);
         g2.setColor(DefaultStyle.SOURCELIST_HEADER_TEXT_COLOR);
         g2.setFont(titleFont);
         Rectangle2D titleBounds = titleFont.getStringBounds(title, g2.getFontRenderContext());
         g2.drawString(title.toUpperCase(), 8f, (float)getHeight()/2 + (float)titleBounds.getHeight()/2);
      }
   }

   /**
    * Class for JTree used in SectionPanel
    */
   public class MyTree extends JTree {

      private Font badgeFont;

      public MyTree(TreeModel model) {
         super(model);

         setOpaque(false);
         setRootVisible(false);
         setShowsRootHandles(true);
         MyCellRenderer cellRenderer = new MyCellRenderer();
         badgeFont = cellRenderer.getLabelFont().deriveFont(Font.BOLD, 11);
         setCellRenderer(cellRenderer);
         setBorder(new EmptyBorder(0, 15, 0, 0));

         //listen for focus changes and release focus on other sections
         addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
               SourceListPanel.this.removeSelectionFromSections((SectionPanel)SwingUtilities.getAncestorOfClass(SectionPanel.class, MyTree.this));
            }
         });

         addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               //listen for click and select the proper row
               Point p = e.getPoint();
               int clickRow = p.y / MyTree.this.getRowHeight();
               int[] selectedRows = MyTree.this.getSelectionRows();

               //if the click row isn't selected then lets select it
               if( (selectedRows != null && selectedRows.length > 0 && selectedRows[0] != clickRow && clickRow < MyTree.this.getRowCount())
                     || clickRow < MyTree.this.getRowCount()) {
                  MyTree.this.setSelectionRow(clickRow);
               }

            }
         });

      }

      /**
       * paint component
       * @param g
       */
      public void paintComponent(Graphics g) {

         Graphics2D g2 = DrawUtilities.getGraphics2DWithAntiAliasing(g);

         //paint selection background
         int[] selectedRows = this.getSelectionRows();
         if(selectedRows != null && selectedRows.length > 0) {
            Rectangle r = this.getRowBounds(selectedRows[0]);

            GradientPaint selectedPaint = new GradientPaint(
                                 0, r.y, DefaultStyle.SOURCELIST_SELECTED_ROW_TOP,
                                 0, r.y + r.height, DefaultStyle.SOURCELIST_SELECTED_ROW_BOTTOM);

            g2.setPaint(selectedPaint);
            g2.fillRect(0, r.y, this.getWidth(), r.height);

            g2.setPaint(DefaultStyle.SOURCELIST_SELECTED_ROW_BOTTOM);
            g2.drawLine(0, r.y, this.getWidth(), r.y);
         }

         //paint badges
         if(SourceListPanel.this.badgeProvider != null) {
            paintBadges(g2);
         }

         super.paintComponent(g);
      }

      /**
       * paint badges
       * @param g2
       */
      public void paintBadges(Graphics2D g2) {

         int[] selectedRows = getSelectionRows();
         g2.setFont(badgeFont);

         for(int i=0;i<getRowCount();i++) {

            TreePath path = getPathForRow(i);
            String badgeText = badgeProvider.getBadgeTextForPath(getModel(), path);
            if(badgeText == null) {
               continue;
            }

            Rectangle r = this.getRowBounds(i);
            Rectangle2D bounds = DrawUtilities.getStringBounds(g2.getFont(), badgeText);
            int badgeW = (int)bounds.getWidth() + 12;
            int badgeH = (int)r.height - 6;
            int badgeX = (int)(this.getWidth() - (bounds.getWidth() + 20));
            int badgeY = (int)( (r.y + r.height/2) - badgeH/2 );
            int textX = badgeX + (int)(badgeW/2 - bounds.getWidth()/2 + 1);
            int textY = (int)(badgeY + badgeH/2 + bounds.getHeight()/2 - 1);

            //row selected
            if(selectedRows != null && selectedRows.length > 0 && selectedRows[0] == i) {
               g2.setColor(Color.WHITE);
               g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, badgeH, badgeH);
               g2.setColor(new Color(151,169,198));
               g2.drawString(badgeText, textX, textY);
            }
            else {
               g2.setColor(new Color(151,169,198));
               g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, badgeH, badgeH);
               g2.setColor(Color.WHITE);
               g2.drawString(badgeText, textX, textY);
            }
         }
      }
   }

   /**
    * Cell Renderer
    */
   public class MyCellRenderer extends DefaultTreeCellRenderer {

      private JLabel lbl;

      public MyCellRenderer() {
         lbl = new JLabel();
         lbl.setOpaque(false);
         //lbl.setFont(lbl.getFont().deriveFont(11f));
      }

      public Font getLabelFont() {
         return lbl.getFont();
      }

      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

         //JLabel comp = (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

         lbl.setText(value.toString());

         if(expanded) lbl.setIcon(this.getDefaultOpenIcon());
         if(!expanded) lbl.setIcon(this.getDefaultClosedIcon());
         if(leaf) lbl.setIcon(this.getDefaultLeafIcon());
         if(sel) lbl.setForeground(Color.WHITE); else lbl.setForeground(Color.BLACK);

         return lbl;
      }
   }
}
