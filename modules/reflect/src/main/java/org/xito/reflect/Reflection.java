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

package org.xito.reflect;

import java.lang.reflect.*;

/** <p>
 * The Reflection class simplifies the process of making reflection calls on classes
 * or objects. There are a wide variety of methods that can be used. The callStatic...
 * methods are used to call static methods with arguments and the call... methods are
 * used to call instance level methods on an object.
 * </p><p>
 * Null argument values should only be sent to the methods that also require argument class
 * types to be sent. This is because the arguments classes are used to determine which method
 * to call on the class or object instance. By passing the argument's class types as a parameter
 * it is possible to pass null as an argument value.
 * </p>
 * <p>
 * To use the ReflectKit api create an instance of Reflection and use the helper
 * methods provided.<br> Example:
 * </p>
 * <pre>
 * Reflection kit = Reflection.getToolKit();
 *
 * try {
 *  Class cls = kit.findClass("org.xito.reflect.TestStatic");
 *  kit.callStatic(cls, "testStaticMethod", "argValue"));
 *  kit.callStatic(cls, "testStaticMethod", true));
 *  kit.callStatic(cls, "testStaticMethod", false));
 * }
 * catch(ReflectException reflect) {
 *  //thrown in there was a problem locating the proper method
 * }
 * catch(InvocationTargetException exp) {
 *  //thrown if the called method threw an exception in its method body etc.
 * }
 * </pre>
 * @author drichan
 */
public class Reflection {
  
  /**
   * Private Constructor for the Reflection Toolkit
   */
  private Reflection() {
  }
  
  /**
   * Get a Reflection TookKit Instance
   * @return Reflection
   */
  public static Reflection getToolKit() {
    return new Reflection();
  }
  
  /**
   * Get a Class from the Reflection Toolkit ClassLoader
   * @param className
   * @return Class
   */
  public Class findClass(String className) throws ClassNotFoundException {
    return findClass(className, null);
  }
  
  /**
   * Get a Class from the specified ClassLoader
   * @param className
   * @param loader
   * @return Class
   */
  public Class findClass(String className, ClassLoader loader) throws ClassNotFoundException {
    if(loader != null) 
      return loader.loadClass(className);
    else
      return Class.forName(className);
  }
  
  /**
   * Create a new Object instance for the given Class.
   * @param cls to create an instance of
   * @throws ReflectException if a no parameter constructor is not found
   * @throws InvovationTargetException if an Exception is thrown by the Constructor
   */
  public Object newInstance(Class cls) throws ReflectException, InvocationTargetException {
    return newInstance(cls, null);
  }
  
  /**
   * Create a new Object instance for the given Class and specified arguments
   * @param cls to create an instance of
   * @param args arguments to pass to the constructor. No null values can be used. To 
   * pass null values to a constructor use the newInstance(Class cls, Object[] args, Class[] types)
   * method.
   *              
   * @throws ReflectException if a no parameter constructor is not found
   * @throws InvovationTargetException if an Exception is thrown by the Constructor
   */
  public Object newInstance(Class cls, Object[] args) throws ReflectException, InvocationTargetException {

    try {
      if(args == null) return cls.newInstance();
    }
    catch(InstantiationException instanceExp) {
      throw new ReflectException(instanceExp);
    }
    catch(IllegalAccessException accessExp) {
      throw new ReflectException(accessExp);
    }
    
    return newInstance(cls, args, getTypes(args));
  }
  
  /**
   * Create a new Object instance for the given Class and specified arguments
   * @param cls to create an instance of
   * @param args arguments to pass to the constructor. 
   * @param types Class types of the parameters to pass. This ensure the correct construtor is called
   * @throws ReflectException if a no parameter constructor is not found
   * @throws InvovationTargetException if an Exception is thrown by the Constructor
   */
  public Object newInstance(Class cls, Object[] args, Class[] types) throws ReflectException, InvocationTargetException {
    try {
      if(args == null) throw new NullPointerException("args cannot be null");
      if(types == null) throw new NullPointerException("types cannot be null");
      
      Constructor c = cls.getConstructor(types);
      return c.newInstance(args);
    }
    catch(InstantiationException instanceExp) {
      throw new ReflectException(instanceExp);
    }
    catch(IllegalAccessException accessExp) {
      throw new ReflectException(accessExp);
    }
    catch(IllegalArgumentException argExp) {
      throw new ReflectException(argExp);
    }
    catch(NoSuchMethodException noMethod) {
      throw new ReflectException(noMethod);
    }
  }
  
