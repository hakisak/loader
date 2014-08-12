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

package org.xito.dazzle.utilities;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.UIManager;

/**
 * This class is used to install action listeners for specific Apple application events.
 * This can be used to handle quit events, About events, and Preference Events.
 * @author deane
 */
public class MacApplicationUtilities {

    private static Class macAdapterClass;
    private static Class macAppListenerInterface;
    private static Class macAppEventClass;
    private static Object macAdapterInstance;
    private static Object macAppListenerInstance;

    private static ActionListener quitListener;
    private static ActionListener prefListener;
    private static ActionListener aboutListener;

    /**
     * Return true if the mac os is least 10.5 or greater
     * @return
     */
    public static boolean isAtLeastMacOSVersionX5() {
       if(!isRunningOnMac()) return false;
       
       String osVersion = System.getProperty("os.version");
       StringTokenizer st = new StringTokenizer(osVersion, ".");
       ArrayList<String> verNums = new ArrayList<String>();
       while(st.hasMoreTokens()) {
          verNums.add(st.nextToken());
       }
       
       //we want the second version number
       if(verNums.size() > 2) {
          return Integer.parseInt(verNums.get(1)) >= 5;
       }
       else {
          //shouldn't really happen
          return false;
       }
    }
    
    /**
     * Return true if the app is running on a Mac
     * @return
     */
    public static boolean isRunningOnMac() {

        String os = System.getProperty("os.name");

        if(os.equals("Mac OS X")) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Install the Aqua Look and Feel and set the apple.laf.useScreenMenuBar property
     */
    public static void initAquaLookAndFeel() {
        try {
            if(isRunningOnMac()) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.setProperty("apple.laf.useScreenMenuBar", "true");
            }
        }
        catch(Exception exp) {
            exp.printStackTrace();
        }
    }

    /**
     * Insatll an ActionListener to handle Mac Applicatino Quit Action
     * @param quitActionListener
     */
    public static void installQuitActionHandler(ActionListener quitActionListener) {

        if(!isRunningOnMac()) return;

        initMacApplicationInstance();
        initMacApplicationListener();

        quitListener = quitActionListener;
    }

    /**
     * Install an ActionListener to handle Mac Application Preference action
     * @param prefActionListener
     */
    public static void installPreferenceActionHandler(ActionListener prefActionListener) {

        if(!isRunningOnMac()) return;

        initMacApplicationInstance();
        initMacApplicationListener();

        prefListener = prefActionListener;

        try {

            //
            // macAdapterInstance.addPreferencesMenuItem();
            //
            Method addPrefsMethod = macAdapterClass.getMethod("addPreferencesMenuItem");
            addPrefsMethod.invoke(macAdapterInstance);

            //
            // macAdapterInstance.setEnabledPreferencesMenu(true);
            //
            Method enablePrefsMethod = macAdapterClass.getMethod(
                    "setEnabledPreferencesMenu",
                    new Class[]{Boolean.TYPE});
            enablePrefsMethod.invoke(macAdapterInstance,
                    new Object[]{Boolean.TRUE});

        }
        catch(Exception exp) {

        }
    }

    /**
     * Install an ActionListener to handle Mac Application About action
     * @param aboutActionListener
     */
    public static void installAboutActionHandler(ActionListener aboutActionListener) {

        if(!isRunningOnMac()) return;

        initMacApplicationInstance();
        initMacApplicationListener();

        aboutListener = aboutActionListener;

        try {
            //
            // macAdapterInstance.setEnabledAboutMenu(true);
            //
            Method enableAboutMethod = macAdapterClass.getMethod("setEnabledAboutMenu",
                    new Class[]{Boolean.TYPE});

            enableAboutMethod.invoke(macAdapterInstance,
                    new Object[]{Boolean.TRUE});

            //
            // macAdapterInstance.addAboutMenuItem();
            //
            Method addAboutMethod = macAdapterClass.getMethod("addAboutMenuItem");
            addAboutMethod.invoke(macAdapterInstance);
            
        }
        catch(Exception exp) {
            exp.printStackTrace();
        }

    }

    /**
     * Installer a listener for mac applications
     */
    private static void initMacApplicationListener() {

        if(macAppListenerInstance != null) return;

        try{
            //
            // macAdapterInstance.addApplicationListener(
            //         new MacApplicationListener(editPrefAction));
            //
            Method addAppListenerMethod = macAdapterClass.getMethod(
                    "addApplicationListener",
                    new Class[]{macAppListenerInterface});

            macAppListenerInstance = Proxy.newProxyInstance(
                    macAppListenerInterface.getClassLoader(),
                    new Class[]{macAppListenerInterface},
                    new MacApplicationListenerProxyInvocationHandler(macAppEventClass));

            addAppListenerMethod.invoke(macAdapterInstance, macAppListenerInstance);
        }
        catch(Exception exp) {
            exp.printStackTrace();
        }

    }

   /**
    * Set Dock Icon Badge Text
    * @param text to place on Dock Icon
    */
    public static void setDockIconBadge(String text) {

        if(!isRunningOnMac()) return;

        initMacApplicationInstance();
        initMacApplicationListener();

        try {
            //
            // macAdapterInstance.setDockIconBadge(text);
            //
            Method setDockIconBadge = macAdapterClass.getMethod("setDockIconBadge",
                    new Class[]{String.class});

            setDockIconBadge.invoke(macAdapterInstance, text);
        }
        catch(Exception exp) {
            exp.printStackTrace();
        }
    }

