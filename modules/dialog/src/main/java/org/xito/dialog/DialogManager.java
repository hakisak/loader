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
import java.security.*;
import javax.swing.*;

/**
 * Provides a Facade to create Simple Dialogs. The DialogManager can be used to display a message
 * to the user or ask the user a confirmation question. Standard Message Dialogs are used by using the 
 * showMessage, showError, showConfirm ect.
 *
 * @author Deane Richan
 */
public class DialogManager {
   
   /** Client Property used to contain the org.xito.dialog result. */
   public static final String RESULT_KEY = "org.xito.dialog.result";
      
   public static final int NONE = 0;
   public static final int OK = 1;
   public static final int CANCEL = 2;
   public static final int OK_CANCEL = 3;
   public static final int YES = 4;
   public static final int NO = 5;
   public static final int YES_NO = 6;
   public static final int YES_NO_CANCEL = 7;
   
   public static final int ERROR_MSG = 100;
   public static final int WARNING_MSG = 101;
   public static final int INFO_MSG = 102;
   public static final int BASIC_MSG = 103;

   /**
    * @deprecated use getErrorIcon()
    */
   @Deprecated
   public static ImageIcon ERROR_ICON;

   /**
    * @deprecated use getInfoIcon()
    */
   @Deprecated
   public static ImageIcon INFO_ICON;

   /**
    * @deprecated use getWarningIcon()
    */
   @Deprecated
   public static ImageIcon WARNING_ICON;