  /**
   * Returns an array of Classes for the array of object arguments passed.
   * The args array cannot contain any null values or a NullPointerException 
   * will be thrown.
   * @param args Object Arguments to get Types for
   * @return Class[]
   */
  public Class[] getTypes(Object[] args) {
    
    Class[] types = null;
    if(args != null) {
      types = new Class[args.length];
      for(int i=0;i<args.length;i++) {
        types[i] = args[i].getClass();
      }
    }
    
    return types;
  }

  /**
   * Calls a Method.
   * Helper methods. 
   * @param cls method is located in
   * @param obj instance to call the method on or null for a static method
   * @param args Arguments for the method. Null values will cause a NullPointerException to be thrown
   * @return Object
   * @throws ReflectException when a problem with the reflect has occured invalid args or method not found etc.
   * @throws InvocationTargetException if the target method throws an exception during its invocation
   */
  public Object call(Class cls, Object obj, String methodName, Object[] args) throws ReflectException, InvocationTargetException {
    
    Class[] types = getTypes(args);
    return call(cls, obj, methodName, args, types);
  }
  
  /**
   * Calls a Method.
   * Helper methods. 
   * @param cls method is located in
   * @param obj instance to call the method on or null for a static method
   * @param args Arguments for the method. Argument values in the array can be null.
   * @param types Class types of Arguments
   * @return Object
   * @throws ReflectException when a problem with the reflect has occured invalid args or method not found etc.
   * @throws InvocationTargetException if the target method throws an exception during its invocation
   */
  public Object call(Class cls, Object obj, String methodName, Object[] args, Class types[]) throws ReflectException, InvocationTargetException {
    
    if(args != null && types == null) {
      types = new Class[args.length];
      for(int i=0;i<args.length;i++) {
        types[i] = args[i].getClass();
      }
    }
    
    try {
      Method m = cls.getMethod(methodName, types);
      return m.invoke(obj, args);
    }
    catch(NullPointerException nullExp) {
      if(obj == null) {
        NoSuchMethodException noMethodExp = new NoSuchMethodException("Static method:"+methodName+" not Found for class:"+cls.getName());
        throw new ReflectException(noMethodExp);
      }
      else {
        throw nullExp;
      }
    }
    catch(IllegalAccessException accessExp) {
      throw new ReflectException(accessExp);
    }
    catch(IllegalArgumentException argExp) {
      throw new ReflectException(argExp);
    }
    catch(NoSuchMethodException noMethodExp) {
      throw new ReflectException(noMethodExp);
    }
  }
  
  /**
   * Call a Static Method on a Class
   * @param cls which contains the method
   * @param methodName
   * @param args values. 
   * @param types of arguments. These classes are the tpyes passed in the args. 
   *
   * @return Object
   */
  public Object callStatic(Class cls, String methodName, Object[] args, Class[] types) throws ReflectException, InvocationTargetException {
    return call(cls, null, methodName, args, types);
  }
  
  /**
   * Call a Static Method on a Class
   * @param cls which contains the method
   * @param methodName
   * @param args values. These values cannot be null or a NullPointerException will be thrown.
   * To pass null values as arguments use the callStatic(Class cls, String methodName, Object[] args, Class[] types) version
   * of this method.
   *
   * @return Object
   */
  public Object callStatic(Class cls, String methodName, Object[] args) throws ReflectException, InvocationTargetException {
    return call(cls, null, methodName, args);
  }
  
