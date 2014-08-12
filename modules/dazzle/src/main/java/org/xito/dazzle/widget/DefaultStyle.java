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

package org.xito.dazzle.widget;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Default Style Attributes used by UI Controls
 *
 * @author Deane Richan
 */
public class DefaultStyle {

    //
    // Fonts
    //

    public final static String LABEL_FONT_KEY = "label.font";
    public final static String CTRL_TITLE_FONT_KEY = "ctrl.title.font";
    public final static String MENU_FONT_KEY = "menu.font";
   
    //
    // Colors
    //
    
    public final static String CTRL_BORDER_COLOR_KEY = "ctrl.border.color";
    public final static String CTRL_TOP_GRADIENT_COLOR_KEY = "ctrl.top.gradient.color";
    public final static String CTRL_BOTTOM_GRADIENT_COLOR_KEY = "ctrl.bottom.gradient.color";
    public final static String CTRL_TITLE_COLOR_KEY = "ctrl.title.color.key";
    public final static String CTRL_TITLE_HIGHLIGHT_COLOR_KEY = "ctrl.title.highlight.color.key";

    public final static String ERROR_GRADIENT_COLOR_KEY = "error.gradient.color";
    public final static String GENERAL_DIVIDER_COLOR_KEY = "general.divider.color";
    
    public static Color LINK_COLOR = new Color(20,20,255);

    public static Color ICON_BAR_SELECTED_SHADOW = new Color(60,60,60);
    public static Color ICON_BAR_TOP_GRAD = new Color(195,195,195);
    public static Color ICON_BAR_BOTTOM_GRAD = new Color(175,175,175);

    public static Color TOOL_BAR_TOP_GRAD = Color.WHITE;
    public static Color TOOL_BAR_BOTTOM_GRAD = new Color(225,225,225);

    public static Color LABEL_COLOR = new Color(150,150,150);
    public static Color LABEL_COLOR_DARK = new Color(102,102,102);

    private static Color CTRL_BORDER = new Color(102,102,102);

    public static Color CTRL_BEVEL_HIGHLIGHT = new Color(229,229,229);
    public static Color CTRL_BOTTOM_GRAD = new Color(154,154,154);
    public static Color CTRL_TOP_GRAD = new Color(195,195,195);

    public static Color INFO_BAR_BG = new Color(196,196,196);
    public static Color INFO_BAR_BEVEL_SHADOW = CTRL_BORDER;
    public static Color INFO_BAR_BEVEL_HIGHLIGHT = Color.WHITE;

    public static Color STACKED_PANEL_HEADER_COLOR = new Color(40,40,40);
    public static Color STACKED_PANEL_HEADER_TOP = CTRL_BEVEL_HIGHLIGHT;
    public static Color STACKED_PANEL_HEADER_BOTTOM = new Color(140,140,140);

    public static Color TABLE_HEADER_BORDER = new Color(196,196,196);
    public static Color TABLE_HEADER_TOP_GRAD = new Color(240,240,240);
    public static Color TABLE_HEADER_BOTTOM_GRAD = Color.WHITE;
    public static Color TABLE_EVEN_ROW = new Color(237,243,254);
    public static Color TABLE_ODD_ROW = Color.WHITE;
    public static Color TABLE_DIRTY_CELL = new Color(225,225,225);


    public static Color SELECTED_COL_BACKGROUND = new Color(227,233,254);
    public static Color SELECTED_CELL_BACKGROUND = new Color(81, 148, 243);
    public static Color SELECTED_ROW_BACKGROUND = new Color(61, 128, 223);
    public static Color SELECTED_ROW_FOREGROUND = Color.WHITE;

    public static Color CMD_BTN_BG_DISABLED = new Color(225,225,225);
    public static Color CMD_BTN_BORDER_DISABLED = new Color(140, 140, 140);
    //public static Color CMD_BTN_BORDER = GENERAL_DIVIDER;
    public static Color CMD_BTN_BORDER = new Color(105,105,105);
    public static Color CMD_BTN_TEXT = Color.BLACK;
    public static Color CMD_BTN_TEXT_DISABLED = new Color(140,140,140);
    //public static Color CMD_BTN_TOP_GRAD = STACKED_PANEL_HEADER_TOP;
    public static Color CMD_BTN_TOP_GRAD = new Color(225, 225, 225);
    //public static Color CMD_BTN_BOTTOM_GRAD = STACKED_PANEL_HEADER_BOTTOM;
    public static Color CMD_BTN_BOTTOM_GRAD = new Color(165, 165, 165);

