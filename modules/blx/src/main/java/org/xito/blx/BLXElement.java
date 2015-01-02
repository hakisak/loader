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

package org.xito.blx;

import java.awt.Point;
import java.awt.Dimension;
import java.net.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;


/**
 * The BLXElement Object describes the type of Object represented by the interface BLXObject. This class
 * contains the following type of information:
 *
 *  - jnlp extension the Object is to be loaded from.
 *  - The name of the Class of the Object
 *  - The type of Object, Component or Factory etc.
 *  - A unique identifier of this Object instance
 *
 * @author $Author: drichan $
 * @version $Revision: 1.8 $
 * @since $Date: 2007/09/02 00:43:00 $
 */
public class BLXElement {
   
   public static final String FILE_EXT = ".blx";
   public static final String BLX_NS = "blx";
   public static final String BLX_DOCUMENT_NAME = "document";
   public static final String BLX_EXT_ALIAS_NAME = "ext-alias";
   public static final String BLX_ALIAS_NAME_ATTR = "name";
   public static final String BLX_ALIAS_EXT_NAME_ATTR = "ext-alias";
   public static final String BLX_ALIAS_VERSION_ATTR = "version";
   public static final String BLX_ALIAS_HREF_ATTR = "href";
   
   public static final String BLX_OBJ_NODE_NAME = "object";
   public static final String BLX_COMP_NODE_NAME = "component";
   public static final String BLX_EXT_NAME_ATTR = "ext-name";
   public static final String BLX_EXT_HREF_ATTR = "ext-href";
   public static final String BLX_EXT_VERSION_ATTR = "ext-version";
   public static final String BLX_EXT_ALIAS_ATTR = "ext-alias";
   public static final String BLX_SERVICE_NAME_ATTR = "service-name";
   public static final String BLX_CLASS_NAME_ATTR = "class";
   public static final String BLX_HREF_ATTR = "href";
   public static final String BLX_COMP_X_ATTR = "x";
   public static final String BLX_COMP_Y_ATTR = "y";
   public static final String BLX_COMP_WIDTH_ATTR = "width";
   public static final String BLX_COMP_HEIGHT_ATTR = "height";
   public static final String BLX_OBJ_ID = "id";
   
   public static final int OBJECT_TYPE = 1;
   public static final int COMP_TYPE = 2;
   public static final int OBJECT_FACTORY_TYPE = 10;
   public static final int COMPONENT_FACTORY_TYPE = 11;
   
   public static final String OBJECT_FACTORY_TYPE_STR = "object";
   public static final String COMPONENT_FACTORY_TYPE_STR = "component";
   
   private int objectType;
   private String extAliasName;
   private String extName;
   private String extVersion;
   private String extHREF;
   private BLXExtension extension;
   private String serviceName;
   
   private String className;
   private String id;
   private int x;
   private int y;
   private int width;
   private int height;
   private org.w3c.dom.Element domElement;
   private org.w3c.dom.Element dataElement;
   
   private URL contextURL;
   private String href;
      
   /**
    * Creates a BLXElement object from a Dom Element that represents a BLX Object
    * @param element to parse
    * @throws InvalidBLXXMLException if the element does not represent a BLX Object
    */
   public BLXElement(org.w3c.dom.Element element, URL contextURL) throws InvalidBLXXMLException {
      this.contextURL = contextURL;
      domElement = element;
      
      processNode();
   }
   
   /**
    * Creates a BLXElement object from an Extension
    */
   public BLXElement(int type, BLXExtension extension, String objClassName, String id) {
      this.extension = extension; 
      this.id = id;
      
      objectType = type;
      className = objClassName;
      if(extension != null && extension.isService()) {
         serviceName = extension.getServiceName();
      }
      else if(extension != null) {
         extName = extension.getName();
         extVersion = extension.getVersion();
         extHREF = extension.getHREF();
      }
   }
   
   
   /**
    * Creates a BLXElement object from an Extension
    */
   public BLXElement(int type, String serviceName, String objClassName, String id) {
      this.serviceName = serviceName; 
      this.id = id;
      
      objectType = type;
      className = objClassName;
   }
   
   /**
    * Creates a BLXElement object from an Extension
    */
   public BLXElement(int type, BLXExtension extension, String objClassName, String id, int x, int y, int width, int height) {
      this(type, extension, objClassName, id);
      
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }
   