  /**
   * Call a Static Method on a Class with a Single Argument
   * @param cls which contains the method
   * @param methodName
   * @param arg Single Object argument this value can not be null or a NullPointerException will be thrown.
   * To pass a null value as an argument use the callStatic(Class cls, String methodName, Object arg, Class type) version
   * of this method
   *
   * @return Object
   */
  public Object callStatic(Class cls, String methodName, Object arg) throws ReflectException, InvocationTargetException {
    return call(cls, null, methodName, new Object[]{arg});
  }
  
  /**
   * Call a Static Method on a Class with a Single Argument
   * @param cls which contains the method
   * @param methodName
   * @param arg Single Object argument
   * @param type Class Type of the Argument
   *
   * @return Object
   */
  public Object callStatic(Class cls, String methodName, Object arg, Class type) throws ReflectException, InvocationTargetException {
    return call(cls, null, methodName, new Object[]{arg}, new Class[]{type});
  }
  
  /**
   * Call a Static Method on a Class with no Arguments
   * @param cls which contains the method
   * @param methodName
   * @return Object
   */
  public Object callStatic(Class cls, String methodName) throws ReflectException, InvocationTargetException  {
    return call(cls, null, methodName, (Object[])null);
  }
  
  /**
   * Call a Static Method on a Class with a single boolean Argument
   * @param cls which contains the method
   * @param methodName
   * @param value boolean argument
   * @return Object
   */
  public Object callStatic(Class cls, String methodName, boolean value) throws ReflectException, InvocationTargetException  {
    
    Class[] types = new Class[]{Boolean.TYPE};
    return call(cls, null, methodName, new Object[]{new Boolean(value)}, types);
  }
  
  /**
   * Call a Static Method on a Class with a single int Argument
   * @param cls which contains the method
   * @param methodName
   * @param value int argument
   * @return Object
   */
  public Object callStatic(Class cls, String methodName, int value) throws ReflectException, InvocationTargetException  {
    Class[] types = new Class[]{Integer.TYPE};
    return call(cls, null, methodName, new Object[]{new Integer(value)}, types);
  }
  
  /**
   * Call a Static Method on a Class with a single String Argument
   * @param cls which contains the method
   * @param methodName
   * @param value String argument
   * @return Object
   */
  public Object callStatic(Class cls, String methodName, String value) throws ReflectException, InvocationTargetException  {
    return call(cls, null, methodName, new Object[]{value}, new Class[]{String.class});
  }
    
  /**
   * Call a Method on a Object instance with a single boolean Argument
   * @param obj to call method on
   * @param methodName
   * @param value boolean argument
   * @return Object
   */
  public Object call(Object obj, String methodName, boolean value) throws ReflectException, InvocationTargetException  {
    if(obj == null) throw new ReflectException("obj cannot be null");
    
    Class[] types = new Class[]{Boolean.TYPE};
    return call(obj.getClass(), obj, methodName, new Object[]{new Boolean(value)}, types);
  }
  
  /**
   * Call a Method on a Object instance with a single int Argument
   * @param obj to call method on
   * @param methodName
   * @param value int argument
   * @return Object
   */
  public Object call(Object obj, String methodName, int value) throws ReflectException, InvocationTargetException  {
    if(obj == null) throw new ReflectException("obj cannot be null");
    
    Class[] types = new Class[]{Integer.TYPE};
    return call(obj.getClass(), obj, methodName, new Object[]{new Integer(value)}, types);
  }
  
  /**
   * Call a Method on a Object instance with a single String Argument
   * @param obj to call method on
   * @param methodName
   * @param value String argument
   * @return Object
   */
  public Object call(Object obj, String methodName, String value) throws ReflectException, InvocationTargetException  {
    if(obj == null) throw new ReflectException("obj cannot be null");
    return call(obj.getClass(), obj, methodName, new Object[]{value}, new Class[]{String.class});
  }
  
  /**
   * Call a Method on a Object instance 
   * @param obj to call method on
   * @param methodName
   * @param args Array of Object arguments. The values in the args array cannot be null or a NullPointerException will be thrown.
   * To pass null values in the array use the call(Object obj, String methodName, Object[] args, Class[] types) version
   * of this method.
   *
   * @return Object
   */
  public Object call(Object obj, String methodName, Object[] args) throws ReflectException, InvocationTargetException  {
    if(obj == null) throw new ReflectException("obj cannot be null");
    return call(obj.getClass(), obj, methodName, args);
  }
  
