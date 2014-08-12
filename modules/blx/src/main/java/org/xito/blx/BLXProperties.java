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

import java.beans.*;
import java.util.*;
import java.lang.reflect.*;

import org.w3c.dom.*;

/**
 *<p>
 * This class wraps a java.util.Map and enables Map contents to be retrieved and
 * Converted to XML. It many ways it is similar to a java.utils.Properties class. This class
 * does not implement a BLXObject interface however it can be used by BLXObjects to easy
 * store their persistence state as a set of XML properties.
 *</p><p>
 * This Class can contain Integers, String, Longs, Floats, Doubles, Dates, and Booleans, 
 * java.io.Serializeable objects, and objects that implement the BLXObject interface.
 *</p><p>
 * To simplify the storage of Java Bean properties the setBean method can be used to
 * automatically store the property values of a Java Bean.
 * </p>
 *
 * @author  drichan
 * @version
 */
public class BLXProperties {

  public static final int STRING_TYPE = 0;
  public static final int INT_TYPE = 1;
  public static final int LONG_TYPE = 2;
  public static final int FLOAT_TYPE = 3;
  public static final int DOUBLE_TYPE = 4;
  public static final int DATE_TYPE = 5;
  public static final int BOOLEAN_TYPE = 6;
  public static final int NULL_TYPE = 7;
  public static final int SERIALIZEABLE_TYPE = 8;
  public static final int BLXOBJECT_TYPE = 9;

  public static final String STRING_ATTR_TYPE = "string";
  public static final String INT_ATTR_TYPE = "int";
  public static final String LONG_ATTR_TYPE = "long";
  public static final String FLOAT_ATTR_TYPE = "float";
  public static final String DOUBLE_ATTR_TYPE = "double";
  public static final String DATE_ATTR_TYPE = "date";
  public static final String BOOLEAN_ATTR_TYPE = "boolean";
  public static final String NULL_ATTR_TYPE = "null";
  public static final String SERIALIZEABLE_ATTR_TYPE = "serializeable";
  public static final String BLXOBJECT_ATTR_TYPE = "blxobject";

  public static final String PROPERTIES_NODE_NAME = "properties";
  public static final String BEAN_CLASS_ATTR_NAME = "beanClass";
  public static final String PROPERTY_NODE_NAME = "property";
  public static final String TYPE_ATTR_NAME = "type";
  public static final String NAME_ATTR_NAME = "name";
  public static final String VALUE_ATTR_NAME = "value";

  protected HashMap properties = new HashMap();
  protected boolean dirty_flag = false;
  protected Calendar calendar;
  protected Object bean;
  protected BeanInfo beanInfo;
  protected HashMap propDescriptors;

  /**
   * Create a new Empty Properties Object
   */
  public BLXProperties() {
    calendar = Calendar.getInstance();
  }

  /**
   * Create a new Properties Object. That uses a Bean to get and set the Properties.
   * This allows this XML Properties object to store Bean Properties.
   * @param pBean to store properties for
   * @param propNames names of Properties to store for this Bean
   */
  public BLXProperties(Object pBean, String[] propNames) {
    this();
    setBean(pBean, propNames);
  }

  /**
   * Get the Type of the Property
   * @param pName Name of Property
   * @return type of Property
   */
  public int getPropertyType(String pName) {
    Object _value = properties.get(pName);

    if(_value == null) return NULL_TYPE;
    else if(_value instanceof String) return STRING_TYPE;
    else if(_value instanceof Integer) return INT_TYPE;
    else if(_value instanceof Long) return LONG_TYPE;
    else if(_value instanceof Float) return FLOAT_TYPE;
    else if(_value instanceof Double) return DOUBLE_TYPE;
    else if(_value instanceof Date) return DATE_TYPE;
    else if(_value instanceof Boolean) return BOOLEAN_TYPE;
    else if(_value instanceof BLXObject) return BLXOBJECT_TYPE;
    else if(_value instanceof java.io.Serializable) return SERIALIZEABLE_TYPE;

    return -1;
  }

  /** 
   * Get Property value as String
   * @param pName name of the property
   */
  public String getPropertyString(String pName) {
    Object _value = properties.get(pName);
    return _value == null?null:_value.toString();
  }

  /** 
   * Get Property value as int
   * @param pName name of the property
   */
  public int getPropertyInt(String pName) {
    Object _value = properties.get(pName);
    return _value == null?0:((Integer)_value).intValue();
  }

