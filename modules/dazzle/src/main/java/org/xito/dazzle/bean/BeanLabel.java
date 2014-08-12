/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.bean;

import javax.swing.JLabel;

/**
 * A JLabel that renders the contents of a BeanProperty
 *
 * @author deane
 */
public class BeanLabel extends JLabel implements BeanComponent {

    protected ControllerHelper controllerHelper;

    public BeanLabel(BeanController controller, String propertyName) {
        setController(controller, propertyName);
    }

    public void setController(BeanController controller, String propertyName) {
        controllerHelper = new ControllerHelper(controller, propertyName);
    }

    /**
     * BeanComponent interface. Called when this components Bean is updated
     */
    public void beanUpdated() {

    }

}
