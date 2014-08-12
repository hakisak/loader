/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.widget;

import org.xito.dazzle.widget.button.*;

/**
 *
 * @author deane
 */
public class WidgetFactory {

    // Buttons
    //------------------------------------------------------------
    
    public static BasicButton createButton(ButtonStyle style) {
        return new BasicButton(style);
    }

}