  /** 
   * Get Property value as long
   * @param pName name of the property
   */
  public long getPropertyLong(String pName) {
    Object _value = properties.get(pName);
    return _value == null?0:((Long)_value).longValue();
  }

  /** 
   * Get Property value as float
   * @param pName name of the property
   */
  public float getPropertyFloat(String pName) {
    Object _value = properties.get(pName);
    return _value == null?0f:((Float)_value).floatValue();
  }

  /** 
   * Get Property value as double
   * @param pName name of the property
   */
  public double getPropertyDouble(String pName) {
    Object _value = properties.get(pName);
    return _value == null?0d:((Double)_value).doubleValue();
  }

  /** 
   * Get Property value as Date
   * @param pName name of the property
   */
  public Date getPropertyDate(String pName) {
    Object _value = properties.get(pName);
    return _value == null?null:(Date)_value;
  }

  /** 
   * Get Property value as boolean
   * @param pName name of the property
   */
  public boolean getPropertyBoolean(String pName) {
    Object _value = properties.get(pName);
    return _value == null?false:((Boolean)_value).booleanValue();
  }

  /** 
   * Get Property Value
   * @param pName name of the property
   */
  public Object getProperty(String pName) {
    return properties.get(pName);
  }

  /** 
   * Set the Property value
   * @param pName name of the property
   * @param pValue value of the property
   */
  public void setProperty(String pName, Object pValue) {
    //Check for correct type
    if( pValue !=null &&
    !(pValue instanceof String) &&
    !(pValue instanceof Integer) &&
    !(pValue instanceof Long) &&
    !(pValue instanceof Float) &&
    !(pValue instanceof Double) &&
    !(pValue instanceof Date) &&
    !(pValue instanceof Boolean) &&
    !(pValue instanceof java.io.Serializable) &&
    !(pValue instanceof BLXObject)) throw new java.lang.IllegalArgumentException(pName.getClass().getName()+" Not supported!");

    dirty_flag = true;
    properties.put(pName, pValue);
  }

  /** 
   * Set int Property value
   * @param pName name of the property
   * @param pValue value of the property
   */
  public void setPropertyInt(String pName, int pValue) {
    setProperty(pName, new Integer(pValue));
  }

  /** 
   * Set long Property value
   * @param pName name of the property
   * @param pValue value of the property
   */
  public void setPropertyLong(String pName, long pValue) {
    setProperty(pName, new Long(pValue));
  }

  /** 
   * Set float Property value
   * @param pName name of the property
   * @param pValue value of the property
   */
  public void setPropertyFloat(String pName, float pValue) {
    setProperty(pName, new Float(pValue));
  }

  /** 
   * Set double Property value
   * @param pName name of the property
   * @param pValue value of the property
   */
  public void setPropertyDouble(String pName, double pValue) {
    setProperty(pName, new Double(pValue));
  }

  /** 
   * Set Date Property value
   * @param pName name of the property
   * @param pValue value of the property
   */
  public void setPropertyDate(String pName, Date pValue) {
    setProperty(pName, pValue);
  }

  /** 
   * Set Boolean Property value
   * @param pName name of the property
   * @param pValue value of the property
   */
  public void setPropertyBoolean(String pName, boolean pValue) {
    setProperty(pName, new Boolean(pValue));
  }

  /** 
   * Set String Property value
   * @param pName name of the property
   * @param pValue value of the property
   */
  public void setPropertyString(String pName, String pValue) {
    setProperty(pName, pValue);
  }

  /**
   * Return true if the property values have changed or a property has
   * been removed or added.
   * @return true if component has changed
   */
  public boolean isDirty() {
    return dirty_flag;
  }
  
  /**
   * Set the dirty state of this object
   * @param dirty true if the properties are dirty or false otherwise
   */
  public void setIsDirty(boolean dirty) {
    dirty_flag = dirty;
  }
   