  /**
   * Call a Method on a Object instance 
   * @param obj to call method on
   * @param methodName
   * @param args Array of Object arguments
   * @param types Array of Class types of the Arguments
   *
   * @return Object
   */
  public Object call(Object obj, String methodName, Object[] args, Class[] types) throws ReflectException, InvocationTargetException  {
    if(obj == null) throw new ReflectException("obj cannot be null");
    return call(obj.getClass(), obj, methodName, args, types);
  }
  
  /**
   * Call a Method on a Object instance 
   * @param obj to call the method on
   * @param methodName
   * @param arg Single Object argument this value can not be null or a NullPointerException will be thrown.
   * To pass a null value as an argument use the call(Object obj, String methodName, Object arg, Class type) version
   * of this method
   *
   * @return Object
   */
  public Object call(Object obj, String methodName, Object arg) throws ReflectException, InvocationTargetException {
    if(obj == null) throw new ReflectException("obj cannot be null");
    return call(obj.getClass(), obj, methodName, new Object[]{arg});
  }
  
  /**
   * Call a Method on a Object instance 
   * @param obj to call the method on
   * @param methodName
   * @param arg Single Object argument or Null
   * @param type Class type of the argument
   *
   * @return Object
   */
  public Object call(Object obj, String methodName, Object arg, Class type) throws ReflectException, InvocationTargetException {
    if(obj == null) throw new ReflectException("obj cannot be null");
    return call(obj.getClass(), obj, methodName, new Object[]{arg}, new Class[]{type});
  }
  
  /**
   * Call a Method on a Object instance with no Arguments
   * @param obj to call method on
   * @param methodName
   * @return Object
   */
  public Object call(Object obj, String methodName) throws ReflectException, InvocationTargetException  {
    if(obj == null) throw new ReflectException("obj cannot be null");
    return call(obj.getClass(), obj, methodName, null);
  }
  
  //******************************
  // boolean Methods
  //******************************
  
  /**
   * Call a Method that returns a boolean
   * @param obj instance which contains the method
   * @param methodName
   * @param args Array of Object arguments
   * @param types Class types of Arguments
   * @return boolean
   */
  public boolean callBoolean(Object obj, String methodName, Object[] args, Class[] types) throws ReflectException, InvocationTargetException {
    
    try {
      Boolean b = (Boolean)call(obj.getClass(), obj, methodName, args, types);
      if(b != null) return b.booleanValue();
      else return false;
    }
    catch(ClassCastException exp) {
      NoSuchMethodException noMethodExp = new NoSuchMethodException("method:"+methodName+" which returns boolean not Found for class:"+obj.getClass().getName());
      throw new ReflectException(noMethodExp);
    }
  }
  
  /**
   * Call a Method that returns a boolean
   * @param obj instance which contains the method
   * @param methodName
   * @param args Array of Object arguments no values can be null
   * @return boolean
   */
  public boolean callBoolean(Object obj, String methodName, Object[] args) throws ReflectException, InvocationTargetException {
    
    return callBoolean(obj, methodName, args, getTypes(args));
  }
  
  /**
   * Call a Method that returns a boolean
   * @param obj instance which contains the method
   * @param methodName
   * @param arg single Object argument or null
   * @param type Class type of the Argument
   * @return boolean
   */
  public boolean callBoolean(Object obj, String methodName, Object arg, Class type) throws ReflectException, InvocationTargetException {
   
    return callBoolean(obj, methodName, new Object[]{obj}, new Class[]{type});
  }
  
  /**
   * Call a Method that returns a boolean
   * @param obj instance which contains the method
   * @param methodName
   * @param arg single Object argument cannot be null
   * @return boolean
   */
  public boolean callBoolean(Object obj, String methodName, Object arg) throws ReflectException, InvocationTargetException {
   
    return callBoolean(obj, methodName, new Object[]{obj}, new Class[]{obj.getClass()});
  }
  