   //Initialize default Icons
   static {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            ERROR_ICON = new ImageIcon(DialogManager.class.getResource("/org/xito/dialog/images/error-32.png"));
            INFO_ICON = new ImageIcon(DialogManager.class.getResource("/org/xito/dialog/images/information-32.png"));
            WARNING_ICON = new ImageIcon(DialogManager.class.getResource("/org/xito/dialog/images/warning-32.png"));
            return null;
         }
      });
   }

   /**
    * Get the Error Icon
    * @return
    */
   public static ImageIcon getErrorIcon() {
      return ERROR_ICON;
   }

   /**
    * Get the Warning Icon
    * @return
    */
   public static ImageIcon getWarningIcon() {
      return WARNING_ICON;
   }

   /**
    * Get the Info Icon
    * @return
    */
   public static ImageIcon getInfoIcon() {
      return INFO_ICON;
   }
   
   public static final String RESOURCES_NOT_FOUND_MSG = "Unable to load Resources for the default Locale. \n\n" +
           "Resources contain the error messages for the specific locale and are required for proper application execution.";
   
   /**
    * Center a Window in its Parent. If parent of this window is null then 
    * it will center on the screen
    * @param w
    */
   public static void centerWindowOnParent(Window w) {
      
      Point centerPoint = null;
      Window owner = w.getOwner();
      if(owner == null || !owner.isVisible()) {
         //center on screen
         centerWindowOnScreen(w);
         return;
      }
      else {
         Point loc = owner.getLocation();
         Dimension size = owner.getSize();
         
         int x = loc.x + (size.width / 2);
         int y = loc.y + (size.height / 2);
         centerPoint = new Point(x, y);
      }

      //center on ppoint
      centerWindowOnPoint(w, centerPoint);
   }
   
   /**
    * Center a Window on the Screen.
    * it will center on the screen
    * @param w
    */
   public static void centerWindowOnScreen(Window w) {
      
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Point centerPoint = new Point((screenSize.width /2), (screenSize.height /2));
      
      //center on ppoint
      centerWindowOnPoint(w, centerPoint);
   }
   
   /**
    * Center a window on a scren point location
    * @param w window to center
    * @param centerPoint point to center window
    */
   public static void centerWindowOnPoint(Window w, Point centerPoint) {
      //center on ppoint
      if(centerPoint != null) {
         w.setLocation(centerPoint.x - (w.getWidth()/2), centerPoint.y - (w.getHeight()/2));
      }
   }
   
   /**
    * Show a Message to the User
    * @param owner frame of dialog.dialog
    * @param title of the dialog.dialog that will show in the Window Title
    * @param message to display (can be in HTML)
    */
   public static void showMessage(Frame owner, String title, String message) {
      showMessage(owner, null, title, message);
   }
   
   /**
    * Show a Message to the User
    * @param owner of dialog.dialog
    * @param icon to show with the Message
    * @param title of the dialog.dialog
    * @param message to display can be HTML
    */
   public static void showMessage(Frame owner, Icon icon, String title, String message) {
   
      DialogDescriptor desc = new DialogDescriptor();
      desc.setIcon(icon);
      desc.setWindowTitle(title);
      desc.setMessage(message);
            
      AlertDialog dialog = new AlertDialog(owner, desc);
      dialog.show();
   }
   
   /**
    * Show a Message to the User
    * @param owner of dialog
    * @param icon to show with the Message
    * @param title of the dialog
    * @param message to display can be HTML
    */
   public static void showMessage(Dialog owner, Icon icon, String title, String message) {
   
      DialogDescriptor desc = new DialogDescriptor();
      desc.setIcon(icon);
      desc.setWindowTitle(title);
      desc.setMessage(message);
            
      AlertDialog dialog = new AlertDialog(owner, desc);
      dialog.setVisible(true);
   }
   
   /**
    * Show an Error to the User
    * @param owner that owns this Dialog
    * @param title of the dialog
    * @param message error message to show
    * @param exp that caused this Error or null
    */
   public static void showError(Frame owner, String title, String message, Throwable exp) {
      showError(owner, title, message, DialogManager.OK, exp);
   }
   
   /**
    * Show an Error to the User
    * @param owner frame that owns this Dialog
    * @param title title of the dialog
    * @param message to show
    * @param type OK, OK_CANCEL, YES, YES_NO, YES_NO_CANCEL
    * @param exp that caused this Error or null
    * @return result OK, CANCEL, YES, NO
    */
   public static int showError(Frame owner, String title, String message, int type, Throwable exp) {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setWindowTitle(title);
      desc.setMessage(message);
      desc.setMessageType(ERROR_MSG);
      desc.setType(type);
      desc.setException(exp);
      if(exp != null) {
         desc.setResizable(true);
      }
      
      Toolkit.getDefaultToolkit().beep();
      AlertDialog dialog = new AlertDialog(owner, desc);
      dialog.setVisible(true);
                  
      return dialog.getResult();
   }
   
   /**
    * Show an Error to the User
    * @param owner Frame that owns this Dialog
    * @param title title of the dialog
    * @param message to show
    * @param type OK, OK_CANCEL, YES, YES_NO, YES_NO_CANCEL
    * @param exp that caused this Error or null
    * @return result OK, CANCEL, YES, NO
    */
   public static int showError(Dialog owner, String title, String message, int type, Throwable exp) {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setWindowTitle(title);
      desc.setMessage(message);
      desc.setMessageType(ERROR_MSG);
      desc.setType(type);
      desc.setException(exp);
      if(exp != null) {
         desc.setResizable(true);
      }
      
      Toolkit.getDefaultToolkit().beep();
      AlertDialog dialog = new AlertDialog(owner, desc);
      dialog.setVisible(true);
                  
      return dialog.getResult();
   }
   
   /**
    * Show an Info Message to the User
    * @param owner Frame that owns this Dialog
    * @param title title of the dialog
    * @param message to show
    */
   public static void showInfoMessage(Frame owner, String title, String message) {
      showInfoMessage(owner, title, message, DialogManager.OK);
   }
   
   /**
    * Show an Info Message to the User
    * @param owner Frame that owns this Dialog
    * @param title title of the dialog
    * @param message to show
    * @param type of OK, OK_CANCEL, YES_NO, YES_NO_CANCEL
    * @return result OK, CANCEL, YES, NO
    */
   public static int showInfoMessage(Frame owner, String title, String message, int type) {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setWindowTitle(title);
      desc.setMessage(message);
      desc.setMessageType(INFO_MSG);
      desc.setType(type);
            
      AlertDialog dialog = new AlertDialog(owner, desc);
      dialog.show();
      
      return dialog.getResult();
   }
   
   /**
    * Show an Info Message to the User
    * @param owner Frame that owns this Dialog
    * @param title title of the dialog
    * @param message to show
    * @param type of OK, OK_CANCEL, YES_NO, YES_NO_CANCEL
    * @return result OK, CANCEL, YES, NO
    */
   public static int showInfoMessage(Dialog owner, String title, String message, int type) {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setWindowTitle(title);
      desc.setMessage(message);
      desc.setMessageType(INFO_MSG);
      desc.setType(type);
            
      AlertDialog dialog = new AlertDialog(owner, desc);
      dialog.show();
      
      return dialog.getResult();
   }
   
   
   /**
    * Show an Warning Message to the User
    * @param owner Frame that owns this Dialog
    * @param title title of the dialog
    * @param message to show
    */
   public static void showWarningMessage(Frame owner, String title, String message) {
      showWarningMessage(owner, title, message, DialogManager.OK);
   }
   
   /**
    * Show an Warning Message to the User
    * @param owner Frame that owns this Dialog
    * @param title title of the dialog
    * @param message to show
    * @param type of OK, OK_CANCEL, YES_NO, YES_NO_CANCEL
    * @return result OK, CANCEL, YES, NO
    */
   public static int showWarningMessage(Frame owner, String title, String message, int type) {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setWindowTitle(title);
      desc.setMessage(message);
      desc.setMessageType(WARNING_MSG);
      desc.setType(type);
            
      AlertDialog dialog = new AlertDialog(owner, desc);
      dialog.show();
      return dialog.getResult();
   }
   
   /**
    * Show an Warning Message to the User
    * @param owner Frame that owns this Dialog
    * @param title title of the dialog
    * @param message to show
    * @param type of OK, OK_CANCEL, YES_NO, YES_NO_CANCEL
    * @return result OK, CANCEL, YES, NO
    */
   public static int showWarningMessage(Dialog owner, String title, String message, int type) {
      
      DialogDescriptor desc = new DialogDescriptor();
      desc.setWindowTitle(title);
      desc.setMessage(message);
      desc.setMessageType(WARNING_MSG);
      desc.setType(type);
            
      AlertDialog dialog = new AlertDialog(owner, desc);
      dialog.show();
      return dialog.getResult();
   }
   
   /**
    * Ask the User for a Confirmation
    * @param owner
    * @param title of the dialog
    * @param message
    * @param type OK, OK_CANCEL, YES_NO, YES_NO_CANCEL
    * @return result of the selection OK, CANCEL, YES, NO
    */
   public static int showConfirm(Frame owner, String title, String message, int type) {
      return showConfirm(owner, null, title, message, type);
   }
   
   /**
    * Ask the User for a Confirmation
    * @param owner
    * @param icon to show with the Message
    * @param title of the dialog
    * @param message
    * @param type OK, OK_CANCEL, YES_NO, YES_NO_CANCEL
    * @return the result of the selection OK, CANCEL, YES, NO
    */
   public static int showConfirm(Dialog owner, Icon icon, String title, String message, int type) {
                  
      DialogDescriptor desc = new DialogDescriptor();
      desc.setIcon(icon);
      desc.setWindowTitle(title);
      desc.setMessage(message);
      desc.setType(type);
      
      AlertDialog dialog = new AlertDialog(owner, desc);
      dialog.show();
      return dialog.getResult();
   }
   
   /**
    * Ask the User for a Confirmation
    * @param owner
    * @param icon to show with the Message
    * @param title of the dialog
    * @param message
    * @param type OK, OK_CANCEL, YES_NO, YES_NO_CANCEL
    * @return the result of the selection OK, CANCEL, YES, NO
    */
   public static int showConfirm(Frame owner, Icon icon, String title, String message, int type) {
                  
      DialogDescriptor desc = new DialogDescriptor();
      desc.setIcon(icon);
      desc.setWindowTitle(title);
      desc.setMessage(message);
      desc.setType(type);
      
      AlertDialog dialog = new AlertDialog(owner, desc);
      dialog.setVisible(true);
      return dialog.getResult();
   }
   
   /**
    * Show a dialog described by the Dialog Descriptor. See AlertDescriptor for more
    * information.
    * @param desc of dialog
    * @return result
    */
   public static int showDialog(DialogDescriptor desc) {
      AlertDialog dialog = new AlertDialog((Frame)null, desc);
      dialog.setVisible(true);
      
      return dialog.getResult();
   }
   
   /**
    * Show a dialog described by the Dialog Descriptor. See AlertDescriptor for more
    * information.
    * @param owner Frame for dialog
    * @param desc of dialog
    * @return result
    */
   public static int showDialog(Frame owner, DialogDescriptor desc) {
      AlertDialog dialog = new AlertDialog(owner, desc);
      dialog.setVisible(true);
      
      return dialog.getResult();
   }
   
   /**
    * Show a dialog described by the Dialog Descriptor. See AlertDescriptor for more
    * information.
    * @param owner Dialog
    * @param desc of dialog
    * @return result
    */
   public static int showDialog(Dialog owner, DialogDescriptor desc) {
      AlertDialog dialog = new AlertDialog(owner, desc);
      dialog.show();
      
      return dialog.getResult();
   }
   
   /**
    * Action Listener for Dialog
    */
   private class MyActionListener implements ActionListener {
      
      JDialog dialog;
      JComponent detailsComp;
      boolean hideDetails = true;
      Dimension hideSize;
      Dimension openSize;
      
      public MyActionListener(JDialog pDialog, JComponent pDetailsComp) {
         dialog = pDialog;
         detailsComp = pDetailsComp;
         hideSize = dialog.getSize();
      }
      
      public void actionPerformed(ActionEvent pEvent) {
         
         JComponent _comp = (JComponent)pEvent.getSource();
         if(_comp.getClientProperty("OK") != null) {
            //Ok Button
            dialog.dispose();
         } else {
            //Show Details
            hideDetails = !hideDetails;
            if(hideDetails) {
               detailsComp.setVisible(false);
               int h = dialog.getHeight();
               openSize = dialog.getSize();
               dialog.setSize(hideSize);
               dialog.setResizable(false);
            } else {
               detailsComp.setVisible(true);
               int h = dialog.getHeight();
               if(openSize == null) {
                  openSize = new Dimension(hideSize.width * 2, hideSize.height *2);
               }
               
               dialog.setSize(openSize);
               dialog.setResizable(true);
            }
         }
      }
   }
   
   /*********************
    * MyWorkInProgressAlert
    ***************************/
   class MyWorkInProgressAlert {
      
      JFrame frame;
      JLabel messageLbl;
      
      /**
       * Create Alert
       */
      public MyWorkInProgressAlert(String title, String message, boolean closeable) {
         frame.setTitle(title);
         //frame.setCloseable(pCloseable);
         frame.setResizable(false);
         init(message);
      }
      
      /**
       * Build the Alert
       */
      private void init(String pMessage) {
         JPanel _main = new JPanel(new BorderLayout());
         _main.setBackground(Color.white);
         
         //Clock
      /*
      ImageIcon _clockImage = new ImageIcon(Shell.class.getResource("org.xito.launcher.images/clock.gif"));
      JLabel _clock = new JLabel(_clockImage);
      _main.add(_clock, BorderLayout.WEST);
       */
         
         //Message
         messageLbl = new JLabel(pMessage);
         _main.add(messageLbl);
         
         //Add Close Button
      /*
      if(frame.isCloseable()) {
        JPanel _bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        _bottom.setOpaque(false);
        JButton _close = new JButton("Close");
        _close.addActionListener(new ActionListener() {
                                   public void actionPerformed(ActionEvent pEvent) {
                                     frame.dispose();
                                   }
                                 });
        _bottom.add(_close);
        _main.add(_bottom, BorderLayout.SOUTH);
      }
       */
         
         //Show Frame
         frame.add(_main);
         Dimension _size = _main.getPreferredSize();
         _size.height+=50;
         _size.width+=50;
         frame.setSize(_size);
         
         frame.setVisible(true);
         frame.toFront();
      }
      
      /**
       * Update the Title of the Alert
       * @param pTitle
       */
      public void setTitle(String pTitle) {
         frame.setTitle(pTitle);
      }
      
      /**
       * Update the Message of the Alert
       * @param pMessage
       */
      public void setMessage(String pMessage) {
         messageLbl.setText(pMessage);
      }
      
      /**
       * Dispose of the Alert
       */
      public void dispose() {
         frame.dispose();
      }
   }
}