   /**
    * Get the Service Name this BLX Object should be loaded from
    */
   public String getServiceName() {
      return serviceName;
   }
   
   /**
    * Get the Type of Object this Element represents Object or Component
    * @return OBJECT_TYPE or COMP_TYPE
    */
   public int getObjectType() {
      return objectType;
   }
   
   /**
    * Get the URL that all HREF attributes are relative to
    * @return URL
    */
   public URL getContextURL() {
      return contextURL;
   }
   
   /**
    * Set the URL that all HREF attributes are relative to
    * @param url
    */
   public void setContextURL(URL url) {
      contextURL = url;
   }
   
   /**
    * Get the ClassName for the BLXObject
    */
   public String getClassName() {
      return className;
   }
   
   /**
    * Set the ClassName for the BLXObject
    * @param clsName name for the BLX Object
    */
   public void setClassName(String clsName) {
      className = clsName;
   }
   
   /**
    * Get the Components X location
    */
   public int getX() {
      return x;
   }
   
   /**
    * Set the Components X location
    */
   public void setX(int x) {
      this.x = x;
   }
   
   /**
    * Get the Components Y location
    */
   public int getY() {
      return y;
   }
   
   /**
    * Set the Components Y location
    */
   public void setY(int y) {
      this.y = y;
   }
   
   /**
    * Get the Components Width
    */
   public int getWidth() {
      return width;
   }
   
   /**
    * Set the Components Width
    * @param w width of component
    */
   public void setWidth(int w) {
      width = w;
   }
   
   /**
    * Get the Components Height
    */
   public int getHeight() {
      return height;
   }
   
   /**
    * Set the Components Height
    * @param h height of component
    */
   public void setHeight(int h) {
      height = h;
   }
   
   /**
    * Get the Components ID
    */
   public String getID() {
      return id;
   }
   
   /**
    * Set the Components ID
    * @param id for this Component
    */
   public void setID(String id) {
      this.id = id;
   }
   
   /**
    * Get the URL reference to the Objects XML Data
    */
   public String getHREF() {
      return href;
   }
   
   /**
    * Set the URL reference to the Objects XML Data
    */
   public void setHREF(String href) {
      if(href != null && href.equals("")) this.href = null;
      else this.href = href;
   }
   
   /**
    * Get the XML syntax for this BLXElement
    */
   public String toXMLString() {
      
      StringBuffer _buf = new StringBuffer();
      _buf.append('<'+BLX_NS+':');
      
      //Object
      if(objectType == OBJECT_TYPE) {
         _buf.append(BLX_OBJ_NODE_NAME);
         _buf.append(' ');
      }
      //Component
      else if(objectType == COMP_TYPE) {
         _buf.append(BLX_COMP_NODE_NAME);
         _buf.append(' ');
      }
      
      //Extension Name
      _buf.append(BLX_EXT_NAME_ATTR + "=\"");
      _buf.append(extName);
      _buf.append("\" ");
      
      //Extension Version
      _buf.append(BLX_EXT_VERSION_ATTR + "=\"");
      _buf.append(extVersion);
      _buf.append("\" ");
      
      //Extension HREF
      _buf.append(BLX_EXT_HREF_ATTR+ "=\"");
      _buf.append(extHREF);
      _buf.append("\" ");
      
      //ClassName
      _buf.append(BLX_CLASS_NAME_ATTR + "=\"");
      _buf.append(className);
      _buf.append("\" ");
      
      //ID
      if(id != null) {
         _buf.append(BLX_OBJ_ID + "=\"");
         _buf.append(id);
         _buf.append("\" ");
      }
      
      if(objectType == COMP_TYPE) {
         //X
         _buf.append(BLX_COMP_X_ATTR + "=\"");
         _buf.append(x);
         _buf.append("\" ");
         
         //Y
         _buf.append(BLX_COMP_Y_ATTR + "=\"");
         _buf.append(y);
         _buf.append("\" ");
         
         //Width
         _buf.append(BLX_COMP_WIDTH_ATTR + "=\"");
         _buf.append(width);
         _buf.append("\" ");
         
         //Height
         _buf.append(BLX_COMP_HEIGHT_ATTR + "=\"");
         _buf.append(height);
         _buf.append("\" ");
      }
      
      //Close Element
      _buf.append('>');
      
      return _buf.toString();
   }
   
   /**
    * Returns this BLXElement as a DOM Element
    */
   public Element getDOMElement() {
      createDOMElement();
      return domElement;
   }
   