  /**
   * Call a Method that returns a boolean with no arguments
   * @param obj instance which contains the method
   * @param methodName
   * @return boolean
   */
  public boolean callBoolean(Object obj, String methodName) throws ReflectException, InvocationTargetException {
    
    return callBoolean(obj, methodName, (Object[])null, (Class[])null);
  }
  
  /**
   * Call a Static Method that returns a boolean
   * @param cls which contains the method
   * @param methodName
   * @param args values. 
   * @param types of the arguments
   *
   * @return boolean
   */
  public boolean callStaticBoolean(Class cls, String methodName, Object[] args, Class[] types) throws ReflectException, InvocationTargetException {
    try {
      Boolean b = (Boolean)call(cls, null, methodName, args, types);
      if(b != null) return b.booleanValue();
      else return false;
    }
    catch(ClassCastException exp) {
      NoSuchMethodException noMethodExp = new NoSuchMethodException("method:"+methodName+" which returns boolean not Found for class:"+cls.getName());
      throw new ReflectException(noMethodExp);
    }
  }
  
  /**
   * Call a Static Method that returns a boolean
   * @param cls which contains the method
   * @param methodName
   * @param args values. These values cannot be null
   *
   * @return boolean
   */
  public boolean callStaticBoolean(Class cls, String methodName, Object[] args) throws ReflectException, InvocationTargetException {
    return callStaticBoolean(cls, methodName, args, getTypes(args));
  }
  
  /**
   * Call a Static Method that returns a boolean
   * @param cls which contains the method
   * @param methodName
   * @param arg value or null.
   * @param type Class type of argument
   *
   * @return boolean
   */
  public boolean callStaticBoolean(Class cls, String methodName, Object arg, Class type) throws ReflectException, InvocationTargetException {
    return callStaticBoolean(cls, methodName, new Object[]{arg}, new Class[]{type});
  }
  
  /**
   * Call a Static Method that returns a boolean
   * @param cls which contains the method
   * @param methodName
   * @param arg value cannot be null.
   *
   * @return boolean
   */
  public boolean callStaticBoolean(Class cls, String methodName, Object arg) throws ReflectException, InvocationTargetException {
    return callStaticBoolean(cls, methodName, new Object[]{arg}, new Class[]{arg.getClass()});
  }
  
  /**
   * Call a Static Method that returns a boolean
   * @param cls which contains the method
   * @param methodName
   *
   * @return boolean
   */
  public boolean callStaticBoolean(Class cls, String methodName) throws ReflectException, InvocationTargetException {
    return callStaticBoolean(cls, methodName, (Object[])null, (Class[])null);
  }
  
  //******************************
  // Int Methods
  //******************************
  
  /**
   * Call a Method that returns an int
   * @param obj instance which contains the method
   * @param methodName
   * @param args Array of Object arguments
   * @param types Class types of Arguments
   *
   * @return int
   */
  public int callInt(Object obj, String methodName, Object[] args, Class[] types) throws ReflectException, InvocationTargetException {
    
    try {
      Integer i = (Integer)call(obj.getClass(), obj, methodName, args, types);
      if(i != null) return i.intValue();
      else return 0;
    }
    catch(ClassCastException exp) {
      NoSuchMethodException noMethodExp = new NoSuchMethodException("method:"+methodName+" which returns boolean not Found for class:"+obj.getClass().getName());
      throw new ReflectException(noMethodExp);
    }
  }
  
  /**
   * Call a Method that returns an int
   * @param obj instance which contains the method
   * @param methodName
   * @param args Array of Object arguments no values can be null
   *
   * @return int
   */
  public int callInt(Object obj, String methodName, Object[] args) throws ReflectException, InvocationTargetException {
    
    return callInt(obj, methodName, args, getTypes(args));
  }
  
  /**
   * Call a Method that returns an int
   * @param obj instance which contains the method
   * @param methodName
   * @param arg single Object argument or null
   * @param type Class type of the Argument
   *
   * @return int
   */
  public int callInt(Object obj, String methodName, Object arg, Class type) throws ReflectException, InvocationTargetException {
   
    return callInt(obj, methodName, new Object[]{obj}, new Class[]{type});
  }
  
