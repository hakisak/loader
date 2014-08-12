// Copyright 2007 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * Descriptor class used to describe how a Dialog Looks. The following 
 * format is used: <pre>
 * ==============================================
 * window title
 * ==============================================
 * |  T I T L E                            icon |
 * |    subtitle                                |
 * ==============================================
 * |                                            |
 * |   Basic Message Text or HTML Message Text  |
 * |           or Custom Panel                  |
 * |                                            |
 * |--------------------------------------------|
 * | | bottom panel |            <ok> <cancel>  |
 * ==============================================
 * </pre>
 *
 * @author  Deane Richan
 */
public class DialogDescriptor {
   
   private Throwable exception;
   private JPanel customPanel;
   private int width;
   private int height;
   private String windowTitle;
   private String title;
   private String subtitle;
   private Icon icon;
   private JComponent iconComp;
   private String message;
   private boolean resizable;
   private boolean pack = false;
   private boolean button_sep_flag = false;
   private boolean hide_on_close_flag = false;
   private Color gradiantColor;
   private float gradiantOffsetRatio = 0.5f;
   private JPanel bottomPanel;
   private ButtonType[] buttonTypes;
   private ArrayList actionListeners = new ArrayList();
   
   /* type of buttons to place on the org.xito */
   private int type = DialogManager.OK;
   
   /* message type either:    ERROR_MSG, WARNING_MSG, INFO_MSG, BASIC_MSG */
   private int messageType = DialogManager.BASIC_MSG;
  
   /**
    * Set the Packing. Set to true if you want the org.xito to be packed
    */
   public void setPack(boolean p) {
      pack = p;
   }
   
   /**
    * Get the Packing
    */
   public boolean getPack() {
      return pack;
   }
   
   /**
    * Return true if the Dialog should be Resizable
    */
   public boolean getResizable() {
      return resizable;
   }
   
   /**
    * Set to true if the Dialog should be Resizable
    */
   public void setResizable(boolean b) {
      resizable = b;
   }
      
   /**
    * Set to true if a Separator should be drawn between buttons and custom/message panel
    */
   public void setShowButtonSeparator(boolean b) {
      button_sep_flag = b;
   }
   
   /**
    * Return true if a Separator should be drawn between buttons and custom/message panel
    */
   public boolean getShowButtonSeparator() {
      return button_sep_flag;
   }
   
   /**
    * Get the Title that should show as the Window Title
    */
   public String getWindowTitle() {
      if(windowTitle == null) 
         return title;
      else
         return windowTitle;
   }
   
   /**
    * Set the Title that should show as the Window Title
    */
   public void setWindowTitle(String windowTitle) {
      this.windowTitle = windowTitle;
   }
   
   /**
    * Get the Title that should show in the Title Panel
    */
   public String getTitle() {
      return title;
   }
   
   /**
    * Set the Title that should show in the Title Panel
    */
   public void setTitle(String title) {
      this.title = title;
   }
   
   /**
    * Get the SubTitle that should show in the Title Panel
    */
   public String getSubtitle() {
      return subtitle;
   }
   
   /**
    * Set the SubTitle that should show in the Title Panel
    */
   public void setSubtitle(String subtitle) {
      this.subtitle = subtitle;
   }
   
   
   
   /**
    * Return the bottom panel used by this org.xito
    * @return the buttonPanel
    */
   public JPanel getBottomPanel() {
      return bottomPanel;
   }

   /**
    * Set a bottom panel that will appear at the bottom of the
    * org.xito. This can be used in conjuction with button_types or without
    * @param bottomPanel the bottomPanel to set
    */
   public void setBottomPanel(JPanel bottomPanel) {
      this.bottomPanel = bottomPanel;
   }

   /** 
    * Set to True to Hide the Dialog on Close rather then Dispose
    * The default is false which will dipose the org.xito on close
    */
   public void setHideOnClose(boolean b) {
      hide_on_close_flag = b;
   }
   
   /** 
    * Return true if the Dialog should Hide on Close rather then Dispose
    * The default is false which will dipose the org.xito on close
    */
   public boolean hideOnClose() {
      return hide_on_close_flag;
   }
   
