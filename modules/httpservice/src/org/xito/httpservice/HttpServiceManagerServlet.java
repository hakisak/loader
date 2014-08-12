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

package org.xito.httpservice;

import java.io.IOException;
import java.io.Writer;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpServiceManagerServlet extends HttpServlet {

   private String serviceNames[];
   
   public void init(ServletConfig config) throws ServletException {
      String serviceNamesStr = config.getInitParameter("service_names");
      serviceNames = serviceNamesStr.split(",");
   }

   protected void doGet(HttpServletRequest reg, HttpServletResponse resp) throws ServletException, IOException {
      
      resp.setContentType("text/html");
      Writer out = resp.getWriter();
      
      out.write("<html><body>\n");
      out.write("<h2>Service Manager</h2>\n");
      out.write("<hr>\n\n");
      
      writeServices(out);
      
      out.write("</body></html>");
   }
   
   protected void writeServices(Writer out) throws IOException {
      
      for(int i=0;i<serviceNames.length;i++) {
         writerServiceInfo(out, serviceNames[i]);
      }
      
   }

   protected void writerServiceInfo(Writer out, String serviceName) throws IOException {
      
      String servletInfoURI = "xito_service/" + serviceName + "?info";
      
      out.write("<a href=\"" + servletInfoURI + "\">");
      out.write(serviceName);
      out.write("</a><br>");
      
   }
   
}
