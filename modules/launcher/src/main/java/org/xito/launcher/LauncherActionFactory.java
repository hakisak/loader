// Copyright (C) 2005 Xito.org. http://www.xito.org
//
// This file is part of Xito(tm).
//
// This Software is licensed under the terms of the
// COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0
//
// To view the complete Terms of this license visit:
// http://www.opensource.org/licenses/cddl1.txt
//
// COVERED SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN AS IS BASIS, WITHOUT
// WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, WITHOUT
// LIMITATION, WARRANTIES THAT THE COVERED SOFTWARE IS FREE OF DEFECTS,
// MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR NON-INFRINGING. THE ENTIRE
// RISK AS TO THE QUALITY AND PERFORMANCE OF THE COVERED SOFTWARE IS WITH YOU.
// SHOULD ANY COVERED SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, YOU (NOT THE
// INITIAL DEVELOPER OR ANY OTHER CONTRIBUTOR) ASSUME THE COST OF ANY
// NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
// CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY COVERED
// SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.

package org.xito.launcher;

import java.awt.Frame;

import javax.swing.Icon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/**
 * Factory responsible for creating LauncherActions
 * 
 * @author deane
 */
public abstract class LauncherActionFactory {

   protected String name;
   protected Icon smallIcon;
   protected Icon largeIcon;
      
   protected DocumentBuilderFactory builderFactory;
   protected DocumentBuilder builder;
   
   public LauncherActionFactory() {
      try {
         builderFactory = DocumentBuilderFactory.newInstance();
         builder = builderFactory.newDocumentBuilder();
      } catch(ParserConfigurationException parserExp) {
         throw new RuntimeException("can read service information file, error:"+parserExp.getMessage(), parserExp);
      } catch(DOMException domExp) {
         throw new RuntimeException("can't read services xml error:"+domExp.getMessage(), domExp);
      }
   }

   /**
    * Get the xml element name that this launcher action factory will create
    * @return
    */
   public abstract String getElementName();
   
   /**
    * Factory method used to create Actions. 
    * This will create an Action based on a data obj or it
    * will return null if it can't create the action. NOTE: This call could block while
    * processing data object so it should be called from a BusyWorker
    *  
    * @param object to use for data
    * @param owner window if we need to show a org.xito
    */
   public abstract LauncherAction createAction(Frame owner, Object obj);
   
   /**
    * Create an empty Action 
    * @return
    */
   public abstract LauncherAction createAction();
   
   /**
    * Create an Action from an XML action element
    */
   public abstract LauncherAction createActionFromDataElement(org.w3c.dom.Element element);
   
   /**
    * Generate XML Element from LauncherAction
    */
   public abstract org.w3c.dom.Element generateDataElement(LauncherAction action);
   
   /**
    * parse an element to determine an action name
    * @param element
    * @return
    */
   protected String paraseName(org.w3c.dom.Element element) {
      if(element == null) {
         return null;
      }
      
      return element.getAttribute("name");
   }
   
   /**
    * parse an element to determine an action title
    * @param element
    * @return
    */
   protected String parseTitle(org.w3c.dom.Element element) {
      if(element == null) {
         return null;
      }
      
      return element.getAttribute("title");
   }
   
   /**
    * parse an element to determine an action id
    * @param element
    * @return
    */
   protected String parseUniqueID(org.w3c.dom.Element element) {
      if(element == null) {
         return null;
      }
      
      return element.getAttribute("id");
   }
   
   /**
    * Initialize a LaunchDesc with the attributes from the element
    * this will set the name, title, and id of an action
    * @param desc
    * @param element
    */
   protected void initNameAndId(LaunchDesc desc, org.w3c.dom.Element element) {
      if(element.getTagName().equals(getElementName()) == false)
         return;
      
      desc.setName(paraseName(element));
      desc.setTitle(parseTitle(element));
      desc.setUniqueID(parseUniqueID(element));
   }
   
   /**
    * Create an inital element from LaunchDesc. This will set the name, title, and id attributes
    * of the element
    * @param desc
    * @return
    */
   protected org.w3c.dom.Element createInitialElement(LaunchDesc desc) {
      
      org.w3c.dom.Document doc = builder.newDocument();
      Element e = doc.createElement(getElementName());
      if(desc.getName()!=null)
         e.setAttribute("name", desc.getName());
      
      if(desc.getTitle()!=null)
         e.setAttribute("title", desc.getTitle());
      
      if(desc.getUniqueID()!=null)
         e.setAttribute("id", desc.getUniqueID());
      
      return e;
   }
   
   /**
    * @return the largeIcon
    */
   public Icon getLargeIcon() {
      return largeIcon;
   }

   /**
    * @param largeIcon the largeIcon to set
    */
   public void setLargeIcon(Icon largeIcon) {
      this.largeIcon = largeIcon;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the smallIcon
    */
   public Icon getSmallIcon() {
      return smallIcon;
   }

   /**
    * @param smallIcon the smallIcon to set
    */
   public void setSmallIcon(Icon smallIcon) {
      this.smallIcon = smallIcon;
   }
}