   /**
    * Set Dock Icon Image
    * @param image for Dock Icon
    */
    public static void setDockIconImage(Image image) {

       if(!isRunningOnMac()) return;

        initMacApplicationInstance();
        initMacApplicationListener();

        try {
            //
            // macAdapterInstance.setDockIconImage(image);
            //
            Method setDockIconImage = macAdapterClass.getMethod("setDockIconImage",
                    new Class[]{Image.class});

            setDockIconImage.invoke(macAdapterInstance, image);
        }
        catch(Exception exp) {
            exp.printStackTrace();
        }
    }

   /**
    * Set a Menu to be used on the Dock Icon
    * @param dockMenu menu to use on Dock Icon
    */
    public static void setDockMenu(PopupMenu dockMenu) {

       if(!isRunningOnMac()) return;

        initMacApplicationInstance();
        initMacApplicationListener();

        try {
            //
            // macAdapterInstance.setDockMenu(dockMenu);
            //
            Method setDockMenu = macAdapterClass.getMethod("setDockMenu",
                    new Class[]{PopupMenu.class});

            setDockMenu.invoke(macAdapterInstance, dockMenu);
        }
        catch(Exception exp) {
            exp.printStackTrace();
        }
    }

    /**
     * This returns the application instance of type com.apple.eawt.Application
     * if running on a mac or null otherwise
     * @return
     */
    private static void initMacApplicationInstance() {

        if(!isRunningOnMac()) return;

        //if we already have initialized these then just return
        if(macAdapterClass != null && macAppListenerInterface !=null &&
            macAppEventClass != null && macAdapterInstance != null) {
            return;
        }


        //Get the classes
        try {
            macAdapterClass = Class.forName("com.apple.eawt.Application");
            macAppListenerInterface = Class.forName("com.apple.eawt.ApplicationListener");
            macAppEventClass = Class.forName("com.apple.eawt.ApplicationEvent");
        } catch (ClassNotFoundException classNotFound) {
            // One (or more) of the Apple extension classes is missing.
            macAdapterClass = null;
            macAppListenerInterface = null;
            macAppEventClass = null;
        }

        try {

            //
            // Object macAdapterInstance = Application.getApplication();
            //
            Method getSingletonMethod = macAdapterClass.getMethod("getApplication");
            macAdapterInstance = getSingletonMethod.invoke(null);

        } catch (Exception dynEx) {
            dynEx.printStackTrace();
        }
    }

    /**
     * Disables Preferences menu item on Mac OS X.
     */
    public static void disablePreferenceMenuItem() {

        if(!isRunningOnMac()) return;

        initMacApplicationInstance();

        try {

            //
            // macAdapterInstance.setEnabledPreferencesMenu(true);
            //
            Method enablePrefsMethod = macAdapterClass.getMethod(
                    "setEnabledPreferencesMenu",
                    new Class[]{Boolean.TYPE});

            enablePrefsMethod.invoke(macAdapterInstance,
                    new Object[]{Boolean.FALSE});

        }
        catch (Exception dynEx) {
            dynEx.printStackTrace();
        }
    }

    /**
     * Enables Preferences menu item on Mac OS X.
     */
    public static void enablePreferenceMenuItem() {

        if(!isRunningOnMac()) return;

        initMacApplicationInstance();

        try {

            //
            // macAdapterInstance.setEnabledPreferencesMenu(true);
            //
            Method enablePrefsMethod = macAdapterClass.getMethod(
                    "setEnabledPreferencesMenu",
                    new Class[]{Boolean.TYPE});

            enablePrefsMethod.invoke(macAdapterInstance, new Object[]{Boolean.TRUE});

        }
        catch (Exception dynEx) {
            dynEx.printStackTrace();
        }
    }

    /**
     * Dynamic Proxy used to handle Apple Events
     */
    private static class MacApplicationListenerProxyInvocationHandler implements InvocationHandler {

        private Method setHandledMethod;
        
        public MacApplicationListenerProxyInvocationHandler(
                Class macAppEventClass) throws NoSuchMethodException {

            this.setHandledMethod = macAppEventClass.getMethod("setHandled", new Class[]{Boolean.TYPE});
        }

        // throws Throwable (allowed, but should not be taken advantage of)
        public Object invoke(Object proxy, Method method, Object[] args) {

            String methodName = method.getName();
            ActionEvent event = new ActionEvent(this, 0, methodName);

            if (methodName.equals("handleQuit")) {
                // fire the action
                if(quitListener != null) {
                    quitListener.actionPerformed(event);
                }
                else {
                    System.exit(0); //default to SystemExit
                }
            }
            else if(methodName.equals("handleAbout")) {
                // fire the action
                if(aboutListener != null) {
                    aboutListener.actionPerformed(event);
                }
            }
            else if(methodName.equals("handlePreferences")) {
                // fire the action
                if(prefListener != null) {
                    prefListener.actionPerformed(event);
                }
            }


            // Mark the ApplicationEvent as being handled.
            try {
                this.setHandledMethod.invoke(args[0],
                        new Object[]{Boolean.TRUE});
            } catch (Exception invokeEx) {
                invokeEx.printStackTrace();
            }

            return null; // All signatures in ApplicationListener return void.
        }
    }


}