    public static Color CMD_BTN_PRESSED_BG = new Color(10,10,10);

    public static Color TAB_BAR_TOP = CTRL_TOP_GRAD;
    public static Color TAB_BAR_BOTTOM = CTRL_BOTTOM_GRAD;
    public static Color TAB_BAR_BEVEL_SHADOW = CTRL_BORDER;
    public static Color TAB_COMP_BG = new Color(140,140,140);
    public static Color TAB_COMP_SELECTED_BG = Color.WHITE;
    public static Color TAB_COMP_SELECTED_BG_SHADOW = ICON_BAR_SELECTED_SHADOW;
    public static Color TAB_COMP_TOP_GRAD = new Color(240,240,240);
    public static Color TAB_COMP_BOTTOM_GRAD = Color.WHITE;

    public static Color SPLIT_TITLE_DIV_BOTTOM = new Color(140,140,140);
    public static Color SPLIT_TITLE_DIV_TOP_GRAD = new Color(240,240,240);
    public static Color SPLIT_TITLE_DIV_BOTTOM_GRAD = new Color(202,202,202);

    public static Color REQUIRED_FIELD_BACKGROUND = new Color(0,0,255,15);
    public static Color ERROR_FIELD_BACKGROUND = new Color(255,0,0,15);

    public static Color REQUIRED_LBL_COLOR = new Color(0,0,150);
    public static Color ERROR_LBL_COLOR = new Color(150,0,0);

    public static Color MESSAGE_PANEL_TOP_GRAD = new Color(255, 252, 218);
    public static Color MESSAGE_PANEL_BOTTOM_GRAD = new Color(240, 231, 142);
    public static Color MESSAGE_TEXT = new Color(169, 119, 0);

    public static Color SOURCELIST_BACKGROUND = new Color(222, 228, 234);
    public static Color SOURCELIST_HEADER_TEXT_COLOR = new Color(127,130,146);
    public static Color SOURCELIST_SELECTED_ROW_TOP = new Color(176, 190, 215);
    public static Color SOURCELIST_SELECTED_ROW_BOTTOM = new Color(133, 153, 186);


    // Borders
    public static Border TABLE_HEADER_CELL_BORDER = new EmptyBorder(2, 5, 2, 5);
    private static UIDefaults styleMap;
    
    public static UIDefaults getDefaults() {
       if(styleMap != null) return styleMap;
       
       styleMap = new UIDefaults();
       
       //fonts
       styleMap.put(LABEL_FONT_KEY, UIManager.getDefaults().getFont("Label.font").deriveFont(Font.PLAIN, 11));
       styleMap.put(MENU_FONT_KEY, styleMap.getFont(LABEL_FONT_KEY));
       //styleMap.put(CTRL_TITLE_FONT_KEY, styleMap.getFont(LABEL_FONT_KEY).deriveFont(Font.BOLD, 12));
       styleMap.put(CTRL_TITLE_FONT_KEY, styleMap.getFont(LABEL_FONT_KEY).deriveFont(12));
       
       //colors
       styleMap.put(CTRL_BORDER_COLOR_KEY,  new Color(124,124,124));
       styleMap.put(CTRL_TOP_GRADIENT_COLOR_KEY, new Color(196,196,196));
       styleMap.put(CTRL_BOTTOM_GRADIENT_COLOR_KEY, new Color(153,153,153));
       styleMap.put(ERROR_GRADIENT_COLOR_KEY, new Color(255,200,200));
       styleMap.put(CTRL_TITLE_COLOR_KEY, Color.BLACK);
       styleMap.put(CTRL_TITLE_HIGHLIGHT_COLOR_KEY, new Color(255, 255, 255, 150));
       
       styleMap.put(GENERAL_DIVIDER_COLOR_KEY, new Color(70,70,70));

       
       return styleMap;
    }

}