  /**
   * Call a Method that returns an int
   * @param obj instance which contains the method
   * @param methodName
   * @param arg single Object argument cannot be null
   *
   * @return int
   */
  public int callInt(Object obj, String methodName, Object arg) throws ReflectException, InvocationTargetException {
   
    return callInt(obj, methodName, new Object[]{obj}, new Class[]{obj.getClass()});
  }
  
  /**
   * Call a Method that returns an int with no arguments
   * @param obj instance which contains the method
   * @param methodName
   *
   * @return int
   */
  public int callInt(Object obj, String methodName) throws ReflectException, InvocationTargetException {
    
    return callInt(obj, methodName, (Object[])null, (Class[])null);
  }
  
  /**
   * Call a Static Method that returns an int
   * @param cls which contains the method
   * @param methodName
   * @param args values. 
   * @param types The Class Types of the arguments
   *
   * @return int
   */
  public int callStaticInt(Class cls, String methodName, Object[] args, Class[] types) throws ReflectException, InvocationTargetException {
    try {
      Integer i = (Integer)call(cls, null, methodName, args, types);
      if(i != null) return i.intValue();
      else return 0;
    }
    catch(ClassCastException exp) {
      NoSuchMethodException noMethodExp = new NoSuchMethodException("method:"+methodName+" which returns boolean not Found for class:"+cls.getName());
      throw new ReflectException(noMethodExp);
    }
  }
  
  /**
   * Call a Static Method that returns an int
   * @param cls which contains the method
   * @param methodName
   * @param args values. These values cannot be null
   *
   * @return int
   */
  public int callStaticInt(Class cls, String methodName, Object[] args) throws ReflectException, InvocationTargetException {
    return callStaticInt(cls, methodName, args, getTypes(args));
  }
  
  /**
   * Call a Static Method that returns an int
   * @param cls which contains the method
   * @param methodName
   * @param arg value or null.
   * @param type Class type of argument
   *
   * @return int
   */
  public int callStaticInt(Class cls, String methodName, Object arg, Class type) throws ReflectException, InvocationTargetException {
    return callStaticInt(cls, methodName, new Object[]{arg}, new Class[]{type});
  }
  
  /**
   * Call a Static Method that returns an int
   * @param cls which contains the method
   * @param methodName
   * @param arg value cannot be null.
   *
   * @return int
   */
  public int callStaticInt(Class cls, String methodName, Object arg) throws ReflectException, InvocationTargetException {
    return callStaticInt(cls, methodName, new Object[]{arg}, new Class[]{arg.getClass()});
  }
  
  /**
   * Call a Static Method that returns an int
   * @param cls which contains the method
   * @param methodName
   *
   * @return int
   */
  public int callStaticInt(Class cls, String methodName) throws ReflectException, InvocationTargetException {
    return callStaticInt(cls, methodName, (Object[])null, (Class[])null);
  }
  
  //******************************
  // String Methods
  //******************************
  
  /**
   * Call a Method that returns a String
   * @param obj instance which contains the method
   * @param methodName
   * @param args Array of Object arguments
   * @param types Class types of Arguments
   *
   * @return String
   */
  public String callString(Object obj, String methodName, Object[] args, Class[] types) throws ReflectException, InvocationTargetException {
    
    try {
      return (String)call(obj.getClass(), obj, methodName, args, types);
    }
    catch(ClassCastException exp) {
      NoSuchMethodException noMethodExp = new NoSuchMethodException("method:"+methodName+" which returns boolean not Found for class:"+obj.getClass().getName());
      throw new ReflectException(noMethodExp);
    }
  }
  
  /**
   * Call a Method that returns a String
   * @param obj instance which contains the method
   * @param methodName
   * @param args Array of Object arguments no values can be null
   *
   * @return String
   */
  public String callString(Object obj, String methodName, Object[] args) throws ReflectException, InvocationTargetException {
    
    return callString(obj, methodName, args, getTypes(args));
  }
  
