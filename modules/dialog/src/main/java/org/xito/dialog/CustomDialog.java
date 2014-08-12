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

import javax.swing.*;

/**
 * Implements an Dialog that maintains the look below. The Dialog can have the following optional layout
 * <pre>
 * ==============================================
 * window title
 * ==============================================
 * |  T I T L E                            icon |
 * |    subtitle                                |
 * ==============================================
 * |                                            |
 * |   Basic Message Text or HTML Message Text  |
 * |                                            |
 * |                                            |
 * |--------------------------------------------|
 * | | bottom panel |            <ok> <cancel>  |
 * ==============================================
 * </pre>
 * The Details section will display a Details Panel. The ok, cancel buttons can be changed
 * to Yes No etc.
 *
 * Also if the Title is not specified Then the Title Bar will not be displayed
 * Note: A Window title can also be specified.  See DialogDescriptor for more information
 *
 * @author  Deane Richan
 */
public class CustomDialog extends JDialog {
   
   protected DialogPanel mainPanel;
   protected JPanel titlePanel;
   protected JPanel buttonPanel;
   protected DialogDescriptor descriptor;
   protected int dialogResult = DialogManager.CANCEL;
   protected ConfirmDialogActionListener defaultActionListener;
   
   /**
    * Used by Sub-classes
    */
   protected CustomDialog(Frame owner) {
      super(owner);
   }
   
   /**
    * Used by Sub-classes
    */
   protected CustomDialog(Dialog owner) {
      super(owner);
   }
   
   /** Creates a new instance of CustomDialog */
   public CustomDialog(Frame owner, DialogDescriptor descriptor) {
      this(owner, descriptor, true);
   }
   
   /** Creates a new instance of CustomDialog */
   public CustomDialog(Frame owner, DialogDescriptor descriptor, boolean modal) {
      super(owner, descriptor.getWindowTitle(), modal);
      this.descriptor = descriptor;
      init();
   }
   
   /** Create a new instance of CustomDialog */
   public CustomDialog(Dialog owner, DialogDescriptor descriptor) {
      this(owner, descriptor, true);
   }
   
   /** Create a new instance of CustomDialog */
   public CustomDialog(Dialog owner, DialogDescriptor descriptor, boolean modal) {
      super(owner, descriptor.getWindowTitle(), modal);
      this.descriptor = descriptor;
      init();
   }
   
   /**
    * Return the Result the user clicked on
    */
   public int getResult() {
      
      return dialogResult;
   }
   
   /**
    * Build the Dialog
    */
   protected void init() {
      
      this.setTitle(descriptor.getWindowTitle());
            
      //add default action listeners for this descriptor if there aren't any yet
      if(descriptor.getActionListeners().isEmpty()) {
         defaultActionListener = new ConfirmDialogActionListener();
         descriptor.addActionListener(defaultActionListener);
      }
      
      mainPanel = new DialogPanel(descriptor);
      getContentPane().add(mainPanel);
      mainPanel.initDefaultButton();
      
      if(descriptor.getPack() || descriptor.getWidth()==0 || descriptor.getHeight()==0) {
         pack();
      }
      
      //set width
      if(descriptor.getWidth() > getWidth()) {
         setSize(descriptor.getWidth(), getHeight());
      }
      
      //set height
      if(descriptor.getHeight() > getHeight()) {
         setSize(getWidth(), descriptor.getHeight());
      }
      
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      if(getWidth()>screenSize.width) setSize(screenSize.width, getHeight());
      if(getHeight()>screenSize.height) setSize(getWidth(), screenSize.height);      
      
      centerLocation();
      
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      setResizable(descriptor.getResizable());
            
      addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent e) {
            descriptor.removeActionListener(defaultActionListener);
         }
      });
   }
   
   protected void centerLocation() {
      
      DialogManager.centerWindowOnParent(this);
   }
   
   
   public Dimension getPreferredSize() {
      Dimension ps = super.getPreferredSize();
      /*
      if(ps.width < 100 || ps.height < 100) {
         ps.width = 350;
         ps.height = 300;
      }
       */ 

      return ps;
   }
   
   /**
    * Set Titles
    */
   public void setTitles(String title, String subtitle) {
      if(mainPanel != null) {
         mainPanel.setTitles(title, subtitle);
      }
   }
   
   /**
    * 
   private void showException() {
      final JDialog expDialog = new JDialog((JFrame)null, "Exception Details", true);
      
      //Label
      JLabel lbl = new JLabel();
      lbl.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      if(exception instanceof Error) {
         lbl.setText("Error: "+exception.getClass().getName());
      } else {
         lbl.setText("Exception: "+exception.getClass().getName());
      }
      expDialog.getContentPane().add(lbl, BorderLayout.NORTH);
      
      //Stack Traces
      JTextArea traceArea = new JTextArea();
      StringBuffer buf = new StringBuffer();
      fillStackTrace(buf, exception);
      traceArea.setText(buf.toString());
      expDialog.getContentPane().add(new JScrollPane(traceArea));
      
      JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      btnPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      JButton closeBtn = new JButton("Close");
      closeBtn.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent evt) {
            expDialog.dispose();
         }
      });
      btnPanel.add(closeBtn);
      expDialog.getContentPane().add(btnPanel, BorderLayout.SOUTH);
      
      //Setup Dialog Size
      expDialog.setSize(500, 300);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      expDialog.setLocation((screenSize.width /2) - (getWidth()/2), (screenSize.height /2) - (getHeight()/2));
      expDialog.setResizable(true);
      expDialog.show();
   }
   
   private void fillStackTrace(StringBuffer buf, Throwable exp) {
      
      StackTraceElement traces[] = exp.getStackTrace();
      for(int i=0;i<traces.length;i++) {
         buf.append(traces[i].toString());
         buf.append("\n");
      }
      
      buf.append("\n");
      Throwable cause = exp.getCause();
      if(cause != null) {
         buf.append("Caused By:\n");
         fillStackTrace(buf, cause);
      }
   }
   
   
   /**
    * Class used as actionListener for modal Dialogs
    */
   private class ConfirmDialogActionListener implements ActionListener {
            
      public void actionPerformed(ActionEvent pEvent) {
         
         //Check to see if the Message Panel is Valid
         //If not Valid then the org.xito will not close
         //it is up to the hasValidData method to display information
         //to the user that lets them know the content is not valid
         JPanel p = CustomDialog.this.descriptor.getCustomPanel();
         int type = -1;
         Integer value = (Integer)((JComponent)pEvent.getSource()).getClientProperty(DialogManager.RESULT_KEY);
         if(value != null) {
            type = value.intValue();
         }
         
         if(p != null && p instanceof Validatable && type != DialogManager.CANCEL) {
            if(((Validatable)p).hasValidData() == false) {
               return;
            }
         }
         
         CustomDialog.this.dialogResult = type;
         if(CustomDialog.this.descriptor.hideOnClose()) {
            CustomDialog.this.setVisible(false);
         }
         else {
            CustomDialog.this.dispose();
         }
      }
   }
   
}
