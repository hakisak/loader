/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.bean;

import java.util.HashMap;

/**
 *
 * @author deane
 */
public class BeanController {

    protected Object bean;
    protected HashMap<String, BeanComponent> beanComponents = new HashMap();

    public void setBean(Object bean) {
        this.bean = bean;
    }

    protected void addBeanComponent(BeanComponent component, String propertyName) {
        beanComponents.put(propertyName, component);
    }

    protected void fireBeanUpdated() {
        for(BeanComponent comp : beanComponents.values()) {
            comp.beanUpdated();
        }
    }

    public Object getPropertyValue(String propertyName) {
        return null;
    }

}
