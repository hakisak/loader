package org.xito.httpservice.gentool;

import java.io.IOException;
import java.io.Writer;

import org.xito.httpservice.HttpServiceServlet;



public class WebXMLGenerator {
   
   private ServiceDescriptor[] srvDescriptors;
   private Writer out;
   
   public WebXMLGenerator(ServiceDescriptor srvDescriptors[], Writer writer) {
      this.srvDescriptors = srvDescriptors;
      this.out = writer;
   }
   
   public void generate() throws IOException {
      
      generateStart();
      StringBuffer serviceNames = new StringBuffer();
      for(int i=0;i<srvDescriptors.length;i++) {
         serviceNames.append(srvDescriptors[i].getInterfaceName());
         if(i<srvDescriptors.length-1) serviceNames.append(",");
         
         generateServletService(srvDescriptors[i]);
      }
      
      generateManagerServlet(serviceNames.toString());
      
      generateEnd();
   }
   
   private void generateStart() throws IOException {
      
      out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n");
      out.write("<!DOCTYPE web-app PUBLIC \"-//Sun Microsystems, Inc.//DTD WebApplication 2.3//EN\" " +
            "\"http://java.sun.com/dtd/web-app_2_3.dtd\">\n\n");
      
      out.write("<web-app>\n");
      out.write("   <display-name>test</display-name>\n\n");
   }
   
   private void generateManagerServlet(String serviceNames) throws IOException {
      
      out.write("<!-- Config for Manager -->\n");
      out.write("<!-- ********************************************  -->\n");
      out.write("<servlet>\n");
      out.write("   <servlet-name>ServiceManagerServlet</servlet-name>\n");
      out.write("   <display-name>ServiceManager</display-name>\n");
      out.write("   <servlet-class>org.xito.httpservice.HttpServiceManagerServlet</servlet-class>\n");
      out.write("   <init-param>\n");
      out.write("      <param-name>service_names</param-name>\n");
      out.write("      <param-value>" + serviceNames + "</param-value>\n");
      out.write("   </init-param>\n");
      out.write("</servlet>\n\n");
      
      out.write("<servlet-mapping>\n");
      out.write("   <servlet-name>ServiceManagerServlet</servlet-name>\n");
      out.write("   <url-pattern>/service_manager</url-pattern>\n");
      out.write("</servlet-mapping>\n\n");
   }
   
   private void generateServletService(ServiceDescriptor srvDesc) throws IOException {
  
      out.write("<!-- Config for " + srvDesc.getInterfaceName() + " -->\n");
      out.write("<!-- ********************************************  -->\n");
      out.write("<servlet>\n");
      out.write("   <servlet-name>" + srvDesc.getInterfaceName() + "Servlet</servlet-name>\n");
      out.write("   <display-name>" + srvDesc.getInterfaceName() + "</display-name>\n");
      out.write("   <servlet-class>org.xito.httpservice.HttpServiceServlet</servlet-class>\n");
      out.write("   <init-param>\n");
      out.write("      <param-name>" + HttpServiceServlet.INTERFACE_NAME + "</param-name>\n");
      out.write("      <param-value>" + srvDesc.getInterfaceName() + "</param-value>\n");
      out.write("   </init-param>\n");
      out.write("   <init-param>\n");
      out.write("      <param-name>" + HttpServiceServlet.IMPL_NAME + "</param-name>\n");
      out.write("      <param-value>" + srvDesc.getImplName() + "</param-value>\n");
      out.write("   </init-param>\n");
      out.write("</servlet>\n\n");
      
      out.write("<servlet-mapping>\n");
      out.write("   <servlet-name>" + srvDesc.getInterfaceName() + "Servlet</servlet-name>\n");
      out.write("   <url-pattern>/xito_service/" + srvDesc.getInterfaceName() + "</url-pattern>\n");
      out.write("</servlet-mapping>\n\n");
      
      /*
      <servlet>
      <servlet-name>AxisServlet</servlet-name>
      <display-name>Apache-Axis Servlet</display-name>
      <servlet-class>
          org.apache.axis.transport.http.AxisServlet
      </servlet-class>
    </servlet>
    
    <servlet-mapping>
    <servlet-name>AxisServlet</servlet-name>
    <url-pattern>/servlet/AxisServlet</url-pattern>
  </servlet-mapping>
  
    */
      
   }
   
   private void generateEnd() throws IOException {
      
      out.write("</web-app>");
   }
   
}



/*
 
 
 <?xml version="1.0" encoding="ISO-8859-1"?>

<!DOCTYPE web-app PUBLIC "-//Sun Microsystems, Inc.//DTD Web
Application 2.3//EN" "http://java.sun.com/dtd/web-app_2_3.dtd">

<web-app>
  <display-name>Apache-Axis</display-name>
    
    <listener>
        <listener-class>org.apache.axis.transport.http.AxisHTTPSessionListener</listener-class>
    </listener>
    
  <servlet>
    <servlet-name>AxisServlet</servlet-name>
    <display-name>Apache-Axis Servlet</display-name>
    <servlet-class>
        org.apache.axis.transport.http.AxisServlet
    </servlet-class>
  </servlet>

  <servlet>
    <servlet-name>AdminServlet</servlet-name>
    <display-name>Axis Admin Servlet</display-name>
    <servlet-class>
        org.apache.axis.transport.http.AdminServlet
    </servlet-class>
    <load-on-startup>100</load-on-startup>
  </servlet>

  <servlet>
    <servlet-name>SOAPMonitorService</servlet-name>
    <display-name>SOAPMonitorService</display-name>
    <servlet-class>
        org.apache.axis.monitor.SOAPMonitorService
    </servlet-class>
    <init-param>
      <param-name>SOAPMonitorPort</param-name>
      <param-value>5001</param-value>
    </init-param>
    <load-on-startup>100</load-on-startup>
  </servlet>

  <servlet-mapping>
    <servlet-name>AxisServlet</servlet-name>
    <url-pattern>/servlet/AxisServlet</url-pattern>
  </servlet-mapping>

  <servlet-mapping>
    <servlet-name>AxisServlet</servlet-name>
    <url-pattern>*.jws</url-pattern>
  </servlet-mapping>

  <servlet-mapping>
    <servlet-name>AxisServlet</servlet-name>
    <url-pattern>/services/*</url-pattern>
  </servlet-mapping>

  <servlet-mapping>
    <servlet-name>SOAPMonitorService</servlet-name>
    <url-pattern>/SOAPMonitor</url-pattern>
  </servlet-mapping>

 <!-- uncomment this if you want the admin servlet -->
 <!--
  <servlet-mapping>
    <servlet-name>AdminServlet</servlet-name>
    <url-pattern>/servlet/AdminServlet</url-pattern>
  </servlet-mapping>
 -->

    <session-config>
        <!-- Default to 5 minute session timeouts -->
        <session-timeout>5</session-timeout>
    </session-config>

    <!-- currently the W3C havent settled on a media type for WSDL;
    http://www.w3.org/TR/2003/WD-wsdl12-20030303/#ietf-draft
    for now we go with the basic 'it's XML' response -->
  <mime-mapping>
    <extension>wsdl</extension>
     <mime-type>text/xml</mime-type>
  </mime-mapping>
  

  <mime-mapping>
    <extension>xsd</extension>
    <mime-type>text/xml</mime-type>
  </mime-mapping>

  <welcome-file-list id="WelcomeFileList">
    <welcome-file>index.jsp</welcome-file>
    <welcome-file>index.html</welcome-file>
    <welcome-file>index.jws</welcome-file>
  </welcome-file-list>

</web-app>

*/