   /**
    * Returns the data element for the BLX Object that this element represents
    * This would probably return the first child element of the BLX Element.
    */
   public Element getDataElement() {
      if(dataElement == null) findDataElement();
      return dataElement;
   }
   
   /**
    * Sets the data element for this BLX Object
    */
   public void setDataElement(Element dataE) {
      dataElement = dataE;
   }
   
   /**
    * Create a DOMElement for this BLXElement
    */
   protected void createDOMElement() {
      
      Document doc = BLXUtility.createDOMDocument();
      
      if(this.getObjectType() == this.OBJECT_TYPE)
         domElement = doc.createElementNS("blx", BLX_NS+":"+this.BLX_OBJ_NODE_NAME);
      else
         domElement = doc.createElementNS("blx", BLX_NS+":"+this.BLX_COMP_NODE_NAME);
      
      //Populate domElement with correct information
      domElement.setAttribute(BLX_CLASS_NAME_ATTR, this.getClassName());
      
      //Service Name
      if(serviceName != null) {
         domElement.setAttribute(BLX_SERVICE_NAME_ATTR, serviceName);
      }
      else {
         //Ext Name
         if(extName != null) domElement.setAttribute(BLX_EXT_NAME_ATTR, extName);
         //Ext Version
         if(extVersion != null) domElement.setAttribute(BLX_EXT_VERSION_ATTR, extVersion);
         //Ext HREF
         if(extHREF != null) domElement.setAttribute(BLX_EXT_HREF_ATTR, extHREF);
      }
            
      //HREF of Data
      if(getHREF()!=null) domElement.setAttribute(BLX_HREF_ATTR, this.getHREF());
      
      //ID
      if(getID()!=null) domElement.setAttribute(BLX_OBJ_ID, getID());
      
      //Popukate Component Information
      if(getObjectType() == this.COMP_TYPE) {
         domElement.setAttribute(BLX_COMP_X_ATTR, "" + getX());
         domElement.setAttribute(BLX_COMP_Y_ATTR, "" + getY());
         domElement.setAttribute(BLX_COMP_WIDTH_ATTR, "" + getWidth());
         domElement.setAttribute(BLX_COMP_HEIGHT_ATTR, "" + getHeight());
      }
   }
   
   /**
    * Process the blxNode and setup all Attributes of this Wrapper Object
    */
   protected void processNode() throws InvalidBLXXMLException {
      
      //Get Type
      String nodeName = BLXUtility.getLocalNodeName(domElement);
      if(nodeName.equals(BLX_OBJ_NODE_NAME)) {
         objectType = OBJECT_TYPE;
      }
      else if(nodeName.equals(BLX_COMP_NODE_NAME)) {
         objectType = COMP_TYPE;
      }
      
      //Throw exception if we don't have a type
      if(objectType == 0) throw new InvalidBLXXMLException("BLX type not known for element:"+domElement.getNodeName());
      
      //Get Class Name
      className = domElement.getAttribute(BLX_CLASS_NAME_ATTR);
      if("".equals(className)) className = null;
      
      //Get Service Name
      serviceName = domElement.getAttribute(BLX_SERVICE_NAME_ATTR);
      if("".equals(serviceName)) serviceName = null;
      if(serviceName != null) {
         try {
            extension = BLXExtManager.getInstance().getExtensionFromService(serviceName);
         }
         catch(ExtensionLoadException extLoadExp) {
            throw new InvalidBLXXMLException(extLoadExp.toString(), extLoadExp);
         }
      }
      else {
         //Get Extension Alias
         extAliasName = domElement.getAttribute(BLX_ALIAS_EXT_NAME_ATTR);
         if("".equals(extAliasName)) extAliasName = null;

         //Get Extension Name
         extName = domElement.getAttribute(BLX_EXT_NAME_ATTR);
         if("".equals(extName)) extName = null;

         //Get Extension HREF
         extHREF = domElement.getAttribute(BLX_EXT_HREF_ATTR);
         if("".equals(extHREF)) extHREF = null;

         //Get Version
         extVersion = domElement.getAttribute(BLX_EXT_VERSION_ATTR);
         if("".equals(extVersion)) extVersion = null;
         
         if(extHREF != null) {
            try {
               extension = BLXExtManager.getInstance().getExtension(new URL(contextURL, extHREF));
            }
            catch(Exception exp) {
               throw new InvalidBLXXMLException(exp.toString() + " contextURL="+contextURL +" extHREF=" + extHREF, exp);
            }
         }
      }
      
      //GET id of Object
      id = domElement.getAttribute(BLX_OBJ_ID);
      if("".equals(id)) id = null;
      
      //Get HREF of Data NODE
      setHREF(domElement.getAttribute(BLX_HREF_ATTR));
      
      //Get Component Stuff
      if(objectType == COMP_TYPE) {
                  
         //Get Location
         try {
            x = Integer.parseInt(domElement.getAttribute(BLX_COMP_X_ATTR));
            y = Integer.parseInt(domElement.getAttribute(BLX_COMP_Y_ATTR));
         }
         catch(NumberFormatException _exp) {
            System.err.println("Error processing x/y location for value: "+domElement.toString());
         }
         
         //Get Size
         try {
            width = Integer.parseInt(domElement.getAttribute(BLX_COMP_WIDTH_ATTR));
            height = Integer.parseInt(domElement.getAttribute(BLX_COMP_HEIGHT_ATTR));
         }
         catch(NumberFormatException _exp) {
            System.err.println("Error processing w/h size for value: "+domElement.toString());
         }
      }
      
      //ClassName must be specified
      if(className == null) {
         throw new InvalidBLXXMLException("Classname for object not specified.");
      }
   }
   
