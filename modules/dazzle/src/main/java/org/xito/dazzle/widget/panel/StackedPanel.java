// Copyright 2007 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.dazzle.widget.panel;

import org.xito.dazzle.widget.DefaultStyle;
import org.xito.dazzle.*;
import org.xito.dazzle.widget.button.GrowButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.event.*;

/**
 * Panel that contains Vertically Stacked Panels
 *
 * @author Deane Richan
 */
public class StackedPanel extends JPanel {

   // Serial Version ID
   private static final long serialVersionUID = 1L;
   
   public static final String HEADER_BORDER_COLOR_KEY = "stackedpanel.header.border.color.key";
   public static final String HEADER_TITLE_FONT_KEY = "stackedpanel.header.title.font.key";
   public static final String HEADER_TITLE_COLOR_KEY = "stackedpanel.header.title.color.key";
   public static final String HEADER_TITLE_BEVEL_COLOR_KEY = "stackedpanel.header.title.bebel.color.key";
   public static final String HEADER_TOP_GRADIENT_KEY = "stackedpanel.header.top.gradient.key";
   public static final String HEADER_BOTTOM_GRADIENT_KEY = "stackedpanel.header.bottom.gradient.key";
   public static final String BACKGROUND_KEY = "stackedpanel.background.key";
   
   protected Font headerFont = null;
   protected Color headerTitleColor = null;
   protected Color headerTitleBevelColor = null;
   protected Color headerTopGradColor = null;
   protected Color headerBottomGradColor = null;
   protected Color backgroundColor = null;
   protected Color headerBorderColor = null;
   protected UIDefaults styleMap;
   protected ArrayList panelList = new ArrayList();

   /**
    * Default Constructor
    */
   public StackedPanel() {
      init();
   }
   
   /**
    * Style Constructor
    */
   public StackedPanel(UIDefaults styleMap) {
      this.styleMap = styleMap;
      init();
   }

   /**
    * Initialize the Component
    */
   private void init() {
      initStyle();
      setOpaque(true);
      setBackground(backgroundColor);
      setMinimumSize(new Dimension(100, 100));
      setLayout(new BorderLayout());
   }

   protected void initDefaultStyle() {
      
      if(this.styleMap == null) {
         this.styleMap = DefaultStyle.getDefaults();
      }
      
      //background
      if(!styleMap.containsKey(BACKGROUND_KEY))
         styleMap.put(BACKGROUND_KEY, new Color(209,215,226));   
      
      //header title font
      if(!styleMap.containsKey(HEADER_TITLE_FONT_KEY))
         styleMap.put(HEADER_TITLE_FONT_KEY, styleMap.getFont(DefaultStyle.LABEL_FONT_KEY));   
      
      //header title color
      if(!styleMap.containsKey(HEADER_TITLE_COLOR_KEY))
         styleMap.put(HEADER_TITLE_COLOR_KEY, new Color(40,40,40));
      
      //header title bevel color
      if(!styleMap.containsKey(HEADER_TITLE_BEVEL_COLOR_KEY))
         styleMap.put(HEADER_TITLE_BEVEL_COLOR_KEY, new Color(196,196,196));
      
      //header border color
      if(!styleMap.containsKey(HEADER_BORDER_COLOR_KEY))
         styleMap.put(HEADER_BORDER_COLOR_KEY, styleMap.getFont(DefaultStyle.CTRL_BORDER_COLOR_KEY));
      
      //header top gradient
      if(!styleMap.containsKey(HEADER_TOP_GRADIENT_KEY))
         styleMap.put(HEADER_TOP_GRADIENT_KEY, new Color(196,196,196));
      
      //header bottom gradient
      if(!styleMap.containsKey(HEADER_BOTTOM_GRADIENT_KEY))
         styleMap.put(HEADER_BOTTOM_GRADIENT_KEY, new Color(153,153,153));
   
   }
   
   protected void initStyle() {
      
      initDefaultStyle();
      
      //Read Style Colors
      backgroundColor = styleMap.getColor(BACKGROUND_KEY);
      headerFont = styleMap.getFont(HEADER_TITLE_FONT_KEY);
      headerTitleColor = styleMap.getColor(HEADER_TITLE_COLOR_KEY);
      headerTitleBevelColor = styleMap.getColor(HEADER_TITLE_BEVEL_COLOR_KEY);
      headerTopGradColor = styleMap.getColor(HEADER_TOP_GRADIENT_KEY);
      headerBottomGradColor = styleMap.getColor(HEADER_BOTTOM_GRADIENT_KEY);
      headerBorderColor = styleMap.getColor(HEADER_BORDER_COLOR_KEY);
   }
   
   public void addNotify() {
      super.addNotify();
      if (getParent() instanceof JSplitPane) {
         setEnableSplitControl(true);
      } else {
         setEnableSplitControl(false);
      }
   }

