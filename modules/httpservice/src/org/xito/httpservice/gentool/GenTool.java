package org.xito.httpservice.gentool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GenTool {
   
   static DocumentBuilderFactory docBuilderFactory;
   static DocumentBuilder docBuilder;
   
   
   public static void main(String[] args) {
      
      String deploymentFileName = args[0];
      File deploymentFile = new File(deploymentFileName);
      
      try {
         docBuilderFactory = DocumentBuilderFactory.newInstance();
         docBuilder = docBuilderFactory.newDocumentBuilder();
         
         ServiceDescriptor srvDescriptors[] = getServiceDescriptors(deploymentFile);
         
         //generate web.xml
         generateWebXML(srvDescriptors);
      }
      catch(Exception exp) {
         exp.printStackTrace();
      }
   }
   
   private static void generateWebXML(ServiceDescriptor srvDescriptors[]) throws GenerateException {
      
      try {
         File genDir = getGenServerDir();
         File webINFDir = new File(genDir, "WEB-INF");
         if(!webINFDir.exists() && !webINFDir.mkdirs()) {
            throw new GenerateException("could not create WEB-INF directory:" + webINFDir.getAbsolutePath());
         }
         
         File webXML = new File(webINFDir, "web.xml");
         webXML.delete();
         if(!webXML.createNewFile()) {
            throw new GenerateException("could not create web.xml:" + webXML.getAbsolutePath());
         }
         
         FileWriter writer = new FileWriter(webXML);
         
         WebXMLGenerator webXMLGenerator = new WebXMLGenerator(srvDescriptors, writer);
         webXMLGenerator.generate();
         
         writer.close();
      }
      catch(Exception exp) {
         exp.printStackTrace();
      }
   }
   
   private static ServiceDescriptor[] getServiceDescriptors(File deploymentFile) throws IOException, ServiceParseException {
            
      try {
          Document doc = docBuilder.parse(deploymentFile);
          Element servicesElement = doc.getDocumentElement();
            
          return getServiceDescriptors(servicesElement);
      }
      catch(SAXException exp) {
         throw new ServiceParseException(exp.getMessage(), exp);
      }
   }
   
   private static ServiceDescriptor[] getServiceDescriptors(Element servicesElement) throws ServiceParseException {
   
      if(servicesElement == null) {
         throw new ServiceParseException("services element is null");
      }
      
      //check name of element
      if(!servicesElement.getNodeName().equals("services")) {
         throw new ServiceParseException("element is not a services elementl");
      }
      
      ArrayList descriptors = new ArrayList();
      NodeList descriptorNodes = servicesElement.getElementsByTagName("descriptor");
      for(int i=0;i<descriptorNodes.getLength();i++) {
         Node descNode = descriptorNodes.item(i);
         descriptors.add(getServiceDescriptor((Element)descNode));
      }
      
      return (ServiceDescriptor[]) descriptors.toArray(new ServiceDescriptor[0]);
   }
   
   private static ServiceDescriptor getServiceDescriptor(Element descElement) throws ServiceParseException {
      
      ServiceDescriptor desc = new ServiceDescriptor();
      
      NodeList childNodes = descElement.getChildNodes();
      for(int i=0;i<childNodes.getLength();i++) {
         Node node = childNodes.item(i);
         if(node.getNodeType() == Node.ELEMENT_NODE) {
            if(((Element)node).getNodeName().equals("interface")) {
               Node textNode = node.getFirstChild();
               if(textNode != null) desc.setInterfaceName(textNode.getNodeValue());
            }
            
            if(((Element)node).getNodeName().equals("impl")) {
               Node textNode = node.getFirstChild();
               if(textNode != null) desc.setImplName(textNode.getNodeValue());
            }
         }
      }
      
      if(desc.getInterfaceName() != null) {
         return desc;
      }
      else {
         throw new ServiceParseException("service descriptor element does not contain interface name");
      }
   }
   
   private static File getGenServerDir() {
      
      //generated directory
      File genDir = new File("generated/server");
      genDir.mkdirs();
      
      return genDir;
   }
   
}
