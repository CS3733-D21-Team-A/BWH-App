/*
 * Copyright (C) 2014 zcxu.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.rosjava.rosjava_catkin_package_a.my_pub_sub_tutorial;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

class MyServer{
  private ServerSocket server;
  private InputStream in;
  private OutputStream out;

  MyServer(int port) throws IOException{
    server =new ServerSocket(port);
    System.out.println("listening");
    Socket socket = server.accept();
    in = socket.getInputStream();
    out = socket.getOutputStream();
    System.out.println("connected!");
  }

  String getMessage() throws IOException {
    int len;
    StringBuilder sb = new StringBuilder();
    byte[] bytes = new byte[1024];
    len = in.read(bytes);
    sb.append(new String(bytes,0,len, StandardCharsets.UTF_8));
    System.out.println("message: "+sb);
    return sb.toString();
  }

  void send(String message) throws IOException {
    System.out.println("send message: "+message);
    out.write(message.getBytes(StandardCharsets.UTF_8));

  }

  void close() throws IOException {
    server.close();
  }
}

/**
 * A simple {@link Publisher} {@link NodeMain}.
 */
public class Talker extends AbstractNodeMain{

  MyServer server;
  String sendMessage;

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("rosjava/talker");
  }

  @Override
  public void onStart(final ConnectedNode connectedNode) {

    final Publisher<std_msgs.String> publisher =
            connectedNode.newPublisher("chatter", std_msgs.String._TYPE);
    // This CancellableLoop will be canceled automatically when the node shuts
    // down.
    connectedNode.executeCancellableLoop(new CancellableLoop() {


      @Override
      protected void setup() {
        sendMessage = "";
      }

      @Override
      protected void loop() throws InterruptedException {
        std_msgs.String str = publisher.newMessage();
        str.setData(sendMessage);
        publisher.publish(str);
        Thread.sleep(200);
      }
    });

    try {
      server = new MyServer(7777);
      String message = "Verifying Server!";
      server.send(message);
      System.out.println("server running");

      while (!Thread.currentThread().isInterrupted()) {
        sendMessage = server.getMessage();
        //System.out.println(server.getMessage());
        Thread.sleep(10);
      }
    } catch(Exception e){
      e.printStackTrace();
    }
  }
}
