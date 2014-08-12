// Copyright 2009 Xito.org
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

package org.xito.dazzle.widget.button;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import org.xito.dazzle.utilities.MacApplicationUtilities;
import org.xito.dazzle.utilities.UIUtilities;
import org.xito.dazzle.widget.laf.button.GradientButtonUI;
import org.xito.dazzle.widget.laf.button.SquareButtonUI;

/**
 *
 * @author deane
 */
public class BasicButton extends JButton {
   
    protected ButtonStyle buttonStyle;
    protected boolean useMacStyles = false;
    
    /**
     * Simple Constructor
     */
    public BasicButton(ButtonStyle style) {
       buttonStyle = style;
       initStyle();
    }
    
    protected void initStyle() {
       boolean useMacStyle = false;
       if(MacApplicationUtilities.isAtLeastMacOSVersionX5() && useMacStyles) {
          useMacStyle = true;
       }
       
       switch(buttonStyle) {
          case SQUARE:
             if(useMacStyle) setMacButtonStyle("square"); else setUI(new SquareButtonUI());
             break;
          case GRADIENT:
             if(useMacStyle) setMacButtonStyle("gradient"); else setUI(new GradientButtonUI());
             break;
       }
    
    }
    
    protected void setMacButtonStyle(String style) {
       putClientProperty("JButton.buttonType", style);
    }
}