  /**
   * Sets the XML Element for these Properties. 
   * @param Element that contains Property Elements for this object
   */
  public void setXMLElement(Element pElement) {
    properties.clear();
    String _beanClsName = pElement.getAttribute(BEAN_CLASS_ATTR_NAME);

    //If we have a bean but the beanClass doesn't match then throw an Exception
    if(bean != null && (_beanClsName == null || _beanClsName.equals(bean.getClass().getName())==false))
      throw new RuntimeException("Current Bean Class: " + bean.getClass().getName()+" does not match class: "+_beanClsName+" specified in XML");

    //load properties from XML
    NodeList _children = pElement.getElementsByTagName(PROPERTY_NODE_NAME);
    for(int i=0; i<_children.getLength();i++) {
      Element _property = (Element)_children.item(i);
      String _name = _property.getAttribute(NAME_ATTR_NAME);
      String _type = _property.getAttribute(TYPE_ATTR_NAME);
      String _value = _property.getAttribute(VALUE_ATTR_NAME);

      //Put the Value in
      try {
        //BLXObject
        if(_type.equals(BLXOBJECT_ATTR_TYPE)) {
          properties.put(_name, getBLXObject(_property));
        }
        //Serializable
        else if(_type.equals(SERIALIZEABLE_ATTR_TYPE)) {
          properties.put(_name, getSerializableObject(_property));
        }
        //null
        else if(_type.equals(NULL_ATTR_TYPE)) {
          properties.put(_name, null);
        }
        //int
        else if(_type.equals(INT_ATTR_TYPE)) {
          properties.put(_name, Integer.valueOf(_value));
        }
        //long
        else if(_type.equals(LONG_ATTR_TYPE)) {
          properties.put(_name, Long.valueOf(_value));
        }
                //float
        else if(_type.equals(FLOAT_ATTR_TYPE)) {
          properties.put(_name, Float.valueOf(_value));
        }
        //double
        else if(_type.equals(DOUBLE_ATTR_TYPE)) {
          properties.put(_name, Double.valueOf(_value));
        }
        //Boolean
        else if(_type.equals(BOOLEAN_ATTR_TYPE)) {
          properties.put(_name, Boolean.valueOf(_value));
        }
        //Date
        else if(_type.equals(DATE_ATTR_TYPE)) {
          try {
            StringTokenizer _tokenizer = new StringTokenizer(_value, ":");
            int year = Integer.parseInt(_tokenizer.nextToken());
            int month = Integer.parseInt(_tokenizer.nextToken());
            int day = Integer.parseInt(_tokenizer.nextToken());
            int hour = Integer.parseInt(_tokenizer.nextToken());
            int min = Integer.parseInt(_tokenizer.nextToken());
            int sec = Integer.parseInt(_tokenizer.nextToken());
            calendar.set(year, month, day, hour, min, sec);
            properties.put(_name, calendar.getTime());
          }
          catch(Exception _exp) {
            _exp.printStackTrace();
            properties.put(_name, null);
          }
        }
        //String
        else if(_type.equals(STRING_ATTR_TYPE)) {
          properties.put(_name, _value);
        }
      }
      catch(NumberFormatException _exp) {
        _exp.printStackTrace();
      }
    }

    //Load the Properties into the Bean
    if(bean != null) storePropertiesInBean();
  }
  
  /**
   * Get a Serializable Object from the specified Property Element
   * @param element Property Element. The first child element of this
   * Property Element should be a <javaobject>
   */
  protected Object getSerializableObject(Element element) {
    
    return null;
  }
  
  /**
   * Get a BLXObject from the specified Property Element
   * @param element Property Element. The first child element of this
   * Property Element should be a <blx:object> or <blx:component>
   */
  protected Object getBLXObject(Element element) {
    return null;
  }

  /**
   * Set the Bean this Property Object is listening on.
   * If the Bean supports Bound Properties then this Object will listen for property Changes
   * on the bean and store the property values.
   * @param pBean to listen for property changes or null to not listen for property changes on a Bean
   * @param pPropNames names of Properties we should listen on or null for all properties
   */
  public void setBean(Object pBean, String[] pPropNames) {
    bean = pBean;
    try {
      beanInfo = Introspector.getBeanInfo(bean.getClass(), Introspector.USE_ALL_BEANINFO);
      PropertyDescriptor props[] = beanInfo.getPropertyDescriptors();
      List propNames = (pPropNames != null)?Arrays.asList(pPropNames):new ArrayList();

      //Popuplate property Descriptors
      propDescriptors = new HashMap();
      for(int i=0;i<props.length;i++) {
        String _name = props[i].getName();
        if(pPropNames != null &&  propNames.contains(_name))
          propDescriptors.put(_name, props[i]);
        else if(pPropNames == null)
          propDescriptors.put(_name, props[i]);
      }
    }
    catch(IntrospectionException exp) {
      //Can't introspect the Bean
      bean = null;
      throw new IllegalArgumentException("Bean: "+pBean.getClass().getName()+" cannot be introspected");
    }
  }

