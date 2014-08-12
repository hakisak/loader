/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.bean;

/**
 *
 * @author deane
 */
public class ControllerHelper {

    protected BeanController beanController;
    protected String propertyName;

    protected ControllerHelper(BeanController controller, String propertyName) {
        this.beanController = controller;
        this.propertyName = propertyName;
    }

    public BeanController getBeanController() {
        return beanController;
    }

    public void setBeanController(BeanController beanController) {
        this.beanController = beanController;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getPropertyValue() {
        return beanController.getPropertyValue(propertyName);
    }

}
