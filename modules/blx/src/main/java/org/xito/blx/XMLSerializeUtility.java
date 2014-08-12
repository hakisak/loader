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

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.4 $
 * @since $Date: 2007/09/02 00:43:00 $
 */
public class XMLSerializeUtility {

  public static final String NODE_NAME = "javaobject";

  private static DocumentBuilder docBuilder;

  /** Creates new XMLSerializeUtility */
  public XMLSerializeUtility() {
    
  }

  public static Node getSerializedObjectNode(Object pObject) {
    byte _data[] = null;

    //Create an Object OutputStream
    try {
      ByteArrayOutputStream _byte_out = new ByteArrayOutputStream();
      ObjectOutputStream _out = new ObjectOutputStream(_byte_out);
      _out.writeObject(pObject);

      _data = _byte_out.toByteArray();
      _out.close();
      _byte_out.close();
    }
    catch(IOException _exp) {
      _data = null;
    }

    //Create Element
    Document _doc = docBuilder.newDocument();
    Element _element = _doc.createElement(NODE_NAME);

    CDATASection _cdata = _doc.createCDATASection(new String(_data));
    _element.appendChild(_cdata);

    return _element;
  }

  public static Object getObjectFromSerializedNode(Node pNode, ClassLoader pLoader) {
    //Get the Data
    byte _data[] = null;
    if(pNode.getNodeName() == NODE_NAME) {
      NodeList _list = pNode.getChildNodes();
      for(int i=0;i<_list.getLength();i++) {
        Node _child = _list.item(i);
        if(_child.getNodeType() == Node.CDATA_SECTION_NODE) {
          _data = pNode.getNodeValue().getBytes();
          break;
        }
      }
    }

    if(_data == null) return null;

    try {
      MyReadThread _thread = new MyReadThread(_data);
      _thread.setContextClassLoader(pLoader);
      _thread.start();
      _thread.join();

      return _thread.getObject();
    }
    catch(InterruptedException _exp) {
      _exp.printStackTrace();
    }

    return null;
  }

  static class MyReadThread extends Thread {
    Exception exp;
    Object obj;
    byte data[];

    public MyReadThread(byte[] pData) {
      data = pData;
      if(pData == null) throw new NullPointerException("Data can not be null");
    }

    public void run() {
      try {
        ByteArrayInputStream _byte_in = new ByteArrayInputStream(data);
        ObjectInputStream _in = new ObjectInputStream(_byte_in);
        obj = _in.readObject();
      }
      catch(Exception _exp) {
        exp = _exp;
        obj = null;
      }
    }

    public Exception getException() {
      return exp;
    }

    public Object getObject() {
      return obj;
    }
  }

}
