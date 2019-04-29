package org.academiadecodigo.bootcamp;

import org.academiadecodigo.bootcamp.server.ServerChat;

public class DemoServer {

    public static void main(String[] args) {

        ServerChat serverChat = new ServerChat();
        serverChat.start();

        // use netcat
    }
}
