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

import java.util.logging.*;
import java.io.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import org.xml.sax.*;


/**
 *
 * @author $Author: drichan $
 * @author ocd_dino - ocd_dino@users.sourceforge.net (initial author)
 * @version $Revision: 1.9 $
 * @since $Date: 2007/09/02 00:42:59 $
 */
public class BLXUtility {

   private static Logger logger = Logger.getLogger(BLXUtility.class.getName());
   private static DocumentBuilderFactory builderFactory;
   private static DocumentBuilder docBuilder;   
   private static TransformerFactory transformerFactory;
   private static Transformer transformer;
   
   static {
      try {
         builderFactory = DocumentBuilderFactory.newInstance();
         docBuilder = builderFactory.newDocumentBuilder();
         transformerFactory = TransformerFactory.newInstance();
         transformer = transformerFactory.newTransformer();
      }
      catch(Exception exp) {
         logger.log(Level.SEVERE, exp.toString(), exp);
      }
   }
   
   /**
    * Helper Method to easily create a
    * DOM Document
    */
   public static org.w3c.dom.Document createDOMDocument() {
      
      return docBuilder.newDocument();
   }
   
   /**
    * Helper Method to easily create a new DOMParser
    */
   public static DocumentBuilder createDOMParser() {
      //Return a DOM Parser
      try {
         return builderFactory.newDocumentBuilder();
      }
      catch(Exception exp) {
         logger.log(Level.SEVERE, exp.toString(), exp);
      }
      
      return null;
   }
   
   /**
    * Creates an Empty BLX Document with a root blx:document node
    * @return Document
    */
   public static org.w3c.dom.Document createBLXDocument() {
      
      Document document = BLXUtility.createDOMDocument();
      Element root = document.createElementNS("blx", BLXElement.BLX_NS+":"+BLXElement.BLX_DOCUMENT_NAME);
      root.setAttribute("xmlns:"+BLXElement.BLX_NS,BLXElement.BLX_NS);
      
      document.appendChild(root);
      
      return document;
   }
   
   /**
    * Write DOM Document to Output Stream
    * @param document to write out
    * @param stream to write Output to.
    * @throws IOException if there is a problem writing to the stream
    */
   public static void writeDocument(Document pDoc, OutputStream pStream) throws IOException {
      
      try {
         transformer.transform(new DOMSource(pDoc.getDocumentElement()), new StreamResult(pStream));
         pStream.flush();
      }
      catch(TransformerException exp) {
         logger.log(Level.SEVERE, exp.toString(), exp);
         throw new IOException(exp.getMessage());
      }
      finally {
         //transformer.reset();
      }
      
      //XMLSerializer _s = new org.apache.xml.serialize.XMLSerializer(pStream, new OutputFormat(pDoc, "UTF-8", true));
      //_s.serialize(pDoc);
   }
   
   /**
    * Write DOM Document to a Writer
    * @param document to write out
    * @param writer to write Output to.
    * @throws IOException if there is a problem writing to the stream
    */
   public static void writeDocument(Document pDoc, Writer writer) throws IOException {
      
      try {
         transformer.transform(new DOMSource(pDoc.getDocumentElement()), new StreamResult(writer));
         writer.flush();
      }
      catch(TransformerException exp) {
         logger.log(Level.SEVERE, exp.toString(), exp);
         throw new IOException(exp.getMessage());
      }
      finally {
         //transformer.reset();
      }
      
      //XMLSerializer _s = new org.apache.xml.serialize.XMLSerializer(writer, new OutputFormat(pDoc, "UTF-8", true));
      //_s.serialize(pDoc);
   }
   
   /**
    * Convert the BLXObjects entire Structure to an Element
    * @param obj
    * @return Element
    */
   public static Element getCompleteBLXElement(BLXObject obj) {
      Element blxE = obj.getBLXElement().getDOMElement();
      Element dataE = (Element)blxE.getOwnerDocument().importNode(obj.getDataElement(), true);
            
      return (Element)blxE.appendChild(dataE);
   }
   
   /**
    * Write the XML contents of a BLXObject to an output stream
    * @param obj
    * @return stream to write to
    * @throws IOException if there is a problem writing to the stream
    */
   public static void writeBLXObject(BLXObject obj, OutputStream stream) throws IOException {
      
      Document doc = createBLXDocument();
      Element blxE = obj.getBLXElement().getDOMElement();
      blxE = (Element)doc.importNode(blxE, true);
      doc.getDocumentElement().appendChild(blxE);
      
      //Data
      Element dataE = obj.getDataElement();
      if(dataE != null) {
         dataE = (Element)doc.importNode(obj.getDataElement(), true);
         blxE.appendChild(dataE);
      }
                  
      writeDocument(doc, stream);
   }
   
   /**
    * Write the XML contents of a BLXObject to a writer
    * @param obj
    * @return writer to write to
    * @throws IOException if there is a problem writing to the stream
    */
   public static void writeBLXObject(BLXObject obj, Writer writer) throws IOException {
      
      Document doc = createBLXDocument();
      Element blxE = obj.getBLXElement().getDOMElement();
      blxE = (Element)doc.importNode(blxE, true);
      doc.getDocumentElement().appendChild(blxE);
      
      //Data
      Element dataE = obj.getDataElement();
      if(dataE != null) {
         dataE = (Element)doc.importNode(obj.getDataElement(), true);
         blxE.appendChild(dataE);
      }
                  
      writeDocument(doc, writer);
   }
   
   /**
    * Makes a copy of a BLX object by first writing out the src BLX XML
    * and then feeding that XML to the CompFactory to create a new Object
    * @param src object
    * @return copy 
    */
   public static BLXObject copyBLXObject(BLXObject src) {
      return null;
   }
   
   /**
    * Returns the local node name of a node name that contains a namespace prefix.
    */
   public static String getLocalNodeName(Node e) {
      if(e == null) return null;
      
      String name = e.getNodeName();
      if(name == null) return null;
      int i = name.indexOf(':');
      if(i == -1) return name;
      
      return name.substring(i+1);
   }
}




