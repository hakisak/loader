// Copyright (C) 2002 Xito.org. http://www.xito.org
//
// This file is part of Xito(tm).
//
// This is free software; you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License (LGPL)
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// It is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this application.
//
// Information about the GNU LGPL License can be obtained at
// http://www.gnu.org/licenses/

package org.xito.desktop;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.net.*;

/**
 *
 *
 *
 */
public class DesktopServer implements Runnable {
   
   boolean run_flag = true;
   ServerSocket serverSocket;
   
   private Logger logger = Logger.getLogger(DesktopServer.class.getName());
   
   public DesktopServer(int port) {
      try {
         serverSocket = new ServerSocket(port);
      }
      catch(IOException exp) {
         exp.printStackTrace();
      }
   }
   
   public synchronized void start() {
      run_flag = true;
      Thread thread = new Thread(this);
      thread.start();
   }
   
   public synchronized void stop() {
      run_flag = false;
   }
   
   public void run() {
      
      logger.info("DesktopServer listening on port:"+serverSocket.getLocalPort());
      
      while(run_flag) {
         Thread.currentThread().yield();
         try {
            Socket client = serverSocket.accept();
            logger.info("Desktop Client Connected!");
            new ClientConnection(client);
         }
         catch(IOException exp) {
            exp.printStackTrace();
         }
      }
   }
   
   public class ClientConnection {
      Socket socket;
      InputStreamReader reader;
      OutputStreamWriter writer;
      Thread readThread;
      
      public ClientConnection(Socket pSocket) {
         socket = pSocket;
         try {
            reader = new InputStreamReader(socket.getInputStream());
            writer = new OutputStreamWriter(socket.getOutputStream());
            
            readThread = new Thread(){
               public void run() {
                  readFromClient();
               }};
               readThread.start();
         }
         catch(IOException exp) {
            exp.printStackTrace();
         }
      }
      
      private void readFromClient() {
         try {
            char buf[] = new char[255];
            int i = reader.read(buf);
            
            String command = new String(buf, 0, i);
            //Show Desktop
            if(command.equals("showDesktop:true")) {
               DesktopService.getDefaultService().getMainFrame().setVisible(true);
               DesktopService.getDefaultService().getMainFrame().setState(Frame.NORMAL);
               
               sendOK();
            }
            logger.info("Command: "+command);
         }
         catch(IOException io) {
            io.printStackTrace();
         }
      }
      
      private void sendOK() {
         try {
            writer.write("OK");
         }
         catch(IOException exp){
            exp.printStackTrace();
         }
         
         try {
            socket.close();
         }
         catch(IOException exp){
            exp.printStackTrace();
         }
      }
   }
   
}