  /**
   * Get the Bean this Property Object is listening on.
   * @return the Bean
   */
  public Object getBean() {
    return bean;
  }

  /**
   * Get the Data Node for this Component
   * @return the Node that contains this Components Settings
   */
  public Element getXMLElement() {
    
    //Create Place holder Document and Element
    Document _doc = BLXUtility.createDOMDocument();
    Element _propsE = _doc.createElement(PROPERTIES_NODE_NAME);

    //If these properties are for a bean first load the properties from the bean
    if(bean != null) {
      loadPropertiesFromBean();
      _propsE.setAttribute(BEAN_CLASS_ATTR_NAME, bean.getClass().getName());
    }

    //Get each Property
    Iterator _it = properties.keySet().iterator();
    while(_it.hasNext()) {
      String _name = (String)_it.next();
      Object _value = properties.get(_name);
      Element _property = _doc.createElement(PROPERTY_NODE_NAME);
      _property.setAttribute(this.NAME_ATTR_NAME, _name);

      int _type = getPropertyType(_name);
      switch(_type) {
        case NULL_TYPE:   _property.setAttribute(TYPE_ATTR_NAME, NULL_ATTR_TYPE);
        break;

        case STRING_TYPE: _property.setAttribute(TYPE_ATTR_NAME, STRING_ATTR_TYPE);
        _property.setAttribute(VALUE_ATTR_NAME, _value.toString());
        break;

        case INT_TYPE:    _property.setAttribute(TYPE_ATTR_NAME, INT_ATTR_TYPE);
        _property.setAttribute(VALUE_ATTR_NAME, _value.toString());
        break;

        case LONG_TYPE:   _property.setAttribute(TYPE_ATTR_NAME, LONG_ATTR_TYPE);
        _property.setAttribute(VALUE_ATTR_NAME, _value.toString());
        break;

        case FLOAT_TYPE:  _property.setAttribute(TYPE_ATTR_NAME, FLOAT_ATTR_TYPE);
        _property.setAttribute(VALUE_ATTR_NAME, _value.toString());
        break;

        case DOUBLE_TYPE: _property.setAttribute(TYPE_ATTR_NAME, DOUBLE_ATTR_TYPE);
        _property.setAttribute(VALUE_ATTR_NAME, _value.toString());
        break;

        case DATE_TYPE:   _property.setAttribute(TYPE_ATTR_NAME, DATE_ATTR_TYPE);
        calendar.setTime((Date)_value);
        _property.setAttribute(VALUE_ATTR_NAME, "" + calendar.get(Calendar.YEAR) + ":"
        + calendar.get(Calendar.MONTH) + ":"
        + calendar.get(Calendar.DAY_OF_MONTH) + ":"
        + calendar.get(Calendar.HOUR) + ":"
        + calendar.get(Calendar.MINUTE) + ":"
        + calendar.get(Calendar.SECOND));
        break;

        case BOOLEAN_TYPE:_property.setAttribute(TYPE_ATTR_NAME, BOOLEAN_ATTR_TYPE);
        _property.setAttribute(VALUE_ATTR_NAME, _value.toString());
        break;
      }

      _propsE.appendChild(_property);
    }

    return _propsE;
  }

  /**
   * Load Properties from the Bean object
   */
  private void loadPropertiesFromBean() {
    if(bean == null) return;

    Iterator it = propDescriptors.values().iterator();
    while(it.hasNext()) {
      PropertyDescriptor prop = (PropertyDescriptor)it.next();
      Method getter = prop.getReadMethod();

      try {
        Object value = getter.invoke(bean, null);
        this.setProperty(prop.getName(), value);
      }
      catch(Exception exp) {
        //Just skip this Property
      }
    }
  }

  /**
   * Load Properties from the Bean object
   */
  private void storePropertiesInBean() {
    if(bean == null) return;

    Iterator it = propDescriptors.values().iterator();
    while(it.hasNext()) {
      PropertyDescriptor prop = (PropertyDescriptor)it.next();
      Method setter = prop.getWriteMethod();

      try {
        Object value = this.getProperty(prop.getName());
        setter.invoke(bean, new Object[]{value});
      }
      catch(Exception exp) {
        //Just skip this Property
      }
    }
  }

}