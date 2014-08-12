/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xito.dazzle.bean;

/**
 *
 * @author deane
 */
public interface BeanComponent {

    /**
     * Called when the Bean Controller this Component is using
     * has updated its Bean
     */
    public void beanUpdated();
}