   /**
    * Find the DataElement for this BLX Element
    */
   protected void findDataElement() {
      
      String href = getHREF();
      if(href != null) {
         dataElement = getDataFromURL();
      }
      else if(domElement != null) {
         NodeList nodes = domElement.getChildNodes();
         for(int i=0;i<nodes.getLength();i++) {
            if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
               dataElement = (Element)nodes.item(i);
               break;
            }
         }
      }
   }
   
   /**
    * Get Data from a seperate Document
    * @return new Element that contains the data from the uri
    */
   protected Element getDataFromURL() {
      try {
         URL url = new URL(contextURL, this.getHREF());
         DocumentBuilder builder = BLXUtility.createDOMParser();
         Document doc = builder.parse(url.toString());
         Element root = doc.getDocumentElement();
         
         //First Child should be a match of this BLX Element
         Element blx = null;
         NodeList nodes = root.getChildNodes();
         for(int i=0;i<nodes.getLength();i++) {
            if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
               blx = (Element)nodes.item(i);
               break;
            }
         }
         if(blx == null) return null;
         
         //First Child of BLX element of this Document should be our Data Element
         Element data = null;
         nodes = blx.getChildNodes();
         for(int i=0;i<nodes.getLength();i++) {
            if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
               data = (Element)nodes.item(i);
               break;
            }
         }
         
         return data;
      }
      catch(Exception _exp) {
         _exp.printStackTrace();
      }
      
      return null;
   }
   
   /**
    * Getter for property extAliasName.
    * @return Value of property extAliasName.
    */
   public java.lang.String getExtAliasName() {
      return extAliasName;
   }
   
   /**
    * Setter for property extAliasName.
    * @param extAliasName New value of property extAliasName.
    */
   public void setExtAliasName(java.lang.String extAliasName) {
      this.extAliasName = extAliasName;
   }
   
   /**
    * Get this BLXElement's Extension
    *
    */
   public BLXExtension getExtension() {
      return extension;
   }
   
   /**
    * Getter for property extName.
    * @return Value of property extName.
    */
   public java.lang.String getExtName() {
      return extName;
   }
   
   /**
    * Setter for property extName.
    * @param extName New value of property extName.
    */
   public void setExtName(java.lang.String extName) {
      this.extName = extName;
   }
   
   /**
    * Getter for property extVersion.
    * @return Value of property extVersion.
    */
   public java.lang.String getExtVersion() {
      return extVersion;
   }
   
   /**
    * Setter for property extVersion.
    * @param extVersion New value of property extVersion.
    */
   public void setExtVersion(java.lang.String extVersion) {
      this.extVersion = extVersion;
   }
   
   /**
    * Getter for property extHREF.
    * @return Value of property extHREF.
    */
   public java.lang.String getExtHREF() {
      return extHREF;
   }
   
   /**
    * Setter for property extHREF.
    * @param extHREF New value of property extHREF.
    */
   public void setExtHREF(java.lang.String extHREF) {
      this.extHREF = extHREF;
   }
   
}