   public void setEnableSplitControl(boolean b) {

      // check to see if we can enable the split control
      JSplitPane parent = null;
      if (b) {
         parent = getParentSplitPane();
      }

      // hide/show the split control on embedded panels
      TitlePanel[] headerPanels = getAllHeaderPanels();
      for (int i = 0; i < headerPanels.length; i++) {
         headerPanels[i].splitCtrl.setSplitPane(parent);
      }
   }

   private TitlePanel[] getAllHeaderPanels() {

      ArrayList panelList = new ArrayList();
      findAllHeaderPanels(this, panelList);

      return (TitlePanel[]) panelList.toArray(new TitlePanel[0]);
   }

   private void findAllHeaderPanels(Container container, java.util.List panelList) {
      Component comps[] = container.getComponents();
      for (int i = 0; i < comps.length; i++) {
         if (comps[i] instanceof TitlePanel) {
            panelList.add(comps[i]);
         } else if (comps[i] instanceof Container) {
            findAllHeaderPanels((Container) comps[i], panelList);
         }
      }
   }

   /**
    * Add a panel to this StackedPanel
    * 
    * @param panel
    */
   public synchronized void addPanel(String title, String name, JPanel panel) {

      boolean isMainPanel = (panelList.size() == 0);

      JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      split.setName(title);
      split.setBorder(new EmptyBorder(0, 0, 0, 0));
      split.setDividerSize(0);
      split.setContinuousLayout(true);

      StackedPanel.StackPanel stackPanel = new StackedPanel.StackPanel(title, name, split, panel, isMainPanel);

      if (isMainPanel) {
         add(split, BorderLayout.CENTER);
         split.setTopComponent(stackPanel);
         split.setBottomComponent(null);
      } else {
         JSplitPane existingSplit = (JSplitPane) panelList.get(panelList.size() - 1);
         stackPanel.setParentSplit(existingSplit);
      }

      panelList.add(split);
   }

   public void resetStackPanelLocations() {
      // no panels so just return
      if (panelList.size() <= 0)
         return;

      JSplitPane sp = (JSplitPane) panelList.get(0);
      sp.setDividerLocation(0.5d);
   }

   public JSplitPane getStackPanelSplit(int index) {

      if (index < 0 || index >= panelList.size()) {
         return null;
      } else {
         return (JSplitPane) panelList.get(index);
      }
   }

   public JSplitPane getParentSplitPane() {

      if (getParent() instanceof JSplitPane) {
         return (JSplitPane) getParent();
      } else {
         return null;
      }
   }

   public void expansionEvent(StackedPanel.StackPanel stackPanel, boolean collapsed) {
      revalidate();
   }
   
   public void paint(Graphics g) {
      super.paint(g);
      //UIUtilities.paintDebugLines(g, this);
   }

   // ---------------------------------------------------------------

   /**
    * StackPanel
    * 
    * A Panel that contains a Header panel and an Component Panel. The
    * Stackpanel can be collapsed and the Header will still remain visible
    * however the component Panel will be hidden
    */
   private class StackPanel extends JPanel {


      private static final long serialVersionUID = 1L;

      private JSplitPane mysplitPane;

      private JPanel containedPanel;

      private boolean collapsed_flag = false;

      private StackedPanel.TitlePanel header;

      private boolean isMainPanel_flag = false;
      
      private String key;

      /**
       * Create a StackPanel with the specified title, key and panel
       * @param title
       * @param key
       * @param splitPane
       * @param panel
       * @param isMainPanel
       */
      public StackPanel(String title, String name, JSplitPane splitPane, JPanel panel, boolean isMainPanel) {

         this.setName(name);
         this.mysplitPane = splitPane;
         this.containedPanel = panel;
         this.isMainPanel_flag = isMainPanel;
         this.header = new StackedPanel.TitlePanel(this, title);

         setMinimumSize(header.getPreferredSize());
         setLayout(new BorderLayout());

         add(header, BorderLayout.NORTH);
         add(containedPanel, BorderLayout.CENTER);

         splitPane.setTopComponent(this);
      }

      public void setCollapsed(boolean shouldCollapse) {

         collapsed_flag = shouldCollapse;

         JSplitPane split = (JSplitPane) mysplitPane.getParent();

         // recalculate split location
         if (collapsed_flag) {
            int y = split.getHeight() - (header.getHeight() + split.getDividerSize());
            split.setDividerLocation(y);
         } else {
            int y = split.getHeight() - getPreferredSize().height;
            split.setDividerLocation(y);
         }

         StackedPanel.this.expansionEvent(this, collapsed_flag);
      }

      public boolean isCollapsed() {
         return collapsed_flag;
      }

      public boolean isMainPanel() {
         return this.isMainPanel_flag;
      }

      public TitlePanel getHeaderPanel() {
         return header;
      }

      public void setParentSplit(JSplitPane splitPane) {
         header.setParentSplitPane(splitPane);
         splitPane.setBottomComponent(mysplitPane);
      }
   }

   // ---------------------------------------------------------------

   /**
    * HeaderPanel
    * 
    * Panel that represents the Header at the top of each embedded Panel
    */
   private class TitlePanel extends JPanel implements MouseListener, MouseMotionListener {

