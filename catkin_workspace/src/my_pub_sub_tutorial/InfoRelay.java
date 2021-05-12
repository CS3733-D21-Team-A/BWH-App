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

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


/**
 * A simple {@link Subscriber} {@link NodeMain}.
 */
public class InfoRelay extends AbstractNodeMain {
    MyServer server3;
    String sendMessage3;
    Thread newThread = new Thread();

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava/inforelay");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        try {
            server3 = new MyServer(5556);
            System.out.println("server running");
        }catch(Exception e){
            e.printStackTrace();
        }
        final Log log = connectedNode.getLog();
        Subscriber<std_msgs.String> subscriber3 = connectedNode.newSubscriber("infoRelay_string", std_msgs.String._TYPE);
        subscriber3.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(std_msgs.String message) {
                log.info("I heard: \"" + message.getData() + "\"");
                sendMessage3 = message.getData();
                try {server3.send(sendMessage3); Thread.sleep(50);} catch(Exception e){
                    e.printStackTrace();}
            }
        });
    }
}