   /**
    * Get the Icon for the Alert. 
    */
   public Icon getIcon() {
      return icon;
   }
   
   public JComponent getIconComp() {
      return iconComp;
   }
   
   /**
    * Set the Icon for the Alert. The Icon will display in the Title Panel if a Title is
    * set or in the Message Area if no Title is set.
    */
   public void setIcon(Icon icon) {
      this.icon = icon;
   }
   
   /**
    * Set the Icon Component for the Alert
    */
   public void setIconComp(JComponent iconComp) {
      this.iconComp = iconComp;
   }
   
   /**
    * Get the Message that is to be displayed
    */
   public String getMessage() {
      return message;
   }
   
   /**
    * Set the Message that is to be displayed
    */
   public void setMessage(String message) {
      this.message = message;
   }
   
   /**
    * Get the Type of Buttons to Display: OK, OK_CANCEL, YES_NO, YES_NO_CANCEL
    */
   public int getType() {
      return type;
   }
   
   /**
    * Set the Type of Buttons to Display: OK, OK_CANCEL, YES_NO, YES_NO_CANCEL
    */
   public void setType(int type) {
      this.type = type;
   }
   
   /**
    * Get the Message Type ERROR, INFO, WARNING, BASIC
    */
   public int getMessageType() {
      return messageType;
   }
   
   /**
    * Set the Message Type ERROR, INFO, WARNING, BASIC
    */
   public void setMessageType(int messageType) {
      this.messageType = messageType;
   }
   
   /**
    * Get the Width of the Dialog
    */
   public int getWidth() {
      return width;
   }
   
   /**
    * Set the Width of the Dialog
    */
   public void setWidth(int width) {
      this.width = width;
   }
   
   /**
    * Get the Height of the Dialog
    */
   public int getHeight() {
      return height;
   }
   
   /**
    * Set the Height of the Dialog
    */
   public void setHeight(int height) {
      this.height = height;
   }
   
   /**
    * Get the Gradiant Color to use in the Title Panel
    */
   public Color getGradiantColor() {
      return gradiantColor;
   }
   
   /**
    * Set the Gradiant Color to use in the Title Panel
    */
   public void setGradiantColor(Color gradiantColor) {
      this.gradiantColor = gradiantColor;
   }
   
   /**
    * Get the Gradiant OffSet ratio. 0.5f ie Half will cause the Gradiant to cover
    * half the Title Panel. 0.25f will cause the Gradiant to cover 1/4 the Title Panel etc.
    */
   public float getGradiantOffsetRatio() {
      return gradiantOffsetRatio;
   }
   
   /**
    * Set the Gradiant OffSet ratio. 0.5f ie Half will cause the Gradiant to cover
    * half the Title Panel. 0.25f will cause the Gradiant to cover 1/4 the Title Panel etc.
    */
   public void setGradiantOffsetRatio(float gradiantOffsetRatio) {
      this.gradiantOffsetRatio = gradiantOffsetRatio;
   }

   /**
    * Get the Custom Panel that should be displayed in the Center Area.
    */
   public JPanel getCustomPanel() {
      return customPanel;
   }

   /**
    * Set the Custom Panel that should be displayed in the Center Area.
    */
   public void setCustomPanel(JPanel customPanel) {
      this.customPanel = customPanel;
   }

   /**
    * Get the Exception that should be shown
    */
   public Throwable getException() {
      return exception;
   }

   /**
    * Set the Exception that should be shown. Will not be shown if a MessagePanel as already been
    * specified.
    */
   public void setException(Throwable exception) {
      this.exception = exception;
   }

   public ButtonType[] getButtonTypes() {
      return buttonTypes;
   }

   public void setButtonTypes(ButtonType[] buttonTypes) {
      this.buttonTypes = buttonTypes;
   }
   
   /**
    * Set the ActionListener that listens for result button actions
    */
   public void addActionListener(ActionListener listener) {
      
      actionListeners.add(listener);
   }
   
   /**
    * Remove an ActionListener
    */
   public void removeActionListener(ActionListener listener) {
      
      actionListeners.remove(listener);
   }
   
   /**
    * Get the ActionListeners that listens for result button actions
    */
   public Collection getActionListeners() {
      
      return actionListeners;
   }
   
}