      private static final long serialVersionUID = 1L;

      private StackedPanel.StackPanel parentStackPanel;

      private SplitPaneControl splitCtrl;

      private JSplitPane parentSplitPane;

      private String title;

      private GrowButton growBtn;

      private Dimension preferredSize;

      private int startY;

      private int startSplitY;

      public TitlePanel(StackedPanel.StackPanel parentStackPanel, String title) {

         this.parentStackPanel = parentStackPanel;
         this.title = title;

         setLayout(new BorderLayout());
         setOpaque(false);

         growBtn = new GrowButton(false);
         growBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               if (TitlePanel.this.parentStackPanel.isCollapsed()) {
                  growBtn.setDownIcons();
               } else {
                  growBtn.setUpIcons();
               }

               // toggle the collapse status of the stack Panel
               TitlePanel.this.parentStackPanel.setCollapsed(!TitlePanel.this.parentStackPanel.isCollapsed());
            }
         });

         JPanel buttonPanel = new JPanel(new BorderLayout());
         buttonPanel.setOpaque(false);
         buttonPanel.setBorder(new EmptyBorder(2, 4, 0, 0));
         buttonPanel.add(growBtn, BorderLayout.CENTER);
         add(buttonPanel, BorderLayout.WEST);

         splitCtrl = new SplitPaneControl(null);
         add(splitCtrl, BorderLayout.EAST);

         if (parentStackPanel.isMainPanel()) {
            growBtn.setVisible(false);
         } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
            addMouseListener(this);
            addMouseMotionListener(this);
            growBtn.setVisible(true);
         }

         setPreferredSize(splitCtrl.getPreferredSize());
      }

      public void setParentSplitPane(JSplitPane parentSplitPane) {
         this.parentSplitPane = parentSplitPane;
      }

      public void mouseDragged(MouseEvent e) {

         Dimension minSize = parentSplitPane.getTopComponent().getMinimumSize();
         Dimension minSize2 = parentSplitPane.getBottomComponent().getMinimumSize();

         int y = e.getComponent().getLocationOnScreen().y + e.getPoint().y;
         int deltaY = y - startY;
         int splitLoc = startSplitY + deltaY;

         // Just return if we are already at top component minimum
         if (splitLoc <= minSize.height) {
            parentSplitPane.setDividerLocation(minSize.height);
            return;
         }

         // Just return if we are already at bottom component minimum
         if (splitLoc > parentSplitPane.getHeight() - minSize2.height) {
            parentSplitPane.setDividerLocation(parentSplitPane.getHeight() - minSize2.height);
            return;
         }

         parentSplitPane.setDividerLocation(splitLoc);
      }

      public void mouseMoved(MouseEvent mouseEvent) {
         // not used
      }

      public void mouseClicked(MouseEvent mouseEvent) {
         // not used
      }

      public void mousePressed(MouseEvent e) {
         startY = e.getComponent().getLocationOnScreen().y + e.getPoint().y;
         startSplitY = parentSplitPane.getDividerLocation();
      }

      public void mouseReleased(MouseEvent mouseEvent) {
         startY = 0;
      }

      public void mouseEntered(MouseEvent mouseEvent) {
         // not used
      }

      public void mouseExited(MouseEvent mouseEvent) {
         // not used
      }

      /**
       * Paint the Header Panel
       * 
       * @param g
       */
      public void paintComponent(Graphics g) {

         Graphics2D g2 = (Graphics2D) g;

         // get title font info
         FontRenderContext fc = g2.getFontRenderContext();
         LineMetrics lm = headerFont.getLineMetrics(title, fc);
         Rectangle2D titleBounds = headerFont.getStringBounds(title, fc);
         setFont(headerFont);

         // calc preferred size
         if (preferredSize == null) {
            preferredSize = new Dimension((int) (titleBounds.getWidth() + 10), (int) (titleBounds.getHeight() + 4));
            setPreferredSize(preferredSize);
            this.setSize(getWidth(), preferredSize.height);
         }

         // Draw Background
         GradientPaint grad = new GradientPaint(0, 0, headerTopGradColor, 0, getHeight(),
               headerBottomGradColor);
         g2.setPaint(grad);
         g2.fillRect(0, 0, getWidth(), getHeight());

         // Draw Border
         g2.setPaint(headerBorderColor);
         g2.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
         // g2.drawLine(getWidth()-1, 0, getWidth()-1, getHeight()-1);

         // Draw Title - Bevel First
         g2.setColor(headerTitleBevelColor);
         int cx = getWidth() / 2;
         int cy = getHeight() / 2;
         float tx = (float) (cx - titleBounds.getCenterX());
         float ty = (float) (cy + lm.getHeight() / 3);
         g2.drawString(title, tx, ty);

         // ty = ty - (float)(lm.getHeight()/10);
         g2.setColor(headerTitleColor);
         g2.drawString(title, tx, ty - 1);
      }
   }

}
