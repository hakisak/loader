/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xito.httpserver;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 *
 * @author deane
 */
public class HttpServerStartup {

    public static void main(String args[]) {

        try {
            Server server = new Server();
            Connector connector = new SelectChannelConnector();
            connector.setPort(Integer.getInteger("http.port", 8080).intValue());
            server.setConnectors(new Connector[]{connector});

            WebAppContext webapp = new WebAppContext();
            webapp.setContextPath("/");
            webapp.setWar("webapp");
            //webapp.setDefaultsDescriptor("/etc/webdefault.xml");

            server.setHandler(webapp);

            server.start();
            server.join();

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}
