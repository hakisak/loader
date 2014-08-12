/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.bean;

import javax.swing.JTextField;

/**
 *
 * @author deane
 */
public class BeanTextField extends JTextField implements BeanComponent {

    protected ControllerHelper controllerHelper;

    public BeanTextField(BeanController controller, String propertyName) {
        controllerHelper = new ControllerHelper(controller, propertyName);
    }

    public void beanUpdated() {
        Object propValue = controllerHelper.getPropertyValue();
    }

}
