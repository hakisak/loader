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

package org.xito.dcf.property;

import java.lang.reflect.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.beans.*;

/**
 *
 * @author  drichan
 * @version
 */
public class PropertySheetDialog extends JDialog implements ActionListener {
   protected Object[] beans;
   protected Object[] beansCopy;
   protected BeanInfo[] beansInfo;
   protected boolean useOptimisticUpdate = true;
   
   private HashMap propertySheets = new HashMap();
   private JPanel bottomPanel;
   private JButton okBtn;
   private JButton cancelBtn;
   private JTabbedPane tabs;
   private int w = 300;
   private int h = 400;
   
   public static void main(String args[]) {
      
      JButton button = new JButton("Test");
      PropertySheetDialog dialog = new PropertySheetDialog(button, "Test");
      dialog.show();
      
   }
   
   /** Creates new PropertySheetDialog */
   public PropertySheetDialog(Object pBean, String pTitle) {
      this(new Object[]
      {pBean},pTitle);
   }
   
   /** Creates new PropertySheetDialog */
   public PropertySheetDialog(Object[] pBeans, String pTitle) {
      super(new JFrame(), true);
      super.setTitle(pTitle);
      beans = pBeans;
      init();
   }
   
   /** Creates new PropertySheetDialog */
   public PropertySheetDialog(Object[] pBeans, String pTitle, boolean optimistic) {
      super(new JFrame(), true);
      super.setTitle(pTitle);
      beans = pBeans;
      useOptimisticUpdate = optimistic;
      init();
   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    */
   private void init() {
      //First Make Clones of Beans
      makeCopies();
      processBeanInfos();
      
      //Setup Dialog
      setModal(true);
      //setResizable(false);
      setSize(w,h);
      Dimension _size = Toolkit.getDefaultToolkit().getScreenSize();
      setLocation((_size.width/2)-(w/2), (_size.height/2)-(h/2));
      
      bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      okBtn = new JButton("Ok");
      okBtn.addActionListener(this);
      cancelBtn = new JButton("Cancel");
      cancelBtn.addActionListener(this);
      tabs = new JTabbedPane();
      
      //Add Window Listener
      addWindowListener(new java.awt.event.WindowAdapter() {
         public void windowClosing(java.awt.event.WindowEvent evt) {
            closeDialog(evt);
         }
      });
      
      bottomPanel.add(okBtn);
      bottomPanel.add(cancelBtn);
      
      getContentPane().add(bottomPanel, BorderLayout.SOUTH);
      
      //Setup Bean Properties Tabs
      for(int i=0;i<beansInfo.length;i++) {
         Object _bean = beansCopy[i];
         if(_bean != null) {
            //PropertySheet2 _sheet = new PropertySheet2(_bean);
            PropertySheet _sheet = new PropertySheet(_bean);
            propertySheets.put(_bean, _sheet);
            tabs.addTab(beansInfo[i].getBeanDescriptor().getDisplayName(), _sheet);
         }
      }
      getContentPane().add(tabs, BorderLayout.CENTER);
   }
   
   //Makes Clones of Beans
   private void makeCopies() {
      //If we are using optimistic update then just set the BeansCopy to the same
      //as the beans
      if(useOptimisticUpdate) {
         beansCopy = beans;
         return;
      }
      
      //Try to Clone each Bean
      beansCopy = new Object[beans.length];
      for(int i=0;i<beans.length;i++) {
         if(beans[i] instanceof Cloneable) {
            //Get copy using clone method
            try {
               beansCopy[i] = cloneBean(beans[i]);
            }
            catch(CloneNotSupportedException _exp) {
               //try copyBean
               beansCopy[i] = copyBean(beans[i]);
            }
         }
         else if(beans[i] instanceof Serializable) {
            //Get copy using serialized copy
            beansCopy[i] = copyBean(beans[i]);
         }
         else {
            //Just set copy to null
            beansCopy[i] = null;
         }
      }
   }
   
   /**
    * Clone a Bean using Reflection
    */
   private Object cloneBean(Object source) throws CloneNotSupportedException {
      if(source instanceof Cloneable) {
         //Get the Method
         try {
            Class _cls = source.getClass();
            Method _method = _cls.getMethod("clone", null);
            Object _copy = _method.invoke(source, null);
            return _copy;
         }
         catch(Exception _exp) {
            //this is bad just throw Clone Not Supported
         }
      }
      
      //Throw an Exception
      throw new CloneNotSupportedException();
   }
   
   /**
    * Copy a Bean using Serialization
    */
   private Object copyBean(Object source) {
      if((source instanceof Serializable)==false) return null;
      
      try {
         //write object
         ByteArrayOutputStream _out = new ByteArrayOutputStream();
         ObjectOutputStream _objOut = new ObjectOutputStream(_out);
         _objOut.writeObject(source);
         _objOut.close();
         _out.close();
         
         ByteArrayInputStream _in = new ByteArrayInputStream(_out.toByteArray());
         ObjectInputStream _objIn = new ObjectInputStream(_in);
         Object _copy = _objIn.readObject();
         return _copy;
      }
      catch(Exception _exp) {
         //This should not happen
         _exp.printStackTrace();
      }
      
      return null;
   }
   
   /**
    * Get the BeanInfo objects for each Bean
    */
   private void processBeanInfos() {
      beansInfo = new  BeanInfo[beans.length];
      for(int i=0;i<beansInfo.length;i++) {
         try {
            Object _bean = beans[i];
            if(_bean != null) beansInfo[i] = Introspector.getBeanInfo(_bean.getClass());
         }
         catch(IntrospectionException _exp) {
            System.out.println("Error processing "+beans[i].getClass().getName());
         }
      }
   }
   
   /**
    * undoChanges
    * this method will undo the changes that have occured to the bean during this
    * Edit session. This is usually called when the user hits cancel on the org.xito
    */
   private void undoChanges() {
      //For each bean ask the sheet to reset Orginial Values
      for(int i=0;i<beansCopy.length;i++) {
         //PropertySheet2 _sheet = (PropertySheet2)propertySheets.get(beansCopy[i]);
         PropertySheet _sheet = (PropertySheet)propertySheets.get(beansCopy[i]);
         if(_sheet != null) _sheet.resetOriginalValues();
      }
   }
   
   /**
    * updateObjects
    * This method will set the properties on the original Bean that where
    * set on the Copied Bean
    */
   private void updateObjects() {
      
      //Stop Editing of all Property Sheets
      Iterator sheets = propertySheets.values().iterator();
      while(sheets.hasNext()) {
         ((PropertySheet)sheets.next()).stopEditing();
      }
      
      //for each bean read its original property value and
      //if the value is different from the beanCopies property Value
      //then update the Original
      for(int i=0;i<beans.length;i++) {
         //get Objects
         Object bean = beans[i];
         Object beanCopy = beansCopy[i];
         
         if(bean == null || beanCopy == null) continue;
         BeanInfo info = beansInfo[i];
         
         //Get Properties
         PropertyDescriptor descs[] = info.getPropertyDescriptors();
         for(int p=0;p<descs.length;p++) {
            PropertyDescriptor desc = descs[p];
            Method getter = desc.getReadMethod();
            try {
               Object _orgVal = getter.invoke(bean, null);
               Object _newVal = getter.invoke(beanCopy, null);
               //If they are not equal Set Value
               if((_orgVal != null && !_orgVal.equals(_newVal)) || (_newVal != null && !_newVal.equals(_orgVal))) {
                  Method setter = desc.getWriteMethod();
                  if(setter == null)  System.out.println(desc.getDisplayName()+" setter null");
                  setter.invoke(bean, new Object[]
                  {_newVal});
               }
            }
            catch(Exception _exp) {
               _exp.printStackTrace();
            }
         }
         
      }
      
   }
   
   /** Closes the org.xito */
   private void closeDialog(java.awt.event.WindowEvent evt) {
      setVisible(false);
      dispose();
   }
   
   /**
    * ActionPerformed
    */
   public void actionPerformed(ActionEvent pEvent) {
      
      //Cancel
      if(pEvent.getSource() == cancelBtn) {
         if(useOptimisticUpdate) undoChanges();
         this.dispose();
      }
      
      //OK
      if(pEvent.getSource() == okBtn) {
         if(useOptimisticUpdate) updateObjects();
         this.dispose();
      }
   }
}
