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

package org.xito.launcher.jnlp.xml;

import java.io.*;
import java.net.*;
import java.util.logging.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 *
 * @author DRICHAN
 */
public class JNLPParser {
   
   private static final Logger logger = Logger.getLogger(JNLPParser.class.getName());
   
   private DocumentBuilderFactory builderFactory;
   private DocumentBuilder builder;
   
   private JNLPNode jnlpNode;
   
   /** Creates a new instance of JNLPParser */
   public JNLPParser() {
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
    * Parse a JNLP URL
    */
   public JNLPNode parse(URL jnlpURL) throws InvalidJNLPException, IOException {
      
      if(jnlpURL == null) throw new InvalidJNLPException("JNLP URL not specified");
      try {
         logger.info("Parsing JNLP:"+jnlpURL.toString());
         Document doc = builder.parse(jnlpURL.openStream());
         return new JNLPNode(jnlpURL, doc.getDocumentElement());
      }
      catch(SAXException saxExp) {
         throw new IOException(saxExp.getMessage());
      }
         
   }
   
   /** 
    * Parse an XML Element
    */
   public JNLPNode parse(URL contextURL, Element node) throws InvalidJNLPException {
      
      if(node == null) throw new InvalidJNLPException("JNLP node not specified");
      if(contextURL == null) throw new InvalidJNLPException("Context URL not specified");
      
      return new JNLPNode(contextURL, node);
   }
}