  /**
   * Call a Method that returns a String
   * @param obj instance which contains the method
   * @param methodName
   * @param arg single Object argument or null
   * @param type Class type of the Argument
   *
   * @return String
   */
  public String callString(Object obj, String methodName, Object arg, Class type) throws ReflectException, InvocationTargetException {
   
    return callString(obj, methodName, new Object[]{obj}, new Class[]{type});
  }
  
  /**
   * Call a Method that returns a String
   * @param obj instance which contains the method
   * @param methodName
   * @param arg single Object argument cannot be null
   *
   * @return String
   */
  public String callString(Object obj, String methodName, Object arg) throws ReflectException, InvocationTargetException {
   
    return callString(obj, methodName, new Object[]{obj}, new Class[]{obj.getClass()});
  }
  
  /**
   * Call a Method that returns an String with no arguments
   * @param obj instance which contains the method
   * @param methodName
   *
   * @return String
   */
  public String callString(Object obj, String methodName) throws ReflectException, InvocationTargetException {
    
    return callString(obj, methodName, (Object[])null, (Class[])null);
  }
  
  /**
   * Call a Static Method that returns a String
   * @param cls which contains the method
   * @param methodName
   * @param args values. 
   * @param types The Class Types of the arguments
   *
   * @return String
   */
  public String callStaticString(Class cls, String methodName, Object[] args, Class[] types) throws ReflectException, InvocationTargetException {
    try {
      return (String)call(cls, null, methodName, args, types);
    }
    catch(ClassCastException exp) {
      NoSuchMethodException noMethodExp = new NoSuchMethodException("method:"+methodName+" which returns boolean not Found for class:"+cls.getName());
      throw new ReflectException(noMethodExp);
    }
  }
  
  /**
   * Call a Static Method that returns a String
   * @param cls which contains the method
   * @param methodName
   * @param args values. These values cannot be null
   *
   * @return String
   */
  public String callStaticString(Class cls, String methodName, Object[] args) throws ReflectException, InvocationTargetException {
    return callStaticString(cls, methodName, args, getTypes(args));
  }
  
  /**
   * Call a Static Method that returns a String
   * @param cls which contains the method
   * @param methodName
   * @param arg value or null.
   * @param type Class type of argument
   *
   * @return String
   */
  public String callStaticString(Class cls, String methodName, Object arg, Class type) throws ReflectException, InvocationTargetException {
    return callStaticString(cls, methodName, new Object[]{arg}, new Class[]{type});
  }
  
  /**
   * Call a Static Method that returns a String
   * @param cls which contains the method
   * @param methodName
   * @param arg value cannot be null.
   *
   * @return String
   */
  public String callStaticString(Class cls, String methodName, Object arg) throws ReflectException, InvocationTargetException {
    return callStaticString(cls, methodName, new Object[]{arg}, new Class[]{arg.getClass()});
  }
  
  /**
   * Call a Static Method that returns a String
   * @param cls which contains the method
   * @param methodName
   *
   * @return String
   */
  public String callStaticString(Class cls, String methodName) throws ReflectException, InvocationTargetException {
    return callStaticString(cls, methodName, (Object[])null, (Class[])null);
  }
  
  //*************************
  // Has Methods
  //*************************
  
  /**
   * Return true if the obj has a method named methodName with no parameters
   * @param obj to check
   * @param methodName name of method
   */
  public boolean hasMethod(Object obj, String methodName) {
     if(obj == null) return false;
     Class<?> cls = obj.getClass();
     
     return hasMethod(cls, methodName, new Class<?>[]{});
     
  }
  
  /**
   * Return true if the class has a method named methodName with specified type paramters
   * @param cls class to check 
   * @param methodName name of method
   * @param types parameter types of methods
   * @return
   */
  public boolean hasMethod(Class<?> cls, String methodName, Class<?>[] types) {
     if(cls == null) return false;
     
     try {
        cls.getMethod(methodName, new Class[]{});
        return true;
     }
     catch(Exception exp) {
        return false;
     }
  }
      
}
